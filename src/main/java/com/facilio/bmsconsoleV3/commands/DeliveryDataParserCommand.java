package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3DeliveriesContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.DeliveryPackageType;
import com.facilio.command.FacilioCommand;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import software.amazon.awssdk.services.textract.model.Block;

import java.io.InputStream;
import java.util.*;

import static com.facilio.aws.util.AwsUtil.detectDocText;

@Log4j
public class DeliveryDataParserCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(currentOrg, "current org null");
        FileStore fileStore = FacilioFactory.getFileStoreFromOrg(currentOrg.getOrgId());
        Map<String, Long> fileMap = new HashMap<>();
        fileMap = Constants.getAttachmentNameVsId(context);

        List<V3DeliveriesContext> deliveriesContextList = new ArrayList<>();
        for (String key : fileMap.keySet()) {
            V3DeliveriesContext delivery = new V3DeliveriesContext();
            InputStream file = fileStore.readFile(fileMap.get(key));
            delivery.setAvatarId(fileMap.get(key));
            if (file == null) {
                continue;
            }
            LOGGER.info("Before ocr parsing");
            List<Block> result = detectDocText(file);
            LOGGER.info("result " + result);

            if (result != null) {
                DeliveryPackageType deliveryPackageType = DeliveryPackageType.detectPackageType(result);
                if (deliveryPackageType != null) {
                    delivery.setCarrier(deliveryPackageType.getCarrierType());
                    String trackingNumber = deliveryPackageType.getTrackingNumber();

                    if (trackingNumber != null) {
                        delivery.setTrackingNumber(trackingNumber);
                    }

                    EmployeeContext employee = deliveryPackageType.getEmployee();
                    if (employee != null) {
                        delivery.setName(employee.getName());
                        V3EmployeeContext v3EmployeeContext = new V3EmployeeContext();
                        v3EmployeeContext.setId(employee.getId());
                        delivery.setEmployee(v3EmployeeContext);
                    } else {
                        delivery.setName("Unidentified");
                    }
                }
            }
            deliveriesContextList.add(delivery);
            file.close();
        }
        context.put("deliveries", deliveriesContextList);
        return false;
    }
}
