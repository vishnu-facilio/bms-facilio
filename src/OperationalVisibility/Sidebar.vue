<template>
  <el-aside class="fc-agent-sidebar" :width="isMinimized ? '60px' : '250px'">
    <div
      :class="[
        'fc-agent-sidebar-inner d-flex flex-col height100',
        isMinimized && 'is-minimized',
      ]"
    >
      <ul v-if="isMinimized" class="operations-sidebar-list mT0 mB0 pB40">
        <template v-for="group in filteredMenu">
          <el-tooltip
            v-if="group.webTabs.length === 1"
            effect="dark"
            placement="right"
            :content="group.name"
            :key="group.id"
            :open-delay="500"
            :enterable="false"
          >
            <router-link
              :class="[
                'pointer list-item list-hover pT10 pB10 pL15',
                group.id === selectedTabGroup.id && 'active',
              ]"
              tag="div"
              :to="getUrlForFirstTab(group)"
            >
              <InlineSvg
                :src="getIconPath(group.iconType)"
                iconClass="icon icon-md mR10"
              ></InlineSvg>
            </router-link>
          </el-tooltip>
          <template v-else>
            <el-popover
              :key="group.id"
              placement="right"
              trigger="hover"
              :open-delay="200"
              popper-class="fc-popover-p02 fc-sidebar-dp-width"
            >
              <div
                slot="reference"
                :class="[
                  'pointer list-item list-hover pT10 pB10 pL15',
                  group.id === selectedTabGroup.id && 'active',
                ]"
              >
                <InlineSvg
                  :src="getIconPath(group.iconType)"
                  iconClass="icon icon-md mR10"
                ></InlineSvg>
              </div>
              <div class="operations-sidebar-popup">
                <router-link
                  v-for="tab in group.webTabs"
                  :key="tab.id"
                  :to="`/${appName}/${group.route}/${tab.route}`"
                  tag="div"
                  class="pointer pT10 pB10 pL20 pR10 list-item"
                >
                  {{ tab.name }}
                </router-link>
              </div>
            </el-popover>
          </template>
        </template>
      </ul>

      <ul v-else class="operations-sidebar-list mT0 mB0 pB40">
        <template v-for="group in filteredMenu">
          <template v-if="group.webTabs.length === 1">
            <router-link
              :key="group.webTabs[0].id"
              :to="getUrlForFirstTab(group)"
              class="pointer list-item list-hover pT10 pB10 pL15 pR30"
              tag="li"
            >
              <InlineSvg
                :src="getIconPath(group.iconType)"
                iconClass="icon icon-md mR10"
                class="vertical-middle"
              ></InlineSvg>
              {{ group.name }}
            </router-link>
          </template>
          <template v-else>
            <div :key="group.id" class="d-flex flex-col">
              <div
                class="list-item pT10 pB10 pL15 pR10 pointer"
                style="border: 3px solid transparent;"
                @click="expandGroup(group)"
              >
                <InlineSvg
                  :src="getIconPath(group.iconType)"
                  iconClass="icon icon-md mR10"
                  class="vertical-middle"
                ></InlineSvg>
                {{ group.name }}
                <i
                  :class="{
                    'el-icon-arrow-up fR f12 mT5': true,
                    rotate180: group.id !== selectedGroupId,
                  }"
                ></i>
              </div>
              <el-collapse-transition>
                <div
                  class="d-flex flex-col pointer"
                  v-show="group.id === selectedGroupId"
                >
                  <router-link
                    v-for="tab in group.webTabs"
                    :key="tab.id"
                    :to="`/${appName}/${group.route}/${tab.route}`"
                    class="pointer list-item list-item-small list-hover pT10 pB10 pL45 pR10"
                  >
                    {{ tab.name }}
                  </router-link>
                </div>
              </el-collapse-transition>
            </div>
          </template>
        </template>
      </ul>
      <div class="flex-grow flex-shrink" @click="expandGroup"></div>
      <div
        @click="toggleSidebar"
        class="pointer pB15 pT15 sidebar-toggle-container"
      >
        <InlineSvg
          src="svgs/arrow-left-circle"
          iconClass="icon icon-md sidebar-toggle"
          :class="['d-flex justify-center', isMinimized && 'rotate-180']"
        ></InlineSvg>
      </div>
    </div>
  </el-aside>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { getApp } from '@facilio/router'
import sortBy from 'lodash/sortBy'
import icons from 'newapp/webtab-icons.js'

export default {
  data() {
    return {
      openGroupId: null,
      minimized: JSON.parse(localStorage.getItem('fc-ops-sidebar')) || false,
    }
  },
  computed: {
    ...mapState('webtabs', ['selectedTabGroup']),
    ...mapGetters('webtabs', ['getTabGroups']),
    appName() {
      return getApp().linkName || 'operations'
    },
    filteredMenu() {
      let groups = sortBy(this.getTabGroups(), ['order'])
      return groups.map(group => {
        return {
          ...group,
          webTabs: sortBy(group.webTabs, ['order']),
        }
      })
    },
    selectedGroupId: {
      get() {
        let { selectedTabGroup, openGroupId } = this
        return openGroupId || this.$getProperty(selectedTabGroup, 'id', null)
      },
      set(value) {
        this.openGroupId = value
      },
    },
    isMinimized: {
      get() {
        return this.minimized
      },
      set(value) {
        this.minimized = value
        localStorage.setItem('fc-ops-sidebar', value)
      },
    },
  },
  watch: {
    selectedTabGroup: {
      handler(value) {
        if (value) this.openGroup = value.id
      },
      immediate: true,
    },
  },
  methods: {
    getIconPath(type) {
      let iconType = icons[type] || {}
      return iconType.icon || icons[100].icon
    },
    getUrlForFirstTab(group) {
      let { appName } = this
      return `/${appName}/${group.route}/${group.webTabs[0].route}`
    },
    expandGroup({ id } = {}) {
      if (!id || this.selectedGroupId === id) {
        this.selectedGroupId = null
      } else {
        this.selectedGroupId = id
      }
    },
    toggleSidebar() {
      this.isMinimized = !this.isMinimized
      this.selectedGroupId = null
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-agent-sidebar {
  height: 100%;

  .fc-agent-sidebar-inner {
    padding-bottom: 0;
    &:not(.is-minimized) .sidebar-toggle-container {
      background-color: #f2f3f7;
    }
  }

  .operations-sidebar-list {
    list-style-type: none;
    padding-left: 0;
    margin-bottom: 0 !important;
    margin-top: 0 !important;

    .active,
    .router-link-exact-active {
      background-color: rgba(46, 91, 255, 0.3);
      font-weight: 500;
      border-left: 3px solid #2e5bff !important;
    }

    .list-item {
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.44px;
      color: #324056;
      font-size: 15px;

      &.list-item-small {
        font-size: 13px;
      }
    }
    .list-hover {
      border: 3px solid transparent;

      &:hover {
        background-color: #f2f3f7;
        color: #324056;
        border-left: 3px solid #2e5bff;
        font-weight: 400;
      }
    }
  }

  @keyframes slideInLeft {
    from {
      display: none;
    }

    to {
      display: initial;
    }
  }
}
</style>
<style lang="scss">
.fc-agent-sidebar .sidebar-toggle {
  fill: none;
}
.operations-sidebar-popup {
  display: flex;
  flex-direction: column;
  padding: 10px 0;

  .list-item {
    font-weight: normal;
    line-height: normal;
    letter-spacing: 0.44px;
    color: #324056;
    font-size: 13px;

    &.active {
      color: rgb(46, 91, 255);
    }
    &:hover {
      background-color: #f2f3f7;
    }
  }
}
</style>
