package com.facilio.wmsv2.constants;

public interface Topics {

    String[] defaultSubscribedTopics = {System.ping};

    interface System {
        String broadcast = "__broadcast__";
        String subscribe = "__subscribe__";
        String unsubscribe = "__unsubscribe__";
        String ping = "__ping__";
        String livereading = "__livereading__";
        String agentpoints = "__agentpoints__";
        String auditLogs = "__audit_logs__";
        String customUser = "__custom__/user";
        String customOrg = "__custom__/org";
        String customApp = "__custom__/app";

        String pmPlanner = "pm_planner/+/execute";
    }

    interface Push {
        String push = "__push__";
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
}
