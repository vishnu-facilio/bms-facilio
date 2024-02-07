<template>
  <div>
    <div v-if="!$validation.isEmpty($getProperty(fieldObjectData, fieldName))">
      <div v-if="fieldObjectData[fieldName].displayType === 'select'">
        <el-select
          v-if="dataTypeEnum === 'BOOLEAN'"
          @change="onFieldInputChange(stringValue)"
          v-model="stringValue"
          collapse-tags
          clearable
          filterable
          placeholder="Select Value"
          class="fc-input-full-border2 width80"
        >
          <el-option
            v-for="(label, value) in fieldObjectData[fieldName].options"
            :key="`option-${value}`"
            :label="label"
            :value="value"
          ></el-option>
        </el-select>
        <el-select
          v-else-if="dataTypeEnum === 'ENUM'"
          v-model="modelValue"
          collapse-tags
          filterable
          placeholder="Select Value"
          class="fc-input-full-border2 width80"
        >
          <el-option
            v-for="(enumValue, index) in fieldObjectData[fieldName].options"
            :key="index"
            :label="enumValue"
            :value="parseInt(index)"
          ></el-option>
        </el-select>
        <el-select
          v-else
          v-model="modelValue"
          collapse-tags
          filterable
          placeholder="Select Value"
          class="fc-input-full-border2 width80"
        >
          <el-option
            v-for="(record, value) in fieldObjectData[fieldName].options"
            :key="`option-${value}`"
            :label="record.label"
            :value="record.value"
          ></el-option>
        </el-select>
      </div>

      <div v-else-if="fieldObjectData[fieldName].displayType === 'currency'">
        <FCurrencyField
          :field="fieldObj"
          :disabled="fieldObj.isDisabled"
          v-model="modelValue"
          @value="updateModelValue"
        ></FCurrencyField>
      </div>

      <div
        v-else-if="fieldObjectData[fieldName].displayType === 'multi_currency'"
      >
        <FNewCurrencyField
          :field="fieldObj"
          :disabled="fieldObj.isDisabled"
          v-model="modelValue"
          :hideCurrency="true"
          @value="updateModelValue"
        ></FNewCurrencyField>
      </div>

      <div v-else-if="fieldObjectData[fieldName].displayType === 'datePicker'">
        <el-date-picker
          @change="onFieldInputChange(stringValue)"
          v-model="stringValue"
          :type="displayTypeEnum === 'DATETIME' ? 'datetime' : 'date'"
          :value-format="'timestamp'"
          class="fc-input-full-border-select2 width100 date-editor"
          style="width: 290px"
        ></el-date-picker>
      </div>

      <div
        v-else-if="fieldObjectData[fieldName].displayType === 'dateRangePicker'"
      >
        <el-date-picker
          @change="onFieldInputChange(dateValue)"
          v-model="dateValue"
          :type="'daterange'"
          :value-format="'timestamp'"
          class="fc-input-full-border-select2 width100 date-editor"
          style="width: 290px"
        ></el-date-picker>
      </div>
      <div v-else-if="displayTypeEnum === 'TIME'">
        <FTimePicker
          v-model="modelValue"
          :key="`${fieldObj.id}`"
          :field="fieldObj"
          :disabled="false"
          @value="updateModelValue"
        ></FTimePicker>
      </div>
      <div
        v-else-if="fieldObjectData[fieldName].displayType === 'durationPicker'"
      >
        <f-duration-field
          :key="fieldObj.id"
          :field="fieldObj"
          @updateDurationValue="updateDurationValue"
        ></f-duration-field>
      </div>
      <div v-else-if="displayTypeEnum === 'GEO_LOCATION'">
        <FLocationField
          v-model="modelValue"
          :key="`${fieldObj.id}`"
          :field="fieldObj"
          :disabled="false"
          :clearable="true"
          @value="updateModelValue"
        ></FLocationField>
      </div>
      <div v-else-if="fieldObjectData[fieldName].displayType === 'siteField'">
        <f-site-field
          class="fc-input-full-border2 width80"
          :model.sync="modelValue"
          :canDisable="false"
          :isClearable="true"
        ></f-site-field>
      </div>
      <template
        v-else-if="
          fieldObjectData[fieldName].displayType === 'lookup' &&
            !fieldObjectData[fieldName].specialType
        "
      >
        <FLookupField
          :key="`${fieldObj.id}`"
          :model="lookupModel.id"
          :field="fieldObj"
          @recordSelected="onRecordSelected"
          @showLookupWizard="showLookupWizard"
        ></FLookupField>
      </template>

      <template v-else-if="fieldObjectData[fieldName].displayType === 'string'">
        <el-input
          v-model="stringValue"
          @input="onFieldInputChange(stringValue)"
          placeholder="Enter the value"
          type="text"
          clearable
          class="fc-input-full-border2 width80"
        ></el-input>
      </template>

      <div
        v-else-if="
          fieldObjectData[fieldName].displayType === 'teamStaffAssignmentPicker'
        "
      >
        <div class="fc-border-input-div2 width80">
          <span>{{ getTeamStaffLabel(teamStaff) }}</span>
          <span style="float: right; padding-right: 12px">
            <img class="svg-icon team-down-icon" src="~assets/down-arrow.svg" />
          </span>
        </div>
        <f-assignment
          :model="teamStaff"
          viewtype="form"
          @value="updateModelValue"
        ></f-assignment>
      </div>

      <div v-else-if="fieldObjectData[fieldName].displayType === 'number'">
        <el-input
          v-model="modelValue"
          type="text"
          placeholder="Enter the value"
          clearable
          class="fc-input-full-border2 width80"
        ></el-input>
      </div>

      <div
        v-else-if="
          ['spaceAssetPicker'].includes(fieldObjectData[fieldName].displayType)
        "
        class="resource-list"
      >
        <el-select
          filterable
          class="multi resource-list el-input-textbox-full-border search-select-comp2 down-arrow-remove search-border-left fc-tag width100"
          multiple
          collapse-tags
          :value="!$validation.isEmpty(modelValue) ? [1] : []"
          disabled
        >
          <el-option :label="spaceAssetFilter" :value="1"></el-option>
        </el-select>
        <i
          @click="spaceAssetProps(fieldObjectData, fieldName)"
          class="el-icon-search serach-icon-picker"
        ></i>
      </div>
    </div>
    <space-asset-multi-chooser
      v-if="chooserVisibility"
      @associate="associateResource($event, fieldObjectData[fieldName])"
      :initialValues="initialValues"
      :visibility.sync="chooserVisibility"
      :hideBanner="true"
      class="fc-input-full-border-select2"
    ></space-asset-multi-chooser>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="fieldObj"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import FAssignment from '@/FAssignment'
import { isSiteField } from '@facilio/utils/field'
import FLookupField from '@/forms/FLookupField'
import FLocationField from '@/forms/FLocationField'
import FDurationField from '@/FDurationField'
import FSiteField from '@/FSiteField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import isEqual from 'lodash/isEqual'
import Constants from 'util/constant'
import cloneDeep from 'lodash/cloneDeep'
import { getFieldOptions } from 'util/picklist'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import FCurrencyField from 'src/components/FCurrencyField.vue'
import FTimePicker from '@/FTimePicker'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'

export default {
  components: {
    FLookupField,
    FLookupFieldWizard,
    SpaceAssetMultiChooser,
    FDurationField,
    FSiteField,
    FAssignment,
    FCurrencyField,
    FLocationField,
    FTimePicker,
    FNewCurrencyField,
  },

  created() {
    Promise.all([
      this.$store.dispatch('loadGroups'),
      this.$store.dispatch('loadUsers'),
    ])
      .then(() => {
        this.setOptions()
      })
      .catch(() => {})

    this.initData()
  },

  props: ['fieldObj', 'value', 'displayTypeEnum', 'dataTypeEnum', 'fieldName'],

  mixins: [TeamStaffMixin],

  data() {
    return {
      fieldObjectData: {},
      lookupModel: {
        id: null,
        label: null,
      },
      stringValue: null,
      dateValue: [],
      setOptionField: {},
      canShowLookupWizard: false,
      chooserVisibility: false,
      duration: {
        days: 0,
        hours: 0,
      },
      teamStaff: {
        assignedTo: {
          id: -1,
        },
        assignmentGroup: {
          id: -1,
        },
      },
    }
  },

  watch: {
    fieldObj(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.initData()
        this.setOptions()
        if (!isEmpty(this.value)) {
          this.stringValue = this.value
        } else {
          this.stringValue = null
        }
      }
    },
    setOptionField: {
      handler: function(newVal) {
        if (!isEmpty(newVal) && !isEmpty(newVal.options)) {
          this.fieldObjectData[this.fieldName].options = newVal.options
        }
      },
      deep: true,
    },
  },

  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
    }),
    spaceAssetFilter() {
      let filters = {}
      return filters
    },

    modelValue: {
      get() {
        return this.value
      },
      set(val) {
        if (!isNaN(val)) {
          this.$emit('update:value', val)
          this.$emit('onValueChange', val)
        }
      },
    },
  },

  mounted() {
    this.constructModelValues()
  },

  methods: {
    constructModelValues() {
      if (!isEmpty(this.value)) {
        if (['STRING', 'BOOLEAN'].includes(this.dataTypeEnum)) {
          this.stringValue = this.value
        } else if (['DATE', 'DATETIME'].includes(this.displayTypeEnum)) {
          this.stringValue = this.value
        } else if (['DATERANGE'].includes(this.displayTypeEnum)) {
          this.dateValue = this.value
        } else if (['GEO_LOCATION'].includes(this.displayTypeEnum)) {
          this.modelValue = this.value
        } else if (['DURATION'].includes(this.displayTypeEnum)) {
          this.modelValue = this.value
          this.$set(this.fieldObj, 'value', this.value)
        } else if (this.dataTypeEnum === 'DECIMAL') {
          this.modelValue = this.value
        } else if (
          !isEmpty(this.fieldObj.field) &&
          this.isLookupDropDownField(this.fieldObj.field)
        ) {
          this.lookupModel.id = this.value
        } else if (this.displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
          this.teamStaff = this.value
        } else if (['CURRENCY'].includes(this.displayTypeEnum)) {
          this.modelValue = this.value
        } else if (['MULTI_CURRENCY'].includes(this.displayTypeEnum)) {
          this.modelValue = this.value
        } else {
          this.modelValue = parseInt(this.value)
        }
      }
    },
    updateDurationValue(field, value) {
      if (!isNaN(value)) {
        this.$emit('update:value', value)
        this.$emit('onValueChange', value)
      }
    },
    setLookupFieldValue(props) {
      let { lookupModel } = this
      let { field } = props
      let { selectedItems } = field || {}
      let [selectedItem] = selectedItems
      if (!isEmpty(selectedItem)) {
        let { value, label } = selectedItem
        this.lookupModel = {
          ...lookupModel,
          id: value,
          label,
        }
      }
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    isLookupDropDownField(field) {
      let { dataTypeEnum, displayTypeEnum } = this
      if (
        !isEmpty(field) &&
        (dataTypeEnum === 'LOOKUP' || displayTypeEnum === 'LOOKUP_SIMPLE')
      ) {
        let { lookupModule } = field
        if (!isEmpty(lookupModule)) {
          let { type } = lookupModule
          return Constants.FIELD_LOOKUP_ENTITY_HASH.includes(type)
        }
      }
      return false
    },
    updateModelValue(value) {
      this.$emit('update:value', value)
      this.$emit('onValueChange', value)
    },
    setDateValue(value) {
      this.$emit('update:value', value.toString())
      this.$emit('onValueChange', value.toString())
    },
    onFieldInputChange(value) {
      this.$emit('update:value', value)
      this.$emit('onValueChange', value)
    },
    initData() {
      let { fieldObj, displayTypeEnum, dataTypeEnum } = this
      let values
      if (!isEmpty(fieldObj) && !isEmpty(displayTypeEnum)) {
        let { field } = fieldObj
        let { name } = fieldObj
        if (!isEmpty(field)) {
          let {
            trueVal,
            falseVal,
            displayName,
            lookupModule,
            specialType,
            enumMap,
          } = field || {}
          values = {
            label: '',
            displayType: 'string',
            value: [],
          }

          if (displayTypeEnum == 'DATE' || displayTypeEnum == 'DATETIME') {
            values.displayType = 'datePicker'
          } else if (displayTypeEnum == 'DATERANGE') {
            values.displayType = 'dateRangePicker'
          } else if (displayTypeEnum == 'DURATION') {
            values.displayType = 'durationPicker'
          } else if (dataTypeEnum == 'STRING') {
            values.displayType = 'string'
          } else if (dataTypeEnum == 'BOOLEAN') {
            values.displayType = 'select'
            values.type = 'Boolean'
            values.options = {
              true: trueVal || 'Yes',
              false: falseVal || 'No',
            }
            values.value = []
          } else if (dataTypeEnum == 'NUMBER' || dataTypeEnum === 'DECIMAL') {
            values.displayType = 'number'
          } else if (dataTypeEnum == 'LOOKUP') {
            if (displayTypeEnum === 'WOASSETSPACECHOOSER') {
              values.displayType = 'spaceAssetPicker'
            } else if (displayTypeEnum === 'GEO_LOCATION') {
              values.displayType = 'Geo Location'
            } else if (isSiteField(fieldObj)) {
              values.displayType = 'siteField'
            } else if (this.isLookupDropDownField(field)) {
              values.displayType = 'lookup'
            } else {
              values.displayType = 'select'
              values.options = []
            }
            values.isCustomFieldLookup = (lookupModule || {}).custom || false

            if (specialType && specialType === 'users') {
              let userOptions = {}

              this.users.forEach(user => {
                userOptions[user.id] = user.name
              })

              values.specialType === true
              values.displayType = 'select'
              values.options = userOptions
            }
          } else if (dataTypeEnum == 'ENUM') {
            values.displayType = 'select'
            values.type = 'enum'
            values.value = []
            values.options = enumMap
          } else if (displayTypeEnum === 'CURRENCY') {
            values.displayType = 'currency'
          } else if (displayTypeEnum === 'TIME') {
            values.displayType = 'time'
          } else if (displayTypeEnum === 'MULTI_CURRENCY') {
            values.displayType = 'multi_currency'
          }

          values.label = displayName

          if (
            [
              'LOOKUP',
              'NUMBER',
              'DECIMAL',
              'ENUM',
              'BOOLEAN',
              'STRING',
            ].includes(dataTypeEnum) ||
            [
              'DATE',
              'DATETIME',
              'DATERANGE',
              'DURATION',
              'CURRENCY',
              'MULTI_CURRENCY',
            ].includes(displayTypeEnum)
          ) {
            // Set only allowed field types
            this.$set(this.fieldObjectData, name, values)
          }
        } else {
          let { name } = fieldObj
          if (isSiteField(fieldObj)) {
            values = {
              displayType: 'siteField',
            }
          } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
            values = {
              displayType: 'teamStaffAssignmentPicker',
            }
          }
          this.$set(this.fieldObjectData, name, values)
        }
      }
    },

    onRecordSelected(filter) {
      if (!isEmpty(filter)) {
        let value = filter.value || ''
        this.lookupModel.id = value
        this.$emit('update:value', value)
        this.$emit('onValueChange', value)
      } else if (isEmpty(filter)) {
        this.lookupModel.id = null
        this.$emit('update:value', null)
        this.$emit('onValueChange', null)
      }
    },

    spaceAssetProps() {
      this.chooserVisibility = true
    },

    async setOption() {
      let fieldObject = this.fieldObjectData
      let { displayTypeEnum, fieldName } = this
      let { name } = this.fieldObj
      let filterObjects = Object.keys(fieldObject)
      if (name === 'frequency') {
        fieldObject[key].options = cloneDeep(this.$constants.FACILIO_FREQUENCY)
        fieldObject[key].options['0'] = 'Once'
      } else if (displayTypeEnum === 'LOOKUP_SIMPLE') {
        let { lookupModuleName } = this.fieldObj
        let field = this.fieldObj
        if (!isEmpty(lookupModuleName)) {
          if (name !== 'siteId' && !this.isLookupDropDownField(field)) {
            let { error, options } = await getFieldOptions({
              field: { lookupModuleName },
            })

            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              field.options = options
            }
          }
        }
      }
      return this.fieldObj
    },

    async setOptions() {
      this.setOptionField = await this.setOption()
    },
  },
}
</script>
<style>
.serach-icon-picker {
  display: flex;
  margin-top: -26px;
  margin-left: 271px;
}
.chooser-date-picker-width {
  width: 176px !important;
}
</style>
