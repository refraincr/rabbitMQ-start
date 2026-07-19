package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class DLXProducer {
    public static void main(String[] args)
    throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        // 声明交换器
        // 死信交换器
        channel.exchangeDeclare("exchange.dlx",
            "direct",
            false,
            true,
            null
        );
        // 普通交换器
        channel.exchangeDeclare("exchange.normal",
            "fanout",
            false,
            true,
            null
        );

        // 参数设定
        Map<String,Object> argus = new HashMap<>();
        argus.put("x-message-ttl",10000);
        argus.put("x-dead-letter-exchange","exchange.dlx");
        argus.put("x-dead-letter-routing-key","routingkey");

        // 死信队列
        channel.queueDeclare("queue.normal",false,false,true,argus);
        // 普通交换器
        channel.queueDeclare(
            "queue.dlx",
            false,
            false,
            true,
            null    
        );

        // 绑定
        channel.queueBind("queue.dlx", "exchange.dlx", "routingkey");
        channel.queueBind("queue.normal", "exchange.normal", "");

        channel.basicPublish("exchange.normal", "rk",
         MessageProperties.PERSISTENT_TEXT_PLAIN, "dlx".getBytes());

        channel.close();
        connection.close();
    }
}