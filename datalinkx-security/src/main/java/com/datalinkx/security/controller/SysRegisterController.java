package com.datalinkx.security.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.security.bean.model.RegisterBody;
import com.datalinkx.security.service.SysRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController {
    @Autowired
    private SysRegisterService userService;

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public WebResult<Boolean> register(@RequestBody RegisterBody user) throws Exception {
        return WebResult.of(userService.register(user));
    }
}
