<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.vendor_kiosk') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions">
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'kioskButton'"
          @click="$refs['buttonTypeTab'].openAddButtonDialog()"
          >{{ $t('common._common.add_kiosk_button') }}</el-button
        >
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'customKiosk'"
          @click="$refs['deviceTab'].openAddDeviceDialog()"
        >
          {{ $t('common._common.add_vendor_kiosk') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <el-tabs v-model="activeName" class="width100">
          <el-tab-pane
            :label="$t('common._common.kiosk_button')"
            name="kioskButton"
          >
            <kioskButtonList
              v-if="activeName === 'kioskButton'"
              ref="buttonTypeTab"
            ></kioskButtonList>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('common._common.vendor_kiosk')"
            name="customKiosk"
          >
            <customDevice
              ref="deviceTab"
              v-if="activeName == 'customKiosk'"
            ></customDevice>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>
<script>
import kioskButtonList from 'src/pages/setup/KioskButtonList'
import customDevice from 'src/pages/setup/CustomDevice'

export default {
  created() {},
  data() {
    return {
      activeName: 'customKiosk',
    }
  },
  components: {
    kioskButtonList,
    customDevice,
  },
  methods: {},
}
</script>
<style></style>
