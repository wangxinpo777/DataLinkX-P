package com.datalinkx.security.repository;


import com.datalinkx.security.bean.domain.SysUserRoleBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRoleBean, String> {
    @Transactional
    @Modifying
    @Query(value = "delete from sys_user_role where role_id = :roleId", nativeQuery = true)
    int deleteAuthUsers(@Param("roleId") String roleId);
}
