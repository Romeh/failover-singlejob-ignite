package com.romeh.failover.demo;

import org.apache.ignite.*;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.EventType;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

/**
 * Created by D-UX07QF on 13/03/2017.
 */
public class NodeApp {

    public static void main(String[] args) throws Exception {
        // just for demo and test purpose , you should design more generic bootstrap logic to start your node
        Ignite ignite = Ignition.start("config/igniteExpiry.xml");
        try {

            IgniteCache<String, Job> cache = ignite.cache(CacheNames.ICEP_JOBS.name());
            // enable that ONLY for one node and after you start see the system outs , you can kill that node to see the fail over logic in the second node
            System.out.println("start of jobs creation");
            for (int i = 0; i <= 25; i++) {
                String key = i + "Key";
                // start creating jobs by inserting them into the
                cache.put(key
                        , Job.builder().nodeId(ignite.cluster().localNode().id().toString()).
                                request(Request.builder().requestID(key).modifiedTimestamp(System.currentTimeMillis()).build()).
                                build());
            }
            // listen globally for all nodes failed or removed events
            ignite.events().localListen(event -> {
                DiscoveryEvent discoveryEvent = (DiscoveryEvent) event;
                System.out.println("Received Node event [evt=" + discoveryEvent.name() +
                        ", nodeID=" + discoveryEvent.eventNode() + ']');

                ignite.compute().runAsync(() -> {
                    IgniteCache<String, String> nodes = ignite.cache(CacheNames.ICEP_NODES.name());
                    String failedNodeId = discoveryEvent.eventNode().id().toString();
                    // only one NODE will manage to insert successfully as it it is an atomic operation and thread safe
                    nodes.withExpiryPolicy(new CreatedExpiryPolicy(Duration.ONE_HOUR)).putIfAbsent(failedNodeId, failedNodeId);
                });

                return true;

            }, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_FAILED);


        } catch (Exception e) {
            // just for test , do not do that in production code
            e.printStackTrace();
        }

    }
}
