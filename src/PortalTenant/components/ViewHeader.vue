<template>
  <div class="flex-middle mT17">
    <template v-if="canShowOpenViewSideBar">
      <inline-svg
        src="svgs/hamburger-menu"
        :class="[
          'portal-view-header-burger-icon',
          canShowViewsSidePanel && 'active',
        ]"
        iconClass="icon icon-sm"
        @click.native="openViewsSidePanel"
      ></inline-svg>

      <div class="grp-view-name">
        {{ currentGroup ? currentGroup.displayName : '' }}
      </div>

      <div
        v-if="!$validation.isEmpty(groupViews)"
        class="fc-separator-lg mL30 mR30"
      ></div>
    </template>
    <div class="flex-middle flex-direction-row">
      <router-link
        v-for="(view, index) in activeViews"
        :key="view.name + index"
        :to="getRoute(view)"
        v-slot="{ navigate }"
      >
        <a
          @click="navigate"
          :class="[
            view.name === currentView && 'router-link-exact-active active',
          ]"
        >
          <div class="label-txt-black pointer mR30">
            {{ view.displayName }}
            <div class="portal-active-tab"></div>
          </div>
        </a>
      </router-link>
      <el-dropdown
        v-if="!$validation.isEmpty(moreViews)"
        trigger="click"
        class="mT3 pointer more-views-dropdown"
        @command="goToView"
      >
        <span class="el-dropdown-link">
          <inline-svg src="svgs/header-more"></inline-svg>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(view, index) in moreViews"
            :key="view.name + index"
            :command="view"
          >
            {{ view.displayName }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['canShowViewsSidePanel', 'moduleName', 'groupViews', 'getRoute'],

  computed: {
    canShowOpenViewSideBar() {
      let { groupViews } = this
      return !isEmpty(groupViews)
    },
    activeViews() {
      let { currentGroup, currentView } = this
      let { views } = currentGroup || {}
      let maxViewCount = 3 //maximum views shown in header
      let activeViews = []

      if (!isEmpty(views)) {
        let currentViewIdx = (views || []).findIndex(
          view => view.name === currentView
        )

        if (currentViewIdx < maxViewCount) {
          activeViews = views.slice(0, maxViewCount)
        } else {
          activeViews = views.slice(0, maxViewCount - 1)
          activeViews.push(views[currentViewIdx])
        }
      }

      return activeViews
    },
    moreViews() {
      let { currentGroup, activeViews } = this
      let { views } = currentGroup || {}
      let activeViewNames = (activeViews || []).map(view => view.name)
      return (
        (views || []).filter(view => !activeViewNames.includes(view.name)) || []
      )
    },
    currentView() {
      return this.$route.params.viewname
    },
    currentGroup() {
      let { groupViews } = this

      let selectedGroup = (groupViews || []).find(group => {
        let { views } = group || {}
        let selectedView = (views || []).find(v => v.name === this.currentView)

        return !isEmpty(selectedView)
      })

      return selectedGroup
    },
  },

  methods: {
    openViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', !this.canShowViewsSidePanel)
    },
    openGroup(command) {
      let group = (this.groupViews || []).find(g => g.displayName === command)
      let { views } = group || {}
      let [initialView] = views || []

      !isEmpty(initialView) && this.goToView(initialView)
    },
    goToView(view) {
      this.$emit('onChange', view)
    },
  },
}
</script>
<style lang="scss">
.portal-view-header-burger-icon {
  cursor: pointer;
  margin-right: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4px;

  &:hover,
  &.active {
    background-color: #ebedf4;
    border-radius: 15%;
  }
}
</style>
