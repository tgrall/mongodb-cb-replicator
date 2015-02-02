# MongoDB CB Data Replicator

MongoDB CB Data Replicator is a HTTP server that responds to Couchbase Replication request (XDCR) and save mutations
into a MongoDB Database.

It is the "implementation" of this tweet"

<blockquote class="twitter-tweet" lang="en"><p>Moving my Java from Couchbase to MongoDB <a href="http://t.co/Wnn3pXfMGi">pic.twitter.com/Wnn3pXfMGi</a></p>&mdash; Tugdual Grall (@tgrall) <a href="https://twitter.com/tgrall/status/559664540041117696">January 26, 2015</a></blockquote>
<script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>


## Installation

#### Java Application

Download the [MongoCBReplicator.jar](http://goo.gl/WkuHBk) file.

You can configure the server using a simple properties file:

```
## MONGODB CONFIGURATION ##

## Comma separated list of MongoDB Servers
mongodb.uri=localhost:27017

# Since MongoDB does now allowed . and $ signs
# into the field names, these will be used instead
# see http://docs.mongodb.org/manual/faq/developers/#faq-dollar-sign-escaping
mongodb.dot_string=_
mongodb.dollar_string=-

## REPLICATOR INFORMATION ##

# Server TCP Binding
server.binding=localhost

## HTTP Port used for XDRC configuration
server.port=8017

# If insert_only, only new documents are pushed to MongoDB, updates are ignored
# If all : insert and updates are sent to MongoDB (document is "replaced")
server.replication_type=insert_only

# name of the collection if documents are not typed in Couchbase
server.default_collection=couchbase_data

# name of the field that described the type of document, used to create new collections
server.collection_field=type

# if yes the CB metadata will be stored into Documents
server.keep_meta=false

# number of vBuckets (keep default except if you are running CB on OSX it should be 64)
server.vbuckets_number=1024

# username for XDCR registration
server.username=admin

# password for XDCR registration
server.password=admin

```


Start the server
```
java -jar MongoCBReplicator.jar [conf.properties]
```

MongoDB CB Replicator is now up and running,



Once the server is started you can now configure Couchbase XDCR to send the data into MongoDB:

1- Create a new "XDCR Cluster Reference"

1. Enter "MongoDB" in the cluster name field
1. Enter the host and port of the MongoDB Replicator Server for example localhost:8017
1. Enter the username and password of the replicator (default being admin/admin)
1. Click Save

2- Create the Replicatin

1. Select "MongoDB" as *remote cluster
1. Select the source bucket
1. Enter the name of the mongodb database for the target bucket. Note: this database MUST exists in MongoDB (Replicator is checking the list of existing db)
1. Click on "Advanced Settings" and select the Version 1 of the XDCR Protocol
    * This tools is based on the CAPI Server Library that supports XDCR Protocol 1.0

3- Replication is starting, all data will be pushed from Couchbase to MongoDB


### Limits

* Deletes are not replicated
* Updates have been tested on very simple use cases
* Document sizes are not managed, if Couchbase sends a document bigger than 16Mb an exception is raised.
* If the server raised the message "Attention - Replication to the bucket with different number of vbuckets is disallowed",
  check the value of the `server.vbuckets_number` (1024 most of the time, 64 if CB is running on OSX)
* If the server raised the message "Attention - Bucket '...' not found", create the database/namespace into MongoDB


## Demonstration

[![Couchbase to MongoDB Migration](http://img.youtube.com/vi/Fpl74Z0HbC0/0.jpg)](https://www.youtube.com/watch?v=Fpl74Z0HbC0)


## From Sources

To build and run this project you need to:

Clone and Build the CAPI Server

```
git clone https://github.com/couchbaselabs/couchbase-capi-server.git

cd couchbase-capi-server

mvn clean install

```
