<template>
  <div class="planner-settings">
    <div class="fc-input-label-txt pB10 mT20">Select View</div>
    <el-select
      class="fc-input-full-border2 width100"
      v-model="selectedSettingsView"
    >
      <el-option
        v-for="(view, index) in views"
        :key="index"
        :value="view"
        :label="$t(`maintenance.planner.views.${view}`)"
      >
      </el-option>
    </el-select>
    <div class="mT20">
      <div class="fc-input-label-txt pB10">Select Gridlines</div>
      <el-select
        class="grid-select fc-input-full-border2 width100"
        v-model="selectedViewState.gridLines"
      >
        <el-option
          v-for="(option, index) in gridLineOptions"
          :key="index"
          :value="option"
          :label="$t(`maintenance.planner.views.${option}`)"
        >
        </el-option>
      </el-select>
    </div>
    <div class="mT20">
      <div class="fc-input-label-txt pB10">Select Grouplines</div>
      <el-select
        class="group-select fc-input-full-border2 width100"
        v-model="selectedViewState.grouping"
      >
        <el-option
          v-for="(option, index) in groupingOptions"
          :key="index"
          :value="option"
          :label="$t(`maintenance.planner.views.${option}`)"
        >
        </el-option>
      </el-select>
    </div>
    <div class="mT20">
      <div class="fc-input-label-txt pB10">Select Label to show in grid</div>
      <el-select
        class="label-select fc-input-full-border2 width100"
        v-model="selectedViewState.displayType"
      >
        <el-option
          v-for="(option, index) in labelOptions"
          :key="index"
          :value="option"
          :label="$t(`maintenance.planner.display_type.${option}`)"
        ></el-option>
      </el-select>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="$emit('close')" class="modal-btn-cancel"
        >Cancel
      </el-button>
      <el-button @click="saveSettings" class="modal-btn-save ">Save </el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['settings', 'selectedView', 'views'],
  computed: {
    selectedViewState() {
      return this.viewSettings[this.selectedSettingsView]
    },
    gridLineOptions() {
      switch (this.selectedSettingsView) {
        case 'YEAR':
          return ['WEEK', 'MONTH']
        case 'MONTH':
          return ['DAY']
        case 'WEEK':
          return ['DAY']
        case 'DAY':
          return ['1H', '4H']
      }

      return null
    },
    groupingOptions() {
      switch (this.selectedSettingsView) {
        case 'YEAR':
          return ['MONTH', 'QUARTER', 'NONE']
        case 'MONTH':
          return ['WEEK', 'NONE']
        case 'WEEK':
          return ['NONE']
        case 'DAY':
          return ['4H', 'NONE']
      }
      return null
    },
  },
  data() {
    return {
      labelOptions: ['subject', 'dateTime', 'frequency'],
      selectedSettingsView: null,
      viewSettings: null,
    }
  },
  created() {
    //make a copy of settings state , only saved  from parent when 'save' clicked
    this.viewSettings = this.$helpers.cloneObject(this.settings)

    this.selectedSettingsView = this.selectedView
  },
  methods: {
    saveSettings() {
      this.$emit('saveSettings', this.viewSettings)
    },
  },
}
</script>

<style lang="scss" scoped>
.planner-settings {
  display: flex;
  flex-direction: column;
}
</style>
