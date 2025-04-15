package com.datalinkx.visualization.repository;

import com.datalinkx.visualization.bean.domain.UserChartImageBean;
import com.datalinkx.visualization.bean.dto.ImageConfig;
import com.datalinkx.visualization.bean.dto.UserChartImageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChartImageRepository extends JpaRepository<UserChartImageBean, Integer> {
    @Query(value = "SELECT user_chart_images.id, user_chart_images.user_id AS userId, user_chart_images.image, user_chart_images.description, user_chart_images.created_time AS createdTime, user_chart_images.updated_time AS updatedTime, user_chart_images.type, sys_user.nick_name AS userName " +
            "FROM user_chart_images LEFT JOIN sys_user ON user_chart_images.user_id=sys_user.user_id " +
            "WHERE user_chart_images.is_del = 0 " +
            "AND (:keyword IS NULL OR description LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY created_time DESC",
            countQuery = "SELECT COUNT(*) FROM user_chart_images " +
                    "WHERE is_del = 0 " +
                    "AND (:keyword IS NULL OR description LIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<UserChartImageProjection> pageQuery(Pageable pageable, @Param("keyword") String keyword);

    @Query(value = "select chart_config as chartConfig, description, user_id as userId, id, chart_json_data as chartJsonData " +
            "from user_chart_images where id = ?1 and is_del = 0",
            nativeQuery = true)
    ImageConfig findImageConfigById(Integer imageId);
}
