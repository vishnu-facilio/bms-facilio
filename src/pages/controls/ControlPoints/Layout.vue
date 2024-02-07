<template>
  <div class="layout container controls-layout">
    <subheader
      :menu="subheaderMenu"
      newbtn="false"
      type="workorder"
      parent="/app/co/cp"
    >
      <div class="row" style="margin-right: 30px; margin-top: -12px;">
        <div class="col-12">
          <div class="fR fc-subheader-right">
            <portal-target name="controlpagenation"></portal-target>
            <button
              v-if="false"
              class="fc-create-btn"
              style="height: 35px;margin-right: -20px;margin-top: 3px;"
              @click="addControlGroup()"
            >
              {{ $t('common.header._new_control_group') }}
            </button>
            <div class="block"></div>
          </div>
        </div>
      </div>
    </subheader>
    <div class="height100 f-list-view" v-bind:class="{ '': '' }">
      <router-view @syncCount="callbackMethod" class=""></router-view>
    </div>
    <new-group-form
      v-if="controlGroupVisible"
      :controlGroup="selectedControlGroup"
      :isNew="isNew"
      :visibility.sync="controlGroupVisible"
    ></new-group-form>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import NewGroupForm from 'pages/controls/ControlPoints/NewControlGroupForm'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

export default {
  data() {
    return {
      showQuickSearch: false,
      quickSearchQuery: null,
      resetFilters: false,
      listCount: '',
      controlGroupbtnShow: false,
      controlGroupVisible: false,
      selectedControlGroup: null,
    }
  },
  components: {
    Subheader,
    NewGroupForm,
  },
  computed: {
    subheaderMenu() {
      let actualPath = '/app/co/cp/controlpoints'
      if (isWebTabsEnabled()) {
        let { path } = findRouteForTab(tabTypes.CUSTOM, {
          config: { type: 'points' },
        })
        actualPath = `/${getApp()?.linkName}/${path}/points/all`
      }
      let points = {
        label: this.$t('common.header.all_control_points'),
        path: { path: actualPath },
      }

      return [points]
    },
  },
  mounted() {
    if (!this.filterSelected && this.filters) {
      this.toggleViewFilter()
    }
    this.$store.dispatch(
      'view/loadModuleMetaForControlPoints',
      'readingdatameta'
    )
  },
  methods: {
    openAsset() {
      this.$refs.assetLayout.addAsset()
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    quickSearch() {
      this.$router.replace({ query: { searchText: this.quickSearchQuery } })
    },
    callbackMethod(newVal) {
      this.listCount = newVal
    },
    addControlGroup(controlData) {
      this.controlGroupVisible = true
      this.isNew = true
      this.selectedControlGroup = controlData
    },
  },
}
</script>
<style scoped lang="scss">
.controls-layout {
  max-width: 100% !important;
}
</style>
