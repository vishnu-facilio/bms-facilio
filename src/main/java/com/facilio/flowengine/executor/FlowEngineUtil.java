package com.facilio.flowengine.executor;


import com.facilio.flowengine.exception.FlowException;
import com.facilio.workflows.util.WorkflowUtil;
import com.udojava.evalex.Expression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

//TODO check this file
public class FlowEngineUtil {
    public static List<String> getUsedVariableNames(String expressionStr) {
        Expression expression = new Expression(expressionStr);
        List<String> usedVariables = new ArrayList<>();
        try {
            usedVariables = expression.getUsedVariables();
        }catch (Exception e){

        }
        return usedVariables;
    }

    public static Object evaluateExpression(Map<String, Object> memory, String expressionStr) throws Exception {

        Map<String,Object> variablesMap = new HashMap<>();
        List<String> usedVariables = getUsedVariableNames(expressionStr);

        if(CollectionUtils.isNotEmpty(usedVariables)){
            for(String variable:usedVariables){
                if(memory.containsKey(variable)){
                    variablesMap.put(variable,memory.get(variable));
                }
            }
        }
        return WorkflowUtil.evaluateExpressionV2(expressionStr,variablesMap);
    }

    public static void setValue(String key, Object value, Map<String, Object> globalMemory, Map<String, Object> localMap, int level) throws FlowException {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        String keys[] = key.split("\\.");
        String currentToken = keys[0];

        String remainingKeys = (keys.length > 1) ? key.substring(currentToken.length() + 1, key.length()) : null;

        if (FlowEngineUtil.isArrayToken(currentToken)) {
            String arrayName = currentToken.substring(0, currentToken.indexOf("["));
            if (level == 1) {
                globalMemory.putIfAbsent(arrayName, new ArrayList<>());
            } else {
                localMap.putIfAbsent(arrayName, new ArrayList<>());
            }
            List list = (List) ((level == 1) ? (globalMemory.get(arrayName)) : (localMap.get(arrayName)));

            Pair<Integer, List> pair = handleDimensionForSet(list, currentToken, globalMemory);
            int pointer = pair.getLeft();
            list = pair.getRight();

            if (keys.length == 1) {
                setValueInList(list, value, pointer);
            } else {
                Object nestedValue = null;
                if (list.size() != 0 || pointer < list.size()) {
                    nestedValue = list.get(pointer);
                }
                if (nestedValue == null) {
                    localMap = new HashMap<>();
                    setValueInList(list, localMap, pointer);
                } else {
                    localMap = (Map<String, Object>) nestedValue;
                }
                setValue(remainingKeys, value, globalMemory, localMap, ++level);
            }
        } else {
            Map<String, Object> map = null;
            if (level == 1 && keys.length > 1) {
                globalMemory.putIfAbsent(currentToken, new HashMap<String, Object>());
                map = (Map<String, Object>) globalMemory.get(currentToken);
            } else if (keys.length > 1) {
                localMap.putIfAbsent(currentToken, new HashMap<String, Object>());
                map = (Map<String, Object>) localMap.get(currentToken);
            } else if (level == 1) {
                map = globalMemory;
            } else {
                map = localMap;
            }

            localMap = map;
            if (keys.length == 1) {
                localMap.put(currentToken, value);
            } else {
                setValue(remainingKeys, value, globalMemory, localMap, ++level);
            }
        }
    }

    private static Pair<Integer, List> handleDimensionForSet(List list, String token, Map<String, Object> globalMemory) throws FlowException {//a[9][9][8]
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
            int pointer = FlowEngineUtil.extractArrayPointer(pointerVar, globalMemory, new HashMap<>());
            lastPointer = pointer;
            List nextList = null;
            if (list.isEmpty() || pointer > list.size()) {
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
        if (pointer > list.size()) {
            for (int i = list.size(); i <= pointer; i++) {
                list.add(null);
            }
            list.set(pointer, value);
        } else {
            list.set(pointer, value);
        }
    }

    public static int extractArrayPointer(String currentToken, Map<String, Object> globalMemory, Map<String, Object> localMap) throws FlowException {
        String pointerStr = currentToken.substring(currentToken.indexOf("[") + 1, currentToken.length() - 1);
        Object pointerValue = null;
        if (NumberUtils.isNumber(pointerStr)) {
            pointerValue = pointerStr;
        } else {
            pointerValue = getValue(pointerStr, globalMemory, localMap, 1);
            if (pointerValue == null || !NumberUtils.isNumber(pointerValue.toString())) {
                throw new FlowException(pointerValue + " is not a number to access the array");
            }
        }
        return Integer.valueOf(pointerValue.toString());
    }

    public static Object getValue(String key, Map<String, Object> globalMemory, Map<String, Object> localMap, int level) throws FlowException {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        String keys[] = key.split("\\.");
        String currentToken = keys[0];

        String remainingKeys = (keys.length > 1) ? key.substring(currentToken.length() + 1, key.length()) : null;

        if (FlowEngineUtil.isArrayToken(currentToken)) {
            String arrayName = currentToken.substring(0, currentToken.indexOf("["));

            List list = (List) ((level == 1) ? (globalMemory.get(arrayName)) : (localMap.get(arrayName)));

            if (list == null) {
                return null;
            }

            Pair<Integer, List> pair = handleDimensionForGet(list, currentToken, globalMemory);
            int pointer = pair.getLeft();
            list = pair.getRight();

            if (list == null) {
                return null;
            }

            if (keys.length == 1) {
                list.get(pointer);
            } else {
                Object nestedValue = null;
                if (list.size() != 0 || pointer < list.size()) {
                    nestedValue = list.get(pointer);
                }
                if (nestedValue == null) {
                    return null;
                } else {
                    localMap = (Map<String, Object>) nestedValue;
                }
                return getValue(remainingKeys, globalMemory, localMap, ++level);
            }
        } else {
            Map<String, Object> map = null;
            if (level == 1 && keys.length > 1) {
                map = (Map<String, Object>) globalMemory.get(currentToken);
            } else if (keys.length > 1) {
                map = (Map<String, Object>) localMap.get(currentToken);
            } else if (level == 1) {
                map = globalMemory;
            } else {
                map = localMap;
            }

            localMap = map;

            if (localMap == null) {
                return null;
            }
            if (keys.length == 1) {
                return localMap.get(currentToken);
            } else {
                return getValue(remainingKeys, globalMemory, localMap, ++level);
            }
        }
        return null;
    }

    private static Pair<Integer, List> handleDimensionForGet(List list, String token, Map<String, Object> globalMemory) throws FlowException {//a[9][9][8]
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
            int pointer = FlowEngineUtil.extractArrayPointer(pointerVar, globalMemory, new HashMap<>());
            lastPointer = pointer;
            List nextList = null;
            if (list.isEmpty() || pointer > list.size()) {
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
        if (token.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*\\[([^\\]]+\\]\\[)*[^\\]]+\\]$")) {
            return true;
        }
        return false;
    }

}
