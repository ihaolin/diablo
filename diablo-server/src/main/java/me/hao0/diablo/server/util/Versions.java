package me.hao0.diablo.server.util;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class Versions {

    private Versions(){}

    /**
     * Get the version int value:
     * 2.0.4 --> 204
     * @param version version string
     * @return version int value
     */
    public static int getInt(String version) {

        if (version == null){
            return -1;
        }

        String[] vs = version.split("\\.");
        int sum = 0;
        for (int i = 0; i < vs.length; i++) {
            try {
                sum = sum * 10 + Integer.parseInt(vs[i]);
            } catch (Exception e) {
                // ignore
            }
        }
        return sum;
    }
}
