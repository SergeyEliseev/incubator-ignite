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

package org.apache.ignite.internal.cluster;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.util.lang.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 */
public class IgniteClusterAsyncImpl extends AsyncSupportAdapter<IgniteCluster>
    implements IgniteCluster, Externalizable {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private IgniteClusterImpl cluster;

    /**
     * Required by {@link Externalizable}.
     */
    public IgniteClusterAsyncImpl() {
        // No-op.
    }

    /**
     * @param cluster Cluster.
     */
    public IgniteClusterAsyncImpl(IgniteClusterImpl cluster) {
        super(true);

        this.cluster = cluster;
    }

    /** {@inheritDoc} */
    @Override public ClusterNode localNode() {
        return cluster.localNode();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forLocal() {
        return cluster.forLocal();
    }

    /** {@inheritDoc} */
    @Override public <K, V> ConcurrentMap<K, V> nodeLocalMap() {
        return cluster.nodeLocalMap();
    }

    /** {@inheritDoc} */
    @Override public boolean pingNode(UUID nodeId) {
        return cluster.pingNode(nodeId);
    }

    /** {@inheritDoc} */
    @Override public long topologyVersion() {
        return cluster.topologyVersion();
    }

    /** {@inheritDoc} */
    @Nullable @Override public Collection<ClusterNode> topology(long topVer) {
        return cluster.topology(topVer);
    }

    /** {@inheritDoc} */
    @Override public <K> Map<ClusterNode, Collection<K>> mapKeysToNodes(@Nullable String cacheName,
        @Nullable Collection<? extends K> keys) {
        return cluster.mapKeysToNodes(cacheName, keys);
    }

    /** {@inheritDoc} */
    @Nullable @Override public <K> ClusterNode mapKeyToNode(@Nullable String cacheName, K key) {
        return cluster.mapKeyToNode(cacheName, key);
    }

    /** {@inheritDoc} */
    @Override public Collection<GridTuple3<String, Boolean, String>> startNodes(File file,
        boolean restart,
        int timeout,
        int maxConn)
    {
        try {
            return saveOrGet(cluster.startNodesAsync(file, restart, timeout, maxConn));
        }
        catch (IgniteCheckedException e) {
            throw U.convertException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public Collection<GridTuple3<String, Boolean, String>> startNodes(
        Collection<Map<String, Object>> hosts,
        @Nullable Map<String, Object> dflts,
        boolean restart,
        int timeout,
        int maxConn)
    {
        try {
            return saveOrGet(cluster.startNodesAsync(hosts, dflts, restart, timeout, maxConn));
        }
        catch (IgniteCheckedException e) {
            throw U.convertException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public void stopNodes() {
        cluster.stopNodes();
    }

    /** {@inheritDoc} */
    @Override public void stopNodes(Collection<UUID> ids) {
        cluster.stopNodes(ids);
    }

    /** {@inheritDoc} */
    @Override public void restartNodes() {
        cluster.restartNodes();
    }

    /** {@inheritDoc} */
    @Override public void restartNodes(Collection<UUID> ids) {
        cluster.restartNodes(ids);
    }

    /** {@inheritDoc} */
    @Override public void resetMetrics() {
        cluster.resetMetrics();
    }

    /** {@inheritDoc} */
    @Override public Ignite ignite() {
        return cluster.ignite();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forNodes(Collection<? extends ClusterNode> nodes) {
        return cluster.forNodes(nodes);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forNode(ClusterNode node, ClusterNode... nodes) {
        return cluster.forNode(node, nodes);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forOthers(ClusterNode node, ClusterNode... nodes) {
        return cluster.forOthers(node, nodes);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forOthers(ClusterGroup prj) {
        return cluster.forOthers(prj);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forNodeIds(Collection<UUID> ids) {
        return cluster.forNodeIds(ids);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forNodeId(UUID id, UUID... ids) {
        return cluster.forNodeId(id, ids);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forPredicate(IgnitePredicate<ClusterNode> p) {
        return cluster.forPredicate(p);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forAttribute(String name, @Nullable String val) {
        return cluster.forAttribute(name, val);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forServers() {
        return cluster.forServers();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forClients() {
        return cluster.forClients();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forCacheNodes(String cacheName) {
        return cluster.forCacheNodes(cacheName);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forDataNodes(String cacheName) {
        return cluster.forDataNodes(cacheName);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forClientNodes(String cacheName) {
        return cluster.forClientNodes(cacheName);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forRemotes() {
        return cluster.forRemotes();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forHost(ClusterNode node) {
        return cluster.forHost(node);
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forDaemons() {
        return cluster.forDaemons();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forRandom() {
        return cluster.forRandom();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forOldest() {
        return cluster.forOldest();
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup forYoungest() {
        return cluster.forYoungest();
    }

    /** {@inheritDoc} */
    @Override public Collection<ClusterNode> nodes() {
        return cluster.nodes();
    }

    /** {@inheritDoc} */
    @Nullable @Override public ClusterNode node(UUID id) {
        return cluster.node(id);
    }

    /** {@inheritDoc} */
    @Nullable @Override public ClusterNode node() {
        return cluster.node();
    }

    /** {@inheritDoc} */
    @Override public IgnitePredicate<ClusterNode> predicate() {
        return cluster.predicate();
    }

    /** {@inheritDoc} */
    @Override public ClusterMetrics metrics() {
        return cluster.metrics();
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cluster = (IgniteClusterImpl)in.readObject();
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(cluster);
    }

    /**
     * @return Cluster async instance.
     *
     * @throws ObjectStreamException If failed.
     */
    protected Object readResolve() throws ObjectStreamException {
        return cluster.withAsync();
    }
}
