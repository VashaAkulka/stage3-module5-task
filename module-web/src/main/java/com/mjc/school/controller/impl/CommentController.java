package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseExtendController;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.*;
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
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting comment in the application")
public class CommentController implements BaseExtendController<CommentDTO, Long> {
    private BaseExtendService<CommentDTO, Long> service;

    @Override
    @ApiOperation("Read comment by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comments"),
            @ApiResponse(code = 204, message = "No comments found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/news/{newsId}/comments")
    public ResponseEntity<PagedModel<CommentDTO>> readByNewsId(@PathVariable("newsId") Long id,
                                                         @ModelAttribute PageInfoDTO pages) {
        List<CommentDTO> commentDTOList = service.readByNewsId(id);
        return pagination(commentDTOList, pages);
    }









    @Override
    @ApiOperation("Read all comments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comments"),
            @ApiResponse(code = 204, message = "No comments found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/comments/all")
    public ResponseEntity<PagedModel<CommentDTO>> readAll(@ModelAttribute PageInfoDTO pages) {

        List<CommentDTO> commentDTOList = service.readAll();
        return pagination(commentDTOList, pages);
    }







    @Override
    @ApiOperation("Read comment by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comment"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentDTO> readById(@PathVariable("id") Long id) throws NoSuchElementException {
        return ResponseEntity.ok(service.readById(id));
    }







    @Override
    @ApiOperation("Create an comment")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create the comment"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PostMapping("/comment")
    public ResponseEntity<CommentDTO> create(@RequestBody @Valid CommentDTO createRequest,
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
    @ApiOperation("Update the comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update the comment"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PatchMapping("/comment/{id}")
    public ResponseEntity<CommentDTO> update(@RequestBody @Valid CommentDTO updateRequest,
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
    @ApiOperation("Delete the comment")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully delete the comment"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @DeleteMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such comment");
    }





    private ResponseEntity<PagedModel<CommentDTO>> pagination(List<CommentDTO> commentDTOList, PageInfoDTO pages) {
        if (commentDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (pages.getPage() == null || pages.getSort() == null || pages.getLimit() == null || pages.getSortBy() == null) {
                return ResponseEntity.ok(PagedModel.of(commentDTOList, new PagedModel.PageMetadata(0, 1, commentDTOList.size())));
            }

            int startIndex = (pages.getPage() - 1) * pages.getLimit();
            int endIndex = Math.min(startIndex + pages.getLimit(), commentDTOList.size());
            List<CommentDTO> paginatedCommentDTOList = commentDTOList.subList(startIndex, endIndex);

            if (pages.getSortBy().equals("Created")) {
                paginatedCommentDTOList.sort(Comparator.comparing(CommentDTO::getCreateDate));
            } else if (pages.getSortBy().equals("Modified")) {
                paginatedCommentDTOList.sort(Comparator.comparing(CommentDTO::getLastUpdateDate));
            }
            if (pages.getSort().equals("desc")) Collections.reverse(paginatedCommentDTOList);

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pages.getLimit(), pages.getPage(), commentDTOList.size());
            PagedModel<CommentDTO> pagedModel = PagedModel.of(paginatedCommentDTOList, metadata);

            if (endIndex < commentDTOList.size()) {
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
}
