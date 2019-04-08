CREATE TABLE `user` (
  `id`            int(10) unsigned NOT NULL  AUTO_INCREMENT,
  `username`      VARCHAR(50)      NOT NULL
  COMMENT '用户帐号',
  `nickname`      VARCHAR(200)     NOT NULL  DEFAULT ''
  COMMENT '昵称',
  `salt`          VARCHAR(50)      NOT NULL
  COMMENT 'salt你懂的',
  `encrypted_pwd` VARCHAR(32)      NOT NULL
  COMMENT '加盐加密过的密码',
  `create_at`     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  `update_at`     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `ik_username`(`username`),
  KEY `ik_nickname`(`nickname`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户表';




