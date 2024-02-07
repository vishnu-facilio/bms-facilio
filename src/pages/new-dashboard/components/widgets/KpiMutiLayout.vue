<template>
  <component
    ref="kpicards"
    v-if="layout"
    :is="layout"
    :test="'test'"
    :widget="config ? config.widget : null"
    :config="config ? { ...config, ...widgetMeta } : null"
    :currentDashboard="config ? config.currentDashboard : null"
  ></component>
</template>
<script>
export default {
  props: { item: { type: Object, required: true } },
  components: {
    kpiCard: () => import('pages/dashboard/widget/cards/KFICards'),
    altayerFcuCard: () => import('pages/dashboard/widget/cards/AlTyreList'),
    profilemini: () => import('pages/dashboard/widget/cards/v2/EnergyMiniv2'),
    energycostmini: () =>
      import('pages/dashboard/widget/cards/v2/EnergyCostMiniv2'),
    carbonmini: () => import('pages/dashboard/widget/cards/v2/CarbonMiniv2'),
    targetmeter: () => import('pages/dashboard/widget/cards/v2/TargetMeter'),
    controlCommandmini: () =>
      import('pages/dashboard/widget/cards/v2/ControlCommandMiniv2'),
    kpitargetCard: () =>
      import('pages/dashboard/widget/cards/v2/KPITargetMeter'),
    smartmap: () => import('pages/dashboard/widget/cards/v2/SmartMap'),
    multiTrend: () => import('pages/dashboard/widget/cards/v2/MultiTrend'),
    smartenergymap: () =>
      import('pages/dashboard/widget/cards/v2/SmartEnergyMap'),
    radialCard: () => import('pages/dashboard/widget/cards/v2/RadialCard'),
    linearCard: () => import('pages/dashboard/widget/cards/v2/LinearCard'),
    pmreadingswidget: () =>
      import('pages/dashboard/widget/cards/v2/PmReadingsWidget'),
    smartnewmap: () => import('pages/dashboard/widget/cards/v2/SmartNewMap'),
    smarttable: () => import('pages/dashboard/widget/cards/v2/SmartTable'),
    smartcard: () => import('pages/dashboard/widget/cards/v2/SmartCard'),
    smarttablewrapper: () =>
      import('pages/dashboard/widget/cards/v2/SmartTableWrapper'),
  },
  computed: {
    config() {
      const {
        item: { widget },
      } = this
      return {
        widget: widget,
      }
    },
    layout() {
      if (
        this.config &&
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.paramsJson
      ) {
        if (this.config.widget.dataOptions.paramsJson.key) {
          return this.config.widget.dataOptions.paramsJson.key
        } else {
          return 'kpiCard'
        }
      } else {
        return 'kpiCard'
      }
    },
    widgetMeta() {
      if (this.$el) {
        return {
          widgetHeight: this.$el.clientHeight,
          widgetWidth: this.$el.clientWidth,
        }
      }
      return {
        widgetHeight: null,
        widgetWidth: null,
      }
    },
  },
  methods: {
    refresh() {
      if (this.$refs['kpicards'] && this.$refs['kpicards'].refresh) {
        this.$refs['kpicards'].refresh()
      }
    },
  },
}
</script>

<style></style>
