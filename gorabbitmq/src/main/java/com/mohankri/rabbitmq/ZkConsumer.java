package com.mohankri.rabbitmq;

import java.util.List;

import org.apache.helix.api.id.StateModelDefId;
import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.participant.StateMachineEngine;

public class ZkConsumer 
{
    private final String _zkAddr;
    private final String _clusterName;
    private final String _consumerId;
    private final String _rqServer;
    private HelixManager _manager = null;

    public ZkConsumer(String zkAddr, String clusterName, String consumerId,
                      String rqServer)
    {
      _zkAddr = zkAddr;
      _clusterName = clusterName;
      _consumerId = consumerId;
      _rqServer = rqServer;
    }
    
    public void connect()
    {
      try 
      {
        _manager = HelixManagerFactory.getZKHelixManager(_clusterName,
                                                       _consumerId,
                                                       InstanceType.PARTICIPANT,
                                                       _zkAddr);
        StateMachineEngine stateMachine = _manager.getStateMachineEngine();
        ZkStateModelFactory modelFactory = 
                        new ZkStateModelFactory(_consumerId, _rqServer);
        stateMachine.registerStateModelFactory(StateModelDefId.from(ZkSetup.DEFAULT_STATE_MODEL), modelFactory);

        _manager.connect();
        Thread.currentThread().join();
    }
    catch (InterruptedException e) 
    {
      System.out.println("[-]" + _consumerId + " is interrupted ..");
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    finally
    {
      disconnect();
    }
  }
   
  public void disconnect()
  {
  }

  public static void main(String[] args) throws Exception {
      final String clusterName = ZkSetup.DEFAULT_CLUSTER_NAME;
      final String zkAddr = args[0];
      final String consumerId = args[1];
      final String rqServer = args[2];

      ZkClient zk = null; 
      System.out.println("Consumer: " + clusterName + " " + zkAddr + " " + rqServer);        
      try
      {
          zk = new ZkClient(zkAddr, ZkClient.DEFAULT_SESSION_TIMEOUT,
                   ZkClient.DEFAULT_CONNECTION_TIMEOUT,
                   new ZNRecordSerializer());
          ZKHelixAdmin admin = new ZKHelixAdmin(zk);
          List<String> nodes = admin.getInstancesInCluster(clusterName);
          if (!nodes.contains("consumer_" + consumerId)) {
            InstanceConfig config = new InstanceConfig("consumer_" + consumerId);
            config.setHostName("localhost");
            config.setInstanceEnabled(true);
            admin.addInstance(clusterName, config);
            System.out.println("Consumer not found");
          }
            
          final ZkConsumer zkconsumer = new ZkConsumer(zkAddr, clusterName, "consumer_" + consumerId, rqServer);
          Runtime.getRuntime().addShutdownHook(new Thread() 
          {
            @Override
            public void run()
            {   
              System.out.println("Shutting Down consumer_" + consumerId);
              zkconsumer.disconnect();
            }
          });
          zkconsumer.connect();
    }
    finally
    {
      if (zk != null) 
      {
        zk.close();
      }
    }
  }
}
