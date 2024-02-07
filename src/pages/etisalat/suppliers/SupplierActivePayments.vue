<template>
  <div>
    <div>
      <div class>
        <div v-if="isLoading" class="text-center fc-empty-center height70vh">
          <spinner :show="true" :size="80"></spinner>
        </div>
        <div v-else-if="result.length === 0" class="fc-empty-center height70vh">
          <inline-svg
            src="svgs/emptystate/history"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Active Payments available
          </div>
        </div>
        <div class="pB20" ref="preview-container" v-else>
          <table class="width100 fc-suppier-payment-table">
            <thead>
              <tr>
                <th style="width: 30%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    {{ $t('etisalat.payments.invoice_details') }}
                  </div>
                </th>
                <th style="width: 30%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    {{
                      isLandloard
                        ? 'TOTAL'
                        : $t('etisalat.payments.dispute_free')
                    }}
                  </div>
                </th>
                <th style="width: 20%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    {{ $t('etisalat.common.status') }}
                  </div>
                </th>
                <th style="width: 20%;" class="pL15 pR15">
                  <div class="fc-black-11 text-uppercase">
                    {{ $t('etisalat.common.actions') }}
                  </div>
                </th>
              </tr>
            </thead>
            <tbody v-for="(value, index) in result" :key="index">
              <tr
                class="border-bottom19"
                v-if="
                  showHeader(value.value['counts']) ||
                    (value.value['invoices'] && value.value['invoices'].length)
                "
              >
                <td style="width: 30%;">
                  <el-row>
                    <el-col :span="10">
                      <div class="fc-black-13 text-left bold">
                        {{ getDate(value.time, 'MMM YYYY', true) }}
                      </div>
                    </el-col>
                    <template v-if="!isLandloard">
                      <el-col
                        :span="14"
                        v-if="
                          value.value['counts'] &&
                            value.value['counts'].disputed.count
                        "
                      >
                        <div class="fc-black-13 text-left bold">
                          {{ $t('etisalat.payments.under_dispute') }}
                        </div>
                        <template v-if="value.value['counts'].disputed">
                          <div class="fc-black-14 text-left pT5 bold">
                            {{
                              Object.keys(value.value['counts'].disputed) &&
                              value.value['counts'].disputed.cost
                                ? `${formatCurrency(
                                    value.value['counts'].disputed.cost
                                  )} AED`
                                : '0 AED'
                            }}
                          </div>
                          <div class="inline-block">
                            <div
                              class="fc-blue-txt3-13 text-left pT5 pointer inline"
                            >
                              <span class="fc-greey9-12 f13">(</span>
                              <span
                                @click="
                                  redirectToBillAlert('disputed', value.time)
                                "
                                >{{
                                  Object.keys(value.value['counts'].disputed) &&
                                  value.value['counts'].disputed.count
                                    ? `${value.value['counts'].disputed.count ||
                                        0} Bill Alerts `
                                    : ''
                                }}</span
                              >
                              <!-- <span
                                @click="redirectToBills('disputed', value.time)"
                              >
                                {{
                                  Object.keys(value.value['counts'].disputed) &&
                                  value.value['counts'].disputed.count
                                    ? ` from ${value.value['counts'].disputed
                                        .count || 0} Bills`
                                    : ''
                                }}
                              </span> -->
                              <span class="fc-greey9-12 f13">)</span>
                            </div>
                          </div>
                        </template>
                      </el-col>
                      <el-col :span="14" v-else>
                        <div class="fc-black-13 text-left bold">
                          {{ $t('etisalat.payments.under_dispute') }}
                        </div>
                        <div class="fc-greey9-12 pT10 pB20">
                          {{ `'No bills are under disuputes` }}
                        </div>
                      </el-col>
                    </template>
                  </el-row>
                </td>
                <template v-if="!isLandloard">
                  <td
                    style="width: 30%;"
                    v-if="
                      value.value['counts'] &&
                        value.value['counts'].canceled.count
                    "
                  >
                    <div class="fc-black-13 text-left bold">
                      {{ $t('etisalat.payments.cancelled_payments') }}
                    </div>
                    <template v-if="value.value['counts'].canceled">
                      <div class="fc-black-14 text-left pT5 bold">
                        {{
                          Object.keys(value.value['counts'].canceled) &&
                          value.value['counts'].canceled.cost
                            ? `${formatCurrency(
                                value.value['counts'].canceled.cost
                              )} AED`
                            : '---'
                        }}
                      </div>
                      <div
                        class="fc-blue-txt3-13 text-left pT5 pointer"
                        @click="redirectToBills('canceled', value.time)"
                      >
                        <span class="fc-greey9-12 f13">(</span>
                        {{
                          Object.keys(value.value['counts'].canceled) &&
                          value.value['counts'].canceled.count
                            ? `From ${value.value['counts'].canceled.count} Bill Alerts`
                            : '---'
                        }}
                        <span class="fc-greey9-12 f13">)</span>
                      </div>
                    </template>
                  </td>
                  <td style="width: 30%;" v-else>
                    <div class="fc-black-13 text-left bold">
                      {{ $t('etisalat.payments.cancelled_payments') }}
                    </div>
                    <div class="fc-greey9-12 pT10 pB20">
                      {{ `No bills are cancelled yet` }}
                    </div>
                  </td>
                </template>
                <td style="width: 20%;"></td>
                <td style="width: 20%;"></td>
                <td style="width: 20%;" v-if="isLandloard"></td>
              </tr>
              <tr
                class="border-bottom19"
                v-if="
                  value.value['counts'].disputeFree &&
                    value.value['counts'].disputeFree.count
                "
              >
                <td style="width: 30%;" class="border-right10">
                  <template v-if="!isLandloard">
                    <div
                      class="fc-black-13 text-left pT10"
                      v-if="
                        value.value['counts'].disputeFree.region &&
                          value.value['counts'].disputeFree.region.length
                      "
                    >
                      {{ $t('etisalat.payments.region_code') }} :
                      {{
                        `${
                          value.value['counts'].disputeFree.region &&
                          value.value['counts'].disputeFree.region.length
                            ? getRegionCode(
                                value.value['counts'].disputeFree.region
                              )
                            : ''
                        }`
                      }}
                    </div>
                    <div
                      class="fc-black-13 text-left pT10"
                      v-if="
                        value.value['counts'].disputeFree.costCenter &&
                          value.value['counts'].disputeFree.costCenter.length
                      "
                    >
                      {{ $t('etisalat.payments.cost_center') }} :
                      {{
                        `${
                          value.value['counts'].disputeFree.costCenter &&
                          value.value['counts'].disputeFree.costCenter.length
                            ? getCostCenter(
                                value.value['counts'].disputeFree.costCenter
                              )
                            : ''
                        }`
                      }}
                    </div>
                  </template>
                </td>
                <td style="width: 30%;" class="border-right10">
                  <div class="fc-black-13 text-left bold">
                    {{
                      Object.keys(value.value['counts'].disputeFree) &&
                      value.value['counts'].disputeFree.cost
                        ? `${formatCurrency(
                            value.value['counts'].disputeFree.cost
                          )} AED`
                        : '0 AED'
                    }}
                  </div>
                  <div
                    class="fc-greey9-12 pT5 pointer"
                    @click="redirectToBills('undisputed', value.time)"
                  >
                    {{
                      Object.keys(value.value['counts'].disputeFree) &&
                      value.value['counts'].disputeFree.count
                        ? value.value['counts'].disputeFree.count === 1
                          ? 'View bill'
                          : `From ${value.value['counts'].disputeFree.count} Bills`
                        : '---'
                    }}
                  </div>
                  <!-- <div class="fc-black-13 text-left bold pT15">
                    {{ $t('etisalat.payments.vat_amount') }}
                    {{
                          Object.keys(value.value['counts'].disputeFree) &&
                          value.value['counts'].disputeFree.cost
                          ? `${formatCurrency(
                          value.value['counts'].disputeFree.cost
                          )} AED`
                          : '0 AED'
                          }}
                  </div>-->
                </td>
                <td style="width: 20%;" class="border-right10">
                  <div class="fc-orange">
                    {{ 'Pending Payment Memo to Create' }}
                  </div>
                </td>
                <!-- <td
                  style="width: 20%;"
                  class="border-right10 text-center"
                  @click="createInvoice(value.time)"
                > -->
                <td style="width: 20%;" class="border-right10 text-center">
                  <el-button
                    class="fc-border-btn-green text-capitalize"
                    @click="
                      createMemo(value, getDate(value.time, 'MMM YYYY', true))
                    "
                  >
                    {{ 'Create Payment Memo' }}
                  </el-button>
                </td>
              </tr>
              <tr
                class="border-bottom19"
                v-for="(invoice, index) in value.value['invoices']"
                :key="index"
              >
                <td style="width: 30%;" class="border-right10">
                  <div class="fc-blue-txt3-13">
                    {{ invoice['singleline_11'] || '' }}
                  </div>
                  <div class="fc-black-13 text-left pT10 bold">
                    {{ $t('etisalat.payments.invoice_date') }} :
                    {{ getDate(invoice.date_1, 'DD MMM YYYY') }}
                  </div>
                  <div class="fc-black-13 text-left pT10" v-if="!isLandloard">
                    {{ $t('etisalat.payments.region_code') }} :
                    {{ invoice.singleline_4 || '' }}
                  </div>
                  <div class="fc-black-13 text-left pT10">
                    {{ $t('etisalat.payments.cost_center') }} :
                    {{ invoice.singleline_6 || '' }}
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
                  <div class="fc-greey9-12 pT5">
                    {{ invoice.number ? `From ${invoice.number} Bills` : `` }}
                  </div>
                  <!-- <div class="fc-black-13 text-left bold pT15">
                    {{ $t('etisalat.payments.vat_amount') }}
                    {{ invoice.decimal_1 || '0' }} AED
                  </div> -->
                </td>
                <td style="width: 20%;" class="border-right10">
                  <div class="fc-orange">{{ currentModuleState(invoice) }}</div>
                </td>
                <td
                  style="width: 20%;"
                  class="border-right10 text-center"
                  @click="goToInvoice(invoice)"
                >
                  <el-button
                    class="fc-border-btn-blue text-capitalize"
                    style="width: 160px;"
                  >
                    {{ 'Show Payment Memo' }}
                  </el-button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <!-- dialog for create memo -->
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
            {{ `Bill Month - ${createPMmeta.data.billMonth}` }}
          </div>
          <div class="pB5">
            {{ `Total Amount - ${createPMmeta.data.cost}` }}
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
import NewDateHelper from '@/mixins/NewDateHelper'
import { isEmpty } from '@facilio/utils/validation'
import axios from 'axios'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  mixins: [NewDateHelper],
  data() {
    return {
      dateFilter: NewDateHelper.getLastNMonthsTimeStamp(12),
      isLoading: true,
      billState: null,
      alertState: null,
      pdfopen: false,
      billMonth: null,
      currentPage: 0,
      pageCount: 0,
      saving: false,
      result: {},
      dialogVisible: false,
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'activePayments',
        paramList: [],
      },
      createPMmeta: {
        info: '',
        buttonInfo: true,
        data: {},
      },
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'custom_invoices')
    this.$store.dispatch('loadTicketPriority')
  },
  mounted() {
    this.getData()
    this.getBillStateList()
    this.getAlertStateList()
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
    showHeader(data) {
      let { disputed, disputeFree, canceled } = data
      let count = 0
      if (disputed && disputed.count) {
        count = count + Number(disputed.count)
      }
      if (disputeFree && disputeFree.count) {
        count = count + Number(disputeFree.count)
      }
      if (canceled && canceled.count) {
        count = count + Number(canceled.count)
      }
      if (count > 0) {
        return true
      }
      return false
    },
    getCostCenter(costCenter) {
      let costCent = ''
      costCenter.forEach(rt => {
        costCent += rt + ','
      })
      return costCent
    },
    getRegionCode(regioncode) {
      let region = ''
      regioncode.forEach(rt => {
        region += rt + ','
      })
      return region
    },
    getBillStateList() {
      this.$http
        .get(`v2/state/list?parentModuleName=custom_utilitybills`)
        .then(({ data }) => {
          this.billState = data.result.status || []
        })
    },
    getAlertStateList() {
      this.$http
        .get(`v2/state/list?parentModuleName=custom_alert`)
        .then(({ data }) => {
          this.alertState = data.result.status || []
        })
    },
    redirectToBillAlert(state, timeStamp) {
      if (state && timeStamp) {
        let moduleName = 'custom_alert'
        let url = `/app/al/${moduleName}/all`
        let startTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .startOf('month')
          .valueOf()
        let id = this.details.id
        let name = this.details.name
        let underDisputedId = null
        let endTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .endOf('month')
          .valueOf()
        let time = []
        time.push(startTime + '')
        time.push(endTime + '')
        let value = []
        value.push(id + '')
        let statusId = this.alertState.find(rt => rt.status === 'open').id
        let stateValue = []
        stateValue.push(statusId + '')
        statusId = this.alertState.find(rt => rt.status === 'under').id
        stateValue.push(statusId + '')
        let filters = {
          date_3: { operatorId: 20, value: time },
          lookup_1: { operatorId: 36, value: value, selectedLabel: name },
          moduleState: { operatorId: 36, value: stateValue },
        }
        let routeData

        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

          if (name) {
            routeData = this.$router.resolve({
              name,
              params: { viewname: 'all' },
              query: {
                search: JSON.stringify(filters),
                includeParentFilter: true,
              },
            })
          }
        } else {
          routeData = this.$router.resolve({
            path: url,
            query: {
              search: JSON.stringify(filters),
              includeParentFilter: true,
            },
          })
        }

        routeData && window.open(routeData.href, '_blank')
      }
    },
    redirectToBills(state, timeStamp) {
      if (state && timeStamp) {
        let moduleName = 'custom_utilitybills'
        let url = `/app/al/${moduleName}/all`
        let startTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .startOf('month')
          .valueOf()
        let id = this.details.id
        let name = this.details.name
        let underDisputedId = null
        let endTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .endOf('month')
          .valueOf()
        let time = []
        time.push(startTime + '')
        time.push(endTime + '')
        let value = []
        value.push(id + '')
        let statusId = this.billState.find(rt => rt.status === 'disputed').id
        if (state === 'disputed') {
          statusId = this.billState.find(rt => rt.status === 'disputed').id
        } else if (state === 'undisputed') {
          statusId = this.billState.find(rt => rt.status === 'alertprocessed')
            .id
        } else if (state === 'canceled') {
          statusId = this.billState.find(rt => rt.status === 'canceled').id
        }
        let stateValue = []
        stateValue.push(statusId + '')
        let filters = {
          date_5: { operatorId: 20, value: time },
          supplier: { operatorId: 36, value: value, selectedLabel: name },
          moduleState: { operatorId: 36, value: stateValue },
        }
        // this.$router.push({
        //   path: url,
        //   query: {
        //     search: JSON.stringify(filters),
        //     includeParentFilter: true,
        //   },
        // })
        let routeData

        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

          if (name) {
            routeData = this.$router.resolve({
              name,
              params: { viewname: 'all' },
              query: {
                search: JSON.stringify(filters),
                includeParentFilter: true,
              },
            })
          }
        } else {
          routeData = this.$router.resolve({
            path: url,
            query: {
              search: JSON.stringify(filters),
              includeParentFilter: true,
            },
          })
        }

        window.open(routeData.href, '_blank')
      }
    },
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
    currentModuleState(invoice) {
      if (invoice) {
        let moduleName = 'custom_invoices'
        let customModuleData = invoice
        let currentStateId = this.$getProperty(
          customModuleData,
          'moduleState.id'
        )
        let currentState = this.$store.getters.getTicketStatus(
          currentStateId,
          moduleName
        )
        let { displayName, status } = currentState || {}
        if (!isEmpty(displayName)) {
          return displayName
        }
        return status || null
      }
      return ''
    },
    isResponse(response) {
      if (response && response.data && response.data.responseCode === 0) {
        return true
      }
      return false
    },
    createInvoice() {
      this.saving = true
      let { billMonth } = this
      let url = `${window.location.origin}/etisalat/api/generateInvoice`
      axios
        .post(url, {
          billMonth: billMonth,
          supplierId: this.details.id,
        })
        .then(response => {
          console.log('response is : ' + response.data)
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
      let supplierrid = this.details.id
      let value = this.dateFilter.value
      this.params.paramList = [supplierrid, ...value]
      this.result = {}
      this.closeDialog()
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = this.filterNodata(
            this.sortResult(resp.data.result.workflow.returnValue)
          )
        }
        this.isLoading = false
        this.autoResize()
      })
    },
    filterNodata(data) {
      let newList = []
      data.forEach(value => {
        if (value && value.value) {
          if (
            this.showHeader(value.value['counts']) ||
            (value.value['invoices'] && value.value['invoices'].length)
          ) {
            newList.push(value)
          }
        }
      })
      return newList
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
        return sortedData.sort((a, b) => a.time - b.time).reverse()
      }
      return data
    },
    createMemo(bill, month) {
      let { time } = bill
      let totalAmount = bill.value.counts.disputeFree.cost
      let billCount = bill.value.counts.disputeFree.count
      this.billMonth = time
      this.dialogVisible = true
      this.$set(this.createPMmeta.data, 'supplier', this.details.name)
      this.$set(this.createPMmeta.data, 'billMonth', month)
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
      this.createPMmeta.info = ''
    },
  },
}
</script>
