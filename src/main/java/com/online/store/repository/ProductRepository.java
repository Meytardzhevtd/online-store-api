package com.online.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findById(Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.viewsCount = p.viewsCount + :count WHERE p.id = :id")
	void incrementViewsById(@Param("id") Long id, @Param("count") Long count);
}
