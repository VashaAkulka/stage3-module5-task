package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseExtendController;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.CommentDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import io.swagger.annotations.Api;
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
@RequestMapping("/authors")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting authors in the application")
public class AuthorController implements BaseExtendController<AuthorDTO, Long> {
    private BaseExtendService<AuthorDTO, Long> service;

    @Override
    @ApiOperation("Read author by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the authors"),
            @ApiResponse(code = 204, message = "No authors found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/news/{id}")
    public ResponseEntity<List<AuthorDTO>> readByNewsId(@PathVariable("id") Long id) throws NoSuchElementException {
        List<AuthorDTO> authorDTOList = service.readByNewsId(id);
        if (authorDTOList.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(authorDTOList);
    }





    @Override
    @ApiOperation("Read all authors")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the authors"),
            @ApiResponse(code = 204, message = "No authors found"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping
    public ResponseEntity<PagedModel<AuthorDTO>> readAll(@RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "sort", required = false) String sort,
                                                          @RequestParam(value = "limit", required = false) Integer limit) {

        List<AuthorDTO> authorDTOList = service.readAll();
        if (authorDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (page == null || sort == null || limit == null) {
                return ResponseEntity.ok(PagedModel.of(authorDTOList, new PagedModel.PageMetadata(0, 0, authorDTOList.size())));
            }

            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, authorDTOList.size());
            List<AuthorDTO> paginatedAuthorDTOList = authorDTOList.subList(startIndex, endIndex);

            if (sort.equals("asc")) {
                paginatedAuthorDTOList.sort(Comparator.comparing(AuthorDTO::getName));
            } else if (sort.equals("desc")) {
                paginatedAuthorDTOList.sort(Comparator.comparing(AuthorDTO::getName).reversed());
            }

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(limit, page, authorDTOList.size());
            PagedModel<AuthorDTO> pagedModel = PagedModel.of(paginatedAuthorDTOList, metadata);

            if (endIndex < authorDTOList.size()) {
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
    @ApiOperation("Read author by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the author"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> readById(@PathVariable("id") Long id) throws NoSuchElementException {
        return ResponseEntity.ok(service.readById(id));
    }





    @Override
    @ApiOperation("Create an author")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create the author"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PostMapping
    public ResponseEntity<AuthorDTO> create(@RequestBody @Valid AuthorDTO createRequest,
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
    @ApiOperation("Update the author")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update the author"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AuthorDTO> update(@RequestBody @Valid AuthorDTO updateRequest,
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
    @ApiOperation("Delete the author")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully delete the author"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such author");
    }
}
