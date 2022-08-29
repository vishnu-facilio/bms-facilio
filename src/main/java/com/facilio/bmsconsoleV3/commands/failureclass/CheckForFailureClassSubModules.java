//package com.facilio.bmsconsoleV3.commands.failureclass;
//
//import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
//import com.facilio.command.FacilioCommand;
//import com.facilio.constants.FacilioConstants;
//import com.facilio.v3.context.Constants;
//import com.facilio.v3.context.SubFormContext;
//import com.facilio.v3.context.V3Context;
//import org.apache.commons.chain.Context;
//import org.apache.commons.collections4.CollectionUtils;
//
//import java.util.List;
//import java.util.Map;
//
//public class CheckForFailureClassSubModules extends FacilioCommand {
//    @Override
//    public boolean executeCommand(Context context) throws Exception {
//        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
//        List<V3FailureClassContext> failureclassList = recordMap.get("failureclass");
//        for(V3FailureClassContext failureclass : failureclassList) {
//            Map<String, List<SubFormContext>> relations = failureclass.getRelations();
//            if (relations != null) {
//                List<SubFormContext> failureCodeProblemList = relations.get(FacilioConstants.ContextNames.FAILURE_CODE_PROBLEMS);
//                if (CollectionUtils.isEmpty(failureCodeProblemList)) {
//                    for(SubFormContext problem: failureCodeProblemList){
//                        List<V3Context> data = problem.getData();
//                        for(V3Context problemData : data){
//                            Map<String,Object> prob = problemData.getData().get("failureCode");
//
//                        }
//                    }
//                    List<SubFormContext> failureCodeCauseList = relations.get(FacilioConstants.ContextNames.FAILURE_CODE_CAUSES);
//                    if (CollectionUtils.isNotEmpty(failureCodeCauseList)) {
//                        throw new IllegalArgumentException("Failed to add cause. Please add a problem first");
//                    }else{
//                        List<SubFormContext> failureCodeRemediesList = relations.get(FacilioConstants.ContextNames.FAILURE_CODE_REMEDIES);
//                        if (CollectionUtils.isNotEmpty(failureCodeRemediesList)) {
//                            throw new IllegalArgumentException("Failed to add remedy. Please add a cause first");
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//}
