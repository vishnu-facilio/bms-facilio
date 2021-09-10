package com.facilio.bmsconsoleV3.commands.asset;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class ValidateQrValueCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        List<Long> recordIds = assetList.stream().map(V3AssetContext::getId).collect(Collectors.toList());
        for(V3AssetContext asset:assetList){
            String qrValue = asset.getQrVal();
            if (StringUtils.isNotEmpty(qrValue)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                String moduleName = FacilioConstants.ContextNames.ASSET;
                FacilioModule module = modBean.getModule(moduleName);
                List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
                if (fields == null) {
                    fields = modBean.getAllFields(moduleName);
                }
                if (CollectionUtils.isNotEmpty(recordIds)) {
                    SelectRecordsBuilder<V3AssetContext> builder = new SelectRecordsBuilder<V3AssetContext>()
                            .module(module)
                            .beanClass(V3AssetContext.class)
                            .select(fields)
                            .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
                    List<V3AssetContext> records = builder.get();

                    for (V3AssetContext record : records) {
                        if (!(qrValue.equals(record.getQrVal()))) {
                            List<V3AssetContext> qrValueRecords = checkQrValue(module, fields, qrValue);
                            if (qrValueRecords.size() >= 1) {
                                throw new IllegalArgumentException("QR Value already exists");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private List<V3AssetContext> checkQrValue(FacilioModule module,List<FacilioField> fields,String qrValue) throws Exception{
        SelectRecordsBuilder<V3AssetContext> selectRecordsBuilder = new SelectRecordsBuilder<V3AssetContext>()
                .module(module)
                .beanClass(V3AssetContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("QR_VALUE", "qrVal", qrValue, StringOperators.IS));
        List<V3AssetContext> qrValueRecords = selectRecordsBuilder.get();
        return qrValueRecords;
    }
}
