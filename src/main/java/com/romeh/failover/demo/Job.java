package com.romeh.failover.demo;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by D-UX07QF on 13/03/2017.
 */
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
