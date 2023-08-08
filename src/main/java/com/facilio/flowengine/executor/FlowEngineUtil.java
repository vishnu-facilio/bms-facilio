package com.facilio.flowengine.executor;


import com.facilio.flowengine.context.ExpressionGroup;
import com.facilio.flowengine.context.FlowCondition;
import com.facilio.flowengine.context.Rule;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.operators.FlowOperator;
import com.facilio.workflows.util.WorkflowUtil;
import com.udojava.evalex.Expression;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO check this file
@Log4j
public class FlowEngineUtil {
    private static final Pattern PLACE_HOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern ARRAY_SYNTEX_PATTERN = Pattern.compile("^[a-zA-Z_$][a-zA-Z\\d_$]*\\[([^\\]]+\\]\\[)*[^\\]]+\\]$");

    public static List<String> getUsedVariableNames(String expressionStr) {
        Expression expression = new Expression(expressionStr);
        List<String> usedVariables = new ArrayList<>();
        try {
            usedVariables = expression.getUsedVariables();
        } catch (Exception e) {

        }
        return usedVariables;
    }

    public static Object evaluateExpression(Map<String, Object> memory, String expressionStr) throws Exception {

        Map<String, Object> variablesMap = new HashMap<>();
        List<String> usedVariables = getUsedVariableNames(expressionStr);

        if (CollectionUtils.isNotEmpty(usedVariables)) {
            for (String variable : usedVariables) {
                if (memory.containsKey(variable)) {
                    variablesMap.put(variable, memory.get(variable));
                }
            }
        }
        return WorkflowUtil.evaluateExpressionV2(expressionStr, variablesMap);
    }
    public static Object evaluateFlowRule(Rule rule, Map<String, Object> memory) throws Exception {
        StringBuilder expressionString = new StringBuilder();
        List<ExpressionGroup> groups = rule.getGroups();
        expressionString.append("(");
        for(int i=0; i<groups.size();i++){
            ExpressionGroup group = groups.get(i);
            if(i!=0){
                expressionString.append(" "+group.getJoinType()+" ");
            }

            expressionString.append("(");

            List<FlowCondition> conditions = group.getConditions();
            for(int j=0;j<conditions.size();j++){
                FlowCondition condition = conditions.get(j);
                if(j!=0){
                    expressionString.append(" "+condition.getJoinType()+" ");
                }
                String left = condition.getLeft();
                FlowOperator operator = condition.getOperator();
                String right = condition.getRight();

                Object leftObject = replacePlaceHolder(left,memory);
                Object rightObject = replacePlaceHolder(right,memory);
                Boolean resultBoolean = operator.evaluate(leftObject,rightObject);
                expressionString.append(" "+resultBoolean+" ");

            }

            expressionString.append(")");
        }
        expressionString.append(")");
        return evaluateExpression(memory,expressionString.toString());
    }
    public static Object replacePlaceHolder(Object source, Map<String, Object> memory) throws Exception {
        if (source instanceof Map) {

            Map map = (Map) source;
            JSONObject replacedConfig = new JSONObject();
            Set<String> keys = map.keySet();

            for (String key : keys) {
                Object value = map.get(key);
                try {
                    replacedConfig.put(key, replacePlaceHolder(value, memory));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return replacedConfig;

        } else if (source instanceof List) {

            List list = (List) source;
            JSONArray replacedJSONArray = new JSONArray();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Object value = iterator.next();
                try {
                    replacedJSONArray.add(replacePlaceHolder(value, memory));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return replacedJSONArray;

        } else if ((source instanceof String) && isPlaceHolderString(source.toString())) {

            List<String> placeholders = extractPlaceholders(source.toString());

            return getOrDefaultValue(placeholders.get(0), memory, null);

        } else if (source instanceof String) {

            List<String> placeholders = extractPlaceholders(source.toString());

            if (CollectionUtils.isEmpty(placeholders)) { //return the same source if placeHolder is empty
                return source;
            }

            Map<String, Object> placeHolderMap = new HashMap<>();

            for (String placeHolder : placeholders) {

                Object value = getOrDefaultValue(placeHolder, memory, "null");
                placeHolderMap.put(placeHolder, value);

            }

            String replacedString = StringSubstitutor.replace(source.toString(), placeHolderMap);

            return replacedString;
        }
        return source;
    }
    public static boolean isAllPlaceHoldersReplaced(Object source)
    {
        boolean replaced;

        if (source instanceof Map) {

            Map map = (Map) source;
            Set<String> keys = map.keySet();

            for (String key : keys) {
                Object value = map.get(key);
                replaced = isAllPlaceHoldersReplaced(value);
                if (!replaced)
                {
                    return false;
                }
            }
        } else if (source instanceof List) {

            List list = (List) source;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Object value = iterator.next();
                replaced = isAllPlaceHoldersReplaced(value);
                if (!replaced)
                {
                    return false;
                }
            }

        } else if (source instanceof String) {
            if(hasPlaceHolder(source.toString())){
                return false;
            }
        }

        return true;
    }
    public static boolean hasPlaceHolder(String placeHolderString) {
        List<String> placeholders = extractPlaceholders(placeHolderString);
        if(CollectionUtils.isNotEmpty(placeholders)){
            return true;
        }
        return false;
    }

    private static List<String> extractPlaceholders(String placeholderString) {
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = PLACE_HOLDER_PATTERN.matcher(placeholderString);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            placeholders.add(placeholder);
        }
        return placeholders;
    }

    public static void setValue(Map<String, Object> data, String key, Object value) throws Exception {
        String[] split = key.split("\\.");
        Map currentNode = data;

        for (int i = 0; i < split.length - 1; i++) {
            String currentToken = split[i];

            if (FlowEngineUtil.isArrayToken(currentToken)) {
                String arrayName = currentToken.substring(0, currentToken.indexOf("["));
                currentNode.putIfAbsent(arrayName, new JSONArray());
                List list = (List) (currentNode.get(arrayName));

                Pair<Integer, List> pair = handleDimensionForSet(list, currentToken, data);
                int pointer = pair.getLeft();
                list = pair.getRight();


                if (list.size()>pointer) {
                    currentNode =  list.get(pointer)!=null? (Map) list.get(pointer) :new JSONObject();
                } else {
                    JSONObject temp = new JSONObject();
                    setValueInList(list, temp, pointer);
                    currentNode = temp;
                }
            } else if (currentNode.containsKey(currentToken)) {
                currentNode = currentNode.get(currentToken)!=null? (Map) currentNode.get(currentToken) :new JSONObject();
            } else {
                JSONObject temp = new JSONObject();
                currentNode.put(currentToken, temp);
                currentNode = temp;
            }
        }

        String lastToken = split[split.length - 1];

        if (FlowEngineUtil.isArrayToken(lastToken)) {
            String arrayName = lastToken.substring(0, lastToken.indexOf("["));
            currentNode.putIfAbsent(arrayName, new JSONArray());
            List list = (List) (currentNode.get(arrayName));
            Pair<Integer, List> pair = handleDimensionForSet(list, lastToken, data);
            int pointer = pair.getLeft();
            list = pair.getRight();
            setValueInList(list, value, pointer);
        } else {
            currentNode.put(lastToken, value);
        }
    }

    private static Pair<Integer, List> handleDimensionForSet(List list, String token, Map<String, Object> data) throws Exception {//a[9][9][8]
        String dimensionString = token.substring(token.indexOf("["));
        int start = 0;
        int end = 0;
        int n = dimensionString.length();
        char dimensionChar[] = dimensionString.toCharArray();
        int lastPointer = 0;
        while (start < n) {
            if (dimensionChar[start] == '[') {
                start++;
            }
            end = dimensionString.indexOf("]", start);
            String pointerVar = dimensionString.substring(start - 1, end + 1);
            int pointer = FlowEngineUtil.extractArrayPointer(pointerVar, data);
            lastPointer = pointer;
            List nextList = null;
            if (end + 1 == n) {
                nextList = list;
            } else if (list.isEmpty() || pointer > list.size()) {
                nextList = new ArrayList();
                setValueInList(list, nextList, pointer);
            } else {
                nextList = (List) list.get(pointer);
            }
            list = nextList;
            start = end;
            start++;
        }
        return new MutablePair<>(lastPointer, list);
    }
    private static void setValueInList(List list, Object value, int pointer) {
        if (pointer > list.size()-1) {
            for (int i = list.size()-1; i < pointer; i++) {
                list.add(null);
            }
            list.set(pointer, value);
        } else {
            list.set(pointer, value);
        }
    }

    public static int extractArrayPointer(String currentToken, Map<String, Object> data) throws Exception {
        String pointerStr = currentToken.substring(currentToken.indexOf("[") + 1, currentToken.length() - 1);
        Object pointerValue = null;
        if (NumberUtils.isNumber(pointerStr)) {
            pointerValue = pointerStr;
        } else {
            pointerValue = getValue(pointerStr, data);
            if (pointerValue == null || !NumberUtils.isNumber(pointerValue.toString())) {
                throw new FlowException(pointerValue + " is not a number to access the array");
            }
        }
        return Integer.valueOf(pointerValue.toString());
    }

    public static Object getValue(String key, Map<String, Object> data) throws Exception {
        return getValue(key, data, null);
    }

    public static Object getOrDefaultValue(String key, Map<String, Object> data, Object defaultValue) {
        try {
            return getValue(key, data, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private static Object getValue(String key, Map<String, Object> data, Object defaultValue) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        String[] split = key.split("\\.");

        Map currentNode = data;

        for (int i = 0; i < split.length - 1; i++) {
            String currentToken = split[i];

            if(currentNode == null){
                return null;
            }

            if (FlowEngineUtil.isArrayToken(currentToken)) {

                String arrayName = currentToken.substring(0, currentToken.indexOf("["));

                if (!currentNode.containsKey(arrayName)) {
                    return defaultValue;
                }
                List list = (List) (currentNode.get(arrayName));

                Pair<Integer, List> pair = handleDimensionForGet(list, currentToken, data);
                int pointer = pair.getLeft();
                list = pair.getRight();

                if (list == null) {
                    return null;
                }

                if (pointer < list.size()) {
                    currentNode = (Map) list.get(pointer);
                }
                else{
                    return null;
                }
            } else {
                if(currentNode.containsKey(currentToken)){
                    currentNode = (Map) currentNode.get(currentToken);
                }
                else{
                    return defaultValue;
                }
            }
        }

        String lastToken = split[split.length - 1];

        if(currentNode == null){
            return null;
        }

        if (FlowEngineUtil.isArrayToken(lastToken)) {
            String arrayName = lastToken.substring(0, lastToken.indexOf("["));
            if (!currentNode.containsKey(arrayName)) {
                return defaultValue;
            }
            List list = (List) (currentNode.get(arrayName));

            Pair<Integer, List> pair = handleDimensionForGet(list, lastToken, data);
            int pointer = pair.getLeft();
            list = pair.getRight();

            if (list == null) {
                return null;
            }

            if (pointer < list.size()) {
                return list.get(pointer);
            }
            else{
                return null;
            }

        } else {
            if(currentNode.containsKey(lastToken)){
                return currentNode.get(lastToken);
            }
            else{
                return defaultValue;
            }
        }
    }

    private static Pair<Integer, List> handleDimensionForGet(List list, String token, Map<String, Object> data) throws Exception {//a[9][9][8]
        String dimensionString = token.substring(token.indexOf("["));
        int start = 0;
        int end = 0;
        int n = dimensionString.length();
        char dimensionChar[] = dimensionString.toCharArray();
        int lastPointer = 0;
        while (start < n) {
            if (dimensionChar[start] == '[') {
                start++;
            }
            end = dimensionString.indexOf("]", start);
            String pointerVar = dimensionString.substring(start - 1, end + 1);
            int pointer = FlowEngineUtil.extractArrayPointer(pointerVar, data);
            lastPointer = pointer;
            List nextList = null;
            if (end + 1 == n) {
                nextList = list;
            } else if (list.isEmpty() || pointer > list.size()) {
                return new MutablePair<>(lastPointer, null);
            } else {
                nextList = (List) list.get(pointer);
            }
            list = nextList;
            start = end;
            start++;
        }
        return new MutablePair<>(lastPointer, list);
    }

    public static boolean isArrayToken(String token) {
        Matcher matcher = ARRAY_SYNTEX_PATTERN.matcher(token);
        return matcher.matches();
    }

    public static boolean isPlaceHolderString(String source) {
        Matcher matcher = PLACE_HOLDER_PATTERN.matcher(source);
        return matcher.matches();
    }

}
