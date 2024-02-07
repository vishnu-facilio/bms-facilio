<template>
  <div>
    <div>
      <div class>
        <spinner :show="loading" size="80" v-if="loading"></spinner>
        <div
          ref="preview-container"
          v-if="$validation.isEmpty(result) && !loading"
          class="fc-empty-center height400"
        >
          <inline-svg
            src="svgs/emptystate/history"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Transactions available
          </div>
        </div>
        <div v-if="!loading && !$validation.isEmpty(result)">
          <table class="width100 fc-suppier-payment-table">
            <thead>
              <tr>
                <th style="width: 11%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">Payment Memo NO</div>
                </th>
                <th style="width: 30%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    Payment Memo Value
                  </div>
                </th>
                <th style="width: 30%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    Payment Memo DETAILS
                  </div>
                </th>
                <th style="width: 10%;" class="pL15 pR15" v-if="!isLandloard">
                  <div class="fc-black-11 text-uppercase">ARREAR</div>
                </th>
                <th style="width: 20%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">DOCUMENTS</div>
                </th>
              </tr>
            </thead>
            <tbody v-for="(value, index) in result" :key="index">
              <tr class="border-bottom19">
                <td style="width: 11%;">
                  <div class="bold">
                    {{ getDate(value.time, 'MMM YYYY', true) }}
                  </div>
                </td>
                <td style="width: 30%;"></td>
                <td style="width: 30%;"></td>
                <td style="width: 10%;"></td>
                <td style="width: 20%;"></td>
              </tr>
              <tr
                class="border-bottom19"
                v-for="(invoice, index) in value.value['invoices']"
                :key="index"
              >
                <td style="width: 11%;" class="border-right10">
                  <div class="fc-blue-txt3-13">
                    {{ invoice['singleline_11'] || '' }}
                  </div>
                </td>
                <td style="width: 30%;" class="border-right10">
                  <div class="fc-black-13 text-left bold">
                    {{
                      invoice.decimal_2
                        ? `${formatCurrency(invoice.decimal_2)} AED`
                        : '0 AED'
                    }}
                  </div>
                  <div class="fc-greey9-12 f13 text-left pT5">
                    <span class="fc-greey9-12 f13">{{
                      invoice.number ? `From ${invoice.number} Bills` : ``
                    }}</span>
                  </div>
                  <!-- <div
                    class="fc-black-13 text-left bold pT15"
                  >VAT Amount {{ formatCurrency(invoice.decimal_1) || '0' }} AED</div> -->
                </td>
                <td style="width: 30%;" class="border-right10">
                  <div class="fc-black-13 text-left">
                    Payment Memo Date :
                    {{ getDate(invoice.date_1, 'DD MMM YYYY') }}
                  </div>
                  <div class="fc-black-13 text-left pT10">
                    Region code : {{ invoice.singleline_4 || '' }}
                  </div>
                  <div class="fc-black-13 text-left pT10">
                    Cost Center :{{ invoice.singleline_6 || '' }}
                  </div>
                </td>
                <td
                  style="width: 10%;"
                  class="border-right10 text-center"
                  v-if="!isLandloard"
                >
                  {{ invoice.boolean ? 'Yes' : 'No' }}
                </td>
                <td style="width: 20%;">
                  <div
                    class="fc-blue-txt3-13 pointer"
                    @click="getSummarySheet(invoice, value.value.time)"
                  >
                    Summary Sheet
                  </div>
                  <div
                    class="fc-blue-txt3-13 pT10 pointer"
                    @click="goToInvoice(invoice)"
                  >
                    View Payment Memo
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import Spinner from '@/Spinner'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  data() {
    return {
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      exportDownloadUrl: null,
      loading: false,
      result: [],
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getTransaction',
        paramList: [],
      },
    }
  },
  mounted() {
    this.getData()
  },
  components: {
    Spinner,
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
    isLandloard() {
      if (
        this.details &&
        this.details.data['picklist'] &&
        this.details.data.picklist === 2
      ) {
        return true
      }
      return false
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['preview-container'].scrollHeight
        let width = this.$refs['preview-container'].scrollWidth
        let { h } = this.calculateDimensions({
          height,
          width,
        })
        this.resizeWidget({
          h: h,
        })
      })
    },
    formatCurrency(value) {
      return formatCurrency(value, 2)
    },
    goToInvoice(invoice) {
      let routeData

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('custom_invoices', pageTypes.OVERVIEW) || {}

        if (name) {
          routeData = this.$router.resolve({
            name,
            params: { viewname: 'all', id: invoice.id },
          })
        }
      } else {
        routeData = this.$router.resolve({
          path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
        })
      }

      routeData && window.open(routeData.href, '_blank')
      // this.$router.push({
      //   path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
      // })
    },
    getSummarySheet(invoice, timeStamp) {
      let paymentMemoId = invoice.id
      let { details } = this
      let params = null
      this.allArrearsTableData = []
      this.params.paramList.push(paymentMemoId)
      let supliervalue = []
      supliervalue.push(paymentMemoId + '')
      let endTime = moment(Number(timeStamp))
        .tz(this.$timezone)
        .endOf('month')
        .valueOf()
      let startTime = moment(Number(timeStamp))
        .tz(this.$timezone)
        .startOf('month')
        .valueOf()
      let time = []
      time.push(startTime + '')
      time.push(endTime + '')
      let filter = {
        date_5: {
          operatorId: 20,
          value: time,
        },
        invoice: {
          operatorId: 36,
          value: supliervalue,
          selectedLabel: invoice.name,
        },
      }
      let url = `/exportModule?type=2&moduleName=custom_utilitybills&specialFields=false&viewName=all&includeParentFilter=true`
      this.$message({
        showClose: true,
        message: 'Downloading...',
        type: 'success',
      })
      if (filter) {
        params = '&filters=' + JSON.stringify(filter)
      }
      this.$http.post(url, params).then(response => {
        if (response.data && typeof response.data === 'object') {
          this.exportDownloadUrl = response.data.fileUrl
        } else {
          this.$error.message('Export failed')
        }
      })
    },
    getDate(time, formatter, uppercase) {
      if (uppercase) {
        return moment(Number(time))
          .tz(this.$timezone)
          .format(formatter)
          .toUpperCase()
      } else {
        return moment(Number(time))
          .tz(this.$timezone)
          .format(formatter)
      }
    },
    getData() {
      this.loading = true
      let supplierrid = this.details.id
      this.params.paramList.push(supplierrid)
      this.params.paramList.push(1577822400000)
      this.params.paramList.push(1609444799999)
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        this.loading = false
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = this.sortResult(resp.data.result.workflow.returnValue)
        }
        this.autoResize()
      })
    },
    sortResult(data) {
      if (data) {
        let timeStamp = Object.keys(data)
        let sortedData = []
        timeStamp.forEach(rt => {
          let d = {
            time: Number(rt),
            value: data[rt],
          }
          sortedData.push(d)
        })
        return sortedData.sort((a, b) => a.time - b.time)
      }
      return data
    },
  },
}
</script>
