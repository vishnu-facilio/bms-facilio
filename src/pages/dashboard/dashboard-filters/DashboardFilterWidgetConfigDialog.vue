<template>
  <div class="db-widget-filter-manager">
    <el-dialog
      :visible="visibility"
      class="db-widget-filter-manager-dialog"
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
          label="Exclude widget from dashboard filters"
        ></el-checkbox>
        <div class="timeline-filter-mapping" v-if="widget.type == 'view'">
          <div class="mB10 exlude-filter-text">
            Apply timeline filter to
          </div>

          <el-select
            :disabled="checkBoxModel"
            class="fc-input-full-border-h35"
            v-model="selectModel"
            :loading="loadingDateFields"
            loading-text="loading"
            clearable
          >
            <el-option
              v-for="(option, index) in dateFields"
              :key="index"
              :label="option.displayName"
              :value="option.name"
            >
            </el-option>
          </el-select>
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
import { deepCloneObject } from 'util/utility-methods'
import { isDateTypeField } from '@facilio/utils/field'
import { API } from '@facilio/api'
export default {
  components: {},
  props: ['visibility', 'widget'],
  created() {
    let widget = deepCloneObject(this.widget)
    let {
      widgetSettings: {
        excludeDbFilters: checkBoxModel,
        dateField: selectModel,
      },
    } = widget
    this.checkBoxModel = checkBoxModel
    this.selectModel = selectModel
    if (this.widget.type == 'view') {
      //for list widget , need to manually map date field
      this.loadFields()
    }
  },
  data() {
    return {
      checkBoxModel: null,
      selectModel: null,
      dateFields: [],
      loadingDateFields: false,
    }
  },
  methods: {
    async loadFields() {
      this.loadingDateFields = true
      let { data, error } = await API.get(
        `v2/modules/meta/${this.widget.dataOptions.moduleName}`
      )
      if (error) {
        this.$message.error('Error loading meta fields')
        this.loading
      } else {
        this.dateFields = data.meta.fields.filter(e => {
          return isDateTypeField(e)
        })
      }

      this.loadingDateFields = false
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },

    saveFilterConfig() {
      let widgetSettings = {
        excludeDbFilters: this.checkBoxModel,
        dateField: this.selectModel,
      }
      this.$emit('widgetFilterConfigChange', { ...this.widget, widgetSettings })
    },
  },
}
</script>

<style lang="scss">
.db-widget-filter-manager-dialog {
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
    height: 150px;
    padding: 20px 30px;
  }

  .exlude-filter-text {
    font-size: 14px;

    letter-spacing: 0.5px;
    color: #6b7e91;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }
}
</style>
