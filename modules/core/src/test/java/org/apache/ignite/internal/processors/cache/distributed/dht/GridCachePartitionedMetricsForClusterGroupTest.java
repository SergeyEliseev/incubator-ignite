/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.distributed.dht;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.processors.cache.*;

import static org.apache.ignite.cache.CacheMode.*;
import static org.apache.ignite.cache.CacheRebalanceMode.*;
import static org.apache.ignite.cache.CacheWriteSynchronizationMode.*;

/**
 *
 */
public class GridCachePartitionedMetricsForClusterGroupTest extends GridCacheAbstractMetricsSelfTest {
    /** Grid count. */
    private static final int GRID_CNT = 3;
    public static final String CACHE_1 = "cache1";

    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return GRID_CNT;
    }

    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration ccfg = super.cacheConfiguration(gridName);

        ccfg.setBackups(0);

        return ccfg;
    }

    public void testMetrics() throws Exception {
        IgniteCache<Integer, Integer> cache = grid(0).cache(null);

        for (int i = 0; i < 1000; i++)
            cache.put(i, i);

        for (int i = 0; i < GRID_CNT; i++) {
            IgniteCache<Integer, Integer> c = grid(i).cache(null);
            System.out.println("!!! Grid(" + i + "), puts = " + c.metrics().getCachePuts() + ", size = " + c.size());
        }

        Thread.sleep(10000);

        CacheMetrics metrics = cache.metrics(grid(0).cluster().forRemotes());

        System.out.println(metrics);
/*
        assertEquals(1, cache.metrics().getCachePuts());
        assertEquals(0, grid(1).cache(null).metrics().getCachePuts());
        assertEquals(1, grid(2).cache(null).metrics().getCachePuts());
*/
    }
}
