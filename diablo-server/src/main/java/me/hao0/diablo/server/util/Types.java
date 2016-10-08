package me.hao0.diablo.server.util;

import com.fasterxml.jackson.databind.JavaType;
import me.hao0.diablo.common.util.JsonUtil;
import me.hao0.diablo.server.dto.ClientDto;
import java.util.ArrayList;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Types {

    JavaType LIST_CLIENT_DTO_TYPE = JsonUtil.INSTANCE.createCollectionType(ArrayList.class, ClientDto.class);
}
