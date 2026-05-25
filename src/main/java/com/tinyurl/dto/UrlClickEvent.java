package com.tinyurl.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlClickEvent {

    private String shortCode;
}