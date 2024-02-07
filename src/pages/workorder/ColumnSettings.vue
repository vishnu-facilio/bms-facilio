<template>
  <el-dialog
    @close="$emit('close')"
    :title="$t('common._common.column_settings')"
    visible
    width="30%"
    class="fc-dialog-center-container fc-dialog-center-body-p0"
    append-to-body
  >
    <div class="height350 p20 overflow-y-scroll pB50">
      <el-checkbox-group v-model="selectedColumns">
        <el-checkbox
          v-show="column.name != 'timeMetric'"
          v-for="(column, index) in columnOptions"
          :label="column.name"
          :key="index"
          class="label-txt-black flex-middle p10 fc-input-div-full-border bold mB20 visibility-visible-actions mB10"
          :disabled="column.mandatory"
        >
          {{
            $t(`maintenance.planner.colHeaders.${plannerType}.${column.name}`)
          }}
          <!-- <InlineSvg src="svgs/drag-and-drop" iconClass="icon icon-md fc-grey-svg-color" class="pm-planner-col-icon cursor-drag visibility-hide-actions"></InlineSvg> -->
        </el-checkbox>
      </el-checkbox-group>
      <div class="modal-dialog-footer">
        <el-button @click="$emit('close')" class="modal-btn-cancel "
          >Cancel
        </el-button>
        <el-button @click="handleSave" class="modal-btn-save ">Save </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['settings', 'plannerType'],
  data() {
    return {
      columnOptions: [],
      selectedColumns: [],
    }
  },
  created() {
    this.columnOptions = this.$helpers.cloneObject(this.settings)
    this.selectedColumns = this.columnOptions
      .filter(e => e.enabled)
      .map(e => e.name)
  },
  methods: {
    handleSave() {
      this.columnOptions.forEach(e => {
        if (this.selectedColumns.indexOf(e.name) != -1) {
          e.enabled = true
        } else {
          e.enabled = false
        }
      })
      this.$emit('save', this.columnOptions)
    },
  },
}
</script>

<style lang="scss" scoped>
.el-checkbox {
  margin-right: 0px; //overriding default margin applied by el on checkbox
}
</style>
