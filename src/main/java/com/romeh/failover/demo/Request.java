package com.romeh.failover.demo;


import com.google.code.morphia.annotations.Indexed;
import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by D-UX07QF on 13/03/2017.
 */

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
