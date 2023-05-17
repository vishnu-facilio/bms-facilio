package com.facilio.wmsv2.constants;

public interface Topics {

    String[] defaultSubscribedTopics = {System.ping};

    interface System {
        String subscribe = "__subscribe__";
        String unsubscribe = "__unsubscribe__";
        String ping = "__ping__";
        String auditLogs = "__audit_logs__";
    }

    interface InApp{
        String inApp = "__inApp__";
    }

    interface Mail {
        String outgoingMail = "__sendmail__/org";
        String prepareOutgoingMail = "__prepare_ogmail__/org";
        String mailResponse = "__mailresponse__";
    }
    interface PushNotification {
        String pushNotification = "__pushnotification__";
    }

    interface Tasks {
        String longRunnningTasks = "__longrunning_tasks__";
    }
    interface MultiImport {
        String multiImport = "__multi_import__";
        String multiImportErrorRecords = "__multi_import_error_records__";
    }

    interface UpdateOnOfflineRecord{
        String updateOnOfflineRecord = "__update_on_offline_record__";
    }
}
