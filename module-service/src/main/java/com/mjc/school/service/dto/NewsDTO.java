package com.mjc.school.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class NewsDTO {
    Long authorId;

    @Size(min = 5, max = 30)
    @UniqueElements
    String title;

    @Size(min = 5, max = 255)
    String content;
    Set<Long> tagsId = new HashSet<>();
}
