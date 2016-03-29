package com.mohankri.rabbitmq;

import com.linkedin.helix.manager.zk.ZkClient;
import com.linkedin.helix.manager.zk.ZKHelixAdmin;
import com.linkedin.helix.manager.zk.ZNRecordSerializer;
import com.linkedin.helix.model.StateModelDefinition;
import com.linkedin.helix.tools.StateModelConfigGenerator;
import com.linkedin.helix.model.IdealState.IdealStateModeProperty;

/**
 * Hello world!
 *
 */
public class ZkSetup 
{
    public static final String DEFAULT_CLUSTER_NAME = "KrishnaHelix";
    public static final String DEFAULT_STATE_MODEL = "OnlineOffline";
    public static final String DEFAULT_RESOURCE_NAME = "topic";
    public static final int DEFAULT_PARTITION_NUMBER = 6;

    public static void main( String[] args )
    {
        final String zkAddr = "localhost:2199";
        final String clusterName = DEFAULT_CLUSTER_NAME;
	ZkClient zk = null;
        
	try {
          zk = new ZkClient(zkAddr, ZkClient.DEFAULT_SESSION_TIMEOUT,
             ZkClient.DEFAULT_CONNECTION_TIMEOUT, new ZNRecordSerializer());
          ZKHelixAdmin admin = new ZKHelixAdmin(zk);
          admin.addCluster(clusterName, true);
          
          StateModelConfigGenerator generator = new StateModelConfigGenerator();
          admin.addStateModelDef(clusterName, DEFAULT_STATE_MODEL,
             new StateModelDefinition(generator.generateConfigForOnlineOffline()));
          
          String resourceName = DEFAULT_RESOURCE_NAME;
          admin.addResource(clusterName, resourceName, DEFAULT_PARTITION_NUMBER,
                  DEFAULT_STATE_MODEL,
                  IdealStateModeProperty.AUTO_REBALANCE.toString());
          admin.rebalance(clusterName, resourceName, 1); 

	} finally 
	{
		if (zk != null) {
			zk.close();
		}
	}
    }
}
