package com.online.store.service;

import com.online.store.dto.Cart.CartRequest;
import com.online.store.dto.Cart.CartResponse;
import com.online.store.entity.CartItem;
import com.online.store.entity.Product;
import com.online.store.entity.User;
import com.online.store.exceptions.CartItemNotFoundEexception;
import com.online.store.exceptions.ProductNotFoundException;
import com.online.store.exceptions.UserNotFoundException;
import com.online.store.mapper.CartMapper;
import com.online.store.repository.CartItemRepository;
import com.online.store.repository.ProductRepository;
import com.online.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private User testUser;
    private Product testProduct;
    private CartItem existingCartItem;
    private CartRequest newProductRequest;
    private CartRequest updateQuantityRequest;
    private CartResponse cartResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("test_user");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setQuantity(10);

        existingCartItem = new CartItem(1L, testUser, testProduct, 5);

        newProductRequest = new CartRequest(1L, 99L, 2);

        updateQuantityRequest = new CartRequest(1L, 1L, 10);

        cartResponse = new CartResponse(1L, 5);
    }

    @Test
    @DisplayName("Should return list of cart items for existing user")
    void testGetCart_Success() {
        List<CartItem> mockCartItems = Arrays.asList(existingCartItem);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findAllByUser(testUser)).thenReturn(mockCartItems);
        when(cartMapper.toCartResponse(existingCartItem)).thenReturn(cartResponse);

        List<CartResponse> result = cartItemService.getCart(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getQuantity());

        verify(userRepository).findById(1L);
        verify(cartItemRepository).findAllByUser(testUser);
        verify(cartMapper).toCartResponse(existingCartItem);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found in getCart")
    void testGetCart_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartItemService.getCart(999L));

        verify(userRepository).findById(999L);
        verify(cartItemRepository, never()).findAllByUser(any());
    }

    @Test
    @DisplayName("Should create new CartItem when product is not in cart")
    void testUpdate_CreateNewItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(99L)).thenReturn(Optional.of(testProduct));

        when(cartItemRepository.existsByUserAndProduct(testUser, testProduct)).thenReturn(false);

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            item.setId(999L);
            return item;
        });

        cartItemService.update(newProductRequest);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));

        verify(cartItemRepository, never()).findByUserAndProduct(any(), any());
    }

    @Test
    @DisplayName("Should update quantity correctly (Catches the logic bug)")
    void testUpdate_LogicCheck() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.existsByUserAndProduct(testUser, testProduct)).thenReturn(true);

        when(cartItemRepository.findByUserAndProduct(testUser, testProduct))
                .thenReturn(Optional.of(existingCartItem));

        int newQuantity = 10;
        CartRequest request = new CartRequest(1L, 1L, newQuantity);

        cartItemService.update(request);

        assertEquals(newQuantity, existingCartItem.getQuantity(),
                "Количество должно обновиться до значения из запроса!");
    }

    @Test
    @DisplayName("Should update quantity when product already exists in cart")
    void testUpdate_UpdateExistingItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.existsByUserAndProduct(testUser, testProduct)).thenReturn(true);
        when(cartItemRepository.findByUserAndProduct(testUser, testProduct))
                .thenReturn(Optional.of(existingCartItem));

        cartItemService.update(updateQuantityRequest);
        verify(cartItemRepository, never()).save(any());
        verify(cartItemRepository).findByUserAndProduct(testUser, testProduct);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException in update if user missing")
    void testUpdate_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartItemService.update(newProductRequest));

        verify(productRepository, never()).findById(any());
        verify(cartItemRepository, never()).existsByUserAndProduct(any(), any());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException in update if product missing")
    void testUpdate_ProductNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> cartItemService.update(newProductRequest));

        verify(cartItemRepository, never()).existsByUserAndProduct(any(), any());
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundEexception if exists returns true but find returns empty")
    void testUpdate_RaceConditionScenario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        when(cartItemRepository.existsByUserAndProduct(testUser, testProduct)).thenReturn(true);
        when(cartItemRepository.findByUserAndProduct(testUser, testProduct))
                .thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundEexception.class,
                () -> cartItemService.update(updateQuantityRequest));
    }
}