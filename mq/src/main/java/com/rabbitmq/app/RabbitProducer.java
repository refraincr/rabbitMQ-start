package com.rabbitmq.app;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class RabbitProducer {
    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routinkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 5672;

    public static void main(String[] args) 
    throws IOException,InterruptedException,TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("root123");
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel(); 
        // 创建一个持久化的非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct",true,false,null);
        // 创建一个持久化的非排他的非自动删除队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 将交换器和队列通过路由键绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);
        // 发送一条持久化的消息
        String message = "Hello World";
        channel.basicPublish(
            EXCHANGE_NAME,
            ROUTING_KEY,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes()
        );
        // 关闭资源
        channel.close();
        connection.close();
    }
}
