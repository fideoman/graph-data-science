/*
 * Copyright (c) 2017-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.graphalgo.pagerank;

import org.immutables.value.Value;
import org.neo4j.graphalgo.annotation.Configuration;
import org.neo4j.graphalgo.impl.pagerank.PageRank;
import org.neo4j.graphalgo.newapi.BaseAlgoConfig;
import org.neo4j.graphalgo.newapi.DampingFactorConfig;
import org.neo4j.graphalgo.newapi.IterationsConfig;
import org.neo4j.graphalgo.newapi.ToleranceConfig;
import org.neo4j.graphalgo.newapi.WeightConfig;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import java.util.Collections;
import java.util.List;

public interface PageRankConfigBase extends
    BaseAlgoConfig,
    WeightConfig,
    ToleranceConfig,
    IterationsConfig,
    DampingFactorConfig {

    @Value.Default
    @Override
    default double tolerance() {
        return 0.0001;
    }

    @Value.Default
    @Override
    default int maxIterations() {
        return 10;
    }

    @Value.Default
    @Override
    default double dampingFactor() {
        return 0.85;
    }

    default List<Node> sourceNodeIds() {
        return Collections.emptyList();
    }

    // TODO: consider moving this to WeightConfig or create a sub interface of that
    default boolean cacheWeights() {
        return false;
    }

    //TODO remove later
    @Configuration.ConvertWith("org.neo4j.graphalgo.Projection#parseDirection")
    @Value.Default
    default Direction direction() {
        return Direction.OUTGOING;
    }

    default PageRank.Config toOldConfig() {
        return new PageRank.Config(
            maxIterations(),
            dampingFactor(),
            tolerance(),
            cacheWeights()
        );
    }
}
