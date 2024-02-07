<template>
  <div
    class="d-flex tags-parent-container items-baseline"
    v-if="!$validation.isEmpty(filterForTag)"
  >
    <div class="mT24 mL20 d-flex flex-grow items-baseline">
      <div class="tag-inline">
        <template v-for="(filterObj, filterName) in filterForTag">
          <el-tag
            v-if="checkForDateTimeField(filterName, filterObj)"
            :closable="!disableCloseIcon"
            @close="handleClose(filterName)"
            class="search-filter-tag"
            :key="filterName"
            :disable-transitions="true"
          >
            {{ checkForDateTimeField(filterName, filterObj) }}
          </el-tag>

          <el-tag
            v-else-if="
              [
                'asset',
                'space',
                'resourceId',
                'resource',
                'resourceIdSpace',
                'resourceIdAsset',
              ].includes(filterName)
            "
            :closable="!disableCloseIcon"
            @close="handleClose(filterName)"
            class="search-filter-tag"
            :key="filterName"
            :disable-transitions="true"
          >
            {{ resourceTag(filterObj, filterName) }}
          </el-tag>

          <el-tag
            v-else-if="$validation.isEmpty(filterObj.value)"
            :closable="!disableCloseIcon"
            @close="handleClose(filterName)"
            class="search-filter-tag"
            :key="filterName"
            :disable-transitions="true"
          >
            {{ fieldLabelMap[filterName] }}
            {{ operatorMap[filterObj.operatorId] }}
          </el-tag>

          <el-tag
            v-else-if="filterObj.value.length === 1"
            :closable="!disableCloseIcon"
            @close="handleClose(filterName)"
            class="search-filter-tag"
            :key="filterName"
            :disable-transitions="true"
          >
            {{ fieldLabelMap[filterName] }}
            {{ operatorMap[filterObj.operatorId] }}
            {{ getValue(filterName, filterObj, null) }}
          </el-tag>

          <el-tag
            v-else
            v-for="id in filterObj.value"
            :closable="!disableCloseIcon"
            @close="handleClose(filterName, id)"
            class="search-filter-tag"
            :key="fieldLabelMap[filterName] + id"
            :disable-transitions="true"
          >
            {{ fieldLabelMap[filterName] }}
            {{ operatorMap[filterObj.operatorId] }}
            {{ getValue(filterName, filterObj, Number(id)) }}
          </el-tag>
        </template>
      </div>

      <div
        v-if="!disableFilterAdd && (canShowOptions || showOptions)"
        class="d-flex align-center"
      >
        <div
          class="f13 fc-dark-blue-txt13 pointer mL10 mR10 fwBold"
          @click.stop="showFilterList = true"
        >
          <i class="el-icon-plus fc-dark-blue-txt13 f13 fwBold"></i>
          {{ $t('common._common.add_filter') }}
        </div>

        <el-popover
          v-if="showFilterList"
          :value="true"
          :visibleArrow="false"
          popperClass="add-filter-popover p0"
        >
          <outside-click
            :visibility="true"
            @onOutsideClick="close()"
            class="tag-list-block z-index1234"
          >
            <div class="tag-list-header">
              <el-input
                type="text"
                v-model="addFilter"
                placeholder="Search..."
                class="tag-list-search"
                :autofocus="true"
              />

              <template
                class="tag-list-body"
                v-for="(filterObj, filterName) in filterObjectsData"
              >
                <div
                  :key="filterName"
                  :value="filterName"
                  v-if="
                    addFilter === '' ||
                      filterObj.label
                        .toLowerCase()
                        .startsWith(addFilter.toLowerCase())
                  "
                >
                  <div
                    class="label-txt-black2 pT10 pB10 pL20 pR20 pointer fc-list-active-hover"
                    @click.stop="openFilterCondition(filterName)"
                  >
                    {{ filterObj.label }}
                  </div>
                </div>
              </template>
            </div>
          </outside-click>
        </el-popover>

        <filter-condition
          v-if="showFilterCondition"
          :fieldName="fieldName"
          :isTag="true"
          popClass="popover-position"
          @back="backCondition()"
          @close="showFilterCondition = false"
        ></filter-condition>
      </div>
    </div>

    <div class="mL-auto mT20 mR20 d-flex">
      <div
        v-if="!disableActions && (canShowOptions || showOptions)"
        class="d-flex align-center"
      >
        <template v-if="!disableSaveFilters">
          <el-dropdown @command="savingView" v-if="!viewDetail.isDefault">
            <el-button type="primary" class="saveas-btn">
              {{ $t('common._common.save_filters') }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>

            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="Save">{{
                $t('common._common._save')
              }}</el-dropdown-item>

              <el-dropdown-item command="Save As">{{
                $t('common._common.save_as')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-button v-else class="saveas-btn" @click="savingView('Save As')">{{
            $t('common._common.save_filters')
          }}</el-button>
        </template>

        <div class="clear-filter" @click="resetFilters()">
          {{ $t('common._common.clear_all_filters') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty, isArray } from '@facilio/utils/validation'
import FilterCondition from './FilterCondition'
import OutsideClick from '@/OutsideClick'
import SearchTagMixin from './SearchTagMixin'
import cloneDeep from 'lodash/cloneDeep'

const SPECIAL_OPS = {
  88: 'is',
}

export default {
  name: 'Tags',
  props: [
    'filter',
    'disableFilterAdd',
    'disableCloseIcon',
    'disableActions',
    'showOptions',
    'disableSaveFilters',
  ],
  components: { FilterCondition, OutsideClick },
  mixins: [SearchTagMixin],

  data() {
    return {
      fieldName: null,
      showFilterList: false,
      addFilter: '',
      showFilterCondition: false,
      canShowOptions: false,
    }
  },

  computed: {
    ...mapState({
      config: state => state.search.configData,
      viewDetail: state => state.view.currentViewDetail,
    }),

    isAssetsCategory() {
      let { moduleName = '' } = this.config

      if (moduleName === 'asset') return true
      return false
    },

    filterForTag() {
      return this.filter ? this.filter : this.appliedFilters
    },

    fieldLabelMap() {
      let fieldmap = {}
      let metaFields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(metaFields)) {
        metaFields.forEach(field => {
          fieldmap[field.name] = field.displayName
        })
      }

      if (this.filterObjectsData) {
        let fieldName = Object.keys(this.filterObjectsData)

        fieldName.forEach(field => {
          let filterField = this.$getProperty(this.filterObjectsData, field, {})

          if (!isEmpty(filterField.key)) {
            fieldmap[filterField.key] = filterField.label
          } else {
            fieldmap[field] = filterField.label
          }
        })
      }

      return fieldmap
    },

    operatorMap() {
      let opMap = { ...SPECIAL_OPS }
      let metaOperators = this.$getProperty(this.metaInfo, 'operators', null)

      if (!isEmpty(metaOperators)) {
        let operators = Object.keys(metaOperators)

        operators.forEach(operator => {
          let metaOp = this.$getProperty(metaOperators, operator, {}) || {}
          let opKeys = Object.keys(metaOp)

          opKeys.forEach(key => {
            let operatorObj = this.$getProperty(metaOp, key, {})

            opMap[operatorObj.operatorId] = operatorObj.operator
          })
        })
      }
      return opMap
    },
  },

  watch: {
    appliedFilters: {
      handler(value, oldValue) {
        if (!isEmpty(value)) this.canShowOptions = true
        else this.canShowOptions = false

        if (JSON.stringify(value) !== JSON.stringify(oldValue)) {
          this.setFilterApplied()
          this.resetFilterData()
        }
      },
      immediate: true,
    },

    showFilterList(value) {
      if (value) this.setFilterApplied()
    },
  },

  methods: {
    getValue(field, fieldObj, fieldId) {
      let metaFields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(metaFields)) {
        let filterField = metaFields.find(fld => fld.name === field)
        field = this.getFilterKey(field)

        let fieldVal = this.$getProperty(fieldObj, 'value', [])
        let filterFieldName = this.$getProperty(
          filterField,
          'dataTypeEnum._name',
          ''
        )
        let filterFieldType = this.$getProperty(
          this.filterObjectsData[field],
          'displayType',
          ''
        )
        let filterOptions = this.$getProperty(
          this.filterObjectsData[field],
          'options',
          {}
        )

        if (fieldId && isEmpty(fieldVal)) {
          return ''
        } else if (
          fieldObj.operatorId === 88 ||
          (filterFieldName === 'LOOKUP' && filterFieldType === 'lookup')
        ) {
          return isArray(fieldObj)
            ? fieldObj[0].selectedLabel
            : fieldObj.selectedLabel
        } else if (
          filterFieldName === 'STRING' ||
          filterFieldType === 'string'
        ) {
          return fieldVal[0]
        } else if (
          ['NUMBER', 'DECIMAL', 'ID'].includes(filterFieldName) &&
          !['siteId', 'frequency'].includes(field)
        ) {
          return fieldVal[0].toString()
        } else if (fieldId) {
          return filterOptions[fieldId]
        } else {
          return filterOptions[fieldVal[0]]
        }
      } else {
        return ''
      }
    },

    getFilterKey(field) {
      let metaName = this.$getProperty(this.metaInfo, 'name', '')
      let fieldMap = {
        preventivemaintenance: {
          type: 'ticketType',
          typeId: 'ticketType',
          categoryId: 'category',
          assignedToId: 'assignedTo',
          priorityId: 'priority',
          tenantId: 'tenant',
        },
        workorder: {
          type: 'ticketType',
          typeId: 'ticketType',
        },
        isAssetsCategory: {
          category: 'assetCategory',
          type: 'assetType',
        },
        department: 'assetDepartment',
        alarmSeverityId: 'severity',
        assetCategoryId: 'assetCategoryId',
      }

      if (
        ['preventivemaintenance', 'workorder'].includes(metaName) &&
        this.hasFieldInMap(field, fieldMap[metaName])
      ) {
        field = fieldMap[metaName][field]
      } else if (
        this.isAssetsCategory &&
        this.hasFieldInMap(field, fieldMap['isAssetsCategory'])
      ) {
        field = fieldMap['isAssetsCategory'][field]
      } else if (this.hasFieldInMap(field, fieldMap)) {
        field = fieldMap[field]
      }

      return field
    },

    hasFieldInMap(field, fieldMap) {
      return this.$getProperty(fieldMap, field, null)
    },

    checkForDateTimeField(field, fieldObject) {
      let fieldObj = isArray(fieldObject) ? fieldObject[0] : fieldObject
      let metaFields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(metaFields)) {
        let dateField = metaFields.find(fld => fld.name === field)

        if (!isEmpty(dateField)) {
          let dateFieldName = this.$getProperty(
            dateField.dataTypeEnum,
            '_name',
            ''
          )

          if (['DATE', 'DATETIME', 'DATE_TIME'].includes(dateFieldName)) {
            let dateVal = this.$getProperty(fieldObj, 'value', [])

            if (!isEmpty(dateVal)) {
              if (dateVal.length === 1) {
                return (
                  this.fieldLabelMap[field] +
                  ' is ' +
                  this.operatorMap[fieldObj.operatorId].replace(
                    ' N ',
                    ` ${dateVal[0]} `
                  )
                )
              } else if (dateVal.length > 1) {
                return (
                  this.fieldLabelMap[field] +
                  ' is ' +
                  this.formatDate(Number(dateVal[0])) +
                  ' - ' +
                  this.formatDate(Number(dateVal[1]))
                )
              }
            } else {
              return (
                this.fieldLabelMap[field] +
                ' is ' +
                this.operatorMap[fieldObj.operatorId]
              )
            }
          }
        }
      }
    },

    formatDate(value) {
      return this.$options.filters.formatDate(Number(value), true, false)
    },

    resourceTag(resObj, value) {
      let resObject = isArray(resObj) ? resObj[0] : resObj
      let resVal = this.$getProperty(resObject, 'value', null)

      if (!isEmpty(resVal)) {
        let spaceFields = ['space', 'visitedSpace', 'resourceIdSpace']
        let assetFields = ['resourceIdAsset']

        if (['resource', 'resourceId'].includes(value)) {
          return (
            resVal.length +
            (resVal.length > 1
              ? ' Spaces/Assets Selected'
              : ' Space/Asset Selected')
          )
        } else if (spaceFields.includes(value)) {
          return (
            resVal.length +
            (resVal.length > 1 ? ' Spaces selected' : ' Space selected')
          )
        } else if (assetFields.includes(value)) {
          return (
            resVal.length +
            (resVal.length > 1 ? ' Assets selected' : ' Asset selected')
          )
        } else {
          let lookupField = {}
          let metaFields = this.$getProperty(this.metaInfo, 'fields', [])

          if (metaFields) {
            lookupField = metaFields.find(field => field.name === value) || {}

            if (
              resObject.selectedLabel &&
              this.$getProperty(lookupField.dataTypeEnum, '_name', '') ===
                'LOOKUP'
            ) {
              return lookupField.displayName + ' is ' + resObject.selectedLabel
            }
          }
        }
      } else {
        return `${this.fieldLabelMap[value]} ${
          this.operatorMap[resObject.operatorId]
        }`
      }
    },

    resetFilters() {
      this.$store.dispatch('search/resetFilters')
      this.$store.dispatch('search/toggleActiveStatus', false)
    },

    handleClose(tag, id) {
      let filter = cloneDeep(this.appliedFilters)

      if (!isEmpty(id) && this.dateTimeFields.includes(tag)) {
        let filterObj = filter[tag]
        let filterObjValue = filterObj.filter(item => item['operatorId'] !== id)

        filter[tag] = filterObjValue

        if (!isEmpty(filterObjValue)) {
          this.appliedFilters[tag] = filter[tag]
        } else {
          this.deleteFilter(tag)
        }
      } else if (!isEmpty(id)) {
        let filterObjValue = filter[tag].value
        let indexOfValue = filterObjValue.findIndex(val => val === id)

        if (!isEmpty(indexOfValue)) {
          filterObjValue.splice(indexOfValue, 1)
        }

        if (!isEmpty(filterObjValue)) {
          this.appliedFilters[tag].value = filterObjValue
        } else {
          this.deleteFilter(tag)
        }
      } else {
        this.deleteFilter(tag)
      }

      if (isEmpty(this.appliedFilters)) {
        this.$store.dispatch('search/resetFilters')
      } else {
        this.$store.dispatch('search/applyFilters', this.appliedFilters)
      }
      this.resetFilterData()
    },

    deleteFilter(tag) {
      delete this.appliedFilters[tag]

      if (['subject', 'name', 'title', 'resource'].includes(tag)) {
        let filterData = this.filterObjectsData[tag] || {}
        this.filterObjectsData = {
          ...this.filterObjectsData,
          [tag]: { ...filterData, value: '' },
        }
      } else if (tag === 'resourceId') {
        Object.keys(this.filterObjectsData || {}).forEach(fieldName => {
          let filterData = this.filterObjectsData[fieldName]
          let key = filterData.key ? filterData.key : fieldName

          if (key === 'resourceId') {
            this.filterObjectsData = {
              ...this.filterObjectsData,
              [key]: { ...filterData, value: [] },
            }
          }
        })
      } else {
        tag = this.getFilterKey(tag)
        let filterData = this.filterObjectsData[tag] || {}

        this.filterObjectsData = {
          ...this.filterObjectsData,
          [tag]: { ...filterData, value: [] },
        }
      }
    },

    savingView(command) {
      if (command === 'Save') {
        this.$store.dispatch('search/updateSaveExistingView', true)
      } else if (command === 'Save As') {
        this.$store.dispatch('search/updateViewDialogState', true)
        this.$store.dispatch('search/updateNewViewFilter', true)
      }
    },

    openFilterCondition(fieldName) {
      this.showFilterList = false
      this.showFilterCondition = true
      this.fieldName = fieldName

      let filterObj = this.$getProperty(this.filterObjectsData, fieldName, null)
      this.setOperatorId(filterObj, fieldName)
    },

    backCondition() {
      this.showFilterCondition = false
      this.showFilterList = true
    },

    close() {
      if (!this.showFilterCondition) this.showFilterList = false
    },
  },
}
</script>
<style lang="scss" scoped>
.search-filter-tag {
  font-size: 13px;
  display: flex;
  letter-spacing: 0.5px;
  color: #748893;
  border-radius: 3px;
  background-color: #eaeef0;
  border: none;
  padding-top: 2px;
  margin-right: 10px;
  margin-bottom: 8px;
}

.search-filter-tag:hover {
  background-color: #ddf1f4;
  color: #38a1ae;
  cursor: pointer;
}

.saveas-btn {
  background: #39b2c2;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #ffffff;
  padding: 7px;
}

.saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.tag-inline {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.tag-list-block {
  width: 240px;
  min-height: 300px;
  height: 300px;
  overflow-x: hidden;
  overflow-y: scroll;
  box-shadow: 1px 7px 11px 3px #dbe1e4;
  background-color: #ffffff;
  border: 1px solid #d0d9e2;
  position: absolute;
  left: 10px;
  top: 33px;
}

.tag-list-search {
  position: sticky;
  top: 0;
  z-index: 200;
  background: #ffffff;
}
.tag-list-body {
  padding-top: 10px;
  padding-bottom: 20px;
}

.clear-filter {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 20px;
  cursor: pointer;
}
</style>
<style>
.search-filter-tag .el-icon-close {
  color: #748893;
  font-weight: 500;
  top: 8px;
}
.search-filter-tag .el-icon-close:hover {
  color: #748893;
  font-weight: 500;
  background: none;
}
.search-filter-tag .el-tag .el-icon-close {
  left: 91px;
  top: -30px;
}
.popover-position {
  top: 15px;
  margin-left: 70px;
}
.add-filter-popover {
  top: 15px;
}
</style>
