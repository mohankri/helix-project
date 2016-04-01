package com.mohankri.replication;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.participant.StateMachineEngine;
import org.apache.helix.api.id.StateModelDefId;

public class DataStore {
  private final String _zkAddr;
  private final String _clusterName;
  private final String _myServer;

  private HelixManager _manager = null;

  public DataStore(String zkAddr, String clusterName, String myServer)
  {
    _zkAddr = zkAddr;
    _clusterName = clusterName;
    _myServer = myServer;
  }
  
  public void connect()
  {
    System.out.println("connect DataStore\n");
    System.out.println("zkAddr " + _zkAddr);
    System.out.println("cluster_name " + _clusterName);
    System.out.println("myServer " + _myServer);
    try {
      _manager = HelixManagerFactory.getZKHelixManager(
               _clusterName,
               _myServer, 
               InstanceType.PARTICIPANT, _zkAddr);

       /* Get StateMachine */
       StateMachineEngine stateMachine = _manager.getStateMachineEngine();
      
       /* Create Model */
       DataStoreModelFactory modelFactory = new DataStoreModelFactory(_manager);
      
       /* Connect Model to the StateMachine */ 
       //stateMachine.(
       stateMachine.registerStateModelFactory(
             StateModelDefId.from(SetupCluster.STATE_MODEL), modelFactory);
        
      _manager.connect();
      Thread.currentThread().join();
    } catch (Exception e) {
      e.printStackTrace(); 
      disconnect();
    }
  }

  public void disconnect()
  {
    System.out.println("disconnect DataStore\n");
    _manager.disconnect();
  }
}
