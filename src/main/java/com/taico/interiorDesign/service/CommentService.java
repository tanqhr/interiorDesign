package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.entity.CommentEntity;
import org.springframework.stereotype.Service;
import com.taico.interiorDesign.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentEntity save(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    public List<CommentEntity> getProjectComments(Long projectId) {
        return commentRepository.findAllByProjectIdOrderByCreatedAtAsc(projectId);
    }
}