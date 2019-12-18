package com.facilio.aws.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class DescribeInstances {
	private static final Logger LOGGER = LogManager.getLogger(DescribeInstances.class.getName());
	public static List<Instance> getAwsvalue()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_WEST_2).withCredentials(AwsUtil.getAWSCredentialsProvider()).build();
        boolean done = false;
        List<Instance> awsEC2Instances = new ArrayList<>();
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            LOGGER.info("EC2 response : "+response.getReservations());
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
//                    System.out.printf(
//                        "Found instance with id %s, " +
//                        "AMI %s, " +
//                        "type %s, " +
//                        "state %s " +
//                        "and monitoring state %s",
//                        instance.getInstanceId(),
//                        instance.getImageId(),
//                        instance.getInstanceType(),
//                        instance.getState().getName(),
//                        instance.getMonitoring().getState());
                	awsEC2Instances.add(instance);
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
        LOGGER.info("EC2 instance : "+awsEC2Instances);
        return awsEC2Instances;
    }
}
