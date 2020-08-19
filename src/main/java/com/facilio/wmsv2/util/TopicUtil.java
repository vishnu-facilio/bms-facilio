package com.facilio.wmsv2.util;

public class TopicUtil {

    private static final String pattern = "[a-zA-Z_][a-zA-Z0-9_]*";

    public static boolean matchTopic(String topic, String... patterns) {
        for (String pattern : patterns) {
            String[] patternSplit = pattern.split("/");
            String[] topicSplit = topic.split("/");

            if (patternSplit.length > topicSplit.length) {
                // topic split cannot be less than patten
                continue;
            }

            boolean continueWithOtherPattern = false;
            for (int i = 0; i < patternSplit.length; i++) {
                String p = patternSplit[i];
                if (p.length() == 1) {
                    if (p.equals("+")) {
                        continue;
                    } else if (p.equals("#")) {
                        // if we encounter #, then we have found the match
                        return true;
                    }
                }

                String t = topicSplit[i];
                if (!(p.equals(t))) {
                    continueWithOtherPattern = true;
                    break;
                }
            }
            if (continueWithOtherPattern) {
                continue;
            }

            // match found
            return true;
        }
        return false;
    }

    public static boolean validateTopic(String topic) {
        return validateTopic(topic, false);
    }

    public static boolean validateTopic(String topic, boolean checkWildcard) {
        String[] split = topic.split("/");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (checkWildcard) {
                if (s.length() == 1) {
                    if (s.equals("+")) {
                        continue;
                    } else if (s.equals("#")) {
                        if (i < (split.length - 1)) {   // # should be the last
                            return false;
                        }
                        continue;
                    }
                }
            }

            if (!(s.matches(pattern))) {
                return false;
            }
        }
        return true;
    }
}
