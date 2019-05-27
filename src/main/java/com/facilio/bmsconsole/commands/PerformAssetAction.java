package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.WorkOrderAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformAssetAction implements Command {

	private static final Logger LOGGER = LogManager.getLogger(PerformAssetAction.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<WorkflowRuleContext> workflowRules = (List<WorkflowRuleContext>) context.get("workflowRules");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		if(workflowRules != null) {
			for(WorkflowRuleContext workflowRule :workflowRules) {
				
				
				boolean isSendMail = false;
				if(isSendMail) {
					
					if(!workflowRule.isActive()) {
						continue;
					}
					Criteria criteria = workflowRule.getCriteria();
					
					SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
							.module(module)
							.beanClass(AssetContext.class)
							.select(modBean.getAllFields(module.getName()))
							;
					
					String expiryDay  = null;
					Long siteId = null;
					int operatorId = -1;
					if(criteria != null && criteria.getConditions() != null && !criteria.getConditions().isEmpty()) {
						 Condition condition = criteria.getConditions().get("1");
						 expiryDay = condition.getValue();
						 operatorId = condition.getOperatorId();
//						 if(criteria.getConditions().get("2") != null) {
//							 condition = criteria.getConditions().get("2");
//							 if(condition.getValue() != null) {
//								 siteId = Long.parseLong(condition.getValue());
//								 List<BaseSpaceContext> baseSpaces = SpaceAPI.getBaseSpaceWithChildren(siteId);
//								 List<Long> baseSpaceids = new ArrayList<>();
//								 baseSpaceids.add(siteId);
//								 for(BaseSpaceContext baseSpace :baseSpaces) {
//									 baseSpaceids.add(baseSpace.getId());
//								 }
//								 condition.setValue(StringUtils.join(baseSpaceids,","));
//							 }
//						 }
					}
					selectBuilder.andCriteria(criteria);
					
					List<AssetContext> assets = selectBuilder.get();
					
					User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getId());
					List<String> assetNameList = new ArrayList<>();
					String domain = AwsUtil.getConfig("clientapp.url");
					int i = 1;
					
					String table = "<table>"+
							"  <tr>"+
							"  	<th>S.No</th>"+
							"    <th>Asset Name</th>"+
							"    <th>Site Name</th>"+
							"    <th>Screen Name</th>"+
							"    <th>Server Name</th>"+
							"    <th>Expire On</th>"+
							"  </tr>";

					for(AssetContext asset :assets) {
						
						assetNameList.add(asset.getName()+"(#"+asset.getId()+")");
						
						String link = domain+"/app/at/asset/"+asset.getId()+"/overview";
						String name = asset.getName()+"(#"+asset.getId()+")";
						name = "<a href="+link+">"+name+"</a>";
						
						String serverName = asset.getDatum("screenname") != null ? asset.getDatum("screenname").toString() : "-";
						String serverno = asset.getDatum("serverno") != null ? asset.getDatum("serverno").toString() : "-";
						
						String siteName = null;
						if(asset.getSpace() != null) {
							BaseSpaceContext space = SpaceAPI.getBaseSpace(asset.getSpace().getId());
							if(space != null) {
								SiteContext site = SpaceAPI.getSiteSpace(space.getSiteId());
								if(site != null) {
									siteName = site.getName(); 
								}
							}
						}
						if(siteName == null) {
							siteName = "---";
						}
						ZonedDateTime expityDate = DateTimeUtil.getDateTime(asset.getWarrantyExpiryDate());
						table = table + "<tr>"+
						"    <td>"+ i++ +".</td>"+
						"    <td>"+name+"</td>"+
						"    <td>"+siteName+"</td>"+
						"    <td>"+serverName+"</td>"+
						"    <td>"+serverno+"</td>"+
						"    <td>"+DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(expityDate)+"</td>"+
						"  </tr>";
					}
					table = table + "</table>";
					
					if(!assetNameList.isEmpty()) {
						
						String assetNames = StringUtils.join(assetNameList,",");
						
						Map<String, Object> placeHolders = new HashMap<>();
						CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
						placeHolders.put("org.superAdmin.email", "shivaraj@facilio.com,"+superAdmin.getEmail()+",murugesan.k@spicinemas.in,manjula.np@spicinemas.in");
//						placeHolders.put("org.superAdmin.email", "krishnan.e@facilio.com");
						placeHolders.put("org.superAdmin.phone", superAdmin.getPhone());
						placeHolders.put("asset.names", assetNames);
						placeHolders.put("asset.url", "\n"+table);
						if(expiryDay != null) {
							Long millis = null;
							if(operatorId == DateOperators.PAST_N_DAY.getOperatorId()) {
								millis = DateTimeUtil.getDayStartTime(-Integer.parseInt(expiryDay));
							}
							else if(operatorId == DateOperators.IN_N_DAY.getOperatorId()) {
								millis = DateTimeUtil.getDayStartTime(Integer.parseInt(expiryDay));
							}
							String formateDate = DateTimeUtil.getFormattedTime(millis, "dd-MM-yyyy");
							placeHolders.put("expired.date", formateDate);
						}
						if(siteId != null) {
							SiteContext site = SpaceAPI.getSiteSpace(siteId);
							placeHolders.put("site.name", site.getName());
						}
						placeHolders.put("mailType", "html");
						for(ActionContext action :workflowRule.getActions()) {
							
							action.executeAction(placeHolders, context, workflowRule, null);
						}
						
					}
				}
				
				else {
					
					module = modBean.getModule("kdm");
					Criteria criteria = workflowRule.getCriteria();
					
					SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
							.module(module)
							.beanClass(ModuleBaseWithCustomFields.class)
							.select(modBean.getAllFields(module.getName()))
							;
					
					Long siteId = null;
					if(criteria != null && criteria.getConditions() != null && !criteria.getConditions().isEmpty()) {
						 Condition condition = criteria.getConditions().get("1");
						 condition.getValue();
						 condition.getOperatorId();
					}
					selectBuilder.andCriteria(criteria);
					
					List<ModuleBaseWithCustomFields> kdms = selectBuilder.get();
					
					LOGGER.log(Priority.ERROR, "kdms111 -- "+kdms.size());
					Multimap<Long,ModuleBaseWithCustomFields> siteWiseMap =  ArrayListMultimap.create();
					for(ModuleBaseWithCustomFields kdm :kdms) {
						
						Map<String, Object> siteMap = (Map<String,Object>) kdm.getDatum("site");
						
						siteId = (Long) siteMap.get("id");
						
						siteWiseMap.put(siteId, kdm);
					}
					
					LOGGER.log(Priority.ERROR, "siteWiseMap111 -- "+siteWiseMap.keySet());
					LOGGER.log(Priority.ERROR, "siteWiseMap222 -- "+siteWiseMap.values().size());
					List<String> options = new ArrayList<>();
					
					options.add("Extended");
					options.add("Expired");
					
					Map<SiteContext, List<TaskContext>> tasksList= new HashMap<>();
					for(Long siteID :siteWiseMap.keySet()) {
						
						SiteContext site = SpaceAPI.getSiteSpace(siteID);
						
						int sequence = 1;
						List<TaskContext> tasks = new ArrayList<>();
						LOGGER.log(Priority.ERROR, "sitee -- "+siteID);
						LOGGER.log(Priority.ERROR, "kdmsss -- "+siteWiseMap.get(siteID).size());
						for(ModuleBaseWithCustomFields kdm :siteWiseMap.get(siteID)) {
							
							TaskContext task = new TaskContext();
							
							task.setSubject((String) kdm.getDatum("moviename"));
							
							task.setOptions(options);
							task.setInputType(5);
							task.setAttachmentRequired(false);
							
							
							Map<String, Object> assetMap= (Map<String, Object>) kdm.getDatum("servernumber");
							
							LOGGER.log(Priority.ERROR, "test222 -- "+assetMap);
							LOGGER.log(Priority.ERROR, "test1111 -- "+assetMap.get("id"));
							AssetContext asset = AssetsAPI.getAssetInfo((Long)assetMap.get("id"));
							
							task.setResource(asset.getSpace());
							
							task.setSequence(sequence);
							sequence++;
							
							tasks.add(task);
						}
						
						tasksList.put(site, tasks);
					}
					
					for(SiteContext site :tasksList.keySet()) {
						WorkOrderContext workOrder = new WorkOrderContext();
						
						workOrder.setSubject("KDM Expiry List - "+site.getName());
						workOrder.setDescription("KDM's Which are about to expire");
						
						TicketCategoryContext category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), "KDM");
						
						workOrder.setCategory(category);
						
						TicketPriorityContext priority = TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "High");
						workOrder.setPriority(priority);
						
						workOrder.setDueDate(DateTimeUtil.getDayStartTime(1)-1000);
						
						if(AccountUtil.getCurrentOrg().getId() == 92l) {
							User user = AccountUtil.getUserBean().getUser(848657l);		//suresh+spicinemas@facilio.com user id
							workOrder.setAssignedTo(user);
						}
						
						workOrder.setSiteId(site.getId());
						
						WorkOrderAction woAction = new WorkOrderAction();
						
						woAction.setWorkorder(workOrder);
						
						List<TaskContext> tasks = tasksList.get(site);
						
						Map<String, List<TaskContext>> tasksList1= new HashMap<>();
						tasksList1.put("default", tasks);
						woAction.setTasks(tasksList1);
						
						woAction.addWorkOrder(workOrder);
					}
					System.out.println("Completed");
				}
			}
		}
		return false;
	}

}
