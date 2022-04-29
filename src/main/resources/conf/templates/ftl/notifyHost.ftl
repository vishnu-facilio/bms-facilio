<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,500,700&display=swap" rel="stylesheet">
</head>

<body style="background: #ffffff;">
	<#setting time_zone=org.timezone>
      <div style="max-width:600px;margin: 0 auto;display: block;padding-top: 50px;">
            <div style="margin:20px auto 0;padding-left: 0;padding-right: 0;background-color:#FFFFFF;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px #ebecf0;border-radius: 10px;overflow: hidden;width: 100%;">
                 
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;background: #f8f9fc;padding: 30px 55px;"
                        border="0">
                        <tbody>
                              <tr>
                                <td colspan="100%">
                                      <div style="font-family:'Roboto', Helvetica,Helvetica Neue,Arial, sans-serif,Times New Roman, Times, serif;font-size: 30px;font-weight: 700;text-align: left;">
                                          <strong style="color:#45474d;font-family:'Cera Pro','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:35px;line-height:40px;text-align:center;Margin:0;margin:0;border-bottom:3px solid #ff3989;letter-spacing: 0.6px;">${org.name}</strong>
                                      </div>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:40px;line-height:50px;text-align:left;font-weight:bold;padding-top: 20px;letter-spacing: 0.6px;">
                                                Happy Meeting! 
                                      </div>
                                      <#if (vLog.visitedSpace) ??>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          ${vLog.visitor.name}  has successfully checked in at ${vLog.visitedSpace.name} on ${(vLog.checkInTime)?number_to_datetime?string(formatDates(org))} and is on the way to meet you!
                                      </div>
                                      <#else>
                                       <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          ${vLog.visitor.name}  has successfully checked in on ${(vLog.checkInTime)?number_to_datetime?string(formatDates(org))} and is on the way to meet you!
                                     </div>
                                     </#if>
                                    </div>
                                </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px 40px;">
                        <tr>
                              <td colspan="100%">
                                    <div style="font-family: 'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important; border-bottom: 1px solid #eaeaec; padding-bottom: 15px; padding-left: 10px; padding-right: 10px; color: #ff3989; font-weight: 600; letter-spacing: 0.9px;">Visitor Details</div>
                              </td>
                        </tr>
                       <tr>
                             <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; color: #7f8498; padding-left: 10px;padding-right: 10px;font-size: 14px;letter-spacing: 0.9px;">Name</td>
                             <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; padding-left: 10px;padding-right: 10px;color: #000;font-weight: 600;letter-spacing: 0.9px;">${vLog.visitor.name}</td>
                       </tr>
                       <tr>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; color: #7f8498; padding-left: 10px;padding-right: 10px;font-size: 14px;letter-spacing: 0.9px;">Mobile</td>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; padding-left: 10px;padding-right: 10px;color: #000;font-weight: 600;letter-spacing: 0.9px;">${vLog.visitor.phone}</td>
                  </tr>
                  <tr>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; color: #7f8498; padding-left: 10px;padding-right: 10px;font-size: 14px;letter-spacing: 0.9px;">Email id</td>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; padding-left: 10px;padding-right: 10px;color: #000;font-weight: 600;letter-spacing: 0.9px;">${vLog.visitor.email}</td>
                  </tr>
                  <tr>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; color: #7f8498; padding-left: 10px;padding-right: 10px;font-size: 14px;letter-spacing: 0.9px;">Date / Time</td>
                        <td style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-bottom: 1px solid #eaeaec; padding-bottom: 15px;padding-top: 15px; padding-left: 10px;padding-right: 10px;color: #000;font-weight: 600;letter-spacing: 0.9px;">${(vLog.checkInTime)?number_to_datetime?string(formatDates(org))}</td>
                  </tr>
                  
                  </table>
                  
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/images/facilio-blue-logo.svg" alt="" title="" style="width: 70px;height: 16px;padding-bottom: 15px;max-width: 100%;">
            </div>
      </div>
    <#function formatDates org>
        <#if org.timeFormat == 2>
            <#local timeStr= "hh:mm aa">
        <#elseif org.timeFormat == 1>
            <#local timeStr= "HH:mm">
        <#else>
            <#local timeStr= "HH:mm">
        </#if>
        <#if org.dateFormat == "DD/MM/YYYY">
            <#return "dd/MM/YYYY "+timeStr>
        <#elseif org.dateFormat == "MM/DD/YYYY">
            <#return "MM/dd/YYYY "+timeStr>
        <#elseif org.dateFormat == "YYYY/MM/DD">
            <#return "YYYY/MM/dd "+timeStr>
        <#else>
            <#return "dd/MM/YYYY "+timeStr>
        </#if>
    </#function>
</body>
</html>