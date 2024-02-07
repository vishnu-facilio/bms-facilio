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
        <div
          class="header-heading pT10"
          v-if="activeTabName == 'ScheduleViews'"
        >
          {{ $t('pivot.ScheduledReports') }}
        </div>
        <div class="header-heading pT10" v-if="activeTabName == 'folderViews'">
          {{ $t('pivot.folderViews') }}
        </div>
      </div>
      <div class="d-flex flex-direction-column" v-if="activeTabName == 'ScheduleViews'">
        <el-button class="new-scheduler-btn" 
        style="margin-top: 12px;border:none;right:5%;padding: 10px;width:180px;"
        @click="openCreatePopupForScheduleReport"
        >New Report Scheduler</el-button>
      </div>
    </div>
    <!-- <el-tabs class="manager-tabs-container" v-model="activeTabName"> -->
    <div style="padding: 10px;">
        <div class="manager-tabs-container" label="Folder Views" v-if="activeTabName == 'folderViews'">
          <PivotFolderListView></PivotFolderListView>
        </div>
      <!-- <el-tab-pane label="Scheduled Reports" name="ScheduleViews">
        <ReportScheduledList :Name="name" :list="list" :isPivot="true" :webTabId="$attrs.tabId"> </ReportScheduledList>
      </el-tab-pane> -->
      <!-- </el-tabs> -->
      <div label="Scheduled Reports" v-if="activeTabName == 'ScheduleViews'">
        <ReportScheduledList
          :Name="name"
          :list="list"
          :isPivot="true"
          :webTabId="$attrs.tabId"
        >
        </ReportScheduledList>
      </div>
    </div>
    <div class="header-container">
      <div class="d-flex flex-direction-column">
      </div>
    </div>
    <ScheduleReportV2
      :moduleName="currentModule"
      v-if="isCreateScheduleReport"
      :visibility.sync="isCreateScheduleReport"
      :report="{}"
      :webtabId='$attrs.tabId'
      :appId='appId'
      :isPivot="true"
    ></ScheduleReportV2>
  </div>
</template>
<script>
import PivotFolderListView from 'pages/energy/pivot/Components/PivotFolderListView'
import ReportScheduledList from '../../../report/ReportScheduledList.vue'
import ScheduleReportV2 from 'src/pages/report/forms/ScheduleReportNew.vue'
import { isWebTabsEnabled, getApp } from '@facilio/router'
export default {
  components: {
    PivotFolderListView,
    ReportScheduledList,
    ScheduleReportV2
  },
  data() {
    return {
      name: 'pivot',
      list: 'pivot',
      activeTabName: 'folderViews',
      isCreateScheduleReport:false,
    }
  },
  methods: {
    goBack() {
      window.history.go(-1)
    },
    openCreatePopupForScheduleReport(){
      this.isCreateScheduleReport=true
    }
  },
  created() {
    const { activeTab } = this.$route.query
    this.activeTabName = activeTab
  },
  computed: {
    currentModule() {
      if (isWebTabsEnabled()) {
        return this.$attrs.moduleName ? this.$attrs.moduleName : ''
      }
      return "pivot"
    },
    users() {
      return this.$store.state.users
    },
    appId(){
      return getApp().id
    }
  },
}
</script>
<style lang="scss">
.views-manager-container {
  .new-scheduler-btn {
    height: 36px;
    padding: 10px 10px;
    border-radius: 3px;
    font-size: 13px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #fff;
    background-color: #ff3184;
    position: absolute;
    right: 23px;
    top: 12px;
    cursor: pointer;
    &:hover {
      box-shadow: 0 3px 14px 0 #fec1d9;
      background-color: #f31f74;
    }
  }
  .header-heading {
    font-size: 18px;
    font-weight: 400;
    letter-spacing: 0.5px;
    color: #324056;
  }
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
