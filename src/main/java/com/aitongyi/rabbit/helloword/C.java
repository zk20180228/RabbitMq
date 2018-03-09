package com.aitongyi.rabbit.helloword;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**简单队列：一个消费者，一个生产者
 * 消息消费者   
 * @author Administrator
 *先运行，消息消费者，来关注这个队列，在运行生产者，发送消息到队列，
 *再切换到消息消费者控制台查看接收到消息
 */
public class C {
	
	public static String QUEUE_NAME="hello";//队列名，需要频道来生明	
	
	public static void main(String[] args) throws Exception {
		
		 // 创建连接工厂 
		 ConnectionFactory connectionFactory = new ConnectionFactory();
		 //设置rabbitMq的服务地址
		 connectionFactory.setHost("127.0.0.1");
		 //还可以设置账号，密码
		 //创建连接
		 Connection connection = connectionFactory.newConnection();
		 //创建频道
		 Channel channel = connection.createChannel();
		 //通过频道声明队列--->队列的声明是幂等性的（即不存在时创建队列，存在时不影响已存在的队列）
		 channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		 //通过频道定义消费者
		 Consumer consumer=new DefaultConsumer(channel){
			 @Override
			public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body) throws IOException {
				String messgae= new String(body,"utf-8");
				System.out.println(messgae);
				System.out.println("监听时。。。。。。。");
			}
		 };
		 
		 System.out.println("监听之前。。。。。。");
		 //消费者监听队列，设置自动ack
		 channel.basicConsume(QUEUE_NAME, true,consumer);//true，自动ack，false手动ack
		 System.out.println("监听后，ack之后。。。。。。。");
		
	}
	

}
