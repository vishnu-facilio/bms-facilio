package com.facilio.telemetry.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cache.CacheUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FacilioModule;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.scriptengine.context.ErrorListener;
import com.facilio.telemetry.context.TelemetryCriteriaCacheContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddTelemetryCriteriaNameSpaceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<TelemetryCriteriaContext> telemetryCriteriaList = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(telemetryCriteriaList)) {
            for (TelemetryCriteriaContext telemetryCriteria : telemetryCriteriaList) {
                NameSpaceContext nameSpaceContext = telemetryCriteria.getNamespace();
                if (nameSpaceContext != null) {
                    nameSpaceContext.setStatus(false);
                    Long nsId;
                    nameSpaceContext.setWorkflowId(addWorkflowCriteria(nameSpaceContext.getWorkflowContext()));
                    if (nameSpaceContext.getId() != null && nameSpaceContext.getId() > 0) {
                        Constants.getNsBean().updateNamespace(nameSpaceContext);
                        nsId = telemetryCriteria.getNamespace().getId();
                    } else {
                        nsId = Constants.getNsBean().addNamespace(nameSpaceContext, null);
                        Constants.getNsBean().addNamespaceFields(nsId,telemetryCriteria.getNamespace().getFields());
                    }
                    telemetryCriteria.setNamespaceId(nsId);
                }
                FacilioCache<String, TelemetryCriteriaCacheContext> telemetryCriteriaCache = LRUCache.getTelemetryCriteriaCache();
                String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
                telemetryCriteriaCache.removeStartsWith(key);
            }
        }
        return false;
    }

    private static Long addWorkflowCriteria(WorkflowContext workflow) throws Exception {
        if(workflow != null) {
            workflow.setIsLogNeeded(true);
            workflow.setIsV2Script(true);
            FacilioChain chain = TransactionChainFactory.getAddWorkflowChain();
            if(workflow.getId() > 0) {
                chain = TransactionChainFactory.getUpdateWorkflowChain();
            }
            FacilioContext context = chain.getContext();
            context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
            try {
                chain.execute();
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            if(context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR) != null) {
                ErrorListener errorListener = (ErrorListener) context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR);
                throw new RESTException(ErrorCode.VALIDATION_ERROR,errorListener.getErrorsAsString());
            }

            WorkflowContext addedWorkflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
            if(addedWorkflow != null && addedWorkflow.getId() > 0) {
                return addedWorkflow.getId();
            }
        }
        return null;
    }
}