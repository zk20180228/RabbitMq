package com.aitongyi.rabbit.routing;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者1，扶着接受error级别的日志信息
 * @author Administrator
 *
 */
public class ReceiveLogsDirect1 {
	
	//声明交换机的名字
	private static String EXCHANGE_NAME="direct_logs";
	
	//声明路由线路名---->只绑定error线路
	private static String[] logs=new String[]{"error"};
	
	public static void main(String[] args) throws Exception {
		//创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		factory.setHost("127.0.0.1");
		//创建连接
		Connection connection = factory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		//声明交换机和交换机的名字
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		//声明队列，返回队列的名字
		String queueName = channel.queueDeclare().getQueue();
		//队列绑定交换机各个路由,就会收到各个路由的消息了
		for(String str :logs){
			channel.queueBind(queueName, EXCHANGE_NAME, str);//指定绑定的路由路线
		}
		//定义消费者
		Consumer consumer = new DefaultConsumer(channel){
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				System.out.println("C1---------->"+new String(body,"utf-8"));
			}
		};
		//消费者监听队列，消费消息，设置自动ack
		channel.basicConsume(queueName, true, consumer);
	}
	
}
