package me.hao0.diablo.server;

import me.hao0.diablo.server.util.SimpleRedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiabloServerTest.class)
public class SimpleRedisLockTests {

    @Autowired
    private StringRedisTemplate redis;

    @Test
    public void testAcquireRelease(){
        SimpleRedisLock.Builder builder = SimpleRedisLock.newBuilder(redis.opsForValue(), "lock_test");
        SimpleRedisLock lock = builder.build();

        try {
            if (!lock.acquire()){
                System.err.println("locked timeout");
                return;
            }
            System.err.println("locked it...");
            Thread.sleep(40000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock != null){
                lock.release();
                System.err.println("unlocked it...");
            }
        }
    }
}
