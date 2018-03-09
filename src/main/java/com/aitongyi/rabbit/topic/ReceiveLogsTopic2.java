package com.aitongyi.rabbit.topic;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 *  匹配 "*.*.rabbit", "lazy.#"
 * @author Administrator
 *
 */
public class ReceiveLogsTopic2 {

	private static String EXCHANGE_NAME="topic_logs";
	private static String[] routingKeys=new String[]{"*.*.rabbit", "lazy.#"};
	
	public static void main(String[] args) throws Exception, Exception {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		
		for(String routingKey:routingKeys){
			channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
		}
		
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				System.out.println("C2--------------->"+new String(body,"utf-8"));	
			}
		};
		
		channel.basicConsume(queueName, true, consumer);
		
	}
}
