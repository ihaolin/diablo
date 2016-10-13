package me.hao0.diablo.client;

import com.google.common.base.Converter;
import me.hao0.diablo.client.listener.ConfigListener;
import me.hao0.diablo.common.convert.BooleanConverter;
import me.hao0.diablo.common.convert.Converters;
import me.hao0.diablo.common.convert.JavaTypeConverter;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ConfigListenerTests {

    @Test
    public void testGetListenerType(){
        ConfigListener<Boolean> boolListener = new ConfigListener<Boolean>() {

            @Override
            public String name() {
                return "boolConfig";
            }

            @Override
            public void onUpdate(Boolean value) {
                System.out.println("value is Boolean");
            }
        };

        Converter<String, ?> converter = Converters.determine(boolListener);
        assertEquals(BooleanConverter.INSTANCE, converter);

        ConfigListener<List<String>> listListener = new ConfigListener<List<String>>() {

            @Override
            public String name() {
                return "boolConfig";
            }

            @Override
            public void onUpdate(List<String> values) {
                System.out.println("value is List");
            }
        };
        converter = Converters.determine(listListener);
        assertTrue(converter instanceof JavaTypeConverter);
    }
}
