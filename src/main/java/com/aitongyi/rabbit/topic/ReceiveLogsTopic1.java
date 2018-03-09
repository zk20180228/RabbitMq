package com.aitongyi.rabbit.topic;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 匹配*.orange.*
 * @author Administrator
 *
 */
public class ReceiveLogsTopic1 {

	//声明交换机的名字
	private static String EXCHANGE_NAME="topic_logs";
	//声明路由路线
	private static String[] routingKeys=new String[]{"*.orange.*"};
	
	public static void main(String[] args) throws Exception {

		//创建连接的工厂
		ConnectionFactory factory = new ConnectionFactory();
		//设置服务地址
		factory.setHost("127.0.0.1");
		//新建连接
		Connection connection = factory.newConnection();
		//新建频道
		Channel channel = connection.createChannel();
		//声明交换机，和交换机的类型
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		//新建临时队列
		String queueName = channel.queueDeclare().getQueue();

		//队列绑定交换机的路由线路
		for(String routingKey:routingKeys){
			channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
		}
		//声明消费者
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
					System.out.println("C1---------------------->"+new String(body,"utf-8"));
			}
		};

		//消费者监听队列，设置自动ack
		channel.basicConsume(queueName, true, consumer);
	}
}
