package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class ManyConfirmProducer {
    public static void main(String[] args)
    throws IOException,TimeoutException,InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("exchange", "direct");

        int BATCH_COUNT = 30;
        // 批量confirm
        try {
            channel.confirmSelect();
            int msgCount = 0;
            while (true) {
                channel.basicPublish(
                    "exchange",
                    "",
                    null,
                    "pip test".getBytes()
                );
                if (++msgCount>=BATCH_COUNT) {
                    msgCount = 0;
                    try {
                        if (channel.waitForConfirms()) {}
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
