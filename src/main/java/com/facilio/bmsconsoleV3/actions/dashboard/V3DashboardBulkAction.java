package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.pdf.PdfUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class V3DashboardBulkAction extends V3Action {
    private List<Long> dashboardIds;
    private Long targetFolderId;
    private List<String> urls;
    private JSONObject additionalInfo;
    public String bulkMove() throws Exception {
        if(targetFolderId <= 0 || targetFolderId == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Folder id cannot be empty");
        }
        else {
            if(!CollectionUtils.isEmpty((dashboardIds))) {
                List<Boolean> status = new ArrayList<>();
                for(Long dashboardId: dashboardIds) {
                    if(dashboardId >0 && dashboardId != null){
                        DashboardContext dashboard = DashboardUtil.getDashboard(dashboardId);
                        FacilioChain chain = TransactionChainFactoryV3.getMoveToDashboardChain();
                        FacilioContext context = chain.getContext();
                        context.put("folder_id", targetFolderId);
                        context.put("dashboard_link", dashboard.getLinkName() );
                        chain.execute();
                        status.add(context.get("isMoved") != null && (Boolean)context.get("isMoved"));
                    }
                }
                if(!status.contains(false)){
                    setData("result", V3Action.SUCCESS);
                }
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "DashboardId list cannot be empty");
            }
        }
        return V3Action.SUCCESS;
    }
    public String bulkExport() throws Exception {
        if(!CollectionUtils.isEmpty(urls)){
            List<String> downloadUrl = new ArrayList<>();
            for(String url: urls){
                String fileUrl = PdfUtil.exportUrlAsPdf(url, false, "download-"+System.currentTimeMillis(), additionalInfo, FileInfo.FileFormat.PDF);
                if(!fileUrl.isEmpty() && !fileUrl.equals("")){
                    downloadUrl.add(fileUrl);
                }
            }
            setData("fileUrl",downloadUrl);
        }
        else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "page url list cannot be empty");
        }
        return V3Action.SUCCESS;
    }
    public String bulkDelete() throws Exception {
        if(!CollectionUtils.isEmpty(dashboardIds)) {
            for(Long dashboardId: dashboardIds) {
                if(dashboardId > 0 && dashboardId != null) {

                    List<DashboardTabContext>  tabs = DashboardUtil.getDashboardTabs(dashboardId);
                    if (!CollectionUtils.isEmpty(tabs)) {
                        for (DashboardTabContext tab : tabs){
                            Long tabId = tab.getId();
                            if(tabId>0) {
                                DashboardUtil.deleteDashboardTab(tabId);
                            }
                        }
                    }

                    FacilioContext context = new FacilioContext();
                    context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboardId);

                    FacilioChain deleteDashboardChain = TransactionChainFactory.getDeleteDashboardChain();
                    deleteDashboardChain.execute(context);

                    if(context.get(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE) != null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, (String) context.get(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE));
                    }
                }
            }
        }
        else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "DashboardId list cannot be empty");
        }
        return V3Action.SUCCESS;
    }
}
