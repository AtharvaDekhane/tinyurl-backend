package com.tinyurl.dto;

import com.tinyurl.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Integer urlQuota;
    private Boolean isActive;
}