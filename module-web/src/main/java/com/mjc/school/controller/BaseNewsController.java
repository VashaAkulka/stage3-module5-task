package com.mjc.school.controller;
import com.mjc.school.service.dto.ParameterDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseNewsController<R, K> extends BaseController<R, K> {
    ResponseEntity<List<R>> readByParameters(ParameterDTO parameters);
}
