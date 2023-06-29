package com.facilio.util;

import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class RedisSubscribeTopics {
    private static List<String> topics = new ArrayList<>();
    public static void init() throws IOException {
        try {
            File file = FacilioUtil.getConfFilePath("conf/redistopics.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> subscribeTopics = new ArrayList<>();
            String topic;
            while ((topic = reader.readLine()) != null) {
                subscribeTopics.add(topic);
            }
            topics = subscribeTopics;
            reader.close();
        }
        catch (Exception e) {
            LOGGER.info(e);
        }
    }
    public static List<String> getAllTopics() {
        return topics;
    }
}