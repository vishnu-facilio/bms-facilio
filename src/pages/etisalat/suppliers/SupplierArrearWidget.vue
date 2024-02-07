<template>
  <div>
    <div class="p30 flex-center-vH">
      <el-row class="pT10">
        <el-col :span="14">
          <div class="fc-black3-16 bold">
            {{ $t('etisalat.payments.outstanding_amount') }}
          </div>
          <div class="fc-black3-16 f22 pT10">
            {{ `${formatCurrency(result.outstandingSum) || 0} AED` }}
          </div>
          <div class="fc-grey6-13">{{ result.outStandingCount }} bills</div>
        </el-col>
        <el-col :span="10" v-if="result.outStandingCount">
          <el-button
            class="fc-border-btn-green bR5 mT34 f8 fwBold"
            @click="createMemo(result)"
            >{{ $t('etisalat.etisalat.create_invoice') }}</el-button
          >
        </el-col>
      </el-row>
    </div>
    <el-dialog
      title="Create payment Memo"
      :visible.sync="dialogVisible"
      width="35%"
      :show-close="false"
      :before-close="handleClose"
      class="fc-dialog-center-container"
    >
      <div class="postion-relative">
        <div class="height200" v-if="createPMmeta.buttonInfo">
          <div class="pB5">
            {{ `Supplier name - ${createPMmeta.data.supplier}` }}
          </div>
          <div class="pB5">
            {{ `Total Arrear Amount - ${createPMmeta.data.cost}` }}
          </div>

          <div class="pB5">
            {{ `No of bills - ${createPMmeta.data.count}` }}
          </div>
        </div>
        <div class="height200" v-else>
          <div
            class="pB5"
            v-if="createPMmeta.data.invoice.id"
            @click="goToInvoice(createPMmeta.data.invoice)"
          >
            {{
              `Payment memo #$${createPMmeta.data.invoice.id} created successfully`
            }}
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            v-if="createPMmeta.buttonInfo"
            :disabled="saving"
            @click="closeDialog()"
            class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button v-else @click="getData()" class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button
            v-if="createPMmeta.buttonInfo"
            type="primary"
            @click="createInvoice"
            :loading="saving"
            class="modal-btn-save "
          >
            {{
              saving ? 'Creating payment Memo...' : 'Create payment Memo'
            }}</el-button
          >
          <el-button
            v-else
            type="primary"
            @click="getData()"
            class="modal-btn-save "
          >
            {{ 'OK' }}</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import axios from 'axios'
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      dialogVisible: false,
      result: {
        underReviewSum: 0,
        canceledSum: 0,
        outstandingSum: 0,
        canceledCount: 0,
        outStandingCount: 0,
        underReviewCount: 0,
      },
      createPMmeta: {
        info: '',
        buttonInfo: true,
        data: {},
      },
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getArrearSummaryCount',
        paramList: [],
      },
      saving: false,
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
      this.params.paramList = []
      this.params.paramList.push(supplierrid)
      this.result = {
        underReviewSum: 0,
        canceledSum: 0,
        outstandingSum: 0,
        canceledCount: 0,
        outStandingCount: 0,
        underReviewCount: 0,
      }
      this.closeDialog()
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
    isResponse(response) {
      if (response && response.data && response.data.responseCode === 0) {
        return true
      }
      return false
    },
    crossOrginReuest() {
      const xhr = new XMLHttpRequest()
      const url = '/etisalat/api/generateArrearInvoiceForOutStandingAmount'
      xhr.setRequestHeader('Content-Type', 'application/json')
      xhr.open('POST', url, true)
      xhr.onreadystatechange = someHandler
      xhr.send(
        JSON.stringify({
          supplierId: this.details.id,
        })
      )
    },
    createInvoice() {
      this.saving = true
      let url = `${window.location.origin}/etisalat/api/generateArrearInvoiceForOutStandingAmount`
      axios
        .post(url, {
          supplierId: this.details.id,
        })
        .then(response => {
          this.saving = false
          this.createPMmeta.data = {}
          this.$set(
            this.createPMmeta.data,
            'invoice',
            this.getInvoiceData(response.data)
          )
          this.createPMmeta.info = ''
          this.createPMmeta.buttonInfo = false
        })
        .catch(error => {
          this.saving = false
          this.createPMmeta.buttonInfo = false
        })
    },
    getInvoiceData(data) {
      if (data.result && data.result.invoices && data.result.invoices.length) {
        return data.result.invoices[0]
      }
      return null
    },
    createMemo(bill) {
      let totalAmount = bill.outstandingSum
      let billCount = bill.outStandingCount
      this.dialogVisible = true
      this.$set(this.createPMmeta.data, 'supplier', this.details.name)
      this.$set(
        this.createPMmeta.data,
        'cost',
        `${this.formatCurrency(totalAmount) || 0} AED`
      )
      this.$set(this.createPMmeta.data, 'count', `${billCount || 0}`)

      this.createPMmeta.info = ''
    },
    handleClose() {
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
      this.createPMmeta.buttonInfo = true
    },
  },
}
</script>
