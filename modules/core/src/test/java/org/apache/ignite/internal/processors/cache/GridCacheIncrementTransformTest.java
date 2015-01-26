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

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.*;
import org.apache.ignite.testframework.junits.common.*;

import javax.cache.processor.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.apache.ignite.cache.CacheAtomicWriteOrderMode.*;
import static org.apache.ignite.cache.CacheAtomicityMode.*;
import static org.apache.ignite.cache.CacheMode.*;
import static org.apache.ignite.cache.CacheDistributionMode.*;
import static org.apache.ignite.cache.CachePreloadMode.*;
import static org.apache.ignite.cache.CacheWriteSynchronizationMode.*;

/**
 * Tests cache in-place modification logic with iterative value increment.
 */
public class GridCacheIncrementTransformTest extends GridCommonAbstractTest {
    /** IP finder. */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** Number of nodes to test on. */
    private static final int GRID_CNT = 4;

    /** Number of increment iterations. */
    private static final int NUM_ITERS = 5000;

    /** Helper for excluding stopped node from iteration logic. */
    private AtomicReferenceArray<Ignite> grids;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        CacheConfiguration cache = new CacheConfiguration();

        cache.setCacheMode(PARTITIONED);
        cache.setAtomicityMode(ATOMIC);
        cache.setDistributionMode(PARTITIONED_ONLY);
        cache.setAtomicWriteOrderMode(PRIMARY);
        cache.setWriteSynchronizationMode(FULL_SYNC);
        cache.setBackups(1);
        cache.setPreloadMode(SYNC);

        cfg.setCacheConfiguration(cache);

        TcpDiscoverySpi disco = new TcpDiscoverySpi();

        disco.setIpFinder(IP_FINDER);

        cfg.setDiscoverySpi(disco);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGrids(GRID_CNT);

        grids = new AtomicReferenceArray<>(GRID_CNT);

        for (int i = 0; i < GRID_CNT; i++)
            grids.set(i, grid(i));

        cache(0).put("key", new TestObject(0));
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();

        grids = null;
    }

    /**
     * @throws Exception If failed.
     */
    public void testIncrement() throws Exception {
        testIncrement(false);
    }

    /**
     * @throws Exception If failed.
     */
    public void testIncrementRestart() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        IgniteFuture<Long> fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override public void run() {
                try {
                    Random rnd = new Random();

                    while (!stop.get()) {
                        int idx = -1;

                        Ignite ignite = null;

                        while (ignite == null) {
                            idx = rnd.nextInt(GRID_CNT);

                            ignite = grids.getAndSet(idx, null);
                        }

                        stopGrid(idx);

                        assert grids.compareAndSet(idx, null, startGrid(idx));
                    }
                }
                catch (Exception e) {
                    error.set(e);
                }
            }
        }, 1, "restarter");

        try {
            testIncrement(true);

            assertNull(error.get());
        }
        finally {
            stop.set(true);

            fut.get(getTestTimeout());
        }
    }

    /**
     * @param restarts Whether test is running with node restarts.
     * @throws Exception If failed.
     */
    private void testIncrement(boolean restarts) throws Exception {
        Random rnd = new Random();

        for (int i = 0; i < NUM_ITERS; i++) {
            int idx = -1;

            Ignite ignite = null;

            while (ignite == null) {
                idx = rnd.nextInt(GRID_CNT);

                ignite = restarts ? grids.getAndSet(idx, null) : grid(idx);
            }

            IgniteCache<String, TestObject> cache = ignite.jcache(null);

            assertNotNull(cache);

            TestObject obj = cache.get("key");

            assertNotNull(obj);
            assertEquals(i, obj.val);

            while (true) {
                try {
                    cache.invoke("key", new Processor());

                    break;
                }
                catch (CachePartialUpdateException ignored) {
                    // Need to re-check if update actually succeeded.
                    TestObject updated = cache.get("key");

                    if (updated != null && updated.val == i + 1)
                        break;
                }
            }

            if (restarts)
                assert grids.compareAndSet(idx, null, ignite);
        }
    }

    /** */
    private static class TestObject implements Serializable {
        /** Value. */
        private int val;

        /**
         * @param val Value.
         */
        private TestObject(int val) {
            this.val = val;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return "TestObject [val=" + val + ']';
        }
    }

    /** */
    private static class Processor implements EntryProcessor<String, TestObject, Void>, Serializable {
        /** {@inheritDoc} */
        @Override public Void process(MutableEntry<String, TestObject> e, Object... args) {
            TestObject obj = e.getValue();

            assert obj != null;

            e.setValue(new TestObject(obj.val + 1));

            return null;
        }
    }
}