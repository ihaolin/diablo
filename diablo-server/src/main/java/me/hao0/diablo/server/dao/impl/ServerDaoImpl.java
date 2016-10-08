package me.hao0.diablo.server.dao.impl;

import me.hao0.diablo.server.dao.ServerDao;
import me.hao0.diablo.server.model.ServerStatus;
import me.hao0.diablo.server.support.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class ServerDaoImpl implements ServerDao {

    @Autowired
    private StringRedisTemplate redis;

    @Override
    public boolean serverOnline(String server) {

        SetOperations<String, String> ops = redis.opsForSet();
        ops.add(RedisKeys.CLUSTER_SERVERS_ONLINE, server);
        ops.remove(RedisKeys.CLUSTER_SERVERS_OFFLINE, server);

        return Boolean.TRUE;
    }

    @Override
    public boolean serverOffline(String server) {
        SetOperations<String, String> ops = redis.opsForSet();
        ops.remove(RedisKeys.CLUSTER_SERVERS_ONLINE, server);
        ops.add(RedisKeys.CLUSTER_SERVERS_OFFLINE, server);

        return Boolean.TRUE;
    }

    @Override
    public boolean deleteServer(String serversKey, String server) {
        return redis.opsForSet().remove(serversKey, server) > 0;
    }

    @Override
    public Set<String> servers(ServerStatus status) {

        SetOperations<String, String> ops = redis.opsForSet();
        if (ServerStatus.ONLINE == status) {
            return ops.members(RedisKeys.CLUSTER_SERVERS_ONLINE);
        } else if (ServerStatus.OFFLINE == status){
            return ops.members(RedisKeys.CLUSTER_SERVERS_OFFLINE);
        } else {
            Set<String> servers = ops.members(RedisKeys.CLUSTER_SERVERS_ONLINE);
            servers.addAll(ops.members(RedisKeys.CLUSTER_SERVERS_OFFLINE));
            return servers;
        }
    }

    @Override
    public String getLeader() {
        return redis.opsForValue().get(RedisKeys.SERVER_LEADER);
    }
}
