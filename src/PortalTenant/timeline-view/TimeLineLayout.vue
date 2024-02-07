<template>
  <div class="portal-timeline-layout">
    <el-header class="portal-timeline-layout-header">
      <div class="header-details">
        <div class="tab-header">
          <div class="tab-name">
            {{ currentTab.name }}
          </div>
          <div class="header-section">
            <slot name="header"></slot>
          </div>
        </div>
        <div class="flex-middle mT17">
          <slot name="header-container">
            <ViewHeader
              :moduleName="moduleName"
              :showCurrentViewOnly="true"
              :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            ></ViewHeader>
            <slot name="calendar-actions"> </slot>
          </slot>
        </div>
      </div>
    </el-header>
    <div class="portal-timeline-layout-container">
      <slot name="views-list">
        <ViewsList
          v-if="canShowViewsSidePanel"
          :canShowViewsSidePanel.sync="canShowViewsSidePanel"
          :groupViews="groupViews"
          :moduleName="moduleName"
          @onChange="goToView"
        ></ViewsList>
      </slot>
      <div class="module-list-container">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import SchedulerLayout from 'src/newapp/timeline-view/SchedulerLayout.vue'
import ViewsList from '../components/ViewSideBar.vue'
import ViewHeader from './TimeLineViewHeader.vue'

export default {
  extends: SchedulerLayout,
  components: { ViewHeader, ViewsList },
}
</script>
<style lang="scss">
.portal-timeline-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: auto;

  .portal-timeline-layout-header {
    height: auto !important;
    background: #ffffff;
    position: relative;
    padding: 13px 25px;
    border-left: 1px solid #f5f6fa;
    flex-shrink: 0;

    .header-details {
      display: flex;
      flex-direction: column;

      .center-title {
        height: 70px;
        justify-content: center;
      }
      .tab-header {
        display: flex;
        justify-content: space-between;

        .tab-name {
          font-size: 24px;
          font-weight: 400;
          letter-spacing: 0.67px;
          color: #000;
          line-height: 40px;
        }
      }
      .header-section {
        display: flex;
        align-items: center;
      }
    }
    .router-link-exact-active {
      .portal-active-tab {
        height: 2px;
        width: 25px;
        background: #ff3184;
        position: absolute;
        bottom: 0;
      }
    }
    .grp-view-name {
      font-weight: 500;
      color: #324056;
    }
  }
  .vue-portal-target {
    .today-btn,
    .scheduler-calender-view {
      margin: 0 28px;
    }
  }
  .scheduler.facilio-resource-date-picker {
    margin-left: 0px;
  }
  .portal-timeline-layout-container {
    flex-grow: 1;
    overflow: hidden;
    display: flex;

    .module-list-container {
      flex-grow: 1;
      overflow: auto;

      .el-table {
        th > .cell {
          padding-left: 0px;
          padding-right: 0px;
        }
        th,
        th.is-leaf,
        td {
          padding: 15px 20px;
        }
      }
    }
  }

  .list-loading,
  .list-empty-state {
    display: flex;
    height: 100%;
    background-color: #fff;
  }
  .list-empty-state {
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }
  .tags-container {
    margin: 0px;
  }
}
</style>
