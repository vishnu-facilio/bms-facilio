package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import com.facilio.bmsconsole.util.QRCodeGenerator;
import com.facilio.services.filestore.PublicFileUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class GenerateQrInviteUrlForBaseVisitCommandV3 extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Boolean withQrUrl = (Boolean) context.get(FacilioConstants.ContextNames.WITH_QRURL);
        if(withQrUrl == null || withQrUrl == true) {
            List<BaseVisitContextV3> inviteVisitors = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(inviteVisitors)) {
                for (BaseVisitContextV3 inviteVisitor : inviteVisitors) {
                    if (StringUtils.isEmpty(inviteVisitor.getPassCode()) && StringUtils.isEmpty(inviteVisitor.getQrUrl())) {
                        String passCode = VisitorManagementAPI.generatePassCode();
                        String qrCode = "visitorLog_" + passCode;
                        String originalUrl=null;
                        String qrName= "inviteQr-"+System.currentTimeMillis()+".png";
                        File qrFile= QRCodeGenerator.generateQR(qrName,qrCode,400,400);
                        if(qrFile != null)
                        {
                            originalUrl= PublicFileUtil.createPublicFile(qrFile,qrName,"png","image/png", -1);
                        }
                        String baseUrl = FacilioProperties.getClientAppUrl();
                        if (!FacilioProperties.isDevelopment() && StringUtils.isNotEmpty(originalUrl)) {
                            inviteVisitor.setQrUrl(baseUrl.concat(originalUrl));
                        } else {
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

            }
            context.put(FacilioConstants.ContextNames.WITH_QRURL, false);
        }
        else
        {
            context.put(FacilioConstants.ContextNames.WITH_QRURL, true);
        }
        return false;
    }
}

