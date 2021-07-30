package com.coco.terminal.cocobizlog.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * redis 分布式锁抽象类
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 当前方案：
 * 通过 redis命令 SET key value [EX seconds] [PX milliseconds] [NX|XX]
 * 设置 key 过期时间 ， 并比较 value 唯一性 来防止误删别的线程的锁
 * 重试获取锁机制相同
 * 修复原方案bug
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 之前版本方案：
 * 1、setNx 命令 尝试对 key 加值，若设置成功，即获取到锁，失败转到2
 * 2、get 命令 获取 key 中值， 判断当前时间与值对应时间 差 是否超过给定时间，即判断死锁
 * 若没有超时，等待 少数时间 继续回去， 一定次数后还未获取到锁 返回 加锁失败
 * 若死锁，转3
 * 3、getSet 命令 对 key 加锁，获取返回值 value 与传值 比较是否相同， 若相同即获取到锁，若不同，
 * 重复 2、3
 * <p>
 * <p>
 * BUG -> 假如C1获取到了锁，这个时候redis挂了，并且数据没有持久化，等redis服务启动起来，C2请求过来获取到了锁。
 * 但是C1请求现在执行完了删除了key，这个时候就把C2的锁删掉了
 *
 * @author ckli01
 * @date 2018/9/18
 */
@Slf4j
public abstract class AbstractRedisDistributedLock implements DistributedLock {


    private RedisTemplate<Object, Object> redisTemplate;

    private ThreadLocal<String> lockThreadLocal = new ThreadLocal<>();

    protected static final String UNLOCK_LUA_STR;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA_STR = sb.toString();
    }

    @Override
    public boolean lock(String key) {
        return this.lock(key, LOCK_TIMEOUT_MILLIS, LOCK_RETRY_TIMES, LOCK_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes) {
        return this.lock(key, LOCK_TIMEOUT_MILLIS, retryTimes, LOCK_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes, long sleepMillis) {
        return this.lock(key, LOCK_TIMEOUT_MILLIS, retryTimes, sleepMillis);
    }

    @Override
    public boolean lock(String key, long expire) {
        return this.lock(key, expire, LOCK_RETRY_TIMES, LOCK_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes) {
        return this.lock(key, expire, retryTimes, LOCK_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = setRedisLock(key, expire);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while ((!result) && retryTimes-- > 0) {
            try {
                log.debug("redisDistributedLock setRedisLock failed for key: {} retryTimes: {}", key, retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedisLock(key, expire);
        }
        return result;
    }


    /**
     * 释放锁
     * 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
     *
     * @param key
     * @return
     */
    @Override
    public boolean releaseLock(String key) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(lockThreadLocal.get());

            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            Long result = getRedisTemplate().execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA_STR, keys, args);
                }
                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA_STR, keys, args);
                }
                return 0L;
            });
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("redisDistributedLock releaseLock failed for key: {} error: {}", key, e.getMessage(), e);
        } finally {
            lockThreadLocal.remove();
        }
        return false;
    }


    /**
     * 通过 redis命令 SET key value [EX seconds] [PX milliseconds] [NX|XX]
     * 设置redis 锁随机内容防止 方法执行时间过长  错杀别的线程获取的锁
     *
     * @param key
     * @param expireTime
     * @return
     */
    private boolean setRedisLock(String key, long expireTime) {
        try {
            String result = getRedisTemplate().execute((RedisCallback<String>) connection -> {
                JedisCommands jedisCommands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                lockThreadLocal.set(uuid);
                return jedisCommands.set(key, uuid, "NX", "PX", expireTime);
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("redisDistributedLock setRedisLock occured an error: {} for key: {}", e.getMessage(), key, e);
        }
        return false;
    }

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisTemplate<Object, Object> getRedisTemplate() {
        return redisTemplate;
    }
}
