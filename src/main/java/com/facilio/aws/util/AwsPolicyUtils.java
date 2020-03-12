package com.facilio.aws.util;

import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.model.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class AwsPolicyUtils
{
    private static final Logger LOGGER = LogManager.getLogger(AwsPolicyUtils.class.getName());

    private static void addClientToPolicy(JSONObject policy, List<String> connectTopics) throws Exception {
        updatePolicyStatement(policy,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),connectTopics);
    }

    private static void updatePolicyStatement( JSONObject policy, List<String> subscribeTopics, List<String> publishTopics, List<String> receiveTopics, List<String> connectTopics) throws Exception {
        if(policy.containsKey("Statement") && (policy.get("Statement") != null)){
            JSONArray policyStatements = (JSONArray) policy.get("Statement");
            for (Object policyStatementObject : policyStatements) {
                JSONObject policyStatement = (JSONObject) policyStatementObject;
                if((policyStatement.containsKey("Action"))&&(policyStatement.get("Action") != null)){
                    String action = (String) policyStatement.get("Action");
                        switch (action){
                            case "iot:Connect":
                                addResourceToPolicyIfNotPresent(policyStatement,connectTopics);
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
                }else{
                    throw new Exception("key -Action- missing from policyJson ");
                }
            } // for ends
        }else {
            throw new Exception(" key -Statement- missing from policyJson ");
        }
    }

    private static String updatePolicy(AWSIot client, String policyName, IotPolicy rule) throws Exception {
        LOGGER.info(" updating policy "+policyName);
        JSONObject policy = new JSONObject();
        policy = getPolicyDocumentJson(getPolicy(policyName, client));
        LOGGER.info("existing policy document "+policy);
        /*List<String> subscribeTopics = rule.getPublishtopics();
        LOGGER.info(subscribeTopics);
        List<String> publishTopics = rule.getPublishtopics();
        LOGGER.info(publishTopics);
        List<String> receiveTopics = rule.getReceiveTopics();
        LOGGER.info(receiveTopics);*/
        List<String> connectTopics = rule.getClientIds();
        addClientToPolicy(policy, connectTopics);
        LOGGER.info(" updated policy document "+policy);
        updateClientPolicyVersion(client, policyName, policy);
        return policy.toString();
    }

    private static void updateClientPolicyVersion(AWSIot client, String policyName, JSONObject policy) {
        updateClientPolicyVersion(client,policyName,policy,0);
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

/*
    public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        IotPolicy iotRule = AwsPolicyUtils.getIotRule("topic", "facilio");
        System.out.println(iotRule.getClientIds());
        System.out.println(iotRule.getArnClientIds());
        System.out.println(iotRule.getPublishtopics());
        System.out.println(iotRule.getArnPublishtopics());
        System.out.println(iotRule.getSubscribeTopics());
        System.out.println(iotRule.getArnSubscribeTopics());
        System.out.println(iotRule.getReceiveTopics());
        System.out.println(iotRule.getArnReceiveTopics());
        //System.out.println(iotRule.getPolicyDocument());
    }
*/

    public static IotPolicy createOrUpdateIotPolicy(String clientName, String policyName, String type, AWSIot iotClient) throws Exception {
        if (iotClient != null) {
            if (type != null) {
                if (policyName != null) {
                    LOGGER.info(" creating iot policy " + policyName);
                    IotPolicy policy = new IotPolicy();
                    policy = AwsPolicyUtils.getIotPolicyWithTopics(policyName, type);
                    if (clientName != null) {
                        policy.getClientIds().add(clientName);
                        policy.getArnClientIds().add(getIotArnClientId(clientName));
                    }
                    policy.setName(policyName);
                    LOGGER.info("p topics " + policy.getPublishtopics());
                    LOGGER.info("s topics " + policy.getSubscribeTopics());
                    LOGGER.info("r topics " + policy.getReceiveTopics());
                    createIotPolicyAtAws(iotClient, policy); // check topic or policy name
                    LOGGER.info(" created iot policy at AWS ");
                    return policy;
                } else {
                    throw new Exception("policy name can't be null");
                }
            } else {
                throw new Exception("type cant be null");
            }
        } else {
            throw new Exception(" iot client cant be null");
        }
    }


    private static void createIotPolicyAtAws(AWSIot iotClient, IotPolicy rule) throws Exception {
        LOGGER.info(" creating Iot policy for " + rule.getName());
        if (iotClient != null) {
            if (rule != null) {
                try {
                    JSONObject policyDocumentJSON = getPolicyDoc(rule.getArnClientIds(), rule.getArnPublishtopics(), rule.getArnSubscribeTopics(), rule.getArnReceiveTopics());
                    LOGGER.info(" policy document " + policyDocumentJSON);
                    rule.setPolicyDocument(policyDocumentJSON);
                    if (FacilioProperties.isProduction()) {
                        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                                .withPolicyName(rule.getName())
                                .withPolicyDocument(rule.getPolicyDocument().toString());
                        CreatePolicyResult createPolicyResult = iotClient.createPolicy(createPolicyRequest);
                        String policyVersionId = createPolicyResult.getPolicyVersionId();
                        LOGGER.info("policy created anf version is " + policyVersionId);
                    } else {
                        LOGGER.info(" not production so can't create policy ");
                    }
                } catch (ResourceAlreadyExistsException resourceExists) {
                    LOGGER.info("Policy already exists for name : " + rule.getName());
                    try {
                        updatePolicy(iotClient, rule.getName(), rule);
                    } catch (Exception e) {
                        LOGGER.info(" Exception while updating policy ", e);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception while creating iot policy ", e);
                }
            }
        } else {
            throw new Exception(" iot client cant be null");
        }

    }

    private static void addResourceToPolicyIfNotPresent(JSONObject policyStatement, List<String> topics) {
        try {
            List<String> resourcesPresent = getResourcesList(policyStatement);
            List<String> resourcesToAdd = getResourcesToAdd(resourcesPresent, topics);
            addResourcesToPolicy(policyStatement,resourcesToAdd);
        } catch (ParseException e) {
           LOGGER.info(" Exception while adding resource to policy ",e);
        }
    }

    private static void addResourcesToPolicy(JSONObject policyStatement, List<String> resourcesToAdd) {
        if((policyStatement != null)&&(resourcesToAdd != null)){
            if (policyStatement.containsKey("Resource")) {
                JSONArray resourceJSONArray;
                JSONParser parser = new JSONParser();
                resourceJSONArray = (JSONArray) policyStatement.get("Resource");
                resourceJSONArray.addAll(resourcesToAdd);
            }
        }
    }

    private static void updateClientPolicyVersion(AWSIot client, String policyName, JSONObject policy, int count) { //TODO possibility of recursion
       try{
           if(count > 3){
               LOGGER.info(" Exception occurred, recursive execution");
               return;
           }
        createPolicyVersion(policyName,client,policy);
        }catch (VersionsLimitExceededException e){
            LOGGER.info(" policy limit reached ");
            deleteOldPolicyVersion(client,policyName);
            updateClientPolicyVersion(client,policyName,policy,count++);
        }
    }

    private static void deleteOldPolicyVersion(AWSIot client, String policyName) {
        List<PolicyVersion> policyVersionList = getPolicyVersions(client, policyName);
        String oldestVersion = getOldestPolicyVersion(policyVersionList);
        deletePolicyVersion(client, policyName, oldestVersion);
    }

    private static String getOldestPolicyVersion(List<PolicyVersion> policyVersionList) {
        Map<Date, PolicyVersion> datePolicyVersionMap = new HashMap<>() ;
        policyVersionList.forEach(policyVersion -> datePolicyVersionMap.put(policyVersion.getCreateDate(),policyVersion));
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
        LOGGER.info(" deleted policy "+policyName+" version "+oldestVersion);
    }

    private static List<PolicyVersion> getPolicyVersions(AWSIot client, String policyName) {
        ListPolicyVersionsRequest listPolicyVersionsRequest = new ListPolicyVersionsRequest()
                .withPolicyName(policyName);
        ListPolicyVersionsResult listPolicyVersionsResut = client.listPolicyVersions(listPolicyVersionsRequest);
        return listPolicyVersionsResut.getPolicyVersions();
    }

    private static JSONObject getPolicyDoc(List<String> clientIds, List<String> publish, List<String> subscribe, List<String> receive ){
        JSONArray statements = new JSONArray();
        statements.add(getPolicyInJson("iot:Connect", clientIds));
        statements.add(getPolicyInJson("iot:Publish",publish ));
        statements.add(getPolicyInJson("iot:Subscribe", subscribe));
        statements.add(getPolicyInJson("iot:Receive", receive));
        JSONObject policyDocument = new JSONObject();
        policyDocument.put("Version", "2012-10-17"); //Refer the versions available in AWS policy document before changing.
        LOGGER.info(" policy doc statement "+statements.toString());
        policyDocument.put("Statement", statements);
        return policyDocument;
    }



    public static String getIotArnClientId(String clientId){
        return AwsUtil.getIotArn() + ":client/" + clientId;
    }

    static String getIotArnTopic(String topic) {
        return AwsUtil.getIotArn() +":topic/"+ topic;
    }

    public static String getIotArnTopicFilter(String topic) {
        return AwsUtil.getIotArn() +":topicfilter/"+ topic;
    }

    private static JSONObject getPolicyInJson(String action, List<String> resource){
        JSONObject object = new JSONObject();
        object.put("Effect", "Allow");
        object.put("Action", action);
        JSONArray array = new JSONArray();
        for(String str : resource) {
            array.add(str);
        }
        object.put("Resource", array);
        return object;
    }
    private static JSONObject getPolicyDocumentJson(GetPolicyResult result) throws ParseException {
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
        LOGGER.info(" policy created v->"+policyVersion);
        return policyVersion;
    }

    private static GetPolicyResult getPolicy(String policyName, AWSIot client) {
        GetPolicyRequest request = new GetPolicyRequest().withPolicyName(policyName);
        GetPolicyResult result = client.getPolicy(request);
        return result;
    }
    private static boolean checkForPolicy(String policyName, AWSIot client){
        GetPolicyRequest request = new GetPolicyRequest().withPolicyName(policyName);
        try {
            GetPolicyResult result = client.getPolicy(request);
            if (result.getPolicyName().equals(policyName)) {
                return true;
            }else {
                LOGGER.info(" resource found but name mismatch ");
                return false;
            }
        }catch (ResourceNotFoundException e){
            return false;
        }
    }
/*    private static JSONObject addClientToPolicyDoc(String policyName, String clientId, JSONObject object) {
        JSONArray array = (JSONArray) object.get("Statement");
        List<String> clients = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject stat = (JSONObject) array.get(i);
            JSONArray action = (JSONArray) stat.get("Action");
            if (action.contains("iot:Connect")) {
                JSONArray resourceArray = (JSONArray) stat.get("Resource");
                for (int j = 0; j < resourceArray.size(); j++) {
                    clients.add((String) resourceArray.get(j));
                }
                break;
            }
        }
        System.out.println(clients);
        clients.add(getIotArnClientId(clientId));
        return getPolicyDoc(new ArrayList<>(Arrays.asList(getIotArnClientId(policyName))), new ArrayList<>(Arrays.asList(getIotArnTopic(policyName))), new ArrayList<>(Arrays.asList(getIotArnTopicFilter(policyName) + "/msgs")), new ArrayList<>(Arrays.asList(getIotArnTopic(policyName) + "/msgs")));
    }*/

    private static List<String> getResourcesList(JSONObject policyStatement) throws ParseException {
        if((policyStatement != null) && (policyStatement.containsKey("Resource")) ){
            JSONArray resourceJSONArray;
            JSONParser parser = new JSONParser();
            resourceJSONArray = (JSONArray) parser.parse(String.valueOf(policyStatement.get("Resource")));
            return getAsList(resourceJSONArray);
        }
        return new ArrayList<>();
    }

    private static List<String> getAsList(JSONArray resourceJSONArray) {
        List<String> resources = new ArrayList<>();
        if(resourceJSONArray != null){
            resourceJSONArray.forEach(resource->resources.add(String.valueOf(resource)));
        }
        return resources;
    }

    private static List<String> getResourcesToAdd(List<String> receiveTopics, List<String> toCheckAndAddArray) {
        List<String> resourcesToAdd = new ArrayList<>();
        for (String element : toCheckAndAddArray) {
            if( ! receiveTopics.contains(element)){
                resourcesToAdd.add(element);
            }
        }
        return resourcesToAdd;
    }

}
