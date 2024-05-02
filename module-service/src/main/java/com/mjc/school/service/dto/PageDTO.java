package com.mjc.school.service.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer page;
    private String sort;
    private String sortBy;
    private Integer limit;
}
