package com.mohankri.rabbitmq;

import com.linkedin.helix.participant.statemachine.StateModelFactory;

public class ZkStateModelFactory extends StateModelFactory<ZkStateModel> {
        private final String _consumerId;
        private final String _rqServer;

	public ZkStateModelFactory(String consumerId, String rqServer)
	{
	  System.out.println("ZkStateModel Factory...\n");
          _consumerId = consumerId;
          _rqServer = rqServer;
	}

        @Override
        public ZkStateModel createNewStateModel(String partition)
        {
	  System.out.println("Create StateModel ...\n");
          ZkStateModel model = new ZkStateModel(_consumerId, partition, _rqServer);
          return model;
        }
}
