package com.romeh.failover.demo;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


//Sample job model
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Job {

    @QuerySqlField(index = true)
    String nodeId;
    Request request;
}
