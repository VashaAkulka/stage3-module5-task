package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {
    @Query("SELECT t FROM TagModel t join t.news n where n.id = :newsId")
    List<TagModel> findTagByNewsId(@Param("newsId") Long newsId);

    @Query("SELECT t FROM TagModel t where t.name LIKE %:name%")
    List<TagModel> findTagByPartName(@Param("name") String name);

    @Query("SELECT COUNT(n.id) FROM TagModel t JOIN t.news n WHERE t.id = :tagId")
    Long countNewsByTagId(@Param("tagId") Long tagId);
}
