package com.mjc.school.controller;

import com.mjc.school.service.dto.PageInfoDTO;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface BaseAuthorAndTagController<R, K> extends BaseExtendController<R, K> {
    ResponseEntity<PagedModel<R>> readByPartName(String name, PageInfoDTO pages);
}
