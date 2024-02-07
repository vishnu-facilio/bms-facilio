<template>
  <div class="layout">
    <subheader
      :menu="subheaderMenu"
      newbtn="false"
      type="workorder"
      :listCount="listCount"
      parent="/app/co/controls"
      :maxVisibleMenu="3"
    >
      <div class="fR fc-subheader-right flex-middle">
        <template>
          <div class="pL15 fc-black-small-txt-12">
            <pagination
              v-if="listCount"
              :total="listCount"
              :perPage="50"
            ></pagination>
            <div class="block"></div>
          </div>
          <span class="separator" v-if="listCount > 0">|</span>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.search')"
            placement="left"
          >
            <AdvancedSearch
              :key="`${moduleName}-search`"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
            >
            </AdvancedSearch>
          </el-tooltip>
        </template>
      </div>
    </subheader>
    <FTags 
      :key="moduleName"
      :moduleName="moduleName"
    >
    </FTags>
    <div class="height100 row clearboth">
      <div class="height100 f-list-view">
        <router-view @syncCount="callbackMethod" class=""></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import Pagination from '@/list/FPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'

export default {
  data() {
    return {
      listCount: '',
      moduleName: 'controlActionCommand',
      moduleDisplayName: 'Control Action Command',
    }
  },

  components: {
    Subheader,
    Pagination,
    AdvancedSearch,
    FTags,
  },
  computed: {
    subheaderMenu() {
      let actualPath = '/app/co/cc/commands'
      if (isWebTabsEnabled()) {
        let { path } = findRouteForTab(tabTypes.CUSTOM, {
          config: { type: 'commands' },
        })
        actualPath = `/${getApp()?.linkName}/${path}/commands/all`
      }
      let points = {
        label: this.$t('common.header.all_commands'),
        path: { path: actualPath },
      }

      return [points]
    },
  },
  mounted() {
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
  },
  methods: {
    callbackMethod(newVal) {
      this.listCount = newVal
    },
  },
}
</script>
<style>
.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}
</style>
