package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.GlobalScopeVariableEvaluationContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ValueGenerator;
import com.opensymphony.xwork2.ActionContext;
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
import org.apache.struts2.dispatcher.Parameter;
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
            Parameter parameter = ActionContext.getContext().getParameters().get("globalScopeInterceptor");
            if(parameter == null || parameter.getValue() == null || parameter.getValue().equals("true")) {
                if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_VARIABLE)) {
                    computeScopeVariable();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error at compute scope variable",e);
            if(ServletActionContext.getRequest() != null) {
                LOGGER.error("x-switch-value -> " + ServletActionContext.getRequest().getHeader("X-Switch-Value"));
            }
        }
        return invocation.invoke();
    }

    public static void computeScopeVariable() throws Exception {
        Long appId = null;
        if(AccountUtil.getCurrentApp() != null) {
            appId = AccountUtil.getCurrentApp().getId();
        }
        if(appId != null && appId > 0) {
            GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
            JSONObject switchMap = GlobalScopeUtil.getFilterRecordIdMapFromHeader();
            Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVsValueGen = scopeBean.getAllScopeVariableAndValueGen(appId);
            if (scopeVsValueGen != null && !scopeVsValueGen.isEmpty()) {
                if (scopeVsValueGen != null && switchMap != null && !scopeVsValueGen.keySet().containsAll(switchMap.keySet())) {
                    LOGGER.error("Invalid switch variable linkname given. Throws 400 Bad Request");
//                    return "invalid";
                }
                for (Map.Entry<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> entry : scopeVsValueGen.entrySet()) {
                    Pair<GlobalScopeVariableContext, ValueGeneratorContext> scopeVariableValGenPair = entry.getValue();
                    ValueGeneratorContext valueGeneratorContext = scopeVariableValGenPair.getRight();
                    String ValueGenLinkName = valueGeneratorContext.getLinkName();
                    GlobalScopeVariableContext gs = scopeVariableValGenPair.getLeft();
                    List<Long> switchValues = GlobalScopeUtil.getFilterRecordIdFromHeader(switchMap, entry.getKey());
                    List<Long> computedValGenIds = null;
                    List<Long> evaluatedValues = null;
                    if(needValueGeneration(valueGeneratorContext)) {
                        if (gs.getType() == GlobalScopeVariableContext.Type.SCOPED.getIndex()) {
//                            computedValGenIds = computeAndSetValueGenerators(ValueGenLinkName);
                        }
                        if (CollectionUtils.isNotEmpty(switchValues)) {
                            if (computedValGenIds != null) { //When empty list all values are accessible
                                if (!computedValGenIds.containsAll(switchValues)) {
                                    LOGGER.error("Provided switch value is not accessible by the user or not a valid record id. Throws 400 Bad Request");
//                                  return "invalid";
                                }
                            }
                            evaluatedValues = switchValues;
                        } else {
                            evaluatedValues = computedValGenIds;
                        }
                        GlobalScopeVariableEvaluationContext scopeVariableEvaluation = new GlobalScopeVariableEvaluationContext(gs.getLinkName(), gs.getApplicableModuleName(), gs.getApplicableModuleId(), gs.getScopeVariableModulesFieldsList(), evaluatedValues, gs.getTypeEnum());
                        setGlobalScopeVariableValues(scopeVariableEvaluation);
                    }
                }
            } else {
                if (switchMap != null) {
//                    LOGGER.error("When no scope variable is active and value from switch throw 400");
//                    return "invalid";
                }
            }
        }
    }

    private static void setGlobalScopeVariableValues(GlobalScopeVariableEvaluationContext scopeVariable) {
        if(scopeVariable != null && StringUtils.isNotEmpty(scopeVariable.getLinkName())) {
            Map<String, GlobalScopeVariableEvaluationContext> globalScopeVariableValues = AccountUtil.getGlobalScopeVariableValues();
            if (globalScopeVariableValues == null) {
                globalScopeVariableValues = new HashMap<>();
            }
            globalScopeVariableValues.put(scopeVariable.getLinkName(), scopeVariable);
            AccountUtil.setGlobalScopeVariableValues(globalScopeVariableValues);
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
                    if(value.equals(FacilioConstants.ContextNames.ALL_VALUE)) {
                        ids = null;
                    } else {
                        ids = Arrays.stream(value.split(",")).map(Long::parseLong).collect(Collectors.toList());
                    }
                }
                AccountUtil.setValueGenerator(valueGenerators);
                return ids;
            }
        }
        return new ArrayList<>();
    }
    private static JSONObject getFilterRecordIdMapFromHeader() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String switchVariable = request.getHeader("X-Switch-Value");
        if (StringUtils.isNotEmpty(switchVariable)) {
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

    private static boolean needValueGeneration(ValueGeneratorContext valueGeneratorContext) throws Exception {
        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_SUBQUERY)) {
            return true;
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_SUBQUERY)) {
            if(valueGeneratorContext.getValueGeneratorType() == null) {
                return true;
            }
            else if(valueGeneratorContext.getValueGeneratorType() == ValueGeneratorContext.ValueGeneratorType.IDENTIFIER) {
                return true;
            }
            else if(valueGeneratorContext.getValueGeneratorType() == ValueGeneratorContext.ValueGeneratorType.SUB_QUERY) {
                return false;
            }
        }
        return false;
    }
}