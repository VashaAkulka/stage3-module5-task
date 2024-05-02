package com.mjc.school.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;

    @Size(min = 5, max = 30)
    private String content;
    private Long newsId;

    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
}
