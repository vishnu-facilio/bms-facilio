<template>
  <div
    class="layout container common-list-layout reading-kpi wo-calendar-max-width"
  >
    <div class="view-header-container subheader-section">
      <calendar-header :showWoFilters="showWoFilters"></calendar-header>
    </div>
    <div class="height100 fc__layout__has__row">
      <div>
        <FTags :key="`ftags-list-${moduleName}`"></FTags>
      </div>
      <tags :disableSaveFilters="true"></tags>
      <div class="height100 f-list-view">
        <router-view class="mT15"></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import CalendarHeader from 'pages/workorder/calendar/CalendarHeader'
import FTags from 'newapp/components/search/FTags'

export default {
  components: {
    CalendarHeader,
    FTags,
  },
  data() {
    return {
      moduleName: 'workorder',
    }
  },
  computed: {
    showWoFilters() {
      let routerPath = ['pmplannernew', 'staffplannernew', 'spaceplanner']
      return routerPath.some(path => this.$route.path.includes(path))
    },
  },
  created() {
    this.$store.dispatch('view/clearViews')
  },
}
</script>
<style css scoped>
.wo-calendar-max-width {
  max-width: 100% !important;
}
</style>
