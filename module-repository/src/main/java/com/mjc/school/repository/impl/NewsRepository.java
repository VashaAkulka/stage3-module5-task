package com.mjc.school.repository.impl;

import com.mjc.school.repository.model.NewsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsModel, Long> {
    @Query("SELECT n FROM NewsRepository n join n.tags t where t.id = :tagId")
    List<NewsModel> findNewsByTagId(@Param("tagId") Long tagId);

    @Query("SELECT n FROM NewsRepository n join n.tags t where t.name = :tagName")
    List<NewsModel> findNewsByTagName(@Param("tagName") String tagName);

    @Query("SELECT n FROM NewsRepository n join n.author a where a.name = :authorName")
    List<NewsModel> findNewsByAuthorName(@Param("authorName") String authorName);

    @Query("SELECT n FROM NewsRepository n WHERE n.title LIKE %:title%")
    List<NewsModel> findNewsByPartOfTitle(@Param("title") String title);

    @Query("SELECT n FROM NewsRepository n WHERE n.title LIKE %:content%")
    List<NewsModel> findNewsByPartOfContent(@Param("content") String content);
}
