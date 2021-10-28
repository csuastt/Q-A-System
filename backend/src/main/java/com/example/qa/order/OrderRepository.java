package com.example.qa.order;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByAskerAndDeleted(User asker, boolean deleted, Pageable pageable);

    Page<Order> findAllByAnswererAndDeletedAndReviewed(User answerer, boolean deleted, boolean reviewed, Pageable pageable);

    Page<Order> findAllByStateAndDeleted(OrderState state, boolean deleted, Pageable pageable);
}
