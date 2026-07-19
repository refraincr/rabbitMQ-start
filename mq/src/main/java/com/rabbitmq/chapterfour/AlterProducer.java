package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class AlterProducer {
    public static void main(String[] args)
    throws IOException, TimeoutException, InterruptedException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String,Object> argus = new HashMap<>();
        argus.put("alternate-exchange","ae");
        // 交换器
        channel.exchangeDeclare("normalExchange",
            "direct",
            false,
            true,
            argus
        );
        channel.exchangeDeclare("ae", "fanout",false,true,null);

        // 队列
        channel.queueDeclare("normalQueue",
        false,
        false,
        true,
        null);
        channel.queueDeclare("aeQueue",
            false,
            false,
            true,
            null
        );

        // 绑定键
        channel.queueBind("normalQueue", "normalExchange", "normalKey=");
        channel.queueBind("aeQueue", "ae", "");

        // mandatory 不能和备份交换器一起使用
        channel.basicPublish("normalExchange",
            "",
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            "mandatory test".getBytes()
        );
        
        channel.close();
        connection.close();
    }
}
