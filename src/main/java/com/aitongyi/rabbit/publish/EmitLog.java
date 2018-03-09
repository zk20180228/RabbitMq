package com.aitongyi.rabbit.publish;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
	 * publish/subscrible发布订阅模型：（一个生产者，一个交换机，多个队列，多个消费者，每个消费者都占一个队列）
	同一个任务消息发送给多个工人。这种模式就是“发布/订阅”。
	我们将以一个日志系统进行讲解：一个日志发送者，两个日志接收者，接收者1可以把这条日志写入到磁盘上，另外一个接收者2可以将这条日志打印到控制台中。
	发布订阅模式的基础是将所有的消息广播到交换机上
	
	              -------->queue1------>C1
	P---->X
	               -------->queue2------->C2
	               
	解读：
	1、1个生产者，多个消费者
	2、每一个消费者都有自己的一个队列
	3、生产者没有将消息直接发送到队列，而是发送到了交换机
	4、每个队列都要绑定到交换机
	5、生产者发送的消息，经过交换机，到达多个队列，实现，一个消息被多个消费者获取的目的  
	
	注意：交换机没有存储消息的能力，因此，当把消息发送给一个没有绑定队列的交换机时，消息将会丢失
	
	RabbitMQ中消息传递模型的核心思想是：生产者不直接发送消息到队列。
	实际的运行环境中，生产者是不知道消息会发送到那个队列上，她只会将消息发送到一个交换器，
	交换器也像一个生产线，她一边接收生产者发来的消息，另外一边则根据交换规则，将消息放到队列中。
	交换器必须知道她所接收的消息是什么？它应该被放到那个队列中？它应该被添加到多个队列吗？还是应该丢弃？这些规则都是按照交换器的规则来确定的。
	交换器的规则有：
	    direct （直连）
	    topic （主题）
	    headers （标题）
	    fanout （分发）也有翻译为扇出的。
	channel.exchangeDeclare("logs", "fanout");//参数一：交换器的名字，参数二：交换器的规则
	   
	 来看看我们之前的代码：
	channel.basicPublish("", "hello", null, message.getBytes());
	    第一个参数就是交换器的名称。如果输入“”空字符串，表示使用默认的匿名交换器。
	    第二个参数是【routingKey】路由线索
	    匿名交换器规则：
	    发送到routingKey名称对应的队列。
	
	//现在我们就可以就将我们的队列跟交换器进行绑定。执行完这段代码后,交换器会将消息添加到我们的队列中。
	channel.queueBind(queueName, "logs", "");//队列绑定交换机
	
	临时队列:
	    首先，每当我们连接到RabbitMQ，我们需要一个新的空队列，我们可以用一个随机名称来创建，
	    或者说让服务器选择一个随机队列名称给我们。
	    一旦我们断开消费者，队列应该立即被删除。
	    在Java客户端，提供queuedeclare()为我们创建一个非持久化、独立、自动删除的队列名称。
	    String queueName = channel.queueDeclare().getQueue();
	 * 发布订阅模型：
	 * 发出信息日志
	 * @author Administrator
	 *生产者生产日志---->交换机---->发送给其绑定的队列--->消费者监听队列
 */

public class EmitLog {
	
	//交换机名字
	private static String EXCHANGE_NAME="logs";
	
	public static void main(String[] args) throws Exception {
		
		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		connectionFactory.setHost("127.0.0.1");
		//创建与rabbitmq的连接会话
		Connection connection = connectionFactory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		
		//声明交换器,第二个参数为交换机的规则,有四种规则：direct(直连),topic(主题),headers(标题),fanout(分发)
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");//fan-out
		
		//分发消息到交换器上
		for(int i=0;i<5;i++){
			String message="hello word "+i;
			//发送消息到交换机上
			//第二个参数：routing(路由)的key，当交换机为默认交换机("")时，生产者会把消息发送给和这个key一样名字的队列
			//有了交换机后，消息发送给交换机，不再发送给队列了
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("utf-8"));
			System.out.println("P:"+message);
		}
		//关闭频道连接和会话连接
		channel.close();
		connection.close();
	}
}
