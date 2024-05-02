package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.CommentRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.model.CommentModel;
import com.mjc.school.service.BaseExtendService;
import com.mjc.school.service.dto.CommentDTO;
import com.mjc.school.service.exception.NoSuchElementException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.CommentMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService implements BaseExtendService<CommentDTO, Long> {
    private CommentRepository repository;
    private NewsRepository newsRepository;

    @Override
    public List<CommentDTO> readByNewsId(Long id) {
        return CommentMapper.INSTANCE.commentListToCommentDTOList(repository.findCommentByNewsId(id));
    }

    @Override
    public List<CommentDTO> readAll() {
        return CommentMapper.INSTANCE.commentListToCommentDTOList(repository.findAll());
    }

    @Override
    public CommentDTO readById(Long id) throws NoSuchElementException {
        return CommentMapper.INSTANCE.commentToCommentDTO(repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such comment")));
    }

    @Override
    public CommentDTO create(CommentDTO createRequest) throws NoSuchElementException, ValidationException {
        CommentModel commentModel = new CommentModel();
        if (createRequest.getNewsId() == null || createRequest.getContent() == null) throw new ValidationException("The fields should not be empty");

        commentModel.setCreateDate(LocalDateTime.now());
        commentModel.setLastUpdateDate(LocalDateTime.now());
        commentModel.setContent(createRequest.getContent());

        commentModel.setNews(newsRepository.findById(createRequest.getNewsId()).orElseThrow(() -> new NoSuchElementException("No such news")));
        return CommentMapper.INSTANCE.commentToCommentDTO(repository.save(commentModel));
    }

    @Override
    public CommentDTO update(CommentDTO updateRequest, Long id) throws NoSuchElementException {
        CommentModel commentModel = repository.findById(id).orElseThrow(() -> new NoSuchElementException("No such comment"));
        commentModel.setLastUpdateDate(LocalDateTime.now());
        if (updateRequest.getContent() != null) commentModel.setContent(updateRequest.getContent());

        if (updateRequest.getNewsId() != null)
            commentModel.setNews(newsRepository.findById(updateRequest.getNewsId()).orElseThrow(() -> new NoSuchElementException("No such news")));
        return CommentMapper.INSTANCE.commentToCommentDTO(repository.save(commentModel));
    }

    @Override
    public boolean deleteById(Long id) {
        CommentModel commentModel = repository.findById(id).orElse(null);
        if (commentModel == null) return false;
        else {
            repository.deleteById(id);
            return true;
        }
    }
}
