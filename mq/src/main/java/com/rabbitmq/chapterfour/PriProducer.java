package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;

public class PriProducer {
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
        argus.put("x-max-priority",10);  // 设定优先级

        channel.queueDeclare("queue.pri",false,false,true,argus);

        Builder builder = new Builder();
        builder.priority(5);  // 设定消息优先级
        AMQP.BasicProperties properties = builder.build();
        channel.basicPublish("",
            "",
            properties,
            "message".getBytes()
        );

        channel.close();
        connection.close();
    }
}
