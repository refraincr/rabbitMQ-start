package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;

public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public static void main(String[] args)
    throws IOException,TimeoutException,InterruptedException {
        Address[] addresses = new Address[] {
            new Address("localhost",5672)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection(addresses);
        Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME,false,false,true,null);
        channel.basicQos(1);
        System.out.println("    [x] Awating RPC requests");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            ) throws IOException {
                // 取得发送过来的请求id
                AMQP.BasicProperties props = new AMQP.BasicProperties()
                        .builder()
                        .correlationId(properties.getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(body,"UTF-8");
                    int n = Integer.parseInt(message);
                    System.out.println("[.] fib("+message+")");
                    response += fib(n);
                } catch (RuntimeException e) {
                    System.out.println(" [.]"+e.toString());
                } finally {
                    channel.basicPublish("",
                        properties.getReplyTo(),
                        props,
                        response.getBytes("UTF-8")
                    );

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(RPC_QUEUE_NAME,false, consumer);
    }

    private static int fib(int n) {
        if (n==0) return 0;
        if (n==1) return 1;
        return fib(n-2) + fib(n-1);
    }
}
