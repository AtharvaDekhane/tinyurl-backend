package com.tinyurl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUrlResponse {

    private String shortUrl;
}