<template>
  <!-- all settings components in planner have close and save event , wrap el dialog close and cancel button action both and emit a close to parent,on save emit the mutated new object -->
  <el-dialog
    @close="$emit('close')"
    title="Calendar Settings"
    visible
    width="30%"
    class="fc-dialog-center-container fc-dialog-center-body-p0"
    append-to-body
  >
    <div class="height350 p20 overflow-y-scroll pB50">
      <el-checkbox-group v-model="selectedMetrics" v-bind:min="1">
        <draggable v-model="timeMetricOptions">
          <el-checkbox
            v-for="(column, index) in timeMetricOptions"
            :label="column.name"
            :key="index"
            class="label-txt-black flex-middle p10 fc-input-div-full-border bold mB20 visibility-visible-actions mB10"
          >
            <el-input
              ref="metricInput"
              v-if="editedOption && editedOption.name == column.name"
              v-model="metricNameInput"
              @blur="renameMetric"
              @keyup.native.enter="renameMetric"
            >
            </el-input>
            <span v-else @dblclick="handleDbclick(column)">
              {{ column.displayName }}
            </span>
            <InlineSvg
              src="svgs/drag-and-drop"
              iconClass="icon icon-md fc-grey-svg-color"
              class="pm-planner-col-icon cursor-drag visibility-hide-actions"
            ></InlineSvg>
          </el-checkbox>
        </draggable>
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
import draggable from 'vuedraggable'
export default {
  props: {
    settings: {
      type: Array,
    },
  },
  components: {
    draggable,
  },
  created() {
    this.timeMetricOptions = this.$helpers.cloneObject(this.settings)
    this.selectedMetrics = this.timeMetricOptions
      .filter(e => e.enabled)
      .map(e => e.name)
    //tick items that are selected from response
  },
  data() {
    return {
      selectedMetrics: [],
      timeMetricOptions: [],
      metricNameInput: '',
      editedOption: null,
      dragOptions: {
        handle: '.icon-right',
        ghostClass: 'drag-ghost',
        dragClass: 'custom-drag',
        animation: 150,
      },
    }
  },
  methods: {
    handleDbclick(column) {
      this.editedOption = column
      this.metricNameInput = column.displayName
      this.$nextTick(() => {
        this.$refs['metricInput'][0].focus()
      })
    },
    renameMetric() {
      if (!this.editedOption) {
        return //blur called after enter
      }
      console.log('blurred')
      if (this.metricNameInput == '') {
        alert('name cannot be empty')
      } else {
        this.editedOption.displayName = this.metricNameInput
        this.editedOption = null
      }
    },
    handleSave() {
      this.timeMetricOptions.forEach(e => {
        if (this.selectedMetrics.indexOf(e.name) != -1) {
          e.enabled = true
        } else {
          e.enabled = false
        }
      })
      this.$emit('save', this.timeMetricOptions)
    },
  },
}
</script>

<style lang="scss" scoped>
.el-checkbox {
  margin-right: 0px; //overriding default margin applied by el on checkbox
}
</style>
