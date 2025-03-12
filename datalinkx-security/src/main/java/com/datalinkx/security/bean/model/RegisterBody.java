package com.datalinkx.security.bean.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户注册对象
 * 
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterBody extends LoginBody
{
    private String email;
    private String nickname;
    private String passwordLevel;
}
