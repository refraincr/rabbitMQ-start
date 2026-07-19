package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TtlQueueProducer {
    public static void main(String[] args)
    throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String,Object> argus = new HashMap<>();
        // 为队列设置过期时间
        argus.put("x-expires",1800000);
        channel.queueDeclare("ttlQueue1",false,false,true,argus);

        channel.close();
        connection.close();
    }
}
