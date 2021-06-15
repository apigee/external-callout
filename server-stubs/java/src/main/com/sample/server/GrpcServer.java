package com.sample.server;

import com.google.apigee.proto.ExternalCallout.MessageContext;
import com.google.apigee.proto.ExternalCallout.Response;
import com.google.apigee.proto.ExternalCallout.Strings;
import com.google.apigee.proto.ExternalCalloutServiceGrpc.ExternalCalloutServiceImplBase;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

/**
 * Sample ExternalCallout gRPC server implementation to be hosted on Cloud Run. This can be used to
 * quickly deploy a gRPC server to experiment with the ExternalCallout policy.
 */
public class GrpcServer {
  private Server server;

  /** Implementation of the server to be deployed. */
  private static class ServerImpl extends ExternalCalloutServiceImplBase {
    @Override
    public void processMessage(
        MessageContext rpcRequest, StreamObserver<MessageContext> responseObserver) {
      // TODO: Optionally, modify what the server does.
      Response response =
          rpcRequest.getResponse().toBuilder()
              .setContent("Response from the gRPC server (Java).")
              .putHeaders("grpc.server.header", Strings.newBuilder().addStrings("Java").build())
              .build();
      MessageContext rpcResponse = rpcRequest.toBuilder().setResponse(response).build();

      responseObserver.onNext(rpcResponse);
      responseObserver.onCompleted();
    }
  }

  /** Creates a server on {@code port} using {@link ServerImpl} as the implementation. */
  public GrpcServer(int port) {
    server = NettyServerBuilder.forPort(port).addService(new ServerImpl()).build();
  }

  /**
   * Starts the server, and blocks until termination. Registers a shutdown hook to call {@link
   * #stop()} when shutdown begins.
   */
  public void start() throws IOException, InterruptedException {
    server.start();
    if (server != null) {
      server.awaitTermination();
    }
    Runtime.getRuntime().addShutdownHook(new Thread(GrpcServer.this::stop));
  }

  /** Stops the server without waiting for in-flight tasks to complete. */
  public void stop() {
    if (server != null) {
      System.err.println("Shutting down the server...");
      server.shutdownNow();
      System.err.println("Server has been shut down.");
    }
  }

  /**
   * Starts a server using environment variable PORT (or if it doesn't exist, using 8080). The
   * server blocks until terminated.
   *
   * <p>The port must be read from the environment variable because Cloud Run requires it. See
   * https://cloud.google.com/run/docs/triggering/grpc#unauthenticated-grpc.
   */
  public static void main(String[] args) {
    String portStr = System.getenv("PORT");
    if (portStr == null || portStr.isEmpty()) {
      portStr = "8080";
    }
    int port = Integer.parseInt(portStr);

    System.out.println("Starting a server on port " + port + "...");
    GrpcServer server = new GrpcServer(port);
    server.start();
  }
}
