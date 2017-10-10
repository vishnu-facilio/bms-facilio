package com.facilio.tasker.job;

import org.threeten.bp.ZonedDateTime;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.base.Optional;

public class CronUtil {
	
	
	/**
	 * Reference link for cron expression : http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html
	 * A modified to definition of quartz cron expression is used to specify both day of week and day of month. Both conditions are ORed
	 * 
	 * Library used : https://github.com/jmrozanec/cron-utils
	 * 
	 * Sample cron  builder code
	 * 
	 * The following runs on 7th and 8th of every month from 15:00 to 15:59 for every 2 minutes
	 * 
	 * List<FieldExpression> expressions = new ArrayList<>();
			expressions.add(FieldExpressionFactory.on(7));
			expressions.add(FieldExpressionFactory.on(8));
			Cron cron = CronBuilder.cron(CronUtil.DEFAULT_CRON_DEFN)
					.withYear(FieldExpressionFactory.always())
					.withMinute(FieldExpressionFactory.every(2))
					.withHour(FieldExpressionFactory.on(15))
					.withDoM(FieldExpressionFactory.and(expressions))
					.withMonth(FieldExpressionFactory.always())
					.withDoW(FieldExpressionFactory.always())
					.instance();
	 */
	
	public static final CronDefinition DEFAULT_CRON_DEFN = CronDefinitionBuilder.defineCron()
								            .withMinutes().and()
								            .withHours().and()
								            .withDayOfMonth()
								            .supportsHash().supportsL().supportsW().supportsLW().and()
								            .withMonth().and()
								            .withDayOfWeek()
								            .withIntMapping(7, 0) 
								            .supportsHash().supportsL().and()
								            .withYear().and()
								            .instance();
	
	public static Cron parse(String cronExpression) {
		return new CronParser(DEFAULT_CRON_DEFN).parse(cronExpression);
	}
	
	public static long nextExecutionTime(Cron cron, long orgId) {
		Optional<ZonedDateTime> zoneDateTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now());
		if(zoneDateTime.isPresent()) {
			return zoneDateTime.get().toEpochSecond();
		}
		return -1;
	}
}
