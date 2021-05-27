package com.hymes.demo1.service.impl;

import com.hymes.demo1.dao.ArticleRepository;
import com.hymes.demo1.dao.CommentRepository;
import com.hymes.demo1.entity.Article;
import com.hymes.demo1.entity.Comment;
import com.hymes.demo1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 评论服务impl
 *
 * @author HymesZhao
 * @date 2021/05/26
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void postCommentNoLock(Long articleId, String content) {
        Article article = articleRepository.getOne(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        commentRepository.save(comment);
        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);
    }

    @Override
    @Transactional(rollbackOn =Exception.class)
    public void postCommentWithSqlPessimisticLock(Long articleId, String content) {
        // 然后把CommentService中使用的查询方法由原来的findById改为我们自定义的方法
        Article article = articleRepository.findArticleWithSqlPessimisticLock(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        commentRepository.save(comment);
        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);

    }

    @Override
    @Transactional(rollbackOn =Exception.class)
    public void postCommentWithJpaPessimisticLock(Long articleId, String content) {
        // 同样的只要在CommentService里把查询方法改为findArticleWithJpaPessimisticLock()
        Article article = articleRepository.findArticleWithJpaPessimisticLock(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        commentRepository.save(comment);
        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void postCommentWithJpaOptimisticLock(Long articleId, String content) {
        Article article = articleRepository.getOne(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        commentRepository.save(comment);
        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void postCommentWithSqlOptimisticLock(Long articleId, String content) {
        // 首先对于Article的查询方法只需要普通的findById()方法就行不用上任何锁。
        Article article = articleRepository.getOne(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        commentRepository.save(comment);
        // 接着在ArticleRepository增加更新的方法，注意这里是更新方法，和悲观锁时增加查询方法不同。
        // 可以看到update的where有一个判断version的条件，并且会set version = version + 1。这就保证了只有当数据库里的版本号和要更新
        // 的实体类的版本号相同的时候才会更新数据。
        // 接着在CommentService里稍微修改一下代码。
        // 然后更新Article的时候改用新加的updateArticleWithVersion()方法。可以看到这个方法有个返回值，这个返回值代表更新了的数据库行数，
        // 如果值为0的时候表示没有符合条件可以更新的行。
        // 这之后就可以由我们自己决定怎么处理了，这里是直接回滚，spring就会帮我们回滚之前的数据操作，把这次的所有操作都取消以保证数据的一致性。
        // 现在看到Article里的comment_count和Comment的数量都不是100了，但是这两个的值必定是一样的了。因为刚才我们处理的时候假如Article
        // 表的数据发生了冲突，那么就不会更新到数据库里，这时抛出异常使其事务回滚，这样就能保证没有更新Article的时候Comment也不会插入，就解决了数据不统一的问题。
        //
        // 这种直接回滚的处理方式用户体验比较差，通常来说如果判断Article更新条数为0时，会尝试重新从数据库里查询信息并重新修改，
        // 再次尝试更新数据，如果不行就再查询，直到能够更新为止。当然也不会是无线的循环这样的操作，
        // 会设置一个上线，比如循环3次查询修改更新都不行，这时候才会抛出异常。
        int rowNum = articleRepository.updateArticleWithOptimisticLock(article.getCommentCount() + 1, article.getId(),
                article.getVersion());
        if (rowNum == 0) {
            throw new RuntimeException("服务器繁忙,更新数据失败");
        }
    }
}
