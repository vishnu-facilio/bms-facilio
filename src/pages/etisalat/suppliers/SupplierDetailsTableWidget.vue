<template>
  <div ref="preview-container">
    <div class>
      <div v-if="loading" class="fc-grid-center height400">
        <spinner :show="loading" size="80" v-if="loading"></spinner>
      </div>
      <div v-else>
        <div v-if="!invoices.length" class="fc-empty-center height400">
          <inline-svg
            src="svgs/emptystate/history"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Payment details
          </div>
        </div>

        <table v-else class="supplier-cancel-table">
          <thead>
            <th class="width25">Payment Memo Details</th>
            <th class="width25">Payment Memo Value</th>
            <th class="width25">STATUS</th>
            <th class="width25">ACTIONS</th>
          </thead>
          <tbody>
            <tr v-for="(invoice, index) in invoices" :key="index">
              <td class="width25 bold">
                <div class="fc-blue-txt3-13">
                  {{ invoice.data['singleline_7'] || '' }}
                </div>
                <div class="fw4 pT10">
                  Payment Memo Date:
                  {{
                    invoice.data.date_1
                      ? getDate(invoice.data.date_1, 'DD MMM YYYY')
                      : '---'
                  }}
                </div>
                <div class="fw4 pT10">
                  Region code : {{ invoice.data.singleline_4 || '' }}
                </div>
                <div class="fw4 pT10">
                  Cost Center :{{ invoice.data.singleline_6 || '' }}
                </div>
              </td>
              <td class="width25">
                <div class="bold">
                  {{
                    invoice.data.decimal_2
                      ? formatCurrency(invoice.data.decimal_2)
                      : '0'
                  }}
                  AED
                </div>
                <div class="fc-greey9-12 pT5">
                  {{
                    invoice.data.number
                      ? `From ${invoice.data.number} Bills`
                      : ``
                  }}
                </div>
              </td>
              <td class="width25">
                <div class="fc-orange">
                  {{
                    invoice.moduleState && invoice.moduleState.displayName
                      ? invoice.moduleState.displayName
                      : ''
                  }}
                </div>
              </td>
              <td class="width25">
                <div
                  class="fc-blue-txt3-13 pointer"
                  @click="goToInvoice(invoice)"
                >
                  Show Payment Memo
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  data() {
    return {
      pdfopen: false,
      loading: false,
      currentPage: 0,
      pageCount: 0,
      page: {
        from: 1,
        to: 100,
      },
      invoices: [],
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getArrearSummaryCount',
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
    autoResize() {
      this.$nextTick(() => {
        let height = this.$refs['preview-container'].scrollHeight
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
    },
    getDate(time) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format('DD MMM YYYY')
    },
    getData() {
      let supplierrid = this.details.id
      let { page, details } = this
      this.params.paramList.push(supplierrid)
      this.loading = true
      let url = `/v2/module/data/list?moduleName=custom_invoices&page=${page.from}&perPage=${page.to}&filters=%7B%22boolean%22%3A%7B%22operatorId%22%3A15%2C%22value%22%3A%5B%22true%22%5D%7D%2C%22lookup%22%3A%7B%22operatorId%22%3A36%2C%22value%22%3A%5B%22${supplierrid}%22%5D%2C%22selectedLabel%22%3A%22${details.name}%22%7D%7D&viewName=all&includeParentFilter=tru`
      this.$http.get(url).then(resp => {
        console.log('deta')
        if (resp.data.responseCode === 0) {
          this.invoices =
            resp.data.result && resp.data.result.moduleDatas
              ? resp.data.result.moduleDatas
              : []
        }
        this.loading = false
        this.autoResize()
      })
    },
  },
}
</script>
