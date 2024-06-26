package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseAuthorAndTagController;
import com.mjc.school.controller.BaseExtendController;
import com.mjc.school.service.BaseAuthorAndTagService;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.PageInfoDTO;
import com.mjc.school.service.dto.TagDTO;
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
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting tag in the application")
@RequestMapping("/api/v1")
public class TagController implements BaseAuthorAndTagController<TagDTO, Long> {
    private BaseAuthorAndTagService<TagDTO, Long> service;

    @Override
    @ApiOperation("Read tags by news id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the tags"),
            @ApiResponse(code = 204, message = "No tags found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/news/{newsId}/tags")
    public ResponseEntity<PagedModel<TagDTO>> readByNewsId(@PathVariable("newsId") Long id,
                                                           @ModelAttribute PageInfoDTO pages) {
        List<TagDTO> tagDTOList = service.readByNewsId(id);
        return pagination(tagDTOList, pages);
    }







    @Override
    @ApiOperation("Read tags by part name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the tags"),
            @ApiResponse(code = 204, message = "No tags found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/tags/search")
    public ResponseEntity<PagedModel<TagDTO>> readByPartName(String name,
                                                             @ModelAttribute PageInfoDTO pages) {
        List<TagDTO> tagDTOList = service.readByPartName(name);
        return pagination(tagDTOList, pages);
    }







    @Override
    @ApiOperation("Read all tags")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the tags"),
            @ApiResponse(code = 204, message = "No tags found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/tags/all")
    public ResponseEntity<PagedModel<TagDTO>> readAll(@ModelAttribute PageInfoDTO pages) {

        List<TagDTO> tagDTOList = service.readAll();
        return pagination(tagDTOList, pages);
    }







    @Override
    @ApiOperation("Read tag by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully read the tag"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/tag/{id}")
    public ResponseEntity<TagDTO> readById(@PathVariable("id") Long id) throws NoSuchElementException {
        return ResponseEntity.ok(service.readById(id));
    }







    @Override
    @ApiOperation("Create an tag")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create the tag"),
            @ApiResponse(code = 400, message = "Invalid request parameters"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @PostMapping("/tag")
    public ResponseEntity<TagDTO> create(@RequestBody @Valid TagDTO createRequest,
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
    @PatchMapping("/tag/{id}")
    public ResponseEntity<TagDTO> update(@RequestBody @Valid TagDTO updateRequest,
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
    @ApiOperation("Delete the tag")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully delete the tag"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @DeleteMapping("/tag/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws NoSuchElementException {
        if (!service.deleteById(id))
            throw new NoSuchElementException("No such tag");
    }






    private ResponseEntity<PagedModel<TagDTO>> pagination(List<TagDTO> tagDTOList,
                                                             PageInfoDTO pages) {
        if (tagDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (pages.getPage() == null || pages.getSort() == null || pages.getLimit() == null) {
                return ResponseEntity.ok(PagedModel.of(tagDTOList, new PagedModel.PageMetadata(tagDTOList.size(), 1, tagDTOList.size())));
            }

            int startIndex = (pages.getPage() - 1) * pages.getLimit();
            int endIndex = Math.min(startIndex + pages.getLimit(), tagDTOList.size());
            List<TagDTO> paginatedTagDTOList = tagDTOList.subList(startIndex, endIndex);

            if (pages.getSort().trim().equalsIgnoreCase("asc")) {
                paginatedTagDTOList.sort(Comparator.comparing(tag -> service.getCountById(tag.getId())));
            } else if (pages.getSort().trim().equalsIgnoreCase("desc")) {
                paginatedTagDTOList.sort(Comparator.comparing(tag -> service.getCountById(tag.getId())));
                Collections.reverse(paginatedTagDTOList);
            }

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pages.getLimit(), pages.getPage(), tagDTOList.size());
            PagedModel<TagDTO> pagedModel = PagedModel.of(paginatedTagDTOList, metadata);

            if (endIndex < tagDTOList.size()) {
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
