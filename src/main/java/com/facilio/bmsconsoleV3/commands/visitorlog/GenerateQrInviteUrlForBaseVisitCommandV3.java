package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.pdf.PdfUtil;
import com.facilio.v3.context.Constants;

public class GenerateQrInviteUrlForBaseVisitCommandV3 extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BaseVisitContextV3> inviteVisitors = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(inviteVisitors)) {
            for(BaseVisitContextV3 inviteVisitor : inviteVisitors) {
                String passCode = VisitorManagementAPI.generatePassCode();
                String qrCode = "visitorLog_" + passCode;
                JSONObject size = new JSONObject();
                size.put("width", 200);
                size.put("height", 200);
                String originalUrl = PdfUtil.exportUrlAsPublicFilePdf("https://app.facilio.com/app/qr?code=" + qrCode, true, null, size, -1,  FileInfo.FileFormat.IMAGE);
                String baseUrl = FacilioProperties.getClientAppUrl();
                if(!FacilioProperties.isDevelopment() && StringUtils.isNotEmpty(originalUrl)) {
                    inviteVisitor.setQrUrl(baseUrl.concat(originalUrl));
                }
                else {
                    inviteVisitor.setQrUrl(originalUrl);
                }

                inviteVisitor.setPassCode(passCode);

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_VISIT);


                Map<String, Object> updateMap = new HashMap<>();
                FacilioField qrUrlField = modBean.getField("qrUrl", module.getName());
                FacilioField otpField = modBean.getField("passCode", module.getName());

                updateMap.put("qrUrl", inviteVisitor.getQrUrl());
                updateMap.put("passCode", passCode);

                List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                updatedfields.add(qrUrlField);
                updatedfields.add(otpField);
                UpdateRecordBuilder<BaseVisitContextV3> updateBuilder = new UpdateRecordBuilder<BaseVisitContextV3>()
                        .module(module)
                        .fields(updatedfields)
                        .andCondition(CriteriaAPI.getIdCondition(inviteVisitor.getId(), module));

                ;
                updateBuilder.updateViaMap(updateMap);

            }

        }
        return false;
    }
}

