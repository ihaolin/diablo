package me.hao0.diablo.server;

import me.hao0.diablo.server.cluster.LeaderSelector;
import me.hao0.diablo.server.cluster.ServerRegister;
import me.hao0.diablo.server.cluster.ServerRouter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiabloServer{

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiabloServer.class, args);

        // server register
        final ServerRegister serverRegister = context.getBean(ServerRegister.class);
        serverRegister.start();

        // leader select
        final LeaderSelector leaderSelector = context.getBean(LeaderSelector.class);
        leaderSelector.start();

        // server route
        ServerRouter serverRouter = context.getBean(ServerRouter.class);
        serverRouter.start();
    }
}
