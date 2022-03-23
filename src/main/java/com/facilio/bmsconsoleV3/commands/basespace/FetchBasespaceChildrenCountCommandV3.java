package com.facilio.bmsconsoleV3.commands.basespace;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.tika.utils.StringUtils;
import java.util.List;
import java.util.Map;

public class FetchBasespaceChildrenCountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        List<Long> recordIds = (List<Long>) context.get("recordIds");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
            if (CollectionUtils.isNotEmpty(recordIds)) {
                for (Long id : recordIds) {
                    Criteria criteria = V3SpaceAPI.getBaseSpaceChildrenCriteria(moduleName,id);
                    if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchChildCount")) {
                        List<V3BaseSpaceContext> basespaces = V3RecordAPI.getRecordsListWithSupplements (baseSpaceModule.getName(), null, V3BaseSpaceContext.class, criteria, null);
                        if(CollectionUtils.isNotEmpty(basespaces)){
                            throw new RESTException(ErrorCode.DEPENDENCY_EXISTS, String.valueOf(constructWarningMessage(basespaces,moduleName)));
                        }
                        else{
                            throw new RESTException(ErrorCode.DEPENDENCY_EXISTS, String.valueOf("clear"));
                        }
                    }
                }
            }

        return false;
    }

    private String constructWarningMessage(List<V3BaseSpaceContext> basespaces,String moduleName) {
        boolean hasBuilding = false,hasFloor = false,hasSpace = false,hasIndependentSpace = false;
        String message = "All the underlying";
        for (V3BaseSpaceContext basespace : basespaces){
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.BUILDING && !hasBuilding){
                hasBuilding = true;
            }
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.FLOOR && !moduleName.equals(FacilioConstants.ContextNames.SITE) && !hasFloor){
                hasFloor = true;
            }
            if(basespace.getSpaceTypeEnum() == V3BaseSpaceContext.SpaceType.SPACE && !hasSpace) {
                hasSpace = true;
            }
            if(moduleName.equals(FacilioConstants.ContextNames.SITE) && basespace.getBuilding() == null && !hasIndependentSpace){
                hasIndependentSpace = true;
            }
            if(moduleName.equals(FacilioConstants.ContextNames.BUILDING) && basespace.getFloor() == null && !hasIndependentSpace){
                hasIndependentSpace = true;
            }
        }
        if(hasBuilding){
            message = message + StringUtils.SPACE + "buildings";
        }
        if(hasFloor){
            if(hasBuilding){
                message = message + StringUtils.SPACE + "and";
            }
            message = message + StringUtils.SPACE + "floors";
        }
        if(moduleName.equals(FacilioConstants.ContextNames.SITE) || moduleName.equals(FacilioConstants.ContextNames.BUILDING)){
            if(hasIndependentSpace) {
                if (hasBuilding || hasFloor) {
                    message = message + StringUtils.SPACE + "and";
                }
                message = message + StringUtils.SPACE + "spaces";
            }
        }
        else if(hasSpace){
            if(hasBuilding || hasFloor){
                message = message + StringUtils.SPACE + "and";
            }

            message = message + StringUtils.SPACE + "spaces";
        }
        return message + StringUtils.SPACE + String.valueOf("will be deleted.");
    }
}
