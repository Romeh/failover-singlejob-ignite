### How to guarantee your single computation task is guaranteed to failover in case of node failures in apache Ignite

How to guarantee your single computation task is guaranteed to failover in case of node failures in apache Ignite ?

as you know failover support in apache ignite for computation tasks is only covered for map reduce jobs where slave nodes will do computations then reduce back to the master node , and in case of any failure in slave nodes where slave jobs are executing , then it that failed slave job will fail over to another node to continue execution .

Ok what about if I need to execute just single computation task and I need to have failover guarantee due may be it is a critical task that do financial data modification or must finished task in an acceptable status (Success or Failure) , how we can do that ? it is not supported out of the box by Ignite but we can have a small design extension using Ignite APIs to cover the same , HOW ?

