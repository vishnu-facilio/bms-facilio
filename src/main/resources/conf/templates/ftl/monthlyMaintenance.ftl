<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body style="background: #fafafa;">
        <div style="max-width:700px;margin: 0 auto;display: block;padding-top: 50px;padding-bottom: 50px;">
            <div style="margin:20px auto 0;max-width:700px;padding-left: 0;padding-right: 0;background-color:#FFFFFF;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05);-webkit-box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05);-moz-box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px rgba(0, 0, 0, 0.08);">
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
                                                <td style="word-wrap:break-word;font-size:0px;padding:30px;"
                                                    align="left">
                                                    <table role="presentation" cellpadding="0"
                                                        cellspacing="0" style="border-collapse:separate;"
                                                        align="left" border="0">
                                                        <tbody>
                                                            <tr>
                                                                <td style="width: 75%;">
                                                                    <div style="font-size: 15px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">Hi
                                                                    </div>
                                                                    <div style="line-height: 22px;font-size: 13px; font-weight: normal; font-style: normal; font-stretch: normal;letter-spacing: 0.4px; color: #486372;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 15px;">
                                                                        Here’s your
                                                                        summary of your
                                                                        portfolios for
                                                                        the month of <br />
                                                                        ${previousMonthName}.
                                                                    </div>
                                                                </td>
                                                                <td style="width: 30%;">
                                                                    <a href=${getPermaLinkUrl} style="border: 0;border-radius: 3px;background:#ff3989;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #ffffff;text-decoration: none;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding: 17px 24px;display:inline-block;transition: all 0.2s;box-sizing: border-box;">Detailed
                                                                    Summary</a>
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
                <#if woOnCompletion??>
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width: 698pxx;max-width:698px;padding-top: 30px;padding-left: 30px;padding-right: 30px;background: #f8f9fc;" border="0">
                    <tbody>
                        <tr>
                            <td>
                                <div style="font-size: 16px; font-weight: 500; line-height: normal; letter-spacing: 0.3px; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                    Work Completion Rates:
                                </div>
                            </td>
                        </tr>
                        <tr>
                                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;max-width: 700px;padding-top: 30px;background: #f8f9fc;padding-bottom: 20px;min-width: 630px;" border="0">
                                        <tr>
                                            <td style="padding-bottom: 20px;width: 100%;">
                                                <#list woOnCompletion as site>
                                                <div style="vertical-align: top; display: inline-block; direction: ltr; font-size: 13px; text-align: left;    margin-right: 30px;width: 100%;max-width: 175px;">
                                                        <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-right: 10px;">
                                                            <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <div style="padding: 20px 15px 20px;border-radius: 5px; box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important; background-color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;width: 100%;border: 1px solid #F1F0F7;">
                                                                            <div>
                                                                                    <div style="font-size: 14px; font-weight: bold; letter-spacing: 0.4px; text-align: left; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;padding-bottom: 20px;">${site.siteName}</div>
                                                                                    <div style="font-size: 13px; font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;float: left;padding-top: 5px;">On Time</div>
                                                                                    <div style="font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 20px; text-align: left; font-weight: normal; letter-spacing: 0.2px; text-align: right; color: #39c2b0;">${site.onTimePercentage} %</div>
                                                                                    <div style="font-size: 13px; font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;float: left;padding-top: 15px;">Over Due</div>
                                                                                    <div style="font-size: 20px; text-align: right; font-weight: normal; letter-spacing: 0.2px; text-align: right; color: #f08532;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-bottom: 5px;padding-top: 10px;">${site.overduePercentage} %</div>
                                                                                </div>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                      </div>
                                                      </#list>
                                            </td>
                                        </tr>
                                    </table>
                        </tr>
                    </tbody>
                </table>
                </#if>
                <#if topNTechnician??>
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width: 600px;max-width:100%;margin-top: 50px;" border="0">
                    <tbody>
                        <tr>
                            <td style="max-width: 180px;width: 100%;">
                                <div style="padding-left: 30px;font-size: 16px; font-weight: 500; line-height: normal; letter-spacing: 0.3px; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                    Top 3 SLA Conscious Technicians
                                </div>
                                <div style="padding-right: 30px; padding-left: 30px;font-size: 14px; line-height: 1.43; letter-spacing: 0.4px;font-weight: 300; color: #677386;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 10px;">
                                    This is a snapshot of SPI's technician performance based on the average resolution time over ${previousMonthName}. This is to help you recognise best performing technicians and boost the performance of other technicians.
                                </div>
                            </td>
                        </tr>
                        <tr>
                                <table cellpadding="0" cellspacing="0" style="margin-left: 5px;margin-right: 5px;font-size:0px;width:100%;min-width: 675px;max-width:100%;margin-top: 50px; margin-bottom: 50px;text-align: center;"
                                border="0"> 
                                  <tr>
                                      <td>
                                            <#list topNTechnician as technician>
                                          <div style="vertical-align: top; display: inline-block; direction: ltr; font-size: 13px; text-align: left;">
                                            <table cellpadding="0" cellspacing="0" border="0" width="100%">
                                                <tbody>
                                                    <tr>
                                                        <td>
                                                            <div style="margin-right: 10px;padding: 20px 20px 20px;border-radius: 5px; box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important; background-color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 170px;width: 100%;border: 1px solid #F1F0F7;">
                                                                <div style="font-size: 17px; font-weight: 500;letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;width: 170px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${technician.user_name}</div>
                                                                <div style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: center; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${technician.site_name}</div>
                                                                <div style="font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #202730;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 30px;padding-bottom: 5px;">On Time Closed</div>
                                                                <div style="font-size: 40px; font-weight: normal; letter-spacing: 0.4px; text-align: center; color: #ff3989;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">${technician.count}</div>
                                                                <div style="font-size: 10px; font-weight: 400; letter-spacing: 0.5px; text-align: center; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform: uppercase;">Workorders</div>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                          </div>
                                          </#list>
                                      </td>
                                  </tr>
                            </table>
                        </tr>
                    </tbody>
                </table>
                </#if>
                <#if sitesWithResolutionTime??>
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width: 600px;max-width:100%;padding-top: 30px;padding-left: 30px;padding-right: 30px;background: #f8f9fc;"
                    border="0">
                    <tbody>
                        <tr>
                            <td>
                                <div style="font-size: 16px; font-weight: 500; line-height: normal; letter-spacing: 0.3px; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                    Sites that Best Operate with Speed
                                </div>
                                <div style="font-size: 14px; line-height: 1.43; letter-spacing: 0.4px;font-weight: 300; color: #677386;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 10px;">
                                        This is a snapshot of work completion time across sites that best operate and resolve maintenance issues with speed.
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width: 600px;max-width:100%;background: #f8f9fc;padding-bottom: 30px;padding-top: 30px;text-align: center;" border="0">
                                <tr>
                                    <#list sitesWithResolutionTime as site>
                                    <td style="padding-bottom: 20px;width: 210px;">
                                        <div style="margin-right: 10px;padding: 20px 15px 20px;border-radius: 5px; box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important;-webkit-box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important;-moz-box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important; background-color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 170px;width: 100%;border: 1px solid #F1F0F7;">
                                            <div style="font-size: 15px; font-weight: 500;letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;width: 167px; white-space: nowrap; text-overflow: ellipsis; overflow: hidden;">${site.siteName}</div>
                                            <div style="font-size: 13px; font-weight: 400; letter-spacing: 0.4px; text-align: center; color: #ff3989;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 20px;padding-bottom: 5px;"><span style="font-size: 13px; font-weight: 400; letter-spacing: 0.4px; text-align: center; color: #486372;">Avg Time</span> ( Mins )</div>
                                            <div style="padding-top: 10px; padding-bottom: 10px;">
                                                <div style="font-size: 20px; letter-spacing: 0.2px; text-align: center; color: #6974fa;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                                    <#if (site.avgResolutionTime > site.avgResolutionTimeTillLastMonth) >
                                                    <span style="font-size: 20px; font-weight: 400; letter-spacing: 1.5px; text-align: center; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;position: relative;top: -6px;">&#8593;</span>
                                                    </#if>
                                                    <#if (site.avgResolutionTime < site.avgResolutionTimeTillLastMonth)>
                                                    <span style="font-size: 20px; font-weight: 400; letter-spacing: 1.5px; text-align: center; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;position: relative;top: -6px;">&#8595;</span>
                                                    </#if>${site.avgResolutionTime}
                                                </div>
                                            </div>
                                            <div style="font-size: 10px; font-weight: 400; letter-spacing: 0.5px; text-align: center; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-transform: uppercase;padding-top: 10px;">${site.avgResolutionTimeTillLastMonth} in ${previousLastMonthName}</div>
                                        </div>
                                    </td>
                                    </#list>
                                </tr>
                            </table>
                        </tr>
                    </tbody>
                </table>
                </#if>
                <#if totalConsumptionBySite??>
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;max-width: 650px; min-width: 650px;margin-top: 50px; margin-bottom: 50px;"
                    border="0">
                    <tbody>
                        <tr>
                            <td>
                                <div style="font-size: 16px; font-weight: 500; line-height: normal; letter-spacing: 0.3px; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;margin-left: 30px;margin-right: 30px;">
                                        Energy and Water Usage
                                </div>
                                <div style="font-size: 14px; line-height: 1.43; letter-spacing: 0.4px;font-weight: 300; color: #677386;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 10px;margin-left: 30px;margin-right: 30px;">
                                        This is a snapshot of energy and water consumption across your sites.
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width: 600px;max-width:650px;margin-top: 50px;margin-left: 30px;margin-right: 30px;" border="0">
                                <tr>
                                    <td style="padding-bottom: 20px;">
                                        <#list totalConsumptionBySite.total_consumption as consumption>
                                        <div style="float: left; max-width: 280px; min-width: 280px; width: 100%; margin-right: 40px;">
                                            <div style="padding:20px 10px 20px;border-radius: 5px; box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important;web-kit-box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important;-moz-box-shadow: 0 4px 11px 0 rgba(225, 224, 232, 0.5) !important; background-color: #ffffff;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 290px; width: 100%;height: 130px;border: 1px solid #F1F0F7;justify-content: space-around; flex-direction: row;margin-bottom: 20px;margin-right: 20px;">
                                                <div>
                                                    <div style="font-size: 15px; font-weight: 500;letter-spacing: 0.4px; text-align: left; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 5px;padding-left: 10px;">${consumption.siteName}</div>
                                                    <div style="font-size: 11px; letter-spacing: 0.3px; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-left: 10px;padding-top: 5px;">${previousMonthName}</div>
                                                    <div style="padding-top: 20px;padding-left: 10px;">
                                                        <div style="font-size: 13px;font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;float: left;max-width: 150px;padding-top: 5px;">Energy</div>
                                                        <div id="energy" style="font-size: 20px;text-align: left; font-weight: normal;letter-spacing: 0.2px; text-align: right; color: #ff3184;padding-left: 35px;float: left;max-width: 200px;font-weight: 300;">${consumption.energyConsumption}<#if (consumption.energyUnit != "--")> <span style="font-size: 12px; font-weight: 400; letter-spacing: 0.4px; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;width: 100%;padding-left: 5px;">${consumption.energyUnit}</span></#if></div>
                                                        <div style="clear: both;">
                                                            <div style="font-size: 13px;font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;float: left;max-width: 150px;padding-top: 5px;">RO Water</div>
                                                            <div style="font-size: 20px;text-align: left; font-weight: normal;letter-spacing: 0.2px; text-align: right; color: #ff3184;padding-left: 20px;float: left;max-width: 200px;font-weight: 300;">${consumption.waterConsumption} <#if (consumption.waterUnit != "--")><span style="font-size: 12px; font-weight: 400; letter-spacing: 0.4px; color: #8ca1ad;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;width: 100%;padding-left: 5px;">${consumption.waterUnit}</span></#if></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        </#list>
                                    </td>
                                </tr>
                            </table>
                        </tr>
                    </tbody>
                </table>
                </#if>
                <table cellpadding="0" cellspacing="0" style="padding-left: 30px;padding-right: 30px;font-size:0px;width:100%;min-width: 600px;max-width:100%;margin-bottom: 50px;" border="0">
                    <tr>
                        <td>
                            <div style="font-size: 13px;  letter-spacing: 0.3px; text-align: center; color: #324056;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-align: center;">For deeper insights into your organisation</div>
                            <div style="padding-top: 30px;text-align: center;">
                                <a href=${getPermaLinkUrl} style="border: 0;border-radius: 3px;background:#ff3989;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #ffffff;text-decoration: none;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding: 17px 24px;">Detailed
                                Summary</a>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div style="text-align: center;margin-top: 20px;">
                <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
                <div style="font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;font-size: 11px;  letter-spacing: 0.3px; text-align: center; color: #8e8c97;">© 2018 FACILIO.COM | ALL RIGHTS RESERVED</div>
            </div>
        </div>
    </body>
</html>