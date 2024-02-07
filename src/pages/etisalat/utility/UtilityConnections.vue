<template>
  <div v-if="loading">
    <spinner :show="loading"></spinner>
  </div>
  <div v-else>
    <div class="visibility-visible-actions width100">
      <div class="width100 flex-center-row-space pL15 pT20 inline-block">
        <div class="fc-black-14 text-left bold">
          <el-select
            class="fc-input-full-border2 width30"
            v-model="selectedSlabName"
            placeholder="Select tarrif"
            @change="selectTarrif()"
          >
            <el-option
              v-for="(tariff, index) in tariffs"
              :key="index"
              :label="tariff.name"
              :value="tariff.name"
            >
            </el-option>
          </el-select>
        </div>
        <div class="width100 flex-center-row-space pT20" v-if="tariff">
          <div class="">
            <div
              class="bold label-txt-black"
              v-if="getFieldData('date') || getFieldData('date_1')"
            >
              {{ getFieldData('date') ? getFieldData('date') : 'All Time' }} -
              {{ getFieldData('date_1') ? getFieldData('date_1') : 'All Time' }}
            </div>
            <div class="bold label-txt-black" v-else>
              All Time
            </div>
            <div class="fc-black-12 text-left line-height30">
              Cost Type : {{ getFieldData('picklist_1') }}
            </div>
          </div>
        </div>
      </div>
      <div v-if="tariff">
        <div>
          <table
            class="fc-etislat-tariff-table mT10"
            v-if="getFieldData('picklist_1') === 'Fixed'"
          >
            <thead>
              <th style="width: 17%;">
                {{ 'RATE' }}
              </th>
              <th style="width: 17%;">
                {{ 'Unit' }}
              </th>
            </thead>
            <tbody>
              <tr>
                <td>
                  {{
                    tariff.data.decimal
                      ? `${formatCurrency(tariff.data.decimal)} AED`
                      : '0 AED'
                  }}
                </td>
                <td>
                  {{ tariff.data.singleline ? tariff.data.singleline : '' }}
                </td>
              </tr>
            </tbody>
          </table>
          <table class="fc-etislat-tariff-table mT10" v-else>
            <thead>
              <th style="width: 17%;">
                {{ $t('etisalat.etisalat.from') }}
              </th>
              <th style="width: 17%;">
                {{ $t('etisalat.etisalat.to') }}
              </th>
              <th>
                {{ $t('etisalat.etisalat.rete_in_aed') }}
              </th>
            </thead>
            <tbody v-if="tariff.slabs && tariff.slabs.length > 0">
              <tr v-for="(slab, idx) in tariff.slabs" :key="idx">
                <td>
                  {{ slab.decimal ? slab.decimal : '---' }}
                </td>
                <td>
                  {{ slab.decimal_1 ? slab.decimal_1 : '---' }}
                </td>
                <td>
                  {{
                    slab.decimal_2
                      ? `${formatCurrency(slab.decimal_2)} AED`
                      : '0 AED'
                  }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr v-if="loading" class="nodata">
                <td colspan="100%" class="text-center p30imp">
                  <spinner :show="loading"></spinner>
                </td>
              </tr>
              <tr v-else class="nodata">
                <td colspan="100%" class="text-center p30imp">
                  <div class="mT40">
                    <InlineSvg
                      src="svgs/emptystate/readings-empty"
                      iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
                    ></InlineSvg>
                    <div class="pT20 fc-black-dark f18 bold">
                      No tariff slabs available!
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="mT100 self-center text-center" v-else>
        <InlineSvg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="pT20 fc-black-dark f18 bold">
          No tariff slabs available!
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { formatCurrency } from 'charts/helpers/formatter'
import moment from 'moment-timezone'
import Spinner from '@/Spinner'
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      pdfopen: false,
      loading: false,
      currentPage: 0,
      pageCount: 0,
      result: {},
      tariff: null,
      tariffs: [],
      selectedSlabName: null,
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getSummaryData',
        paramList: [],
      },
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    selectTarrif() {
      this.getTarrif()
    },
    formatCurrency(date) {
      return formatCurrency(date, 2)
    },
    getData() {
      let supplierrid =
        this.details.data.supplier && this.details.data.supplier.id
          ? this.details.data.supplier.id
          : null
      this.loading = true
      if (supplierrid) {
        this.params.paramList.push(supplierrid)
        this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
          if (
            resp.data.result &&
            resp.data.result.workflow &&
            resp.data.result.workflow.returnValue
          ) {
            this.result = resp.data.result.workflow.returnValue
            this.tariffs = resp.data.result.workflow.returnValue.tariffs || []
          }
          this.getTarrif()
          this.loading = false
        })
      } else {
        this.loading = false
      }
    },
    getTarrif() {
      if (this.selectedSlabName) {
        this.tariff = this.tariffs.find(rt => rt.name === this.selectedSlabName)
      } else if (this.tariffs.length) {
        this.tariff = this.tariffs[0]
        this.selectedSlabName = this.tariff.name
      }
    },
    getFieldData(fieldName) {
      if (fieldName) {
        if (['date', 'date_1'].includes(fieldName)) {
          if (this.tariff[fieldName]) {
            return moment(this.tariff[fieldName])
              .tz(this.$timezone)
              .format('DD MMM YYYY')
          }
          return ''
        } else if (fieldName === 'picklist_1') {
          let picklist = this.tariff.picklist_1
          if (picklist === 1) {
            return 'Slab'
          } else if (picklist === 2) {
            return 'Fixed'
          }
        }
      }
      return ''
    },
  },
}
</script>
