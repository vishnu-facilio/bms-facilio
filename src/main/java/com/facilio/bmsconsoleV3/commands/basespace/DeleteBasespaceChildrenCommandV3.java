package com.facilio.bmsconsoleV3.commands.basespace;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.tika.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteBasespaceChildrenCommandV3 extends FacilioCommand {
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
                List<V3BaseSpaceContext> basespaces = V3SpaceAPI.getBaseSpaceChildren(moduleName,id);
                if(CollectionUtils.isNotEmpty(basespaces)){
                    List<Long> basespaceIds = basespaces.stream().map(V3BaseSpaceContext::getId).collect(Collectors.toList());
                    basespaceIds.add(id);
                    Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
                    Criteria assetDeleteCriteria = new Criteria();
                    assetDeleteCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("space"),basespaceIds, NumberOperators.EQUALS));
                    //delete assets for basespaces
                    List<V3AssetContext> assets = V3ResourceAPI.getAssetsForSpaces(basespaceIds);
                    if(CollectionUtils.isNotEmpty(assets))  {
                        Map<String, Object> deleteObj = new HashMap<>();
                        deleteObj.put(ModuleFactory.getAssetsModule().getName(), assets.stream().map(V3AssetContext::getId).collect(Collectors.toList()));
                        V3Util.deleteRecords(ModuleFactory.getAssetsModule().getName(), deleteObj, null, null,false);
                    }
                    //V3RecordAPI.deleteRecords(FacilioConstants.ContextNames.ASSET,assetDeleteCriteria,true);
                }
                //delete child spaces for basespaces
                V3RecordAPI.deleteRecords(baseSpaceModule.getName(),criteria,true);
            }
        }
        return false;
    }
}
