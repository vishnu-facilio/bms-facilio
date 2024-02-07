<template>
  <div v-if="widget">
    <portal to="custom-topbar-menu" slim v-if="topbarMenuState.showMenu">
      <el-badge
        :is-dot="topbarMenuState.badge.isDot"
        :value="topbarMenuState.badge.value"
        :max="topbarMenuState.badge.max"
        :hidden="topbarMenuState.badge.hidden"
        class="item"
      >
        <i @click="onToggle" :class="topbarMenuState.icon"></i>
      </el-badge>
    </portal>
    <div class="capp-topbar-popover" v-show="topbarMenuState.visible">
      <div class="capp-topbar-popover-header" v-if="topbarMenuState.header">
        <div>
          {{ widget.widgetName }}
        </div>
        <i class="el-icon-close pointer f16 fwBold" @click="onToggle"></i>
      </div>
      <ConnectedAppViewWidget
        class="capp-topbar-popover-container"
        :style="{ height: topbarMenuState.height + ' !important' }"
        :ref="`ref-${widget.id}`"
        :key="widget.id"
        :widgetId="widget.id"
        :handlers="handlers"
      ></ConnectedAppViewWidget>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'

export default {
  components: {
    ConnectedAppViewWidget,
  },
  data() {
    return {
      widget: null,
      topbarMenuState: {
        showMenu: false,
        header: true,
        icon: 'el-icon-help',
        height: '150px',
        visible: false,
        badge: {
          value: null,
          max: null,
          isDot: false,
          hidden: false,
        },
      },
      handlers: {},
    }
  },
  computed: {
    currentAppId() {
      return (getApp() || {}).id
    },
  },
  mounted() {
    this.initWidgetHandlers()
    this.loadWidgets()
  },
  methods: {
    onToggle() {
      this.topbarMenuState.visible = !this.topbarMenuState.visible

      if (!this.topbarMenuState.visible) {
        this.sendEvent('topbar.inactive', true)
      } else {
        this.sendEvent('topbar.active', true)
      }
    },
    initWidgetHandlers() {
      let showMenu = ({ menu }) => {
        this.topbarMenuState.showMenu = menu
      }
      let showHeader = ({ header }) => {
        this.topbarMenuState.header = header
      }
      let hide = () => {
        this.topbarMenuState.visible = false
      }
      let resize = ({ height }) => {
        this.topbarMenuState.height = height || '150px'
      }
      let setBadge = ({ value, max, isDot, hidden }) => {
        this.topbarMenuState.badge.value = value || null
        this.topbarMenuState.badge.max = max || null
        this.topbarMenuState.badge.isDot = isDot || false
        this.topbarMenuState.badge.hidden = hidden || false
      }
      let resetBadge = () => {
        this.topbarMenuState.badge.value = null
        this.topbarMenuState.badge.max = null
        this.topbarMenuState.badge.isDot = false
        this.topbarMenuState.badge.hidden = false
      }
      let setIcon = ({ icon }) => {
        this.topbarMenuState.icon = icon || 'el-icon-help'
      }
      this.handlers = {
        showMenu,
        showHeader,
        hide,
        resize,
        setBadge,
        resetBadge,
        setIcon,
      }
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
          value: ['12'],
        },
        entityId: {
          operatorId: 9,
          value: [`${this.currentAppId}`],
        },
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
.capp-topbar-popover {
  position: fixed;
  right: 0;
  top: 50px;
  width: 390px;
  -webkit-box-shadow: 0 20px 22px 0 rgba(234, 234, 234, 0.6);
  box-shadow: 0 20px 22px 0 rgba(234, 234, 234, 0.6);
  border-left: solid 1px #eaecf1;
  background-color: #ffffff;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-transition: opacity 0.3s linear, right 0.3s ease-out;
  transition: opacity 0.3s linear, right 0.3s ease-out;
  -webkit-transform: translate3d(0%, 0, 0);
  transform: translate3d(0%, 0, 0);
  z-index: 1234;
  overflow: hidden;
}
.capp-topbar-popover .capp-topbar-popover-header {
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  padding: 15px;
  font-weight: 500;
  font-size: 16px;
}
</style>
