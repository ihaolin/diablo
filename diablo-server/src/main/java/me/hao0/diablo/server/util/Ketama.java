package me.hao0.diablo.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The Ketama implementation
 * @param <T> the generic type
 */
public class Ketama<T> {

    private final Integer virtual;

    private final SortedMap<Long, T> circle = new TreeMap<>();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock rLock = rwLock.readLock();

    private final Lock wLock = rwLock.writeLock();

    private MessageDigest md5Algorithm;

    public Ketama() {
        this(400, null);
    }

    public Ketama(Integer virtual) {
        this(virtual, null);
    }

    public Ketama(List<T> nodes) {
        this(400, nodes);
    }

    public Ketama(int virtual, List<T> nodes) {
        this.virtual = virtual;
        try {
            md5Algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 isn't available.");
        }
        add(nodes);
    }

    public void add(T node) {
        wLock.lock();
        try {
            addNode(node);
        } finally {
            wLock.unlock();
        }
    }

    public void add(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()){
            return;
        }
        wLock.lock();
        try {
            for (T node : nodes) {
                addNode(node);
            }
        } finally {
            wLock.unlock();
        }
    }

    private void addNode(T node) {
        for (int i = 0; i < virtual / 4; i++) {
            byte[] digest = md5(node.toString() + i);
            for (int h = 0; h < 4; h++) {
                circle.put(hash(digest, h), node);
            }
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hash(key.toString());
        rLock.lock();
        try {
            if (!circle.containsKey(hash)) {
                SortedMap<Long, T> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        } finally {
            rLock.unlock();
        }
    }

    public void remove(List<T> nodes) {
        wLock.lock();
        try {
            for (T node : nodes) {
                removeNode(node);
            }
        } finally {
            wLock.unlock();
        }
    }

    public void remove(T node) {
        wLock.lock();
        try {
            removeNode(node);
        } finally {
            wLock.unlock();
        }
    }

    private void removeNode(T node) {
        for (int i = 0; i < virtual / 4; i++) {
            byte[] digest = md5(node.toString() + i);
            for (int h = 0; h < 4; h++) {
                circle.remove(hash(digest, h));
            }
        }
    }

    private long hash(final String k) {
        byte[] digest = md5(k);
        return hash(digest, 0) & 0xffffffffL;
    }

    private long hash(byte[] digest, int h) {
        return ((long) (digest[3 + h * 4] & 0xFF) << 24) | ((long) (digest[2 + h * 4] & 0xFF) << 16) | ((long) (digest[1 + h * 4] & 0xFF) << 8) | (digest[h * 4] & 0xFF);
    }

    private byte[] md5(String text) {
        md5Algorithm.update(text.getBytes());
        return md5Algorithm.digest();
    }

    public Map<Long, T> getCircle(){
        rLock.lock();
        try {
            return circle;
        } finally {
            rLock.unlock();
        }
    }
}