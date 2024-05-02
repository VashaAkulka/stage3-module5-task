package com.mjc.school.service.impl;


import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.BaseNewsService;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.dto.ParameterDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.NewsMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class NewsService implements BaseNewsService<NewsDTO, Long> {
    private NewsRepository repository;
    private TagRepository tagRepository;
    private AuthorRepository authorRepository;

    @Override
    public List<NewsDTO> readAll() {
        return NewsMapper.INSTANCE.newsListToNewsDtoList(repository.findAll());
    }

    @Override
    public NewsDTO readById(Long id) throws NoSuchElementException {
        return NewsMapper.INSTANCE.newsToNewsDto(repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such news")));
    }

    @Override
    public NewsDTO create(NewsDTO createRequest) throws NoSuchElementException, ValidationException {
        NewsModel newsModel = new NewsModel();
        if (createRequest.getTitle() == null || createRequest.getContent() == null || createRequest.getAuthorId() == null)
            throw new ValidationException("The fields should not be empty");

        newsModel.setTitle(createRequest.getTitle());
        newsModel.setContent(createRequest.getContent());
        newsModel.setCreateDate(LocalDateTime.now());
        newsModel.setLastUpdateDate(LocalDateTime.now());

        Set<TagModel> tagModelSet = new HashSet<>();
        for (Long TagId : createRequest.getTagsId()) {
            tagModelSet.add(tagRepository.findById(TagId).orElseThrow(() -> new NoSuchElementException("No such tag")));
        }
        AuthorModel authorModel = authorRepository.findById(createRequest.getAuthorId()).orElseThrow(() -> new NoSuchElementException("No such author"));

        newsModel.setAuthor(authorModel);
        newsModel.setTags(tagModelSet);
        newsModel.setComments(new ArrayList<>());

        return NewsMapper.INSTANCE.newsToNewsDto(repository.save(newsModel));
    }

    @Override
    public NewsDTO update(NewsDTO updateRequest, Long id) throws NoSuchElementException {
        NewsModel newsModel = repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such news"));
        if (updateRequest.getTitle() != null) newsModel.setTitle(updateRequest.getTitle());
        if (updateRequest.getContent() != null) newsModel.setContent(updateRequest.getContent());
        newsModel.setLastUpdateDate(LocalDateTime.now());

        if (updateRequest.getTagsId() != null) {
            Set<TagModel> tagModelSet = new HashSet<>();
            for (Long TagId : updateRequest.getTagsId()) {
                tagModelSet.add(tagRepository.findById(TagId).orElseThrow(() -> new NoSuchElementException("No such tag")));
            }
            newsModel.setTags(tagModelSet);
        }

        if (updateRequest.getAuthorId() != null) {
            AuthorModel authorModel = authorRepository.findById(updateRequest.getAuthorId()).orElseThrow(() -> new NoSuchElementException("No such author"));
            newsModel.setAuthor(authorModel);
        }

        return NewsMapper.INSTANCE.newsToNewsDto(repository.save(newsModel));
    }

    @Override
    public boolean deleteById(Long id) {
        NewsModel newsModel = repository.findById(id).orElse(null);
        if (newsModel == null) return false;
        else {
            repository.deleteById(id);
            return true;
        }
    }

    @Override
    public List<NewsDTO> readByParameters(ParameterDTO parameters) {
        Set<NewsModel> newsModelSet = new HashSet<>();
        if (parameters.getTagId() != null) newsModelSet.addAll(repository.findNewsByTagId(parameters.getTagId()));
        if (parameters.getTagName() != null) newsModelSet.addAll(repository.findNewsByTagName(parameters.getTagName()));
        if (parameters.getAuthorName() != null) newsModelSet.addAll(repository.findNewsByAuthorName(parameters.getAuthorName()));
        if (parameters.getTitle() != null) newsModelSet.addAll(repository.findNewsByPartOfTitle(parameters.getTitle()));
        if (parameters.getContent() != null) newsModelSet.addAll(repository.findNewsByPartOfContent(parameters.getContent()));
        return NewsMapper.INSTANCE.newsListToNewsDtoList(new ArrayList<>(newsModelSet));
    }
}
