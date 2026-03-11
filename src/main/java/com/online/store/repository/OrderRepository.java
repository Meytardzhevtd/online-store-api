package com.online.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.store.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
