package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j

public class InsertReadingDataMetaForMultiImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("InsertReadingDataMetaForMultiImportCommand start time:"+System.currentTimeMillis());
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if(!MultiImportApi.isResourceExtendedModule(module)){
            return false;
        }

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        List<ResourceContext> resources = new ArrayList<>();

        for(ModuleBaseWithCustomFields record : records){
            V3ResourceContext v3ResourceContext = (V3ResourceContext) record;
            ResourceContext resource = new ResourceContext();
            resource.setId(v3ResourceContext.getId());
            resource.setResourceType(v3ResourceContext.getResourceType());
            resources.add(resource);
        }

        ReadingsAPI.updateReadingDataMeta(resources);

        LOGGER.info("InsertReadingDataMetaForMultiImportCommand end time:"+System.currentTimeMillis());
        return false;
    }
}
