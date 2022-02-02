<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>

<body style="background: #fafafa;">
      <#setting time_zone=org.timezone>
            <div style="max-width:700px;margin: 0 auto;">
                  <div style="margin:60px auto 0;max-width:700px;padding-left: 0;padding-right: 0;background-color:#FFFFFF;padding-bottom: 50px;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px rgba(0, 0, 0, 0.08);">
                        <table role="presentation" cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;"
                              align="center" border="0">
                              <tbody>
                                    <tr>
                                          <td style="text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:0 0px 17px 0px;">
                                                <div class="mj-column-per-100 outlook-group-fix" style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;">
                                                      <table role="presentation" cellpadding="0" cellspacing="0" width="100%"
                                                            border="0">
                                                            <tbody>
                                                                  <tr>
                                                                        <td style="word-wrap:break-word;font-size:0px;padding: 35px 0px 35px 0px;border: 0;border-bottom: 1px solid rgba(0,0,0,0.06)"
                                                                              align="center">
                                                                              <table role="presentation" cellpadding="0"
                                                                                    cellspacing="0" style="border-collapse:collapse;border-spacing:0px;"
                                                                                    align="center" border="0">
                                                                                    <tbody>
                                                                                          <tr>
                                                                                                <td>
                                                                                                      <div style="font-size: 22px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; text-align: center; color: #19191c;text-align: center;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 500px;">
                                                                                                            Daily Maintenance Report - ${.now?date?string["dd MMM yyyy"]}
                                                                                                      </div>
                                                                                                </td>
                                                                                          </tr>
                                                                                    </tbody>
                                                                              </table>
                                                                        </td>
                                                                  </tr>
                                                                  <tr>
                                                                        <td style="word-wrap:break-word;font-size:0px;padding: 45px 45px 20px;"
                                                                              align="left">
                                                                              <table role="presentation" cellpadding="0"
                                                                                    cellspacing="0" style="border-collapse:separate;"
                                                                                    align="left" border="0">
                                                                                    <tbody>
                                                                                          <tr>
                                                                                                <td>
                                                                                                      <div style="font-size: 15px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #19191c;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">Hi,</div>
                                                                                                      <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #78787f;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 15px;">
                                                                                                            Here's your maintenance report for today
                                                                                                      </div>
                                                                                                </td>
                                                                                          </tr>
                                                                                    </tbody>
                                                                              </table>
                                                                        </td>
                                                                  </tr>
                                                            </tbody>
                                                      </table>
                                                </div>
                                          </td>
                                    </tr>
                              </tbody> 
                        </table>
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;"
                              border="0">
                              <tbody>
                                    <tr>
                                          <td style="padding-left: 50px;padding-right: 50px;">
                                                <div style="display: inline-flex;float: left;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 16px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;">
                                                      Overdue <div style="padding-left: 8px;padding-right: 8px; background-color: #a180ff;border-radius: 50px;color: #fff;font-size: 13px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #ffffff;line-height: 23px;float: right; margin-left: 10px;text-align:center;">${overdueCount}</div>
                                                </div>
                                                <div style="float: right;">
                                                      <a href="https://app.facilio.com/app/wo/orders/overdue"
                                                            style="font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-decoration: none;font-size: 12px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.8px; text-align: right; color: #ff3184;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 11px;">VIEW
                                                            ALL</a>
                                                </div>
                                          </td>
                                          <td>
                                          </td>
                                    </tr>
                              </tbody>
                        </table>

                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;padding-left: 30px;padding-right:30px;margin-top:30px;"
                              border="0">
                              <tbody>
                                    <#list overdueWos as workorder>
                                    <#if workorder?is_odd_item>
                                    <#assign color='#f5f5f5'/>
                                    <#else>
                                          <#assign color='#ffffff'/>
                                    </#if>
                                          <tr style="background-color: ${color};">
                                                <td style="padding:0 0 18px 10px;width: 10px;max-width:10px;width: 3%;">
                                                </td>
                                                <td style="padding:18px 10px;width: 25%;max-width:200px;">
                                                      <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.4; letter-spacing: 0.4px; color: #171719; font-family: Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                             ${workorder.subject}
                                                      </div>
                                                </td>
                                                <td style="padding:18px 10px;width: 25%;max-width:150px;">
                                                      <div style="padding-top: 30px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            ${(workorder.resourceName?keep_before(","))!}
                                                      </div>
                                                      <div style="padding-top: 3px;font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            ${(workorder.resourceName?keep_after_last(","))!}
                                                      </div>
                                                </td>
                                                <td style="padding:0;text-align: left;width: 35%;">
                                                      <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            Time Created: ${workorder.createdTime?number_to_date?string["MMM dd, HH:mm"]}
                                                      </div>
                                                      <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                            Due: ${workorder.dueDate?number_to_date?string["MMM dd, HH:mm"]}
                                                      </div>
                                                </td>
                                                <!-- <td style="padding:0;text-align: right;width: 10%;padding-right: 20px;">
                                                      <div style="text-align: left;">
                                                            <div style="text-align: left;">
                                                                  <div style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                         Priority
                                                                  </div>
                                                                  <div style="font-size: 15px; font-weight: 600; color: #fc9b00;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                         ${workorder.priority}
                                                                  </div>
                                                            </div>
                                                </td> -->
                                          </tr>
                                    </#list>
                              </tbody>
                        </table>
                        <!-- second -->
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-top: 50px;"
                              border="0">
                              <tbody>
                                    <tr>
                                          <td style="padding-left: 50px;padding-right: 50px;">
                                                <div style="display: inline-flex;width: 113px;float: left;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 16px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;">
                                                       Due Today <div style="padding-left: 8px;padding-right: 8px; background-color: #2bbbad;border-radius: 50px;color: #fff;font-size: 13px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #ffffff;line-height: 23px;float: right; margin-left: 10px;text-align:center;">${dueTodayCount}</div>
                                                </div>
                                                <div style="float: right;">
                                                      <a href="https://app.facilio.com/app/wo/orders/duetoday" style="font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-decoration: none;font-size: 12px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.8px; text-align: right; color: #ff3184;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 11px;">VIEW
                                                       ALL</a>
                                                </div>
                                          </td>
                                          <td>
                                          </td>
                                    </tr>
                              </tbody>
                        </table>

                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;padding-left: 30px;padding-right:30px;margin-top:20px;"
                              border="0">
                              <tbody>
                                     <#list dueTodayWos as workorder>
                                        <#if workorder?is_odd_item>
                                          <#assign color='#f5f5f5'/>
                                          <#else>
                                                <#assign color='#ffffff'/>
                                          </#if>
                                    <tr style="background-color: ${color};">
                                          <td style="padding:0 0 18px 10px;width: 10px;max-width:10px;width: 3%;">
                                          </td>
                                          <td style="padding:18px 10px;width: 25%;max-width:200px;">
                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.4; letter-spacing: 0.4px; color: #171719; font-family: Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                       ${workorder.subject}
                                                </div>
                                          </td>
                                          <td style="padding:18px 10px;width: 25%;max-width:150px;">
                                                <div style="padding-top: 30px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    ${(workorder.resourceName?keep_before(","))!}
                                                </div>
                                                <div style="padding-top: 3px;font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    ${(workorder.resourceName?keep_after_last(","))!}
                                                </div>
                                          </td>
                                          <td style="padding:0;text-align: left;width: 35%;">
                                                <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    Time Created: ${workorder.createdTime?number_to_date?string["MMM dd, HH:mm"]}
                                                </div>
                                                <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                       Due: ${workorder.dueDate?number_to_date?string["MMM dd, HH:mm"]}
                                                </div>
                                          </td>
                                          <!-- <td style="padding:0;text-align: right;width: 10%;padding-right: 20px;">
                                                <div style="text-align: left;">
                                                      <div style="text-align: left;">
                                                            <div style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                   Priority
                                                            </div>
                                                            <div style="font-size: 15px; font-weight: 600; color: #fc9b00;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                   ${workorder.priority}
                                                            </div>
                                                      </div>
                                          </td> -->
                                    </tr>
                                    </#list>
                              </tbody>
                        </table>
                        <!-- second -->

                        <!-- third -->
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;margin-top: 50px;"
                              border="0">
                              <tbody>
                                    <tr>
                                          <td style="padding-left: 50px;padding-right: 50px;">
                                                <div style="float: left;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 16px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;">
                                                       Open <div style="width: 22px; height: 22px; background-color: #372668;border-radius: 50px;color: #fff;font-size: 13px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #ffffff;line-height: 23px;float: right; margin-left: 10px;text-align:center;">${openCount}</div>
                                                </div>
                                                <div style="float: right;">
                                                      <a href="http://localhost:9090/app/wo/orders/open" style="font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-decoration: none;font-size: 12px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.8px; text-align: right; color: #ff3184;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 11px;">VIEW
                                                       ALL</a>
                                                </div>
                                          </td>
                                          <td>
                                          </td>
                                    </tr>
                              </tbody>
                        </table>

                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:100%;max-width:100%;padding-left: 30px;padding-right:30px;margin-top:20px;"
                              border="0">
                              <tbody>
                                          <#list openWos as workorder>
                                          <#if workorder?is_odd_item>
                                            <#assign color='#f5f5f5'/>
                                            <#else>
                                                  <#assign color='#ffffff'/>
                                            </#if>
                                    <tr style="background-color: ${color};">
                                          <td>
                                          </td>
                                          <td style="padding:18px 10px;width: 25%;max-width:200px;">
                                                <div style="font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.4; letter-spacing: 0.4px; color: #171719; font-family: Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                       ${workorder.subject}
                                                </div>
                                          </td>
                                          <td style="padding:18px 10px;width: 25%;max-width:150px;">
                                                <div style="padding-top: 30px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #252528;padding-top:5px;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    ${(workorder.resourceName?keep_before(","))!}
                                                </div>
                                                <div style="padding-top: 3px;font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    ${(workorder.resourceName?keep_after_last(","))!}
                                                </div>
                                          </td>
                                          <td style="padding:0;text-align: left;width: 35%;">
                                                <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    Time Created: ${workorder.createdTime?number_to_date?string["MMM dd, HH:mm"]}
                                                </div>
                                                <div style="font-size: 12px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.33; letter-spacing: 0.3px; color: #1f1f22;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    Due: ${(workorder.dueDate?number_to_date?string["MMM dd, HH:mm"])!}
                                                </div>
                                          </td>
                                          <!-- <td style="padding:0;text-align: right;width: 10%;padding-right: 20px;">
                                                <div style="text-align: left;">
                                                      <div style="text-align: left;">
                                                            <div style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #7a7a81;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                  Priority
                                                            </div>
                                                            <div style="font-size: 15px; font-weight: 600; color: #fc9b00;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                                   ${workorder.priority}
                                                            </div>
                                                      </div>
                                          </td> -->
                                    </tr>    
                                    </#list>                              
                              </tbody>
                        </table>
                        <!-- third -->

                  </div>
                  <div style="text-align: center;margin-top: 40px;">
                        <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;max-width: 100%;">
                        <p style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: center; color: #8e8c97;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">©
                              2018 FACILIO.COM | ALL RIGHTS RESERVED</p>
                  </div>
            </div>
</body>

</html>