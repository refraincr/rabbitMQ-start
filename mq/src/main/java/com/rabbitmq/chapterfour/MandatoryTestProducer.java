package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;

public class MandatoryTestProducer {
    public static void main(String[] args)
    throws IOException, TimeoutException, InterruptedException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 交换器
        channel.exchangeDeclare("ex",
            "direct",
            false,
            true,
            null
        );

        // 队列
        channel.queueDeclare("qu",
        false,
        false,
        true,
        null);

        // 监听消息回收
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode,String replyText,
                String exchange,String routingKey,
                AMQP.BasicProperties basicProperties,
                byte[] body
            ) throws IOException {
                String message = new String(body);
                System.out.println("Bsasic.Return 返回的结果是： "+message);
            }
        });

        // mandatory 设为 true
        channel.basicPublish("ex",
            "",
            true,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            "mandatory test".getBytes()
        );
        
        channel.close();
        connection.close();
    }
}