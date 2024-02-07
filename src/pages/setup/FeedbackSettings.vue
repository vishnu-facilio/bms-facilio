<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common._common.feedback_settings') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.customize_feedback_settings') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions">
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'feedbackTypes'"
          @click="$refs['feedbackTypeTab'].openAddFeedbackDialog()"
          >{{ $t('common._common.add_feedback_type') }}</el-button
        >
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          v-show="activeName === 'devices'"
          @click="$refs['deviceTab'].openAddDeviceDialog()"
        >
          {{ $t('common._common.add_feedback_kiosk') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll overflow-y">
      <div class="row setting-Rlayout overflow-y">
        <el-tabs v-model="activeName" class="width100">
          <el-tab-pane
            :label="$t('common._common.feedback_types')"
            name="feedbackTypes"
          >
            <feedback-type-list
              v-if="activeName === 'feedbackTypes'"
              ref="feedbackTypeTab"
            ></feedback-type-list>
          </el-tab-pane>
          <el-tab-pane :label="$t('common._common.devices')" name="devices">
            <feedback-device
              ref="deviceTab"
              v-if="activeName == 'devices'"
            ></feedback-device>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>
<script>
import feedbackTypeList from 'src/pages/setup/FeedbackTypeList'

import feedbackDevice from 'src/pages/setup/FeedbackDevice'

export default {
  created() {},
  data() {
    return {
      activeName: 'devices',
    }
  },
  components: {
    feedbackTypeList,
    feedbackDevice,
  },
  methods: {},
}
</script>
<style></style>
