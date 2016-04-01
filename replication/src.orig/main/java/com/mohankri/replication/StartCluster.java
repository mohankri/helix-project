package com.mohankri.replication;

import org.apache.helix.HelixManager;
import org.apache.helix.controller.HelixControllerMain;

public class StartCluster {
  public static void main(String[] args) {
    try {
      final HelixManager controller =
         HelixControllerMain.startHelixController(SetupCluster.ZKADDRESS,
          SetupCluster.CLUSTER_NAME, "Replication Controller",
             HelixControllerMain.STANDALONE);
         
      Runtime.getRuntime().addShutdownHook(new Thread() {
       @Override
       public void run() {
         System.out.println("Shutting Down Cluster manager " +
                                              controller.getInstanceName());
         controller.disconnect();
       }
      }); 
      Thread.currentThread().join();
    } catch (Exception e) {
     e.printStackTrace();
    }
  }
} 
