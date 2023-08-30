package com.facilio.aws.util;

import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.model.*;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class AwsPolicyUtils {
    private static final Logger LOGGER = LogManager.getLogger(AwsPolicyUtils.class.getName());
    public static final String TOPIC = ":topic/";
    public static final String CLIENT = ":client/";
    public static final String TOPICFILTER = ":topicfilter/";

    private static void addClientToPolicy(JSONObject policy, List<String> connectTopics) throws Exception {
        updatePolicyStatement(policy, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), connectTopics);
    }

    private static JSONObject getGist(JSONObject policy) throws Exception {
        if (policy.containsKey("Statement") && (policy.get("Statement") != null)) {
            JSONArray policyStatements = (JSONArray) policy.get("Statement");
            JSONObject policyGist = new JSONObject();
            for (Object policyStatementObject : policyStatements) {
                JSONObject policyStatement = (JSONObject) policyStatementObject;
                if ((policyStatement.containsKey("Action")) && (policyStatement.get("Action") != null)) {
                    String action = (String) policyStatement.get("Action");
                    policyGist.put(action, getTopicsFromArns(getResourcesList(policyStatement)));
                } else {
                    LOGGER.info("action not found");
                }
            }
            return policyGist;
        } else {
            throw new Exception("statement missing from polictdoc");
        }
    }

    private static void updatePolicyStatement(JSONObject policy, List<String> subscribeTopics, List<String> publishTopics, List<String> receiveTopics, List<String> connectTopics) throws Exception {
        if (policy.containsKey("Statement") && (policy.get("Statement") != null)) {
            JSONArray policyStatements = (JSONArray) policy.get("Statement");
            for (Object policyStatementObject : policyStatements) {
                JSONObject policyStatement = (JSONObject) policyStatementObject;
                if ((policyStatement.containsKey("Action")) && (policyStatement.get("Action") != null)) {
                    String action = (String) policyStatement.get("Action");
                    switch (action) {
                        case "iot:Connect":
                            addResourceToPolicyIfNotPresent(policyStatement, connectTopics);
                            break;
                            /*case "iot:Publish":
                                addResourceToPolicyIfNotPresent(policyStatement,publishTopics);
                                break;
                            case "iot:Receive":
                                addResourceToPolicyIfNotPresent(policyStatement,receiveTopics);
                                break;
                            case "iot:Subscribe":
                                addResourceToPolicyIfNotPresent(policyStatement,subscribeTopics);
                                break;*/
                    }
                } else {
                    throw new Exception("key -Action- missing from policyJson ");
                }
            } // for ends
        } else {
            throw new Exception(" key -Statement- missing from policyJson ");
        }
    }

    private static String updatePolicy(AWSIot client, String policyName, IotPolicy rule) throws Exception {
        LOGGER.info("Updating policy " + policyName);
        JSONObject policy = getPolicyDocumentJson(getPolicy(policyName, client));
        LOGGER.info("Existing policy document " + policy);
        List<String> connectTopics = rule.getArnClientIds();
        addClientToPolicy(policy, connectTopics);
        LOGGER.info("Updated policy document " + policy);
        updateClientPolicyVersion(client, policyName, policy);
        return policy.toString();
    }

    private static void updateClientPolicyVersion(AWSIot client, String policyName, JSONObject policy) {
        updateClientPolicyVersion(client, policyName, policy, 0);
    }

    public static IotPolicy getIotPolicyWithTopics(String topic, String type) {
        LOGGER.info(" creating IotRule for " + topic + "   and type " + type);
        IotPolicy policy = new IotPolicy();
        policy.setPublishtopics(new ArrayList<>(Arrays.asList(topic)));
        policy.setArnPublishtopics(new ArrayList<>(Arrays.asList(getIotArnTopic(topic))));
        policy.setSubscribeTopics(new ArrayList<>(Arrays.asList(topic + "/msgs")));
        policy.setArnSubscribeTopics(new ArrayList<>(Arrays.asList(getIotArnTopicFilter(topic) + "/msgs")));
        policy.setReceiveTopics(new ArrayList<>(Arrays.asList(topic + "/msgs")));
        policy.setArnReceiveTopics(new ArrayList<>(Arrays.asList(getIotArnTopic(topic + "/msgs"))));
        policy.setType(type);
        return policy;
    }

    public static IotPolicy createOrUpdateIotPolicy(String clientName, String policyName, String type, AWSIot iotClient) throws Exception {
        if (iotClient != null) {
            if (type != null) {
                if (policyName != null) {
                    LOGGER.info(" creating iot policy " + policyName);
                    new IotPolicy();
                    IotPolicy policy = AwsPolicyUtils.getIotPolicyWithTopics(policyName, type);
                    if (clientName != null) {
                        policy.getClientIds().add(clientName);
                        policy.getArnClientIds().add(getIotArnClientId(clientName));
                    }
                    policy.setName(policyName);
                    LOGGER.info("Publish topics " + policy.getPublishtopics());
                    LOGGER.info("Subscribe topics " + policy.getSubscribeTopics());
                    LOGGER.info("Receive topics " + policy.getReceiveTopics());
                    createIotPolicyAtAws(iotClient, policy); // check topic or policy name
                    LOGGER.info("Created iot policy at AWS");
                    return policy;
                } else {
                    throw new Exception("Policy name can't be null");
                }
            } else {
                throw new Exception("Type cant be null");
            }
        } else {
            throw new Exception("Iot client cant be null");
        }
    }

    private static void createIotPolicyAtAws(AWSIot iotClient, IotPolicy rule) throws Exception {
        LOGGER.info("Creating Iot policy for " + rule.getName());
        if (iotClient != null) {
            try {
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                JSONObject policyDocumentJSON = getPolicyDoc(rule.getArnClientIds(), rule.getArnPublishtopics(), rule.getArnSubscribeTopics(), rule.getArnReceiveTopics());
                LOGGER.info("Policy document " + policyDocumentJSON);
                rule.setPolicyDocument(policyDocumentJSON);
                if (FacilioProperties.isProduction() || (!FacilioProperties.isDevelopment() && (orgId == 152 || orgId == 660))) {
                    CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                            .withPolicyName(rule.getName())
                            .withPolicyDocument(rule.getPolicyDocument().toString());
                    CreatePolicyResult createPolicyResult = iotClient.createPolicy(createPolicyRequest);
                    String policyVersionId = createPolicyResult.getPolicyVersionId();
                    LOGGER.info("Policy created!Version is " + policyVersionId);
                } else {
                    LOGGER.info("Not production so can't create policy ");
                }
            } catch (ResourceAlreadyExistsException resourceExists) {
                LOGGER.info("Policy already exists for name : " + rule.getName());
                try {
                    updatePolicy(iotClient, rule.getName(), rule);
                } catch (Exception e) {
                    LOGGER.info("Exception while updating policy ", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.info("Exception while creating iot policy ", e);
                throw e;
            }
        } else {
            throw new Exception("Iot client can't be null");
        }
    }

    private static void addResourceToPolicyIfNotPresent(JSONObject policyStatement, List<String> topics) {
        try {
            List<String> resourcesPresent = getResourcesList(policyStatement);
            Set<String> resourcesToAdd = getResourcesToAdd(resourcesPresent, topics);
            addResourcesToPolicy(policyStatement, resourcesToAdd);
        } catch (Exception e) {
            LOGGER.info(" Exception while adding resource to policy ", e);
        }
    }

    private static void addResourcesToPolicy(JSONObject policyStatement, Set<String> resourcesToAdd) {
        if ((policyStatement != null) && (resourcesToAdd != null)) {
            if (policyStatement.containsKey("Resource")) {
                JSONArray resourceJSONArray;
                resourceJSONArray = (JSONArray) policyStatement.get("Resource");
                resourceJSONArray.addAll(resourcesToAdd);
            }
        }
    }

    private static void updateClientPolicyVersion(AWSIot client, String policyName, JSONObject policy, int count) { //TODO possibility of recursion
        try {
            if (count > 3) {
                LOGGER.info(" Exception occurred, recursive execution");
                return;
            }
            createPolicyVersion(policyName, client, policy);
        } catch (VersionsLimitExceededException e) {
            LOGGER.info(" policy limit reached ");
            deleteOldPolicyVersion(client, policyName);
            updateClientPolicyVersion(client, policyName, policy, count++);
        }
    }

    private static void deleteOldPolicyVersion(AWSIot client, String policyName) {
        List<PolicyVersion> policyVersionList = getPolicyVersions(client, policyName);
        String oldestVersion = getOldestPolicyVersion(policyVersionList);
        deletePolicyVersion(client, policyName, oldestVersion);
    }

    private static String getOldestPolicyVersion(List<PolicyVersion> policyVersionList) {
        Map<Date, PolicyVersion> datePolicyVersionMap = new HashMap<>();
        policyVersionList.forEach(policyVersion -> datePolicyVersionMap.put(policyVersion.getCreateDate(), policyVersion));
        List<Date> dates = new ArrayList<>(datePolicyVersionMap.keySet());
        dates.sort(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });
        return datePolicyVersionMap.get(dates.get(0)).getVersionId();
    }

    private static void deletePolicyVersion(AWSIot client, String policyName, String oldestVersion) {
        DeletePolicyVersionRequest deletePolicyVersionRequest = new DeletePolicyVersionRequest()
                .withPolicyName(policyName)
                .withPolicyVersionId(oldestVersion);
        DeletePolicyVersionResult deletePolicyVersionResult = client.deletePolicyVersion(deletePolicyVersionRequest);
        LOGGER.info(" deleted policy " + policyName + " version " + oldestVersion);
    }

    private static List<PolicyVersion> getPolicyVersions(AWSIot client, String policyName) {
        ListPolicyVersionsRequest listPolicyVersionsRequest = new ListPolicyVersionsRequest()
                .withPolicyName(policyName);
        ListPolicyVersionsResult listPolicyVersionsResut = client.listPolicyVersions(listPolicyVersionsRequest);
        return listPolicyVersionsResut.getPolicyVersions();
    }

    private static JSONObject getPolicyDoc(List<String> clientIds, List<String> publish, List<String> subscribe, List<String> receive) {
        JSONArray statements = new JSONArray();
        statements.add(getPolicyInJson("iot:Connect", clientIds));
        statements.add(getPolicyInJson("iot:Publish", publish));
        statements.add(getPolicyInJson("iot:Subscribe", subscribe));
        statements.add(getPolicyInJson("iot:Receive", receive));
        JSONObject policyDocument = new JSONObject();
        //Refer the versions available in AWS policy document before changing : aws3-demo.link/woBkoC
        policyDocument.put("Version", "2012-10-17");
        LOGGER.info(" policy doc statement " + statements.toString());
        policyDocument.put("Statement", statements);
        return policyDocument;
    }

    public static String getIotArnClientId(String clientId) {
        return getIotArnClientString() + clientId;
    }

    private static String getIotArnClientString() {
        return AwsUtil.getIotArn() + CLIENT;
    }

    static String getIotArnTopic(String topic) {
        return getIotArnTopicString() + topic;
    }

    private static String getIotArnTopicString() {
        return AwsUtil.getIotArn() + TOPIC;
    }

    public static String getIotArnTopicFilter(String topic) {
        return getIotArnTopicFilterString() + topic;
    }

    private static String getIotArnTopicFilterString() {
        return AwsUtil.getIotArn() + TOPICFILTER;
    }

    private static JSONObject getPolicyInJson(String action, List<String> resource) {
        JSONObject object = new JSONObject();
        object.put("Effect", "Allow");
        object.put("Action", action);
        JSONArray array = new JSONArray();
        for (String str : resource) {
            array.add(str);
        }
        object.put("Resource", array);
        return object;
    }

    private static JSONObject getPolicyDocumentJson(GetPolicyResult result) throws Exception {
        Objects.requireNonNull(result);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(result.getPolicyDocument());
    }

    private static String createPolicyVersion(String policyName, AWSIot client, JSONObject policyDoc) throws VersionsLimitExceededException {
        CreatePolicyVersionRequest versionRequest = new CreatePolicyVersionRequest().withPolicyName(policyName)
                .withPolicyDocument(policyDoc.toString())
                .withSetAsDefault(true);
        CreatePolicyVersionResult versionResult = client.createPolicyVersion(versionRequest);
        LOGGER.info("Policy created for " + policyName + ", with " + versionResult.getPolicyDocument() + ", status: " + versionResult.getSdkHttpMetadata().getHttpStatusCode());
        String policyVersion = versionResult.getPolicyVersionId();
        LOGGER.info(" policy created v->" + policyVersion);
        return policyVersion;
    }

    private static GetPolicyResult getPolicy(String policyName, AWSIot client) {
        GetPolicyRequest request = new GetPolicyRequest().withPolicyName(policyName);
        GetPolicyResult result = client.getPolicy(request);
        return result;
    }

    public static JSONObject getPolicyGist(AWSIot iotClient) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(currentOrg);
        JSONObject policyDocumentJson = getPolicyDocumentJson( getPolicy(currentOrg.getDomain(),iotClient));
        LOGGER.info("policy json " + policyDocumentJson);
        JSONObject gist = getGist(policyDocumentJson);
        LOGGER.info("policy name " + currentOrg.getDomain() + " ---- " + policyDocumentJson + "  --- " + gist);
        return gist;
    }

    private static List<Policy> getAllPolicy(AWSIot client) {
        Objects.requireNonNull(client);
        ListPoliciesRequest request = new ListPoliciesRequest();
        ListPoliciesResult listPoliciesResult = client.listPolicies(request);
        return listPoliciesResult.getPolicies();
    }

    private static boolean checkForPolicy(String policyName, AWSIot client) {
        GetPolicyRequest request = new GetPolicyRequest().withPolicyName(policyName);
        try {
            GetPolicyResult result = client.getPolicy(request);
            if (result.getPolicyName().equals(policyName)) {
                return true;
            } else {
                LOGGER.info(" resource found but name mismatch ");
                return false;
            }
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private static List<String> getResourcesList(JSONObject policyStatement) throws Exception {
        if ((policyStatement != null) && (policyStatement.containsKey("Resource"))) {
            JSONArray resourceJSONArray;
            JSONParser parser = new JSONParser();
            resourceJSONArray = (JSONArray) parser.parse(String.valueOf(policyStatement.get("Resource")));
            List<String> asList = getAsList(resourceJSONArray);
            return asList;
        } else {
            System.out.println("no resource found");
        }
        return new ArrayList<>();
    }

    private static List<String> getTopicsFromArns(List<String> arnTopics) throws Exception {
        List<String> topics = new ArrayList<>();
        Objects.requireNonNull(arnTopics, "arn tpoics cant be null");
        for (String arnTopic : arnTopics) {
            if (arnTopic.contains(TOPIC)) {
                arnTopic = getTopic(arnTopic, TOPIC);
            } else if (arnTopic.contains(TOPICFILTER)) {
                arnTopic = getTopic(arnTopic, TOPICFILTER);
            } else if (arnTopic.contains(CLIENT)) {
                arnTopic = getTopic(arnTopic, CLIENT);
            } else {
                throw new Exception("no arnFilter matches ARNTopic " + arnTopic);
            }
            topics.add(arnTopic);
        }
        return topics;
    }

    private static String getTopic(String arnTopic, String arnString) throws Exception {
        if (arnTopic.contains(arnString)) {
            int indexOf = arnTopic.indexOf(arnString) + arnString.length();
            return arnTopic.substring(indexOf);
        } else {
            throw new Exception(" topic is not in having " + arnString);
        }
    }

    private static List<String> getAsList(JSONArray resourceJSONArray) {
        List<String> resources = new ArrayList<>();
        if (resourceJSONArray != null) {
            resourceJSONArray.forEach(resource -> resources.add(String.valueOf(resource)));
        }
        return resources;
    }

    private static Set<String> getResourcesToAdd(List<String> receiveTopics, List<String> toCheckAndAddArray) {
        Set<String> resourcesToAdd = new HashSet<>();
        for (String element : toCheckAndAddArray) {
            if (!receiveTopics.contains(element)) {
                resourcesToAdd.add(element);
            }
        }
        return resourcesToAdd;
    }

}
