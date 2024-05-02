package com.mjc.school.service.dto;

import lombok.Data;

@Data
public class ParameterDTO {
    private Long tagId;
    private String tagName;
    private String authorName;
    private String title;
    private String content;
}
