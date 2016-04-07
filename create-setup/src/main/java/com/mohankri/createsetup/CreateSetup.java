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
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.commons.io.FileUtils;
import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixManager;
import org.apache.helix.PropertyKey.Builder;
import org.apache.helix.controller.HelixControllerMain;
import org.apache.helix.model.HelixConfigScope;
import org.apache.helix.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.model.builder.HelixConfigScopeBuilder;
import org.apache.helix.tools.ClusterSetup;


public class CreateSetup {
  public static final String DEFAULT_STATE_MODEL = "MasterSlave";
  public static final String DEFAULT_CLUSTER_NAME = "file-store-test1";

  public static void main(String[] args) throws InterruptedException {
    ZkServer server = null;
    System.out.println("Integration Test");
    try {
      String baseDir = "/tmp/IntegrationTest/";
      int zkPort = 2199;

      final String zkAddress = "localhost:" + zkPort;

/*      server = new ZkServer(dataDir, logDir, defaultNameSpace, zkPort);
      server.start(); */

      ClusterSetup setup = new ClusterSetup(zkAddress);
      final String clusterName = "file-store-test1";
      setup.deleteCluster(clusterName);
      setup.addCluster(clusterName, true);
      setup.addInstanceToCluster(clusterName, "localhost_12001");
      setup.addInstanceToCluster(clusterName, "localhost_12002");
      setup.addInstanceToCluster(clusterName, "localhost_12003");
      setup.addResourceToCluster(clusterName, "repository", 1, "MasterSlave");
      setup.rebalanceResource(clusterName, "repository", 3); 

      // Set the configuration
      final String instanceName1 = "localhost_12001";
      addConfiguration(setup, baseDir, clusterName, instanceName1);
      final String instanceName2 = "localhost_12002";
      addConfiguration(setup, baseDir, clusterName, instanceName2);
      final String instanceName3 = "localhost_12003";
      addConfiguration(setup, baseDir, clusterName, instanceName3);

      // Start Controller
/*
      final HelixManager manager =
          HelixControllerMain.startHelixController(zkAddress, clusterName, "controller",
              HelixControllerMain.STANDALONE); */
      Thread.sleep(5000);
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
    FileUtils.deleteDirectory(new File(properties.get("change_log_dir")));
    FileUtils.deleteDirectory(new File(properties.get("file_store_dir")));
    FileUtils.deleteDirectory(new File(properties.get("check_point_dir")));
    new File(properties.get("change_log_dir")).mkdirs();
    new File(properties.get("file_store_dir")).mkdirs();
    new File(properties.get("check_point_dir")).mkdirs();
  }
}
