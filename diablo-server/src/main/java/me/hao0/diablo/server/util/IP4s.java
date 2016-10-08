package me.hao0.diablo.server.util;

import java.net.InetAddress;

/**
 * IP Util
 */
public class IP4s {
  
    public static byte[] ipToBytesByInet(String ipAddr) {
        try {
            return InetAddress.getByName(ipAddr).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }

    public static int ipToInt(String ipAddr) {  
        try {  
            return bytesToInt(ipToBytesByInet(ipAddr));  
        } catch (Exception e) {  
            throw new IllegalArgumentException(ipAddr + " is invalid IP");  
        }  
    }  

    public static String intToIp(int ipInt) {  
        return String.valueOf(((ipInt >> 24) & 0xff)) + '.' +
                ((ipInt >> 16) & 0xff) + '.' + ((ipInt >> 8) & 0xff) + '.' + (ipInt & 0xff);
    }
}  