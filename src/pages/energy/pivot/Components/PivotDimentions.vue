<template>
  <div class="pivot-dimentions">
    <div class="title-section">
      <div class="dimension-title">
        <div class="title">
          {{ $t('pivot.dimensions') }}
        </div>
        <div class="hr-line"></div>
      </div>
    </div>
    <div class="pivot-dimention-list">
      <skeletonLoader
        v-if="showLoader"
        :rowCount="3"
        width="186.5px"
        height="28px"
      />
      <div v-if="!showLoader" class="outter-container-rows">
        <div class="search-bar">
          <el-input
            size="mini"
            placeholder="Search..."
            class="pivot-search-box"
            :prefix-icon="'el-icon-search'"
            v-model="pivotDimentionSearch"
          >
          </el-input>
        </div>
        <div
          class="original-list-row"
          v-click-outside="nullifyActiveRow"
        >
          <div class="pivot-dimention-fields-section pB35" v-if="reRender">
            <el-collapse v-model="activeModule" accordion>
              <el-collapse-item :name="pivotBaseModuleName">
                <template slot="title">
                  <div class="module-title-icon-section">
                    <div class="module-icon-section" style="margin-left:2px;">
                      <img
                        src="~statics/pivot/module-pink.svg"
                        width="14px"
                        height="16px"
                      />
                    </div>
                    <div
                      v-if="pivotBaseModule"
                      class="module-name-section base-module-name"
                    >
                      {{ pivotBaseModule.displayName }}
                    </div>
                  </div>
                </template>
                <div class="original-list-row-full">
                  <draggable
                    class="dragArea list-group"
                    :list="pivotFilteredDimentions"
                    :group="{
                      name: 'pivotDimentions',
                      pull: 'clone',
                    }"
                    @start="isDraggingActive = true"
                    @end="isDraggingActive = false"
                    :clone="pivotDimentionAdded"
                    :options="draggableOptions"
                  >
                    <div
                      class="task-handle mR10"
                      style="padding-left:27px;"
                      v-if="pivotFilteredDimentions.length == 0"
                    >
                      No Data
                    </div>
                    <div
                      class="field-row"
                      v-for="(row, index) in pivotFilteredDimentions"
                      :key="index"
                      :class="{ 'field-hover': activeRow == row.primary.uuid }"
                      @click.stop="
                      activeRow == row.primary.uuid
                        ? (activeRow = null)
                        : (activeRow = row.primary.uuid)
                    "
                      v-else
                    >
                      <div
                        class="task-handle mR10 pointer"
                        v-if="
                          isDecimalField(row.primary) ||
                            isNumberField(row.primary) ||
                            isIdField(row.primary)
                        "
                      >
                        <img
                          src="~statics/pivot/icon-123.svg"
                          width="15px"
                          height="15px"
                          style="margin-top:9px;"
                        />
                      </div>
                      <div
                        class="task-handle mR10 pointer"
                        v-else-if="isLookupField(row.primary)"
                      >
                        <img
                          src="~statics/pivot/Lookup field.svg"
                          width="15px"
                          height="15px"
                          style="margin-top:4px;"
                        />
                      </div>
                      <div
                        class="task-handle mR10 pointer"
                        v-else-if="
                          isDateField(row.primary) ||
                            isDateTimeField(row.primary)
                        "
                      >
                        <img
                          src="~statics/pivot/icon-date.svg"
                          width="15px"
                          height="15px"
                          style="margin-top:4px;"
                        />
                      </div>
                      <div class="task-handle mR10 pointer" v-else>
                        <img
                          src="~statics/pivot/icon-abc.svg"
                          width="15px"
                          height="15px"
                          style="margin-top:9px;"
                        />
                      </div>
                      <div class="row-body-section">
                        <div
                          :class="
                            activeRow == row.primary.uuid
                              ? 'mT-auto mB-auto each-row active-row-width'
                              : 'mT-auto mB-auto each-row'
                          "
                        >
                        <el-tooltip
                            class="fld-tooltip"
                            effect="dark"
                            placement="top-start"
                            :content="row.primary.displayName"
                            :open-delay="1000"
                          >
                          <span>{{ row.primary.displayName }}</span>
                        </el-tooltip>
                        </div>
                        <div class="move-row-button" @click="moveRow(row)">
                          <img
                            src="~statics/pivot/move-row.svg"
                            width="15px"
                            height="15px"
                          />
                        </div>
                        <div
                          v-if="
                            (isDateField(row.primary) ||
                              isDateTimeField(row.primary)) && showAggr
                          "
                          class="aggr-select-option pR10 pT1"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline'
                              : ''
                          "
                        >
                          <el-select
                            filterable
                            size="mini"
                            default-first-option
                            placeholder="Aggr"
                            popper-class="fc-group-select"
                            value-key="label"
                            @focus="activeRow = row.primary.uuid"
                            v-model="row.selectedTimeAggr"
                            @change="reRenderDimension()"
                          >
                            <el-option
                              v-for="aggr in timeAggregation"
                              :key="aggr.name"
                              :label="aggr.label"
                              :value="aggr.value"
                              @click.stop=""
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                  </draggable>
                </div>
              </el-collapse-item>
            </el-collapse>
            <div class="lookup-options-list">
              <div
                class="looup-items-title-section"
                v-if="!islookupOptionsAvailable"
              >
                {{ $t('pivot.lookups') }}
              </div>
              <div
                class="single-lookup-field"
                v-for="(lookup, index) in filteredLookupOptions"
                :key="index"
              >
                <el-collapse
                  v-if="lookup.lookupOptions.length > 0"
                  v-model="activeModule"
                  accordion
                >
                  <el-collapse-item :name="lookup.lookupPrimary.moduleName">
                    <template slot="title">
                      <div class="module-title-icon-section">
                        <div class="module-icon-section">
                          <fc-icon group="files" name="one-level-lookup" class="one-level-lookup-icon"></fc-icon>
                        </div>
                        <div
                          v-if="lookup.lookupPrimary"
                          class="lookup-name-section"
                        >
                          {{ lookup.lookupPrimary.displayName }}
                        </div>
                      </div>
                    </template>
                    <div v-if="reRenderLookup" class="original-list-row-full">
                      <draggable
                        class="dragArea list-group"
                        :list="lookup.lookupOptions"
                        :group="{
                          name: 'pivotDimentions',
                          pull: 'clone',
                          put: false,
                        }"
                        @start="isDraggingActive = true"
                        @end="isDraggingActive = false"
                        :clone="
                          dim => pivotDimentionAdded(dim, lookup.lookupPrimary)
                        "
                        :options="draggableOptions"
                      >
                        <div
                          class="field-row"
                          v-for="(row, index) in lookup.lookupOptions"
                          :key="index"     
                          :class="{ 'field-hover': activeRow == row.uuid }"
                          @click="
                          activeRow == row.uuid
                            ? (activeRow = null)
                            : (activeRow = row.uuid)
                    "            
                        >
                          <div
                            class="task-handle mR10 pointer"
                            v-if="
                              isDecimalField(row) ||
                                isNumberField(row) ||
                                isIdField(row)
                            "
                          >
                            <img
                              src="~statics/pivot/drag.svg"
                              width="15px"
                              height="15px"
                              class="sortable-icon"
                            />
                            <img
                              src="~statics/pivot/icon-123.svg"
                              width="15px"
                              height="15px"
                              style="margin-top:5px;"
                            />
                          </div>
                          <div
                            class="task-handle mR10 pointer"
                            v-else-if="isLookupField(row)"
                          >
                            <img
                              src="~statics/pivot/drag.svg"
                              width="15px"
                              height="15px"
                              class="sortable-icon"
                            />
                            <img
                              src="~statics/pivot/Lookup field.svg"
                              width="15px"
                              height="15px"
                            />
                          </div>
                          <div
                            class="task-handle mR10 pointer"
                            v-else-if="isDateField(row) || isDateTimeField(row)"
                          >
                            <img
                              src="~statics/pivot/drag.svg"
                              width="15px"
                              height="15px"
                              class="sortable-icon"
                            />
                            <img
                              src="~statics/pivot/icon-date.svg"
                              width="15px"
                              height="15px"
                            />
                          </div>
                          <div class="task-handle mR10 pointer" v-else>
                            <img
                              src="~statics/pivot/drag.svg"
                              width="15px"
                              height="15px"
                              class="sortable-icon"
                            />
                            <img
                              src="~statics/pivot/icon-abc.svg"
                              width="15px"
                              height="15px"
                              style="margin-top:5px;"
                            />
                          </div>
                          <div class="row-body-section lookups-row">
                            <div
                              :class="
                                activeRow == row.uuid
                                  ? 'mT-auto mB-auto each-row active-row-width'
                                  : 'mT-auto mB-auto each-row'
                              "
                            >
                            <el-tooltip
                              class="fld-tooltip"
                              effect="dark"
                              placement="top-start"
                              :content="row.displayName"
                              :open-delay="1000"
                            >
                              <span>{{ row.displayName }}</span>
                            </el-tooltip>
                            </div>
                            <div
                              class="move-row-button"
                              @click="moveRow(row, lookup.lookupPrimary)"
                            >
                              <img
                                src="~statics/pivot/move-row.svg"
                                width="15px"
                                height="15px"
                              />
                            </div>
                            <div
                              v-if="isDateField(row) || isDateTimeField(row)"
                              class="aggr-select-option pR10 pT1"
                              :style="
                                activeRow == row.uuid ? 'display:inline' : ''
                              "
                            >
                              <el-select
                                filterable
                                default-first-option
                                size="mini"
                                placeholder="Aggr"
                                popper-class="fc-group-select"
                                @focus="activeRow = row.uuid"
                                value-key="label"
                                v-model="row.selectedTimeAggr"
                                @change="reRenderLookupFlds()"
                              >
                                <el-option
                                  v-for="aggr in timeAggregation"
                                  :key="aggr.name"
                                  :label="aggr.label"
                                  :value="aggr.value"
                                  @click.stop=""
                                ></el-option>
                              </el-select>
                            </div>
                          </div>
                        </div>
                      </draggable>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="vertical-line-split">
        <div class="vertical-line"></div>
      </div>
      <div class="new-list-row">
        <div class="row-header">
          <div class="title-header">
            <img src="~statics/pivot/row-icon.svg" width="15px" height="15px" />
            <span style="padding-left:5px;">{{ $t('pivot.rows') }}</span>
          </div>
        </div>
        <skeletonLoader
          v-if="showLoader"
          :rowCount="1"
          width="186.5px"
          height="28px"
        />
        <draggable
          class="dragArea list-group new-list"
          :class="{ 'dragging-active': isDraggingActive }"
          :list="pivotConfigDimentions"
          :options="draggableOptions"
          @start="isDraggingActive = true"
          @end="isDraggingActive = false"
          @change="pivotDimenetionUpdated"
          v-if="!showLoader"
        >
          <div
            class="field-row-new"
            :class="{ activeDimention: activeColumn === row.alias }"
            @click.stop="$emit('activeColumnChanged', row.alias)"
            v-for="(row, index) in pivotConfigDimentions"
            :key="index"
            @mouseenter="rowHover = row.alias"
            @mouseleave="rowHover = null"
          >
          <el-tooltip class="fld-tooltip" effect="dark" placement="top-start" :open-delay="1000">
              <div slot="content">
                <span
                  v-if="row.selectedTimeAggr >= 0 && getDataType(row.alias)"
                >
                  {{
                    timeAggregationMap[row.selectedTimeAggr] +
                      ' ( ' +
                      getFieldLabel(row.alias, row.field, row.fieldDisplayName)
                  }}
                  <span
                    v-if="
                      row.field.module != undefined && row.lookupFieldId != -1
                    "
                  >
                    {{ '(' + row.field.module.displayName + ')' }}
                  </span>
                  {{ ')' }}
                </span>
                <span v-else>
                  {{
                    getFieldLabel(row.alias, row.field, row.fieldDisplayName)
                  }}
                  <span
                    v-if="
                      row.field.module != undefined && row.lookupFieldId != -1
                    "
                  >
                    {{ '(' + row.field.module.displayName + ')' }}
                  </span>
                </span>
              </div>
              <div>
            <div
              class="task-handle mR10 pointer"
              v-if="
                isDecimalField(getField(row.alias, row.field)) ||
                  isNumberField(getField(row.alias, row.field)) ||
                  isIdField(getField(row.alias, row.field))
              "
            >
              <InlineSvg
                src="svgs/pivot/icon-123"
                iconClass="icon icon-sm15"
                style="margin-top:7px;"
              />
            </div>
            <div
              class="task-handle mR10 pointer"
              v-else-if="isLookupField(getField(row.alias, row.field))"
            >
              <InlineSvg
                src="svgs/pivot/Lookup field"
                iconClass="icon icon-sm15"
              />
            </div>
            <div
              class="task-handle mR10 pointer"
              v-else-if="
                isDateField(getField(row.alias, row.field)) ||
                  isDateTimeField(getField(row.alias, row.field))
              "
            >
              <InlineSvg
                src="svgs/pivot/icon-date"
                iconClass="icon icon-sm15"
              />
            </div>
            <div class="task-handle mR10 pointer" v-else>
              <InlineSvg
                src="svgs/pivot/icon-abc"
                iconClass="icon-sm15 icon"
                style="margin-top:6px;"
              />
            </div>
            <div
              class="mT-auto mB-auto field-label-area"
              v-if="reRenderDimentions"
            >
              <div class="pivot-row-label">
                {{ getFieldLabel(row.alias, row.field, row.fieldDisplayName) }}
                <span
                  v-if="
                    row.field.module != undefined && row.lookupFieldId != -1
                  "
                  >{{ '(' + row.field.module.displayName + ')' }}</span
                >
              </div>
            </div>
            <div class="mL-auto mT-auto mB-auto">
              <div class="inline">
                <i
                  class="el-icon-close pivot-icon-hover"
                  @click.stop="() => pivotDimentionRemoved(row)"
                ></i>
              </div>
            </div>
          </div>
            </el-tooltip>
          </div>
          <div
            class="config-instruction-section"
            v-if="pivotConfigDimentions.length == 0"
            :key="677"
          >
            {{ $t('pivot.dropDimention') }}
          </div>
        </draggable>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import Constants from 'util/constant'
import { v4 as uuid } from 'uuid'
import draggable from 'vuedraggable'
import skeletonLoader from './skeletonLoader.vue'
import { isEmpty } from '@facilio/utils/validation'
import './../pivot.scss'
import {
  isDecimalField,
  isNumberField,
  isIdField,
  isLookupField,
  isDateField,
  isDateTimeField,
} from '../../../../util/field-utils'
import { getUniqueCharAlias } from 'util/utility-methods'
import { defaultColFormat, timeAggregation } from './../PivotDefaults'

export default {
  name: 'PivotDimentions',
  props: [
    'pivotBaseModuleName',
    'initaialRows',
    'builderConfig',
    'pivotResponse',
    'activeColumn',
  ],
  components: {
    draggable,
    skeletonLoader,
  },
  watch: {
    pivotBaseModuleName(newVal) {
      if (newVal) {
        this.loadPivotDimentions()
        this.loadPivotBaseModuleMeta()
      }
    },
    initaialRows(newVal) {
      if (newVal) {
        this.pivotConfigDimentions = newVal
      }
    },
    // activeColumn() {
    //   this.reRenderDimentions = false
    //   setTimeout(() => {
    //     this.reRenderDimentions = true
    //   }, 100)
    // },
  },
  created() {
    this.setTimeAggrIdVsMap()
  },
  computed: {
    pivotFilteredDimentions() {
      if (!this.pivotDimentionSearch) return this.transformedPivotDiemtions
      return this.transformedPivotDiemtions.filter(rowObj =>
        rowObj.primary.displayName
          .toLowerCase()
          .includes(this.pivotDimentionSearch.toLowerCase())
      )
    },

    transformedPivotDiemtions() {
      let transformedPivotDimentions = this.pivotDimentions.map(field => {
        let rowObject = {}
        rowObject.primary = { ...field }
        if (
          field.dataTypeEnum == 'LOOKUP' &&
          this.meta[field.lookupModule.name]
        ) {
          let lookupOptions = [...this.meta[field.lookupModule.name]]
          let selectedField = lookupOptions.find(
            lookupField => lookupField.mainField
          )

          rowObject.lookupOptions = [...lookupOptions]
          rowObject.selectedLookupField = {
            ...selectedField,
          }
        } else {
          rowObject.lookupOptions = []
        }
        if (
          (field.dataTypeEnum == 'DATE_TIME' || field.dataTypeEnum == 'DATE') &&
          isEmpty(rowObject?.primary?.selectedTimeAggr)
        ) {
          rowObject.selectedTimeAggr = 0
        }
        return rowObject
      })

      return transformedPivotDimentions
    },

    transformedLookupOptionsList() {
      let transformArray = []

      this.pivotDimentions.forEach(field => {
        let rowObject = {}
        rowObject.lookupPrimary = { ...field }
        if (
          field.dataTypeEnum == 'LOOKUP' && field.name != 'siteId' &&
          this.meta[field.lookupModule.name]
        ) {
          let lookupOptions = this.meta[field.lookupModule.name].filter(
            fd => fd.id > 0
          )

          rowObject.lookupOptions = [...lookupOptions]
          transformArray.push(rowObject)
        }
      })
      return transformArray
    },
    filteredLookupOptions() {
      if (!this.pivotDimentionSearch) return this.transformedLookupOptionsList
      let cloneTransformedLookupOptionsList = JSON.parse(
        JSON.stringify(this.transformedLookupOptionsList)
      )
      let filtered = cloneTransformedLookupOptionsList.map(lookup => {
        lookup.lookupOptions = lookup.lookupOptions.filter(lp => {
          if (
            (lp.dataTypeEnum == 'DATE_TIME' || lp.dataTypeEnum == 'DATE') &&
            isEmpty(lp.selectedTimeAggr)
          ) {
            lp.selectedTimeAggr = 0
          }
          return lp.displayName
            .toLowerCase()
            .startsWith(this.pivotDimentionSearch.toLowerCase())
        })
        return lookup
      })
      return filtered
    },
    islookupOptionsAvailable() {
      return this.filteredLookupOptions.every(l => l.lookupOptions.length == 0)
    },
  },
  mounted() {
    if (this.pivotBaseModuleName) {
      this.loadPivotDimentions()
      this.loadPivotBaseModuleMeta()
    }
  },
  data() {
    return {
      pivotDimentions: [],
      showLoader: true,
      showAggr:true,
      transformedPivotDimentions: [],
      pivotConfigDimentions: [],
      draggableOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'pivotDimentions',
        sort: true,
      },
      activeModule: '',
      activeRow: null,
      reRenderDimentions: true,
      activeSelectBox: false,
      isDraggingActive: false,
      meta: {},
      pivotColumnFormat: {},
      pivotDimentionSearch: '',
      activeLookupFieldName: null,
      pivotBaseModule: null,
      defaultColFormat: defaultColFormat,
      timeAggregation: timeAggregation,
      rowHover: null,
      reRender: true,
      reRenderLookup: true,
      timeAggregationMap: {},
    }
  },
  methods: {
    isDecimalField,
    isNumberField,
    isIdField,
    isLookupField,
    isDateField,
    isDateTimeField,
    initDimentions() {
      if (this.initaialRows)
        this.pivotConfigDimentions = JSON.parse(
          JSON.stringify(this.initaialRows)
        )
    },
    reRenderDimension() {
      const list = document.querySelector('.pivot-dimention-fields-section')
      const scrollPos=list.scrollTop
      this.showAggr=false
      this.$nextTick(() => {
        this.reRender = true
      this.showAggr=true
        this.scrollTop(scrollPos,list)
      })
    },
    scrollTop(pos,list) {
      list.scrollTop=pos
    },
    reRenderLookupFlds() {
      const list = document.querySelector('.pivot-dimention-fields-section')
      const scrollPos=list.scrollTop
      this.$nextTick(() => {
        this.reRenderLookup = true
        this.scrollTop(scrollPos,list)
      })
    },
    nullifyActiveRow() {
      this.activeRow = null
    },
    moveRow(row, lookup) {
      let addRow = this.pivotDimentionAdded(row, lookup ? lookup : null)
      this.pivotConfigDimentions.push(addRow)
      this.pivotDimenetionUpdated({ added: { element: addRow } })
    },
    async loadPivotDimentions() {
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

        this.pivotDimentions = meta[this.pivotBaseModuleName]
      }
    },
    getDataType(alias) {
      if (this?.pivotResponse?.pivotAliasVsField[alias]) {
        return (
          this.pivotResponse.pivotAliasVsField[alias]?.dataTypeEnum ===
            'DATE_TIME' ||
          this.pivotResponse.pivotAliasVsField[alias]?.dataTypeEnum === 'DATE'
        )
      }
    },
    getFieldLabel(alias, field, fieldDisplayName) {
      if (fieldDisplayName) return fieldDisplayName
      if (field.displayName) return field.displayName
      if (
        this.pivotResponse &&
        this.pivotResponse.pivotAliasVsField[alias] &&
        this.pivotResponse.pivotAliasVsField[alias].displayName
      )
        return this.pivotResponse.pivotAliasVsField[alias].displayName
      return this.builderConfig.templateJSON.columnFormatting[alias].label
    },
    getField(alias, field) {
      if (field.dataType) return field
      if (this.pivotResponse && this.pivotResponse.pivotAliasVsField[alias])
        return this.pivotResponse.pivotAliasVsField[alias]
      return {}
    },
    pivotDimentionAdded(row, lookupField) {
      let alias = `row_${getUniqueCharAlias(
        this.pivotConfigDimentions.map(row => row.alias.split('_')[1])
      )}`
      let field = {}

      if (lookupField) {
        field = row
      } else {
        field = row.primary
      }

      let rowObj = {
        field: field,
        aggr: field.aggr ? field.aggr : 0,
        lookupFieldId: lookupField ? lookupField.id : -1,
        alias: alias,
        moduleName:
          lookupField && lookupField?.lookupModule
            ? lookupField.lookupModule.name
            : null,
        fieldDisplayName: field.displayName,
      }
      if (row?.selectedTimeAggr >= 0) {
        rowObj.selectedTimeAggr = row.selectedTimeAggr
      }
      rowObj.field.aggr = 0
      return rowObj
    },
    async loadPivotBaseModuleMeta() {
      let moduleMetaResp = await API.get(
        `v2/modules/meta/${this.pivotBaseModuleName}`
      )
      let { data, error } = moduleMetaResp
      if (error) {
        this.$message.error('Error fetching base module meta')
      } else {
        this.pivotBaseModule = JSON.parse(JSON.stringify(data.meta))
        this.activeModule = this.pivotBaseModule.name
        this.$emit('dimensionsLoaded')
        this.showLoader = false
      }
    },
    pivotDimentionRemoved(dimention) {
      this.$emit('deleteRowValue', dimention.alias)
    },
    pivotDimenetionUpdated({ added }) {
      if (added) {
        let rowObj = added.element
        let formatConfig = this.defaultColFormat.rowColumn
        formatConfig.label = rowObj.field.displayName
        this.pivotColumnFormat[rowObj.alias] = { ...formatConfig }
      }

      this.$emit('pivotDimentionUpdated', {
        dimentions: this.pivotConfigDimentions,
        formats: this.pivotColumnFormat,
      })
    },
    openUserFilter() {
      this.$emit('openUserFilter')
    },
    setTimeAggrIdVsMap() {
      timeAggregation.forEach(aggr => {
        this.timeAggregationMap[aggr.value] = aggr.label
      })
    },
  },
}
</script>

<style lang="scss">
.pivot-dimentions {
  display: flex;
  flex-direction: column;
  height: 43vh;
  overflow: hidden;
  .one-level-lookup-icon
  {
    scale:0.8;
    opacity:0.7;
  }
  .active-row-width {
    text-overflow: ellipsis;
    overflow: hidden;
    width: 135px;
  }

  .pivot-icon-hover {
    &:hover {
      color: black !important;
      border-radius: 50%;
    }
  }
  .pivot-search-box {
    .el-input__prefix {
      top: -1px;
      left: -2px;
    }
    .el-input__icon {
      scale: 1.1;
    }
    .el-input__inner {
      padding-left: 22px !important;
    }
  }

  .field-hover {
    background: #ecf0f6 !important;
    color: #8d96a4;
    border-radius: 3px;
    cursor: move;
  }

  .filter-tilte {
    padding-left: 10px;
    font-size: 13px;
  }
  .filter-section {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .title-section {
    display: flex;
    justify-content: space-between;
  }

  .module-title-icon-section {
    display: flex;
    align-content: space-between;
    align-items: center;
    justify-content: space-around;
  }

  .module-icon-section {
    padding-right: 10px;
  }

  .move-row-button {
    width: 15px;
    height: 15px;
    cursor: pointer;
    margin-right: 10px;
    display: none;
    margin-top: 2.5px;
  }

  .module-name-section {
    color: #ff3184;
    font-size: 12px;
    font-weight: 500;
  }

  .el-collapse-item__header {
    line-height: 0px !important;
    height: 30px !important;
  }

  .lookup-options-list {
    padding-top: 20px;

    .lookup-name-section {
      color: #324056;
      font-size: 13px;
      font-weight: normal;
      letter-spacing: normal;
    }

    .el-collapse-item__arrow {
      color: #324056;
    }

    .lookup-options-list {
      padding-top: 15px;
    }
  }

  .field-row {
    &:hover {
      .aggr-select-option {
        display: inline;
      }
    }
    display: flex;
    box-shadow: none !important;
    border: none !important;
    padding: 10px 2px !important;
    width: 100%;
    text-overflow: ellipsis;
    height: 28px;
    white-space: nowrap;
    -webkit-box-pack: end;
    -ms-flex-pack: end;
    color: #324056;
    align-items: center;
    font-size: 13px;
    font-weight: normal;
    letter-spacing: normal;
    &:hover{
    background: #ecf0f6 !important;
    border-radius: 3px;
    cursor: move;
    }
  }

  .field-row-new {
    background: #ecf0f6;
    color: #324056;
    padding: 0px 8px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin: 5px 5px;
    /* width: 204px; */
    border-radius: 3px;
    cursor: move;
    height: 28px;
    align-items: center;
    font-size: 13px;
    font-weight: normal;
    letter-spacing: normal;

    .fld-tooltip {
      display: flex;
      width: 186.5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .task-handle {
      margin-top: 2px;
    }
  }

  .row-header {
    display: flex;
    justify-content: space-between;
    padding-right: 10px;
    padding-bottom: 10px;
  }
  .title-header {
    font-size: 12px;
    display: flex;
    align-items: flex-end;
  }

  .original-list-row {
    display: flex;
    width: 186.5px;
    flex-direction: column;
    height: calc(50vh - 84px);
    padding-top: 5px;
  }
  .el-collapse-item__content {
    padding-bottom: 0 !important;
  }

  .original-list-row-full {
    overflow: auto;
    overflow-x: scroll;
    text-overflow: ellipsis;
    .el-input .el-input__inner,
    .el-textarea .el-textarea__inner {
      border: 1px solid #d8dce5;
      padding: 3px;
      background: #c8d2e0 !important;
      border-radius: 4px;
      height: 20px !important;
      line-height: 20px !important;
      border-radius: 3px !important;
      letter-spacing: 0.3px;
      font-size: 10px;
      color: #324056;
      text-overflow: ellipsis;
      font-weight: 400;
      white-space: nowrap;
      width: 100px;
    }
  }

  .pivot-dimention-list {
    padding-top: 15px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    padding-bottom: 10px;
  }

  .dragArea.list-group {
    height: 100%;
    &:hover {
      cursor: move;
    }
  }

  .single-lookup-field {
    .el-collapse-item__content {
      background-color: #f8fafd;
    }
  }

  .dragArea.list-group.new-list {
    height: 32vh;
    width: 186.5px;
    border: 1px solid #ebedf5;
    overflow: scroll;
    border-radius: 3px;
  }

  .dragArea.list-group.new-list.dragging-active {
    border: 1px dashed #ff6116 !important;
  }

  .new-list-row {
    height: 350px;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
    overflow: hidden;
    .el-input .el-input__inner,
    .el-textarea .el-textarea__inner {
      border: 1px solid #d8dce5;
      padding: 3px;
      background: #c8d2e0 !important;
      border-radius: 4px;
      height: 20px !important;
      line-height: 20px !important;
      border-radius: 3px !important;
      letter-spacing: 0.3px;
      font-size: 10px;
      color: #324056;
      text-overflow: ellipsis;
      font-weight: 400;
      white-space: nowrap;
      width: 100px;
    }
  }

  .vertical-line-split {
    width: 27px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  .vertical-line {
    width: 1px;
    height: 100%;
    border-radius: 3px;
    border-right: solid 0.5px #dbdee5;
  }

  .row-body-section {
    display: flex;
    justify-content: space-between;
    width: 100%;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    &:hover {
      .move-row-button {
        display: inline;
      }
    }
  }
  .lookups-row {
    width: 135px !important;
  }

  .config-instruction-section {
    height: 100%;
    width: 100%;
    background: #fbfdff;
    color: #a2a9b3;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-around;
  }

  .el-input .el-input__inner,
  .el-textarea .el-textarea__inner {
    border: 1px solid #d8dce5;
    padding: 10px;
    border-radius: 4px;
    height: 25px !important;
    line-height: 25px !important;
    border-radius: 3px !important;
    letter-spacing: 0.3px;
    color: #324056;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
  }
  .title {
    font-size: 13px;
    padding-bottom: 7px;
    font-weight: 500;
    letter-spacing: 0.5px;
  }
  .hr-line {
    width: 40px;
    border-bottom: 2px solid #ff3184;
  }

  .el-collapse {
    border: none !important;
  }

  .el-collapse-item__header {
    border: none !important;
  }

  .el-collapse {
    border: none !important;
  }

  .el-collapse-item__arrow {
    color: #ff3184;
  }

  .el-collapse-item {
  }

  .looup-items-title-section {
    padding-bottom: 10px;
    font-size: 13px;
    font-weight: 500;
  }

  .resize-row-width {
    width: 100px;
  }

  .pivot-dimention-fields-section {
    overflow: scroll;
  }

  .activeDimention {
    background: #647faa !important;
    color: #fff !important;
    .cls-3,
    .cls-1,
    .cls-2 {
      fill: white !important;
    }
  }

  .field-label-area {
    overflow: hidden;
  }
  .pivot-row-label {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
  .single-lookup-field {
    .original-list-row-full {
      width: 230px;
      background-color: #f8fafd;
    }
  }
  .field-row {
    .sortable-icon {
      opacity: 0;
      margin-right: 10.8px;
    }
    &:hover {
      .sortable-icon {
        opacity: 1;
      }
    }
  }

  .el-collapse-item__header {
    &:hover {
      background-color: #ecf0f6;
      border-radius: 3px;
    }
  }
  .each-row {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    width: 206px;
  }
  .aggr-select-option {
    font-size: 11px !important;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    text-decoration: lowercase !important;
    border-radius: 3px;
    display: none;
    padding-top: 4px;

    color: #324056;
    .el-input__inner {
      width: 60px !important;
      text-transform: lowercase;
      font-size: 10px !important;
    }
    .el-input__suffix {
      top: 5px;
      right: -4px;
      font-size: 10px !important;
    }
    .el-input__inner::placeholder {
      font-size: 10px;
    }
    /* google */
    .el-input__inner::-webkit-input-placeholder {
      font-size: 10px;
    }
    /* firefox */
    .el-input__inner:-moz-placeholder {
      font-size: 10px;
    }
  }
  .pT1 {
    padding-top: 1.5px;
  }
}
</style>
