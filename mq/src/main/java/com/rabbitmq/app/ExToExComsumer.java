package com.rabbitmq.app;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;


public class ExToExComsumer {
    public static void main(String[] args)
    throws IOException,InterruptedException,TimeoutException {
        Address[] addresses = new Address[] {
            new Address("localhost",5672)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection conn = factory.newConnection(addresses);
        Channel channel = conn.createChannel();

        channel.basicQos(64);
        channel.queueDeclare("queue", false, false, true, null);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                Envelope envelope,AMQP.BasicProperties properties,
                byte[] body
            ) throws IOException {
                System.out.println("recv message: "+new String(body));
            }
        };

        channel.basicConsume("queue",true, consumer);

        TimeUnit.SECONDS.sleep(5);

        channel.close();
        conn.close();
    }
}
