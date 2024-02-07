<template>
  <div v-if="!$validation.isEmpty(tagsArr)" class="tags-container">
    <el-tag v-for="(tag, index) in tagsArr" :key="index" class="tag">
      <div class="pR3">{{ getTagName(tag) }}</div>
      <div class="pR3">{{ tag.operatorDisplayName }}</div>
      <div class="pR5 truncate-text">
        {{ tag.valueStr }}
      </div>
      <InlineSvg
        v-if="!hideClose"
        class="d-flex"
        src="svgs/close-circled"
        iconClass="icon icon-xs self-center mR5 cursor-pointer close-icon"
        @click.native="clearFilter(tag)"
      ></InlineSvg>
    </el-tag>
    <div v-if="!hideActionBtn" class="tags-action">
      <div class="tags-clear" @click="resetFilters">
        {{ $t('filters.tags.clear_all') }}
      </div>
      <div v-if="canSaveView" class="tags-save">
        <span v-if="isSystemView" @click="savingView('new')">{{
          $t('filters.tags.save_view')
        }}</span>
        <el-dropdown v-else @command="savingView">
          <span class="el-dropdown-link">
            {{ $t('filters.tags.save_view_as')
            }}<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item class="text-capitalize" command="new">{{
              $t('filters.tags.save_as_new')
            }}</el-dropdown-item>
            <el-dropdown-item class="text-capitalize" command="edit">{{
              $t('filters.tags.save_to_existing')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script>
import isEqual from 'lodash/isEqual'
import { isEmpty, isArray } from '@facilio/utils/validation'
import FSearchTagMixin from 'newapp/components/search/FSearchTagMixin'
import { mapGetters, mapActions, mapState } from 'vuex'
import { deepCloneObject } from 'util/utility-methods'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import { findRouterForModuleInApp } from '../../viewmanager/routeUtil'
import { getFormatedTime } from '@/mixins/TimeFormatMixin'
import cloneDeep from 'lodash/cloneDeep'

const dateTimeOperator = {
  84: 'Month',
  85: 'Day of Week',
  101: 'Day of Month',
  102: 'Day of Year',
  103: 'Hours of Day',
  106: 'Before N Days',
  107: 'After N Days',
  108: 'Week of Year',
}

export default {
  name: 'fTags',
  props: [
    'showOnlyExisting',
    'hideActionBtn',
    'hideClose',
    'hideSaveView',
    'hideQuery',
    'filterList',
  ],
  mixins: [FSearchTagMixin],
  data() {
    return {
      tagsArr: [],
      operatorsList: [],
      simpleFilters: {},
      oneLevelFilters: {},
      advancedFields: [],
      oneLevelFields: [],
      cachedModule: {},
    }
  },
  created() {
    this.init()
  },
  watch: {
    appliedFilters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.constructTags(newVal)
      }
    },
    filterList: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.constructTags(newVal)
        }
      },
      deep: true,
    },
    fields(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { appliedFilters } = this
        this.constructTags(appliedFilters)
      }
    },
    moduleName: {
      async handler(val) {
        let { appliedFilters } = this
        if (!isEmpty(val)) {
          await this.loadFields()
          this.constructTags(appliedFilters)
        }
      },
      immediate: true,
    },
    operatorsList: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { appliedFilters } = this
          this.constructTags(appliedFilters)
        }
      },
    },
  },
  computed: {
    ...mapGetters({
      getOperatorsList: 'getOperatorsList',
    }),
    ...mapState('view', {
      currentViewDetail: state => state.currentViewDetail,
    }),
    ...mapState('search', {
      currentModuleName: state => state.currentModuleName,
    }),
    canSaveView() {
      let { hideSaveView = false, currentViewDetail } = this
      return !hideSaveView && !isEmpty(currentViewDetail)
    },
    isSystemView() {
      let { currentViewDetail } = this
      let { isDefault } = currentViewDetail
      return !!isDefault
    },
    existingFilters() {
      let { currentViewDetail } = this
      let { filters } = currentViewDetail
      return filters
    },
    moduleName() {
      let { currentViewDetail } = this
      let { moduleName } = currentViewDetail
      return this.$attrs.moduleName || this.currentModuleName || moduleName
    },
    orgCurrency() {
      let { $account } = this
      let { data } = $account
      let { currencyInfo } = data
      let { displaySymbol } = currencyInfo || {}

      return displaySymbol
    },
  },
  methods: {
    ...mapActions({
      loadOperators: 'loadOperators',
      editView: 'view/editView',
    }),
    async init() {
      this.loadGroupViewsList()
      this.loadOperators().then(() => {
        let operatorsList = this.getOperatorsList()
        this.$set(this, 'operatorsList', operatorsList)
      })
    },
    isLookupTypeField(field) {
      let { displayType } = field || {}

      return ['LOOKUP_SIMPLE', 'MULTI_LOOKUP_SIMPLE'].includes(displayType)
    },
    isLookupPopupField(field) {
      let { displayType } = field || {}
      return ['LOOKUP_POPUP'].includes(displayType)
    },
    getTagName(tag) {
      let { fieldDisplayName, parentDisplayName } = tag
      if (!isEmpty(parentDisplayName)) {
        return `${parentDisplayName}/${fieldDisplayName}`
      }
      return fieldDisplayName
    },
    async loadFields() {
      let { moduleName } = this
      let url = `/v2/fields/advancedFilter/${moduleName}`
      let oneLevelUrl = `/v2/fields/advancedFilter/drilldown/${moduleName}?OneLevelFields=true`
      let [advancedFieldResponse, oneLevelFieldResponse] = await Promise.all([
        API.get(url),
        API.get(oneLevelUrl),
      ])
      let {
        data: advancedFieldData,
        error: advancedFieldError,
      } = advancedFieldResponse
      let {
        data: oneLevelFieldData,
        error: oneLevelFieldError,
      } = oneLevelFieldResponse
      if (advancedFieldError || oneLevelFieldError) {
        let { message = 'Error occured' } =
          oneLevelFieldError || advancedFieldError || {}
        this.$message.error(message)
      } else {
        let { fields: advancedFields } = advancedFieldData || {}
        this.advancedFields = advancedFields.map(field => {
          if (this.isLookupPopupField(field) || this.isLookupTypeField(field)) {
            return { ...field, multiple: true, canShowCriteria: false }
          } else {
            return field
          }
        })

        let { fields } = oneLevelFieldData || {}
        let { lookupModuleFields, moduleFields } = fields || {}
        let oneLevelFields = moduleFields.filter(
          field => !isEmpty(field.lookupModuleName)
        )
        oneLevelFields = oneLevelFields.map(field => {
          let { lookupModuleName } = field
          let children = lookupModuleFields[lookupModuleName]
          return { ...field, children, canShowChildren: false }
        })
        this.oneLevelFields = oneLevelFields
      }
    },

    loadGroupViewsList() {
      let { moduleName } = this
      this.isLoading = true
      let data = {
        moduleName,
      }
      this.$store.dispatch('view/loadGroupViews', data).then(() => {
        this.isLoading = false
      })
    },
    async constructTags(filters) {
      let {
        operatorsList,
        showOnlyExisting,
        existingFilters,
        oneLevelFields,
        fields,
      } = this
      let constructedOneLevelFilter = {}
      if (!isEmpty(fields))
        this.advancedFields = fields.map(field => {
          if (this.isLookupPopupField(field) || this.isLookupTypeField(field)) {
            return { ...field, multiple: true, canShowCriteria: false }
          } else {
            return field
          }
        })

      if (isEmpty(filters)) return
      let copiedFilters = cloneDeep(filters)
      let oneLevelFilters = copiedFilters['oneLevelLookup'] || {}
      oneLevelFilters = Object.entries(oneLevelFilters).reduce((acc, curr) => {
        let [parentLookup, childFilters] = curr
        childFilters = childFilters.map(filters => {
          let { criteriaValue } = filters
          return criteriaValue
        })
        return { ...acc, [parentLookup]: childFilters }
      }, {})
      this.oneLevelFilters = oneLevelFilters
      constructedOneLevelFilter = oneLevelFilters
      let oneLevelModules = Object.keys(oneLevelFilters)
      oneLevelModules = [...new Set(oneLevelModules)]
      await this.populateOneLevelModules(oneLevelModules)
      delete copiedFilters['oneLevelLookup']
      this.simpleFilters = copiedFilters
      if (showOnlyExisting) {
        let simpleFilters = Object.entries(existingFilters).filter(obj => {
          let [, value] = obj
          if (Array.isArray(value)) {
            let child = value.filter(obj => obj.operatorId != 35)

            if (child.length > 0) return true
            else return false
          } else {
            return value.operatorId != 35
          }
        })

        simpleFilters = simpleFilters.map(obj => {
          let [key, value] = obj
          if (Array.isArray(value)) {
            let updatedValue = value.filter(filter => filter.operatorId != 35)
            return [key, updatedValue[0]]
          }
          return obj
        })
        this.simpleFilters = Object.fromEntries(simpleFilters)
        let clonedFilter = JSON.parse(JSON.stringify(existingFilters))
        let existingOneLevelFilters = Object.entries(clonedFilter).filter(
          obj => {
            let [, value] = obj
            if (Array.isArray(value)) {
              let child = value.filter(obj => obj.operatorId == 35)

              if (child.length > 0) return true
              else return false
            } else if (value?.operatorId == 35) {
              return true
            }
            return false
          }
        )

        existingOneLevelFilters = existingOneLevelFilters.map(obj => {
          let [key, value] = obj
          if (Array.isArray(value)) {
            let updatedValue = value.filter(filter => filter.operatorId == 35)
            updatedValue = updatedValue.map(obj => obj.value)
            return [key, updatedValue]
          } else if (value?.operatorId == 35) {
            let { value: filterValue } = value
            let [firstKey] = Object.keys(filterValue)
            return [key, [{ [firstKey]: filterValue[firstKey] }]]
          }
          return obj
        })
        let updatedOneLevelfilters = Object.fromEntries(existingOneLevelFilters)
        this.oneLevelFilters = updatedOneLevelfilters
        constructedOneLevelFilter = updatedOneLevelfilters
        let existingOneLevelModules = Object.keys(constructedOneLevelFilter)
        existingOneLevelModules = [...new Set(existingOneLevelModules)]
        await this.populateOneLevelModules(existingOneLevelModules)
      }

      let tagsArr = []
      if (
        !isEmpty(this.simpleFilters) &&
        !isEmpty(operatorsList) &&
        !isEmpty(this.advancedFields)
      ) {
        let list = this.getTagsList(this.simpleFilters, this.advancedFields)
        let { tagsArr: tags, labelMeta, lookupFieldsArr } = list
        tagsArr = tags
        if (!isEmpty(labelMeta)) {
          tagsArr = await this.fetchLookupLabels({
            labelMeta,
            lookupFieldsArr,
            tagsArr,
            fields: this.advancedFields,
            appliedFilters: this.simpleFilters,
          })
        }
      }
      if (
        !isEmpty(constructedOneLevelFilter) &&
        !isEmpty(operatorsList) &&
        !isEmpty(oneLevelFields)
      ) {
        for (let [key, value] of Object.entries(constructedOneLevelFilter)) {
          let childFilters = {}
          if (isArray(value)) {
            childFilters = value.reduce((acc, curr) => {
              let [firstKey] = Object.keys(curr)
              return { ...acc, [firstKey]: curr[firstKey] }
            }, {})
          } else {
            childFilters = value
          }

          let { lookupModuleName, displayName } = oneLevelFields.find(
            field => field.name === key
          )
          let list = this.getTagsList(
            childFilters,
            this.cachedModule[lookupModuleName]
          )
          let { tagsArr: oneLevelChildTags, labelMeta, lookupFieldsArr } = list
          if (!isEmpty(labelMeta)) {
            oneLevelChildTags = await this.fetchLookupLabels({
              labelMeta,
              lookupFieldsArr,
              tagsArr: oneLevelChildTags,
              fields: this.cachedModule[lookupModuleName],
              appliedFilters: childFilters,
            })
          }
          let oneLevelTags = oneLevelChildTags.map(tag => ({
            ...tag,
            parentFieldName: key,
            parentDisplayName: displayName,
          }))
          tagsArr = [...tagsArr, ...oneLevelTags]
        }
      }
      this.$set(this, 'tagsArr', tagsArr)
    },
    async populateOneLevelModules(oneLevelModules) {
      let { oneLevelFields } = this
      if (isEmpty(oneLevelFields)) return

      if (!isEmpty(oneLevelModules)) {
        let lookupModuleNames = oneLevelModules.map(fieldName => {
          let field = oneLevelFields.find(field => field.name === fieldName)
          let { lookupModuleName } = field
          return lookupModuleName
        })

        let response = await Promise.all(
          lookupModuleNames.map(lookupModuleName =>
            API.get(`/v2/filter/advanced/fields/${lookupModuleName}`)
          )
        )
        for (let i in lookupModuleNames) {
          let lookupModuleName = lookupModuleNames[i]
          let { data } = response[i]
          let { fields: oneLevelFields } = data
          this.cachedModule[lookupModuleName] = oneLevelFields
        }
      }
    },
    getTagsList(filters, fields) {
      let tagsArr = []
      let labelMeta = {}
      let lookupFieldsArr = []
      Object.entries(filters).forEach(([key, value]) => {
        let tagObj = {}
        let valueStr = ''
        let { value: valueArr, operatorId } = value || {}
        // send parent field and child field
        let selectedField = this.getSelectedField({
          fields,
          fieldName: key,
        })
        let isLookupField =
          this.isLookupField(selectedField) ||
          this.isLookupPopupField(selectedField) ||
          this.isMultiLookupField(selectedField)
        let selectedOperator = this.getSelectedOperator({
          selectedField,
          operatorId,
        })
        if (isLookupField) {
          labelMeta = this.constructLabelMeta({
            selectedField,
            lookupFieldsArr,
            labelMeta,
            valueArr,
            selectedOperator,
          })
        }
        let operatorDisplayName = this.getOperatorDisplayName({
          operator: selectedOperator,
        })
        let isDefaultOperator = !isEmpty(selectedOperator.defaultValue)
        let { displayName: fieldDisplayName } = selectedField
        if (!isDefaultOperator) {
          valueStr = this.getValueString({
            valueArr,
            selectedField,
            isLookupField,
            operator: selectedOperator,
          })
        }
        tagObj = {
          fieldName: key,
          fieldDisplayName,
          operatorDisplayName,
          valueStr,
        }

        if (key != 'drillDownPattern') tagsArr.push(tagObj)
      })
      return { tagsArr, labelMeta, lookupFieldsArr }
    },

    constructLabelMeta(props) {
      let {
        selectedField,
        lookupFieldsArr,
        labelMeta,
        valueArr,
        selectedOperator = {},
      } = props
      // Have to construct the label meta keys from lookup module name
      // either from the operator or from the field
      let name = this.getLookupModuleName({
        field: selectedField,
        operator: selectedOperator,
      })
      lookupFieldsArr.push(selectedField)
      let selectedModule = labelMeta[name]
      if (isEmpty(selectedModule)) {
        labelMeta[name] = valueArr || []
      } else {
        let ids = labelMeta[name]
        labelMeta[name] = ids.concat(valueArr)
      }
      return labelMeta
    },
    async fetchLookupLabels(props) {
      let {
        labelMeta,
        lookupFieldsArr,
        tagsArr,
        fields,
        appliedFilters,
      } = props
      let { data, error } = await API.post(`/v2/picklist/label`, {
        labelMeta,
      })
      if (error) {
        let { message = 'Error Occurred' } = error
        this.$message.error(message)
      } else {
        let { label } = data || {}
        tagsArr = tagsArr.map(tag => {
          let { fieldName, valueStr } = tag
          let lookupField = lookupFieldsArr.find(
            field => field.name === fieldName
          )
          if (lookupField) {
            // Have to get the value labels from lookup module name
            // either from the operator or from the field
            let field = this.getSelectedField({ fields, fieldName })
            let currentFiter = appliedFilters[fieldName]
            let operatorId = currentFiter.operatorId
            let operator = this.getSelectedOperator({
              selectedField: field,
              operatorId,
            })
            let name = this.getLookupModuleName({ operator, field })
            let moduleLabels = label[name] || []
            let labelStr = this.constructLabelString(valueStr, moduleLabels)
            tag.valueStr = labelStr
          }
          return tag
        })
      }
      return tagsArr
    },
    getLookupModuleName({ operator, field }) {
      let fieldLookupModule = this.$getProperty(field, 'lookupModule.name')
      let operatorLookupModule = this.$getProperty(
        operator,
        'lookupModule.name'
      )
      if (!isEmpty(operatorLookupModule)) {
        return operatorLookupModule
      } else {
        return fieldLookupModule
      }
    },
    getSelectedField(props) {
      let { fields, fieldName } = props
      let selectedField = (fields || []).find(field => field.name === fieldName)
      return selectedField || {}
    },
    getOperatorDisplayName(props) {
      let { operator } = props
      let { tagDisplayName, displayName, operatorId } = operator || {}
      if ([dateTimeOperator[operatorId]].includes(displayName)) {
        return `is`
      }
      return tagDisplayName || displayName
    },
    getSelectedOperator(props) {
      let { operatorsList } = this
      let { operatorId, selectedField } = props
      let { dataType, operators = [] } = selectedField || {}
      let selectedOperator = operatorsList[dataType] || []
      if (!isEmpty(operators)) {
        selectedOperator = selectedOperator.concat(operators)
      }
      return (
        selectedOperator.find(operator => operator.operatorId === operatorId) ||
        {}
      )
    },
    getValueString(props) {
      let { valueArr, selectedField, isLookupField, operator } = props
      let { operatorId, displayName: operatorDisplayName } = operator
      if (!isEmpty(valueArr)) {
        let valueStr = ''
        let isPicklistOrBoolean = this.isPicklistOrBoolean(selectedField)
        let isDateTypeField = this.isDateTypeField(selectedField)
        let isCurrencyField = this.isCurrencyField(selectedField)
        let isTimeField = this.isTimeField(selectedField)
        if (isDateTypeField) {
          valueStr = valueArr.reduce((acc, value, index) => {
            if ([dateTimeOperator[operatorId]].includes(operatorDisplayName)) {
              if (![106, 107, 84, 85].includes(operatorId)) {
                acc += `${value}${
                  value == 1
                    ? 'st'
                    : value == 2
                    ? 'nd'
                    : value == 3
                    ? 'rd'
                    : 'th'
                }`
              } else if (operatorId === 84) {
                let currentMonth = (this.$constants.MONTHS || [])
                  .filter(month => month?.value == value)
                  .map(month => month?.label)
                acc += `${currentMonth}`
              } else if (operatorId === 85) {
                acc += `${this.$constants.WEEK_DAYS[value]}`
              } else {
                acc += value
              }
              acc += index !== valueArr.length - 1 ? ', ' : ' '
              return acc
            } else {
              let dateStr = this.$options.filters.formatDate(
                Number(value),
                true,
                false
              )
              if (!isEmpty(acc)) {
                return `${acc}, ${dateStr}`
              }
              return `${dateStr}`
            }
          }, '')
          if ([dateTimeOperator[operatorId]].includes(operatorDisplayName)) {
            if ([106, 107].includes(operatorId)) {
              let operatorIdKey = { 106: 'Before', 107: 'After' }
              valueStr = `${operatorIdKey[operatorId] || ''} ${valueStr} days`
            } else if (![84, 85].includes(operatorId)) {
              valueStr += `${operatorDisplayName}`
            }
          }
        } else if (isPicklistOrBoolean) {
          let { options } = selectedField
          if (!isEmpty(options)) {
            valueStr = this.constructLabelString(valueArr, options)
          }
        } else if (isLookupField) {
          return valueArr
        } else if (isCurrencyField) {
          let { orgCurrency } = this
          if (valueArr.length === 2) {
            valueStr = `${orgCurrency} ${valueArr[0]}, ${orgCurrency} ${valueArr[1]}`
          } else {
            valueStr = `${orgCurrency} ${valueArr[0]}`
          }
        } else if (isTimeField) {
          return (valueStr = getFormatedTime(valueArr[0]))
        } else {
          valueStr = valueArr.reduce((acc, value) => {
            if (!isEmpty(acc)) {
              return `${acc}, ${value}`
            }
            return `${value}`
          }, '')
        }
        return `"${valueStr}"`
      }
      return ''
    },
    isCurrencyField(field) {
      let { dataType } = field || {}
      return dataType === 'CURRENCY_FIELD'
    },
    isTimeField(field) {
      let { dataType, displayType } = field || {}
      return dataType === 'NUMBER' && displayType === 'TIME'
    },
    constructLabelString(valueArr = [], options) {
      let valueStr = ''
      if (isArray(valueArr)) {
        valueStr = valueArr.reduce((acc, value) => {
          let { label = 'Invalid' } =
            options.find(option => String(option.value) === value) || {}
          if (!isEmpty(acc)) {
            return `${acc}, ${label}`
          }
          return `${label}`
        }, '')
      }
      return valueStr
    },
    isDateTypeField(field) {
      let { displayType } = field || {}
      return ['DATETIME', 'DATE'].includes(displayType)
    },
    isPicklistOrBoolean(field) {
      let { displayType } = field || {}
      return ['SELECTBOX', 'DECISION_BOX'].includes(displayType)
    },
    resetFilters() {
      let { hideQuery, filterList } = this
      this.$store.dispatch('search/resetFilters')
      this.tagsArr = []
      if (hideQuery && !isEmpty(filterList)) {
        this.$emit('resetFilters')
      }
    },
    savingView(command) {
      let { currentViewDetail, moduleName, $route } = this
      let { query = {} } = $route || {}
      let { name: viewname } = currentViewDetail || {}
      let appId = (getApp() || {}).id

      if (command === 'edit') {
        this.openConfirmDialog()
      } else if (command === 'new') {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.VIEW_CREATION) || {}
          if (name)
            this.$router.push({
              name,
              query: {
                ...(query || {}),
                appId,
                saveAsNew: true,
                viewname,
              },
            })
        } else {
          let { name } =
            findRouterForModuleInApp(moduleName, pageTypes.VIEW_CREATION) || {}

          if (name) {
            this.$router.push({
              name,
              params: { moduleName },
              query: {
                ...(query || {}),
                appId,
                saveAsNew: true,
                viewname,
              },
            })
          }
        }
      }
    },
    openConfirmDialog() {
      this.$dialog
        .confirm({
          title: this.$t('viewsmanager.views.edit_view'),
          message: this.$t('filters.views.confirm_edit'),
          rbDanger: true,
          rbLabel: this.$t('common._common.edit'),
        })
        .then(value => {
          if (value) this.saveFiltersInExistingView()
        })
    },
    saveFiltersInExistingView() {
      let {
        currentViewDetail,
        moduleName,
        existingFilters,
        appliedFilters,
      } = this
      let { name, id } = currentViewDetail || {}
      let data = {
        moduleName,
        view: {
          includeParentCriteria: true,
          name,
          id,
        },
      }
      let finalFilterObj = {}
      if (!isEmpty(existingFilters)) {
        finalFilterObj = {
          ...this.existingFilters,
        }
      }
      if (!isEmpty(appliedFilters)) {
        finalFilterObj = {
          ...finalFilterObj,
          ...appliedFilters,
        }
      }
      if (!isEmpty(finalFilterObj)) {
        data.view.filtersJson = JSON.stringify(finalFilterObj)
      }
      this.editView(data)
        .then(data => {
          let { view } = data
          let { name } = view || {}
          this.$router.push({
            params: {
              viewname: name,
            },
            query: {},
          })
          let msg = this.$t('filters.views.edit_success')
          this.$message.success(msg)
        })
        .catch(error => {
          let { message } = error
          this.$message.error(message)
        })
    },
    clearFilter(tag) {
      let { appliedFilters, hideQuery, filterList } = this
      let filters = deepCloneObject(appliedFilters) || {}
      let isPivotPatternPresent = filters['drillDownPattern']
      if (!isEmpty(isPivotPatternPresent)) {
        delete filters['drillDownPattern']
        let newFilter = {}
        let regex = /([-]+1)+$/
        for (let key of Object.keys(filters)) {
          let isDuplicateField = String(key).match(regex)
          if (isEmpty(isDuplicateField)) {
            newFilter[key] = filters[key]
          }
        }
        filters = newFilter
      }
      let { fieldName, parentFieldName } = tag
      if (!isEmpty(parentFieldName)) {
        let filter = filters['oneLevelLookup']
        let parentFilter = filter[parentFieldName]
        parentFilter = parentFilter.filter(filterObj => {
          let { criteriaValue } = filterObj
          let [name] = Object.keys(criteriaValue)
          return name !== fieldName
        })
        if (isEmpty(parentFilter)) {
          delete filter[parentFieldName]
        } else {
          filter[parentFieldName] = parentFilter
        }
      } else {
        delete filters[fieldName]
      }
      if (hideQuery && !isEmpty(filterList)) {
        this.$emit('updateFilters', { filters })
      } else {
        let keys = Object.keys(filters)
        if (
          keys.length == 1 &&
          keys[0] === 'oneLevelLookup' &&
          isEmpty(filters['oneLevelLookup'])
        ) {
          this.$store.dispatch('search/resetFilters')
          this.tagsArr = []
        } else if (!isEmpty(filters)) {
          this.$store.dispatch('search/applyFilters', filters)
        }
      }
    },
  },
}
</script>
<style lang="scss">
.view-tags {
  .tags-container {
    border: none;
    margin: 0px 10px;
    padding: 0px;
  }
}
.tags-container {
  background: #fff;
  border: solid 1px #ececec;
  margin: 10px;
  margin-bottom: 0px;
  padding: 20px 15px 10px;
  display: flex;
  flex-wrap: wrap;
  .tag {
    display: flex;
    border-radius: 3px;
    border: solid 1px #3ab2c2;
    background-color: #f7feff;
    letter-spacing: 0.5px;
    color: #324056;
    font-size: 13px;
    font-weight: 500;
    margin-right: 10px;
    margin-bottom: 10px;
    overflow: hidden;
    .close-icon {
      fill: #bbc5cc;
    }
  }
  .tags-action {
    display: flex;
    border: 1px solid #dae0e8;
    height: 32px;
    .tags-clear,
    .tags-save,
    .tags-save .el-dropdown-link {
      align-self: center;
      padding: 8px;
      text-transform: uppercase;
      letter-spacing: 1px;
      color: #324056;
      font-size: 12px;
      font-weight: 500;
      cursor: pointer;
    }
    .tags-save {
      border-left: 1px solid #dae0e8;
    }
  }
}
</style>
