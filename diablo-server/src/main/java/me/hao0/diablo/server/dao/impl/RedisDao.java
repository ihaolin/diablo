package me.hao0.diablo.server.dao.impl;

import com.google.common.collect.Lists;
import me.hao0.common.date.Dates;
import me.hao0.diablo.server.dao.BaseDao;
import me.hao0.diablo.server.model.Model;
import me.hao0.diablo.server.support.RedisIds;
import me.hao0.diablo.server.support.RedisKeys;
import me.hao0.diablo.server.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SuppressWarnings("unchecked")
public class RedisDao<T extends Model> implements BaseDao<T> {

    @SuppressWarnings("unchecked")
    protected final Class genericClazz =
            (Class<T>)((ParameterizedType)getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];

    protected final String CLASSNAMES = genericClazz.getSimpleName().toLowerCase() + "s";

    @Autowired
    protected  StringRedisTemplate redis;

    @Autowired
    private RedisIds ids;

    @Override
    public Boolean save(Model m) {

        Date now = Dates.now();
        boolean isCreated = false;
        if (m.getId() == null){
            // create
            isCreated = true;
            m.setId(ids.generate(genericClazz));
            m.setCtime(now);
        }
        m.setUtime(now);

        // save object
        String objKey = objectKey(m.getId());
        Map<?, ?> objMap = Maps.toMap(m);
        redis.opsForHash().putAll(objKey, objMap);

        // bind ids if create
        if (isCreated){
            String idsKey = RedisKeys.keyOfIds(genericClazz);
            redis.opsForList().leftPush(idsKey, m.getId().toString());
        }

        return Boolean.TRUE;
    }

    @Override
    public T findById(Long id) {
        String objKey = objectKey(id);
        Map objectMap = redis.opsForHash().entries(objKey);
        if (objectMap == null || objectMap.isEmpty()){
            return null;
        }
        return (T)Maps.fromMap(objectMap, genericClazz);
    }

    @Override
    public Boolean delete(Long id) {

        // delete id in ids
        String idsKey = RedisKeys.keyOfIds(genericClazz);
        redis.opsForList().remove(idsKey, 1, String.valueOf(id));

        // delete object
        String objKey = objectKey(id);
        redis.delete(objKey);

        return Boolean.TRUE;
    }

    @Override
    public List<T> findByIds(final List<String> ids) {
        List<T> objs = Lists.newArrayListWithExpectedSize(ids.size());
        for (String id : ids){
            objs.add(findById(Long.valueOf(id)));
        }
        return objs;
    }

    @Override
    public Long count() {
        return count(RedisKeys.keyOfIds(genericClazz));
    }

    @Override
    public Long count(String listKey) {
        return redis.opsForList().size(listKey);
    }

    @Override
    public List<T> list(Integer offset, Integer limit) {
        return list(RedisKeys.keyOfIds(genericClazz), offset, limit);
    }

    @Override
    public List<String> listStr(String listKey, Integer offset, Integer limit) {
        return redis.opsForList().range(listKey, offset, offset + limit - 1);
    }

    @Override
    public List<T> list(String idsKey, Integer offset, Integer limit) {
        final List<String> ids = redis.opsForList().range(idsKey, offset, offset + limit - 1);
        if (ids == null || ids.isEmpty()){
            return Collections.emptyList();
        }

        return findByIds(ids);
    }

    /**
     * Default use id as unique key
     */
    protected String objectKey(Object id) {
        return RedisKeys.format(CLASSNAMES, id.toString());
    }
}
