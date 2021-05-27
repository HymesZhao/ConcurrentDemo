package com.hymes.demo1.dao;

import com.hymes.demo1.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

/**
 * 文章
 *
 * @author HymesZhao
 * @date 2021/05/26
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    /**
     * 使用sql悲观锁
     *
     * @param articleId 文章的id
     * @return {@link Article}
     */
    @Query(value = "select * from tbl_article where tbl_article.id=?1 for update ", nativeQuery = true)
    Article findArticleWithSqlPessimisticLock(@Param("articleId") Long articleId);

    /**
     * 使用JPA悲观锁
     *
     * @param articleId 文章的id
     * @return {@link Article}
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Article a where a.id =?1")
    Article findArticleWithJpaPessimisticLock(@Param("articleId") Long articleId);

    /**
     * 更新文章与乐观锁
     * sql乐观锁
     *
     * @param commentCount 文章
     * @param id           id
     * @param version      版本
     * @return int
     */
    @Query(value = "update tbl_article set comment_count=?1 , version=version+1 where id=?2 and version=?3",
            nativeQuery = true)
    @Modifying
    int updateArticleWithOptimisticLock(@Param("commentCount") Long commentCount, @Param("id") Long id, @Param(
            "version") Long version);
}
