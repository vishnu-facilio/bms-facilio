<template>
  <div class="white-bg-block pm-summary-right-bg wo-summary-right-bg-border">
    <div class="new-in-header p30">
      {{ $t('common._common.estimated_cost') }}
    </div>
    <div>
      <el-row class="p30 p10 pB10">
        <el-col :span="12" class="label-txt-black">
          {{ $t('common.products.items') }}
        </el-col>
        <el-col :span="12" class="label-txt-black text-right">
          <currency :value="itemsCost"></currency>
        </el-col>
      </el-row>
      <el-row class="p30 p10 pB5">
        <el-col :span="12" class="label-txt-black">
          {{ $t('common.header.tools') }}
        </el-col>
        <el-col :span="12" class="label-txt-black text-right">
          <currency :value="toolsCost"></currency>
        </el-col>
      </el-row>
      <el-row class="p30 p10 pB5">
        <el-col :span="12" class="label-txt-black">
          {{ $t('common.products.services') }}
        </el-col>
        <el-col :span="12" class="label-txt-black text-right">
          <currency :value="servicesCost"></currency>
        </el-col>
      </el-row>
    </div>
    <el-row class="border-top1 text-right total-amount">
      <el-col :span="24" class="p30 p10 pB5">
        <currency :value="overAllCost"></currency>
        <div class="fc-text-pink13 fw-bold text-right pT5">
          {{ $t('common.header._total') }}
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  props: ['details'],
  data() {
    return {
      itemsCost: null,
      toolsCost: null,
      servicesCost: null,
      overAllCost: null,
    }
  },

  mounted() {
    eventBus.$on('reloadOverallCost', () => {
      this.init()
    })
    this.init()
  },
  methods: {
    init() {
      eventBus.$on('itemsTotalCost', this.getItemsCost)
      eventBus.$on('toolsTotalCost', this.getToolsCost)
      eventBus.$on('servicesTotalCost', this.getServicesCost)
    },
    getItemsCost(cost) {
      this.itemsCost = cost
      this.plannedInventoryTotalCost()
    },
    getToolsCost(cost) {
      this.toolsCost = cost
      this.plannedInventoryTotalCost()
    },
    getServicesCost(cost) {
      this.servicesCost = cost
      this.plannedInventoryTotalCost()
    },
    plannedInventoryTotalCost() {
      let { itemsCost, toolsCost, servicesCost } = this
      this.overAllCost =
        Number(itemsCost) + Number(toolsCost) + Number(servicesCost)
    },
  },
}
</script>
<style scoped>
.total-amount {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: right;
  color: #324056;
}
.new-in-header {
  font-size: 16px;
  letter-spacing: 0.7px;
  color: #324056;
  padding-bottom: 20px;
  padding-top: 30px;
}
</style>
