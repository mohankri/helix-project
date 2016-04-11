package com.mohankri.createsetup;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import com.hcd.clusternode.FileStore;
//import com.hcd.clusternode.FileStore;

import org.I0Itec.zkclient.IDefaultNameSpace;
//import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.commons.io.FileUtils;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixManager;
import org.apache.helix.api.id.StateModelDefId;
import org.apache.helix.api.State;
import org.apache.helix.PropertyKey.Builder;
import org.apache.helix.controller.HelixControllerMain;
import org.apache.helix.model.HelixConfigScope;
import org.apache.helix.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.model.builder.HelixConfigScopeBuilder;
import org.apache.helix.model.StateModelDefinition;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.IdealState.RebalanceMode;
import org.apache.helix.tools.ClusterSetup;
import org.apache.helix.tools.StateModelConfigGenerator;


public class CreateSetup {
  public static final String DEFAULT_STATE_MODEL = "MasterSlave";
  public static final String DEFAULT_CLUSTER_NAME = "file-store-test1";
  private static final StateModelDefId STATE_MODEL_NAME = StateModelDefId.from("MyStateModel");

  // states
  private static final State SLAVE = State.from("SLAVE");
  private static final State OFFLINE = State.from("OFFLINE");
  private static final State MASTER = State.from("MASTER");
  private static final State DROPPED = State.from("DROPPED");

  public static void main(String[] args) throws InterruptedException {
    ZkServer server = null;
    System.out.println("Integration Test");
    ZkClient zkclient = null;
    try {
      String baseDir = "/tmp/IntegrationTest/";
      int zkPort = 2199;

      final String zkAddress = "localhost:" + zkPort;
      final String clusterName = "file-store-test1";
/*
      ClusterSetup setup = new ClusterSetup(zkAddress);
      setup.deleteCluster(clusterName);
      setup.addCluster(clusterName, true);
      setup.addInstanceToCluster(clusterName, "localhost_12001");
      setup.addInstanceToCluster(clusterName, "localhost_12002");
      setup.addInstanceToCluster(clusterName, "localhost_12003");
      setup.addResourceToCluster(clusterName, "repository", 1, "MasterSlave");
      setup.rebalanceResource(clusterName, "repository", 2);  */

      
      zkclient = new ZkClient(zkAddress, ZkClient.DEFAULT_SESSION_TIMEOUT,
		ZkClient.DEFAULT_CONNECTION_TIMEOUT, new ZNRecordSerializer());

      ZKHelixAdmin admin = new ZKHelixAdmin(zkclient);
      admin.addCluster(clusterName, true); 
/*
      admin.addStateModelDef(clusterName, DEFAULT_STATE_MODEL,
          new StateModelDefinition(StateModelConfigGenerator.generateConfigForMasterSlave())); */

      StateModelDefinition myStateModel = defineStateModel();

     System.out.println("Configuring StateModel: " + "MyStateModel  with 1 Master and 1 Slave");
    admin.addStateModelDef(clusterName, STATE_MODEL_NAME.stringify(), myStateModel);

      for (int i = 1; i < 4; i++) {
        String port = "" + (12000 + i);
        String serverId = "localhost_" + port;
        InstanceConfig config = new InstanceConfig(serverId);
        config.setHostName("localhost");
	config.setPort(port);
	config.setInstanceEnabled(true);
      	admin.addInstance(clusterName, config);
      }

       admin.addResource(clusterName, "repository", 1, STATE_MODEL_NAME.stringify(),
        "SEMI_AUTO");

      admin.rebalance(clusterName, "repository", 3);
      // Set the configuration

/*      final String instanceName1 = "localhost_12001";
      addConfiguration(setup, baseDir, clusterName, instanceName1);
      final String instanceName2 = "localhost_12002";
      addConfiguration(setup, baseDir, clusterName, instanceName2);
      final String instanceName3 = "localhost_12003";
      addConfiguration(setup, baseDir, clusterName, instanceName3); */

      //printStatus(manager);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (server != null) {
        // server.shutdown();
      }
    }
    Thread.currentThread().join();
    //Thread.sleep(600);
  }
  
   private static StateModelDefinition defineStateModel() {
    StateModelDefinition.Builder builder = new StateModelDefinition.Builder(STATE_MODEL_NAME);
    // Add states and their rank to indicate priority. Lower the rank higher the
    // priority
    builder.addState(MASTER, 1);
    builder.addState(SLAVE, 2);
    builder.addState(OFFLINE);
    builder.addState(DROPPED);
    // Set the initial state when the node starts
    builder.initialState(OFFLINE);

    // Add transitions between the states.
    builder.addTransition(OFFLINE, SLAVE);
    builder.addTransition(SLAVE, OFFLINE);
    builder.addTransition(SLAVE, MASTER);
    builder.addTransition(MASTER, SLAVE);
    builder.addTransition(OFFLINE, DROPPED);

    // set constraints on states.
    // static constraint
    builder.upperBound(MASTER, 1);
    // dynamic constraint, R means it should be derived based on the replication
    // factor.
    builder.dynamicUpperBound(SLAVE, "R");

    StateModelDefinition statemodelDefinition = builder.build();
    return statemodelDefinition;
  }
  private static void addConfiguration(ClusterSetup setup, String baseDir, String clusterName,
      String instanceName) throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    HelixConfigScopeBuilder builder = new HelixConfigScopeBuilder(ConfigScopeProperty.PARTICIPANT);
    HelixConfigScope instanceScope =
        builder.forCluster(clusterName).forParticipant(instanceName).build();
    properties.put("change_log_dir", baseDir + instanceName + "/translog");
    properties.put("file_store_dir", baseDir + instanceName + "/filestore");
    properties.put("check_point_dir", baseDir + instanceName + "/checkpoint");
    setup.getClusterManagementTool().setConfig(instanceScope, properties);
/*
    FileUtils.deleteDirectory(new File(properties.get("change_log_dir")));
    FileUtils.deleteDirectory(new File(properties.get("file_store_dir")));
    FileUtils.deleteDirectory(new File(properties.get("check_point_dir")));
    new File(properties.get("change_log_dir")).mkdirs();
    new File(properties.get("file_store_dir")).mkdirs();
    new File(properties.get("check_point_dir")).mkdirs(); */
  }
}
