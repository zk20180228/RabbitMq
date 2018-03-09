package com.aitongyi.rabbit.routing;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ReceiveLogsDirect2 {

	//声明交换机的名字
	private static String EXCHANGE_NAME="direct_logs";
	//声明路由线路
	private static String[] logs=new String[]{"info","error","warning"};
	
	public static void main(String[] args) throws Exception {

		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置服务地址
		connectionFactory.setHost("127.0.0.1");
		//新建连接
		Connection connection = connectionFactory.newConnection();
		//新建频道
		Channel channel = connection.createChannel();
		//声明交换机和交换机的类型
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		//创建临时队列
		String queueName = channel.queueDeclare().getQueue();
		//队列绑定交换机，绑定路由线路
		for(String str:logs){
			channel.queueBind(queueName, EXCHANGE_NAME, str);
		}

		//声明消费者
		DefaultConsumer consumer = new DefaultConsumer(channel){
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						BasicProperties properties, byte[] body) throws IOException {
					System.out.println("C2-------------->"+new String(body,"utf-8"));
				}
				
		};

		//消费者监听队列，设置自动ack
		channel.basicConsume(queueName, true, consumer);
	}
}
