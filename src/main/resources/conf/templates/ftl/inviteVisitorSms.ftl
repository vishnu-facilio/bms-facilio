<#setting time_zone=org.timezone>
Hi ${vLog.visitor.name}, 

You have been invited <#if (vLog.host) ?? >by ${vLog.host.name} (${org.name}) </#if><#if (vLog.expectedCheckInTime &gt; 0)>for ${(vLog.expectedCheckInTime)?number_to_datetime?string("YYYY-MM-dd HH:mm:ss")} </#if>. Your invite passcode is ${vLog.passCode}. Meet you there !

Regards,
${org.name} - Admin Team.