package com.coco.terminal.cocobizlog.lock.impl;

import com.coco.terminal.cocobizlog.lock.AbstractRedisDistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * es 初始化 售点 索引
 *
 * @author ckli01
 * @date 2019/11/8
 */
@Component
public class CommonRedisDistributedLock extends AbstractRedisDistributedLock {

    @Override
    @Autowired
    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        super.setRedisTemplate(redisTemplate);
    }
}

    
    
  