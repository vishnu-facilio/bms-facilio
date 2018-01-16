package com.facilio.events.commands;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.events.constants.EventConstants;

public class AddEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		JSONObject payload = (JSONObject) context.get(EventConstants.EventContextNames.EVENT_PAYLOAD);
		if(payload != null) {
			try {
				AmazonKinesis kinesis = AwsUtil.getKinesisClient();
				String orgDomainName = AccountUtil.getCurrentAccount().getOrg().getDomain();
				PutRecordRequest recordRequest = new PutRecordRequest().withStreamName(orgDomainName).withData(ByteBuffer.wrap(payload.toJSONString().getBytes(Charset.defaultCharset()))).withPartitionKey(orgDomainName);
				PutRecordResult result = kinesis.putRecord(recordRequest);
				System.out.println(result.getSequenceNumber() + " is the sequence number for the record " + payload.toString());
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage() + " Exception while adding record to kinesis: " + payload.toString());
			}
		}
		return false;
	}
}
