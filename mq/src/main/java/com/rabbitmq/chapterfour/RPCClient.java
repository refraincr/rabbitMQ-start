package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RPCClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueeuingConsumer consumer;
    
    public RPCClient() throws IOException,TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        connection = factory.newConnection();
        channel = connection.createChannel();


        replyQueueName = channel.queueDeclare().getQueue();
        // 消费
        consumer = new QueeuingConsumer(channel);
        channel.basicConsume(replyQueueName,true, consumer);
    }

    public String call(String message)
    throws IOException,ShutdownSignalException,
    ConsumerCancelledException,InterruptedException {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties().builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        
        channel.basicPublish("", requestQueueName, props, message.getBytes());

        // 匹配id
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody());
                break
            }
        }

        return response;
    }

    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        RPCClient fibRpc = new RPCClient();
        System.out.println(" [x] Requesting fib(30)");
        String response = fibRpc.call("30");
        System.out.println(" [.] Got '"+response+"'");
        
    }
}
