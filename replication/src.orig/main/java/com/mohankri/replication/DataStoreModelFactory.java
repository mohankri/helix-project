package com.mohankri.replication;

import org.apache.helix.HelixManager;
import org.apache.helix.api.StateTransitionHandlerFactory;
import org.apache.helix.api.id.PartitionId;

public class DataStoreModelFactory extends
                StateTransitionHandlerFactory<DataStoreModel> 
{
   private final HelixManager manager;

   public DataStoreModelFactory(HelixManager manager)
   {
     System.out.println("DataStoreModelFactory ");
     this.manager = manager;
   }

   @Override
   public DataStoreModel createStateTransitionHandler(PartitionId part)
   {
     System.out.println("createStateTransitionHandler " + part.stringify());
     DataStoreModel model;
     model = new DataStoreModel(manager);
     return model;
   }
}
