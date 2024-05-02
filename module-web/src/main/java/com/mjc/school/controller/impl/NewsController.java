package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.dto.TagDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/news")
public class NewsController implements BaseController<NewsDTO, Long> {
    private BaseService<NewsDTO, Long> service;

    @Override
    @ApiOperation("Read all news")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the news"),
            @ApiResponse(code = 204, message = "No news found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping
    public ResponseEntity<PagedModel<NewsDTO>> readAll(@RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "sort", required = false) String sort,
                                                 @RequestParam(value = "limit", required = false) Integer limit) {

        List<NewsDTO> newsDTOList = service.readAll();
        if (newsDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (page == null || sort == null || limit == null) {
                return ResponseEntity.ok(PagedModel.of(newsDTOList, new PagedModel.PageMetadata(0, 0, newsDTOList.size())));
            }

            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, newsDTOList.size());
            List<NewsDTO> paginatedNewsDTOList = newsDTOList.subList(startIndex, endIndex);

            if (sort.equals("asc")) {
                paginatedNewsDTOList.sort(Comparator.comparing(NewsDTO::getTitle));
            } else if (sort.equals("desc")) {
                paginatedNewsDTOList.sort(Comparator.comparing(NewsDTO::getTitle).reversed());
            }

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(limit, page, newsDTOList.size());
            PagedModel<NewsDTO> pagedModel = PagedModel.of(paginatedNewsDTOList, metadata);

            if (endIndex < newsDTOList.size()) {
                String nextLink = String.format("/news?page=%d&limit=%d&sort=%s", (page + 1), limit, sort);
                pagedModel.add(Link.of(nextLink, LinkRelation.of("next")));
            }

            if (startIndex > 0) {
                String previousLink = String.format("/news?page=%d&limit=%d&sort=%s", (page - 1), limit, sort);
                pagedModel.add(Link.of(previousLink, LinkRelation.of("previous")));
            }

            return ResponseEntity.ok(pagedModel);
        }
    }







    @Override
    @ApiOperation("Read news by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the news"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}")
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
    @PostMapping
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
    @PatchMapping("/{id}")
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such news");
    }
}
