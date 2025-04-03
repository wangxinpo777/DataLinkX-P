package com.datalinkx.dataserver.repository;

import com.datalinkx.dataserver.bean.domain.JobLogBean;
import com.datalinkx.dataserver.bean.dto.JobCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface JobLogRepository extends CRUDRepository<JobLogBean, String> {


    String PAGE_QUERY_SQL = "select * from JOB_LOG where is_del = 0 and if(:jobId != '', job_id =:jobId, 1=1) order by start_time desc";

    @Query(
            value = PAGE_QUERY_SQL,
            countQuery = "SELECT COUNT(1) FROM (" + PAGE_QUERY_SQL + ") t",
            nativeQuery = true)
    Page<JobLogBean> pageQuery(Pageable pageable, String jobId);

    @Modifying
    @Transactional
    @Query(value = " update JOB_LOG set is_del = 1 where job_id = :jobId and is_del = 0 ", nativeQuery = true)
    void logicDeleteByJobId(String jobId);

    @Query("SELECT NEW com.datalinkx.dataserver.bean.dto.JobCountDto("
            + "FUNCTION('DATE_FORMAT', j.startTime, '%Y-%m-%d'), "
            + "j.jobId, "
            + "SUM(CASE WHEN j.status = 0 THEN 1 ELSE 0 END), "
            + "SUM(CASE WHEN j.status = 1 THEN 1 ELSE 0 END) )"
            + "FROM JobLogBean j "
            + "WHERE (j.isDel IS NULL OR j.isDel = 0) "
            + "AND (:jobId IS NULL OR j.jobId = :jobId) "
            + "AND (:dateFrom IS NULL OR j.startTime >= :dateFrom) "
            + "AND (:dateTo IS NULL OR j.startTime <= :dateTo) "
            + "GROUP BY FUNCTION('DATE', j.startTime) "
            + "ORDER BY FUNCTION('DATE', j.startTime) DESC")
        // 按日期升序排序
    List<JobCountDto> count(String jobId, Timestamp dateFrom, Timestamp dateTo);
}
