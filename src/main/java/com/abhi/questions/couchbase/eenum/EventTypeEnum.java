package com.abhi.questions.couchbase.eenum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EventTypeEnum {
        INSERT, UPDATE, DELETE;

        private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeEnum.class);

        public static EventTypeEnum fromValue(String value) {
            if(value != null && value.isEmpty()) {
                LOGGER.error("No Event type is Empty");
                System.exit(0);
            }
            for (EventTypeEnum type : values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            LOGGER.error("No Event type found for value: {}", value);
            System.exit(0);
            return null;
        }
}
