package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseAuthorAndTagController;
import com.mjc.school.controller.BaseExtendController;
import com.mjc.school.service.BaseAuthorAndTagService;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.PageInfoDTO;
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
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting authors in the application")
@RequestMapping("/api/v1")
public class AuthorController implements BaseAuthorAndTagController<AuthorDTO, Long> {
    private BaseAuthorAndTagService<AuthorDTO, Long> service;

    @Override
    @ApiOperation("Read author by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the authors"),
            @ApiResponse(code = 204, message = "No authors found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/news/{newsId}/authors")
    public ResponseEntity<PagedModel<AuthorDTO>> readByNewsId(@PathVariable("newsId") Long id,
                                                              @ModelAttribute PageInfoDTO pages) {
        List<AuthorDTO> authorDTOList = service.readByNewsId(id);
        return pagination(authorDTOList, pages);
    }







    @Override
    @ApiOperation("Read authors by part name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the authors"),
            @ApiResponse(code = 204, message = "No authors found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/authors/search")
    public ResponseEntity<PagedModel<AuthorDTO>> readByPartName(@RequestParam("name") String name,
                                                          @ModelAttribute PageInfoDTO pages) {
        List<AuthorDTO> authorDTOList = service.readByPartName(name);
        return pagination(authorDTOList, pages);
    }







    @Override
    @ApiOperation("Read all authors")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the authors"),
            @ApiResponse(code = 204, message = "No authors found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/authors/all")
    public ResponseEntity<PagedModel<AuthorDTO>> readAll(@ModelAttribute PageInfoDTO pages) {

        List<AuthorDTO> authorDTOList = service.readAll();
        return pagination(authorDTOList, pages);
    }





    @Override
    @ApiOperation("Read author by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the author"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/author/{id}")
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
    @PostMapping("/author")
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
    @PatchMapping("/author/{id}")
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
    @DeleteMapping("/author/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such author");
    }





    private ResponseEntity<PagedModel<AuthorDTO>> pagination(List<AuthorDTO> authorDTOList,
                                                             PageInfoDTO pages) {
        if (authorDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (pages.getPage() == null || pages.getSort() == null || pages.getLimit() == null) {
                return ResponseEntity.ok(PagedModel.of(authorDTOList, new PagedModel.PageMetadata(authorDTOList.size(), 1, authorDTOList.size())));
            }

            int startIndex = (pages.getPage() - 1) * pages.getLimit();
            int endIndex = Math.min(startIndex + pages.getLimit(), authorDTOList.size());
            List<AuthorDTO> paginatedAuthorDTOList = authorDTOList.subList(startIndex, endIndex);

            if (pages.getSort().trim().equalsIgnoreCase("asc")) {
                paginatedAuthorDTOList.sort(Comparator.comparing(author -> service.getCountById(author.getId())));
            } else if (pages.getSort().trim().equalsIgnoreCase("desc")) {
                paginatedAuthorDTOList.sort(Comparator.comparing(author -> service.getCountById(author.getId())));
                Collections.reverse(paginatedAuthorDTOList);
            }

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pages.getLimit(), pages.getPage(), authorDTOList.size());
            PagedModel<AuthorDTO> pagedModel = PagedModel.of(paginatedAuthorDTOList, metadata);

            if (endIndex < authorDTOList.size()) {
                String nextLink = String.format("/news?page=%d&limit=%d&sort=%s", (pages.getPage() + 1), pages.getLimit(), pages.getSort());
                pagedModel.add(Link.of(nextLink, LinkRelation.of("next")));
            }

            if (startIndex > 0) {
                String previousLink = String.format("/news?page=%d&limit=%d&sort=%s", (pages.getPage() - 1), pages.getLimit(), pages.getSort());
                pagedModel.add(Link.of(previousLink, LinkRelation.of("previous")));
            }

            return ResponseEntity.ok(pagedModel);
        }
    }
}
