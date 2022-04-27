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
      <div style="max-width:650px;margin: 0 auto;display: block;">
            <div style="margin:20px auto 0;padding-left: 0;padding-right: 0;background-color:#FFFFFF;border-radius: 10px;overflow: hidden;width: 100%;">
                 
                 
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px 40px;">
                        <tr>
                              <td colspan="100%" style="border-bottom: 1px solid #eee;">
                                    <img src="https://facilio.com/images/facilio-blue-logo.svg" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
                              </td>
                        </tr>
                        <tr>
                              <td colspan="100%" align="center">
                                  <img src="https://facilio.com/images/email-images/visitor-thankyou.png" alt="" title=""  width="250" height="250"/>
                              </td>
                      </tr>
                        <tr>
                        
                              <td style="padding-top: 30px;">
                              <div style="font-family:'Roboto', Helvetica,Helvetica Neue,Arial, sans-serif,Times New Roman, Times, serif;font-size: 30px;font-weight: 700;text-align: left;">
                                          <strong style="color:#45474d;font-family:'Cera Pro','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:35px;line-height:40px;text-align:center;Margin:0;margin:0;border-bottom:3px solid #ff3989;letter-spacing: 0.6px;">${org.name}</strong>
                                      </div>
                                      
                                          <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:40px;line-height:50px;text-align:left;font-weight:bold;padding-top: 20px;letter-spacing: 0.6px;">
                                                      Thanks for visiting us! 
                                                  </div>
                                    <#if (vLog.visitedSpace) ??>
                                    <div style="color: #000;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 16px;padding-bottom: 30px;letter-spacing: 0.5px;">
                                                You have successfully checked out of ${vLog.visitedSpace.name} on ${(vLog.checkOutTime)?number_to_datetime?string(formatDates(org))}.
                                    </div>
                                    <#else>
                                    <div style="color: #000;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 16px;padding-bottom: 30px;letter-spacing: 0.5px;">
                                                You have successfully checked out on ${(vLog.checkOutTime)?number_to_datetime?string(formatDates(org))}.
                                    </div>
                                    </#if>
                              </td>
                        </tr>
                        <tr>
                                <td colspan="100%" align="center">
                                    <img src="confetti.png" alt="" title=""  width="200" height="200"/>
                                </td>
                        </tr>
                  </table>
                  
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