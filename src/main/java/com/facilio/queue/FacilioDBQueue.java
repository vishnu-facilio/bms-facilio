package com.facilio.queue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

/**
 * @author facilio
 *
 */

public class FacilioDBQueue implements FacilioQueue {

	private static final Logger LOGGER = LogManager.getLogger(FacilioDBQueue.class.getName());

	private static final long DEFAULT_VISIBILITY_TIME = 300000;
	private static final long DEFAULT_CLIENT_RECEIPT_COUNT = 0;
	private static final long DEFAULT_LAST_CLIENT_RECEIVED_TIME = 0;
	private static final long DEFAULT_MAX_CLIENT_RECEIPT_COUNT = 5;

	private static final String VISIBILITY_TIMEOUT = "visibilityTimeout";
	private static final String DELETED_TIME = "deletedTime";
	private static final String CLIENT_RECEIPT_COUNT = "clientReceiptCount";
	private static final String LAST_CLIENT_RECEIVED_TIME = "lastClientReceivedTime";
	private static final String ADDED_TIME = "addedTime";
	private static final String DATA = "data";
	private static final String ID = "id";
	private static final String QUEUE_NAME = "queueName";
	private static final String MAX_CLIENT_RECEIPT_COUNT = "maxClientReceiptCount";

	private static final FacilioModule MODULE = ModuleFactory.getFacilioQueueModule();
	private static final List<FacilioField> FIELDS = FieldFactory.getFacilioQueueFields();
	private static final Map<String, FacilioField> FIELD_MAP = FieldFactory.getAsMap(FIELDS);

	private static final FacilioField QUEUE_NAME_FIELD = FIELD_MAP.get(QUEUE_NAME);
	private static final FacilioField ID_FIELD = FIELD_MAP.get(ID);
	private static final FacilioField CLIENT_RECEIPT_COUNT_FIELD = FIELD_MAP.get(CLIENT_RECEIPT_COUNT);
	private static final FacilioField DELETED_TIME_FIELD = FIELD_MAP.get(DELETED_TIME);
	private static final FacilioField LAST_CLIENT_RECEIVED_TIME_FIELD = FIELD_MAP.get(LAST_CLIENT_RECEIVED_TIME);

	private static final String GET_QUEUE_MESSAGE_CONDITION = "LAST_CLIENT_RECEIVED_TIME + VISIBILITY_TIMEOUT";// (<System.currentTimeMillis())
	private static final String MAX_COUNT_CONDITION = "MAX_CLIENT_RECEIPT_COUNT";

	@Override
	public boolean push(String queueName, String message) {

		if (StringUtils.isBlank(queueName) || StringUtils.isBlank(message)) {
			throw new IllegalArgumentException(
					"QueueName and Message are mandatory fields ,hence both fields shouldn't be null or empty string");
		}

		Map<String, Object> insertValue = new HashMap<>();
		insertValue.put(QUEUE_NAME, queueName);
		insertValue.put(DATA, message);
		insertValue.put(ADDED_TIME, System.currentTimeMillis());
		insertValue.put(VISIBILITY_TIMEOUT, DEFAULT_VISIBILITY_TIME);
		insertValue.put(LAST_CLIENT_RECEIVED_TIME, DEFAULT_LAST_CLIENT_RECEIVED_TIME);
		insertValue.put(CLIENT_RECEIPT_COUNT, DEFAULT_CLIENT_RECEIPT_COUNT);
		insertValue.put(MAX_CLIENT_RECEIPT_COUNT, DEFAULT_MAX_CLIENT_RECEIPT_COUNT);

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().fields(FIELDS)
				.table(MODULE.getTableName()).addRecord(insertValue);
		try {
			insertBuilder.save();
			return true;
		} catch (Exception e) {
			LOGGER.info("Exception occured While insert Queue Message in  FacilioQueue" + e);
		}

		return false;
	}

	@Override
	public QueueMessage pull(String queueName) {

		if (StringUtils.isBlank(queueName)) {
			throw new IllegalArgumentException(
					"QueueName is mandatory field ,hence it shouldn't be null or empty string");
		}
		List<QueueMessage> queueData = pull(queueName, 1);

		if (CollectionUtils.isNotEmpty(queueData)) {
			return queueData.get(0);
		}
		return null;
	}

	@Override
	public void delete(String queueName, String messageId) {

		if (StringUtils.isBlank(queueName) || StringUtils.isBlank(messageId)) {
			throw new IllegalArgumentException(
					"QueueName and MessageId is mandatory fields ,hence both fields shouldn't be null or empty string");
		}

		Map<String, Object> updateValue = new HashMap<>();
		updateValue.put(DELETED_TIME, System.currentTimeMillis());

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().fields(FIELDS)
				.table(MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(QUEUE_NAME_FIELD, queueName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(DELETED_TIME_FIELD, CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(ID_FIELD, messageId, StringOperators.IS));
		try {

			updateBuilder.update(updateValue);

		} catch (Exception e) {
			LOGGER.info("Exception occured to Delete QueueMessage in FacilioQueue" + e);
		}
	}

	@Override
	public List<QueueMessage> pull(String queueName, int limit) {

		if (StringUtils.isBlank(queueName) || limit <= 0) {
			throw new IllegalArgumentException("QueueName and limit is mandatory fields");
		}
		if (limit > 20) {
			limit = 20;
		}

		List<QueueMessage> queueMessageList = new ArrayList<>();
		List<Map<String, Object>> queueMessages = new ArrayList<>();

		queueMessages = getQueueMessage(queueName, limit);
		while (queueMessageList.size() < limit && queueMessages.size() > 0) {
			queueMessageList.addAll(updateQueue(queueName, limit, queueMessages, queueMessageList.size()));
			if (queueMessageList.size() != limit) {
				queueMessages = getQueueMessage(queueName, limit);
			} else {
				break;
			}
		}
		return queueMessageList;

	}

	@Override
	public boolean changeVisibilityTimeout(String queueName, String receiptHandle, int visibilityTimeout) {

		if (StringUtils.isBlank(queueName) || StringUtils.isBlank(receiptHandle) || visibilityTimeout > 5) {// check
																											// time with
																											// manthosh
																											// for
																											// instance
																											// jobs
			throw new IllegalArgumentException(
					"QueueName ,MessageID and visibilityTimeout are mandatory fields ,hence all fields shouldn't be null or empty string and visibilityTimeout must greater than 5min");
		}

		long millisec = TimeUnit.MINUTES.toMillis(visibilityTimeout);
		Map<String, Object> updateValue = new HashMap<>();
		updateValue.put(VISIBILITY_TIMEOUT, millisec);

		GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder().fields(FIELDS)
				.table(MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(QUEUE_NAME_FIELD, queueName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(ID_FIELD, receiptHandle, StringOperators.IS));

		try {
			int count = updatebuilder.update(updateValue);
			if (count == 1) {
				LOGGER.info("New Visibility TimeOut is  " + visibilityTimeout + "  Updated, where Queuename is  "
						+ queueName);
				return true;
			}

		} catch (SQLException e) {
			LOGGER.info("Exception occured in Update Message Queue in FacilioQueue" + e);
		}

		return false;
	}

	private static List<Map<String, Object>> getQueueMessage(String queueName, int limit) {

		String receivedTime = GET_QUEUE_MESSAGE_CONDITION;
		FacilioField whereField = FieldFactory.getField(receivedTime, receivedTime, FieldType.NUMBER);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(FIELDS)
				.table(MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(QUEUE_NAME_FIELD, queueName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(DELETED_TIME_FIELD, CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(CLIENT_RECEIPT_COUNT_FIELD, MAX_COUNT_CONDITION,
						NumberOperators.LESS_THAN))
				.andCondition(CriteriaAPI.getCondition(whereField, String.valueOf(System.currentTimeMillis()),
						DateOperators.IS_BEFORE))
				.limit(limit * 2).orderBy(ADDED_TIME);

		List<Map<String, Object>> queueMessages = new ArrayList<>();
		try {
			queueMessages = builder.get();
		} catch (Exception e1) {
			LOGGER.info("Exception occured in get Message Queue in FacilioQueue" + e1);
		}
		return queueMessages;
	}

	private static List<QueueMessage> updateQueue(String queueName, int limit, List<Map<String, Object>> queueMessages,
			int queueMessageListSize) {

		List<QueueMessage> messageUpdatedList = new ArrayList<>();

		if (queueMessageListSize >= limit) {
			return messageUpdatedList;
		}
		for (Map<String, Object> msg : queueMessages) {

			Long idVal = (long) msg.get(ID);
			String message = (String) msg.get(DATA);
			long lastUpdatedtime = (long) msg.get(LAST_CLIENT_RECEIVED_TIME);
			int clientReceiptCount = (int) msg.get(CLIENT_RECEIPT_COUNT);

			msg.put(LAST_CLIENT_RECEIVED_TIME, System.currentTimeMillis());
			msg.put(CLIENT_RECEIPT_COUNT, clientReceiptCount + 1);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().fields(FIELDS)
					.table(MODULE.getTableName())
					.andCondition(CriteriaAPI.getCondition(QUEUE_NAME_FIELD, queueName, StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition(CLIENT_RECEIPT_COUNT_FIELD,
							String.valueOf(clientReceiptCount), NumberOperators.EQUALS))// check with manthosh
					.andCondition(CriteriaAPI.getCondition(LAST_CLIENT_RECEIVED_TIME_FIELD,
							String.valueOf(lastUpdatedtime), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getIdCondition(idVal, MODULE));

			try {
				int count = updateBuilder.update(msg);
				if (count == 1) {
					QueueMessage msgQueue = new QueueMessage(idVal.toString(), message);

					messageUpdatedList.add(msgQueue);
					queueMessageListSize++;
					if (queueMessageListSize == limit) {
						break;
					}
				}

			} catch (SQLException e) {
				LOGGER.info("Exception occured in Update Message Queue in FacilioQueue" + e);
			}

		}

		return messageUpdatedList;
	}
}