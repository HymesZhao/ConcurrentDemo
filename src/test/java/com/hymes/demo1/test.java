package com.hymes.demo1;

import com.hymes.demo1.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConcurrentDemoApplication.class)
public class test {
    @Autowired
    private CommentService commentService;

    @Test
    public void concurrentComment() {
//        commentService.postCommentNoLock(Long.parseLong("1"), "测试评论内容" );
    }

}
