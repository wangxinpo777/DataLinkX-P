package com.datalinkx.dataserver.repository;

import com.datalinkx.dataserver.bean.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query(value = "select * from conversation where user_id = :userId", nativeQuery = true)
    List<Conversation> findByUserId(Long userId);
}
