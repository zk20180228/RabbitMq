package com.aitongyi.rabbit.queues;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 *   工作队列模型（即任务队列，一个队列，多个消费者，生产者直接把消息发送给队列）
	  的主要思想是不用一直等待资源密集型的任务处理完成，这就像一个生产线，将半成品放到生产线中，然后在生产线后面安排多个工人同时对半成品进行处理，
	 这样比一个生产线对应一个工人的吞吐量大几个数量级。  
	 一个生产者产生消息，多个消费者处理消息，一个消息只能被一个消费者消费
	                    ------>c1
	 p---->queue------>c2          消息会被轮询分发
	                     ------>c3 
 * 
 * 
 * 工厂队列模型，一个生产者，多个消费者，消息轮询发送给消费者
 * 一条消息只能给一个消费者
 * @author Administrator
	 * 
	 * 注意：   如果处理一条消息需要几秒钟的时间，你可能会想，如果在处理消息的过程中，消费者服务器、
	 网络、网卡出现故障挂了，那可能这条正在处理的消息或者任务就没有完成，就会失去这个消息和任务。
	为了确保消息或者任务不会丢失，RabbitMQ支持消息确认–ACK。ACK机制是消费者端从RabbitMQ收到消息并处理完成后，
	反馈给RabbitMQ，RabbitMQ收到反馈后才将此消息从队列中删除。如果一个消费者在处理消息时挂掉（网络不稳定、服务器异常、
	网站故障等原因导致频道、连接关闭或者TCP连接丢失等），那么他就不会有ACK反馈，RabbitMQ会认为这个消息没有正常消费，
	会将此消息重新放入队列中。如果有其他消费者同时在线，RabbitMQ会立即将这个消息推送给这个在线的消费者。
	这种机制保证了在消费者服务器故障的时候，能不丢失任何消息和任务。
	如果RabbitMQ向消费者发送消息时，消费者服务器挂了，消息也不会有超时；
	即使一个消息需要非常长的时间处理，也不会导致消息超时。这样消息永远不会从RabbitMQ服务器中删除。
	只有当消费者正确的发送ACK确认反馈，RabbitMQ确认收到后，消息才会从RabbitMQ服务器的数据中删除。                 
	  
	消息的持久化：channel.queueDeclare("hello", true, false, false, null);//声明队列时，持久化                   
	                     
	                     channel.basicPublish("", "hello", MessageProperties.PERSISTENT_TEXT_PLAIN,  message.getBytes());//发布消息时持久化
	                     
	注意：标记为持久化后的消息也不能完全保证不会丢失。虽然已经告诉RabbitMQ消息要保存到磁盘上，但是理论上，RabbitMQ已经接收到生产者的消息，
	        但是还没有来得及保存到磁盘上，服务器就挂了（比如机房断电），那么重启后，RabbitMQ中的这条未及时保存的消息就会丢失。
	        因为RabbitMQ不做实时立即的磁盘同步（fsync）。这种情况下，
	        对于持久化要求不是特别高的简单任务队列来说，还是可以满足的。如果需要更强大的保证，那么你可以考虑使用生产者确认反馈机制。
	                     
	负载均衡：
	                     默认情况下，RabbitMQ将队列消息随机分配给每个消费者，这时可能出现消息调度不均衡的问题。
	                     例如有两台消费者服务器，一个服务器可能非常繁忙，消息不断，另外一个却很悠闲，没有什么负载。
	                     RabbitMQ不会主动介入这些情况，还是会随机调度消息到每台服务器。
	                    这是因为RabbitMQ此时只负责调度消息，不会根据ACK的反馈机制来分析那台服务器返回反馈慢，是不是处理不过来啊？
	                    
	解决方案：我们可以使用【prefetchcount = 1】这个设置。这个设置告诉RabbitMQ，不要一次将多个消息发送给一个消费者。
	                  这样做的好处是只有当消费者处理完成当前消息并反馈后，才会收到另外一条消息或任务。这样就避免了负载不均衡的事情了。                    
	                 prefetchCount：会告诉RabbitMQ不要同时给一个消费者推送多于N个消息，即一旦有N个消息还没有ack，
	                 则该consumer将block掉，直到有消息ack 。    
	                     
	能者多劳： 我们可以设置当前线程的休眠时间，来达到能者多劳的效果               
	注意：       如果所有的消费者负载都很高，你的队列很可能会被塞满。这时你需要增加更多的消费者或者其他方案。  
 */

public class NewTask {
	
	//队列名字
	private static String TASK_QUEUE_NAME="task_queue";
	
	public static void main(String[] args) throws Exception {
		
		//创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		//设置rabbitmq的服务地址
		connectionFactory.setHost("127.0.0.1");
		//创建与rabbitmq的连接会话
		Connection connection = connectionFactory.newConnection();
		//创建频道
		Channel channel = connection.createChannel();
		//通过频道声明队列
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		//分发消息
		for(int i =0;i<5;i++){
			String message="hello word "+i;
			//发送消息到队列,设置持久化
			channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
			System.out.println("P:"+message);
		}
		//关闭频道和会话连接
		channel.close();
		connection.close();
	}
	
}
