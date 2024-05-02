package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.BaseAuthorAndTagService;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.TagDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.mapper.TagMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class TagService implements BaseAuthorAndTagService<TagDTO, Long> {
    private TagRepository repository;
    private NewsRepository newsRepository;

    @Override
    public List<TagDTO> readByNewsId(Long id) {
        return TagMapper.INSTANCE.tagListToTagDTOList(repository.findTagByNewsId(id));
    }

    @Override
    public List<TagDTO> readAll() {
        return TagMapper.INSTANCE.tagListToTagDTOList(repository.findAll());
    }

    @Override
    public TagDTO readById(Long id) throws NoSuchElementException {
        return TagMapper.INSTANCE.tagToTagDTO(repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such tag")));
    }

    @Override
    public TagDTO create(TagDTO createRequest) throws NoSuchElementException, ValidationException {
        TagModel tagModel = new TagModel();
        if (createRequest.getName() == null) throw new ValidationException("The fields should not be empty");

        tagModel.setName(createRequest.getName());

        Set<NewsModel> newsModelSet = new HashSet<>();
        for (Long newsId : createRequest.getNewsId()) {
            newsModelSet.add(newsRepository.findById(newsId).orElseThrow(() -> new NoSuchElementException("No such news")));
        }
        tagModel.setNews(newsModelSet);

        return TagMapper.INSTANCE.tagToTagDTO(repository.save(tagModel));
    }

    @Override
    public TagDTO update(TagDTO updateRequest, Long id) throws NoSuchElementException {
        TagModel tagModel = repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such tag"));
        if (updateRequest.getName() != null) tagModel.setName(updateRequest.getName());

        if (updateRequest.getNewsId() != null) {
            Set<NewsModel> newsModelSet = new HashSet<>();
            for (Long newsId : updateRequest.getNewsId()) {
                newsModelSet.add(newsRepository.findById(newsId).orElseThrow(() -> new NoSuchElementException("No such news")));
            }
            tagModel.setNews(newsModelSet);
        }

        return TagMapper.INSTANCE.tagToTagDTO(repository.save(tagModel));
    }

    @Override
    public boolean deleteById(Long id) {
        TagModel tagModel = repository.findById(id).orElse(null);
        if (tagModel == null) return false;
        else {
            repository.deleteById(id);
            return true;
        }
    }

    @Override
    public List<TagDTO> readByPartName(String name) {
        return TagMapper.INSTANCE.tagListToTagDTOList(repository.findTagByPartName(name));
    }

    @Override
    public Long getCountById(Long id) {
        return repository.countNewsByTagId(id);
    }
}
