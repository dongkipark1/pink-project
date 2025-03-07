package com.pinkproject.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    @Query("SELECT n FROM Notice n JOIN FETCH n.admin ORDER BY n.createdAt DESC")
    Page<Notice> findAllWithAdmin(Pageable pageable);

    @Query("SELECT n FROM Notice n JOIN FETCH n.admin WHERE n.title LIKE %:keyword% ORDER BY n.createdAt DESC")
    Page<Notice> findByKeywordWithNotice(@Param("keyword") String keyword, Pageable pageable);
}
