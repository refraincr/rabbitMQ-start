package com.rabbitmq.chapterfour;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;

public class AlterConsumer {
    public static void main(String[] args)
    throws IOException,TimeoutException,InterruptedException {
        Address[] addresses = new Address[] {
            new Address("localhost",5672)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection(addresses);
        Channel channel = connection.createChannel();

        channel.queueDeclare("aeQueue",false,false,true,null);

        channel.basicQos(64);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            ) throws IOException {
                System.out.println("recv message: "+new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume("aeQueue", consumer);

        // 等待5秒
        TimeUnit.SECONDS.sleep(5);

        channel.close();
        connection.close();
    }
}
