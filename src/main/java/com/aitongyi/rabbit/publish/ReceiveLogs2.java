package com.aitongyi.rabbit.publish;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 接受信息日志2
 * @author Administrator
 *
 */
public class ReceiveLogs2 {
	
	private static String EXCHANGE_NAME="logs";
	public static void main(String[] args) throws Exception {

		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置连接url，账号，密码
		connectionFactory.setHost("127.0.0.1");
		//新建连接
		Connection connection = connectionFactory.newConnection();
		//声明频道
		Channel channel = connection.createChannel();
		//声明交换机
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		//创建临时队列
		String queue = channel.queueDeclare().getQueue();
		//队列绑定交换机
		channel.queueBind(queue, EXCHANGE_NAME, "");
		//定义消费者
		DefaultConsumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
					System.out.println(new String(body,"utf-8"));
			}
		};
		//消费者监听队列，自动ack
		channel.basicConsume(queue, true, consumer);
	}
}
