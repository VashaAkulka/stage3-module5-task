package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseExtendController;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.CommentDTO;
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
@RequestMapping("/comments")
public class CommentController implements BaseExtendController<CommentDTO, Long> {
    private BaseExtendService<CommentDTO, Long> service;

    @Override
    @ApiOperation("Read comment by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comments"),
            @ApiResponse(code = 204, message = "No comments found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/news/{id}")
    public ResponseEntity<List<CommentDTO>> readByNewsId(@PathVariable("id") Long id) throws NoSuchElementException {
        List<CommentDTO> commentDTOList = service.readByNewsId(id);
        if (commentDTOList.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(commentDTOList);
    }





    @Override
    @ApiOperation("Read all comments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comments"),
            @ApiResponse(code = 204, message = "No comments found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping
    public ResponseEntity<PagedModel<CommentDTO>> readAll(@RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "sort", required = false) String sort,
                                                      @RequestParam(value = "limit", required = false) Integer limit) {

        List<CommentDTO> commentDTOList = service.readAll();
        if (commentDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (page == null || sort == null || limit == null) {
                return ResponseEntity.ok(PagedModel.of(commentDTOList, new PagedModel.PageMetadata(0, 0, commentDTOList.size())));
            }

            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, commentDTOList.size());
            List<CommentDTO> paginatedCommentDTOList = commentDTOList.subList(startIndex, endIndex);

            if (sort.equals("asc")) {
                paginatedCommentDTOList.sort(Comparator.comparing(CommentDTO::getContent));
            } else if (sort.equals("desc")) {
                paginatedCommentDTOList.sort(Comparator.comparing(CommentDTO::getContent).reversed());
            }

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(limit, page, commentDTOList.size());
            PagedModel<CommentDTO> pagedModel = PagedModel.of(paginatedCommentDTOList, metadata);

            if (endIndex < commentDTOList.size()) {
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
    @ApiOperation("Read comment by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the comment"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}")
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
    @PostMapping
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
    @PatchMapping("/{id}")
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such comment");
    }
}
