<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body style="background: #fafafa;">
      <#setting time_zone=org.timezone>
      <#setting number_format="0.##">
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
                                                            </tr>
                                                            <tr>
                                                                  <td style="word-wrap:break-word;font-size:0px;padding:30px 45px;"
                                                                        align="left">
                                                                        <table role="presentation" cellpadding="0"
                                                                              cellspacing="0" style="border-collapse:separate;"
                                                                              align="left" border="0">
                                                                              <tbody>
                                                                                    <tr>
                                                                                          <td>
                                                                                                <div style="font-size: 15px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #19191c;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">Hi,</div>
                                                                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #78787f;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 15px;">
                                                                                                        The alarm has been auto cleared ( ${alarm.clearedTime?number_to_date?string["MMM dd, HH:mm"]} ) since the source status doesn't match the alarm condition anymore.
                                                                                                </div>
                                                                                          </td>
                                                                                    </tr>
                                                                              </tbody>
                                                                        </table>
                                                                  </td>
                                                            </tr>
                                                            <tr>
                                                                  <td style="word-wrap:break-word;font-size:0px;padding: 0 40px;"
                                                                        align="left">
                                                                        <div>
                                                                              <table role="presentation" cellpadding="0"
                                                                                    cellspacing="0" style="border-collapse:separate;border-radius: 3px; box-shadow: 0 1px 13px 0 rgba(0, 0, 0, 0.07); border: solid 1px rgba(0, 0, 0, 0.06); background-color: #ffffff; padding-top: 20px; padding-bottom: 20px; padding-left: 20px; padding-right: 20px;"
                                                                                    align="left" border="0">
                                                                                    <tbody>
                                                                                          <tr>
                                                                                                <td style="width: 3%;">
                                                                                                <div style="width: 10px; height: 10px; background-color: #f66969;border-radius: 100%;display: inline-block;margin-right: 10px;"></div>
                                                                                                </td>
                                                                                                <td style="width: 50%;max-width: 50%;padding: 0;">
                                                                                                      <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.4; letter-spacing: 0.4px; color: #171719; font-family: Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                                                            ${alarm.subject}
                                                                                                      </div>
                                                                                                </td>
                                                                                                <td style="padding: 0;width: 30%;">
                                                                                                      <div style="font-size: 11px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: right; color: #8c8c92;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                                                            Time
                                                                                                            Created
                                                                                                      </div>
                                                                                                      <div style="padding-top: 5px;font-size: 13px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: right; color: #372668;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                                                            ${alarm.createdTime?number_to_date?string["MMM dd, HH:mm"]}
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
                                          </div>
                                    </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-left: 50px;margin-right: 50px;margin-top: 25px;"
                        border="0">
                        <tbody>
                              <tr>
                                    <td style="width: 250px;max-width: 250px;">
                                          
                                          <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                Cleared Time <span style="font-size: 17px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.5px; color: #252528;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">:</span>
                                          </div>
                                    </td>
                                    <td>
                                          <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #372668;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;">
                                                      ${alarm.clearedTime?number_to_date?string["MMM dd, HH:mm"]}
                                          </div>
                                    </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-left: 50px;margin-right: 50px;margin-top: 25px;"
                        border="0">
                        <tbody>
                                    <#if (alarmReadingField.fieldId)??>
                                    <tr>
                                          <td style="width: 250px;max-width: 250px;">
                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #7b7981;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                      Threshold Metric
                                                </div>
                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                      Last Recorded Value <span style="font-size: 17px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.5px; color: #252528;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">:</span>
                                                </div>
                                          </td>
                                          <td>
                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #7b7981;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                      ${alarmReadingField.displayName}
                                                </div>
                                                <div style="font-size: 13px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #372668;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;">
                                                      <span style="font-weight: 600;">
                                                        <#if alarmReadingField.dataType == 3>
                                                        ${(alarm.readingVal?number)!}
                                                        <#else>
                                                            ${alarm.readingVal!}
                                                        </#if>
                                                        </span>
                                                        ${alarmReadingField.unit!}
                                                </div>
                                          </td>
                                    </tr>
                                    </#if>
                              </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-left: 50px;margin-right: 50px;"
                        border="0">
                        <tbody>
                              <tr>
                                    <td style="padding-top: 40px;padding-bottom: 0;">
                                                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;"
                                                border="0">
                                                <tbody>
                                                      <tr>
                                                            <td>
                                                                  <a href="${alarm.url}" style="border-radius: 3px; border: solid 1px #e2dadd;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #717078;;text-decoration: none;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding: 17px 24px;">ALARM SUMMARY</a>
                                                            </td>
                                                      </tr>
                                                </tbody>
                                                </table>
                                    </td>
                              </tr>
                        </tbody>
                  </table>
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
            </div>
      </div>
</body>

</html>