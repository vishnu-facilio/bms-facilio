<template>
  <div
    v-if="showSummaryLinkIcon"
    content="View Summary"
    v-tippy="{ arrow: true }"
    @click="openRecordSummary"
    class="flex-middle"
  >
    <div class="inline d-flex" v-html="computedSvg"></div>
  </div>
</template>
<script>
import InlineSvg from 'src/components/InlineSvg.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: {
    moduleName: {
      type: String,
      required: true,
    },
    id: {
      required: true,
    },
  },
  extends: InlineSvg,
  computed: {
    showSummaryLinkIcon() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          return true
        }
      }
      return false
    },
  },
  methods: {
    openRecordSummary() {
      let viewname = 'all'
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          let routeData = this.$router.resolve({
            name,
            params: {
              viewname,
              id: this.id,
            },
          })
          window.open(routeData.href, '_blank')
        } else {
          this.$message.warning('No summary page found')
        }
      }
    },
  },
}
</script>
