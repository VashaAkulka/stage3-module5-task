package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseNewsController;
import com.mjc.school.service.BaseNewsService;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.dto.PageInfoDTO;
import com.mjc.school.service.dto.SearchParameterForNewsDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@AllArgsConstructor
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting news in the application")
public class NewsController implements BaseNewsController<NewsDTO, Long> {
    private BaseNewsService<NewsDTO, Long> service;

    @Override
    @ApiOperation("Read all news")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the news"),
            @ApiResponse(code = 204, message = "No news found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/new/all")
    public ResponseEntity<PagedModel<NewsDTO>> readAll(@ModelAttribute PageInfoDTO pages) {

        List<NewsDTO> newsDTOList = service.readAll();
        if (newsDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (pages.getPage() == null || pages.getSort() == null || pages.getLimit() == null || pages.getSortBy() == null) {
                return ResponseEntity.ok(PagedModel.of(newsDTOList, new PagedModel.PageMetadata(0, 1, newsDTOList.size())));
            }

            int startIndex = (pages.getPage() - 1) * pages.getLimit();
            int endIndex = Math.min(startIndex + pages.getLimit(), newsDTOList.size());
            List<NewsDTO> paginatedNewsDTOList = newsDTOList.subList(startIndex, endIndex);

            if (pages.getSortBy().equals("Created")) {
                paginatedNewsDTOList.sort(Comparator.comparing(NewsDTO::getCreateDate));
            } else if (pages.getSortBy().equals("Modified")) {
                paginatedNewsDTOList.sort(Comparator.comparing(NewsDTO::getLastUpdateDate));
            }
            if (pages.getSort().equals("desc")) Collections.reverse(paginatedNewsDTOList);

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pages.getLimit(), pages.getPage(), newsDTOList.size());
            PagedModel<NewsDTO> pagedModel = PagedModel.of(paginatedNewsDTOList, metadata);

            if (endIndex < newsDTOList.size()) {
                String nextLink = String.format("/news?page=%d&limit=%d&sort=%s&sortBy=%s", (pages.getPage() + 1), pages.getLimit(), pages.getSort(), pages.getSortBy());
                pagedModel.add(Link.of(nextLink, LinkRelation.of("next")));
            }

            if (startIndex > 0) {
                String previousLink = String.format("/news?page=%d&limit=%d&sort=%s&sortBy=%s", (pages.getPage() - 1), pages.getLimit(), pages.getSort(), pages.getSortBy());
                pagedModel.add(Link.of(previousLink, LinkRelation.of("previous")));
            }

            return ResponseEntity.ok(pagedModel);
        }
    }




    @Override
    @ApiOperation("Read news by parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the news"),
            @ApiResponse(code = 204, message = "No news found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/new/search")
    public ResponseEntity<List<NewsDTO>> readByParameters(@ModelAttribute SearchParameterForNewsDTO parameters) {
        List<NewsDTO> newsDTOList = service.readByParameters(parameters);
        if (newsDTOList.isEmpty())
            return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok().body(newsDTOList);
    }






    @Override
    @ApiOperation("Read news by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the news"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/new/{id}")
    public ResponseEntity<NewsDTO> readById(@PathVariable("id") Long id) throws NoSuchElementException {
        return ResponseEntity.ok(service.readById(id));
    }







    @Override
    @ApiOperation("Create an news")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create the news"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PostMapping("/new")
    public ResponseEntity<NewsDTO> create(@RequestBody @Valid NewsDTO createRequest,
                                          BindingResult bindingResult) throws NoSuchElementException, ValidationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
                    .toString());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(createRequest));
    }







    @Override
    @ApiOperation("Update the tag")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update the tag"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PatchMapping("/new/{id}")
    public ResponseEntity<NewsDTO> update(@RequestBody @Valid NewsDTO updateRequest,
                                          @PathVariable("id") Long id,
                                          BindingResult bindingResult) throws ValidationException, NoSuchElementException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
                    .toString());
        }

        return ResponseEntity.ok(service.update(updateRequest, id));
    }







    @Override
    @ApiOperation("Delete the news")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully delete the news"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @DeleteMapping("/new/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such news");
    }
}
