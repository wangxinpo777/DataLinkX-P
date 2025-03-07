package com.datalinkx.security.bean.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthUserBody {
    // Getters and Setters
    private String roleId;
    private String[] userIds;

}