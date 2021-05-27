package com.hymes.demo1.dao;

import com.hymes.demo1.entity.Article;
import com.hymes.demo1.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 评论
 *
 * @author HymesZhao
 * @date 2021/05/26
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
