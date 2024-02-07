<template>
  <bill-summary v-if="moduleName === 'custom_alert'"></bill-summary>
  <invoice v-else-if="moduleName === 'custom_invoices'"></invoice>
  <tariff v-else-if="moduleName === 'custom_tariffinfo'"></tariff>
  <summary-view v-else></summary-view>
</template>
<script>
import summaryView from 'pages/base-module-v2/Overview.vue'
import billSummary from 'pages/etisalat/BillAlerts/BillAlertSummary'
import invoice from 'pages/etisalat/Invoice/PaymentSummary'
import tariff from 'src/pages/etisalat/Tariff/TariffSummary'
export default {
  props: ['moduleName'],
  components: {
    summaryView,
    billSummary,
    invoice,
    tariff,
  },
}
</script>
