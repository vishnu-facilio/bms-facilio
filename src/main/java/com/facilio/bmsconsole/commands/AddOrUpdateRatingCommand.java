package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.RatingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddOrUpdateRatingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        RatingContext rating = (RatingContext) context.get(FacilioConstants.ContextNames.RECORD);
        if (parentModuleName != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(parentModuleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid parent module");
            }
            List<FacilioModule> subModules = modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.RATING);
            if (CollectionUtils.isEmpty(subModules)) {
                throw new IllegalArgumentException("There is no rating module configured for " + module.getDisplayName());
            }
            if (subModules.size() > 1) {
                throw new IllegalArgumentException("There are more than one Rating module configured for " + module.getDisplayName());
            }

            FacilioModule ratingModule = subModules.get(0);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, ratingModule.getName());

            validateRatingContext(rating);
        }
        return false;
    }

    private void validateRatingContext(RatingContext rating) {
        if (StringUtils.isEmpty(rating.getName())) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (rating.getParent() == null || rating.getParent().getId() <= 0) {
            throw new IllegalArgumentException("Invalid parent Id");
        }
    }
}
