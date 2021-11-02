package com.example.qa.order;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.model.User;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    @Setter
    private PageRequest pageRequest;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public Page<Order> listByAsker(User asker, Boolean finished) {
        return finished == null ?
                orderRepository.findAllByDeletedAndAsker(false, asker, pageRequest) :
                orderRepository.findAllByDeletedAndAskerAndFinished(false, asker, finished, pageRequest);
    }

    public Page<Order> listByAnswerer(User answerer, Boolean finished) {
        return finished == null ?
                orderRepository.findAllByDeletedAndReviewedAndAnswerer(false, true, answerer, pageRequest) :
                orderRepository.findAllByDeletedAndReviewedAndAnswererAndFinished(false, true, answerer, finished, pageRequest);
    }

    public Page<Order> listByState(OrderState state) {
        return state == null ?
                orderRepository.findAll(pageRequest) :
                orderRepository.findAllByDeletedAndState(false, state, pageRequest);
    }

    public Order answerOrder(Order order, String answer) {
        order.setAnswerSummary(answer);
        order.setState(OrderState.ANSWERED);
        // 结束定时或条件
        return save(order);
    }

    public Order answerOrder(long id, String answer) {
        return answerOrder(findById(id).get(), answer);
    }
}
