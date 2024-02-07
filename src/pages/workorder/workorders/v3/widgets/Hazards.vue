<template>
  <div v-if="isLoading" class="loading-container d-flex justify-content-center">
    <spinner :show="isLoading"></spinner>
  </div>
  <HazardListWidget
    v-else
    :widget="hazardWidget"
    :details="parentDetails"
    :moduleName="moduleName"
    :staticWidgetHeight="'270'"
  ></HazardListWidget>
</template>
<script>
import HazardListWidget from '@/page/widget/common/CommonHazardList'
import { API } from '@facilio/api'
const BASE_SPACE = ['site', 'building', 'floor', 'space']

export default {
  name: 'Hazards',
  components: {
    HazardListWidget,
  },
  props: ['moduleName', 'details'],
  created() {
    this.loadSafetyPlanWidgets()
  },
  data() {
    return {
      hazardWidget: {
        relatedList: {
          module: {},
          field: { name: this.moduleName },
        },
      },
      isLoading: true,
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Hazards'
    },
    parentDetails() {
      let { moduleName } = this
      let details

      if (moduleName === 'workorder') {
        details = this.details.workorder
      } else if (moduleName === 'asset' || BASE_SPACE.includes(moduleName)) {
        details = this.details
      }
      return details
    },
  },
  methods: {
    loadSafetyPlanWidgets() {
      let { moduleName } = this
      if (BASE_SPACE.includes(moduleName)) {
        moduleName = 'space'
      }
      if (this.$helpers.isLicenseEnabled('SAFETY_PLAN')) {
        API.get(`module/meta?moduleName=${moduleName}Hazard`).then(
          ({ data, error }) => {
            if (!error) {
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module',
                this.$getProperty(data, 'meta.module', null)
              )
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module.displayName',
                'Hazards'
              )
              this.isLoading = false
            }
          }
        )
      }
    },
  },
}
</script>
