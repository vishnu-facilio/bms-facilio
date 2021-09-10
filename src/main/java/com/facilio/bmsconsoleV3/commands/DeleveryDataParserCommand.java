package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3DeliveriesContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.DeliveryPackageType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import software.amazon.awssdk.services.textract.model.Block;

import java.io.InputStream;
import java.util.*;

import static com.facilio.aws.util.AwsUtil.detectDocText;

public class DeleveryDataParserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(currentOrg, "current org null");
        FileStore fileStore = FacilioFactory.getFileStoreFromOrg(currentOrg.getOrgId());
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3DeliveriesContext> deliveries = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(deliveries)) {

            for (V3DeliveriesContext delivery : deliveries) {

                if(delivery.getAvatarId() > 0 && delivery.getName() == null && delivery.getTrackingNumber() == null && delivery.getCarrier() == 0) {
                    InputStream file = fileStore.readFile(delivery.getAvatarId());
                    if(file == null) {
                        continue;
                    }
                    List<Block>  result =  detectDocText(file);
                    if(result != null) {
                        DeliveryPackageType deliveryPackageType = DeliveryPackageType.detectPackageType(result);
                        if(deliveryPackageType != null){
                            delivery.setCarrier(deliveryPackageType.getCarrierType());
                            String trackingNumber = deliveryPackageType.getTrackingNumber();

                            if(trackingNumber != null){
                                delivery.setTrackingNumber(trackingNumber);
                            }

                            EmployeeContext employee = deliveryPackageType.getEmployee();
                            if(employee != null){
                                delivery.setName(employee.getName());
                                V3EmployeeContext v3EmployeeContext = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, employee.getId(), V3EmployeeContext.class);
                                delivery.setEmployee(v3EmployeeContext);
                            } else {
                                delivery.setName("Unidentified");
                            }
                        }
                    }
                    file.close();
                }
            }
        }
        return false;
    }
}
