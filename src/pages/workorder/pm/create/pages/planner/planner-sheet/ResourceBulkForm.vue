<template>
  <div class="bulk-form-container fc-hottable-com-page" v-if="loadHot">
    <HotTableWrapper
      :hotTableSettingsData="hotConfig"
      :selectedSiteId="selectedSiteId"
      :pasteDataMap.sync="pasteDataMap"
      :onRowChange="onRowChange"
      ref="hotTableWrapper"
      :showCustomFilter="showCustomFilter"
      @openCustomFilter="openCustomFilter"
      @toggleSelectAll="emitSelection"
    ></HotTableWrapper
    ><AdvancedSearch
      ref="resource-search"
      key="resource-search"
      moduleName="resource"
      moduleDisplayName="Resource"
      :hideQuery="true"
      :onSave="applyResourceFilters"
      :filterList="resourceFilters"
    >
      <template #icon>
        <div></div>
      </template>
    </AdvancedSearch>
  </div>
</template>
<script>
import BulkForm from '@/bulkform/BulkForm'
import range from 'lodash/range'
import cloneDeep from 'lodash/cloneDeep'
import { requiredFieldValidator } from '@/handsontable/base/CustomValidator'
import { isLookupSimple } from '@facilio/utils/field'
import { isChooserTypeField } from 'util/field-utils'
import {
  isObject,
  areValuesEmpty,
  isEmpty,
  isFunction,
} from '@facilio/utils/validation'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import HotTableWrapper from './PlannerHotTable'

const isChecked = false

export default {
  props: ['resourcePlannerList', 'lookupOptions'],
  extends: BulkForm,
  data: () => ({
    showCustomFilter: true,
    resourceFilters: null,
  }),
  watch: {
    hotConfig: {
      handler(value) {
        this.$emit('setModelData', value)
      },
      deep: true,
    },
  },
  components: { AdvancedSearch, HotTableWrapper },
  methods: {
    async deserialize() {
      this.isLoading = true
      let {
        form,
        removeFieldsHash,
        isEdit,
        moduleDataId,
        modifySectionPropsHook,
        isLiveSubForm,
      } = this
      let { sections, ruleFieldIds } = form
      if (!isEmpty(ruleFieldIds)) {
        this.$set(this, 'ruleFieldIds', ruleFieldIds)
      }
      let filteredSections = (sections || []).filter(section => {
        let { fields, subFormId } = section

        let filteredFields = (fields || []).filter(field => {
          if (!isEmpty(removeFieldsHash)) {
            return !(removeFieldsHash || []).includes(field.name)
          } else if (field.name === 'photo') {
            return field.hideField !== true
          }
          return true
        })
        section.fields = filteredFields
        return !isEmpty(subFormId) || !isEmpty(filteredFields)
      })
      let deserializedSections = await Promise.all(
        filteredSections.map(async section => {
          let { fields, subFormId, subForm, subFormValue } = section
          let subFormsArr = []
          if (!isEmpty(subFormId)) {
            if (isEdit) {
              let moduleName = this.$getProperty(form, 'module.name')
              let records = await this.loadSubFormRecords(
                section,
                moduleDataId,
                moduleName
              )
              subFormValue = { data: records }
            }
            subFormsArr = this.constructSubFormValues({
              subForm,
              subFormValue,
              isEdit,
              ruleFieldIds,
            })
            this.$set(section, 'subFormsArr', subFormsArr)
          }
          if (!isEmpty(fields)) {
            this.$set(section, 'fields', this.deserializeData(fields))
          }

          if (!isEmpty(section)) this.$set(section, 'hideSection', false)

          if (
            !isEmpty(modifySectionPropsHook) &&
            isFunction(modifySectionPropsHook)
          ) {
            let modifiedSection = modifySectionPropsHook(section)
            if (!isEmpty(modifiedSection))
              section = { ...section, ...modifiedSection }
          }
          return section
        })
      )
      if (isLiveSubForm) this.onChangeHandler({ field: {}, isSubForm: true })
      this.$set(form, 'sections', deserializedSections)
      this.isLoading = false
    },
    applyResourceFilters(filters) {
      this.resourceFilters = filters
      this.$emit('setFilters', { filters })
    },
    initBulkForm() {
      //Bulk form has no section , accumulate fields from all section to single list
      let allFields = this.form.sections.reduce((accum, section) => {
        accum.push(...section.fields)
        return accum
      }, [])
      this.constructHotColumns(allFields)
    },
    openCustomFilter() {
      // let { column } = props
      // let { hotConfig } = this || {}
      // let { columns } = hotConfig || {}
      // console.log(columns[column])
      this.$refs['resource-search'].openCloseSearch(true)
      this.showResourceFilter = true
    },
    constructHotColumns(deserializedFormFields) {
      let { resourcePlannerList } = this || {}
      let supportedFields = deserializedFormFields.filter(formField =>
        this.isBulkModeSupported(formField)
      )

      supportedFields.unshift({ name: '#selectorField' })

      this.hotConfig.colHeaders = supportedFields.map(field => {
        let { displayName, name } = field || {}
        if (name === '#selectorField') {
          return `<div class="selector-field">
            <input type="checkbox" class="bulk-select-checkbox" ${
              isChecked ? `checked="checked"` : ''
            } readonly>
            </div>`
        }

        // Commenting now will be uncommented later
        // if (name === 'resource') {
        //   let svgContent = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../../../../../assets/filter.svg`)
        //   let styledSvg = `<svg class="icon icon-sm filter-icon"`
        //   svgContent = (svgContent || {}).default || ''
        //   svgContent = svgContent.replace(/<svg/, styledSvg)
        //   return `<span class="pR10">${displayName}</span><span class="mT2 cursor-pointer">${svgContent}</span>`
        // } else {
        //   return `${displayName}`
        // }
        return `${displayName}`
      })

      this.hotConfig.dataSchema = cloneDeep(this.formModel)
      this.hotConfig.data = range(0, 25).map((_, i) => {
        let model = resourcePlannerList[i] || this.formModel
        return cloneDeep({
          ...model,
          isSelected: false,
          visitorPhone: 'sample',
        })
      })

      //get appropriate column object for each field object
      supportedFields.forEach(formField => {
        this.hotConfig.columns.push(this.getHotColumn(formField))
      })
    },
    getHotColumn(field) {
      let { name, displayTypeEnum, required } = field

      let columnObj = {}

      if (isObject(this.formModel[name])) {
        columnObj['data'] = `${name}.id` //map nested object schema to handson column
      } else {
        columnObj['data'] = `${name}`
      }

      //Can be type:'systemTypeString' or type:{renderer,validator,editor} for customtypes'
      //define props for column type and then mix into column object
      let columnTypeObj = {}

      if (name === '#selectorField') {
        columnTypeObj.type = 'checkbox'
      } else if (
        displayTypeEnum === 'TEXTBOX' ||
        displayTypeEnum === 'TEXTAREA'
      ) {
        columnTypeObj.renderer = 'input'
      } else if (
        displayTypeEnum === 'NUMBER' ||
        displayTypeEnum === 'DECIMAL'
      ) {
        columnTypeObj.type = 'numeric'
      } else if (displayTypeEnum === 'DATETIME') {
        columnTypeObj.editor = 'picker'
        columnTypeObj.pickerType = 'datetime'
        columnTypeObj.renderer = 'picker'
        columnTypeObj.width = 200
      } else if (isLookupSimple(field) || isChooserTypeField(field)) {
        columnObj.editor = 'lookup'
        columnObj.renderer = 'lookup'
        columnObj.cellType = 'lookup'
        columnObj.field = field
        if (['resource', 'assignedTo', 'jobPlan'].includes(name)) {
          let { lookupOptions } = this || {}
          let options = lookupOptions[name]
          let optionsChache = {}
          options.forEach(option => {
            let { label, value } = option
            optionsChache[value] = label
          })
          columnObj.field.optionsCache = optionsChache
          columnObj.field.options = options
        } else {
          columnObj.field.optionsCache = {}
        }
      } else if (this.isDropdownTypeFields(field.displayTypeEnum)) {
        columnTypeObj.renderer = 'select'
        columnTypeObj.editor = 'select'
        columnObj.cellType = 'select'
        columnTypeObj.options = field.options
      } else if (displayTypeEnum === 'DECISION_BOX') {
        columnTypeObj.type = 'checkbox'
      }

      columnObj = { ...columnObj, ...columnTypeObj }
      columnObj.required = required
      if (required) {
        columnObj.validateFn = requiredFieldValidator
      }

      return columnObj
    },
    onRowChange(props) {
      let { $refs, hotConfig } = this
      let { data } = hotConfig || {}
      let { index } = props
      let selectedRowData = data[index] || {}
      let { ['uuid']: uuid, ...dataWithoutId } = selectedRowData || {}
      let canClearError = areValuesEmpty(dataWithoutId)
      if (!isEmpty($refs) && canClearError) {
        $refs['hotTableWrapper'].clearRowError(uuid)
      }
      this.$emit('onRowChange', { data: data[index] })
    },
    toggleSelectAll(count, selection) {
      let { hotConfig } = this || {}
      let { data } = hotConfig || {}
      data = data.map((row, index) => {
        if (index < count) return { ...row, '#selectorField': selection }
        else return row
      })
      this.$set(hotConfig, 'data', data)
      data.forEach(row => {
        this.$emit('onRowChange', { data: row })
      })
    },
    emitSelection() {
      this.$emit('toggleSelectAll')
    },
  },
}
</script>

<style lang="scss">
.bulk-select-checkbox {
  pointer-events: none;
}
</style>
