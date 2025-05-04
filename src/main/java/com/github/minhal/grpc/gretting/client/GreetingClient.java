package com.github.minhal.grpc.gretting.client;

import com.proto.greet.*;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    static ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext() // deactivate ssl
            .build();

    public static void main(String[] args) {

        // Async client
        GreetServiceGrpc.GreetServiceStub syncClient = GreetServiceGrpc.newStub(channel);

        // create a concurrent latch used by the stream observor response to signal when the server is done
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObservor =  syncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // we get a response from server
                System.out.println("recieved from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // we get error from server
            }

            @Override
            public void onCompleted() {
                // server signalled completed
                System.out.println("Server was done");
                // signal server was done
                latch.countDown();
            }
        });


        // Stream first GreetRequest
        requestObservor.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Minhal").setLastName("Khan").build()).build());
        // stream second
        requestObservor.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Muntaha").setLastName("Ahmed").build()).build());
        // stream third
        requestObservor.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Mini").setLastName("Khan").build()).build());
        // tell server we are done sending requests
        requestObservor.onCompleted();

        // we need to wait for server to complete
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channel.shutdown();
    }
}


