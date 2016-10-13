package me.hao0.diablo.client;

import me.hao0.diablo.common.convert.Converters;
import org.junit.Test;
import java.lang.reflect.Field;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ConvertersTests {

    @Test
    public void testDetermine() throws NoSuchFieldException {
        Field field = Student.class.getDeclaredField("id");
        System.out.println(Converters.determine(field));
    }
}
