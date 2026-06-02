package com.tinyurl.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for URL shortening")
public class UrlRequest {

    @Schema(description = "Original URL", example = "https://google.com")
    private String originalUrl;
}
