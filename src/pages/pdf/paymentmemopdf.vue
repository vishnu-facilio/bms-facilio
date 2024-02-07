<template>
  <div>
    <div class="height100vh">
      <div>
        <div
          class="scrollable header-sidebar-hide paymentmemo-pdf-con"
          :class="{
            'header-sidebar-hide': $route.path === 'pdf/paymentmemopdf',
          }"
        >
          <div v-if="isLoading" class="flex-middle fc-empty-white">
            <spinner :show="isLoading" size="80"></spinner>
          </div>

          <div v-else>
            <!-- first memo start-->
            <div class="fc-etisalat-pdf-con print-break-page">
              <div class style="background: #fff;">
                <div class="flex-middle justify-content-space">
                  <!-- <div class="">
              <img
                src="~assets/customer-logo/etisalat.png"
                width="120"
                height="50"
              />
            </div>-->
                  <div>
                    <div class="f13 fc-green-lite bold line-height20">
                      Supplier Name
                    </div>
                    <div class="label-txt-black bold line-height20">
                      {{ supplier ? supplier.name : '' }}
                    </div>
                    <div class="fc-black-13 line-height20">
                      Payment Memo for {{ supplier ? supplier.name : '' }} for
                      {{
                        result.date ? getDate(result.date, 'MMM YYYY') : '---'
                      }}
                    </div>
                  </div>
                  <div class>
                    <div class="fc-black-com letter-spacing1 f36 fwBold">
                      PAYMENT MEMO
                    </div>
                    <div class="fc-green-lite f14 text-right">
                      {{ result.singleline_11 || '' }}
                    </div>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT20">
                  <div class></div>
                  <div class>
                    <div class="flex-middle justify-between">
                      <div class="fc-green-lite f13 line-height20 nowrap">
                        Memo Period
                      </div>
                      <div class="fc-black-13 line-height20 pL5 pR5">:</div>
                      <div class="fc-black-13 line-height20 pL5">
                        {{
                          result.date ? getDate(result.date, 'MMM YYYY') : '---'
                        }}
                      </div>
                    </div>
                    <div class="flex-middle justify-between">
                      <div class="fc-green-lite f13 line-height20 nowrap">
                        Memo Date
                      </div>
                      <div class="fc-black-13 line-height20 pL15 pR6">:</div>
                      <div class="fc-black-13 line-height20 pL5">
                        {{
                          result.date_1
                            ? getDate(result.date_1, 'MMM DD, YYYY')
                            : '---'
                        }}
                      </div>
                    </div>
                  </div>
                </div>

                <!-- first table -->
                <div class="fc-black-12 mT20 text-left bold">
                  Total Payable Summary
                </div>
                <div class="fc-invoice-table-cave mT10">
                  <el-row class="fc-invoice-pay-table fc-invoice-pay-table">
                    <el-col
                      :span="8"
                      v-if="result.singleline_8"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10" class="">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Vendor Number
                          </div>
                        </el-col>
                        <el-col :span="14" class="fborder-right9">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_8 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col :span="8" class="fc-invoice-table-borderBottom">
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Arrears Flag
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.boolean ? 'Yes' : 'No' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_3"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Account Code
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_3 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_9"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            TRN Number
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="text-left fc-black-13 line-height20 bold">
                            : {{ result.singleline_9 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_10"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Agreement No.
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_10 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_4"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Region Code
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_4 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Tax Code
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_1"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Contract Agreement No.
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_1 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_5"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Vendor Site Code
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 line-height20 text-left bold">
                            : {{ result.singleline_5 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col :span="8" class="fc-invoice-table-borderBottom">
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Landlord Flag
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 text-left line-height20 bold">
                            : {{ result.boolean_1 ? 'Yes' : 'No' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col
                      :span="8"
                      v-if="result.singleline_2"
                      class="fc-invoice-table-borderBottom"
                    >
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Category Code
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 text-left line-height20 bold">
                            : {{ result.singleline_2 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                    <el-col :span="8" v-if="result.singleline_6">
                      <el-row>
                        <el-col :span="10">
                          <div class="fc-green-lite f11 line-height20 nowrap">
                            Cost Center
                          </div>
                        </el-col>
                        <el-col :span="14">
                          <div class="fc-black-13 text-left line-height20 bold">
                            : {{ result.singleline_6 || '' }}
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                  </el-row>
                </div>

                <!-- table 3 -->

                <table class="fc-invoice-table3">
                  <thead>
                    <tr>
                      <th style="width: 35%;">ITEM,DESCRIPTION</th>
                      <th>Non Taxable Amount</th>
                      <th class="text-right">Taxable Amount</th>
                      <th class="text-right">VAT</th>
                      <th class="text-right">TOTAL</th>
                    </tr>
                  </thead>
                  <tbody v-for="(item, index) in result.lineItems" :key="index">
                    <tr>
                      <td>
                        <div class="bold">{{ item.name }}</div>
                        <div class="pT5 nowrap" v-if="item.number">
                          Generated from
                          {{ `${item.number}/${details.data.number}` }} Bills
                        </div>
                        <div class="pT5 nowrap" v-if="item.picklist">
                          Account code: {{ accountCode[item.picklist] | '' }}
                        </div>
                      </td>
                      <td class="text-right">
                        <div class>
                          {{
                            `${
                              item.decimal_3
                                ? formatCurrency(item.decimal_3)
                                : 0
                            } AED`
                          }}
                        </div>
                      </td>
                      <td class="text-right">
                        {{
                          `${
                            item.decimal ? formatCurrency(item.decimal) : 0
                          } AED`
                        }}
                      </td>
                      <td class="text-right">
                        {{
                          `${
                            item.decimal_1 ? formatCurrency(item.decimal_1) : 0
                          } AED`
                        }}
                      </td>
                      <td class="text-right">
                        <div class>
                          {{
                            `${
                              item.decimal_2
                                ? formatCurrency(item.decimal_2)
                                : 0
                            } AED`
                          }}
                        </div>
                      </td>
                    </tr>
                  </tbody>
                  <tbody>
                    <tr>
                      <td style="background: #f8fbf4;"></td>
                      <td style="background: #f8fbf4;">
                        <div class="fc-black-14 bold text-right nowrap">
                          {{
                            `${
                              result.decimal_3
                                ? formatCurrency(result.decimal_3)
                                : 0
                            } AED`
                          }}
                        </div>
                      </td>
                      <td style="background: #f8fbf4;">
                        <div class="flex-middle justify-content-space">
                          <div>
                            <i class="el-icon-plus fwBold"></i>
                          </div>
                          <div class="fc-black-14 bold text-right nowrap">
                            {{
                              `${
                                result.decimal
                                  ? formatCurrency(result.decimal)
                                  : 0
                              } AED`
                            }}
                          </div>
                        </div>
                      </td>
                      <td style="background: #f8fbf4;">
                        <div class="flex-middle justify-content-space">
                          <div><i class="el-icon-plus fwBold"></i></div>
                          <div class="fc-black-14 bold text-right nowrap">
                            {{
                              `${
                                result.decimal_1
                                  ? formatCurrency(result.decimal_1)
                                  : 0
                              } AED`
                            }}
                          </div>
                        </div>
                      </td>
                      <td style="background: #f8fbf4;" class="p20">
                        <div class="flex-middle justify-content-space">
                          <div class="equal-to">=</div>
                          <div class="fc-black-14 bold text-right nowrap">
                            {{
                              `${
                                result.decimal_2
                                  ? formatCurrency(result.decimal_2)
                                  : 0
                              } AED`
                            }}
                          </div>
                        </div>
                      </td>
                      <!-- <td colspan="100%" style="background: #f8fbf4;">
                  <div class="fc-black-14 bold text-right">
                    {{
                      `${
                        result.decimal_3 ? formatCurrency(result.decimal_3) : 0
                      } AED + ${
                        result.decimal ? formatCurrency(result.decimal) : 0
                      } AED + ${
                        result.decimal_1 ? formatCurrency(result.decimal_1) : 0
                      } AED =  ${
                        result.decimal_2 ? formatCurrency(result.decimal_2) : 0
                      } AED`
                    }}
                  </div>
                </td> -->
                    </tr>
                    <tr>
                      <td colspan="100%">
                        <div
                          class="fc-black2-18 text-right fwBold flex-middle justify-end"
                        >
                          <div class="pR35">PAYMENT MEMO TOTAL</div>
                          <div class="nowrap">
                            {{
                              `${
                                result.decimal_2
                                  ? formatCurrency(result.decimal_2)
                                  : 0
                              } AED`
                            }}
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>

                <!-- third -->
                <template
                  v-if="result.approvalList && result.approvalList.length"
                >
                  <div class="fc-black-12 mT20 text-left fw6">
                    Approval Summary
                  </div>
                  <table class="fc-invoice-table4 mT15">
                    <thead>
                      <th>NAME</th>
                      <th>DESIGNATION</th>
                      <th>DATE OF APPROVAL</th>
                      <th>STATUS</th>
                      <th width="30%">COMMENTS</th>
                    </thead>
                    <tbody
                      v-for="(approval, index) in result.approvalList"
                      :key="index"
                    >
                      <tr>
                        <td class="bold">{{ approval.name || '' }}</td>
                        <td>{{ approval.singleline || '' }}</td>
                        <td>
                          {{
                            approval.datetime
                              ? getDate(approval.datetime, 'DD MMM YYYY h:mm a')
                              : '---'
                          }}
                        </td>
                        <td>
                          <div class="fc-green-label3">
                            {{
                              approval.picklist
                                ? approvalStatus[approval.picklist]
                                : ''
                            }}
                          </div>
                        </td>
                        <td>{{ approval.multiline || '' }}</td>
                      </tr>
                    </tbody>
                  </table>
                </template>
              </div>
            </div>
            <!-- first memo end -->
            <!-- second memo start -->
            <div
              style="background: #fff;margin-top: 100px;"
              class="fc-etisalat-pdf-con print-break-page"
            >
              <div class style="background: #fff;">
                <div class="flex-middle justify-content-space">
                  <!-- <div class="">
              <img
                src="~assets/customer-logo/etisalat.png"
                width="120"
                height="50"
              />
            </div>-->
                  <div>
                    <div class="fc-green-lite f13 bold line-height20">
                      Supplier Name
                    </div>
                    <div class="label-txt-black bold line-height20">
                      {{ supplier ? supplier.name : '' }}
                    </div>
                    <div class="fc-black-13 line-height20">
                      Payment Memo for {{ supplier ? supplier.name : '' }} for
                      {{
                        result.date ? getDate(result.date, 'MMM YYYY') : '---'
                      }}
                    </div>
                  </div>
                  <div class>
                    <div class="fc-black-com letter-spacing1 f36 fwBold">
                      PAYMENT MEMO
                    </div>
                    <div class="fc-green-lite text-right">
                      {{ result.singleline_11 || '' }}
                    </div>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT20">
                  <div class></div>
                  <div class>
                    <div class="flex-middle justify-between">
                      <div class="fc-green-lite f13 line-height20">
                        Memo Period
                      </div>
                      <div class="fc-black-13 line-height20 pL5 pR5">:</div>
                      <div class="fc-black-13 line-height20 pL5">
                        {{
                          result.date ? getDate(result.date, 'MMM YYYY') : '---'
                        }}
                      </div>
                    </div>
                    <div class="flex-middle justify-between">
                      <div class="fc-green-lite f13 line-height20">
                        Memo Date
                      </div>
                      <div class="fc-black-13 line-height20 pL15 pR6">:</div>
                      <div class="fc-black-13 line-height20 pL5">
                        {{
                          result.date_1
                            ? getDate(result.date_1, 'MMM DD, YYYY')
                            : '---'
                        }}
                      </div>
                    </div>
                  </div>
                </div>

                <!-- third table -->
                <div class="fc-black-12 mT20 text-left fw6">
                  Break up by Cost Centre
                </div>
                <table class="fc-invoice-table3">
                  <thead>
                    <tr>
                      <th style="width: 30%;">ITEM</th>
                      <th style="width: 250px;" class="text-right">
                        Non Taxable Amount
                      </th>
                      <th class="text-right" style="width: 200px;">
                        Taxable Amount
                      </th>
                      <th class="text-right">VAT</th>
                      <th class="text-right">TOTAL</th>
                    </tr>
                  </thead>
                  <tbody>
                    <template v-for="(value, key) in result.adminLineItems">
                      <tr :key="key" v-if="key !== 'total'">
                        <td>
                          <div class="bold">{{ utility[key] }}</div>
                        </td>
                        <td class="text-right">
                          {{
                            `${
                              value.decimal_3
                                ? formatCurrency(value.decimal_3)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal ? formatCurrency(value.decimal) : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal_1
                                ? formatCurrency(value.decimal_1)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal_2
                                ? formatCurrency(value.decimal_2)
                                : 0
                            } AED`
                          }}
                        </td>
                      </tr>
                      <tr :key="key" v-else>
                        <td class="bold" style="background-color: #f8fbf4">
                          ADMIN TOTAL (A)
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right bold"
                        >
                          {{
                            `${
                              value.decimal_3
                                ? formatCurrency(value.decimal_3)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div><i class="el-icon-plus fwBold pR10"></i></div>
                            <div>
                              {{
                                `${
                                  value.decimal
                                    ? formatCurrency(value.decimal)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div><i class="el-icon-plus fwBold pR10"></i></div>
                            <div>
                              {{
                                `${
                                  value.decimal_1
                                    ? formatCurrency(value.decimal_1)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div class="pR10">=</div>
                            <div>
                              {{
                                `${
                                  value.decimal_2
                                    ? formatCurrency(value.decimal_2)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                      </tr>
                    </template>
                    <template v-for="(value, key) in result.techLineItems">
                      <tr :key="key" v-if="key !== 'total'">
                        <td>
                          <div class="bold">{{ utility[key] }}</div>
                        </td>
                        <td class="text-right">
                          {{
                            `${
                              value.decimal_3
                                ? formatCurrency(value.decimal_3)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal ? formatCurrency(value.decimal) : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal_1
                                ? formatCurrency(value.decimal_1)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td class="text-right nowrap">
                          {{
                            `${
                              value.decimal_2
                                ? formatCurrency(value.decimal_2)
                                : 0
                            } AED`
                          }}
                        </td>
                      </tr>
                      <tr :key="key" v-else>
                        <td
                          class="bold uppercase"
                          style="background-color: #f8fbf4"
                        >
                          Engineering TOTAL (B)
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right bold"
                        >
                          {{
                            `${
                              value.decimal_3
                                ? formatCurrency(value.decimal_3)
                                : 0
                            } AED`
                          }}
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div><i class="el-icon-plus fwBold pR10"></i></div>
                            <div>
                              {{
                                `${
                                  value.decimal
                                    ? formatCurrency(value.decimal)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div><i class="el-icon-plus fwBold pR10"></i></div>
                            <div>
                              {{
                                `${
                                  value.decimal_1
                                    ? formatCurrency(value.decimal_1)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                        <td
                          style="background-color: #f8fbf4"
                          class="text-right nowrap bold"
                        >
                          <div class="flex-middle justify-content-space">
                            <div class="pR10">=</div>
                            <div>
                              {{
                                `${
                                  value.decimal_2
                                    ? formatCurrency(value.decimal_2)
                                    : 0
                                } AED`
                              }}
                            </div>
                          </div>
                        </td>
                      </tr>
                    </template>
                    <tr>
                      <td colspan="100%">
                        <div class="fc-black2-18 text-right nowrap fwBold">
                          <span class="pR20">PAYMENT MEMO TOTAL (A+B)</span>
                          {{
                            `${
                              result.decimal_2
                                ? formatCurrency(result.decimal_2)
                                : 0
                            } AED`
                          }}
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <!-- second memo end -->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      isLoading: true,
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      result: {},
      supplier: null,
      fieldObject: null,
      detail: null,
      utility: {
        1: 'Electricity',
        2: 'Water',
        3: 'Gas',
        4: 'Sewage',
        7: 'Arrears',
      },
      approvalStatus: {
        1: 'Approved',
        2: 'Rejected',
      },
      accountCode: {
        1: '62001',
        2: '62003',
        3: '',
        4: '62004',
      },
      params: {
        nameSpace: 'invoiceSummary',
        functionName: 'getInvoiceSummary',
        paramList: [],
      },
    }
  },
  mounted() {
    this.getSummaryData()
    this.getFields()
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    id() {
      return this.$route.query.paymentId
        ? parseInt(this.$route.query.paymentId)
        : ''
    },
    data() {
      return this.details.data || null
    },
    details() {
      if (this.detail) {
        return this.detail
      }
      return null
    },
    fieldDisplayNames() {
      if (this.moduleMeta && this.moduleMeta.fields) {
        let fieldMap = {}
        this.moduleMeta.fields.forEach(field => {
          this.$set(fieldMap, field.name, field.displayName)
        })
        return fieldMap
      }
      return []
    },
  },
  methods: {
    formatCurrency(value) {
      return formatCurrency(value, 2)
    },
    getDate(time, formatter) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format(formatter)
    },
    getSummaryData() {
      let { id } = this
      this.$http
        .get(`/v2/module/data/${id}?moduleName=custom_invoices`)
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleData
          ) {
            this.detail = response.data.result.moduleData
          }
          this.getData()
        })
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=custom_invoices')
        .then(response => {
          if (response.data.meta) {
            this.fieldObject = response.data.meta
          }
        })
    },
    getData() {
      let invoiceid = this.id
      this.params.paramList.push(invoiceid)
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = resp.data.result.workflow.returnValue
          this.getsupplierId()
        }
        this.isLoading = false
      })
    },
    getsupplierId() {
      let { lookup } = this.result
      if (lookup && lookup.id) {
        this.getsupplier(lookup.id)
      }
    },
    getsupplier(id) {
      this.$http.get(`/v2/pages/vendors?id=${id}`).then(resp => {
        if (resp.data.result && resp.data.result.record) {
          this.supplier = resp.data.result.record
        }
      })
    },
  },
}
</script>
<style lang="scss">
.paymentmemo-print-con {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}

@media print {
  body {
    overflow: scroll;
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .f36 {
    font-size: 28px !important;
  }

  .fc-invoice-table3 th {
    font-size: 11px !important;
  }

  .fc-invoice-table3 td {
    font-size: 12px !important;
    padding: 16px 10px 16px 10px !important;
  }

  .fc-black-14 {
    font-size: 13px !important;
  }

  .fc-black2-18 {
    font-size: 14px !important;
  }

  .mT20 {
    margin-top: 0 !important;
  }

  .pT20 {
    padding-top: 0 !important;
  }

  .fc-invoice-table3 th {
    padding: 10px !important;
  }
  .fc-invoice-table3 {
    border-left: none !important;
    border-right: none !important;
  }

  .el-icon-plus,
  .equal-to {
    padding-left: 10px !important;
    padding-right: 10px !important;
  }

  .fc-etisalat-pdf-con {
    max-width: 1000px;
    width: 100%;
    padding: 0 30px;
    margin-left: auto;
    height: 100%;
    margin-right: auto;
    position: relative;
    overflow: visible;
    display: flex;
    flex-direction: column;
    margin-top: 0 !important;

    .fc-green-lite {
      font-size: 10px !important;
    }

    .fc-black-13 {
      font-size: 11px !important;
    }

    .fc-invoice-table3 th,
    .fc-invoice-table3 td {
      font-size: 11px !important;
    }

    .fc-black-14 {
      font-size: 12px !important;
    }

    .fc-black2-18 {
      font-size: 14px !important;
    }

    .f36 {
      font-size: 26px !important;
    }
  }

  .print-break-page {
    height: 100%;
    overflow-y: scroll;
    display: block;
    page-break-after: always;
  }

  @page {
    orphans: 4;
    widows: 2;
    // margin-bottom: 10px;
    margin-top: 28px;
    z-index: 900;
  }

  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .normal main {
    height: 100vh !important;
    background: #fff;
    min-height: 100vh !important;
  }

  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  .header-sidebar-hide {
    width: 100%;
    height: 100%;
    position: inherit;
    top: 0;
    left: 0;
    right: 0;
    page-break-after: always;
    page-break-inside: avoid;
  }

  .layout-page .pL60 {
    padding-left: 0 !important;
    position: inherit !important;
    height: initial !important;
  }
}
</style>
