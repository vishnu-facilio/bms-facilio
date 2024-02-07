<template>
  <div>
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="isLoading" :size="80"></spinner>
    </div>
    <div v-else ref="preview-container">
      <div class="fc-etisalat-pdf-con">
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
                {{ result.date ? getDate(result.date, 'MMM YYYY') : '---' }}
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
                <div class="fc-green-lite f13 line-height20">Memo Period</div>
                <div class="fc-black-13 line-height20 pL5 pR5">:</div>
                <div class="fc-black-13 line-height20 pL5">
                  {{ result.date ? getDate(result.date, 'MMM YYYY') : '---' }}
                </div>
              </div>
              <div class="flex-middle justify-between">
                <div class="fc-green-lite f13 line-height20">Memo Date</div>
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
                  <el-col :span="13" class="">
                    <div class="fc-green-lite f11 line-height20">
                      Vendor Number
                    </div>
                  </el-col>
                  <el-col :span="11" class="fborder-right9">
                    <div class="fc-black-13 line-height20 text-left bold">
                      : {{ result.singleline_8 || '' }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8" class="fc-invoice-table-borderBottom">
                <el-row>
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Arrears Flag
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Account Code
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      TRN Number
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Agreement No.
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Region Code
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">Tax Code</div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Contract Agreement No.
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Vendor Site Code
                    </div>
                  </el-col>
                  <el-col :span="11">
                    <div class="fc-black-13 line-height20 text-left bold">
                      : {{ result.singleline_5 || '' }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8" class="fc-invoice-table-borderBottom">
                <el-row>
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Landlord Flag
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Category Code
                    </div>
                  </el-col>
                  <el-col :span="11">
                    <div class="fc-black-13 text-left line-height20 bold">
                      : {{ result.singleline_2 || '' }}
                    </div>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="8" v-if="result.singleline_6">
                <el-row>
                  <el-col :span="13">
                    <div class="fc-green-lite f11 line-height20">
                      Cost Center
                    </div>
                  </el-col>
                  <el-col :span="11">
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
                <th class="text-right" style="width: 25%;">VAT</th>
                <th class="text-right" style="width: 25%;">TOTAL</th>
              </tr>
            </thead>
            <tbody v-for="(item, index) in result.lineItems" :key="index">
              <tr>
                <td>
                  <div class="bold">{{ item.name }}</div>
                  <div class="pT5" v-if="item.number">
                    Generated from
                    {{ `${item.number}/${details.data.number}` }} Bills
                  </div>
                  <div class="pT5" v-if="item.picklist">
                    Account code: {{ accountCode[item.picklist] | '' }}
                  </div>
                </td>
                <td class="text-right">
                  <div class>
                    {{
                      `${
                        item.decimal_3 ? formatCurrency(item.decimal_3) : 0
                      } AED`
                    }}
                  </div>
                </td>
                <td class="text-right">
                  {{ `${item.decimal ? formatCurrency(item.decimal) : 0} AED` }}
                </td>
                <td class="text-right">
                  {{
                    `${item.decimal_1 ? formatCurrency(item.decimal_1) : 0} AED`
                  }}
                </td>
                <td class="text-right">
                  <div class>
                    {{
                      `${
                        item.decimal_2 ? formatCurrency(item.decimal_2) : 0
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
                  <div class="fc-black-14 bold text-right">
                    {{
                      `${
                        result.decimal_3 ? formatCurrency(result.decimal_3) : 0
                      } AED`
                    }}
                  </div>
                </td>
                <td style="background: #f8fbf4;">
                  <div class="flex-middle justify-content-space">
                    <div>
                      <i class="el-icon-plus fwBold"></i>
                    </div>
                    <div class="fc-black-14 bold text-right">
                      {{
                        `${
                          result.decimal ? formatCurrency(result.decimal) : 0
                        } AED`
                      }}
                    </div>
                  </div>
                </td>
                <td style="background: #f8fbf4;">
                  <div class="flex-middle justify-content-space">
                    <div><i class="el-icon-plus fwBold"></i></div>
                    <div class="fc-black-14 bold text-right">
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
                    <div>=</div>
                    <div class="fc-black-14 bold text-right">
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
                    <div>
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
          <template v-if="result.approvalList && result.approvalList.length">
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
      <!-- invoice sub -->
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  data() {
    return {
      isLoading: true,
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      result: {},
      supplier: null,
      utility: {
        1: 'Electricity',
        2: 'Water',
        3: 'Gas',
        4: 'Sewage',
      },
      accountCode: {
        1: '62001',
        2: '62003',
        3: '',
        4: '62004',
      },
      approvalStatus: {
        1: 'Approved',
        2: 'Rejected',
      },
      params: {
        nameSpace: 'invoiceSummary',
        functionName: 'getInvoiceSummary',
        paramList: [],
      },
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
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
    goToInvoice(invoice) {
      let routeData = this.$router.resolve({
        path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
      })
      window.open(routeData.href, '_blank')
      // this.$router.push({
      //   path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
      // })
    },
    getDate(time, formatter) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format(formatter)
    },
    getData() {
      let invoiceid = this.details.id
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
        this.autoResize()
      })
    },
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['preview-container'].scrollHeight + 60
        let width = this.$refs['preview-container'].scrollWidth
        let { h } = this.calculateDimensions({ height, width })
        if (isEmpty(this.initialWidgetHeight)) {
          this.initialWidgetHeight = h
        }
        this.resizeWidget({
          h: this.isAllVisible ? h : this.initialWidgetHeight,
        })
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
