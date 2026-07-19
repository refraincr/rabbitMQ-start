package com.rabbitmq.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
// import com.rabbitmq.client.MessageProperties;

public class ExToEx {
    public static void main(String[] args)
            throws IOException, InterruptedException, TimeoutException, KeyManagementException,
            NoSuchAlgorithmException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        // factory.setUri("amqp://userName:password@ipAddress tportNumber/virtualHost")
        factory.setUri("amqp://root:root123@localhost:5672/%2F");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("source", "direct", false, true, null);
        channel.exchangeDeclare("destination", "fanout", false, true, null);
        channel.exchangeBind("destination", "source", "exToEx");
        channel.queueDeclare("queue", false, false, true, null);
        channel.queueBind("queue", "destination", "");
        channel.basicPublish("source", "exToEx", null, "exToExDDDD".getBytes());

        // 关闭资源
        channel.close();
        connection.close();
    }
}
