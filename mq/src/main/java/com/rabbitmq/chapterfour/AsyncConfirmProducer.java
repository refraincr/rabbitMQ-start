package com.rabbitmq.chapterfour;

import java.util.SortedSet;
import java.util.TreeSet;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class AsyncConfirmProducer {
    private static SortedSet<Long> confirmSet = new TreeSet<>();
    public static void main(String[] args)
    throws IOException,TimeoutException,InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.confirmSelect();    // 开启生产者确认机制
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Nack, SeqNo: "+deliveryTag+", multiple: "+multiple);
                if (multiple) {
                    confirmSet.headSet(deliveryTag - 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag - 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
                // 消息重发场景...
            }
        });
    }
}
