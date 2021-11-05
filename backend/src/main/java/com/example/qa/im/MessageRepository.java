package com.example.qa.im;

import com.example.qa.im.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find all by orderId order by sendTime
    List<Message> findAllByOrder_IdOrderBySendTimeAsc(long orderId);
}
