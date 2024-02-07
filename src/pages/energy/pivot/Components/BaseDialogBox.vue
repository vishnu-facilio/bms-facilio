<template>
  <div>
    <el-dialog
      custom-class="base-dialog-box"
      v-if="visibility"
      :title="title"
      :visible.sync="visibility"
      :append-to-body="true"
      :show-close="false"
      :close-on-click-modal="false"
      :width="width"
      :destroy-on-close="true"
    >
      <div
        class="title-slot"
        slot="title"
        :style="{ display: hideHeader ? 'none' : 'block' }"
      >
        <div class="title-div">
          {{ title }}
        </div>
        <hr />
      </div>
      <div class="dialog-body">
        <slot name="body"></slot>
      </div>
      <span
        slot="footer"
        :class="isAlert ? 'dialog-footer-alert' : 'dialog-footer'"

      >
        <el-button
          :disabled="disableConfirm"
          type="el-button  el-button--primary"
          @click="onConfirm"
          class="mR10 dialog-btn confirm-btn"
          >{{ confirmText }}</el-button
        >
        <el-button
          :disabled="disableCancel"
          @click="onCancel"
          class="mL10 dialog-btn cancel-btn"
          >{{ cancelText }}</el-button
        >
        <el-button
          class="clear-all-btn"
          v-if="onClearAll"
          type="text"
          style="margin-left: 20px !important;
          text-decoration: underline;"
          @click="onClearAll"
          >Clear all</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: [
    'title',
    'visibility',
    'width',
    'onCancel',
    'cancelText',
    'onConfirm',
    'confirmText',
    'disableCancel',
    'disableConfirm',
    'hideHeader',
    'onClearAll',
    'isAlert',
  ],
}
</script>

<style lang="scss">
.base-dialog-box {
  border-radius: 10px;
  margin-top: 8vh !important;

  .el-dialog__header {
    padding: 0px;
  }
  .el-dialog__footer{
    border-top:1px solid #e7e7e7;
  }

  .setup-el-btn {
    text-transform: none !important;
    font-weight: 500;
    width: 80px !important;
    height: 36px !important;
    padding: 0px 10px !important;
  }

  .confirm-btn {
    background-color: rgb(57, 178, 194) !important;
    font-size: 13px;
  }

  .cancel-btn {
    font-size: 13px;
  }

  .title-div {
    padding: 20px 20px 10px;
    font-size: 16px;
    font-weight: 500;
    margin-left: 19px;
  }

  hr {
    border-top: 1px solid #f3f2f2;
  }

  .dialog-footer {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    padding-bottom: 10px;
    padding-top: 10px;
    padding-right: 20px;
  }
  .dialog-footer-alert {
    display: flex;
    flex-direction: row;
    justify-content: center;
    padding-bottom: 10px;
    padding-top: 10px;
    padding-right: 20px;
  }
  .dialog-btn {
    width: 80px;
    height: 36px;
    padding: 0px 10px;
  }

  .el-dialog__body {
    padding: 0px 20px;
  }
}

.dialog-footer > button:not(.cancel-btn):not(.clear-all-btn):hover {
  box-shadow: 0 1px 2px 0 rgb(57, 178, 194) !important;
}
.dialog-footer > button:not(.confirm-btn):not(.clear-all-btn):hover {
  border-color: #409EFF !important;
}
.dialog-footer-alert > button:not(.cancel-btn):not(.clear-all-btn):hover {
  box-shadow: 0 1px 2px 0 rgb(57, 178, 194) !important;
}

.dialog-footer-alert > button:not(.confirm-btn):not(.clear-all-btn):hover {
  border-color: #409EFF !important;
}

.dialog-body {
  padding: 0px 20px;
}
.clear-all-btn {
  &:hover {
    box-shadow: none !important;
  }
}
</style>
