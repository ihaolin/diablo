package me.hao0.diablo.server.util;

import com.google.common.base.Objects;
import org.springframework.data.redis.core.ValueOperations;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A Simple Redis Lock
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SuppressWarnings("unchecked")
public class SimpleRedisLock {

    private static final int ONE_SECOND = 1000;

    /**
     * Default acquire lock timeout
     */
    private static final int DEFAULT_ACQUIRE_TIMEOUT_MILLIS = Integer.getInteger("diablo.lock.acquire.timeout", 30 * ONE_SECOND);;

    /**
     * Default pause time when trying to require lock
     */
    public static final int DEFAULT_ACQUIRE_PAUSE_MILLIS = Integer.getInteger("diablo.lock.acquire.pause", 100);

    /**
     * Default expired time of lock
     */
    private static final int DEFAULT_EXPIRED_TIME_MILLIS = Integer.getInteger("diablo.lock.expired.timeout", 10 * ONE_SECOND);

    private ValueOperations ops;

    private final String lockKey;

    private final String lockValue;

    private int acquireTimeout;

    private long expiredTimeout;

    private boolean isHold = false;

    SimpleRedisLock(ValueOperations ops, String lockKey, String lockValue) {
        this(ops, lockKey, lockValue, DEFAULT_ACQUIRE_TIMEOUT_MILLIS, DEFAULT_EXPIRED_TIME_MILLIS);
    }

    SimpleRedisLock(ValueOperations ops, String lockKey, String lockValue, int acquireTimeout, int expiredTimeout){
        this.ops = ops;
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.acquireTimeout = acquireTimeout;
        this.expiredTimeout = expiredTimeout;
    }

    void setAcquireTimeout(int acquireTimeout) {
        this.acquireTimeout = acquireTimeout;
    }

    void setExpiredTimeout(long expiredTimeout) {
        this.expiredTimeout = expiredTimeout;
    }

    /**
     * acquire the lock
     * @return return true if required successfully, or waiting to acquireTimeout elapsed and return false
     */
    public boolean acquire(){
        int timeout = acquireTimeout;
        while (timeout >= 0) {

            if (ops.setIfAbsent(lockKey, lockValue)){
                // lock successfully
                new Thread(new LockExpiredTask()).start();
                isHold = true;
                return true;
            }

            // Others locked it
            // Wait for a while & acquire again
            try {
                timeout -= DEFAULT_ACQUIRE_PAUSE_MILLIS;
                Thread.sleep(DEFAULT_ACQUIRE_PAUSE_MILLIS);
            } catch (InterruptedException e) {
                // ignore
            }

        }

        return false;
    }

    public void release(){
        if (isHold){

            Object redisLockValue = ops.get(lockKey);
            if (!Objects.equal(lockValue, redisLockValue)){
                ops = null;
                isHold = false;
                return;
            }

            ops.getOperations().delete(lockKey);
            ops = null;
            isHold = false;
        }
    }

    private class LockExpiredTask implements Runnable{

        @Override
        public void run() {
            while (isHold){
                try {
                    // refresh lock expired time
                    ops.getOperations().expire(lockKey, expiredTimeout, TimeUnit.MILLISECONDS);
                    Thread.sleep(expiredTimeout / 5);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    public static Builder newBuilder(ValueOperations ops, String lockKey){
        return newBuilder(ops, lockKey, UUID.randomUUID().toString());
    }

    public static Builder newBuilder(ValueOperations ops, String lockKey, String lockValue){
        return new Builder(ops, lockKey, lockValue);
    }

    public static class Builder{

        private SimpleRedisLock lock;

        Builder(ValueOperations ops, String lockKey, String lockValue){
            lock = new SimpleRedisLock(ops, lockKey, lockValue);
        }

        public Builder aquireTimeout(int mills){
            lock.setAcquireTimeout(mills);
            return this;
        }

        public Builder expredTimeout(int mills){
            lock.setExpiredTimeout(mills);
            return this;
        }

        public SimpleRedisLock build(){
            return lock;
        }
    }
}
