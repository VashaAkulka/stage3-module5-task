package com.mjc.school.controller;

import com.mjc.school.service.dto.PageInfoDTO;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface BaseExtendController<R, K> extends BaseController<R, K> {
    ResponseEntity<PagedModel<R>> readByNewsId(K id, PageInfoDTO pages);
}
