package com.aitongyi.rabbit.routing;


import com.rabbitmq.client.*;

/**
 * Routing：按照某条路线发送，路由的意思。按照交换机的某条路线发送

消息路由：可以理解生产者向所有标签的路由通道发送消息，但是，消费者可以订阅某一个通道，而其他的消息，消费者则不管
        上一章教程中我们建立了一个简单的日志记录系统，能够将消息广播到多个消费者。本章，
        我们将添加一个新功能，类似订阅消息的子集。例如：我们只接收日志文件中ERROR类型的日志。
       channel.queueBind(queueName, EXCHANGE_NAME, "black");  //队列绑定路由的某条线
       绑定关系中使用的路由关键字【routingkey】是否有效取决于交换器的类型。
       如果交换器是分发【fanout】类型，就会忽略路由关键字【routingkey】的作用。
 * routing:按照某条路线发送，路由的意思
 * @author Administrator
 *
 */
public class RoutingSendDirect {

	//声明交换机的名字
	private static String EXCHANGE_NAME="direct_logs";
	//声明路由线路
	private static String[] logs= new String[]{"info","error","warning"};
	
	public static void main(String[] args) throws Exception {
		
		//创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		factory.setHost("127.0.0.1");
		//创建连接
		Connection connection = factory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		//声明交换机，和交换机的类型
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		//根据路由线路分发消息
		for(String str :logs){
			String message="log---------->"+str;
			channel.basicPublish(EXCHANGE_NAME, str, null, message.getBytes("utf-8"));
			System.out.println("P:"+message);
		}
		//关闭频道和连接
		channel.close();
		connection.close();
	}
}
