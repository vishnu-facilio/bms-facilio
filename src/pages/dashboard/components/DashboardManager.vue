<template>
  <el-dialog
    :visible.sync="visibility"
    @open="openDialog"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog74 setup-dialog"
    style="z-index: 1999"
  >
    <div class="rep-form">
      <div class="rep-header">
        <div class="rep-title">{{ $t('panel.remote.dash_manager') }}</div>
        <div class="rep-tabs">
          <ul>
            <li
              v-for="(tab, index) in tabs"
              :key="index"
              class="rep-tab"
              @click="selectedTab = tab.value"
              v-bind:class="{ active: selectedTab === tab.value }"
            >
              <a>{{ tab.label }}</a>
              <div class="sh-selection-bar"></div>
            </li>
          </ul>
        </div>
      </div>
      <div class="rep-body row pL20 pR20">
        <!-- <div class="rep-body-selection col-3">
          <div v-for="(tab, index) in tabs" :key="index" class="rep-side-label" @click="selectedTab = tab.value" v-bind:class="{active: selectedTab === tab.value}">{{tab.label}}</div>
        </div> -->
        <div class="rep-body-action col-12" v-if="selectedTab">
          <div class="" v-if="selectedTab === 'screens'">
            <dashboard-screens
              :module="module ? module.module : 'workorder'"
            ></dashboard-screens>
          </div>
          <div class="" v-if="selectedTab === 'remote_screens'">
            <dashboard-remote-screens></dashboard-remote-screens>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel"
          style="width: 100% !important;"
          @click="closeDialog"
          >{{ $t('panel.remote.close') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
import DashboardScreens from './DashboardScreens'
import DashboardRemoteScreens from './DashboardRemoteScreens'

export default {
  props: ['visibility', 'module'],
  components: {
    DashboardScreens,
    DashboardRemoteScreens,
  },
  data() {
    return {
      tabs: [
        {
          label: 'Screens',
          value: 'screens',
        },
        {
          label: 'Remote Screens',
          value: 'remote_screens',
        },
      ],
      selectedTab: null,
    }
  },
  methods: {
    openDialog() {
      if (!this.selectedTab) {
        this.selectedTab = 'screens'
      }
    },
    closeDialog() {
      this.selectedTab = null
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style>
.rep-title {
  font-weight: 500;
  font-size: 16px;
  letter-spacing: 0.3px;
}
.rep-body-selection {
  border-right: 1px solid #f4f4f4;
}
.rep-body {
  height: calc(100vh - 110px);
}
.rep-body-action {
  background: #f8f9fa;
}
.rep-side-label {
  padding: 15px 20px;
  letter-spacing: 0.4px;
  cursor: pointer;
}
.rep-side-label.active {
  background: #3ab2c2;
  color: #fff;
  font-weight: 500;
}
.rep-col {
  padding-left: 20px;
  padding-bottom: 30px;
}
.rep-data {
  width: 85%;
  margin: auto;
  padding: 20px;
  background: #fff;
}
.rep-data-container {
  background: #fff;
  height: 100%;
  padding-top: 25px;
  padding-bottom: 25px;
  overflow-y: scroll;
}
.rep-data-header {
  text-transform: uppercase;
  font-size: 11px;
  letter-spacing: 0.6px;
  color: gray;
  font-weight: 500;
}
.rep-cont {
  border-radius: 4px;
  padding: 10px;
  border: 1px solid #f4f4f4;
  margin-top: 15px;
  box-shadow: 0 3px 2px 0 rgba(217, 217, 217, 0.5);
  position: relative;
}
.rep-drag-icon {
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  -o-transform: rotate(90deg);
  -ms-transform: rotate(90deg);
  transform: rotate(90deg);
  text-align: center;
  position: relative;
  top: -1px;
  cursor: pointer;
  color: #ddd;
}
.rep-drag-icon .el-icon-more:first-child {
  position: relative;
  top: 10px;
}
.rep-axis-content {
  display: inline-flex;
  width: 100%;
  padding-top: 10px;
  padding-bottom: 10px;
}
.inline-display {
  display: -moz-inline-box;
  display: -o-inline-box;
  display: -ms-inline-box;
  display: -webkit-inline-block;
}
.rep-label-subsection {
  align-items: center;
  width: 100%;
  /* display: inline-box;
  display: -webkit-inline-box !important;
  display: -moz-inline-box !important;
  display: -o-inline-box !important;
  display: -mz-inline-box !important; */
}
.rep-container {
  padding: 10px;
  border: 1px solid #f4f4f4;
  border-radius: 3px;
  margin: 20px;
}
.rep-conatiner-header {
  font-weight: 500;
  padding-right: 10px;
}
.rep-axis-box {
  height: 100%;
  overflow: scroll;
}
.rep-container .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: #ed518f;
  border-color: #ed518f;
}
.datagroup {
  border: 1px solid #f2f2f2;
  width: 100%;
}
.rep-normal-conetnt .rep-drag-icon {
  display: inline-grid;
}
.rep-content-rm-icon {
  margin: auto;
  text-align: end;
  cursor: pointer;
}
.drop-conatiner {
  background: #8a2be257;
  height: calc(100% - 20px);
  position: absolute;
  width: calc(100% - 20px);
  z-index: 100;
  color: #333;
  font-size: 22px;
  text-transform: uppercase;
  align-items: center;
  text-align: center;
}

.rep-header {
  border-bottom: 1px solid #f4f4f4;
  padding: 20px 20px 0px 20px;
}

.rep-tabs ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.rep-tabs ul li {
  display: inline-block;
  padding: 22px 25px 11px 0px;
}

.rep-tabs ul li a {
  color: #50516c;
}

.rep-tabs ul li.active a,
.rep-tabs ul li:hover a {
  color: #25243e;
}

.rep-tabs ul li.active .sh-selection-bar {
  border: 1px solid #ee518f;
  width: 25px;
  margin-top: 10px;
  position: absolute;
}
.screen-editable-name {
  border-radius: 0 !important;
  border: none !important;
  margin: 0 !important;
  vertical-align: top;
  display: inline-block;
  width: 100%;
  font: inherit;
  overflow: hidden;
  background: transparent;
  box-sizing: border-box;
  resize: none;
  border-bottom: 1px dotted !important;
  outline: 0 solid transparent !important;
  padding: 0 0 2px !important;
  transition: border-bottom 80ms ease-in-out;
  color: #3e4553 !important;
  font-size: 14px !important;
  margin-top: 7px !important;
  white-space: nowrap;
  height: 25px;
  letter-spacing: 0.6px;
}
.setup-dialog74 {
  width: 74% !important;
}
</style>
