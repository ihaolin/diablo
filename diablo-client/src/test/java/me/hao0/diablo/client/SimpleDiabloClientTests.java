package me.hao0.diablo.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleDiabloClientTests {

    private SimpleDiabloClient client;

    @Before
    public void start(){
        client = new SimpleDiabloClient();
        client.setAppName("myapp");
        client.setAppKey("123456x");
        client.setServers("192.168.0.102:12345,127.0.0.1:12345");
        client.start();
    }

    @After
    public void shutdown(){
        if (client != null){
            client.shutdown();
        }
    }

    @Test
    public void testStartClient(){
        SimpleDiabloClient client = new SimpleDiabloClient();
        client.setAppName("myapp");
        client.setAppKey("123456x");
        client.setServers("127.0.0.1:12345,192.168.0.100:12345");
        client.start();
    }

    @Test
    public void testGet() throws InterruptedException {
        String test0 = client.get("test_config1");
        System.out.println(test0);

        //TODO update the test1 on console
        Thread.sleep(20000);

        test0 = client.get("test_config1");
        System.out.println(test0);
    }

    @Test
    public void testGetJson() throws InterruptedException {
        Student student = client.get("test99", Student.class);
        System.out.println(student);

        //TODO update the test99 on console
        Thread.sleep(20000);

        student = client.get("test99", Student.class);
        System.out.println(student);
    }
}
