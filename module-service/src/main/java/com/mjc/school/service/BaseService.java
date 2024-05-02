package com.mjc.school.service;

import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;

import java.util.List;

public interface BaseService<R, K> {
    List<R> readAll();

    R readById(K id) throws NoSuchElementException;

    R create(R createRequest) throws NoSuchElementException, ValidationException;

    R update(R updateRequest, K id) throws NoSuchElementException;

    boolean deleteById(K id);
}
