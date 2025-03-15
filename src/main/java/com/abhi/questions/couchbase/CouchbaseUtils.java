package com.abhi.questions.couchbase;

import com.couchbase.client.core.env.TimeoutConfig;
import com.couchbase.client.core.service.ServiceType;
import com.couchbase.client.java.*;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;

import java.time.Duration;


public class CouchbaseUtils {

    @Value("${spring.couchbase.connection-string}")
    private String connectionString;

    @Value("${spring.couchbase.username}")
    private String username;

    @Value("${spring.couchbase.password}")
    private String password;

    @Value("${spring.couchbase.bucket-name}")
    private String bucketName;

    @Value("${spring.couchbase.scope-name}")
    private String scopeName;

    @Value("${spring.couchbase.collection-name}")
    private String collectionName;

    Pair<String, Boolean> clusterCheck = Pair.of("", false);

    @PostConstruct
    public void init() {
        Cluster cluster = createCluster();
        createBucketScopeCollectionIfNotExist(cluster);
    }

    public Cluster createCluster() {
        ClusterEnvironment environment = ClusterEnvironment.builder()
                .ioConfig(ioConfig ->
                        ioConfig.captureTraffic(ServiceType.MANAGER))
                .timeoutConfig(TimeoutConfig.kvTimeout(Duration.ofSeconds(20))
                        .queryTimeout(Duration.ofSeconds(50))
                        .connectTimeout(Duration.ofSeconds(60)))
                .build();
        return Cluster.connect(connectionString, ClusterOptions.clusterOptions(username, password).environment(environment));
    }


    public Bucket getBucket(Cluster cluster) {
        return cluster.bucket(bucketName);
    }

    public Scope getScope(Bucket bucket) {
        return bucket.scope(scopeName);
    }

    public Collection getCollection(Bucket bucket) {
        return bucket.scope(scopeName).collection(collectionName);
    }

    public void createBucketScopeCollectionIfNotExist(Cluster cluster) {
        BucketManager bucketManager = cluster.buckets();
        boolean bucketExists = bucketManager.getAllBuckets().containsKey(bucketName);
        if (!bucketExists) {
            bucketManager.createBucket(BucketSettings.create(bucketName).ramQuotaMB(100));
            Bucket bucket = cluster.bucket(bucketName);
            bucket.collections().createScope(scopeName);
            bucket.collections().createCollection(CollectionSpec.create(collectionName, scopeName));
            clusterCheck = Pair.of(bucketName, true);
        } else {
            Bucket bucket = cluster.bucket(bucketName);
            boolean scopeExists = bucket.collections().getAllScopes().stream()
                    .anyMatch(scope -> scope.name().equals(scopeName));
            if (scopeExists) {
                boolean collectionExists = bucket.collections().getAllScopes().stream()
                        .filter(scope -> scope.name().equals(scopeName))
                        .flatMap(scope -> scope.collections().stream())
                        .anyMatch(collection -> collection.name().equals(collectionName));
                if (!collectionExists) {
                    bucket.collections().createCollection(CollectionSpec.create(collectionName, scopeName));
                    clusterCheck = Pair.of(collectionName, true);
                }
            } else {
                bucket.collections().createScope(scopeName);
                bucket.collections().createCollection(CollectionSpec.create(collectionName, scopeName));
                clusterCheck = Pair.of(scopeName, true);
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        Cluster cluster = createCluster();
        if (clusterCheck == null) {
            cluster.disconnect();
        } else {
            BucketManager bucketManager = cluster.buckets();
            if (clusterCheck.getFirst().equals(bucketName)) {
                if (bucketManager.getAllBuckets().containsKey(bucketName)) {
                    bucketManager.dropBucket(bucketName);
                }
            } else {
                Bucket bucket = cluster.bucket(bucketName);
                if (clusterCheck.getFirst().equals(scopeName)) {
                    bucket.collections().dropScope(scopeName);
                } else if (clusterCheck.getFirst().equals(collectionName)) {
                    bucket.collections().dropCollection(CollectionSpec.create(collectionName, scopeName));
                }
            }
        }

        cluster.disconnect();
    }
}
