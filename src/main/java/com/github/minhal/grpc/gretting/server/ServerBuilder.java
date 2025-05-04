package com.github.minhal.grpc.gretting.server;

import dagger.Module;
import dagger.Provides;
import io.grpc.Server;

@Module
public class ServerBuilder {

    @Provides
    Server provideGRPCServer() {
        return io.grpc.ServerBuilder
                .forPort(50051)
                .addService(new GreetServiceImpl())
                .build();
    }
}
