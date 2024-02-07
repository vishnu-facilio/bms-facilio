<template>
  <ul
    class="subheader-tabs pull-left"
    :class="{
      'has-more-views':
        !$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly,
    }"
  >
    <template
      v-if="!$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly"
    >
      <el-dropdown
        trigger="click"
        class="pointer more-views-dropdown"
        @command="goToView"
      >
        <span class="el-dropdown-link">
          <inline-svg src="svgs/header-more" class="rotate90"></inline-svg>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(item, index) in filteredViews.more"
            :key="item.name + index"
            :command="item"
          >
            {{ item.label }}
          </el-dropdown-item>
          <el-dropdown-item
            v-if="showRearrange"
            class="rearrange-option"
            command="rearrange-views"
            >{{ $t('setup.users_management.rearrange') }}</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
    </template>

    <ViewsList
      v-show="showAllViews"
      :groupViews="groupViews"
      :showViewSchedulerListTab="showScheduledList"
      :showEditIcon="showEditIcon"
      :moduleName="moduleName"
      @onChange="goToView"
      @onClose="showAllViews = false"
      @onDelete="removeView"
    ></ViewsList>
    <ViewCustomization
      :visible.sync="showReorderPanel"
      :reload="true"
      :menu="currentViews"
      :moduleName="moduleName ? moduleName : ''"
      @onchange="loadViews()"
    ></ViewCustomization>
    <NewView :pathPrefix="pathPrefix"></NewView>
  </ul>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ViewHeader from 'newapp/components/ViewHeader'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import ViewsList from 'pages/approvals/components/ApprovalViewSidePanel'

export default {
  extends: ViewHeader,
  props: [
    'moduleName',
    'pathPrefix',
    'showCurrentViewOnly',
    'showEditIcon',
    'maxVisibleMenu',
    'showRearrange',
    'retainFilters',
    'cancelRedirect',
    'currentViewName',
    'hideleftViewHeader',
    'target',
  ],
  components: {
    ViewsList,
  },
  created() {
    this.init()
  },
  data() {
    return {
      showScheduledList: [],
      showReorderPanel: false,
      showAllViews: false,
    }
  },
  computed: {
    currentView() {
      if (!isEmpty(this.currentViewName)) {
        return this.currentViewName
      } else {
        return null
      }
    },
  },
  methods: {
    goToView(view) {
      let { name: viewname } = view

      if (viewname === 'rearrange-views') {
        this.showReorderPanel = true
        return
      }

      let { currentTab, retainFilters = [] } = this
      let { query } = this.$route

      let hasRetainableQuery = retainFilters.some(filterName =>
        query.hasOwnProperty(filterName)
      )

      if (isWebTabsEnabled() && this.target !== 'popup') {
        let route =
          findRouteForTab(currentTab.id, {
            pageType: pageTypes.LIST,
          }) || {}

        if (route) {
          this.$router
            .push({
              ...route,
              params: { viewname },
              query: hasRetainableQuery ? query : null,
            })
            .catch(error => console.warn('Could not switch view\n', error))
        }
      }

      this.$store.dispatch('search/resetFilters')
    },
    removeView(view) {
      let { groupViews } = this

      groupViews.forEach(group => {
        let { views } = group
        let hasView = views.findIndex(v => v.name === view.name)
        if (!isEmpty(hasView)) views.splice(hasView, 1)

        if (!isEmpty(views)) {
          this.goToView(views[0], group)
        } else {
          let index = groupViews.findIndex(grp => grp.name === group.name)
          groupViews.splice(index, 1)

          let groupView = groupViews[0]
          views = groupView.views

          this.goToView(views[0], groupView)
        }
      })
    },
  },
}
</script>
