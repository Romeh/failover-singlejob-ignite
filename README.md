**How to guarantee your single computation task to be finished in case of node
crash in apache Ignite **

How to guarantee your single computation task is guaranteed to failover in case
of node failures in apache Ignite ?

![](https://cdn-images-1.medium.com/max/800/1*Yclh5mXd8QfJu3AYMEfBWQ.png)

As you know failover support in apache ignite for computation tasks is only
covered for map reduce jobs where slave nodes will do computations then reduce
back to the master node , and in case of any failure in slave nodes where slave
jobs are executing , then it that failed slave job will fail over to another
node to continue execution .

Ok what about if I need to execute just single computation task and I need to
 have failover guarantee due may be it is a critical task that do financial data
modification or must finished task in an acceptable status (Success or Failure)
, how we can do that ? it is not supported out of the box by Ignite but we can
have a small design extension using Ignite APIs to cover the same , HOW ?



**Here is the main steps from the overview above via the following flow :**

> 1- You need to create 2 partitioned caches , one for single jobs reference and
> one for node Ids reference , you should make those caches backed by persistence
store in production if you need to survive total grid crash

> 2- Define jobs cache after put interceptor to set the node id which is the
> primary owner and triggerer of that compute task 

> 3- Define nodes cache interceptor to intercept after put actions so it can query
> for all pending jobs for that node id then submit them again into the compute
grid with affinity 

> 4- Enable event listening for node left and node removal in the grid to
> intercept node failure

**Then let us run the show , imagine you have data and compute grid of 2 server
nodes :**

> a- you trigger a job in node 1 which will do sensitive action like financial
> action and you need to be sure it is finished with a valid state whatever the
case 

> b- what if that primary node 1 crashed , what will happen to that compute task ,
> without the extension highlighted above it will disappear with the wind 

> c- but with that failover small extension , Node 2 . will catch an event that
> Node 1 left , then it will query jobs cache for all jobs that has that node id
and resubmit them again for computation , optimal case if you have idempotent
actions so it can be executed multiple times or use job checkpointing for saving
the execution state to resume from the last saved point 

**Job data model for Jobs cache where we mark node id an ignite SQL queryable
indexed field :**

**How the ignite failed nodes cache interceptor is implemented :**

**How the ignite jobs cache interceptor is implemented :**

**Apache ignite config :**

Enable Node removal and failure events listening ONLY as enabling too much
events will cause some performance overhead

**Main App tester :**

**Testing flow :**

1- first run the first ignite server node with that code commented out :

2- then run the second server node but before doing it , uncomment the
highlighted code above which simulate creating now jobs for computation by
inserting them into the jobs cache

3- once you run the second node , after 5 seconds kill it by shutting it down
once you see it started to submit jobs from the code you just uncommented, like:

> intercepting for job action triggering and setting node id :
> f0920c5b-3655–4e85-aa60-f763a9eb1111<br> Executing computation logic for the
request0Key

4- you will see in the first still running node a message that highlight it
received and event about the removal of the second node which from it , it will
fetch the node id , then insert it on the failed nodes cache where its cache
interceptor will intercept the after put action , use the node id and query in
jobs cache for still pending jobs that has the same node id and resubmit them
again for execution in the compute grid and here we are happy that we caught the
non finished jobs from the failed crashed primary node that submitted those jobs

> Received Node event [evt=NODE_LEFT, nodeID=TcpDiscoveryNode
> [id=2da3e806–72e3–415b-acd3–07b7da0eabe0, addrs=[0:0:0:0:0:0:0:1%lo0, 127.0.0.1,
192.168.1.169], sockAddrs=[/192.168.1.169:47501, /0:0:0:0:0:0:0:1%lo0:47501,
/127.0.0.1:47501], discPort=47501, order=2, intOrder=2,
lastExchangeTime=1510666504589, loc=false, ver=2.3.1#20171031-sha1:d2c82c3c,
isClient=false]]

and you will see it is fetching pending jobs and submitting it again, for
example you will see the following in the IDEA console:

> found a pending jobs for node id: c2a32b7d-1420–4e1a-8ca2-b7080e91dc22 and job
> id: 19Key<br> Executing the expiry post action for the request19Key

<br> 

#### **References :**

* Apache Ignite :
[https://apacheignite.readme.io/docs](https://apacheignite.readme.io/docs)

<br> 
