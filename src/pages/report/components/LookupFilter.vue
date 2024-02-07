<template>
  <el-dialog
    :title="filterConfig ? filterConfig.name : ''"
    :visible.sync="visibility"
    width="40%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :show-close="false"
    :before-close="closeConfigDialog"
    :close-on-click-modal="false"
  >
    <div class="height400 overflow-y-scroll pB80">
      <div
        class="border-bottom4 width100 inline-block"
        v-if="Object.keys(filterConfig.allValues).length <= maxCount"
      >
        <div class="fL">
          <div class="pT10 pB10">
            <el-checkbox @change="selectAll()" v-model="select_All"
              >Select All</el-checkbox
            >
          </div>
        </div>
      </div>

      <div class="clearboth">
        <div
          class="pT20 pB20 pL20"
          v-if="!Object.keys(filterConfig.allValues).length"
        >
          Loading...
        </div>
        <el-row>
          <el-checkbox-group
            class="fdimension-filter-group"
            v-model="selectedValues"
            :max="maxCount"
          >
            <el-checkbox
              v-for="(value, key) in filterConfig.allValues"
              :label="parseInt(key)"
              :key="key"
              >{{ value }}</el-checkbox
            >
          </el-checkbox-group>
        </el-row>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeConfigDialog"
        >CANCEL</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="saveFilter"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'config'],
  watch: {
    config: {
      handler: function(oldValue, newValue) {
        if (this.config === null) {
          this.reset()
        } else {
          this.load()
        }
      },
      deep: true,
    },
  },
  created() {
    if (typeof this.config !== 'undefined' && this.config !== null) {
      this.load()
    }
  },
  data() {
    return {
      filterConfig: null,
      select_All: false,
      selectedValues: [],
      chooseValues: [],
      maxCount: 15,
    }
  },
  methods: {
    load() {
      this.filterConfig = this.$helpers.cloneObject(this.config)
      this.selectedValues = this.filterConfig.chooseValues
      console.log('########### this.selectedValues : ', this.selectedValues)
    },
    selectAll() {
      if (this.select_All === true) {
        let values = []
        for (let field in this.filterConfig.allValues) {
          values.push(parseInt(field))
        }
        this.selectedValues = values
      } else {
        this.selectedValues = []
      }
    },
    reset() {
      ;(this.filterConfig = null),
        (this.selectedValues = []),
        (this.select_All = false)
    },
    saveFilter() {
      this.filterConfig.chooseValues = this.selectedValues
      this.$emit('setLookUpFilterConfig', this.filterConfig)
      this.closeConfigDialog()
    },
    closeConfigDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style>
.fdimension-filter-group .el-checkbox {
  width: 40%;
  overflow: hidden;
  padding: 10px 0;
}
</style>
