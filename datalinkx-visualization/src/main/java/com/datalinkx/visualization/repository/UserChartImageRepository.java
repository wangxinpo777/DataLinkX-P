package com.datalinkx.visualization.repository;

import com.datalinkx.visualization.bean.domain.UserChartImageBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserChartImageRepository extends JpaRepository<UserChartImageBean, Integer> {
   List<UserChartImageBean> findByUserId(Integer userId);

   @Query(value = "select * from user_chart_images where user_id = :userId and is_del = 0", nativeQuery = true)
   Page<UserChartImageBean> pageQuery(Pageable pageable, Integer userId);
}
