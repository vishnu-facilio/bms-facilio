<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.header.visitor_setting') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.customize_visitor_settings') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions">
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'visitorTypes'"
          @click="$refs['visitorTypeTab'].openAddVisitorDialog()"
          >{{ $t('common._common.add_visitor_type') }}</el-button
        >
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'devices'"
          @click="$refs['deviceTab'].openAddDeviceDialog()"
          >{{ $t('common._common.add_visitor_kiosk') }}</el-button
        >
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'printers'"
          @click="$refs['printerTab'].openAddPrinterDialog()"
          >{{ $t('common._common.add_printer') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <el-tabs v-model="activeName" class="width100">
          <el-tab-pane
            :label="$t('common.tabs.visitor_types')"
            name="visitorTypes"
          >
            <visitor-type-list
              v-if="activeName === 'visitorTypes'"
              ref="visitorTypeTab"
            ></visitor-type-list>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('common.visitor_forms.visitor_kiosk')"
            name="devices"
          >
            <visitor-device
              ref="deviceTab"
              v-if="activeName == 'devices'"
            ></visitor-device>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('common.visitor_forms.printers')"
            name="printers"
          >
            <printer-list
              ref="printerTab"
              v-if="activeName == 'printers'"
            ></printer-list>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('common.notification._notifications')"
            name="notifications"
          >
            <visitor-notification
              v-if="activeName == 'notifications'"
            ></visitor-notification>
          </el-tab-pane>
          <el-tab-pane :label="$t('common.header.watch_list')" name="watchList">
            <watch-list-settings
              v-if="activeName == 'watchList'"
            ></watch-list-settings>
          </el-tab-pane>
          <el-tab-pane :label="$t('common._common.others')" name="general">
            <module-preference-settings
              v-if="activeName == 'general'"
              :moduleName="'visitor'"
            ></module-preference-settings>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>
<script>
import visitorTypeList from 'src/pages/setup/VisitorTypeList'
import visitorNotification from 'src/pages/setup/VisitorNotifications'
import watchListSettings from 'src/pages/setup/WatchListSettings'
import modulePreferenceSettings from 'src/pages/setup/ModulePreferenceSettings'
import visitorDevice from 'src/pages/setup/VisitorDevice'
import printerList from 'src/pages/setup/PrinterList'

export default {
  created() {},
  data() {
    return {
      activeName: 'visitorTypes',
      dialogVisible: false,
      showForm: false,
      isActive: undefined,
    }
  },
  components: {
    visitorTypeList,
    visitorNotification,
    visitorDevice,
    printerList,
    watchListSettings,
    modulePreferenceSettings,
  },
}
</script>
<style></style>
