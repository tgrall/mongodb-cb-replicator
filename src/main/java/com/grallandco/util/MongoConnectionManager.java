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

package com.grallandco.util;

import com.grallandco.MongoDBCouchbaseReplicator;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the singleton used to manage the connection to the MongoDB Cluster
 * It takes the connection string from the properties file you pass as parameter to the server
 *
 */
public class MongoConnectionManager {
    private static final Logger logger = Logger.getLogger(MongoConnectionManager.class.getName());

    static  MongoClient client = null;

    /**
     * Get the MongoDB client
     * @return
     */
    public static MongoClient getMongoClient() {
        if (client == null) {

            // get the URU list form the configuration
            String mongoURIs = MongoDBCouchbaseReplicator.mongoUri;
            List<String> list = Arrays.asList( mongoURIs.split(",") );

            List<ServerAddress> addressList = new ArrayList<ServerAddress>();

            try {
            for (String addr : list) {
                    addressList.add( new ServerAddress(addr)   );
            }
            client = new MongoClient(addressList);

            } catch (RuntimeException e) {
              logger.log(Level.SEVERE, e.getMessage());
              System.exit(1);
            }

            logger.log(Level.INFO, "MongoDB Replicator Connected to "+ addressList);

        }
        return client;
    }


}
