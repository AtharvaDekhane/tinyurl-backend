package com.tinyurl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlListResponse {

    private Long id;

    private String originalUrl;
}