package com.example.qa.order;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByDeletedAndState(boolean deleted, OrderState state, Pageable pageable);

    Page<Order> findAllByDeletedAndAsker(boolean deleted, User asker, Pageable pageable);

    Page<Order> findAllByDeletedAndAskerAndFinished(boolean deleted, User asker, boolean finished, Pageable pageable);

    Page<Order> findAllByDeletedAndReviewedAndAnswerer(boolean deleted, boolean reviewed, User answerer, Pageable pageable);

    Page<Order> findAllByDeletedAndReviewedAndAnswererAndFinished(boolean deleted, boolean reviewed, User answerer, boolean finished, Pageable pageable);

    List<Order> findAllByExpireTimeBefore(ZonedDateTime time);
}
