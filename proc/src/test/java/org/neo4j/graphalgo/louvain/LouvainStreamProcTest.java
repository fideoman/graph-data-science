/*
 * Copyright (c) 2017-2020 "Neo4j,"
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
package org.neo4j.graphalgo.louvain;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.graphalgo.AlgoBaseProc;
import org.neo4j.graphalgo.GdsCypher;
import org.neo4j.graphalgo.core.CypherMapWrapper;
import org.neo4j.graphalgo.impl.louvain.Louvain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.neo4j.graphalgo.CommunityHelper.assertCommunities;

class LouvainStreamProcTest extends LouvainBaseProcTest<LouvainStreamConfig> {
    @Override
    public Class<? extends AlgoBaseProc<?, Louvain, LouvainStreamConfig>> getProcedureClazz() {
        return LouvainStreamProc.class;
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("org.neo4j.graphalgo.louvain.LouvainBaseProcTest#graphVariations")
    void testStream(GdsCypher.QueryBuilder queryBuilder, String testCaseName) {
        @Language("Cypher") String query = queryBuilder
            .algo("louvain")
            .streamMode()
            .yields("nodeId", "communityId", "communityIds");

        List<Long> actualCommunities = new ArrayList<>();
        runQueryWithRowConsumer(query, row -> {
            int id = row.getNumber("nodeId").intValue();
            long community = row.getNumber("communityId").longValue();
            assertNull(row.get("communityIds"));
            actualCommunities.add(id, community);
        });
        assertCommunities(actualCommunities, RESULT);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("org.neo4j.graphalgo.louvain.LouvainBaseProcTest#graphVariations")
    void testStreamCommunities(GdsCypher.QueryBuilder queryBuilder, String testCaseName) {
        @Language("Cypher") String query = queryBuilder
            .algo("louvain")
            .streamMode()
            .addParameter("includeIntermediateCommunities", true)
            .yields("nodeId", "communityId", "communityIds");

        runQueryWithRowConsumer(query, row -> {
            Object maybeList = row.get("communityIds");
            assertTrue(maybeList instanceof List);
            List<Long> communities = (List<Long>) maybeList;
            assertEquals(2, communities.size());
            assertEquals(communities.get(1), row.getNumber("communityId").longValue());
        });
    }

    @Test
    void testCreateConfigWithDefaults() {
        LouvainBaseConfig louvainConfig = LouvainStreamConfig.of(
            "",
            Optional.empty(),
            Optional.empty(),
            CypherMapWrapper.empty()
        );
        assertEquals(false, louvainConfig.includeIntermediateCommunities());
        assertEquals(10, louvainConfig.maxLevels());
    }

    @Override
    public LouvainStreamConfig createConfig(CypherMapWrapper mapWrapper) {
        return LouvainStreamConfig.of("", Optional.empty(), Optional.empty(), mapWrapper);
    }
}
