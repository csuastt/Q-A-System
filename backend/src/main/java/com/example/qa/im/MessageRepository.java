package com.example.qa.im;

import com.example.qa.im.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find all by orderId order by sendTime
    List<Message> findAllByOrder_IdOrderBySendTimeAsc(long orderId);
}
