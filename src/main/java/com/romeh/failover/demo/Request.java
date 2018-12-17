package com.romeh.failover.demo;



import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;


@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Request implements Serializable {

    @QuerySqlField (index = true)
    private Long modifiedTimestamp;
    @QuerySqlField
    private String requestID;

}
