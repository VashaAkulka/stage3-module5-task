package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.service.BaseAuthorAndTagService;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.AuthorMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService implements BaseAuthorAndTagService<AuthorDTO, Long> {
    private AuthorRepository repository;

    @Override
    public List<AuthorDTO> readByNewsId(Long id) {
        return AuthorMapper.INSTANCE.authorListToAuthorDtoList(repository.findAuthorByNewsId(id));
    }

    @Override
    public List<AuthorDTO> readAll() {
        return AuthorMapper.INSTANCE.authorListToAuthorDtoList(repository.findAll());
    }

    @Override
    public AuthorDTO readById(Long id) throws NoSuchElementException {
        return AuthorMapper.INSTANCE.authorToAuthorDto(repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such author")));
    }

    @Override
    public AuthorDTO create(AuthorDTO createRequest) throws ValidationException {
        AuthorModel authorModel = new AuthorModel();
        if (createRequest.getName() == null) throw new ValidationException("The fields should not be empty");

        authorModel.setName(createRequest.getName());
        authorModel.setNews(new HashSet<>());

        return AuthorMapper.INSTANCE.authorToAuthorDto(repository.save(authorModel));
    }

    @Override
    public AuthorDTO update(AuthorDTO updateRequest, Long id) throws NoSuchElementException {
        AuthorModel authorModel = repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such author"));
        if (updateRequest.getName() != null) authorModel.setName(updateRequest.getName());

        return AuthorMapper.INSTANCE.authorToAuthorDto(repository.save(authorModel));
    }

    @Override
    public boolean deleteById(Long id) {
        AuthorModel authorModel = repository.findById(id).orElse(null);
        if (authorModel == null) return false;
        else {
            repository.deleteById(id);
            return true;
        }
    }

    @Override
    public List<AuthorDTO> readByPartName(String name) {
        return AuthorMapper.INSTANCE.authorListToAuthorDtoList(repository.findAuthorByPartName(name));
    }

    @Override
    public Long getCountById(Long id) {
        return repository.countNewsByAuthorId(id);
    }
}
