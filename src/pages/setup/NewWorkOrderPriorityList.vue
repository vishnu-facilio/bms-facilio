<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Work Order Priority</div>
        <div class="heading-description">List of all Priority</div>
      </div>
    </div>
    <subheader
      :menu="subheaderMenu"
      parent="app/setup/workordersettings/"
      maxVisibleMenu="5"
    ></subheader>
    <new-f-custom-module-type
      :module="'ticketPriority'"
    ></new-f-custom-module-type>
  </div>
</template>
<script>
import NewFCustomModuleType from '@/NewFCustomModuleType.vue'
import Subheader from '@/Subheader'
// import Constants from 'util/constant'
import Data from './SetupWorkorderMixin'

export default {
  components: {
    NewFCustomModuleType,
    Subheader,
  },
  title() {
    'Work Order Priority'
  },
  mixins: [Data],
  data() {
    // let subheaderMenu = Constants.WORKORDER_SUB_HEADER_MENU
    return {
      // subheaderMenu,
    }
  },
  methods: {},
}
</script>
<style>
.el-icon-close:before {
  content: '';
}
/* .fc-dialog-form .el-dialog__header {
    padding: 0px;
} */
.more-actions:hover {
  background-color: #fafbfc;
  cursor: pointer;
}
.setting-header .add-btn {
  position: fixed;
  right: 0;
  z-index: 111;
}
.user-layout .setting-page-btn {
  top: 7.5rem !important;
}
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
.add-btn {
  position: relative;
  right: 21px;
  top: -47px;
  z-index: 1111;
}
</style>
