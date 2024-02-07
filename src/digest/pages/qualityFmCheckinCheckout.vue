<template>
  <div>
    <div
      v-if="loading"
      class="fc-pm-main-bg height100vh fc-grid-center"
      style="margin-left: auto;margin-right: auto;"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-if="!loading">
      <!-- start -->
      <div
        style="max-width:750px;margin: 0 auto;display: block;padding-top: 0px;padding-bottom: 30px;padding-left: 0px;padding-right: 0px;"
      >
        <div
          style="margin:0 auto 0;padding: 0px 0 30px;max-width:750px;padding-left: 0;padding-right: 0;background-color:#FFFFFF;padding-bottom: 50px;"
        >
          <table
            cellpadding="0"
            cellspacing="0"
            style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 0px;padding-top: 30px;"
            border="0"
          >
            <tbody>
              <tr>
                <td colspan="50%" style="text-align: left;padding: 10px 0px 0;">
                  <img
                    src="~assets/facilio-blue-logo.svg"
                    style="height:25px;"
                  />
                </td>
                <td
                  colspan="50%"
                  style="text-align: right;padding: 10px 0px 0;"
                >
                  <img
                    src="~assets/customer-logo/emirtage.png"
                    width="120"
                    height="30"
                    class="object-scale-down"
                  />
                </td>
              </tr>

              <tr>
                <td colspan="100%" style="padding: 20px 0px;">
                  <div
                    class="fwBold"
                    style="font-size:30px;text-align:left;font-size:15px;font-weight:normal;line-height:normal;letter-spacing:0.3px;color:#0e0f2a"
                  >
                    Hi Sumith Sukumaran,
                  </div>
                  <div
                    style="padding-top:10px;font-size:14px;font-weight:normal;font-stretch:normal;font-style:normal;line-height:1.57;letter-spacing:0.3px;color:#232441"
                  >
                    Below is the CICO Digest for the last week ({{
                      resultObj.startTime
                    }}
                    - {{ resultObj.endTime }})
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <table
            cellpadding="0"
            cellspacing="0"
            style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 0px;padding-top: 30px;border-left: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;"
            border="0"
          >
            <tr>
              <td colspan="100%" class="fc-digest-bg-violet pT15 pB15">
                <div class="fc-digest-font-white">
                  CICO Summary of all WOs ( 01-01-2020 till
                  {{ resultObj.endTime }} )
                </div>
              </td>
            </tr>
            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              ></td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                No Of CICO
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  (Till {{ resultObj.endTime }})
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                Percentage Open
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  (CICO Open)
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                Percentage Hold
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  (CICO Hold)
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                Average Completion Time(Days)
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                ></div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;"
              >
                <div class="fc-black-12 text-left fwBold">
                  Total booked until current day
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div class="fc-black-12 text-left fwBold">
                  {{ resultObj.totalWoInProperties }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalOpenWoInProperties }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalClosedWoInProperties }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAllWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%"
              >
                <div class="fc-black-12 text-left fwBold">
                  Al Wahaibi Properties
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAlWahaibiWo }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAlWahaibiWoOpen }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAlWahaibiWoClose }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAlWahaibiWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%"
              >
                <div class="fc-black-12 text-left fwBold">
                  BD Properties
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalBdPropertiesWo }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalBdPropertiesWoOpen }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalBdPropertiesWoClose }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalBdPropertiesWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%"
              >
                <div class="fc-black-12 text-left fwBold">
                  AM properties
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAmPropertiesWo }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAmPropertiesWoOpen }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAmPropertiesWoClose }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalAmPropertiesWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%"
              >
                <div class="fc-black-12 text-left fwBold">
                  Eqarat properties
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalEqaratPropertiesWo }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalEqaratPropertiesWoOpen }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalEqaratPropertiesWoClose }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalEqaratPropertiessWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%"
              >
                <div class="fc-black-12 text-left fwBold">
                  DG Portfolio
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalDgPropertiesWo }}
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalDgPropertiesWoOpen }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalDgPropertiesWoClose }}%
                </div>
              </td>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee;width: 25%font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalDgPropertiessWoAvgComp }}
                </div>
              </td>
            </tr>
            <!-- second row -->
          </table>

          <table
            cellpadding="0"
            cellspacing="0"
            style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 30px;border-left: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;"
            border="0"
          >
            <tr>
              <td colspan="100%" class="fc-digest-bg-violet pT15 pB15">
                <div class="fc-digest-font-white">
                  WOs open as of {{ resultObj.endTime }}
                </div>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-right: 1px solid #e2e8ee;width: 20%;"
              ></td>
              <td colspan="80%" style="width: 80%;">
                <table
                  cellpadding="0"
                  cellspacing="0"
                  style="width:100%;max-width:750px;padding-bottom: 0px; padding: 0;margin-bottom: -1px;"
                  border="0"
                >
                  <tr>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Unit #
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        CO Date
                      </div>
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        [site review date]
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        No Of Days <br />
                        In Open <br />Status(Days)
                      </div>
                    </td>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Default <br />
                        Hand <br />
                        Over Date
                      </div>
                    </td>
                    <td
                      colspan="23.8%"
                      style="width: 23.8%;vertical-align: top;border-bottom: 1px solid #e2e8ee;text-align: center;"
                    >
                      <div
                        style="padding: 10px 10px 0;width: 100%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Variation
                      </div>
                      <div
                        style="padding: 0 10px 10px;width: 100%;font-size: 12px; font-weight: 400;border-bottom: 1px solid #e2e8ee; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        (days +/-) <br />
                        [standard timeline <br />
                        : 5 days]
                      </div>
                      <div
                        class="display-flex"
                        style="width: 100%;font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53;color: #324056;"
                      >
                        <div style="width: 49.9%;vertical-align: top;">
                          Variation excluding on hold time
                        </div>
                        <div
                          style="width: 50%;vertical-align: top;padding: 10px;border-left: 1px solid #e2e8ee;"
                        >
                          Total variation
                        </div>
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>

            <tr>
              <td
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 20%;"
              >
                <div class="fc-black-12 text-left fwBold">
                  {{
                    resultObj.buildingVsUnitFirstItem[0].name
                      ? resultObj.buildingVsUnitFirstItem[0].name
                      : '---'
                  }}
                </div>
              </td>
              <td colspan="80%">
                <table
                  cellpadding="0"
                  cellspacing="0"
                  style="width:100%;max-width:750px;padding-bottom: 0px; padding: 0;margin-bottom: -1px;"
                  border="0"
                >
                  <tr
                    v-if="resultObj.buildingVsUnitFirstItem[0]"
                    v-for="(list, index) in resultObj.buildingVsUnitFirstItem[0]
                      .lists"
                    :key="index"
                  >
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.unitName }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.date ? list.date : 'Not started' }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.daysInOpen }}
                      </div>
                    </td>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.handOverDate ? list.handOverDate : '---' }}
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;border-right: 1px solid #e2e8ee;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          0
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variation.includes('+')
                                ? '+' + ~~list.variation
                                : ~~list.variation
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          {{ list.onHoldDuration }}
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variationindays + list.onHoldDuration
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>

            <!-- second row -->
            <tr
              v-if="resultObj.buildingVsUnitList[0]"
              v-for="(buildingVsUnit, index) in resultObj.buildingVsUnitList"
              :key="index"
            >
              <td
                v-if="buildingVsUnit.lists && buildingVsUnit.lists[0]"
                colspan="20%"
                style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 20%;"
              >
                <div class="fc-black-12 text-left fwBold">
                  {{ buildingVsUnit.name }}
                </div>
              </td>
              <td
                colspan="80%"
                v-if="buildingVsUnit.lists && buildingVsUnit.lists[0]"
              >
                <table
                  cellpadding="0"
                  cellspacing="0"
                  style="width:100%;max-width:750px;padding-bottom: 0px; padding: 0;margin-bottom: -1px;"
                  border="0"
                >
                  <tr v-for="(list, idx) in buildingVsUnit.lists" :key="idx">
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid rgb(226, 232, 238); border-right: 1px solid rgb(226, 232, 238);padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.unitName }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid rgb(226, 232, 238); border-right: 1px solid rgb(226, 232, 238);padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.date ? list.date : 'Not started' }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top; border-bottom: 1px solid rgb(226, 232, 238); border-right: 1px solid rgb(226, 232, 238);padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.daysInOpen }}
                      </div>
                    </td>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-right: 1px solid #e2e8ee;padding: 10px;border-bottom: 1px solid rgb(226, 232, 238);"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.handOverDate ? list.handOverDate : '---' }}
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;padding: 10px;border-bottom: 1px solid rgb(226, 232, 238);border-right: 1px solid #e2e8ee;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          0
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variation.includes('+')
                                ? '+' + ~~list.variation
                                : ~~list.variation
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;padding: 10px;border-bottom: 1px solid rgb(226, 232, 238);"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          {{ list.onHoldDuration }}
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variationindays + list.onHoldDuration
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>

          <table
            cellpadding="0"
            cellspacing="0"
            style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 30px;border-left: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;"
            border="0"
          >
            <tr>
              <td colspan="100%" class="fc-digest-bg-violet pT15 pB15">
                <div class="fc-digest-font-white">
                  WOs closed in the last week ({{ resultObj.startTime }} till
                  {{ resultObj.endTime }} )
                </div>
              </td>
            </tr>

            <tr
              v-if="
                resultObj.buildingVsUnitFirstItemForClosed &&
                  resultObj.buildingVsUnitFirstItemForClosed[0]
              "
            >
              <td
                colspan="20%"
                rowspan=""
                style="width: 20%;vertical-align: middle;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.buildingVsUnitFirstItemForClosed[0].name }}
                </div>
              </td>
              <td colspan="80%">
                <table
                  cellpadding="0"
                  cellspacing="0"
                  style="width:100%;max-width:750px;padding-bottom: 0px; padding: 0;margin-bottom: -1px;"
                  border="0"
                >
                  <tr>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Unit #
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        CO Date
                      </div>
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        [site review date]
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        No Of Days In Open Status(Days)
                      </div>
                    </td>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Default Hand Over Date
                      </div>
                    </td>
                    <td
                      colspan="23.8%"
                      style="width: 23.8%;vertical-align: top;border-bottom: 1px solid #e2e8ee;text-align: center;"
                    >
                      <div
                        style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        Variation
                      </div>
                      <div
                        style="font-size: 12px; font-weight: 400;padding:0 10px 10px ;border-bottom: 1px solid #e2e8ee; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        (days +/-) [standard timeline : 5 days]
                      </div>
                      <div
                        class="display-flex width100"
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53;color: #324056;"
                      >
                        <div style="width: 49.9%;vertical-align: top;">
                          Variation excluding on hold time
                        </div>
                        <div
                          style="width: 50%;vertical-align: top;padding: 10px;border-left: 1px solid #e2e8ee;"
                        >
                          Total variation
                        </div>
                      </div>
                    </td>
                  </tr>

                  <div
                    style="display: table-row-group;"
                    v-if="
                      resultObj.buildingVsUnitFirstItemForClosed &&
                        resultObj.buildingVsUnitFirstItemForClosed[0] &&
                        resultObj.buildingVsUnitFirstItemForClosed[0].lists
                    "
                  >
                    <tr
                      v-for="(list, index) in resultObj
                        .buildingVsUnitFirstItemForClosed[0].lists"
                      :key="index"
                    >
                      <td
                        colspan="18%"
                        style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                      >
                        <div
                          style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          {{ list.unitName }}
                        </div>
                      </td>
                      <td
                        colspan="20%"
                        style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                      >
                        <div
                          style="font-size: 12px;font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          {{ list.date ? list.date : 'Not started' }}
                        </div>
                      </td>
                      <td
                        colspan="20%"
                        style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                      >
                        <div
                          style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          {{ list.daysInOpen }}
                        </div>
                      </td>
                      <td
                        colspan="18%"
                        style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                      >
                        <div
                          style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          {{ list.handOverDate ? list.handOverDate : '---' }}
                        </div>
                      </td>
                      <td
                        colspan="12%"
                        style="width: 12%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px; border-right: 1px solid rgb(226, 232, 238);"
                      >
                        <div
                          style="font-size: 12px;font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          <span v-if="list.daysInOpen === 5">
                            0
                          </span>
                          <span v-else>
                            {{
                              list.variation && list.date
                                ? list.variation.includes('+')
                                  ? '+' + ~~list.variation
                                  : ~~list.variation
                                : '---'
                            }}
                          </span>
                        </div>
                      </td>
                      <td
                        colspan="12%"
                        style="width: 23.79%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;"
                      >
                        <div
                          style="font-size: 12px;font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                        >
                          <span v-if="list.daysInOpen === 5">
                            {{ list.onHoldDuration }}
                          </span>
                          <span v-else>
                            {{
                              list.variation && list.date
                                ? list.variationindays + list.onHoldDuration
                                : '---'
                            }}
                          </span>
                        </div>
                      </td>
                    </tr>
                  </div>
                </table>
              </td>
            </tr>

            <tr v-else>
              <td colspan="100%">
                <div class="label-txt-black bold text-center pT20 pB20">
                  No Closed Work Order
                </div>
              </td>
            </tr>

            <!-- second row -->
            <tr
              v-if="resultObj.buildingVsUnitListForClosed[0]"
              v-for="(buildingVsUnit,
              index) in resultObj.buildingVsUnitListForClosed"
              :key="index"
            >
              <td
                v-if="buildingVsUnit.lists && buildingVsUnit.lists[0]"
                colspan="20%"
                style="width: 20%;vertical-align: middle; text-align: left;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ buildingVsUnit.name }}
                </div>
              </td>
              <td
                v-if="buildingVsUnit.lists && buildingVsUnit.lists[0]"
                colspan="80%"
              >
                <table
                  cellpadding="0"
                  cellspacing="0"
                  style="width:100%;max-width:750px;padding-bottom: 0px; padding: 0;margin-bottom: -1px;"
                  border="0"
                >
                  <tr v-for="(list, idx) in buildingVsUnit.lists" :key="idx">
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.unitName }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.date ? list.date : 'Not started' }}
                      </div>
                    </td>
                    <td
                      colspan="20%"
                      style="width: 20%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.daysInOpen }}
                      </div>
                    </td>
                    <td
                      colspan="18%"
                      style="width: 18%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        {{ list.handOverDate ? list.handOverDate : '---' }}
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;border-right: 1px solid #e2e8ee;"
                    >
                      <div
                        style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          0
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variation.includes('+')
                                ? '+' + ~~list.variation
                                : ~~list.variation
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                    <td
                      colspan="12%"
                      style="width: 12%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;"
                    >
                      <div
                        style="font-size: 12px;font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                      >
                        <span v-if="list.daysInOpen === 5">
                          {{ list.onHoldDuration }}
                        </span>
                        <span v-else>
                          {{
                            list.variation && list.date
                              ? list.variationindays + list.onHoldDuration
                              : '---'
                          }}
                        </span>
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>

          <table
            cellpadding="0"
            cellspacing="0"
            style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 30px;border-left: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;"
            border="0"
          >
            <tr>
              <td colspan="100%" class="fc-digest-bg-violet pT15 pB15">
                <div class="fc-digest-font-white">
                  Detailed CICO Statistics for all WOs ( 01-01-2020 till
                  {{ resultObj.endTime }} )
                </div>
              </td>
            </tr>
            <tr>
              <td
                colspan="25%"
                style="padding: 10px;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;"
              >
                Total CICO Until Now (For full year)
              </td>
              <td
                colspan="25%"
                style="padding: 10px;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;"
              >
                No of Work Orders
              </td>
              <td
                colspan="25%"
                style="padding: 10px;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;"
              >
                No of Work Orders Completed
              </td>
              <td
                colspan="25%"
                style="padding: 10px;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;border-bottom: 1px solid #e2e8ee;border-right: 1px solid #e2e8ee;"
              >
                Average Completion Time(Days)
              </td>
            </tr>

            <tr>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;border-left: 1px solid #e2e8ee;"
              >
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  Full Portfolios
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;border-left: 1px solid #e2e8ee;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalWorkorder }}
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;border-left: 1px solid #e2e8ee;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalClosedWorkorder }}
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ resultObj.totalWoAvgComp }}
                </div>
              </td>
            </tr>

            <tr v-for="(building, index) in buildings" :key="index">
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ building.name }}
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ building.totalWo }}
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ building.totalClosedWo }}
                </div>
              </td>
              <td
                colspan="25%"
                style="width: 25%;vertical-align: top;border-bottom: 1px solid #e2e8ee;padding: 10px;"
              >
                <div
                  style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  {{ building.WoAvgComp }}
                </div>
              </td>
            </tr>
          </table>

          <div>
            <table
              cellpadding="0"
              cellspacing="0"
              style="width:100%;max-width:750px;padding-left: 0px;padding-right: 0px;padding-bottom: 0px;margin-top: 30px;"
              border="0"
            >
              <tr>
                <td colspan="33.3%" class="fc-digest-bg-violet text-left p10">
                  <div class="fc-digest-font-white">
                    Time Taken
                  </div>
                  <div
                    style="font-size: 13px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #fff;"
                  >
                    (excluding on hold)
                  </div>
                </td>
                <td colspan="33.3%" class="fc-digest-bg-violet text-left p10">
                  <div class="fc-digest-font-white text-center">
                    This week {{ resultObj.startTime }} -
                    {{ resultObj.endTime }}
                  </div>
                </td>
                <td colspan="33.3%" class="fc-digest-bg-violet text-left p10">
                  <div class="fc-digest-font-white text-center">
                    Last week {{ resultObj.lastWeekstartTime }} -
                    {{ resultObj.lastWeekendTime }}
                  </div>
                </td>
              </tr>
              <tr>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;border-left: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    Avg work duration (closed wo) (days)
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ getTotalWoAvgCompThisWeek() }}
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="border-right: 1px solid #e2e8ee;padding: 10px;border-bottom: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ getTotalWoAvgCompLastWeek() }}
                  </div>
                </td>
              </tr>
              <tr>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;border-left: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    Max time taken for work completion(days)
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ resultObj.maxdaysThisWeek }}
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="border-right: 1px solid #e2e8ee;padding: 10px;border-bottom: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ resultObj.maxdaysLastWeek }}
                  </div>
                </td>
              </tr>
              <tr>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;border-left: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    Min time taken for work completion(days)
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="padding: 10px;border-bottom: 1px solid #e2e8ee; border-right: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ resultObj.mindaysThisWeek }}
                  </div>
                </td>
                <td
                  colspan="33.3%"
                  style="border-right: 1px solid #e2e8ee;padding: 10px;border-bottom: 1px solid #e2e8ee;width: 33.3%;font-size: 12px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                >
                  <div
                    style="font-size: 12px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;"
                  >
                    {{ resultObj.mindaysLastWeek }}
                  </div>
                </td>
              </tr>
            </table>
          </div>
        </div>
        <!-- <div style="text-align: center;" class="fc-digest-footer">
        <div
          style="font-size: 11px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.28px; text-align: center; color: #324056;">
          © 2020 <a href="#"
            style="padding-left: 5px;font-size: 11px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: 20px; letter-spacing: 0.5px; color: #396dc2 !important;text-decoration: none;">https://facilio.com</a>
          | ALL RIGHTS RESERVED</div>
      </div> -->
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'

export default {
  components: {
    Spinner,
  },

  data() {
    return {
      loading: false,
      workflow: {},
      resultObj: {},
      buildings: [],
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      this.loading = true
      let params = {
        defaultWorkflowId: 199,
      }

      API.post('/v2/workflow/getDefaultWorkflowResult', params).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error)
          } else {
            let { workflow } = data
            let { returnValue } = workflow
            let { buildings } = returnValue
            this.workflow = workflow
            this.resultObj = returnValue
            this.buildings = buildings
          }
          this.loading = false
        }
      )
    },
    getTotalWoAvgCompThisWeek() {
      let { resultObj } = this
      if (resultObj.totalWoAvgCompThisWeek > 0) {
        return resultObj.totalWoAvgCompThisWeek
      } else if (
        (resultObj.maxdaysThisWeek > 0 || resultObj.mindaysThisWeek > 0) &&
        resultObj.totalWoAvgCompThisWeek === 0
      ) {
        return 'Less than a day'
      }
      return 0
    },
    getTotalWoAvgCompLastWeek() {
      let { resultObj } = this
      if (resultObj.totalWoAvgCompLastWeek > 0) {
        return resultObj.totalWoAvgCompLastWeek
      } else if (
        (resultObj.maxdaysLastWeek > 0 || resultObj.mindaysLastWeek > 0) &&
        resultObj.totalWoAvgCompLastWeek === 0
      ) {
        return 'Less than a day'
      }
      return 0
    },
  },
}
</script>
<style lang="scss">
.fc-digest-bg-violet {
  text-align: center;
  border: 1px solid #6643cb;
  background: #6643cb;

  .fc-digest-font-white {
    font-weight: bold;
    font-size: 13px;
    line-height: 1.53;
    letter-spacing: 0.4px;
    color: rgb(255, 255, 255);
  }
}

.fc-digest-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}

@media print {
  @page {
    orphans: 4;
    widows: 2;
    z-index: 900;
    margin-top: 0.9cm;
    margin-bottom: 0.9cm;
  }

  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  body {
    overflow: scroll;
    background: #ffffff;
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  table {
    page-break-after: auto;
    page-break-inside: auto;
  }

  tr {
    page-break-inside: auto;
    page-break-after: auto;
  }

  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  thead {
    display: table-header-group;
  }

  tfoot {
    display: table-footer-group;
  }
}
</style>
