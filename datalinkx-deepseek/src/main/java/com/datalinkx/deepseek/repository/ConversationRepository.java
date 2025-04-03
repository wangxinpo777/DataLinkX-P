package com.datalinkx.deepseek.repository;

import com.datalinkx.deepseek.bean.domain.ConversationBean;
import com.datalinkx.deepseek.bean.dto.ChatApiCountDTO;
import com.datalinkx.deepseek.bean.dto.ChatTokenCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationBean, String> {

    @Query(value = "select * from deepseek_conversation where user_id = :userId", nativeQuery = true)
    List<ConversationBean> findByUserId(Long userId);

    @Query("SELECT NEW com.datalinkx.deepseek.bean.dto.ChatApiCountDTO("
            + "FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d'), "
            + "m.model, COUNT(*)) "
            + "FROM MessageBean m "
            + "WHERE (:model IS NULL OR m.model = :model) "
            + "AND (:dateFrom IS NULL OR m.createdTime >= :dateFrom) "
            + "AND (:dateTo IS NULL OR m.createdTime <= :dateTo) "
            + "AND m.role = 'user' "
            + "GROUP BY FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d'), m.model "
            + "ORDER BY FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d') DESC")
    List<ChatApiCountDTO> chatApiCount(String model,
                                       Timestamp dateFrom,
                                       Timestamp dateTo);

    @Query("SELECT NEW com.datalinkx.deepseek.bean.dto.ChatTokenCountDTO("
            + "FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d'), "
            + "m.model, "
            + "SUM(m.promptCacheHitTokens), "
            + "SUM(m.promptCacheMissTokens), "
            + "SUM(m.promptTokens), "
            + "SUM(m.completionTokens), "
            + "SUM(m.totalTokens)) "
            + "FROM MessageBean m "
            + "WHERE (:model IS NULL OR m.model = :model) "
            + "AND (:dateFrom IS NULL OR m.createdTime >= :dateFrom) "
            + "AND (:dateTo IS NULL OR m.createdTime <= :dateTo) "
            + "AND m.role = 'assistant' "
            + "GROUP BY FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d'), m.model "
            + "ORDER BY FUNCTION('DATE_FORMAT', m.createdTime, '%Y-%m-%d') ASC")
    List<ChatTokenCountDTO> chatTokenCount(String model,
                                           Timestamp dateFrom,
                                           Timestamp dateTo);
}
