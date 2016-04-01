package com.mohankri.replication;

public class StartAgent {
  public static void main(String[] args)
  {
    final String serverId = args[0];
    try {
      final DataStore store = new DataStore(SetupCluster.ZKADDRESS,
              SetupCluster.CLUSTER_NAME, serverId);
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          System.out.println("Add ShutDown Hook\n");
          store.disconnect();  
        }
      });
      store.connect();
    } finally {
       
    }
  }
} 
