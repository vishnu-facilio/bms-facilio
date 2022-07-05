package com.facilio.emailtemplate.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class EmailStructureUtil {

    private static final Logger LOGGER = LogManager.getLogManager().getLogger(EmailStructureUtil.class.getSimpleName());

    public static Template getEmailStructureFromMap(Map<String, Object> templateMap) throws Exception {
        EMailStructure eMailStructure = FieldUtil.getAsBeanFromMap(templateMap, EMailStructure.class);

        User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
        try (InputStream body = FacilioFactory.getFileStore(superAdmin.getId()).readFile(eMailStructure.getBodyId())) {
            eMailStructure.setMessage(IOUtils.toString(body));
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Exception occurred ", e);
        }

        return eMailStructure;
    }

    public static boolean getDraft(long id) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getEMailStructureModule().getTableName())
                .select(FieldFactory.getEMailStructureFields())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getEMailStructureModule()));

        EMailStructure eMailStructure = FieldUtil.getAsBeanFromMap(selectRecordBuilder.fetchFirst(),EMailStructure.class);
        boolean draft = eMailStructure.isDraft();
        return draft;
    }
}
