package com.github.minhal.grpc.gretting.server;

import dagger.Component;
import io.grpc.Server;

import java.io.IOException;


@Component(modules = com.github.minhal.grpc.gretting.server.ServerBuilder.class)
interface AppComponent {
    Server getServer();
}

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        AppComponent app = DaggerAppComponent.create();
        Server server = app.getServer();

        server.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread( () -> {
                    System.out.println("received shutdown request");
                    server.shutdown();
                    System.out.println("shutting down...");
                })
        );

        server.awaitTermination();
    }
}
