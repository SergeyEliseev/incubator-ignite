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

package org.apache.ignite.spi.discovery.tcp.messages;

import org.apache.ignite.cache.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.cache.*;
import org.apache.ignite.internal.util.tostring.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import java.io.*;
import java.util.*;

/**
 * Heartbeat message.
 * <p>
 * It is sent by coordinator node across the ring once a configured period.
 * Message makes two passes:
 * <ol>
 *      <li>During first pass, all nodes add their metrics to the message and
 *          update local metrics with metrics currently present in the message.</li>
 *      <li>During second pass, all nodes update all metrics present in the message
 *          and remove their own metrics from the message.</li>
 * </ol>
 * When message reaches coordinator second time it is discarded (it finishes the
 * second pass).
 */
@TcpDiscoveryRedirectToClient
public class TcpDiscoveryHeartbeatMessage extends TcpDiscoveryAbstractMessage {
    /** */
    private static final long serialVersionUID = 0L;

    /** Map to store nodes metrics. */
    @GridToStringExclude
    private Map<UUID, MetricsSet> metrics;

    /** Client node IDs. */
    private Collection<UUID> clientNodeIds;

    /** Cahce metrics by node. */
    @GridToStringExclude
    private Map<UUID, Map<String, CacheMetrics>> cacheMetrics;

    /**
     * Public default no-arg constructor for {@link Externalizable} interface.
     */
    public TcpDiscoveryHeartbeatMessage() {
        // No-op.
    }

    /**
     * Constructor.
     *
     * @param creatorNodeId Creator node.
     */
    public TcpDiscoveryHeartbeatMessage(UUID creatorNodeId) {
        super(creatorNodeId);

        metrics = U.newHashMap(1);
        cacheMetrics = U.newHashMap(1);
        clientNodeIds = new HashSet<>();
    }

    /**
     * Sets metrics for particular node.
     *
     * @param nodeId Node ID.
     * @param metrics Node metrics.
     */
    public void setMetrics(UUID nodeId, ClusterMetrics metrics) {
        assert nodeId != null;
        assert metrics != null;
        assert !this.metrics.containsKey(nodeId);

        this.metrics.put(nodeId, new MetricsSet(metrics));
    }

    /**
     * Sets cache metrics for particular node.
     *
     * @param nodeId Node ID.
     * @param metrics Node cache metrics.
     */
    public void setCacheMetrics(UUID nodeId, Map<String, CacheMetrics> metrics) {
        assert nodeId != null;
        assert metrics != null;
        assert !this.cacheMetrics.containsKey(nodeId);

        this.cacheMetrics.put(nodeId, metrics);
    }

    /**
     * Sets metrics for a client node.
     *
     * @param nodeId Server node ID.
     * @param clientNodeId Client node ID.
     * @param metrics Node metrics.
     */
    public void setClientMetrics(UUID nodeId, UUID clientNodeId, ClusterMetrics metrics) {
        assert nodeId != null;
        assert clientNodeId != null;
        assert metrics != null;
        assert this.metrics.containsKey(nodeId);

        this.metrics.get(nodeId).addClientMetrics(clientNodeId, metrics);
    }

    /**
     * Removes metrics for particular node from the message.
     *
     * @param nodeId Node ID.
     */
    public void removeMetrics(UUID nodeId) {
        assert nodeId != null;

        metrics.remove(nodeId);
    }

    /**
     * Removes cache metrics for particular node from the message.
     *
     * @param nodeId Node ID.
     */
    public void removeCacheMetrics(UUID nodeId) {
        assert nodeId != null;

        cacheMetrics.remove(nodeId);
    }

    /**
     * Gets metrics map.
     *
     * @return Metrics map.
     */
    public Map<UUID, MetricsSet> metrics() {
        return metrics;
    }

    /**
     * Gets cache metrics map.
     *
     * @return Cache metrics map.
     */
    public Map<UUID, Map<String, CacheMetrics>> cacheMetrics() {
        return cacheMetrics;
    }

    /**
     * @return {@code True} if this message contains metrics.
     */
    public boolean hasMetrics() {
        return !metrics.isEmpty();
    }

    /**
     * @return {@code True} this message contains cache metrics.
     */
    public boolean hasCacheMetrics() {
        return !cacheMetrics.isEmpty();
    }

    /**
     * @return {@code True} if this message contains metrics.
     */
    public boolean hasMetrics(UUID nodeId) {
        assert nodeId != null;

        return metrics.get(nodeId) != null;
    }

    /**
     * @param nodeId Node ID.
     *
     * @return {@code True} if this message contains cache metrics for particular node.
     */
    public boolean hasCacheMetrics(UUID nodeId) {
        assert nodeId != null;

        return cacheMetrics.get(nodeId) != null;
    }

    /**
     * Gets client node IDs for  particular node.
     *
     * @return Client node IDs.
     */
    public Collection<UUID> clientNodeIds() {
        return clientNodeIds;
    }

    /**
     * Adds client node ID.
     *
     * @param clientNodeId Client node ID.
     */
    public void addClientNodeId(UUID clientNodeId) {
        clientNodeIds.add(clientNodeId);
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeInt(metrics.size());

        if (!metrics.isEmpty()) {
            for (Map.Entry<UUID, MetricsSet> e : metrics.entrySet()) {
                U.writeUuid(out, e.getKey());
                out.writeObject(e.getValue());
            }
        }

        out.writeInt(cacheMetrics.size());

        if (!cacheMetrics.isEmpty()) {
            for (Map.Entry<UUID, Map<String, CacheMetrics>> e : cacheMetrics.entrySet()) {
                U.writeUuid(out, e.getKey());

                Map<String, CacheMetrics> ms = e.getValue();

                out.writeInt(ms == null ? 0 : ms.size());

                for (Map.Entry<String, CacheMetrics> m : ms.entrySet())
                    out.writeObject(m.getValue());
            }
        }

        U.writeCollection(out, clientNodeIds);
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int metricsSize = in.readInt();

        metrics = U.newHashMap(metricsSize);

        for (int i = 0; i < metricsSize; i++)
            metrics.put(U.readUuid(in), (MetricsSet)in.readObject());

        int cacheMetricsSize = in.readInt();

        cacheMetrics = U.newHashMap(cacheMetricsSize);

        for (int i = 0; i < cacheMetricsSize; i++) {
            UUID uuid = U.readUuid(in);

            int size = in.readInt();

            Map<String, CacheMetrics> ms = U.newHashMap(size);

            for (int j = 0; j < size; j++) {
                CacheMetricsSnapshot m = (CacheMetricsSnapshot) in.readObject();
                ms.put(m.name(), m);
            }

            cacheMetrics.put(uuid, ms);
        }

        clientNodeIds = U.readCollection(in);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(TcpDiscoveryHeartbeatMessage.class, this, "super", super.toString());
    }

    /**
     * @param metrics Metrics.
     * @return Serialized metrics.
     */
    private static byte[] serializeMetrics(ClusterMetrics metrics) {
        assert metrics != null;

        byte[] buf = new byte[ClusterMetricsSnapshot.METRICS_SIZE];

        ClusterMetricsSnapshot.serialize(buf, 0, metrics);

        return buf;
    }

    /**
     * @param nodeId Node ID.
     * @param metrics Metrics.
     * @return Serialized metrics.
     */
    private static byte[] serializeMetrics(UUID nodeId, ClusterMetrics metrics) {
        assert nodeId != null;
        assert metrics != null;

        byte[] buf = new byte[16 + ClusterMetricsSnapshot.METRICS_SIZE];

        U.longToBytes(nodeId.getMostSignificantBits(), buf, 0);
        U.longToBytes(nodeId.getLeastSignificantBits(), buf, 8);

        ClusterMetricsSnapshot.serialize(buf, 16, metrics);

        return buf;
    }

    /**
     */
    @SuppressWarnings("PublicInnerClass")
    public static class MetricsSet implements Externalizable {
        /** */
        private static final long serialVersionUID = 0L;

        /** Metrics. */
        private byte[] metrics;

        /** Client metrics. */
        private Collection<byte[]> clientMetrics;

        /**
         */
        public MetricsSet() {
            // No-op.
        }

        /**
         * @param metrics Metrics.
         */
        public MetricsSet(ClusterMetrics metrics) {
            assert metrics != null;

            this.metrics = serializeMetrics(metrics);
        }

        /**
         * @return Deserialized metrics.
         */
        public ClusterMetrics metrics() {
            return ClusterMetricsSnapshot.deserialize(metrics, 0);
        }

        /**
         * @return Client metrics.
         */
        public Collection<T2<UUID, ClusterMetrics>> clientMetrics() {
            return F.viewReadOnly(clientMetrics, new C1<byte[], T2<UUID, ClusterMetrics>>() {
                @Override public T2<UUID, ClusterMetrics> apply(byte[] bytes) {
                    UUID nodeId = new UUID(U.bytesToLong(bytes, 0), U.bytesToLong(bytes, 8));

                    return new T2<>(nodeId, ClusterMetricsSnapshot.deserialize(bytes, 16));
                }
            });
        }

        /**
         * @param nodeId Client node ID.
         * @param metrics Client metrics.
         */
        private void addClientMetrics(UUID nodeId, ClusterMetrics metrics) {
            assert nodeId != null;
            assert metrics != null;

            if (clientMetrics == null)
                clientMetrics = new ArrayList<>();

            clientMetrics.add(serializeMetrics(nodeId, metrics));
        }

        /** {@inheritDoc} */
        @Override public void writeExternal(ObjectOutput out) throws IOException {
            U.writeByteArray(out, metrics);

            out.writeInt(clientMetrics != null ? clientMetrics.size() : -1);

            if (clientMetrics != null) {
                for (byte[] arr : clientMetrics)
                    U.writeByteArray(out, arr);
            }
        }

        /** {@inheritDoc} */
        @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            metrics = U.readByteArray(in);

            int clientMetricsSize = in.readInt();

            if (clientMetricsSize >= 0) {
                clientMetrics = new ArrayList<>(clientMetricsSize);

                for (int i = 0; i < clientMetricsSize; i++)
                    clientMetrics.add(U.readByteArray(in));
            }
        }
    }
}
