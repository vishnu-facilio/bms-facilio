<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Page Title</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" media="screen" href="main.css">
    <script src="main.js"></script>
</head>

<body>
    <div style="background:#fafafa">
        <div style="max-width:700px;margin:0 auto;display:block;padding-top:50px;padding-bottom:50px">
            <div style="margin:20px auto 0;max-width:700px;padding-left:0;padding-right:0;background-color:#ffffff;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05);">
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%" align="center" border="0">
                    <tbody>
                        <tr>
                            <td style="text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:0 0px 17px 0px">
                                <div class="" style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%">
                                    <table cellpadding="0" cellspacing="0" width="100%" border="0">
                                        <tbody>
                                            <tr>
                                                <td style="word-wrap:break-word;font-size:0px;padding:30px;border-bottom: 1px solid #ecf2f9;" align="left">
                                                    <table cellpadding="0" cellspacing="0" style="border-collapse:separate" align="left" border="0">
                                                        <tbody>
                                                            <tr>
                                                                <td style="width:75%">
                                                                    <div style="font-size:15px;font-weight:normal;font-style:normal;font-stretch:normal;line-height:normal;letter-spacing:0.3px;color:#324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif">
                                                                    </div>
                                                                    <div style="line-height:22px;font-size:13px;font-weight:normal;font-style:normal;font-stretch:normal;letter-spacing:0.4px;color:#486372;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:15px">
                                                                        Hi,
                                                                        <br/> Here’s a quick overview of ${siteName} performance during ${previousMonthName}.
                                                                        <br/>

                                                                    </div>
                                                                </td>
                                                                <td style="width:30%">
                                                                    <a href=${getPermaLinkUrl} style="border: 0;border-radius: 3px;background:#ff3989;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #ffffff;text-decoration: none;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding: 13px 14px;display:inline-block;transition: all 0.2s;box-sizing: border-box;">Explore Performance</a>
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
                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:600px;max-width:100%;margin-top:0px;margin-left: 30px;margin-right: 30px;margin-bottom:50px;padding-left:30px;padding-right:30px" border="0">
                    <tbody>
                        
                         <#if plannedType??>

                        <tr>
                            <td>
                                <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:650px;max-width:100%;margin-top:20px;margin-bottom:20px;text-align:left;" border="0">
                                    <tbody>
                                       <tr>
                                    <td colspan="100%">
                                        <div style="font-size: 16px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;">
                                            Operations & Maintenance Spotlight
                                      </div>
                                        <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.43; letter-spacing: 0.5px; color: #677386;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:10px;font-weight: 300;padding-bottom: 20px;">
                                                 Here’s what your team has been up to last month - find out how many tasks were scheduled, work orders created and new jobs in your buildings.
                                        </div>
                                        <div style="font-size: 15px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px;padding-bottom:15px; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;text-align: left;">
                                    Planned Work Orders
                                </div>
                                    </td>
                                </tr>
                                            <tr>

                                                <td style="text-align: center;">
                                                    <#list plannedType as planned>

                                                        <div style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:center;margin-right:50px;width: 25.3%;">
                                                            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-right:10px">
                                                                <tbody>
                                                                    <tr>
                                                                        <td>
                                                                            <div style="margin-right:10px;padding:20px 20px 20px;border-radius:5px;background-color:#ffffff;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;width:100%;border:1px solid #f1f0f7;">
                                                                                <div style="font-size: 14px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:5px">${planned.resourceName}</div>
                                                                                <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #6974fa;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 20px;">${planned.plannedCount}</div>
                                                                                <div style="font-size:10px;font-weight:400;letter-spacing:1.5px;text-align:center;color:#8ca1ad;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;text-transform:uppercase">WORKORDERS</div>
                                                                                <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 30px;">
                                                                                    <#if planned.difference==1>
                                                                                        <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #228b22;">${planned.percentage}% </span><span style="color:#228b22;font-size: 15px;">&#8593;</span> than ${previousLastMonthName}
                                                                                        <#elseif planned.difference==3>
                                                                                            <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #f35a5a;">${planned.percentage}% </span><span style="color:#f35a5a;font-size: 15px;">&#8595;</span> than ${previousLastMonthName}
                                                                                            <#elseif planned.difference==2>
                                                                                                <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #f08532;">Same as ${previousLastMonthName}</span>

                                                                                </div>
                                                                                </#if>

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
            </tbody>
            </table>

            </td>
            </tr>
            </#if>

            <!-- second row -->

            <#if unPlannedType??>

                <tr>
                    <td>
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:650px;max-width:100%;margin-top:20px;margin-bottom:20px;text-align:left;" border="0">
                            <tbody>
                                  <tr>
                                    <td colspan="100%">
                                        <div style="font-size: 15px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; padding-bottom:20px;color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;">
                                            Unplanned Workorders
                                        </div>
                                    </td>
                                </tr>
                                <tr>

                                    <td style="text-align: center;">
                                        <#list unPlannedType as unplanned>

                                            <div style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:center;margin-right:50px;width: 25.3%;">
                                                <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-right:10px">
                                                    <tbody>
                                                        <tr>
                                                            <td>

                                                                <div style="margin-right:10px;padding:20px 20px 20px;border-radius:5px;background-color:#ffffff;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;width:100%;border:1px solid #f1f0f7;">
                                                                    <div style="font-size: 14px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:5px">${unplanned.resourceName}</div>
                                                                    <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #ff3989;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 20px;">${unplanned.unplannedCount}</div>
                                                                    <div style="font-size:10px;font-weight:400;letter-spacing:1.5px;text-align:center;color:#8ca1ad;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;text-transform:uppercase">WORKORDERS</div>
                                                                    <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 30px;">
                                                                        <#if unplanned.difference==1>
                                                                            <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #228b22;">${unplanned.percentage}% </span><span style="color:#228b22;font-size: 15px;">&#8593;</span> than ${previousLastMonthName}
                                                                            <#elseif unplanned.difference==3>
                                                                                <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color:#f35a5a ;">${unplanned.percentage}% </span><span style="color:#f35a5a;font-size: 15px;">&#8595;</span> than ${previousLastMonthName}
                                                                                <#elseif unplanned.difference==2>
                                                                                    <span style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #f08532;">Same as ${previousLastMonthName} </span>

                                                                    </div>
                                                                    </#if>
                                                                </div>
                                                            </td>

                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </#list>

                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </#if>
            <!-- third row -->
            <#if topTechWithOpenClosed??>

                <tr>
                    <td>
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:650px;max-width:100%;margin-top:20px;margin-bottom:20px;text-align:left;" border="0">
                            <tbody>
                                <tr>
                                    <td colspan="100%">
                                        <div style="background: #f5f5f5;width: 100%;height: 1px;margin-bottom: 30px;"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="100%">
                                        <div style="font-size: 16px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;">
                                            Top Performers of the Month
                                        </div>
                                        <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.43; letter-spacing: 0.5px; color: #677386;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:10px;font-weight: 300;padding-bottom: 20px;padding-right: 30px;">
                                            Stay on top of your teams’ work - find out top performing teams of ${siteName}, work orders closed and overall work progress last month.
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <#list topTechWithOpenClosed as technician>
                                            <div style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:center;margin-right:50px;width: 25.3%;">
                                                <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-right:10px">
                                                    <tbody>
                                                        <tr>
                                                            <td>

                                                                <div style="margin-right:10px;padding:20px 20px 20px;border-radius:5px;background-color:#ffffff;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;width:100%;border:1px solid #f1f0f7;">
                                                                    <div style="font-size: 14px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:5px">${technician.technicianName}</div>
                                                                    <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center; color: #6974fa;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 20px;">${technician.closed}</div>
                                                                    <div style="font-size:10px;font-weight:400;letter-spacing:1.5px;text-align:center;color:#8ca1ad;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;text-transform:uppercase">CLOSED WORKORDERS</div>
                                                                    <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 30px;">
                                                                        <span style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; font-weight: bold; color: #d31738;">${technician.open}</span> Open.</div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>

                                        </#list>

                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </#if>
            <!-- fourth row -->
            <#if topTechnicians??>
                <tr>
                    <td>
                        <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:650px;max-width:100%;margin-top:20px;text-align:left;" border="0">
                            <tbody>
                                <tr>
                                    <td colspan="100%">
                                        <div style="background: #f5f5f5;width: 100%;height: 1px;margin-bottom: 30px;"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="100%">
                                        <div style="font-size: 16px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;">
                                            ${siteName} RockStars
                                        </div>
                                        <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: 1.43; letter-spacing: 0.5px; color: #677386;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:10px;font-weight: 300;padding-bottom: 20px;padding-right: 30px;">
                                            Who did the best job of the lot? Here’re your rock stars from ${previousMonthName} who executed work orders with precision and quick at that!.
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <#list topTechnicians as tech>

                                            <div style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:center;margin-right:50px;width: 25.3%;">
                                                <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-right:10px">
                                                    <tbody>
                                                        <tr>
                                                            <td>
                                                                <div style="margin-right:10px;padding:20px 20px 20px;border-radius:5px;background-color:#ffffff;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;width:100%;border:1px solid #f1f0f7;">
                                                                    <div style="font-size: 14px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top:5px">

                                                                        ${tech.technicianName}</div>
                                                                    <div style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.3px; text-align: center; color: #324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 30px;padding-bottom: 10px">
                                                                        Avg Duration
                                                                    </div>
                                                                    <div style="font-size: 35px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.9px; text-align: center;color: #ff3989;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;">
                                                                        ${tech.avgResolutionTime} <span style="color: #324056;font-size: 13px;letter-spacing: 0.5px;"> (Mins)</span></div>
                                                                    <div style="font-size: 14px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #8ca1ad;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;padding-top: 30px;">
                                                                        ${tech.avg_resolution_time_last_month} mins In ${previousLastMonthName}.
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
                            </tbody>
                        </table>
                    </td>
                </tr>
            </#if>
            </tbody>
            </table>

            <table cellpadding="0" cellspacing="0" style="padding-left:30px;padding-right:30px;font-size:0px;width:100%;min-width:600px;max-width:100%;margin-bottom:50px;padding-bottom: 50px;" border="0">
                <tbody>
                    <tr>
                        <td colspan="100%">
                            <div style="background: #f5f5f5;width: 100%;height: 1px;margin-bottom: 30px;"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div style="font-size:13px;letter-spacing:0.3px;text-align:center;color:#324056;font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;text-align:center">For drill-down analysis into your teams, operations and ${siteName} performance</div>
                            <div style="padding-top:30px;text-align:center;padding-bottom: 30px;">
                                <a href=${getPermaLinkUrl} style="border: 0;border-radius: 3px;background:#ff3989;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; text-align: center; color: #ffffff;text-decoration: none;font-family:Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding: 17px 24px;display:inline-block;transition: all 0.2s;box-sizing: border-box;">Explore Performance</a>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div style="text-align:center;margin-top:20px">
            <img src="https://ci3.googleusercontent.com/proxy/im9Pl_7N6Um7UuuyeoNM092dUqZgGoFY9_mfQaguaOXoTsTWC0tuw-lbVwU_nI1YQatw77LJP29N_f8plw=s0-d-e1-ft#https://facilio.com/assets/images/logo.png" alt="" title="" style="width:70px;height:16x;padding-bottom:10px;max-width:100%" class="CToWUd">
            <div style="font-family:Ubuntu,Helvetica,Arial,sans-serif,Times New Roman,Times,serif;font-size:11px;letter-spacing:0.3px;text-align:center;color:#8e8c97">© 2018 <a href="http://FACILIO.COM" target="_blank" data-saferedirecturl="https://www.google.com/url?q=http://FACILIO.COM&amp;source=gmail&amp;ust=1549953854229000&amp;usg=AFQjCNHEaKFMCTC2w9BJGfkyKb1W1vtmmg" style="font-size: 10px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.4px; color: #4a90e2;">FACILIO.COM</a> | ALL RIGHTS RESERVED</div>
        </div>
    </div>
    </div>
</body>

</html>