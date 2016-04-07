package com.mohankri.clusterparticipant;

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

import org.apache.helix.HelixManager;
import org.apache.helix.api.StateTransitionHandlerFactory;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;

public class ClusterParticipantStateModelFactory extends StateTransitionHandlerFactory<ClusterParticipantStateModel> {
  private final HelixManager manager;

  public ClusterParticipantStateModelFactory(HelixManager manager) {
    this.manager = manager;
  }

  @Override
  public ClusterParticipantStateModel createStateTransitionHandler(ResourceId resource, PartitionId partition) {
  //@Override
  //public ClusterParticipantStateModel createStateTransitionHandler(PartitionId partition) {
    ClusterParticipantStateModel model;
    model =
        new ClusterParticipantStateModel(manager, partition.toString().split("_")[0], partition.toString());
    return model;
  }
}
