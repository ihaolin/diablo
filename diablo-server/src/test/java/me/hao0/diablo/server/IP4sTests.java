package me.hao0.diablo.server;

import me.hao0.diablo.server.util.IP4s;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class IP4sTests {

    @Test
    public void testIp(){
        String ip = "127.0.0.1";
        int ipInt = IP4s.ipToInt(ip);
        System.out.println(ipInt);
        System.out.println(IP4s.intToIp(ipInt));
    }
}
