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
              <div class="fc-green-lite f13 bold line-height20">
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
              <div class="fc-green-lite text-right">
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
                <th class="text-right" style="width: 200px;">Taxable Amount</th>
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
                        value.decimal_3 ? formatCurrency(value.decimal_3) : 0
                      } AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${value.decimal ? formatCurrency(value.decimal) : 0} AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${
                        value.decimal_1 ? formatCurrency(value.decimal_1) : 0
                      } AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${
                        value.decimal_2 ? formatCurrency(value.decimal_2) : 0
                      } AED`
                    }}
                  </td>
                </tr>
                <tr :key="key" v-else>
                  <td class="bold" style="background-color: #f8fbf4">
                    ADMIN TOTAL (A)
                  </td>
                  <td style="background-color: #f8fbf4" class="text-right bold">
                    {{
                      `${
                        value.decimal_3 ? formatCurrency(value.decimal_3) : 0
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
                            value.decimal ? formatCurrency(value.decimal) : 0
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
                        value.decimal_3 ? formatCurrency(value.decimal_3) : 0
                      } AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${value.decimal ? formatCurrency(value.decimal) : 0} AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${
                        value.decimal_1 ? formatCurrency(value.decimal_1) : 0
                      } AED`
                    }}
                  </td>
                  <td class="text-right nowrap">
                    {{
                      `${
                        value.decimal_2 ? formatCurrency(value.decimal_2) : 0
                      } AED`
                    }}
                  </td>
                </tr>
                <tr :key="key" v-else>
                  <td class="bold uppercase" style="background-color: #f8fbf4">
                    Engineering TOTAL (B)
                  </td>
                  <td style="background-color: #f8fbf4" class="text-right bold">
                    {{
                      `${
                        value.decimal_3 ? formatCurrency(value.decimal_3) : 0
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
                            value.decimal ? formatCurrency(value.decimal) : 0
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
                        result.decimal_2 ? formatCurrency(result.decimal_2) : 0
                      } AED`
                    }}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
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
        7: 'Arrears',
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
