package com.mjc.school.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewsDTO {
    private Long id;

    private Long authorId;

    @Size(min = 5, max = 30)
    @UniqueElements
    private String title;

    @Size(min = 5, max = 255)
    private String content;
    private Set<Long> tagsId = new HashSet<>();

    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
}
