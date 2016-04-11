package com.mohankri.rabbitmq;

import org.apache.helix.HelixManager;
import org.apache.helix.api.StateTransitionHandlerFactory;
import org.apache.helix.api.id.PartitionId;

public class ZkStateModelFactory extends
			StateTransitionHandlerFactory<ZkStateModel> {
        private final String _consumerId;
        private final String _rqServer;

	public ZkStateModelFactory(String consumerId, String rqServer)
	{
	  System.out.println("ZkStateModel Factory...\n");
          _consumerId = consumerId;
          _rqServer = rqServer;
	}

        @Override
        public ZkStateModel createStateTransitionHandler(PartitionId partition)
        {
	  System.out.println("Create StateModel ...\n");
          ZkStateModel model = new ZkStateModel(_consumerId, partition.toString().split("_")[0], _rqServer);
          return model;
        }
}
