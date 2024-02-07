<template>
  <div class="views-manager-container">
    <div class="header-container">
      <div class="d-flex flex-direction-column">
        <div class="d-flex pointer" @click="goBack">
          <InlineSvg
            src="arrow-pointing-to-left2"
            iconClass="icon icon-xs self-center mR5"
          ></InlineSvg>
          <div class="text-fc-blue f11 letter-spacing0_5">
            {{ $t('viewsmanager.list.back') }}
          </div>
        </div>
        <div class="heading-black22 pT10">
          {{ $t('viewsmanager.list.views_manager') }}
        </div>
      </div>
      <portal-target
        name="view-manager-actions"
        class="mL-auto"
      ></portal-target>
    </div>
    <el-tabs class="manager-tabs-container" v-model="activeTabName">
      <el-tab-pane label="List Views" name="listViews">
        <ModuleListView
          v-if="activeTabName === 'listViews'"
          :moduleName="moduleName"
        ></ModuleListView>
      </el-tab-pane>
      <el-tab-pane label="Scheduled Views" name="scheduledViews">
        <ViewScheduledList
          v-if="activeTabName === 'scheduledViews'"
          :moduleName="moduleName"
        ></ViewScheduledList>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import ViewScheduledList from './ViewScheduledList.vue'
import ModuleListView from './ModuleListView.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from './routeUtil'

export default {
  name: 'ViewManagerLayout',
  props: ['moduleName'],
  components: { ViewScheduledList, ModuleListView },
  data() {
    return {
      activeTabName: 'listViews',
    }
  },
  methods: {
    goBack() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name })
      } else {
        let route = findRouterForModuleInApp(moduleName, pageTypes.LIST) || {}
        route && this.$router.push({ ...route, params: { moduleName } })
      }
    },
  },
}
</script>
<style lang="scss">
.views-manager-container {
  .header-container {
    display: flex;
    padding: 20px 20px 25px;
    background: #fff;
  }
  .btn-container {
    .manager-primary-btn,
    .manager-secondary-btn {
      line-height: normal;
      padding: 11px 17px;
      .btn-label {
        text-transform: uppercase;
        font-size: 12px;
        letter-spacing: 1px;
        font-weight: bold;
        cursor: pointer;
        text-align: center;
      }
    }
    .manager-primary-btn {
      background-color: #fff;
      border: solid 1px #ee518f;
      .btn-label {
        color: #ef508f;
      }
    }
    .manager-secondary-btn {
      background-color: #ef508f;
      border: solid 1px transparent;
      .btn-label {
        color: #fff;
      }
    }
  }
  .manager-tabs-container {
    .el-tabs__header {
      background: #fff;
      padding: 0 20px;
      margin: 0px;
    }
    .el-tabs__content {
      padding: 15px 20px;
      .el-collapse {
        &.folder-collapse {
          border-radius: 5px;
          border: solid 1px #f0eff0;
        }
      }
    }
    .manager-views-item {
      &:hover {
        box-shadow: 0 2px 10px 4px rgba(215, 222, 229, 0.41);
      }
      .el-collapse-item__header {
        padding: 0 12px 0 20px;
      }
      .el-collapse-item__content {
        padding-bottom: 0px;
        .views-item {
          border-top: 1px solid #f0eff0;
          &:hover {
            background-color: #fcfcfc;
          }
          display: flex;
          height: 54px;
          padding-left: 50px;
          .icon {
            &.views-list {
              fill: #353f54;
            }
          }
          .shared-label {
            font-size: 13px;
            letter-spacing: 0.5px;
            color: #8ca0ad;
          }
          .shared-txt {
            font-size: 13px;
            color: #324056;
          }
        }
      }
    }
  }
}
</style>
