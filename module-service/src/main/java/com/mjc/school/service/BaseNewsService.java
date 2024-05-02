package com.mjc.school.service;

import com.mjc.school.service.dto.ParameterDTO;

import java.util.List;

public interface BaseNewsService<R, K> extends BaseService<R, K> {
    List<R> readByParameters(ParameterDTO parameters);
}
