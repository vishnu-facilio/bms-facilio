package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class EditRDMWritableableCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(EditRDMWritableableCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

			List<Map<String, Object>> mappedPoints = getOnlyMappedPoints(context);
			List<ReadingDataMeta> writableReadingList = new ArrayList<>();
			Boolean isWritable = (Boolean) context.get(AgentConstants.WRITABLE);
			if(isWritable != null) {
				if (CollectionUtils.isNotEmpty(mappedPoints)) {
					mappedPoints.forEach((Map<String, Object> map) -> {
						ReadingDataMeta meta = new ReadingDataMeta();
						meta.setResourceId((long) map.get(AgentConstants.RESOURCE_ID));
						meta.setFieldId((long) map.get(AgentConstants.FIELD_ID));
						writableReadingList.add(meta);
					});
					int updatedRows = ReadingsAPI.updateReadingDataMetaInputType(writableReadingList, ReadingInputType.CONTROLLER_MAPPED,ReadingType.WRITE);
					LOGGER.info("RDM's Is_Controllable type updated rows count is  : "+updatedRows);
				} else {
					throw new IllegalArgumentException("Points are unmapped/empty. pointIds size is ::: "+ mappedPoints.size()+"  hence can't edit RDM controllable... ");
				}
			}
			
		return false;
	}

	private List<Map<String, Object>> getOnlyMappedPoints(Context context) throws Exception {
		Long controllerId = (Long) context.get(AgentConstants.CONTROLLER_ID);
		Criteria pointIds = (Criteria) context.get(AgentConstants.POINT_IDS);
		FacilioModule module = ModuleFactory.getPointModule();
		List<FacilioField> fields = FieldFactory.getPointFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		builder.select(fields).table(module.getTableName())
		.andCondition(CriteriaAPI.getCondition(FieldFactory.getPointResourceIdField(module),CommonOperators.IS_NOT_EMPTY))
		.andCondition(CriteriaAPI.getCondition(FieldFactory.getPointFieldIdField(module),CommonOperators.IS_NOT_EMPTY));
		if(pointIds != null && !pointIds.isEmpty()) {
			builder.andCriteria(pointIds);
		}
		if(controllerId != null && controllerId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(module), String.valueOf(controllerId),NumberOperators.EQUALS));
		}
		return builder.get();
	}
}
