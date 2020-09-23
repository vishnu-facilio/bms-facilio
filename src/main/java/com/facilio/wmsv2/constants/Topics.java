package com.facilio.wmsv2.constants;

public interface Topics {

    String[] defaultSubscribedTopics = {System.ping};

    interface System {
        String broadcast = "__broadcast__";
        String subscribe = "__subscribe__";
        String unsubscribe = "__unsubscribe__";
        String ping = "__ping__";
        String livereading = "__livereading__";
    }

    interface Push {
        String push = "__push__";
    }
}
