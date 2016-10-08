package me.hao0.diablo.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * A Model will persist into storage
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Model<K> extends Serializable {

    K getId();

    void setId(K id);

    void setCtime(Date ctime);

    void setUtime(Date utime);
}
