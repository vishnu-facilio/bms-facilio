package com.facilio.classification.command;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationDataContext;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddClassificationDataCommand extends BaseClassificationDataCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3Context> records = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(Constants.getModuleName(context));
        List<FacilioModule> classificationDataModuleList = modBean.getSubModules(parentModule.getModuleId(), FacilioModule.ModuleType.CLASSIFICATION_DATA);

        Set<Long> deleteRecordIds = new HashSet<>();

        records = records.stream().filter(record -> record.getClassification() != null).collect(Collectors.toList()); //filter clasification holding record only

        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        if (CollectionUtils.isEmpty(classificationDataModuleList) || classificationDataModuleList.size() > 1) {  //there should only one ClassificationData module
            throw new IllegalArgumentException("ClassificationData module may be empty or more than one entry");
        }
        FacilioModule classificationDataModule = classificationDataModuleList.get(0);

        List<ClassificationDataContext> classificationDataList = new ArrayList<>();
        addClassificationDatatoClassificationDatList(records, parentModule, deleteRecordIds, classificationDataList, classificationDataModule);

        ClassificationUtil.addClassificationData(classificationDataList, classificationDataModule);
        return false;
    }
}
