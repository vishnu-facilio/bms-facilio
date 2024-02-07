<template v-if="loading">
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :title="$t('rule.create.add_impact')"
    custom-class="setup-dialog40 fc-dialog-center-container fc-dialog-center-body-p0"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="height150 p20">
      <el-select
        v-model="impact"
        placeholder="Select Impact"
        collapse-tags
        class="fc-input-full-border-select2 width100 fc-tag"
      >
        <el-option
          v-for="impact in impactList"
          :key="impact.value"
          :label="impact.label"
          :value="impact.value"
        >
        </el-option>
      </el-select>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('alarm.alarm.cancel')
        }}</el-button>
        <el-button
          ref="formbuilder"
          class="modal-btn-save"
          @click="addImpact"
          >{{ $t('alarm.alarm.save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
export default {
  name: 'NewImpact',
  data() {
    return {
      impact: null,
      loading: false,
    }
  },
  props: ['closeDialog', 'selectedImpact', 'impactList', 'isV2'],
  mounted() {
    if (this.isV2) {
      const interval = setInterval(() => {
        if (this.$refs.formbuilder) {
          this.addbuttonRef()
          clearInterval(interval)
        }
      }, 50)
      this.loading = true
    } else {
      this.loading = true
    }
  },
  methods: {
    addbuttonRef() {
      this.$refs.formbuilder.$el.classList.value = 'fc-v2-btn'
    },
    addImpact() {
      this.$emit('addImpact', this.impact)
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss">
.fc-v2-btn {
  width: 50%;
  padding-top: 18px;
  padding-bottom: 18px;
  cursor: pointer;
  border: transparent;
  letter-spacing: 1.1px;
  text-align: center;
  color: #ffffff;
  text-transform: uppercase;
  font-weight: 500;
  border-radius: 0;
  float: right;
  line-height: 16px;
  cursor: pointer;
  background-color: #0059d6 !important;
  &:hover {
    background-color: #0074d1;
    color: #ffffff;
  }
  &:active {
    color: #fff;
    background-color: #0074d1;
    border: transparent;
  }
}
.fc-v2-color {
  background-color: #0059d6 !important;
  color: #fff !important;
}
</style>
