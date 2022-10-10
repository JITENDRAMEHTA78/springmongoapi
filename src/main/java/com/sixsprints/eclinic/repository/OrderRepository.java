package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.enums.OrderStatus;

@Repository
public interface OrderRepository extends GenericRepository<Order> {

  Order findByStatusAndProducts_productId(OrderStatus status, String productId);

}
