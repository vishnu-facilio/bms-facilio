<template>
  <listsummary v-if="moduleName === 'custom_alert'"></listsummary>
  <invoice v-else-if="moduleName === 'custom_invoices'"></invoice>
  <tariff v-else-if="moduleName === 'custom_tariffinfo'"></tariff>
  <custome-summryList v-else></custome-summryList>
</template>
<script>
import listsummary from 'pages/etisalat/BillAlerts/BillAlertSummaryListSummary'
import customeSummryList from 'pages/base-module-v2/ModuleListOverview.vue'
import invoice from 'pages/etisalat/Invoice/paymentmemoSummaryListView.vue'
import tariff from 'pages/etisalat/Tariff/TariffListSummary'
export default {
  props: ['moduleName'],
  components: {
    listsummary,
    customeSummryList,
    invoice,
    tariff,
  },
}
</script>
