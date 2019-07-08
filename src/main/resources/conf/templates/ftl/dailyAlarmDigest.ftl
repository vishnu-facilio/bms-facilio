<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body style="background: #fafafa;">
      <#setting time_zone=org.timezone>
      <div style="max-width:700px;margin: 0 auto;padding-top: 50px;padding-bottom: 50px;">
            <div style="margin:20px auto 0;max-width:700px;padding-left: 0;padding-right: 0;background-color:#FFFFFF;padding-bottom: 50px;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px rgba(0, 0, 0, 0.08);">
                  <table role="presentation" cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;" align="center"
                        border="0">
                        <tbody>
                              <tr>
                                    <td style="text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:0 0px 17px 0px;">
                                          <div class="mj-column-per-100 outlook-group-fix" style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;">
                                                <table role="presentation" cellpadding="0" cellspacing="0" width="100%"
                                                      border="0">
                                                      <tbody>
                                                            <tr>
                                                                  <td style="word-wrap:break-word;font-size:0px;padding: 30px 45px;"
                                                                        align="left">
                                                                        <table role="presentation" cellpadding="0"
                                                                              cellspacing="0" style="border-collapse:separate;"
                                                                              align="left" border="0">
                                                                              <tbody>
                                                                                    <tr>
                                                                                          <td>
                                                                                                <div style="font-size: 15px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #19191c;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">Hi,</div>
                                                                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #78787f;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 15px;">
                                                                                                      Here’s your summarized report till ${.now?date?string["MMM dd, HH:mm"]}
                                                                                                </div>
                                                                                          </td>
                                                                                    </tr>
                                                                              </tbody>
                                                                        </table>
                                                                  </td>
                                                            </tr>
                                                            <tr>
                                                                  <td style="word-wrap:break-word;font-size:0px;padding:0px 30px 0px 30px;"
                                                                        align="left">
                                                                        <div style="border-radius: 3px; box-shadow: 0 1px 13px 0 rgba(0, 0, 0, 0.07);border: solid 1px #372668;background-color: #372668;padding-top: 30px; padding-top: 25px; padding-bottom: 90px; padding-left: 20px; padding-right: 20px;max-width: 600px;">
                                                                              <table role="presentation" cellpadding="0"
                                                                                    cellspacing="0" style="border-collapse:separate;"
                                                                                    align="left" border="0">
                                                                                    <tbody>
                                                                                          <tr>
                                                                                                <td style="padding-left: 10px;padding-right: 20px; border-right: 1px solid rgba(255,255,255,0.1);width: 25%;">
                                                                                                 <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${all}</div>
                                                                                                 <div style="padding-top: 5px;font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform:uppercase;">ACTIVE</div>
                                                                                                </td>
                                                                                                <td style="padding-left: 30px;padding-right: 30px; border-right: 1px solid rgba(255,255,255,0.1);width: 25%;">
                                                                                                <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #4fc378;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${active}</div>
                                                                                                <div style="padding-top: 5px;font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform:uppercase;">YESTERDAY</div>
                                                                                                </td>
                                                                                                <td style="padding-left: 20px;padding-right: 20px; border-right: 1px solid rgba(255,255,255,0.1);width: 25%;">
                                                                                                <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #e9b81f;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${unack}</div>
                                                                                                <div style="padding-top: 5px;font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform:uppercase;">Unacknowledged</div>
                                                                                                </td>
                                                                                                <td style="padding-left: 30px;padding-right: 30px;width: 25%;">
                                                                                                <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #f66969;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${critical}</div>
                                                                                                <div style="padding-top: 5px;font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform:uppercase;">Critical</div>
                                                                                                </td>
                                                                                          </tr>
                                                                                    </tbody>
                                                                              </table>
                                                                        </div>
                                                                  </td>
                                                            </tr>
                                                      </tbody>
                                                </table>
                                          </div>
                                    </td>
                              </tr>
                        </tbody>
                  </table>
                  <#if maxResource??>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-left: 50px;margin-right: 50px;margin-top: 25px;"
                        border="0">
                        <tbody>
                              <tr>
                                          <td style="width: 150px;max-width: 150px;">
                                                <div style="padding-top: 30px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif; max-width: 87%;">
                                                      Just to be safe, <b>(${maxResource})</b> has generated the maximum number of alarms - a total of <b>(${maxResourceCount} alarms)</b> yesterday
                                                </div>
                                          </td>
                                    </tr>
                        </tbody>
                  </table>
                  </#if>
                  <#if alarms??>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-left: 50px;margin-right: 50px;margin-bottom: 30px;" border="0">
                  <tbody>
                        <tr>
                        <td>
                        <div style="width: 599px;max-width: 599px;margin-top: 40px;height: 1px;background: rgba(0,0,0,0.06);"></div>
                        </td>
                        </tr>
                  </tbody>
            </table>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;"
                        border="0">
                        <tbody>
                              <tr>
                               <td style="padding-left: 50px;padding-right: 50px;">
                                    <div style="float: left;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;">
                                          Unacknowledged Alarms
                                    </div>
                                    <div style="float: right;">
                                          <a href="https://app.facilio.com/app/fa/alarms/unacknowledged" style="text-decoration: none;font-size: 12px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.8px; text-align: right; color: #ff3184;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">VIEW ALL</a>
                                    </div>
                               </td>
                               <td>                             
                               </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;padding-left: 30px;padding-right:30px;margin-top:30px;"border="0">
                        <tbody>
                                    <#list alarms as alarm>
                                    <#if alarm?is_odd_item>
                                          <#assign color='#f5f5f5'/>
                                    <#else>
                                          <#assign color='#ffffff'/>
                                    </#if>
                                    <tr style="background-color: ${color};">
                                          <td style=' padding:0 0 18px 10px;width: 10px;max-width:10px;width: 3%;'>
                                                      <div style="width: 10px; height: 10px; background-color: #f66969;border-radius: 100%;display: inline-block;margin-right: 5px;margin-top: 14px;"></div>
                                          </td>
                                                <td style='padding:18px 10px;width: 35%;'>
                                                    <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.4; letter-spacing: 0.4px; color: #171719; font-family: Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                          ${alarm.subject}
                                                    </div>
                                                </td>
                                                <td style='padding:18px 10px;width: 25%;'>
                                                          <div style="padding-top: 30px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                  ${(alarm.resourceName?keep_before(","))!}
                                                          </div>
                                                          <div style="padding-top: 3px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #7b7981;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            ${(alarm.resourceName?keep_after_last(","))!}
                                                                </div>
                                                    </td>
                                                    <td style="text-align: left;width: 15%">
                                                      <div style="padding-top: 30px;text-align: left;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            ${alarm.modifiedTime?number_to_date?string["MMM dd, HH:mm"]}
                                                      </div>
                                                    </td>
                                                    <td style='padding:18px 0;width: 20%;text-align: right;'>
                                                      <div style="text-align: center;">
                                                     <a href="https://app.facilio.com/app/fa/alarms/summary/${alarm.id?c}" style="text-decoration:none;border-radius: 2px;background:#39b2c2;padding:10px 13px;font-size: 10px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: center; color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">View</a>
                                                    </div>
                                                    </td>
                                              </tr>
                                              </#list>
                        </tbody>
                  </table>
                  </#if>
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
            </div>
      </div>
</body>

</html>