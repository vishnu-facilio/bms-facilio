<template>
  <div class="pivot-add-row">
    <el-dialog
      :visible="visibility"
      class="pivot-add-row-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('pivot.addRow')"
      width="40%"
    >
      <div class="body">
        <spinner v-if="loading" :show="loading" size="80" class=""></spinner>
        <div v-else class="add-row-content">
          <!-- <div class="select-row-type">
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              Select row type
            </div>
            <el-radio v-model="showSubmodule" label="1" @change="loadSubmodules"
              >Lookup</el-radio
            >
            <el-radio v-model="showSubmodule" label="2" @change="loadSubmodules"
              >Submodule</el-radio
            >
          </div> -->
          <div v-if="showSubmodule == 1">
            <div class="module-field-select">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.field') }}
              </div>

              <el-select
                v-model="selectedModuleField"
                filterable
                default-first-option
                placeholder="Select Field"
                class="fc-input-full-border-select2 width75 "
                popper-class="fc-group-select"
                value-key="uuid"
                @change="moduleFieldChanged"
                :disabled="editConfig"
              >
                <el-option
                  v-for="field in mainModuleFields"
                  :key="field.uuid"
                  :label="field.displayName"
                  :value="field.uuid"
                ></el-option>
              </el-select>
            </div>
            <div class="lookup-field-select" v-if="showLookupPropertyDropDown">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.selectLookupField') }}
              </div>

              <el-select
                v-model="selectedLookupField"
                filterable
                default-first-option
                placeholder="Select"
                class="fc-input-full-border-select2 width75 "
                popper-class="fc-group-select"
                value-key="uuid"
                @change="lookupFieldChanged"
              >
                <el-option
                  v-for="field in lookupFieldOptions"
                  :key="field.uuid"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </div>
            <div class="rename-column">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.columnName') }}
              </div>

              <el-input
                v-model="columnName"
                placeholder="Rename"
                class="fc-input-full-border2 width75 mT5 "
              >
              </el-input>
            </div>
            <div v-if="showTimeAggregation">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.datePeriod') }}
              </div>

              <el-select
                v-model="moduleDatePeriod"
                filterable
                placeholder="Select"
                class="fc-input-full-border-select2 width75 mT5 module-date-period-select "
              >
                <el-option
                  v-for="(aggr, index) in timeAggregation"
                  :key="'module-date-period' + index"
                  :label="aggr.name"
                  :value="aggr.value"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div v-else>
            <!-- <div class="submodule-select">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                Select submodule
              </div>
              <el-select
                v-model="selectedSubmoduleId"
                filterable
                default-first-option
                placeholder="Select"
                class="fc-input-full-border-select2 width75 "
                popper-class="fc-group-select"
                value-key="uuid"
                @change="submoduleChange"
              >
                <el-option
                  v-for="field in submoduleList"
                  :key="field.moduleId"
                  :label="field.displayName"
                  :value="field.moduleId"
                >
                </el-option>
              </el-select>
            </div> -->
            <!-- <div class="select-submodule-field">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                Select submodule field
              </div>
              <el-select
                v-model="selectedSubmoduleFieldId"
                filterable
                :disabled="submoduleFields.length == 0"
                default-first-option
                placeholder="Select"
                class="fc-input-full-border-select2 width75 "
                popper-class="fc-group-select"
                value-key="uuid"
                @change="submoduleFieldChange"
              >
                <el-option
                  v-for="field in submoduleFields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </el-select>
            </div> -->
            <!-- <div class="select-sort-field">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                Select sort field
              </div>
              <el-select
                v-model="selectedSortFieldId"
                filterable
                :disabled="submoduleFields.length == 0"
                default-first-option
                placeholder="Select"
                class="fc-input-full-border-select2 width75 "
                popper-class="fc-group-select"
                value-key="uuid"
                @change="sortFieldChange"
              >
                <el-option
                  v-for="field in submoduleFields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </el-select>
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                <el-radio v-model="sortOrder" label="1">Accending</el-radio>
                <el-radio v-model="sortOrder" label="2">Descending</el-radio>
              </div>
            </div> -->
            <div class="rename-column">
              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.columnName') }}
              </div>

              <el-input
                v-model="columnName"
                placeholder="Rename"
                class="fc-input-full-border2 width75 mT5 "
              >
              </el-input>
            </div>
            <!-- <div
              class="criteria-builder fc-grey7-12 f14 text-left line-height25 mT10"
            >
              <new-criteria-builder
                :lookupModuleFieldsList="submoduleFields"
                :key="refreshKey"
                title="Show options that match"
                class="stateflow-criteria"
                ref="criteriaBuilder"
                v-model="criteria"
                :exrule="criteria"
                :module="selectedSubmodule.name"
                @condition="updateCriteria"
              ></new-criteria-builder>
            </div> -->
          </div>
        </div>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button type="primary" class="modal-btn-save mL0" @click="save"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Constants from 'util/constant'
import { v4 as uuid } from 'uuid'
import { defaultColFormat, timeAggregation } from './PivotDefaults'
import { isDateTimeField, isDateField } from '@facilio/utils/field'
// import NewCriteriaBuilder from '@/NewCriteriaBuilder'

export default {
  props: [
    'visibility',
    'pivotBaseModuleName',
    'editConfig',
    'index',
    'formatConfig',
  ],
  // components: {
  //   NewCriteriaBuilder,
  // },
  data() {
    return {
      meta: {},
      moduleField: {},
      selectedModuleField: null,
      lookupField: {},
      selectedLookupField: null,
      loading: true,
      showSubmodule: '1',
      submoduleList: [],
      submoduleFields: [],
      selectedSubmoduleFieldId: null,
      selectedSubmoduleField: {},
      selectedSubmoduleId: null,
      selectedSubmodule: {},
      selectedSortFieldId: {},
      sortOrder: '1',
      criteria: null,
      columnName: null,
      refreshKey: 0,
      showLookupPropertyDropDown: false,
      lookupFieldOptions: [],
      showTimeAggregation: false,
      TimeAggregation: 0,
      moduleDatePeriod: 0,
      timeAggregation,
    }
  },

  created() {
    this.loadRowOptions()
    // this.loadSubmodules()
  },

  methods: {
    async loadSubmodules(value) {
      let { data } = await API.get(
        `v2/report/getDataModuleList?moduleName=${this.pivotBaseModuleName}`
      )
      this.submoduleList = data.modules
      this.selectedSubmoduleId = data.modules[0].moduleId
      this.selectedSubmodule = data.modules[0]
      if (value == '2') {
        this.columnName = ''
      } else {
        this.submoduleChange()
      }
      if (this.editConfig) {
        this.initProps()
      }
    },
    updateCriteria(newValue) {
      if (this.isValidCriteria(newValue)) {
        this.criteria = newValue
      }
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      } //all conditions empty ,invalid criteria
      let areAllConditionsEmpty = false

      areAllConditionsEmpty = Object.keys(criteria.conditions).every(
        conditionKey => {
          let condition = criteria.conditions[conditionKey]
          return isEmpty(condition)
        }
      )

      return !areAllConditionsEmpty
    },
    // sortFieldChange() {
    // },
    getMainField(fields) {
      //get main field or first field
      return fields.find(field => field.mainField) || fields[0]
    },
    isEmpty,

    initProps() {
      let self = this
      // if (this.editConfig.subModuleFieldId) {
      //   this.showSubmodule = '2'
      //   this.selectedSubmoduleFieldId = this.editConfig.subModuleFieldId
      //   this.sortOrder = this.editConfig.sortOrder.toString()
      //   this.selectedSubmoduleField = this.editConfig.field
      //   this.selectedSortFieldId = this.editConfig.sortField.id
      //   this.criteria = this.editConfig.criteria
      //   this.selectedSubmodule = this.submoduleList.find(
      //     el => el.moduleId == self.editConfig.field.moduleId
      //   )
      //   this.selectedSubmoduleId = this.selectedSubmodule.moduleId

      //   // this.submoduleChange()
      //   // this.submoduleFieldChange()
      // } else

      if (this.editConfig.lookupFieldId !== -1) {
        this.moduleField = this.mainModuleFields.find(
          field =>
            field.id == self.editConfig.lookupFieldId &&
            self.editConfig.moduleName == field.lookupModule.name
        )

        this.selectedModuleField = this.moduleField.uuid
        this.moduleFieldChanged(this.selectedModuleField)
        this.lookupField = this.editConfig.field
        this.selectedLookupField = this.editConfig.field.id
        this.lookupFieldChanged(this.lookupField.id)
      } else {
        this.moduleField = this.mainModuleFields.find(
          field => field.id == self.editConfig.field.id
        )
        this.selectedModuleField = this.moduleField.uuid
        this.moduleFieldChanged(this.selectedModuleField)
      }
      this.columnName = this.formatConfig.label
    },

    async loadRowOptions() {
      let resp = await API.get('v2/report/getTabularRowReportFields', {
        moduleName: this.pivotBaseModuleName,
      })
      let { data, error } = resp

      if (error) {
        this.$message.error('Error loading row options ')
      } else {
        //api format getTabularRowReportFields/asset ->meta{asset:fields,relatedModule(vendor):fields}
        //take main module 'asset'.for each lookup field in asset ,fill fields from lookup's module
        let meta = data.meta
        this.meta = meta
        //add a unique uuid to each field, some fields have same name and id, Asset.building and asset.space
        Object.keys(meta).forEach(moduleName => {
          meta[moduleName].forEach(field => {
            field.uuid = uuid()
            if (['site', 'building', 'floor', 'space'].includes(moduleName)) {
              field.aggr = Constants.SPACE_AGGREGATE_OPERATOR.getOperatorId(
                moduleName
              )
            }
          })
        })

        this.mainModuleFields = meta[this.pivotBaseModuleName]

        this.loading = false
        this.moduleField = this.getMainField(this.mainModuleFields)
        this.selectedModuleField = this.moduleField.uuid
        this.columnName = this.moduleField.displayName

        if (this.editConfig) {
          this.initProps()
        }
      }
    },

    async submoduleChange() {
      let self = this
      this.selectedSubmodule = this.submoduleList.find(
        mod => mod.moduleId === self.selectedSubmoduleId
      )
      let { data } = await API.get(
        `/module/metafields?moduleName=${this.selectedSubmodule.name}`
      )
      let result = JSON.parse(JSON.stringify(data))
      this.submoduleFields = result.meta.fields.filter(
        el => el.dataTypeEnum !== 'LOOKUP'
      )
      if (!this.editConfig) {
        this.selectedSubmoduleFieldId = this.submoduleFields[0].id
      }
      this.submoduleFieldChange()
    },
    submoduleFieldChange() {
      let self = this
      this.selectedSubmoduleField = this.submoduleFields.find(
        mod => mod.id == self.selectedSubmoduleFieldId
      )
      if (this.selectedSubmoduleField) {
        this.columnName = this.selectedSubmoduleField.displayName
      }
    },
    moduleFieldChanged() {
      //if selected field is a lookup field , show another dropbox with lookupmodule's fields
      let selectedField = this.selectedModuleField
      this.moduleField = this.meta[this.pivotBaseModuleName].find(
        field => field.uuid == selectedField
      )
      selectedField = this.moduleField
      this.showTimeAggregation =
        isDateTimeField(selectedField) || isDateField(selectedField)
      this.columnName = selectedField.displayName
      this.lookupField = {}
      this.showLookupPropertyDropDown = false

      if (
        selectedField.dataTypeEnum == 'LOOKUP' &&
        this.meta[selectedField.lookupModule.name]
      ) {
        this.lookupFieldOptions = this.meta[selectedField.lookupModule.name]

        this.showLookupPropertyDropDown = true

        this.lookupField = this.getMainField(this.lookupFieldOptions)
        // this.selectedLookupField = this.selectedLookupField.id
      }
    },
    lookupFieldChanged() {
      let selectedField = this.selectedLookupField
      this.lookupField = this.lookupFieldOptions.find(
        el => el.id == selectedField
      )
      selectedField = this.lookupField
      this.columnName = selectedField.displayName
    },
    closeDialog() {
      if (this.editConfig) {
        this.$emit('updateCancel')
      }
      this.$emit('update:visibility', false)
    },
    save() {
      let rowObj = {}
      let formatting = {
        ...defaultColFormat.rowColumn,
        label: this.columnName,
      }
      let self = this
      if (!isEmpty(this.lookupField)) {
        rowObj.field = this.lookupField
        rowObj.lookupFieldId = this.moduleField.id //if lookup's property selected .also send lookup field's id
        rowObj.aggr = this.lookupField.aggr || 0
        rowObj.moduleName = this.moduleField.lookupModule.name
      } else if (this.showSubmodule == '1') {
        rowObj.field = this.moduleField
        rowObj.lookupFieldId = -1
        rowObj.aggr = this.moduleField.aggr || 0
        rowObj.moduleName = this.pivotBaseModuleName
      } else {
        //subModuleFieldId
        rowObj.field = this.selectedSubmoduleField
        rowObj.lookupFieldId = -1
        rowObj.aggr = this.selectedSubmoduleField.aggr || 0
        rowObj.moduleName = this.selectedSubmoduleField.name
        rowObj.subModuleFieldId = this.selectedSubmoduleField.id
        rowObj.criteria = this.criteria
        rowObj.sortField = this.submoduleFields.find(
          el => el.id == self.selectedSortFieldId
        )
        rowObj.sortOrder = parseInt(this.sortOrder)
        // sort order - 1 - acending
        //              2 - descending
      }

      // rowObj.field.aggr = this.timeAggregation
      rowObj.field.aggr = this.moduleDatePeriod

      if (this.editConfig) {
        rowObj.alias = this.editConfig.alias
        this.$emit('update', {
          params: { row: rowObj, formatting },
          index: this.index,
        })
      } else {
        this.$emit('save', { row: rowObj, formatting })
      }
    },
  },
}
</script>

<style lang="scss">
// .pivot-add-row-dialog {
//   .body {
//     height: 250px;
//   }
// }
//
</style>
