<template>
  <div>
    <div class="fc-black3-16 p20 pB10 cost-heading-border">
      {{ $t('common.tabs.cost') }}
    </div>
    <div v-if="isLoading">
      <spinner :show="isLoading" size="80"></spinner>
    </div>

    <table v-else>
      <tbody>
        <tr>
          <td class="pL20">{{ $t('common.header.labours') }}</td>
          <td class="text-right pT15 pB15 pR20 cost-color">
            {{ `${$currency} ${labourCost}` }}
          </td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.header.items') }}</td>
          <td class="text-right pT15 pB15 pR20 cost-color">
            {{ `${$currency} ${itemCost}` }}
          </td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.header.tools') }}</td>
          <td class="text-right pT15 pB15 pR20 cost-color">
            {{ `${$currency} ${toolCost}` }}
          </td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.products.services') }}</td>
          <td class="text-right pT15 pB15 pR20 cost-color">
            {{ `${$currency} ${serviceCost}` }}
          </td>
        </tr>
      </tbody>
    </table>
    <div class="text-right mT40 mR20" v-if="!isLoading">
      <div class="cost-color pB10">
        {{ $t('common._common.grand_total') }}
      </div>
      <div class="fw-550">{{ `${$currency} ${grossAmount}` }}</div>
    </div>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
export default {
  props: ['details'],
  data() {
    return {
      itemsSelected: true,
      toolsSelected: false,
      servicesSelected: false,
      itemCost: null,
      toolCost: null,
      serviceCost: null,
      grossAmount: null,
      isLoading: false,
    }
  },
  computed: {
    selectItemsClass() {
      return this.itemsSelected ? 'row_select' : ''
    },
    selectToolsClass() {
      return this.toolsSelected ? 'row_select' : ''
    },
    selectServicesClass() {
      return this.servicesSelected ? 'row_select' : ''
    },
    selectLaboursClass() {
      return this.laboursSelected ? 'row_select' : ''
    },
  },
  created() {
    this.setPlansCost()
    eventBus.$on('loadCost', () => {
      this.setPlansCost()
    })
  },
  methods: {
    async setPlansCost() {
      this.isLoading = true
      let workOrderId = this.$getProperty(this.details, 'id')
      let url = '/v3/workOrderPlansCost/cost'
      let queryParam = {
        workOrderId: workOrderId,
      }
      let { data, error } = await API.get(url, queryParam)

      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_cost'))
      } else {
        let {
          plannedToolsCost,
          plannedItemsCost,
          plannedServicesCost,
          plannedLabourCost,
        } = data || {}

        this.itemCost = plannedItemsCost
        this.toolCost = plannedToolsCost
        this.serviceCost = plannedServicesCost
        this.labourCost = plannedLabourCost
        let { itemCost, toolCost, serviceCost, labourCost } = this

        this.grossAmount = itemCost + toolCost + serviceCost + labourCost
        this.isLoading = false
      }
    },
  },
}
</script>
<style scoped>
table tr,
.cost-heading-border {
  border-bottom: 1px solid #e5eaee;
}
.row_select {
  border-left: 2px solid blue;
  background-color: #f7faff;
}
.fw-550 {
  font-weight: 550;
}
.cost-color {
  color: #808998;
  font-weight: 400;
}
.mT90 {
  margin-top: 90px !important;
}
</style>
