package com.rabbitmq.app;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.*;

public class RabbitComsumer {
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 5672;
    
    public static void main(String[] args) 
    throws IOException,InterruptedException,TimeoutException {
        Address[] addresses = new Address[] {
            new Address(IP_ADDRESS,PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root123");
        // 创建连接
        Connection connection = factory.newConnection(addresses);
        // 创建信道
        Channel channel = connection.createChannel();
        // 创建队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 设置客户端最多接受未被ack的个数
        channel.basicQos(64);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                Envelope envelope,AMQP.BasicProperties properties,
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
        channel.basicConsume(QUEUE_NAME, consumer);
        // 回调函数执行完毕后执行资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }
}
