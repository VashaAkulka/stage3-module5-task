package com.mjc.school.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.HashSet;
import java.util.Set;

@Data
public class TagDTO {
    @Size(min = 3, max = 15)
    @UniqueElements
    private String name;
    private Set<Long> newsId = new HashSet<>();
}
