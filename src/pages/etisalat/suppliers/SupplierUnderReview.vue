<template>
  <div>
    <div class="p30 flex-center-vH">
      <el-row class="pT10">
        <el-col :span="12" class="border-right3">
          <div class="fc-black3-16 bold">
            {{ $t('etisalat.payments.under_review') }}
          </div>
          <div class="fc-black3-16 f22 pT10">
            {{ `${formatCurrency(result.underReviewSum) || 0} AED` }}
          </div>
          <div class="fc-grey6-13">{{ result.underReviewCount }} bills</div>
        </el-col>
        <el-col :span="12" class="pL30">
          <div class="fc-black3-16 bold">
            {{ $t('etisalat.common.cancelled') }}
          </div>
          <div class="fc-black3-16 f22 pT10">
            {{ `${formatCurrency(result.canceledSum) || 0} AED` }}
          </div>
          <div class="fc-grey6-13">{{ result.canceledCount }} bills</div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      result: {
        underReviewSum: 0,
        canceledSum: 0,
        outstandingSum: 0,
        canceledCount: 0,
        outStandingCount: 0,
        underReviewCount: 0,
      },
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
    getDate(time) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format('DD MMM YYYY')
    },
    getData() {
      let supplierrid = this.details.id
      this.params.paramList.push(supplierrid)
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = resp.data.result.workflow.returnValue
        }
      })
    },
  },
}
</script>
