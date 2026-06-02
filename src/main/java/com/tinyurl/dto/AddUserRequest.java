package com.tinyurl.dto;

import com.tinyurl.enums.Role;
import lombok.Data;

@Data
public class AddUserRequest {

    private String name;
    private String email;
    private Role role;
    private Integer urlQuota;
}