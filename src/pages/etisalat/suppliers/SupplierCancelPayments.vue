<template>
  <div>
    <div class>
      <div v-if="isLoading" class="flex-middle justify-content-center">
        <spinner :show="true" :size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(result) && !isLoading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
      >
        <inline-svg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="nowo-label">
          No Cancelled Payments available
        </div>
      </div>
      <div class v-if="!isLoading && !$validation.isEmpty(result)">
        <table class="supplier-cancel-table">
          <thead>
            <th class="width20">{{ $t('etisalat.common.month') }}</th>
            <th class="width30">
              {{ $t('etisalat.payments.disputes_alerts') }}
            </th>
            <th class="width30">{{ $t('etisalat.common.details') }}</th>
            <th class="width20">{{ $t('etisalat.common.actions') }}</th>
          </thead>
          <tbody>
            <tr v-for="(value, key) in result" :key="key">
              <td class="width20 bold">{{ getDate(key, 'MMM YYYY', true) }}</td>
              <td class="width30">
                <template v-if="value['count'].canceled">
                  <div class="bold">
                    {{
                      Object.keys(value['count'].canceled) &&
                      value['count'].canceled.cost
                        ? `${formatCurrency(value['count'].canceled.cost)} AED`
                        : '---'
                    }}
                  </div>
                  <div class="fc-greey9-12 pT5">
                    {{
                      Object.keys(value['count'].canceled) &&
                      value['count'].canceled.count
                        ? `From ${value['count'].canceled.count} Bills`
                        : '---'
                    }}
                  </div>
                </template>
              </td>
              <td class="width30" v-if="value.region">
                <div
                  v-for="(region, index) in value.region.regionList"
                  :key="index"
                >
                  {{ $t('etisalat.payments.region_code') }} : {{ region }}
                </div>
                <div
                  v-for="(cost, index) in value.region.costCenteList"
                  :key="index"
                  class="pT10"
                >
                  {{ 'Cost Center' }} : {{ cost }}
                </div>
              </td>
              <td class="width30" v-else></td>
              <td class="width20">
                <div class="fc-blue-txt3-13 pointer" @click="showAlerts(key)">
                  {{ $t('etisalat.common.show_alerts') }}
                </div>
                <div
                  @click="redirectToBills('canceled', key)"
                  class="pT10 fc-blue-txt3-13 pointer"
                >
                  {{ $t('etisalat.common.show_bills') }}
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
import NewDateHelper from '@/mixins/NewDateHelper'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details', 'moduleName'],
  mixins: [NewDateHelper],
  data() {
    return {
      dateFilter: NewDateHelper.getLastNMonthsTimeStamp(12),
      pdfopen: false,
      isLoading: true,
      currentPage: 0,
      pageCount: 0,
      result: {},
      billState: null,
      alertState: null,
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'canceledPayments',
        paramList: [],
      },
    }
  },
  created() {
    this.getAlertStateList()
    this.getBillStateList()
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
    showAlerts(timeStamp) {
      if (timeStamp) {
        let moduleName = 'custom_alert'
        let url = `/app/al/${moduleName}/all`
        let startTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .startOf('month')
          .valueOf()
        let id = this.details.id
        let name = this.details.name
        let endTime = moment(Number(timeStamp))
          .tz(this.$timezone)
          .endOf('month')
          .valueOf()
        let time = []
        time.push(startTime + '')
        time.push(endTime + '')
        let value = []
        value.push(id + '')
        let statusId = null
        let stateValue = []
        statusId = this.alertState.find(rt => rt.status === 'cancelled').id
        stateValue.push(statusId + '')
        let filters = {
          date_3: { operatorId: 20, value: time },
          lookup_1: { operatorId: 36, value: value, selectedLabel: name },
          moduleState: { operatorId: 36, value: stateValue },
        }
        let routeData
        let query = {
          search: JSON.stringify(filters),
          includeParentFilter: true,
        }

        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

          if (name) {
            routeData = this.$router.resolve({
              name,
              query,
            })
          }
        } else {
          routeData = this.$router.resolve({
            path: url,
            query,
          })
        }

        if (!isEmpty(routeData)) {
          window.open(routeData.href, '_blank')
        }
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
        let routeData
        let query = {
          search: JSON.stringify(filters),
          includeParentFilter: true,
        }

        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

          if (name) {
            routeData = this.$router.resolve({
              name,
              query,
            })
          }
        } else {
          routeData = this.$router.resolve({
            path: url,
            query,
          })
        }

        if (!isEmpty(routeData)) {
          window.open(routeData.href, '_blank')
        }
      }
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
            params: { id: invoice.id, viewname: 'all' },
          })
        }
      } else {
        routeData = this.$router.resolve({
          path: `/app/supp/custom_invoices/all/${invoice.id}/summary`,
        })
      }

      if (!isEmpty(routeData)) {
        window.open(routeData.href, '_blank')
      }
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
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = resp.data.result.workflow.returnValue
          this.isLoading = false
        }
      })
    },
  },
}
</script>
