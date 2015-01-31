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

import com.couchbase.capi.CAPIBehavior;
import com.grallandco.MongoDBCouchbaseReplicator;
import com.grallandco.util.MongoConnectionManager;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class received all the changes from Couchbase and manage the diffs to insert them
 * into mongodb
 */
public class MongoCAPIBehavior implements CAPIBehavior {

    private static final Logger logger = Logger.getLogger(MongoCAPIBehavior.class.getName());


    @Override
    public Map<String, Object> welcome() {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("welcome", "monogdb-capi-server");
        return responseMap;
    }

    @Override
    public String databaseExists(String database) {
        return getBucketUUIDFromDatabase(database);
    }


    @Override
    public Map<String, Object> getDatabaseDetails(String database) {
        logger.log(Level.INFO, "MongoCAPIBehavior.getDatabaseDetails()");
        String doesNotExistReason = databaseExists(database);
        if (doesNotExistReason == null) {
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put("db_name", database);
            return responseMap;
        }
        return null;
    }

    @Override
    public boolean createDatabase(String database) {
        throw new UnsupportedOperationException("Creating databases is not supported");
    }

    @Override
    public boolean deleteDatabase(String database) {
        throw new UnsupportedOperationException("Deleting databases is not supported");
    }

    @Override
    public boolean ensureFullCommit(String database) {
        return true;
    }

    @Override
    public Map<String, Object> revsDiff(String database,
                                        Map<String, Object> revsMap) {
        logger.log(Level.INFO, "MongoCAPIBehavior.revsDiff()");
            Map<String, Object> responseMap = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : revsMap.entrySet()) {
                String id = entry.getKey();
                Object revs = entry.getValue();
                Map<String, Object> rev = new HashMap<String, Object>();
                rev.put("missing", revs);
                responseMap.put(id, rev);
            }
            return responseMap;
    }

    /**
     * Load the documents into MongoDB
     * @param database
     * @param docs
     * @return
     */
    @Override
    public List<Object> bulkDocs(String database, List<Map<String, Object>> docs) {

        DB db = MongoConnectionManager.getMongoClient().getDB(getDatabaseName(database));
        List<Object> result = new ArrayList<Object>();




        for (Map<String, Object> doc : docs) {
            Map<String, Object> meta = (Map<String, Object>) doc.get("meta");
            Map<String, Object> json = (Map<String, Object>) doc.get("json");
            String base64 = (String) doc.get("base64");

            if (meta == null) {
                // if there is no meta-data section, there is nothing we can do
                logger.log( Level.WARNING,  "Document without meta in bulk_docs, ignoring....");
                continue;
            } else if("non-JSON mode".equals(meta.get("att_reason")) || "invalid_json".equals(meta.get("att_reason"))) {
                // optimization, this tells us the body isn't json
                json = new HashMap<String, Object>();
            } else if(json == null && base64 != null) {

                // use Java 6/7 XML Base64 library
                // TODO : see if it makes sense to use Java8 Library java.util.Base64
                String jsonValue = new String(DatatypeConverter.parseBase64Binary( base64 ));
                DBObject o = (DBObject) JSON.parse( jsonValue );
                DBObject mongoJson = BasicDBObjectBuilder.start("_id", meta.get("id")).get();

                // need to check if json keys do not contains . or $ and replace them with other char
                // TODO : Copy the doc, put _id at the top and clean key names
                Set<String> keys = o.keySet();
                for ( String key : keys ) {
                    String newKey = key;
                    newKey = newKey.replace(".", MongoDBCouchbaseReplicator.dotReplacement  );
                    newKey = newKey.replace("$", MongoDBCouchbaseReplicator.dollarReplacement );
                    mongoJson.put( newKey, o.get(key) );
                }

                // add meta data if configured
                if (MongoDBCouchbaseReplicator.keepMeta) {
                    mongoJson.put("meta", new BasicDBObject(meta));
                }

                String collectionName = MongoDBCouchbaseReplicator.defaultCollection;
                if (o.get(MongoDBCouchbaseReplicator.collectionField ) != null) {
                    collectionName = (String)o.get( MongoDBCouchbaseReplicator.collectionField );
                }

                System.out.println("DDEMO");

                try {
                    if (meta.containsKey("deleted")) {
                        db.getCollection(collectionName).remove(BasicDBObjectBuilder.start("_id", meta.get("id")).get());
                    } else {
                        db.getCollection(collectionName).save(mongoJson);
                    }
                } catch (Exception e) {
                    System.out.println( e.getMessage() );
                }

            }

            String id = (String) meta.get("id");
            String rev = (String) meta.get("rev");
            Map<String, Object> itemResponse = new HashMap<String, Object>();
            itemResponse.put("id", id);
            itemResponse.put("rev", rev);
            result.add(itemResponse);
        }

        return result;
    }

    @Override
    public Map<String, Object> getDocument(String database, String docId) {
        logger.log(Level.INFO, "MongoCAPIBehavior.getDocument()");

        if ("default".equals(database)) {
            if ("docid".equals(docId)) {
                Map<String, Object> document = new HashMap<String, Object>();
                document.put("_id", "docid");
                document.put("_rev", "1-abc");
                document.put("value", "test");
                return document;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getLocalDocument(String database, String docId) {
        logger.log(Level.INFO, "MongoCAPIBehavior.getLocalDocument()");

        return null;
    }

    @Override
    public String storeDocument(String s, String s1, Map<String, Object> map) {
        System.out.println("MongoCAPIBehavior.storeDocument()");

        return null;
    }

    @Override
    public String storeLocalDocument(String s, String s1, Map<String, Object> map) {
        System.out.println("MongoCAPIBehavior.storeDocument()");

        return null;
    }


    @Override
    public InputStream getAttachment(String database, String docId,
                                     String attachmentName) {
        throw new UnsupportedOperationException("Attachments are not supported");
    }

    @Override
    public String storeAttachment(String database, String docId,
                                  String attachmentName, String contentType, InputStream input) {
        throw new UnsupportedOperationException("Attachments are not supported");
    }

    @Override
    public InputStream getLocalAttachment(String databsae, String docId,
                                          String attachmentName) {
        throw new UnsupportedOperationException("Attachments are not supported");
    }

    @Override
    public String storeLocalAttachment(String database, String docId,
                                       String attachmentName, String contentType, InputStream input) {
        throw new UnsupportedOperationException("Attachments are not supported");
    }

    /**
     * Not used for this connector
     * @return
     */
    public Map<String, Object> getStats() {
        return null;
    }

    @Override
    public String getVBucketUUID(String pool, String bucket, int vbucket) {
        // TODO : improve this for production if needed
        return "00000000000000000000000000000000";
    }

    @Override
    public String getBucketUUID(String pool, String bucket) {
        // TODO : improve this for production if needed
        return "00000000000000000000000000000000";
    }


    /**
     * Extract the database from the bucket ID
     * @param database
     * @return
     */
    protected String getDatabaseName(String database) {
        return database.split(";")[0].split("/")[0];
    }


    /**
     * Exctract the bucket from the UUID
     * @param database
     * @return
     */
    protected String getBucketUUIDFromDatabase(String database) {
        String[] pieces = database.split(";", 2);
        if (pieces.length < 2) {
            return null;
        } else {
            return pieces[0];
        }
    }


}
