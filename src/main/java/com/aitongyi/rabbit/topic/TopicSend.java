package com.aitongyi.rabbit.topic;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Topic：匹配模式。队列绑定交换机路由，消费者监听队列。队列可以进行多重绑定，
    即一个队列可以绑定多个路由，这样消费者接收到的信息种类就多了，因为来自不同的路由
    消费者可以绑定模糊匹配的路由，进行接收消息，如Q1绑定关键字是【*.orange.*】,Q2绑定关键字是【*.*.rabbit】和【lazy.#】
    *（星号）表示一个单词
    #（井号）表示零个或者多个单词
   
    注意：
    模糊匹配是对于消费者来说的，消费者可以对路由进行模糊匹配绑定，但是路由对交换机来说是精准的
    如果消费者端的路由关键字只使用【#】来匹配消息，在匹配【topic】模式下，它会变成一个分发【fanout】模式，接收所有消息。
    如果消费者端的路由关键字中没有【#】或者【*】，它就变成直连【direct】模式来工作。
 * @author Administrator
 *
 */

public class TopicSend {
	
	//声明交换机的名字
	private static String EXCHANGE_NAME="topic_logs";
	//声明路由名字
	private static String[] routingKeys = new String[]{"quick.orange.rabbit",   
        "lazy.orange.elephant",   
        "quick.orange.fox",   
        "lazy.brown.fox",   
        "quick.brown.fox",   
        "quick.orange.male.rabbit",   
        "lazy.orange.male.rabbit"}; 
	
	public static void main(String[] args) throws Exception {
		//创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		//设置rabbitmq的地址
		factory.setHost("127.0.0.1");
		//创建连接
		Connection connection = factory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		//声明交换机，交换机的类型
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		//分别向不同的路由分发消息
		for(String routingKey:routingKeys){
			String message="--------------------->"+routingKey;
			System.out.println(message);
			channel.basicPublish(EXCHANGE_NAME, routingKey, null,message.getBytes("utf-8"));
		}
		//关闭连接
		channel.close();
		connection.close();
	}
}
