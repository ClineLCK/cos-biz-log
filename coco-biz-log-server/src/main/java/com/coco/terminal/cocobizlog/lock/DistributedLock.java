package com.coco.terminal.cocobizlog.lock;

/**
 * 分布式锁
 *
 * 可以实现的有redis、zk等
 *
 * @author ckli01
 * @date 2018/9/18
 */
public interface DistributedLock {


    /**
     * 锁过期时间
     */
    public static final long LOCK_TIMEOUT_MILLIS = 10000;

    /**
     * 尝试获取锁次数
     */
    public static final int LOCK_RETRY_TIMES = 10;

    /**
     * 获取锁休眠时间
     */
    public static final long LOCK_SLEEP_MILLIS = 500;

    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    boolean lock(String key, long expire);

    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    boolean releaseLock(String key);

}
