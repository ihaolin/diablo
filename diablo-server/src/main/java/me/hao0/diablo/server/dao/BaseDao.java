package me.hao0.diablo.server.dao;

import me.hao0.diablo.server.model.Model;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface BaseDao<T extends Model> {

    /**
     * Persist the object
     * @param t the object
     * @return return true if save successfully, or false
     */
    Boolean save(Model t);

    /**
     * Find the object by id
     * @param id the object id
     * @return the object
     */
    T findById(Long id);

    /**
     * Delete the object by id
     * @param id the object id
     * @return return true if delete successfully, or false
     */
    Boolean delete(Long id);

    /**
     * Find the objects by id listByAppId
     * <p><b>Note:</b>  Not Support for Redis Proxy Middleware
     * @param ids id listByAppId
     * @return the objects
     */
    List<T> findByIds(List<String> ids);

    /**
     * Count all
     * @return the total countByAppId
     */
    Long count();

    /**
     * Count all
     * @param listKey the list key
     * @return the total countByAppId
     */
    Long count(String listKey);

    /**
     * List the objects
     * @param offset start offset
     * @param limit limit
     * @return the objects
     */
    List<T> list(Integer offset, Integer limit);

    /**
     * List the strings
     * @param offset the offset
     * @param limit the limit
     * @return string list
     */
    List<String> listStr(String listKey, Integer offset, Integer limit);

    /**
     * List the object
     * @param idsKey the ids key
     * @param offset start offset
     * @param limit limit
     * @return the objects
     */
    List<T> list(String idsKey, Integer offset, Integer limit);
}
