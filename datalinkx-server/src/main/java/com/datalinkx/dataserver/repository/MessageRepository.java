package com.datalinkx.dataserver.repository;

import com.datalinkx.dataserver.bean.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "select * from message where conversation_id = :conversationId", nativeQuery = true)
    List<Message> findByConversationId(String conversationId);
}
