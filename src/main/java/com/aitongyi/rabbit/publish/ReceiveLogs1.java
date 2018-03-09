package com.aitongyi.rabbit.publish;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 接受信息日志1
 * @author Administrator
 *
 */
public class ReceiveLogs1 {
	
	//声明交换机的名字
	private static String EXCHANGE_NAME="logs";
	
	public static void main(String[] args) throws Exception {
		
		//创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		factory.setHost("127.0.0.1");
		//创建连接会话
		Connection connection = factory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		
		//声明交换机
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		
		//声明一个临时的队列,返回临时队列的名字,这个临时队列会在当前消费者断开连接时，自动删掉
		String queueName = channel.queueDeclare().getQueue();
		
		//队列绑定交换机，交换机的信息会分发给队列
		//参数一：队列名，交换机名字,路由的key,队列与交换机绑定，此时不需要路由的key
		channel.queueBind(queueName,EXCHANGE_NAME, "");
		
		//定义消费者
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body) throws IOException {
					
					String message=new String(body,"utf-8");
					System.out.println("LOG1: "+message);
			}
		};
		//消费者监听队列,设置自动ack
		channel.basicConsume(queueName, true, consumer);
	}
	
}
