package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GetPageSectionCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetPageSectionCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(id == null || id <= 0){
            LOGGER.info("Invalid id to get section");
            throw new IllegalArgumentException("Invalid id");
        }

        PageSectionContext section = CustomPageAPI.getSection(id);

        if(section == null ){
            LOGGER.info("Section does not exists");
            throw new IllegalArgumentException("Section does not exists");
        }

        context.put(FacilioConstants.CustomPage.SECTION, section);
        return false;
    }
}