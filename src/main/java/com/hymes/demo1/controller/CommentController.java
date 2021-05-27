package com.hymes.demo1.controller;

import com.hymes.demo1.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 并发问题分析
     * 从刚才的代码实现里可以看出这个简单的评论功能的流程，当用户发起评论的请求时，
     * 从数据库找出对应的文章的实体类Article，然后根据文章信息生成对应的评论实体类Comment，
     * 并且插入到数据库中，接着增加该文章的评论数量，再把修改后的文章更新到数据库中，
     *
     * 在这个流程中有个问题，当有多个用户同时并发评论时，他们同时进入步骤1中拿到Article，然后插入对应的Comment，最后在步骤3
     * 中更新评论数量保存到数据库。只是由于他们是同时在步骤1拿到的Article，所以他们的Article.commentCount的值相同，
     * 那么在步骤3中保存的Article.commentCount+1也相同，那么原来应该+3的评论数量，只加了1。
     *
     * @param articleId 文章的id
     * @param content   内容
     * @return {@link String}
     */
    @PostMapping("comment")
    public String comment(Long articleId, String content) {
        try {
//            commentService.postCommentNoLock(articleId, content);
            commentService.postCommentWithSqlPessimisticLock(articleId, content);
//            commentService.postCommentWithJpaPessimisticLock(articleId, content);
//            commentService.postCommentWithJpaOptimisticLock(articleId, content);
//            commentService.postCommentWithSqlOptimisticLock(articleId, content);
        } catch (Exception e) {
            log.error("{}", e);
            return "error: " + e.getMessage();
        }
        return "success";
    }
}

