/*
 * Copyright (c) 2003-2015 Tugdual Grall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grallandco.impl;

import com.couchbase.capi.CouchbaseBehavior;
import com.grallandco.MongoDBCouchbaseReplicator;
import com.grallandco.util.MongoConnectionManager;

import java.util.*;

public class MongoCouchbaseBehavior implements CouchbaseBehavior {

    // UUID used for the registration of the node to Couchbase
    String poolUUID = null;
    String bucketUUid = null;


    public List<String> getPools() {
        List<String> result = new ArrayList<String>();
        result.add("default"); // keep the default pool
        return result;
    }

    public String getPoolUUID(String pool) {
        // TODO : improve this for production if needed
        return "00000000000000000000000000000000";
    }

    public Map<String, Object> getPoolDetails(String pool) {
        Map<String, Object> bucket = new HashMap<String, Object>();
        bucket.put("uri", "/pools/" + pool + "/buckets?uuid=" + getPoolUUID(pool));

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("buckets", bucket);

        List<Map<String, Object>> nodes = getNodesServingPool(pool);
        responseMap.put("nodes", nodes);
        return responseMap;
    }

    /**
     * This methods will return the list of MongoDB Databases
     * The "default" database will always be OK (for demonstration purpose)
     *
     * The proper way will be to create a new DB into MongoDB
     *
     * @param pool
     * @return
     */
    public List<String> getBucketsInPool(String pool) {
        List<String> bucketNameList = new ArrayList<String>();
        bucketNameList.add("default");
        // lookup mongodb databases list
        bucketNameList.addAll(MongoConnectionManager.getMongoClient().getDatabaseNames());
        return bucketNameList;
    }

    public String getBucketUUID(String pool, String bucket) {
        // TODO : improve this for production if needed
        return "00000000000000000000000000000000";
    }

    /**
     * Return the list of information for the pool
     *
     * @param pool name of the pool (only "default" is supported)
     * @return the information about the node
     */
    public List<Map<String, Object>>  getNodesServingPool(String pool) {
        List<Map<String, Object>> nodes = null;
        if ("default".equals(pool)) {
            nodes = new ArrayList<Map<String, Object>>();
            Map<String, Object> nodePorts = new HashMap<String, Object>();
            nodePorts.put("direct", MongoDBCouchbaseReplicator.serverPort);
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("couchApiBase", String.format("http://%s:%s/", MongoDBCouchbaseReplicator.serverBinding, MongoDBCouchbaseReplicator.serverPort));
            node.put("hostname", String.format("%s:%s", MongoDBCouchbaseReplicator.serverBinding, MongoDBCouchbaseReplicator.serverPort));
            node.put("ports", nodePorts);
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * Not used
     * @return empty map
     */
    public Map<String, Object> getStats() {
        return new HashMap<String, Object>();
    }

}
