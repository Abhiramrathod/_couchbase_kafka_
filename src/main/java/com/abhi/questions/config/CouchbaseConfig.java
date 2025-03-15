package com.abhi.questions.config;

import com.abhi.questions.couchbase.CouchbaseChangeListener;
import com.abhi.questions.couchbase.CouchbaseUtils;
import com.abhi.questions.kafka.KafkaProducerService;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchbaseConfig {

    @Bean
    public CouchbaseUtils couchbaseUtils() {
        return new CouchbaseUtils();
    }

    @Bean
    public Cluster couchbaseCluster(CouchbaseUtils couchbaseUtils) {
        return couchbaseUtils.createCluster();
    }

    @Bean
    public Bucket couchbaseBucket(CouchbaseUtils couchbaseUtils, Cluster cluster) {
        return couchbaseUtils.getBucket(cluster);
    }

    @Bean
    public Scope couchbaseScope(CouchbaseUtils couchbaseUtils, Bucket bucket) {
        return couchbaseUtils.getScope(bucket);
    }

    @Bean
    public Collection couchbaseCollection(CouchbaseUtils couchbaseUtils, Bucket bucket) {
        return couchbaseUtils.getCollection(bucket);
    }
}