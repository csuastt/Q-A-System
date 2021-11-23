package com.example.qa.order;

import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByStateIn(Collection<Order.State> state, Pageable pageable);

    Page<Order> findAllByAsker(User asker, Pageable pageable);

    Page<Order> findAllByAskerAndFinished(User asker, boolean finished, Pageable pageable);

    Page<Order> findAllByVisibleToAnswererAndAnswerer(boolean visible, User answerer, Pageable pageable);

    Page<Order> findAllByVisibleToAnswererAndAnswererAndFinished(boolean visible, User answerer, boolean finished, Pageable pageable);

    List<Order> findAllByExpireTimeBefore(ZonedDateTime time);

    List<Order> findAllByNotifyTimeBefore(ZonedDateTime time);

    Page<Order> findAllByStateInAndShowPublic(Collection<Order.State> state, boolean showPublic, Pageable pageable);

    Page<Order> findAllByStateInAndShowPublicAndQuestionTitleContains(Collection<Order.State> state, boolean showPublic, String keyword, Pageable pageable);

    @Transactional(readOnly = true)
    Page<Order> findAllByPaidUsers(User user, Pageable pageable);
}
