package com.mjc.school.service;

import com.mjc.school.service.exception.NoSuchElementException;

import java.util.List;

public interface BaseAuthorAndTagService<R, K> extends BaseExtendService<R, K> {
    List<R> readByPartName(String name);
    Long getCountById(Long id);
}
