<template>
  <div class="db-time-filter-manager">
    <el-dialog
      :visible="visibility"
      class="db-timeline-filter-manager-dialog"
      :show-close="false"
      :append-to-body="true"
      title="Dashboard Filters"
      width="35%"
    >
      <div class="body">
        <el-checkbox
          class="mB20"
          style="border: none;"
          v-model="checkBoxModel"
          label="Enable Timeline Filter"
        ></el-checkbox>
        <div class="mB10 default-time-range-txt">
          Default Time Range
        </div>

        <el-select
          :disabled="!checkBoxModel"
          class="fc-input-full-border-h35"
          v-model="selectModel"
          value-key="dateOperator"
        >
          <el-option
            v-for="option in dateOperators"
            :key="option.enumValue"
            :label="option.value"
            :value="option.enumValue"
          >
          </el-option>
        </el-select>
        <div class="margin">
          <el-checkbox
            :disabled="!checkBoxModel"
            class="mB20"
            style="border: none;"
            v-model="FilterModel"
            label="Hide Timeline Filters In Widgets"
          ></el-checkbox>
        </div>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveFilterConfig"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { deepCloneObject } from 'util/utility-methods'
import { dateOperators } from 'pages/card-builder/card-constants'
export default {
  props: ['visibility', 'timelineFilterState'],
  created() {
    let {
      isTimelineFilterEnabled: checkBoxModel,
      hideFilterInsideWidgets: FilterModel,
      dateOperator: selectModel,
    } = this.timelineFilterState
    this.checkBoxModel = checkBoxModel
    this.FilterModel = FilterModel
    this.selectModel = selectModel
  },
  data() {
    return {
      checkBoxModel: null,
      FilterModel: null,
      selectModel: null,
      dateOperators,
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    saveFilterConfig() {
      this.$emit('timelineFilterConfigSave', {
        isTimelineFilterEnabled: this.checkBoxModel,
        hideFilterInsideWidgets: this.FilterModel,
        dateOperator: this.selectModel,
        dateLabel: this.dateOperators.find(e => e.enumValue == this.selectModel)
          .value,
      })
    },
  },
}
</script>

<style lang="scss">
.db-timeline-filter-manager-dialog {
  .el-select {
    width: 250px !important;
  }
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 180px;
    padding: 20px 30px;
  }
  .margin {
    margin-top: 20px;
  }

  .default-time-range-txt {
    font-size: 14px;

    letter-spacing: 0.5px;
    color: #6b7e91;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }
}
</style>
