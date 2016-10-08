package me.hao0.diablo.common.util;

import com.fasterxml.jackson.databind.JavaType;
import me.hao0.common.json.Jsons;
import me.hao0.diablo.common.model.ConfigItem;
import java.util.ArrayList;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JsonUtil {

     Jsons INSTANCE = Jsons.DEFAULT;

     JavaType LIST_CONFIG_ITEM_TYPE = INSTANCE.createCollectionType(ArrayList.class, ConfigItem.class);

     JavaType MAP_STR_STR_TYPE = INSTANCE.createCollectionType(Map.class, String.class, String.class);
}
