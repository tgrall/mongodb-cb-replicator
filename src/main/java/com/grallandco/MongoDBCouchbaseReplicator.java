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

package com.grallandco;

import com.couchbase.capi.CAPIBehavior;
import com.couchbase.capi.CAPIServer;
import com.couchbase.capi.CouchbaseBehavior;
import com.grallandco.impl.MongoCAPIBehavior;
import com.grallandco.impl.MongoCouchbaseBehavior;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class MongoDBCouchbaseReplicator {

    private static final String MONGO_URI                   = "mongodb.uri";

    // Which character to replace . and $ in json key name (no allowed by MongoDB)
    private static final String MONGO_DOT_STRING            = "mongodb.dot_string";
    private static final String MONGO_DOLLAR_STRING         = "mongodb.dollar_string";

    private static final String SERVER_BINDING              = "server.binding";
    private static final String SERVER_PORT                 = "server.port";
    private static final String SERVER_DEFAULT_COLLECTION   = "server.default_collection";
    private static final String SERVER_COLLECTION_FIELD     = "server.collection_field";
    private static final String SERVER_KEEP_META            = "server.keep_meta";
    private static final String SERVER_VBUCKET_NUMBER       = "server.vbuckets_number";

    private static final String SERVER_USERNAME     = "server.username";
    private static final String SERVER_PASSWORD     = "server.password";


    public static String    mongoUri           = "localhost:27017";

    public static String    dotReplacement     = "_";
    public static String    dollarReplacement     = "-";

    public static String    serverBinding      = "127.0.0.1";
    public static int       serverPort         = 8017;
    public static String    defaultCollection  = "couchbase_data";
    public static String    collectionField    = "type";
    public static boolean   keepMeta           = false;
    public static int       vBucketNumber      = 1024;

    public static String username           = "admin";
    public static String password           = "admin";




    public static void main(String[] args) throws Exception {

        System.out.println("\n\n");
        System.out.println("############################################");
        System.out.println("#         MONGODB CAPI SERVER              #");
        System.out.println("############################################\n\n");
        System.out.println("\n\n");



        if (args != null && args.length != 0) {
            setup(args[0]);
        }

        System.out.println(new InetSocketAddress(serverBinding, serverPort));


        CouchbaseBehavior couchbaseBehavior = new MongoCouchbaseBehavior();
        CAPIBehavior capiBehavior = new MongoCAPIBehavior();

        CAPIServer capiServer = new CAPIServer(
                    capiBehavior,
                    couchbaseBehavior,
                    new InetSocketAddress(serverBinding, serverPort) ,
                    username,
                    password,
                    vBucketNumber);

        capiServer.start();




    }

    /**
     * Load configuration
     * @param fileName
     */
    private static void setup(String fileName) {

        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(fileName));


            if (prop.containsKey(MONGO_URI)) {
                mongoUri = prop.getProperty(MONGO_URI);
            }

            if (prop.containsKey(MONGO_DOT_STRING)) {
                dotReplacement = prop.getProperty(MONGO_DOT_STRING);
            }

            if (prop.containsKey(MONGO_DOLLAR_STRING)) {
                dotReplacement = prop.getProperty(MONGO_DOLLAR_STRING);
            }

            if (prop.containsKey(SERVER_BINDING)) {
                serverBinding = prop.getProperty(SERVER_BINDING);
            }

            if (prop.containsKey(SERVER_PORT)) {
                serverPort = Integer.parseInt(prop.getProperty(SERVER_PORT));
            }

            if (prop.containsKey(SERVER_DEFAULT_COLLECTION)) {
                defaultCollection = prop.getProperty(SERVER_DEFAULT_COLLECTION);
            }

            if (prop.containsKey(SERVER_COLLECTION_FIELD)) {
                collectionField = prop.getProperty(SERVER_COLLECTION_FIELD);
            }

            if (prop.containsKey(SERVER_KEEP_META)) {
                keepMeta = Boolean.parseBoolean(prop.getProperty(SERVER_KEEP_META));
            }

            if (prop.containsKey(SERVER_VBUCKET_NUMBER)) {
                vBucketNumber = Integer.parseInt( prop.getProperty(SERVER_VBUCKET_NUMBER) );
            }

            if (prop.containsKey(SERVER_USERNAME)) {
                username = prop.getProperty(SERVER_USERNAME);
            }

            if (prop.containsKey(SERVER_PASSWORD)) {
                password = prop.getProperty( SERVER_PASSWORD );
            }





        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n\n");
            System.exit(0);
        }


    }

}
