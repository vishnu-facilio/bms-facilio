package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdatePlansAndSkillsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks =  recordMap.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask: serviceTasks){
                        ServiceTaskContext serviceTaskContext = new ServiceTaskContext();
                        serviceTaskContext.setId(serviceTask.getId());

                        ServiceOrderContext serviceOrderContext = new ServiceOrderContext();
                        serviceOrderContext.setId(serviceTask.getServiceOrder().getId());

                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedItems())){
                            List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = serviceTask.getServiceOrderPlannedItems();
                            for(ServiceOrderPlannedItemsContext plannedItem: serviceOrderPlannedItems){
                                plannedItem.setServiceTask(serviceTaskContext);
                                plannedItem.setServiceOrder(serviceOrderContext);
                            }
                            serviceTask.setServiceOrderPlannedItems(serviceOrderPlannedItems);
                        }
                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedTools())){
                            List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = serviceTask.getServiceOrderPlannedTools();
                            for(ServiceOrderPlannedToolsContext plannedTool: serviceOrderPlannedTools){
                                plannedTool.setServiceTask(serviceTaskContext);
                                plannedTool.setServiceOrder(serviceOrderContext);
                            }
                            serviceTask.setServiceOrderPlannedTools(serviceOrderPlannedTools);
                        }
                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedServices())){
                            List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = serviceTask.getServiceOrderPlannedServices();
                            for(ServiceOrderPlannedServicesContext serviceOrderPlannedService : serviceOrderPlannedServices){
                                serviceOrderPlannedService.setServiceTask(serviceTaskContext);
                                serviceOrderPlannedService.setServiceOrder(serviceOrderContext);
                            }
                            serviceTask.setServiceOrderPlannedServices(serviceOrderPlannedServices);
                        }
                        if(CollectionUtils.isNotEmpty(serviceTask.getSkills())){
                            List<ServiceTaskSkillsContext> serviceTaskSkills = serviceTask.getSkills();
                            for(ServiceTaskSkillsContext serviceTaskSkill : serviceTaskSkills){
                                serviceTaskSkill.setLeft(serviceTaskContext);

                                ServiceSkillsContext serviceSkillContext = new ServiceSkillsContext();
                                serviceSkillContext.setId(serviceTaskSkill.getId());
                                serviceTaskSkill.setRight(serviceSkillContext);
                            }
                            serviceTask.setSkills(serviceTaskSkills);
                        }

                    }
            V3Util.processAndUpdateBulkRecords(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK), recordMap.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK),  FieldUtil.getAsMapList(serviceTasks, ServiceTaskContext.class)  ,null,null,null,null,null,null,null,null,false,false);
        }
        return false;
    }
}
