<template>
  <el-form :inline="true" :model="value" :label-position="'top'">
    <div v-if="value && value.fields">
      <div v-for="(field, index) in value.fields" :key="index">
        <el-row class="module-builder mT20" :gutter="20">
          <el-col :span="12">
            <div class="new-label-text pB10">Fields</div>
            <el-form-item>
              <el-input
                v-model="field.displayName"
                placeholder="Field name"
                class="width250px fc-input-full-border-select2"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12" class="pL0">
            <div class="new-label-text pB10">Fields Types</div>
            <el-form-item>
              <el-select
                :disabled="value && value.disableUneditable"
                v-model="field.dataType"
                class="width250px fc-input-full-border-select2"
              >
                <el-option
                  v-for="(dtype, index) in dataTypes"
                  :key="index"
                  :label="dtype.label"
                  :value="dtype.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <div
              v-if="
                value.includeValidations &&
                  (field.dataType === 2 || field.dataType === 3)
              "
            >
              <div class="new-label-text pB10">Input Pattern</div>
              <el-form-item>
                <el-select
                  v-model="field.inputPattern"
                  @change="forceUpdate"
                  class="width250px fc-input-full-border-select2"
                >
                  <el-option key="none" label="None" value="none"></el-option>
                  <el-option
                    key="incremental"
                    label="Incremental"
                    value="incremental"
                  ></el-option>
                  <el-option
                    key="decremental"
                    label="Decremental"
                    value="decremental"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-checkbox
                  v-model="field.raiseInputPatternAlarm"
                  @change="forceUpdate"
                  >Raise Alarm</el-checkbox
                >
              </el-form-item>
              <div v-show="field.raiseInputPatternAlarm">
                <div class="new-label-text pB10">Alarm Severity</div>
                <el-form-item>
                  <el-select
                    v-model="field.inputPatternSeverity"
                    @change="forceUpdate"
                    class="width250px fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(type, idx) in alarmSeverity"
                      :key="idx"
                      :label="type"
                      :value="type"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="new-label-text">Safe Limit</div>
              <el-form-item label="Minimum Value">
                <el-input
                  v-model="field.minSafeLimit"
                  @change="forceUpdate"
                  class="width250px fc-input-full-border-select2"
                ></el-input>
              </el-form-item>
              <el-form-item label="Maximum Value">
                <el-input
                  v-model="field.maxSafeLimit"
                  class="width250px fc-input-full-border-select2"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-checkbox
                  v-model="field.raiseSafeLimitAlarm"
                  @change="forceUpdate"
                  >Raise Alarm</el-checkbox
                >
              </el-form-item>
              <div v-show="field.raiseSafeLimitAlarm">
                <div class="new-label-text pB10">Alarm Severity</div>
                <el-form-item>
                  <el-select
                    v-model="field.safeLimitSeverity"
                    @change="forceUpdate"
                    class="width250px fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(type, idx) in alarmSeverity"
                      :key="idx"
                      :label="type"
                      :value="type"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </div>
            <el-form-item class="module-builder-close">
              <el-button
                v-if="value && !value.disableUneditable"
                @click="deleteRow(index)"
                type="text"
                class="f18"
                icon="el-icon-close"
              ></el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-button
      v-if="value && !value.disableUneditable"
      @click="addRow"
      type="info"
      plain
      icon="el-icon-plus"
      class="btn-modal-fill mT20"
      >Add Field</el-button
    >
  </el-form>
</template>
<script>
export default {
  props: ['value'],
  data() {
    return {
      alarmSeverity: ['Critical', 'Major', 'Minor'],
      dataTypes: [
        {
          label: 'Text',
          value: 1,
        },
        {
          label: 'Number',
          value: 2,
        },
        {
          label: 'Decimal',
          value: 3,
        },
        {
          label: 'Boolean',
          value: 4,
        },
      ],
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    addRow() {
      this.value.fields.push({
        raiseSafeLimitAlarm: false,
        raiseInputPatternAlarm: false,
        displayName: '',
        dataType: 1,
        minSafeLimit: null,
        maxSafeLimit: null,
        safeLimitSeverity: 'Minor',
        inputPatternSeverity: 'Minor',
        inputPattern: 'none',
        inputPatternId: -1,
        safeLimitId: -1,
      })
    },
    deleteRow(index) {
      if (index && this.value && this.value.fields) {
        this.value.fields.splice(index, 1)
      }
    },
    forceUpdate() {
      this.$forceUpdate()
    },
  },
}
</script>
<style>
.el-icon-clos {
  color: #000;
  font-size: 16px;
  font-weight: bold;
}
.module-builder-close {
  margin-right: 0 !important;
}
.module-builder .el-form-item {
  margin-bottom: 0;
}
</style>
