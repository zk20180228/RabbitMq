package com.aitongyi.rabbit.helloword;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 消息生产者
 * @author Administrator
 *
 */
public class P {
	
	private static String QUEUE_NAME="hello";//队列的名字
	
	public static void main(String[] args) throws Exception{
		
		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		connectionFactory.setHost("127.0.0.1");
		//还可以设置账号，密码
		//创建新的连接会话
		Connection connection = connectionFactory.newConnection();
		//通过频道声明队列--->队列的声明是幂等性的（即不存在时创建队列，存在时不影响已存在的队列）
		Channel channel = connection.createChannel();
		//通过频道声明队列--->队列的声明是幂等性的（即不存在时创建队列，存在时不影响已存在的队列）
		//参数分别是：队列名字,是否持久化，当前队列是否独占此连接，但没有消费者时是否删除该队列，Map<String,Object>其他参数
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		//要发送的消息
		String message="helloWord！";
		//通过频道发布消息到队列中
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes("utf-8"));
		
		System.out.println("P-->Send:"+message);
		//关闭频道和连接
		channel.close();
		connection.close();
	}
	

}
