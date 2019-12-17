package com.facilio;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MqttTest
{


    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
// "guest"/"guest" by default, limited to localhost connections
        factory.setUsername("arulmain");
        factory.setPassword("pass");
        factory.setVirtualHost("/");
        factory.setHost("192.168.1.31");
        factory.setPort(5672);
        System.out.println(" connecting ");
        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare("amq.topic", "direct", true);
            String queueName = channel.queueDeclare().getQueue();
            System.out.println(" queue name "+queueName);
        }catch (Exception e){
            System.out.println(" Exception ");
            System.out.println(e);
        }}
}
