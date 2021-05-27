### 创建文章表添加默认数据：
CREATE TABLE `tbl_article` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `comment_count` int(4) NOT NULL DEFAULT '0' COMMENT '文章的评论数量',
  `title` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT '0' COMMENT '乐观锁是需要的版本号字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='article 文章表';
INSERT INTO `tbl_article` VALUES ('1', '0', '文章1', '0');


### 创建评论表：
CREATE TABLE `tbl_comment` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `article_id` int(4) NOT NULL COMMENT '评论的文章id',
  `content` varchar(255) NOT NULL COMMENT '评论内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='comment 评论表';