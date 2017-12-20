package com.facilio.beans;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	@Override
	public long addWorkOrder(WorkOrderContext workorder) throws Exception {
		// TODO Auto-generated method stub
		if (workorder != null) {
			FacilioContext context = new FacilioContext();
			// context.put(FacilioConstants.ContextNames.TICKET,
			// workorder.getTicket());
			context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);

			Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(context);
			return workorder.getId();
		}
		return -1;
	}

	@Override
	public long addAlarm(AlarmContext alarm) throws Exception {
		// TODO Auto-generated method stub
		if (alarm != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);

			Chain addAlarmChain = FacilioChainFactory.getAddAlarmChain();
			addAlarmChain.execute(context);
			return alarm.getId();
		}
		return -1;
	}

	@Override
	public int deleteAlarm(List<Long> id) throws Exception {
		// TODO Auto-generated method stub
		if (id != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

			Chain deleteAlarmChain = FacilioChainFactory.getDeleteAlarmChain();
			deleteAlarmChain.execute(context);

			int rowsDeleted = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			return rowsDeleted;
		}
		return 0;
	}

	@Override
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception {
		// TODO Auto-generated method stub
		if (workOrderRequest != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrderRequest.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);

			Command addWorkOrderRequest = FacilioChainFactory.getAddWorkOrderRequestChain();
			addWorkOrderRequest.execute(context);
			return workOrderRequest.getId();
		}
		return -1;
	}

	@Override
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		if (alarm != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
			updateAlarm.execute(context);

			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return -1;
	}

	@Override
	public int updateAlarmPriority(String priority, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		if (priority != null && !priority.isEmpty()) {
			AlarmContext alarm = new AlarmContext();
			alarm.setPriority(TicketAPI.getPriority(AccountUtil.getCurrentOrg().getOrgId(), priority));

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
			updateAlarm.execute(context);
			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return -1;
	}

	@Override
	public int updateAlarmAsset(long assetId, String node) throws Exception {
		// TODO Auto-generated method stub
		if (node != null && !node.isEmpty() && assetId != -1) {
			AlarmContext alarm = new AlarmContext();
			AssetContext asset = new AssetContext();
			asset.setId(assetId);
			alarm.setAsset(asset);

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.NODE, node);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmAssetChain();
			updateAlarm.execute(context);
			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		}
		return -1;
	}

	@Override
	public long addWorkOrderFromPM(long pmId) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getPreventiveMaintenanceFields())
				.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName()).andCustomWhere("ID = ?", pmId);

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			long templateId = (long) prop.get("templateId");
			JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(),
					templateId);

			JSONObject content = template.getTemplate(null);
			WorkOrderContext wo = FieldUtil.getAsBeanFromJson(
					(JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);

			JSONArray taskJson = (JSONArray) content.get(FacilioConstants.ContextNames.TASK_LIST);
			if (taskJson != null) {
				List<TaskContext> tasks = FieldUtil.getAsBeanListFromJsonArray(taskJson, TaskContext.class);
				context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
			}

			Chain addWOChain = FacilioChainFactory.getAddWorkOrderChain();
			addWOChain.execute(context);

			addToRelTable(pmId, wo.getId());
			return wo.getId();
		}
		return -1;
	}
	
	private void addToRelTable(long pmId, long woId) throws SQLException, RuntimeException {
		Map<String, Object> relProp = new HashMap<>();
		relProp.put("pmId", pmId);
		relProp.put("woId", woId);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getPmToWoRelFields())
														.table(ModuleFactory.getPmToWoRelModule().getTableName())
														.addRecord(relProp);
		
		insertBuilder.save();
	}

}
