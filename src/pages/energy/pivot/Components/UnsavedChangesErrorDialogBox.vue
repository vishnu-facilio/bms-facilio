<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="save"
      :onCancel="cancel"
      :hideHeader="true"
      :isAlert="true"
      cancelText="No"
      confirmText="Yes"
      width="480px"
    >
      <div class="dialog-content-body" slot="body">
        <div class="header">
          Cancel Editing?
        </div>
        <div class="content-body" style="margin-bottom:0px;">
          Changes you made so far will not be saved
        </div>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from './BaseDialogBox.vue'

export default {
  props: ['visibility'],
  components: {
    BaseDialogBox,
  },
  methods: {
    cancel() {
      this.$emit('result', false)
      this.$emit('update:visibility', false)
    },
    save() {
      this.$emit('result', true)
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style land="scss" scoped>
.dialog-content-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-content: center;
  align-items: center;
  padding: 24px 0px;
}
.header {
  font-size: 16px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  padding: 20px;
  color: #324056;
}

.content-body {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  margin-bottom: -20px;
}
.module-text {
  font-size: 14px;
  color: #324056;
}
</style>
<style lang="scss">
.confirm-btn {
  font-size: 13px !important;
  margin-right: 20px !important;
  background: #38b2c2;
  border-color: #38b2c2;
}
.cancel-btn {
  font-size: 13px !important;
}
.dialog-btn {
  width: 89px !important;
  height: 36px !important;
}
</style>
