package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ValueGenerator;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class GlobalScopeVariableInterceptor extends AbstractInterceptor {

    @Override
    @WithSpan
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_VARIABLE)) {
                return computeScopeVariable(invocation);
            }
        } catch (Exception e) {
            LOGGER.error("Error at compute scope variable");
        }
        return invocation.invoke();
    }

    public static String computeScopeVariable(ActionInvocation invocation) throws Exception {
        Long appId = null;
        if(AccountUtil.getCurrentApp() != null){
            appId = AccountUtil.getCurrentApp().getId();
        }
        if(appId != null && appId > 0) {
            GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
            JSONObject switchMap = getFilterRecordIdMapFromHeader();
            Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVsValueGen = scopeBean.getAllScopeVariableAndValueGen(appId);
            if (scopeVsValueGen != null && !scopeVsValueGen.isEmpty()) {
                if (scopeVsValueGen != null && switchMap != null && !scopeVsValueGen.keySet().containsAll(switchMap.keySet())) {
                    return "invalid"; //Invalid switch variable linkname given. Throws 400 Bad Request.
                }
                for (Map.Entry<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> entry : scopeVsValueGen.entrySet()) {
                    Pair<GlobalScopeVariableContext, ValueGeneratorContext> scopeVariableValGenPair = entry.getValue();
                    ValueGeneratorContext valueGeneratorContext = scopeVariableValGenPair.getRight();
                    String ValueGenLinkName = valueGeneratorContext.getLinkName();
                    GlobalScopeVariableContext scopeVariable = scopeVariableValGenPair.getLeft();
                    List<Long> switchValues = getFilterRecordIdFromHeader(switchMap, entry.getKey());
                    List<Long> computedValGenIds = computeAndSetValueGenerators(ValueGenLinkName);
                    if (CollectionUtils.isNotEmpty(switchValues)) {
                        if (CollectionUtils.isNotEmpty(computedValGenIds)) { //When empty list all values are accessible
                            if (!computedValGenIds.containsAll(switchValues)) {
                                return "invalid"; //Provided switch value is not accessible by the user or not a valid record id. Throws 400 Bad Request.
                            }
                        }
                        scopeVariable.setValues(switchValues);
                    } else {
                        scopeVariable.setValues(computedValGenIds);
                    }
                    setGlobalScopeVariableValues(scopeVariable);
                }
            } else {
                if (switchMap != null) {
                    return "invalid"; //When no scope variable is active and value from switch throw 400.
                }
            }
        }
        return invocation.invoke();
    }

    private static void setGlobalScopeVariableValues(GlobalScopeVariableContext scopeVariable) {
        if(scopeVariable != null && StringUtils.isNotEmpty(scopeVariable.getLinkName())) {
            Map<String, GlobalScopeVariableContext> globalScopeVariableValues = AccountUtil.getGlobalScopeVariableValues();
            if (globalScopeVariableValues == null) {
                globalScopeVariableValues = new HashMap<>();
            }
            if (scopeVariable.getValues() != null) {
                globalScopeVariableValues.put(scopeVariable.getLinkName(), scopeVariable);
                AccountUtil.setGlobalScopeVariableValues(globalScopeVariableValues);
            }
        }
    }

    private static List<Long> computeAndSetValueGenerators(String linkName) throws Exception {
        if (linkName != null) {
            ValueGenerator valueGenerator = ScopingUtil.getValueGeneratorForLinkName(linkName);
            if (valueGenerator != null) {
                String value = "";
                List<Long> ids = new ArrayList<>();
                Map<String, String> valueGenerators = null;
                if (MapUtils.isNotEmpty(AccountUtil.getValueGenerator())) {
                    valueGenerators = AccountUtil.getValueGenerator();
                } else {
                    valueGenerators = new HashMap<>();
                }
                if (valueGenerators.containsKey(linkName)) {
                    value = valueGenerators.get(linkName);
                }
                else {
                    value = ScopeOperator.SCOPING_IS.getEvaluatedValues(valueGenerator);
                    valueGenerators.put(linkName, value);
                }
                if(value != null && !value.equals(Strings.EMPTY)) {
                    ids = Arrays.stream(value.split(",")).map(Long::parseLong).collect(Collectors.toList());
                }
                AccountUtil.setValueGenerator(valueGenerators);
                return ids;
            }
        }
        return null;
    }

    private static JSONObject getFilterRecordIdMapFromHeader() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String switchVariable = request.getHeader("x-switch-value");
        if (switchVariable != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(switchVariable);
            if (decodedBytes != null) {
                String decodedString = new String(decodedBytes);
                if (decodedString != null) {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(decodedString);
                    if (json != null) {
                        return json;
                    }
                }
            }
        }
        return null;
    }

    private static List<Long> getFilterRecordIdFromHeader(JSONObject switchMap, String linkName) throws Exception {
        if (switchMap != null && !switchMap.isEmpty() && switchMap.containsKey(linkName)) {
            JSONArray arrayList = (JSONArray) switchMap.get(linkName);
            if (arrayList != null && !arrayList.isEmpty()) {
                List<Long> list = new ArrayList<>();
                for (Object id : arrayList) {
                    Long recordId = Long.parseLong(String.valueOf(id));
                    list.add(recordId);
                }
                return list;
            }
        }
        return null;
    }
}