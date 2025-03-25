package com.datalinkx.deepseek.repository;

import com.datalinkx.deepseek.bean.MessageBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageBean, String> {
    @Query(value = "select * from deepseek_message where conversation_id = :conversationId", nativeQuery = true)
    List<MessageBean> findByConversationId(String conversationId);
}
