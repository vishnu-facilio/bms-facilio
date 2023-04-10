package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class AddPageSectionCommand extends FacilioCommand {
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(AddPageSectionCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long columnId = (long) context.get(FacilioConstants.CustomPage.COLUMN_ID);
        if(columnId<=0){
            LOGGER.error("Invalid Column Id For Creating Section");
            throw new IllegalArgumentException("Invalid Column Id For Creating Section");
        }

        PageSectionContext section = (PageSectionContext) context.get(FacilioConstants.CustomPage.SECTION);
        FacilioModule sectionsModule = ModuleFactory.getPageSectionsModule();

        if(section != null) {

            FacilioField columnIdField = FieldFactory.getNumberField("columnId", "COLUMN_ID", sectionsModule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(columnIdField, String.valueOf(columnId)));

            section.setColumnId(columnId);

            if (section.getSequenceNumber() <= 0) {
                double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(sectionsModule, criteria);
                section.setSequenceNumber(sequenceNumber+10);
            }


            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = section.getDisplayName() != null ? section.getDisplayName() : "section";
            name = CustomPageAPI.getUniqueName(sectionsModule, criteria, columnIdField, columnId, name, isSystem);
            section.setName(name);

            section.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            section.setSysCreatedTime(System.currentTimeMillis());

            CustomPageAPI.insertPageSectionToDB(section);
            context.put(FacilioConstants.CustomPage.SECTION_ID, section.getId());
        }

        return false;
    }

}