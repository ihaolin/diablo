package me.hao0.diablo.server.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import me.hao0.diablo.server.util.Logs;
import org.springframework.stereotype.Component;
import java.util.concurrent.Executors;

/**
 * Author: haolin
 * Date  : 7/14/15.
 * Email : haolin.h0@gmail.com
 */
@Component
public class EventDispatcher {

    private final EventBus eventBus;

    public EventDispatcher() {
        this(Runtime.getRuntime().availableProcessors() + 1);
    }

    public EventDispatcher(Integer threadCount) {
        eventBus = new AsyncEventBus(Executors.newFixedThreadPool(threadCount));
        Logs.info("event dispatcher has started!");
    }

    /**
     * Register the listener
     * @param listener the listener
     */
    public void register(Object listener) {
        eventBus.register(listener);
        Logs.info("register an listener({})", listener);
    }

    /**
     * Unregister the listener
     * @param listener the listener
     */
    public void unRegister(Object listener) {
        eventBus.unregister(listener);
        Logs.info("un register an listener({})", listener);
    }

    /**
     * Post an event
     * @param event the event
     */
    public void post(Object event) {
        eventBus.post(event);
        Logs.info("published an event({})", event);
    }
}