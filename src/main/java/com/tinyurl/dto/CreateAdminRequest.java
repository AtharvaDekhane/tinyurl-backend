package com.tinyurl.dto;

import lombok.Data;

@Data
public class CreateAdminRequest {

    private String name;
    private String email;
    private String password;
}