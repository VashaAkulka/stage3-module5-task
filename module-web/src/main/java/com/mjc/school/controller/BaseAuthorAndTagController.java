package com.mjc.school.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BaseAuthorAndTagController<R, K> extends BaseExtendController<R, K> {
    ResponseEntity<List<R>> readByPartName(String name);
}
