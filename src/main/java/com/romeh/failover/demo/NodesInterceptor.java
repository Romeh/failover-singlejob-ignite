package com.romeh.failover.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheInterceptorAdapter;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;

import static com.romeh.failover.demo.CacheNames.ICEP_JOBS;


public class NodesInterceptor extends CacheInterceptorAdapter<String, String> {

    @IgniteInstanceResource
    Ignite ignite;
    private transient IgniteCache<String, Job> jobs;
    private final String sql = "nodeId = ?";
    private transient SqlQuery<String, Job> affinityKeyRequestSqlQuery;


    @Nullable@Override
    public void onAfterPut(Cache.Entry<String, String> entry) {
        // sample compute task that can be sensitive and it need to have fail over support
        QueryTask task = new QueryTask();
        // get partitioned jobs cache reference
        jobs = ignite.cache(ICEP_JOBS.name());
        // get the current local node reference
        ClusterNode clusterNode = ignite.cluster().localNode();
        System.out.println("intercepting for Node failure and retry from node id : "+ clusterNode.id().toString()+" to node id : "+entry.getValue());

        // Create query to get pending jobs for that node id and submit them again
        affinityKeyRequestSqlQuery= new SqlQuery<>(Job.class, sql);
        affinityKeyRequestSqlQuery.setArgs(entry.getValue());
        jobs.query(affinityKeyRequestSqlQuery).forEach(affinityKeyJobEntry -> {
            System.out.println("found a pending jobs for node id: "+entry.getValue() +" and job id: "+affinityKeyJobEntry.getKey());
            // submit again the jobs for re-execution
            ignite.compute().withTimeout(5500)
                    .affinityRunAsync(ICEP_JOBS.name(),affinityKeyJobEntry.getKey(),
                            ()->task.execute(affinityKeyJobEntry.getValue().request));

        });

    }



}
