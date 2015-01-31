# MongoDB CB Data Replicator

MongoDB CB Data Replicator is a HTTP server that responds to Couchbase Replication request (XDCR) and save mutations
into a MongoDB Database.

It is the "implementation" of this tweet"

<blockquote class="twitter-tweet" lang="en"><p>Moving my Java from Couchbase to MongoDB <a href="http://t.co/Wnn3pXfMGi">pic.twitter.com/Wnn3pXfMGi</a></p>&mdash; Tugdual Grall (@tgrall) <a href="https://twitter.com/tgrall/status/559664540041117696">January 26, 2015</a></blockquote>
<script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>


## Installation

For now you have to build it from sources to run it:

1. Clone and Build the CAPI Server

```
git clone https://github.com/couchbaselabs/couchbase-capi-server.git

cd couchbase-capi-server

mvn clean install
```

2. Clone this repository

Then Run the "MongoDBCouchbaseReplicator class