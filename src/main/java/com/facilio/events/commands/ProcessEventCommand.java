package com.facilio.events.commands;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;

public class AddEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		if(payload != null) {
			try {
//				AmazonKinesis kinesis = AwsUtil.getKinesisClient();
//				String orgDomainName = AccountUtil.getCurrentAccount().getOrg().getDomain();
//				PutRecordRequest recordRequest = new PutRecordRequest().withStreamName(orgDomainName).withData(ByteBuffer.wrap(payload.toJSONString().getBytes(Charset.defaultCharset()))).withPartitionKey(orgDomainName);
//				PutRecordResult result = kinesis.putRecord(recordRequest);
//				System.out.println(result.getSequenceNumber() + " is the sequence number for the record " + payload.toString());
				List<EventRule> ruleList = EventRulesAPI.getEventRules(AccountUtil.getCurrentOrg().getId());
				EventAPI.processEvents(System.currentTimeMillis(), payload, ruleList, new HashMap<>(), -1);
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage() + " Exception while adding event: " + payload.toString());
				e.printStackTrace();
			}
		}
		return false;
	}
}
