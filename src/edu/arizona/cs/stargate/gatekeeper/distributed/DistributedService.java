/*
 * The MIT License
 *
 * Copyright 2015 iychoi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.arizona.cs.stargate.gatekeeper.distributed;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.ReplicatedMap;
import edu.arizona.cs.stargate.common.ServiceNotStartedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class DistributedService {
    private static final Log LOG = LogFactory.getLog(DistributedService.class);
    
    private static DistributedService instance;

    private DistributedServiceConfiguration config;
    private HazelcastInstance hazelcastInstance;
    
    public static DistributedService getInstance(DistributedServiceConfiguration config) throws Exception {
        synchronized (DistributedService.class) {
            if(instance == null) {
                instance = new DistributedService(config);
            }
            return instance;
        }
    }
    
    public static DistributedService getInstance() throws ServiceNotStartedException {
        synchronized (DistributedService.class) {
            if(instance == null) {
                throw new ServiceNotStartedException("Cache service is not started");
            }
            return instance;
        }
    }
    
    DistributedService(DistributedServiceConfiguration config) throws Exception {
        if(config == null) {
            throw new Exception("DistributedServiceConfiguration is null. Failed to start DistributedService.");
        }
        
        this.config = config;
    }
    
    public synchronized void start() throws Exception {
        startHazelcast();
        LOG.info("Distributed service started");
    }
    
    private synchronized void startHazelcast() throws Exception {
        //Config hazelcastConfig = makeHazelcastMulticastConfig();
        Config hazelcastConfig = makeHazelcastTCPConfig();
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
    
    private Config makeDefaultHazelcastConfig() {
        Config config = new Config();
        config.getGroupConfig().setName("Stargate");
        
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(this.config.getPort());
        network.setPortAutoIncrement(true);
        network.setPortCount(100);
        
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("dht");
        mapConfig.setBackupCount(2);
        mapConfig.getMaxSizeConfig().setSize(0);
        mapConfig.setTimeToLiveSeconds(0);

        config.addMapConfig(mapConfig);
        
        return config;
    }
    
    private Config makeHazelcastMulticastConfig() {
        Config config = makeDefaultHazelcastConfig();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true).setMulticastGroup("224.2.2.3").setMulticastPort(54327);
        return config;
    }
    
    private Config makeHazelcastTCPConfig() {
        Config config = makeDefaultHazelcastConfig();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        if(!this.config.isLeaderHost()) {
            config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(this.config.getKnownHostAddr() + ":" + this.config.getPort());
        }
        
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(this.config.getMyHostAddr()+ ":" + this.config.getPort());
        
        return config;
    }
    
    public synchronized void stop() throws InterruptedException {
        stopHazelcast();
        LOG.info("Distributed service stopped");
    }
    
    private synchronized void stopHazelcast() throws InterruptedException {
        this.hazelcastInstance.shutdown();
    }
    
    public synchronized IMap getDistributedMap(String name) {
        return this.hazelcastInstance.getMap(name);
    }
    
    public synchronized MultiMap getDistributedMultiMap(String name) {
        return this.hazelcastInstance.getMultiMap(name);
    }
    
    public synchronized ReplicatedMap getReplicatedMap(String name) {
        return this.hazelcastInstance.getReplicatedMap(name);
    }
    
    public synchronized IQueue getDistributedQueue(String name) {
        return this.hazelcastInstance.getQueue(name);
    }
    
    public synchronized boolean isLeaderNode() {
        Member member = this.hazelcastInstance.getCluster().getMembers().iterator().next();
        return member.localMember();
    }
}