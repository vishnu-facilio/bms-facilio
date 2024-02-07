<template>
  <alertlist
    :moduleName="moduleName"
    v-if="moduleName === 'custom_alert'"
  ></alertlist>
  <list-view :moduleName="moduleName" v-else></list-view>
</template>
<script>
import alertlist from 'pages/etisalat/BillAlerts/BillAlertList'
import listView from 'pages/base-module-v2/ModuleList.vue'
export default {
  props: ['moduleName'],
  components: {
    alertlist,
    listView,
  },
}
</script>
