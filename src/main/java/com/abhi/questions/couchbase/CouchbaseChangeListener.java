package com.abhi.questions.couchbase;


import com.abhi.questions.kafka.KafkaProducerService;
import com.abhi.questions.model.Question;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static com.abhi.questions.couchbase.constants.Constants.EVENT;
import static com.abhi.questions.couchbase.eenum.EventTypeEnum.INSERT;
import static com.abhi.questions.couchbase.eenum.EventTypeEnum.UPDATE;
import static com.abhi.questions.couchbase.eenum.EventTypeEnum.DELETE;
import static com.abhi.questions.couchbase.constants.Constants.ID;
import static com.abhi.questions.couchbase.constants.Constants.DATA;
import static com.abhi.questions.couchbase.constants.Constants.KFK_SUCCESS;

public class CouchbaseChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseChangeListener.class);


    private final KafkaProducerService kafkaProducer;
    private final Cluster cluster;
    private final Bucket bucket;
    private final Scope scope;
    private final Collection collection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CouchbaseChangeListener(CouchbaseUtils couchbaseUtils, KafkaProducerService kafkaProducer) {
        this.cluster = couchbaseUtils.createCluster();
        this.bucket = couchbaseUtils.getBucket(cluster);
        this.scope = couchbaseUtils.getScope(bucket);
        this.collection = couchbaseUtils.getCollection(bucket);
        this.kafkaProducer = kafkaProducer;
    }


    public void insertDocument(String id, Object data) {
        try {
            MutationResult result = collection.insert(id, data);
            String message = objectMapper.writeValueAsString(Map.of(
                    EVENT, INSERT.name(),
                    ID, id,
                    DATA, data
            ));
            logger.info("Doc inserted result in couchbase{}", result);
            kafkaProducer.sendMessage(message);
            logger.info(KFK_SUCCESS + " after inserting the doc with docId {} in couchbase", id);
        } catch (Exception e) {
            logger.error("Error inserting document with docId {} : {} ", id, e.getMessage(), e);
        }
    }

    public void updateDocument(String id, Object data) {
        try {
            MutationResult result = collection.replace(id, data);
            String message = objectMapper.writeValueAsString(Map.of(
                    EVENT, UPDATE.name(),
                    ID, id,
                    DATA, data
            ));
            logger.info("result updated in couchbase{}", result);
            kafkaProducer.sendMessage(message);
            logger.info(KFK_SUCCESS + " after updating the doc with docId {} in couchbase", id);
        } catch (Exception e) {
            logger.error("Error updating document with docId {} : {} ", id, e.getMessage(), e);
        }
    }

    public void deleteDocument(String id) {
        try {
            collection.remove(id);
            String message = objectMapper.writeValueAsString(Map.of(
                    EVENT, DELETE.name(),
                    ID, id
            ));

            kafkaProducer.sendMessage(message);
            logger.info(KFK_SUCCESS + " after deleting the doc with docId {} from couchbase", id);
        } catch (Exception e) {
            logger.error("Error deleting document with docId {} : {} ", id, e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Question readDocument(int id) {
        try {
            String query = "SELECT * FROM `" + bucket.name() + "`.`" + scope.name() + "`.`" + collection.name() + "` WHERE META().id = \"" + id + "\"";
            QueryResult result = cluster.query(query);
            List<Object> rows = result.rowsAs(Object.class);
            if (rows != null && !rows.isEmpty()) {
                LinkedHashMap<String, Object> row = (LinkedHashMap<String, Object>) rows.get(0);
                String jsonDoc = objectMapper.writeValueAsString(row.get(collection.name()));
                return objectMapper.readValue(jsonDoc, Question.class);
            }
        } catch (Exception e) {
            logger.error("Error reading document with docId {} : {} ", id, e.getMessage(), e);
        }
        return null;
    }

    public List<Question> readAllDocuments() {
        try {
            List<Question> questions = new ArrayList<>();
            String query = "SELECT * FROM `" + bucket.name() + "`." + scope.name() + "." + collection.name();
            List<JsonObject> result = cluster.query(query).rowsAsObject();
            if (result != null && !result.isEmpty()) {
                for (JsonObject row : result) {
                    String jsonDoc = row.get(collection.name()).toString();
                    Question question = objectMapper.readValue(jsonDoc, Question.class);
                    questions.add(question);
                }
            }
            return questions;
        } catch (Exception e) {
            logger.error("Error reading all documents : {} ", e.getMessage(), e);
        }
        return new ArrayList<>();
    }
}