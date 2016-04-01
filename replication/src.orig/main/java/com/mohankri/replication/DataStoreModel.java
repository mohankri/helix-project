package com.mohankri.replication;

import org.apache.helix.HelixManager;
import org.apache.helix.api.TransitionHandler;
import org.apache.helix.participant.statemachine.Transition;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.model.Message;
import org.apache.helix.NotificationContext;

@StateModelInfo(initialState = "OFFLINE",
                states = {"OFFLINE", "MASTER", "SLAVE"})

public class DataStoreModel extends TransitionHandler {
  public DataStoreModel(HelixManager manager) {
    System.out.println("DataStoreModel...\n");
  }
 
  @Transition(to = "ONLINE", from = "OFFLINE")
  public void onBecomeOnlineFromOffline(Message message, NotificationContext
                                                     context) {
    System.out.println("OFFLINE ---> ONLINE\n");
  }

  @Transition(to = "OFFLINE", from = "ONLINE")
  public void onBecomeOfflineFromOnline(Message message, NotificationContext
                                                           context) {
    System.out.println("ONLINE ---> OFFLINE\n");
  }

  @Transition(to = "DROPPED", from = "OFFLINE")
  public void onBecomeDroppedFromOffline(Message message, NotificationContext
                                                           context)   {
    System.out.println("Dropped ---> OFFLINE\n");
  }

  @Transition(to = "OFFLINE", from = "ERROR")
  public void onBecomeOfflineFromError(Message message, NotificationContext
                                                        context)
  {
    System.out.println("OFFLINE ---> ERROR\n");
  }

  @Transition(from = "MASTER", to = "SLAVE")
  public void onBecomeSlaveFromMaster(Message message, NotificationContext
                                                        context)
  {
    System.out.println("Master ---> Slave\n");
  }

  @Transition(from = "SLAVE", to = "OFFLINE")
  public void onBecomeOfflineFromSlave(Message message, NotificationContext
                                                        context)
  {
    System.out.println("Master ---> Slave\n");
  }

  @Override
  public void reset()
  {
    System.out.println("reset \n");
  }
}
