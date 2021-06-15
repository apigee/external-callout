package com.sample.client;

import com.google.apigee.proto.ExternalCallout.MessageContext;
import com.google.apigee.proto.ExternalCalloutServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Sample gRPC client implementation. This can be used for test purposes to send a request to a
 * local or deployed gRPC server.
 */
public class GrpcClient {

  private ExternalCalloutServiceGrpc.ExternalCalloutServiceBlockingStub serverStub;
  private ManagedChannel channel;

  /**
   * Creates a blocking server stub using a channel for a server located at
   * {@code host}:{@code port}.
   *
   * <p> Note: If {@code host} is localhost or 127.0.0.1, then disable TLS and use plaintext.
   * Otherwise, this will default to using system certificates.
   */
  public GrpcClient(String host, int port) {
    NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(host, port);
    if (host.equals("localhost") || host.equals("127.0.0.1")) {
      channelBuilder.usePlaintext();
    }
    channel = channelBuilder.build();
    serverStub = ExternalCalloutServiceGrpc.newBlockingStub(channel);
  }

  /** Sends a request to the server. */
  public MessageContext processMessage() {
    // TODO: Optionally, modify the content of the request.
    MessageContext rpcRequest = MessageContext.newBuilder().build();
    return serverStub.processMessage(rpcRequest);
  }

  /** Shuts down the channel used to connect to the server. */
  public void shutdown() throws InterruptedException {
    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Parses the host and port from {@code args}, sends a gRPC request to the server whose address is
   * "host:port", and prints the response from the server.
   */
  public static void main(String[] args) throws InterruptedException {
    if (args.length != 2) {
      System.out.println("usage: com.sample.client.GrpcClient [host] [port]");
      System.exit(1);
    }

    String host = args[0];
    int port = Integer.parseInt(args[1]);

    GrpcClient client = new GrpcClient(host, port);

    try {
      System.out.println("Sending a request to " + host + ":" + port + "...");
      MessageContext rpcResponse = client.processMessage();
      System.out.println(rpcResponse);
    } finally {
      client.shutdown();
    }
  }
}
