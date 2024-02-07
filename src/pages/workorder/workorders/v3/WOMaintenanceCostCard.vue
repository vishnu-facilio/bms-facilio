<template>
  <div class="wo-maintenance-cost">
    <div class="actuals-cost cost-detail">
      <div class="cost-header">
        {{ $t('maintenance._workorder.actuals_cost') }}
      </div>
      <div class="cost-info">
        <CurrencyPopOver
          v-if="checkForMultiCurrency('cost', metaFieldTypeMap)"
          :field="{
            displayValue: $getProperty(actualMaintenanceObj, 'grossAmount', 0),
          }"
          :details="workorder"
          :showInfo="true"
        />
        <div v-else class="cost">{{ actualsGrossTotal }}</div>
        <div class="view-info" @click="openInfo('actuals')">
          {{ $t('maintenance._workorder.view_detail') }}
        </div>
      </div>
    </div>
    <div class="mT10 plans-cost cost-detail">
      <div class="cost-header plans">
        {{ $t('maintenance._workorder.plans_cost') }}
      </div>
      <div class="cost-info">
        <CurrencyPopOver
          v-if="checkForMultiCurrency('cost', metaFieldTypeMap)"
          :field="{
            displayValue: $getProperty(plannedMaintenance, 'grossAmount', 0),
          }"
          :details="workorder"
          :showInfo="true"
        />
        <div v-else class="cost plans">{{ plansGrossTotal }}</div>
        <div class="view-info" @click="openInfo('plans')">
          {{ $t('maintenance._workorder.view_detail') }}
        </div>
      </div>
    </div>
    <CostInfo
      v-if="showDetails"
      :details="workorder"
      :costType="costType"
      :costDetails="costDetails"
      :showDetails="showDetails"
      :isLoading="isLoading"
      @updateAdditionalCost="updateAdditionalCost"
      @closeInfo="closeInfo"
    />
  </div>
</template>
<script>
import CostInfo from 'src/pages/workorder/workorders/v3/widgets/WOCostDetailsCard.vue'
import { getCurrencyForCurrencyCode } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  name: 'WOMaintenanceCostCard',
  props: ['workorder', 'plannedMaintenance', 'actualMaintenance', 'isLoading'],
  data: () => ({
    showDetails: false,
    costType: null,
    costDetails: {},
    actualMaintenanceObj: {
      itemCost: null,
      toolCost: null,
      serviceCost: null,
      grossAmount: null,
    },
    metaFieldTypeMap: {},
    checkForMultiCurrency,
  }),
  components: { CostInfo, CurrencyPopOver },
  computed: {
    plansGrossTotal() {
      let { $currency, plannedMaintenance, workorderCurrency } = this
      let grossAmount = this.$getProperty(plannedMaintenance, 'grossAmount', 0)

      return `${workorderCurrency || $currency} ${grossAmount}`
    },
    actualsGrossTotal() {
      let { $currency, actualMaintenanceObj, workorderCurrency } = this

      let grossAmount = this.$getProperty(
        actualMaintenanceObj,
        'grossAmount',
        0
      )

      return `${workorderCurrency || $currency} ${grossAmount}`
    },
    workorderCurrency() {
      let { currencyCode } = this.workorder || {}
      return getCurrencyForCurrencyCode(currencyCode)
    },
  },
  async created() {
    this.init()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('workorderCost')
  },
  watch: {
    actualMaintenance: {
      async handler(newVal) {
        let { costType } = this
        this.actualMaintenanceObj = newVal || {}
        if (costType === 'actuals') this.costDetails = newVal || {}
      },
      deep: true,
    },
  },
  methods: {
    async init() {
      let { actualMaintenance } = this
      this.actualMaintenanceObj = actualMaintenance || {}
    },
    openInfo(type) {
      let { plannedMaintenance, actualMaintenanceObj } = this
      this.costType = type
      this.costDetails =
        type === 'plans' ? plannedMaintenance : actualMaintenanceObj
      this.showDetails = true
    },
    updateAdditionalCost({ costList, skipOldValues }) {
      let { actualMaintenanceObj } = this
      let { customCostList } = actualMaintenanceObj || {}
      if (!skipOldValues) {
        customCostList = [...customCostList, ...costList]
      } else {
        customCostList = costList
      }
      let _actualMaintenanceObj = { ...actualMaintenanceObj, customCostList }
      this.actualMaintenanceObj = _actualMaintenanceObj
      this.costDetails = _actualMaintenanceObj
      this.$emit('updateActualGrossTotal', _actualMaintenanceObj)
    },
    closeInfo() {
      this.showDetails = false
      this.costType = null
      this.costDetails = null
    },
  },
}
</script>
<style scoped lang="scss">
.wo-maintenance-cost {
  .widget-card-title {
    color: #39b2c2;
  }
  .cost-detail {
    height: 70px;
    border-bottom: 1px solid #e3e6e8;
    .cost-header {
      font-size: 14px;
      font-weight: 500;
      color: #1d384e;
      padding-left: 20px;
      padding-right: 15px;
    }
    .cost-info {
      display: flex;
      justify-content: space-between;
      margin-top: 15px;
      padding-left: 20px;
      padding-right: 15px;
      .view-info {
        color: #0074d1;
        font-weight: 400;
        font-size: 12px;
        cursor: pointer;
        &:hover {
          text-decoration: underline;
        }
      }
    }
    .plans {
      font-size: 12px;
    }
  }
}
</style>
