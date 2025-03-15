package com.abhi.questions.couchbase.constants;

public final class Constants {
    public static final String EVENT = "event";
    public static final String ID = "id";
    public static final String DATA = "data";
    public static final String KFK_SUCCESS = "message sent to the Kafka";

    public static final String QUERY_GET_ALL = "SELECT * FROM `questions`";
    public static final String QUERY_GET_BY_ID = "SELECT * FROM `questions` WHERE id = $1";

    private Constants() {
    }

}
