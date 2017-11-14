package com.romeh.failover.demo;

import org.apache.ignite.Ignition;

import java.io.Serializable;

// example of sensitive action , best practice is to make it idempotent , if not you can use checkpoints for task state saving

public class QueryTask implements Serializable{


    public void execute(Request request){
        // start of the logic where it will take the data from
        System.out.println("Executing computation logic for the request " +request.getRequestID());
       // simulate processing time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // once it is done , remove from the jobs state cache to mark the job as finished and no more failover is needed
        Ignition.ignite().cache(CacheNames.ICEP_JOBS.name()).remove(request.getRequestID());
    }
}
