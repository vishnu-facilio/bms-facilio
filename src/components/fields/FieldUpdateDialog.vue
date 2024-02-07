<template>
  <div>
    <el-dialog
      :visible="true"
      width="50%"
      title="Field Change"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <div class="height330 overflow-y-scroll pB50">
        <div
          v-for="(fieldMatcher, index) in fieldChange.templateJson.fieldMatcher"
          :key="index"
        >
          <el-row
            class="visibility-visible-actions fc-row-hover pT10 pB10 pL10 pointer"
          >
            <el-col :span="1">
              <div class="criteria-alphabet-block pT6">
                <div class="alphabet-circle">{{ index + 1 }}</div>
              </div>
            </el-col>
            <el-col :span="9" class="pL10">
              <el-select
                v-model="fieldMatcher.field"
                @change="statusFieldName(fieldMatcher, index)"
                filterable
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(field, index) in updateFields"
                  :key="index"
                  :label="field.displayName"
                  :value="field.name"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="9">
              <div class>
                <div v-if="!fieldMatcher.fieldObj">
                  <el-input
                    v-model="fieldMatcher.value"
                    class="fc-input-full-border-select2 mL20 width100"
                  ></el-input>
                </div>
                <div v-else-if="fieldMatcher.isSpacePicker">
                  <el-input
                    v-model="field.parseLabel"
                    disabled
                    class="fc-input-full-border-select2 mL20 width100"
                  >
                    <i
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                </div>
                <div v-else class="flex-middle">
                  <el-input
                    v-if="
                      fieldMatcher.fieldObj[0].dataType === 'NUMBER' ||
                        fieldMatcher.fieldObj[0].dataType === 'DECIMAL'
                    "
                    v-model="fieldMatcher.value"
                    type="number"
                    class="fc-input-full-border-select2 mL20 width100"
                  ></el-input>
                  <el-select
                    v-else-if="
                      fieldMatcher.fieldObj[0].dataTypeEnum._name === 'BOOLEAN'
                    "
                    v-model="fieldMatcher.value"
                    class="fc-input-full-border-select2 mL20 width100pxss"
                  >
                    <el-option
                      :label="
                        fieldMatcher.fieldObj[0].trueVal
                          ? fieldMatcher.fieldObj[0].trueVal
                          : 'Yes'
                      "
                      value="true"
                    ></el-option>
                    <el-option
                      :label="
                        fieldMatcher.fieldObj[0].falseVal
                          ? fieldMatcher.fieldObj[0].falseVal
                          : 'No'
                      "
                      value="false"
                    ></el-option>
                  </el-select>
                  <el-select
                    v-else-if="
                      fieldMatcher.fieldObj[0].dataTypeEnum._name === 'ENUM'
                    "
                    filterable
                    collapse-tags
                    v-model="fieldMatcher.value"
                    class="fc-input-full-border-select2 width100 mL20"
                  >
                    <el-option
                      v-for="(value, key) in picklistOptions[
                        fieldMatcher.field
                      ]"
                      :key="key"
                      :label="value"
                      :value="key"
                    ></el-option>
                  </el-select>
                  <el-row
                    class="mL20"
                    v-else-if="
                      fieldMatcher.fieldObj[0].dataTypeEnum._name === 'DATE' ||
                        fieldMatcher.fieldObj[0].dataTypeEnum._name ===
                          'DATE_TIME'
                    "
                  >
                    <el-col :span="7">
                      <el-input
                        @change="changeDataType(fieldMatcher)"
                        v-model="fieldMatcher.dateObject.days"
                        type="number"
                        class="fc-input-full-border-select2 width75px"
                      ></el-input>
                      <div class="f12 fc-darkgrey text-left pT10 nowrap">
                        Days
                      </div>
                    </el-col>
                    <el-col :span="7" class="pL30">
                      <el-select
                        @change="changeDataType(fieldMatcher)"
                        v-model="fieldMatcher.dateObject.hours"
                        clearable
                        filterable
                        class="fc-input-full-border-select2 width75px"
                        placeholder="Num"
                      >
                        <el-option
                          v-for="index in $constants.HOURS"
                          :label="index"
                          :key="index + 1"
                          :value="index"
                        ></el-option>
                      </el-select>
                      <div class="f12 fc-darkgrey text-left pT10 nowrap">
                        Hours
                      </div>
                    </el-col>
                    <el-col :span="7" class="pL60">
                      <el-select
                        @change="changeDataType(fieldMatcher)"
                        v-model="fieldMatcher.dateObject.minute"
                        placeholder="Num"
                        filterable
                        class="fc-input-full-border-select2 width75px"
                      >
                        <el-option
                          v-for="index in minDuration"
                          :label="index"
                          :key="index + 1"
                          :value="index"
                        ></el-option>
                      </el-select>
                      <div class="f12 fc-darkgrey text-left pT10 nowrap">
                        Mins
                      </div>
                    </el-col>
                  </el-row>
                  <el-select
                    @change="changeDataType(fieldMatcher)"
                    v-else-if="
                      fieldMatcher.fieldObj[0].dataTypeEnum._name ===
                        'LOOKUP' &&
                        fieldMatcher.fieldObj[0].displayType._name ===
                          'LOOKUP_SIMPLE'
                    "
                    filterable
                    collapse-tags
                    v-model="fieldMatcher.valueArray"
                    class="fc-input-full-border-select2 width100 mL20"
                  >
                    <el-option
                      v-for="(value, key) in picklistOptions[
                        fieldMatcher.field
                      ]"
                      :key="key"
                      :label="value"
                      :value="key"
                    ></el-option>
                  </el-select>

                  <el-select
                    @change="changeDataType(fieldMatcher)"
                    v-else-if="
                      fieldMatcher.fieldObj[0].dataTypeEnum._name === 'LOOKUP'
                    "
                    filterable
                    collapse-tags
                    v-model="fieldMatcher.valueArray"
                    class="fc-input-full-border-select2 width100 mL20"
                  >
                    <el-option
                      v-for="(value, key) in picklistOptions[
                        fieldMatcher.field
                      ]"
                      :key="key"
                      :label="value"
                      :value="key"
                    ></el-option>
                  </el-select>
                  <el-input
                    v-else
                    v-model="fieldMatcher.value"
                    class="fc-input-full-border-select2 mL20 width130px"
                  ></el-input>
                </div>
              </div>
            </el-col>
            <el-col :span="5">
              <div class="visibility-hide-actions pT10 mR20 text-right pointer">
                <img
                  src="~assets/add-icon.svg"
                  @click="addRow"
                  class="pointer"
                />
                <img
                  src="~assets/remove-icon.svg"
                  v-if="
                    fieldChange.templateJson.fieldMatcher.length > 1 &&
                      !(fieldChange.templateJson.fieldMatcher.length === 1)
                  "
                  @click="deleteRow(index)"
                  class="pointer mL5"
                />
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="actionSave(fieldChange)"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'statusFieldName',
    'fieldChange',
    'moduleFields',
    'module',
    'picklistOptions',
    'addRow',
    'actionSave',
    'fieldUpdateValue',
    'durationConfig',
  ],

  data() {
    return {
      moduleObj: {},
      excludeFields: ['stateFlowId', 'siteId', 'photo', 'moduleState'],
      updateFieldName: {
        alarm: ['severity'],
        newreadingalarm: ['severity'],
        workorder: [
          'assignedTo',
          'assignmentGroup',
          'category',
          'dueDate',
          'priority',
          'type',
        ],
        asset: [
          'type',
          'model',
          'manufacturer',
          'purchasedDate',
          'retireDate',
          'unitPrice',
          'warrantyExpiryDate',
          'department',
        ],
        dealsandoffers: ['active'],
      },
    }
  },

  computed: {
    updateFields() {
      let fields = []
      let { moduleObj = {}, excludeFields, moduleFields, $getProperty } = this
      let decommissionFields = ['decommission' , 'decommissionedBy' , 'viewRecommissionedBtn', 'commissionedTime']

      if (!isEmpty(moduleFields)) {
        moduleFields = moduleFields.filter(field => !decommissionFields.includes(field.name))
        if (this.updateFieldName[this.module]) {
          fields = moduleFields.filter(
            field =>
              !field.default ||
              (this.updateFieldName[this.module] &&
                this.updateFieldName[this.module].includes(field.name))
          )
        } else {
          fields = moduleFields
        }
      } else if ($getProperty(moduleObj.typeEnum, '_name', '') === 'CUSTOM') {
        fields = moduleFields.filter(
          field => !excludeFields.includes(field.name)
        )
      }
      return fields
    },
    minDuration() {
      let { durationConfig } = this
      if (this.$getProperty(durationConfig, 'minInterval')) {
        let minutes = []
        let { minInterval } = durationConfig
        for (let i = 0; i < 60; i += minInterval) {
          minutes.push(i)
        }
        return minutes
      }
      return this.$constants.MINUTES
    },
  },

  mounted() {
    this.getMetaFields()
  },

  methods: {
    getMetaFields() {
      let { module } = this
      if (module) {
        let url = `/module/meta?moduleName=${module}`
        this.$http.get(url).then(response => {
          this.moduleObj = response.data.meta.module
        })
      }
    },
    changeDataType(criteria) {
      if (
        criteria.fieldObj[0].dataTypeEnum._name === 'DATE_TIME' ||
        criteria.fieldObj[0].dataTypeEnum._name === 'DATE'
      ) {
        if (criteria.dateObject) {
          let interval = this.$helpers.daysHoursMinuToSec(criteria.dateObject)
          criteria.value = interval * 1000 // second to milliseconds
        }
      } else {
        if (criteria.valueArray) {
          // let values = criteria.valueArray.join()
          criteria.value = { id: criteria.valueArray }
        }
      }
      this.$forceUpdate()
    },

    deleteRow(index) {
      this.fieldChange.templateJson.fieldMatcher.splice(index, 1)
    },

    close() {
      this.$emit('update:fieldUpdateValue', false)
    },
  },
}
</script>
