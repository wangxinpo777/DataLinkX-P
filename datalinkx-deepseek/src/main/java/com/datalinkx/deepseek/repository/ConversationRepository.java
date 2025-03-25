package com.datalinkx.deepseek.repository;

import com.datalinkx.deepseek.bean.ConversationBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationBean, String> {

    @Query(value = "select * from deepseek_conversation where user_id = :userId", nativeQuery = true)
    List<ConversationBean> findByUserId(Long userId);
}
