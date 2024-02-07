<template>
  <component
    ref="cardWidget"
    v-if="staticKey"
    :is="staticKey"
    :widget="config ? config.widget : null"
    :config="config ? config : null"
    :currentDashboard="config ? config.currentDashboard : null"
  >
  </component>
</template>
<script>
export default {
  props: ['config'],
  data() {
    return {}
  },
  components: {
    workordersummary: () =>
      import('pages/dashboard/widget/static/WorkorderSummary'),
    categories: () => import('pages/dashboard/widget/static/Categories'),
    technicians: () => import('pages/dashboard/widget/static/Technicians'),
    closedwotrend: () => import('pages/dashboard/widget/static/ClosedWoTrend'),
    mywosummary: () =>
      import('pages/dashboard/widget/static/MyWorkorderSummary'),
    profilecard: () => import('pages/dashboard/widget/static/ProfileCard'),
    energycard: () => import('pages/dashboard/widget/static/EnergyCard'),
    energycost: () => import('pages/dashboard/widget/static/EnergyCost'),
    weathercard: () => import('pages/dashboard/widget/static/WeatherCard'),
    openalarms: () => import('pages/dashboard/widget/static/OpenAlarms'),
    buildingopenalarms: () =>
      import('pages/dashboard/widget/static/BuildingOpenAlarms'),
    mapwidget: () => import('pages/dashboard/widget/static/MapWidget'),
    buildingmapwidget: () =>
      import('pages/dashboard/widget/static/BuildingMapWidget'),
    sampleppm: () => import('pages/dashboard/widget/static/sampleppm'),
    utsdata: () => import('pages/dashboard/widget/static/UtsTableData'),
    energycostaltayer: () =>
      import('pages/dashboard/widget/static/EnergySaving'),
    weathercardaltayer: () =>
      import('pages/dashboard/widget/static/CarbonSaving'),
    cumulativesavings: () =>
      import('pages/dashboard/widget/static/CumulativeSavings'),
    weathermini: () => import('pages/dashboard/widget/cards/WeatherMiniCard'),
    carbonmini: () => import('pages/dashboard/widget/cards/CarbonMini'),
    energycostmini: () => import('pages/dashboard/widget/cards/Energymini'),
    profilemini: () => import('pages/dashboard/widget/cards/Profilemini'),
    readingcard: () => import('pages/dashboard/widget/cards/Readingcard'),
    readingWithGraphCard: () =>
      import('pages/dashboard/widget/cards/Readingcard'),
    readingGaugeCard: () => import('pages/dashboard/widget/cards/Readingcard'),
    imagecard: () => import('pages/dashboard/widget/cards/Imagecard'),
    fahuStatusCard: () => import('pages/dashboard/widget/cards/FAHUCard'),
    fahuStatusCard1: () => import('pages/dashboard/widget/cards/FAHUCard'),
    fahuStatusCard2: () => import('pages/dashboard/widget/cards/FAHUCard'),
    fahuStatusCard3: () => import('pages/dashboard/widget/cards/FAHUCard'),
    fahuStatusCardNew: () => import('pages/dashboard/widget/cards/FAHUCard'),
    textcard: () => import('pages/dashboard/widget/cards/TextCard'),
    web: () => import('pages/dashboard/widget/FWebWidget'),
    readingComboCard: () => import('pages/dashboard/widget/cards/AssetCard'),
    fcucard: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrilllevel1: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrilllevel2: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrilllevel3: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrilllevel: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrillFcu: () => import('pages/dashboard/widget/cards/FcuCard'),
    emrilllevel1List: () => import('pages/dashboard/widget/cards/FcuList'),
    emrilllevel2List: () => import('pages/dashboard/widget/cards/FcuList'),
    emrilllevel3List: () => import('pages/dashboard/widget/cards/FcuList'),
    emrillFcuList: () => import('pages/dashboard/widget/cards/FcuList'),
    resourceAlarmBar: () => import('pages/dashboard/widget/cards/FBooleanCard'),
    alarmbarwidget: () => import('pages/dashboard/widget/cards/FAlarmCard'),
    kpiCard: () => import('pages/dashboard/widget/cards/KpiMutiLayout'),
    kpiMultiResultCard: () =>
      import('pages/dashboard/widget/cards/kpiMultiResultCard'),
    tempcard1: () => import('pages/dashboard/widget/cards/tempcard1'),
  },
  computed: {
    staticKey() {
      if (this.config && this.config.widget.dataOptions) {
        return this.config.widget.dataOptions.staticKey
      }
      return null
    },
  },
  methods: {
    refresh() {
      if (this.$refs['cardWidget'] && this.$refs['cardWidget'].refresh) {
        this.$refs['cardWidget'].refresh()
      }
    },
  },
}
</script>
<style></style>
