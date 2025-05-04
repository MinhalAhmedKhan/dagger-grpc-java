package com.github.minhal.grpc.gretting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String result = "Hello " + greeting.getFirstName();

        // create response
        GreetResponse gr = GreetResponse.newBuilder().setResult(result).build();
        // send response
        responseObserver.onNext(gr);
        // complete the rpc call
        responseObserver.onCompleted(); // .onCompleted has to be called to complete the response

    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {

        Context ctx = Context.current();

        String firstName = request.getGreeting().getFirstName();


        if (ctx.isCancelled()){
            // stop and clear up
        }

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number: " + i;
                responseObserver.onNext(GreetManyTimesResponse.newBuilder().setResult(result).build());
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        // Since the client is async we return a Stream to handle each request async
        StreamObserver<LongGreetRequest> streamObserverOfRequest = new StreamObserver<>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message
                System.out.println("Running request in thread: " + Thread.currentThread().getId());
                result += "Hello " + value.getGreeting().getFirstName() + value.getGreeting().getLastName() + "!";
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client is done
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                responseObserver.onCompleted();
            }
        };
        return streamObserverOfRequest;
    }
}
