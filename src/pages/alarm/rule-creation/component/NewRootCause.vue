<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :title="$t('alarm.rules.add_root_cause')"
    custom-class="setup-dialog40 fc-dialog-center-container fc-dialog-center-body-p0"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="dialog-container">
      <el-select
        v-model="ruleObjs"
        multiple
        placeholder="Select root causes"
        collapse-tags
        class="fc-input-full-border-select2 width100 fc-tag"
      >
        <el-option
          v-for="rule in rulesList"
          :key="rule.value"
          :label="rule.label"
          :value="rule.value"
        >
        </el-option>
      </el-select>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('alarm.alarm.cancel')
        }}</el-button>
        <el-button
          ref="rootcause"
          class="modal-btn-save"
          @click="addRootCause"
          >{{ $t('alarm.alarm.save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from 'util/validation'
export default {
  props: ['closeDialog', 'rulesList', 'rcaSelected', 'isV2'],
  data() {
    return {
      ruleObjs: [],
    }
  },
  mounted() {
    if (this.isV2) {
      const interval = setInterval(() => {
        if (this.$refs.rootcause) {
          this.addbuttonRef()
          clearInterval(interval)
        }
      }, 50)
      this.loading = true
    } else {
      this.loading = true
    }
  },
  created() {
    let { rcaSelected } = this
    if (!isEmpty(rcaSelected)) this.ruleObjs = rcaSelected
  },
  methods: {
    addbuttonRef() {
      this.$refs.rootcause.$el.classList.value = 'fc-v2-btn'
    },
    addRootCause() {
      this.$emit('addRootCause', this.ruleObjs)
      this.closeDialog()
    },
  },
}
</script>

<style scoped>
.dialog-container {
  height: 125px;
  padding: 20px 30px 5px;
}
</style>
