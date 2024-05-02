package com.mjc.school.controller;
import com.mjc.school.service.dto.PageInfoDTO;
import com.mjc.school.service.dto.SearchParameterForNewsDTO;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface BaseNewsController<R, K> extends BaseController<R, K> {
    ResponseEntity<PagedModel<R>> readByParameters(SearchParameterForNewsDTO parameters, PageInfoDTO pages);
}
