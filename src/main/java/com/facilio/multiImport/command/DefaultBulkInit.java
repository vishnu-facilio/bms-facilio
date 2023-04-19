package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DefaultBulkInit extends FacilioCommand {
    FacilioModule module = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (MapUtils.isNotEmpty(ImportConstants.getInsertRecordMap(context))) {
            return false;
        }
        String moduleName = Constants.getModuleName(context) ;
        module = ChainUtil.getModule(moduleName);

        Collection<Pair<Long, Map<String, Object>>> insertRecords = ImportConstants.getInsertRecords(context);

        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = new HashMap<>();
        List<Pair<Long, ModuleBaseWithCustomFields>> insertRecordsPair = new ArrayList<>();

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        List<ModuleBaseWithCustomFields> records = new LinkedList<>();

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);

        if(CollectionUtils.isEmpty(insertRecords)){
            insertRecordMap.put(moduleName,insertRecordsPair);
            ImportConstants.setInsertRecordMap(context, insertRecordMap);
            Constants.setRecordMap(context, recordMap);
            return false;
        }

        for (Pair<Long, Map<String, Object>> rawObj : insertRecords) {

            if(rawObj == null){
                continue;
            }

            Long logId = rawObj.getLeft();
            Map<String, Object> rawRecord = rawObj.getRight();
            ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(rawRecord, beanClass);
            Pair logIdVsReordPair = new MutablePair(logId, record);

            insertRecordsPair.add(logIdVsReordPair);
            records.add(record);
        }
        insertRecordMap.put(moduleName, insertRecordsPair);
        recordMap.put(moduleName,records);

        ImportConstants.setInsertRecordMap(context, insertRecordMap);
        Constants.setRecordMap(context, recordMap);
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);

        if (module.isCustom()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField localIdField = modBean.getField("localId", module.getName());
            if (localIdField != null) {
                context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            }
        }
        return false;
    }
}
