package com.mohankri.replication;

import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.model.StateModelDefinition;
import org.apache.helix.tools.StateModelConfigGenerator;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.IdealState.RebalanceMode;

public class SetupCluster {
  public static final String ZKADDRESS = "localhost:2199";   
  public static final String CLUSTER_NAME = "replication"; 
  public static final String STATE_MODEL = "MasterSlave";
  public static final String RESOURCE_NAME = "file_repository";
  public static final int DEFAULT_PARTITION_NUM = 1;

  public static void main(String[] args) {
     ZkClient zkc = null;
     try {
       zkc = new ZkClient(ZKADDRESS, ZkClient.DEFAULT_SESSION_TIMEOUT,
         ZkClient.DEFAULT_CONNECTION_TIMEOUT, new ZNRecordSerializer());

       ZKHelixAdmin admin = new ZKHelixAdmin(zkc);
       admin.addCluster(CLUSTER_NAME, true);
       
       StateModelConfigGenerator generator = new StateModelConfigGenerator();

       admin.addStateModelDef(CLUSTER_NAME, STATE_MODEL,
          new StateModelDefinition(generator.generateConfigForMasterSlave()));

       /* add instance of each node */ 
       for (int i = 0; i < 5; i++) {
         String port = "" + (1200 + i); 
         String serverId = "localhost_" + port;
         InstanceConfig config = new InstanceConfig(serverId);
         config.setHostName("localhost");
         config.setPort(port);
         config.setInstanceEnabled(true);
         admin.addInstance(CLUSTER_NAME, config);
       } 

       admin.addResource(CLUSTER_NAME, RESOURCE_NAME, DEFAULT_PARTITION_NUM,
               STATE_MODEL, RebalanceMode.SEMI_AUTO.toString());

       admin.rebalance(CLUSTER_NAME, RESOURCE_NAME, 1);

     } finally 
     {
       if (zkc != null) {
         zkc.close();
       }
     }
  }
}
