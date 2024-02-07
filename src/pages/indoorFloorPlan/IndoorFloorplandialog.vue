<template>
  <div>
    <div
      class="indoor-floorplan-editor fc-indoor-floorplan-editor-view indoor-floorplan-editor-new"
      id="dialogviewer"
    >
      <div
        class="setting-header2 position-relative fp-header indoor-fp-topbar flex-middle"
      >
        <div class="setting-title-block">
          <div class="fc-black3-16 fw4 flex-middle cursor-default">
            <portal-target
              :name="'employee-portal-top-header2'"
              class="emp-floor-switcher"
              :slot-props="{ targetRecived: true, disableDialog: false }"
            >
            </portal-target>
            <span class="fc-grey3-text14 bold f14"></span>
          </div>
        </div>
        <div class="action-btn setting-page-btn">
          <el-button
            @click="$emit('close')"
            type="primary"
            class="fc-btn-green-medium-fill shadow-none"
            >Done</el-button
          >
        </div>
      </div>
      <el-tabs
        v-model="activeTab"
        @tab-click="handleClick"
        class="fp-new-layout-conatiner"
      >
        <el-tab-pane label="Assignment" name="assignment">
          <div class="main-layout">
            <div class="dialog-sidebar">
              <portal-target name="ASSIGNMENT_SIDEBAR" />
            </div>
            <slot name="main"></slot>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      activeTab: 'assignment',
      PORTAL_NAME: 'assignment-view-floor-switch',
    }
  },
}
</script>
<style>
.main-layout .show-leadends-text,
.main-layout .assignment-viwe-leagends {
  bottom: 20px;
}
.indoor-floorplan-editor-new {
  height: auto !important;
}
.fp-new-layout-conatiner .el-tabs__header {
  margin-bottom: 1px;
}
.fp-new-layout-conatiner {
  background: #fff;
  width: 100%;
  height: 100%;
}
.fp-new-layout-conatiner .el-tabs__nav-wrap.is-top {
  padding: 0px 20px;
}
#dialogviewer {
  display: flex;
  flex-direction: column;
  position: fixed;
  width: 100%;
  height: 100%;
  left: 0;
  z-index: 10;
  top: 0;
}
.full-layout {
  height: 100%;
  width: 100%;
  background: #fff;
  z-index: 13;
}
.main-layout {
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100% !important;
  z-index: 10;
  background: #fff;
}
.dialog-sidebar {
  z-index: 3;
  width: 25%;
  background: #fff;
  border-right: 1px solid #eeeeee;
}
.indoor-floorplan-editor-new div#viewer {
  width: 100%;
  height: calc(100vh - 95px);
}
.diabledDropDown {
  display: none;
}
</style>
