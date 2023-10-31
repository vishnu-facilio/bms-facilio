package com.facilio.wmsv2.constants;

public interface Topics {

    String[] defaultSubscribedTopics = {System.ping, System.subscribe, System.unsubscribe, System.push};

    interface System {
        String subscribe = "__subscribe__";
        String unsubscribe = "__unsubscribe__";
        String ping = "__ping__";
        String push = "__push__";
    }

    interface InApp {
        String inApp = "__inApp__";
    }

    interface MultiImport {
        String multiImport = "__multi_import__";
        String multiImportErrorRecords = "__multi_import_error_records__";
    }
    interface FlaggedEventCreation {
        String flaggedEventTopic = "__flaggedEvent__";
    }

    interface ControllerOffline {
        String controllerOfflineTopic = "controller__offline__alarm__";
    }
}
