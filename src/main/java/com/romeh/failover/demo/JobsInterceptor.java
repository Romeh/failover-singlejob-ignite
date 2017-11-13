package com.romeh.failover.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheInterceptorAdapter;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;

import static com.romeh.failover.demo.CacheNames.ICEP_JOBS;

/**
 * Created by D-UX07QF on 14/03/2017.
 */
public class JobsInterceptor extends CacheInterceptorAdapter<String, Job> {

    @IgniteInstanceResource
    Ignite ignite;


    @Nullable@Override
    public void onAfterPut(Cache.Entry<String, Job> entry) {
        // sample sensitive computation task
        QueryTask queryTask=new QueryTask();
        // get current node reference to get its node id
        ClusterNode clusterNode = ignite.cluster().localNode();
        System.out.println("intercepting for job action triggering and setting node id : "+ clusterNode.id().toString());
        //store node id in the job wrapper object
        entry.getValue().setNodeId(clusterNode.id().toString());
        //create async computation with specific timeout with affinity to the jobs data cache to have collocated computation
        ignite.compute().withTimeout(5500)
                .affinityRunAsync(ICEP_JOBS.name(),entry.getKey(),
                        ()->queryTask.execute(entry.getValue().getRequest()));
    }

}
