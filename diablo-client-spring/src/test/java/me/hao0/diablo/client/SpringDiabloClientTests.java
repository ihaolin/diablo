package me.hao0.diablo.client;

import me.hao0.diablo.client.config.MyAppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:diablo-context.xml")
public class SpringDiabloClientTests {

    @Autowired
    private MyAppConfig myappConfig;

    @Test
    public void test() throws InterruptedException {

        for (;;){
            // TODO update some config on diablo tower
            Thread.sleep(5000L);
            System.out.println(myappConfig);
        }
    }
}
