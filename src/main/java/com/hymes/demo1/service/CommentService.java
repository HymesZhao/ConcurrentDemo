package com.hymes.demo1.service;

import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    //悲观锁和乐观锁比较
    //悲观锁适合写多读少的场景。因为在使用的时候该线程会独占这个资源，在本文的例子来说就是某个id
    // 的文章，如果有大量的评论操作的时候，就适合用悲观锁，否则用户只是浏览文章而没什么评论的话，用悲观锁就会经常加锁，增加了加锁解锁的资源消耗。
    //
    //乐观锁适合写少读多的场景。由于乐观锁在发生冲突的时候会回滚或者重试，如果写的请求量很大的话，就经常发生冲突，经常的回滚和重试，
    // 这样对系统资源消耗也是非常大。
    //
    //所以悲观锁和乐观锁没有绝对的好坏，必须结合具体的业务情况来决定使用哪一种方式。另外在阿里巴巴开发手册里也有提到：
    //
    //如果每次访问冲突概率小于 20%，推荐使用乐观锁，否则使用悲观锁。乐观锁的重试次 数不得小于 3 次。
    //
    //阿里巴巴建议以冲突概率20%这个数值作为分界线来决定使用乐观锁和悲观锁，虽然说这个数值不是绝对的，但是作为阿里巴巴各个大佬总结出来的也是一个很好的参考。


    /**
     * 这里我们开了100个线程，同时发送评论请求，对应的文章id为1。
     *
     * 在发送请求前，数据库数据为
     * id   comment_count title
     * 1	     391      文章1
     * 明显的看到在article表里的comment_count的值不是100，这个值不一定是我图里的14，但是必然是不大于100的，而comment表的数量肯定等于100。
     * 这就展示了在文章开头里提到的并发问题，这种问题其实十分的常见，只要有类似上面这样评论功能的流程的系统，都要小心避免出现这种问题。
     *
     *
     * @param articleId 文章的id
     * @param content   内容
     */
    void postCommentNoLock(Long articleId, String content);

    /**
     * 悲观锁解决并发问题
     * 悲观锁顾名思义就是悲观的认为自己操作的数据都会被其他线程操作，所以就必须自己独占这个数据，可以理解为”独占锁“。在java中synchronized
     * 和ReentrantLock等锁就是悲观锁，数据库中表锁、行锁、读写锁等也是悲观锁。
     *
     * 利用SQL解决并发问题
     * 行锁就是操作数据的时候把这一行数据锁住，其他线程想要读写必须等待，但同一个表的其他数据还是能被其他线程操作的。只要在需要查询的sql后面加上for
     * update，就能锁住查询的行，特别要注意查询条件必须要是索引列，如果不是索引就会变成表锁，把整个表都锁住。
     *
     * 现在在原有的代码的基础上修改一下，先在ArticleRepository增加一个手动写sql查询方法。
     *
     * 这样我们查出来的Article，在我们没有将其提交事务之前，其他线程是不能获取修改的，保证了同时只有一个线程能操作对应数据。
     * 现在再用JMeter测一下，article.comment_count的值必定是100。
     *
     * @param articleId 文章的id
     * @param content   内容
     */
    void postCommentWithSqlPessimisticLock(Long articleId, String content);

    /**
     * 利用JPA自带行锁解决并发问题
     * 对于刚才提到的在sql后面增加for update，JPA有提供一个更优雅的方式，就是@Lock注解，这个注解的参数可以传入想要的锁级别。
     *
     * 现在在ArticleRepository中增加JPA的锁方法，其中LockModeType.PESSIMISTIC_WRITE参数就是行锁。
     *
     * 再Jmeter测一下，肯定不会有并发问题。而且这时看一下控制台打印信息，发现实际上查询的sql还是加了for update，只不过是JPA帮我们加了而已。
     * @param articleId 文章的id
     * @param content   内容
     */
    void postCommentWithJpaPessimisticLock(Long articleId, String content);

    /**
     * 乐观锁解决并发问题
     * 乐观锁顾名思义就是特别乐观，认为自己拿到的资源不会被其他线程操作所以不上锁，只是在插入数据库的时候再判断一下数据有没有被修改。
     * 所以悲观锁是限制其他线程，而乐观锁是限制自己，虽然他的名字有锁，但是实际上不算上锁，只是在最后操作的时候再判断具体怎么操作。
     * 乐观锁通常为版本号机制或者CAS算法
     *
     * 利用SQL实现版本号解决并发问题
     * 版本号机制就是在数据库中加一个字段当作版本号，比如我们加个字段version。那么这时候拿到Article的时候就会带一个版本号，比如拿到的版本是1，
     * 然后你对这个Article一通操作，操作完之后要插入到数据库了。发现哎呀，怎么数据库里的Article版本是2，和我手里的版本不一样啊，
     * 说明我手里的Article不是最新的了，那么就不能放到数据库了。这样就避免了并发时数据冲突的问题。
     *
     * 对应的实体类也增加version字段
     *
     *
     * @param articleId 文章的id
     * @param content   内容
     */
    void postCommentWithSqlOptimisticLock(Long articleId, String content);

    /**
     * 利用JPA实现版本现解决并发问题
     * JPA对悲观锁有实现方式，乐观锁自然也是有的，现在就用JPA自带的方法实现乐观锁。
     *
     * 首先在Article实体类的version字段上加上@Version注解，我们进注解看一下源码的注释，可以看到有部分写到:
     *
     * 注释里面说版本号的类型支持int, short, long三种基本数据类型和他们的包装类以及Timestamp，我们现在用的是Long类型
     *
     * 接着只需要在CommentService里的评论流程修改回我们最开头的“会触发并发问题”的业务代码就行了。说明JPA的这种乐观锁实现方式是非侵入式的。
     * 同样的Article里的comment_count和Comment的数量也不是100，但是这两个数值肯定是一样的。
     * 看一下IDEA的控制台会发现系统抛出了ObjectOptimisticLockingFailureException的异常。
     * 这和刚才我们自己实现乐观锁类似，如果没有成功更新数据则抛出异常回滚保证数据的一致性。
     * 如果想要实现重试流程可以捕获ObjectOptimisticLockingFailureException这个异常，
     * 通常会利用AOP+自定义注解来实现一个全局通用的重试机制，这里就是要根据具体的业务情况来拓展了，想要了解的可以自行搜索一下方案。
     * @param articleId 文章的id
     * @param content   内容
     */
    void postCommentWithJpaOptimisticLock(Long articleId, String content);


}
