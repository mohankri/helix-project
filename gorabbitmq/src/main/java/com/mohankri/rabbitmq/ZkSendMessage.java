package com.mohankri.rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ZkSendMessage
{
  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] args) throws Exception
  {
    if (args.length < 1) 
    {
      System.err.println("USAGE: Java SendMessage rabitMq\n");
      System.exit(1);
    }
    final String rqServer = args[0];
    int count = Integer.MAX_VALUE;
    if (args.length > 1) {
       try 
       {
         count = Integer.parseInt(args[1]);
       } catch (Exception e) {
       }
    }
    System.out.println("Sending " + count + " message ");
       ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(rqServer);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");

    for (int i = 0; i < count; i++)
    {
      int rand = ((int) (Math.random() * 10000) % ZkSetup.DEFAULT_PARTITION_NUMBER);
      String routingKey = "topic_" + rand;
      String message = "message_" + rand;

      channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
      System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

      Thread.sleep(1000);
    }

    connection.close();
  }
}
