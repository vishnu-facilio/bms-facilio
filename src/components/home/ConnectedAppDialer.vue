<template>
  <el-card
    class="dialer-card"
    :style="dialerStyle"
    :class="{ minimized: minimized, closed: closed }"
    v-if="widget"
  >
    <div slot="header" class="clearfix" @click="toggleDialer">
      <span>{{ widget.widgetName }}</span>
      <el-button
        style="float: right; padding: 3px 0"
        type="text"
        :icon="minimized ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"
      ></el-button>
    </div>
    <ConnectedAppViewWidget
      v-if="widget"
      :ref="`ref-${widget.id}`"
      :key="widget.id"
      :widgetId="widget.id"
      :handlers="handlers"
    ></ConnectedAppViewWidget>
  </el-card>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'
import { getApp } from '@facilio/router'

export default {
  components: {
    ConnectedAppViewWidget,
  },
  data() {
    return {
      widget: null,
      dialerState: {
        width: '300px',
        height: '450px',
        top: null,
        bottom: '0px',
        right: '20px',
        left: null,
        theme: null,
        color: null,
      },
      minimized: false,
      closed: true,
      handlers: {},
    }
  },
  mounted() {
    this.initWidgetHandlers()
    this.loadWidgets()
  },
  computed: {
    dialerStyle() {
      let style = ''
      if (this.dialerState.width != null) {
        style += 'width: ' + this.dialerState.width + ';'
      }
      if (this.dialerState.height != null) {
        style += 'height: ' + this.dialerState.height + ';'
      }
      if (this.dialerState.top != null) {
        style += 'top: ' + this.dialerState.top + ';'
      }
      if (this.dialerState.bottom != null) {
        style += 'bottom: ' + this.dialerState.bottom + ';'
      }
      if (this.dialerState.right != null) {
        style += 'right: ' + this.dialerState.right + ';'
      }
      if (this.dialerState.left != null) {
        style += 'left: ' + this.dialerState.left + ';'
      }
      if (this.dialerState.theme != null) {
        style += 'background: ' + this.dialerState.theme + ';'
        style += 'border: 1px solid ' + this.dialerState.theme + ';'
      }
      if (this.dialerState.color != null) {
        style += 'color: ' + this.dialerState.color + ';'
      }
      return style
    },
    currentAppId() {
      return (getApp() || {}).id
    },
  },
  methods: {
    toggleDialer() {
      this.minimized = !this.minimized

      if (!this.minimized) {
        this.sendEvent('dialer.active', true)
      } else {
        this.sendEvent('dialer.inactive', true)
      }
    },
    initWidgetHandlers() {
      let open = () => {
        this.closed = false
      }
      let close = () => {
        this.closed = true
      }
      let maximize = () => {
        this.minimized = false
      }
      let minimize = () => {
        this.minimized = true
      }
      let resize = ({ width, height, top, bottom, left, right }) => {
        this.dialerState.width = width || '300px'
        this.dialerState.height = height || '450px'
        this.dialerState.top = top || null
        this.dialerState.bottom = bottom || '0px'
        this.dialerState.right = right || '20px'
        this.dialerState.left = left || null
      }
      let style = ({ theme, color }) => {
        this.dialerState.theme = theme || null
        this.dialerState.color = color || null
      }

      // live data subscribe
      let subscribe = ({ topic }) => {
        if (topic) {
          this.$wms.subscribe(topic, this.onLiveData)
        }
      }
      let unsubscribe = ({ topic }) => {
        if (topic) {
          this.$wms.unsubscribe(topic, this.onLiveData)
        }
      }

      this.handlers = {
        open,
        close,
        maximize,
        minimize,
        resize,
        style,
        subscribe,
        unsubscribe,
      }
    },
    onLiveData(eventData) {
      this.sendEvent('live.data.received', eventData)
    },
    sendEvent(eventName, eventData) {
      let { id } = this.widget
      let refElement = this.$refs[`ref-${id}`]
      if (!isEmpty(refElement)) {
        refElement.sendEvent(eventName, eventData)
      }
    },
    async loadWidgets() {
      let filters = {
        entityType: {
          operatorId: 9,
          value: ['4'],
        },
        entityId: { operatorId: 9, value: [`${this.currentAppId}`] },
      }

      let encodedFilters = encodeURIComponent(JSON.stringify(filters))
      let url = `/v2/connectedApps/widgetList?filters=${encodedFilters}`
      let { data } = await API.get(url)
      if (data) {
        let { connectedAppWidgets } = data || {}
        if (connectedAppWidgets && connectedAppWidgets.length) {
          this.widget = connectedAppWidgets[0]
        }
      }
    },
  },
}
</script>

<style>
.dialer-card {
  position: fixed;
  bottom: 0px;
  right: 20px;
  z-index: 1000;
  height: 450px;
  width: 300px;
  border: 1px solid #f4f4f4;
}
.dialer-card .el-card__header {
  padding: 12px 20px;
  font-weight: 500;
  cursor: pointer;
}
.dialer-card .el-card__body {
  padding: 0px;
  height: 100%;
}
.dialer-card .el-card__header .el-button--text,
.dialer-card .el-card__header .el-button--text:hover {
  color: unset;
}
.dialer-card.minimized {
  height: 42px !important;
  width: 300px !important;
  bottom: 0px !important;
  right: 20px !important;
  top: unset !important;
  left: unset !important;
}
.dialer-card.closed {
  display: none;
}
</style>
