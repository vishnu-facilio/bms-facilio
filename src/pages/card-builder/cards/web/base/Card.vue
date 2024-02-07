<template>
  <div class="dragabale-card height100">
    <div class="card-header-block f15 bold">
      {{ cardData.title }}
      <span class="pull-right" @click="mobiledrilldown()" v-if="$mobile">
        <i class="fa fa-external-link f13"></i>
      </span>
    </div>
    <template v-if="type === 'url'">
      <iframe
        v-if="cardData && cardData.value"
        :src="cardData.value"
        class="dashboard-web-widget"
        @error="() => {}"
      ></iframe>
    </template>
    <template v-else>
      <connected-app-view-widget
        style="height: calc(100% - 46px);"
        ref="connectedAppWidget"
        :context="connectedAppWidgetContext"
        :widgetId="connectedAppWidgetId"
      ></connected-app-view-widget>
    </template>
  </div>
</template>
<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import { isEqual } from 'lodash'

export default {
  props: ['widget', 'config', 'loading', 'cardDataObj'],
  components: {
    ConnectedAppViewWidget: () =>
      import('pages/connectedapps/ConnectedAppViewWidget'),
  },
  extends: BaseCard,
  data() {
    return {}
  },
  computed: {
    connectedAppWidgetId() {
      if (this.cardParams && this.cardParams.connectedAppWidgetId) {
        return this.cardParams.connectedAppWidgetId
      } else if (this.cardDataObj && this.cardDataObj.connectedAppWidgetId) {
        return this.cardDataObj.connectedAppWidgetId
      }
      return null
    },
    connectedAppWidgetContext() {
      if (
        (this.cardParams && this.cardParams.connectedAppWidgetId) ||
        (this.cardDataObj && this.cardDataObj.connectedAppWidgetId)
      ) {
        return {
          dashboardFilter: {
            timelineFilter: this.dbTimelineFilter,
            userFilters: this.dbCustomScriptFilter,
          },
        }
      }
      return null
    },
    type() {
      if (this.cardParams && this.cardParams.type) {
        return this.cardParams.type
      } else if (this.cardDataObj && this.cardDataObj.type) {
        return this.cardDataObj.type
      }
      return 'url'
    },
  },
  watch: {
    connectedAppWidgetContext: function(oldValue, newValue) {
      if (!isEqual(oldValue, newValue)) {
        if (this.$refs.connectedAppWidget) {
          this.$refs.connectedAppWidget.sendEvent(
            'db.filters.changed',
            this.connectedAppWidgetContext
          )
        }
      }
    },
  },
  methods: {
    mobiledrilldown() {
      if (this.$mobile && this.type === 'url') {
        let drillDowndata = {
          path: this.cardData.value,
          type: 'url',
        }
        this.$helpers.sendToMobile(drillDowndata)
      }
    },
  },
}
</script>
