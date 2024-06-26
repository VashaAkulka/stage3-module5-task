package com.mjc.school.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Data
public class AuthorDTO {
    private Long id;
    @Size(min = 3, max = 15)
    @UniqueElements
    private String name;
    private Set<Long> newsId;
}
