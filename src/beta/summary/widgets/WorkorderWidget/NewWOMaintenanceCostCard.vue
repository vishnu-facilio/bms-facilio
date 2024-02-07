<template>
  <FContainer display="flex" flex-direction="column" gap="containerXLarge">
    <FContainer>
      <FContainer
        padding="containerMedium containerNone"
        display="flex"
        justifyContent="space-between"
      >
        <FText appearance="headingMed14" color="textMain" fontWeight="bold">{{
          $t('maintenance._workorder.actuals_cost')
        }}</FText>

        <FButton @click="openInfo('actuals')" appearance="link">{{
          $t('maintenance._workorder.view_detail')
        }}</FButton>
      </FContainer>
      <FContainer
        padding="containerMedium containerNone"
        display="flex"
        justifyContent="space-between"
        align-self="stretch"
        alignItems="center"
        gap="containerLarge"
      >
        <FText appearance="bodyReg14" color="textMain">{{
          actualsGrossTotal
        }}</FText>
      </FContainer>
    </FContainer>

    <FContainer display="flex" flex-direction="column" alignItems="flex-start">
      <FContainer
        padding="containerMedium containerNone"
        display="flex"
        justifyContent="space-between"
        align-self="stretch"
        alignItems="center"
      >
        <FText appearance="headingMed14" color="textMain" fontWeight="bold">{{
          $t('maintenance._workorder.plans_cost')
        }}</FText>
        <FButton @click="openInfo('plans')" appearance="link">{{
          $t('maintenance._workorder.view_detail')
        }}</FButton>
      </FContainer>
      <FContainer
        padding="containerMedium containerNone"
        display="flex"
        justifyContent="space-between"
        align-self="stretch"
        alignItems="center"
        gap="containerLarge"
      >
        <FText appearance="bodyReg14" color="textMain">{{
          plansGrossTotal
        }}</FText>
      </FContainer>
    </FContainer>
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
  </FContainer>
</template>
<script>
import { FContainer, FText, FButton } from '@facilio/design-system'
import CostInfo from 'src/pages/workorder/workorders/v3/widgets/WOCostDetailsCard.vue'
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
  }),
  components: { CostInfo, FContainer, FText, FButton },
  computed: {
    plansGrossTotal() {
      let { $currency, plannedMaintenance } = this
      let grossAmount = this.$getProperty(plannedMaintenance, 'grossAmount', 0)

      return `${$currency} ${grossAmount}`
    },
    actualsGrossTotal() {
      let { $currency, actualMaintenanceObj } = this
      let grossAmount = this.$getProperty(
        actualMaintenanceObj,
        'grossAmount',
        0
      )

      return `${$currency} ${grossAmount}`
    },
  },
  created() {
    this.init()
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
