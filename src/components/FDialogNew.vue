<template>
  <el-dialog
    :visible.sync="visible"
    :class="['f-dialog', customClass]"
    :before-close="close"
    :width="width"
    :append-to-body="true"
    class="costdialog"
  >
    <div class="f-dialog-title" slot="title">
      <slot name="header">
        <div class="label-txt-black fwBold">
          {{ title }}
        </div>
      </slot>
    </div>
    <div :style="styleCss" class="f-dialog-content">
      <slot name="content"></slot>
      <slot></slot>
    </div>
    <div class="f-footer row" style="height:46px;">
      <slot name="footer" class="modal-dialog-footer">
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="close">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="save"
            :loading="loading"
            >{{
              loading ? loadingTitle || 'Saving...' : confirmTitle || 'Save'
            }}</el-button
          >
        </div>
      </slot>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: [
    'title',
    'confirmTitle',
    'visible',
    'customClass',
    'width',
    'maxHeight',
    'stayOnSave',
    'loading',
    'loadingTitle',
    'record',
  ],
  computed: {
    styleCss() {
      if (this.maxHeight) {
        return {
          'max-height': this.maxHeight,
          overflow: 'scroll',
        }
      } else {
        return null
      }
    },
  },
  methods: {
    close() {
      this.$emit('close')
      this.$emit('update:visible', false)
    },
    save() {
      this.$emit('save')
      if (!this.stayOnSave) {
        this.$emit('update:visible', false)
      }
    },
  },
}
</script>
<style>
.f-dialog .el-dialog__body,
.f-dialog .el-dialog__header {
  padding: 0px;
}

.f-dialog-title {
  padding: 20px 30px 18px;
  border-bottom: 1px solid #eeeeee;
}

.f-dialog-content {
  padding: 15px 25px 60px;
}

.costdialog .el-icon-close {
  display: none;
}
</style>
