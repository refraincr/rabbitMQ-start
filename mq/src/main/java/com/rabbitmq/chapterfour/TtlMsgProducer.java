package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TtlMsgProducer {
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
        channel.exchangeDeclare("test", "direct",false,true,null);
        
        Builder builder = new Builder();
        builder.deliveryMode(2);  // 持久化消息
        builder.expiration("60000");    // 设定1分钟的过期时间
        AMQP.BasicProperties properties = builder.build();

        // 发布消息
        channel.basicPublish("test",
            "", properties, "ttl test".getBytes());
    }
}
