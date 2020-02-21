package com.facilio.aws.util;

import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.model.*;
import com.facilio.agent.AgentType;
import com.facilio.agent.PublishType;
import com.facilio.agent.integration.AgentIntegrationKeys;
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
                            case "iot:Publish":
                                addResourceToPolicyIfNotPresent(policyStatement,publishTopics);
                                break;
                            case "iot:Receive":
                                addResourceToPolicyIfNotPresent(policyStatement,receiveTopics);
                                break;
                            case "iot:Subscribe":
                                addResourceToPolicyIfNotPresent(policyStatement,subscribeTopics);
                                break;
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
        List<String> subscribeTopics = rule.getPublishtopics();
        LOGGER.info(subscribeTopics);
        List<String> publishTopics = rule.getPublishtopics();
        LOGGER.info(publishTopics);
        List<String> receiveTopics = rule.getReceiveTopics();
        LOGGER.info(receiveTopics);
        List<String> connectTopics = rule.getClientIds();
        updatePolicyStatement(policy, subscribeTopics, publishTopics, receiveTopics, connectTopics);
        LOGGER.info(" updated policy document "+policy);
        updateClientPolicyVersion(client, policyName, policy);
        return policy.toString();
    }

    private static void updateClientPolicyVersion(AWSIot client, String policyName, JSONObject policy) {
        updateClientPolicyVersion(client,policyName,policy,0);
    }

    public static IotPolicy getIotRule(String topic,String type){
        LOGGER.info(" creating IotRule for "+topic+"   and type "+type);
        IotPolicy policy = new IotPolicy();
        if(AgentType.Wattsense.getLabel().equalsIgnoreCase(type)){
            policy.setToModify(true);
            policy.setPublishtopics(new ArrayList<>(
                    Arrays.asList((topic + AgentIntegrationKeys.TOPIC_WT_EVENTS) , ( topic+ AgentIntegrationKeys.TOPIC_WT_ALARMS ), ( topic+ AgentIntegrationKeys.TOPIC_WT_VALUES ))));
            policy.setClientIds(new ArrayList<>(Arrays.asList(getIotArnClientId(topic))));
            policy.setReceiveTopics(new ArrayList<>(Arrays.asList(topic+AgentIntegrationKeys.TOPIC_WT_CMD)));
            policy.setSubscribeTopics(new ArrayList<>(Arrays.asList(getIotArnTopicFilter(topic)+AgentIntegrationKeys.TOPIC_WT_CMD)));
            policy.setPublishTypes(new ArrayList<>(Arrays.asList(PublishType.event.getValue(),PublishType.event.getValue(),PublishType.timeseries.getValue())));
            LOGGER.info("topic and pubtype "+policy.getMappedTopicAndPublished());
            return policy;
        }
        else {
            policy.setClientIds(new ArrayList<>(Arrays.asList( getIotArnClientId(topic))));
            policy.setPublishtopics(new ArrayList<>(Arrays.asList(getIotArnTopic(topic))));
            policy.setSubscribeTopics(new ArrayList<>(Arrays.asList(getIotArnTopicFilter(topic)+"/msgs")));
            policy.setReceiveTopics(new ArrayList<>(Arrays.asList(getIotArnTopic(topic+"/msgs"))));
            policy.setType(type);
            return policy;
        }
    }

    public static IotPolicy createIotPolicy(String topic, String policyName, String type, AWSIot iotClient) {
        LOGGER.info(" creating iot policy "+policyName);
        IotPolicy policy;
        policy = AwsPolicyUtils.getIotRule(topic,type);
        policy.setName(policyName);
        LOGGER.info("p topics "+policy.getPublishtopics());
        LOGGER.info("s topics "+policy.getSubscribeTopics());
        LOGGER.info("r topics "+policy.getReceiveTopics());
        createIotPolicy(iotClient, policy); // check topic or policy name
        return policy;
    }



    private static String createIotPolicy(AWSIot iotClient , IotPolicy rule) {
        LOGGER.info(" creating Iot policy for "+rule.getName());

       /* for(int i=0;i<rule.getPublishtopics().size();i++){
            publish.add(getIotArnTopic(rule.getPublishtopics().get(i)));
        }
		LOGGER.info(publish);
		for(int i=0;i<rule.getReceiveTopics().size();i++){
            receive.add(getIotArnTopic(rule.getReceiveTopics().get(i)));
		}*/

        try {
            JSONObject policyDocumentJSON = getPolicyDoc(rule.getClientIds(), rule.getPublishtopics(), rule.getSubscribeTopics(), rule.getReceiveTopics());
            LOGGER.info(" policy document "+policyDocumentJSON);
            rule.setPolicyDocument(policyDocumentJSON);
            CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                    .withPolicyName(rule.getName())
                    .withPolicyDocument(rule.getPolicyDocument().toString());
            CreatePolicyResult createPolicyResult = iotClient.createPolicy(createPolicyRequest);
            return createPolicyResult.getPolicyVersionId();
        } catch (ResourceAlreadyExistsException resourceExists){
            LOGGER.info("Policy already exists for name : " + rule.getName());
            try {
                return updatePolicy(iotClient,rule.getName(),rule);
            } catch (Exception e) {
                LOGGER.info(" Exception while updating policy ",e);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while creating iot policy ",e);
        }
        return null;
    }

    private static void addResourceToPolicyIfNotPresent(JSONObject policyStatement, List<String> topics) {
        try {
            List<String> resourcesPresent = getResourcesList(policyStatement);
            List<String> resourcesToAdd = getResourcesToAdd(resourcesPresent, topics);
            addResourcesToPolicy(policyStatement,resourcesToAdd);
        } catch (ParseException e) {
            e.printStackTrace();
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
