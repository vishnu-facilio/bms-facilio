<template>
  <el-dialog
    title="CREATE NEW APP"
    :visible="true"
    width="40%"
    :before-close="closeDialog"
    class="fc-dialog-center-container close-icon-hide-dialog"
    :append-to-body="true"
    :close-on-press-escape="true"
    :close-on-click-modal="true"
  >
    <div class="height350 position-relative">
      <el-row>
        <el-col :span="8">
          <div class="fc-lancher-circle-logo">
            <img src="~statics/apps/app.svg" />
          </div>
        </el-col>
        <el-col :span="16">
          <div class="pB20">
            <div class="fc-dark-grey-txt14 pB5">
              {{ $t('panel.panel.appname') }}
            </div>
            <el-input
              placeholder="Please input"
              class="fc-input-full-border2"
            ></el-input>
          </div>
          <div class="pB20">
            <div class="fc-dark-grey-txt14 pB5">
              {{ $t('panel.panel.desq') }}
            </div>
            <el-input
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 2 }"
              resize="none"
              :placeholder="$t('common._common.enter_desc')"
              class="fc-input-full-border-select2"
            ></el-input>
          </div>
          <div class="pB20">
            <div class="fc-dark-grey-txt14 pB5">
              {{ $t('panel.panel.urlapp') }}
            </div>
            <div class="fc-border-input-div p5">
              <div class="app-url-txt">http://sales-integration.com</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="dialogFormVisible = false">{{
        $t('panel.panel.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save">{{
        $t('panel.panel.createapp')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['closeDialog'],
}
</script>
