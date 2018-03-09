package com.aitongyi.rabbit.queues;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者2
 * @author Administrator
 *
 */
public class Worker2 {

	//队列名字
	private static String TASK_QUEUE_NAME="task_queue";
	
	public static void main(String[] args) throws Exception {
		
		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		connectionFactory.setHost("127.0.0.1");
		//创建与rabbit的新的连接会话
		Connection connection = connectionFactory.newConnection();
		//创建频道
		final Channel channel = connection.createChannel();
		//声明队列
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		//设置每次给消费者发送的消息条数，如果消费者有n个消息还没有ack，那么会阻塞
		channel.basicQos(1);//可以达到负载均衡的效果
		
		//声明消费者
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body) throws IOException {
				
				System.out.println("Work2"+new String(body,"utf-8"));
				
				try {
					Thread.sleep(1000);//当前线程休眠1秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//ack反馈，否则，消息会重新会到队列
				System.out.println(envelope.getDeliveryTag()+"这个是什么呢？");//消费者接收到的总的消息数量
				channel.basicAck(envelope.getDeliveryTag(), false);//第二个参数代表是否批量
			}
		};
		
		//消费者监听队列,设置手动ack
		//参数：第一个代表：队列的名字，第二个代表手动ack,consumer:回调的消费者
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
	}
}
