<template>
  <div class="tarrif-table-details tariff-widget-tabs" ref="preview-container">
    <div class="">
      <div class="white-bg-block">
        <div class="pL20 pR20 pT20 flex-middle justify-content-space">
          <div>
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
          <div></div>
        </div>
        <table
          class="fc-etislat-tariff-table mT10"
          v-if="details.data.picklist_1 === 2"
        >
          <thead>
            <th style="width: 17%;">
              {{ 'Rate' }}
            </th>
            <th style="width: 17%;">
              {{ 'Unit' }}
            </th>
          </thead>
          <tbody>
            <tr>
              <td>
                {{
                  details.data.decimal
                    ? `${formatCurrency(details.data.decimal)} AED`
                    : '0 AED'
                }}
              </td>
              <td>
                {{ details.data.singleline ? details.data.singleline : '' }}
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
          <tbody v-if="slabs && slabs.length > 0">
            <tr v-for="(slab, index) in slabs" :key="index">
              <td>
                {{ slab.data.decimal ? slab.data.decimal : '---' }}
              </td>
              <td>
                {{ slab.data.decimal_1 ? slab.data.decimal_1 : '---' }}
              </td>
              <td>
                {{
                  slab.data.decimal_2
                    ? `${formatCurrency(slab.data.decimal_2)} AED`
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
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      activeName: 'activeRates',
      lookupMap: {},
      slabs: [],
      loading: false,
    }
  },
  mounted() {
    this.loading = true
    let url = 'v2/module/data/list'
    let params = {
      moduleName: 'custom_tariffslab',
      filters: JSON.stringify({
        lookup: {
          operatorId: 36,
          value: [this.details.id.toString()],
          selectedLabel: this.details.name,
        },
      }),
      viewName: 'all',
      includeParentFilter: true,
    }
    API.post(url, params).then(({ data, error }) => {
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.slabs = data.moduleDatas
      }
      this.loading = false
    })
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    data() {
      return this.details.data || null
    },
  },
  methods: {
    formatCurrency(date) {
      return formatCurrency(date, 2)
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
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (
          fieldobj &&
          ['picklist', 'picklist_1', 'picklist_2'].includes(fieldName)
        ) {
          let { enumMap } = fieldobj
          return enumMap[this.data[fieldName]]
        } else if (
          fieldName === 'lookup' &&
          this.lookupMap[this.data.lookup.id]
        ) {
          return this.lookupMap[this.data.lookup.id]
        } else if (fieldName === 'number') {
          return this.data.number || '---'
        } else if (fieldName === 'singleline') {
          return this.data.number || '---'
        } else if (['date', 'date_1'].includes(fieldName)) {
          if (this.data[fieldName]) {
            return moment(this.data[fieldName])
              .tz(this.$timezone)
              .format('DD MMM YYYY')
          }
          return null
        } else if (fieldName === 'singleline') {
          return this.data.singleline ? this.data.singleline : ''
        } else if (fieldName === 'number') {
          return this.data.number ? this.data.number : '---'
        }
      }
      return ''
    },
  },
}
</script>
<style>
.tarrif-table-details {
  background-color: #f7f8f9;
}
</style>
