package com.romeh.failover.demo;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


//Sample job model
@Builder
@Data
@AllArgsConstructor
public class Job {

    @QuerySqlField(index = true)
    String nodeId;
    Request request;
}
