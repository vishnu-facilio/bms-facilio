<template>
  <div class="p30">
    <div class="header cards-config-header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Reading Table 1' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section config-panel">
        <el-tabs v-model="activeTab" class="card-tab-fixed">
          <el-tab-pane label="Config" name="config">
            <el-form
              :model="cardDataObj"
              :ref="`${this.cardLayout}_form`"
              :rules="validationRules"
              :label-position="'top'"
            >
              <el-row class="" :gutter="20">
                <el-col :span="12">
                  <el-form-item prop="title" class="mB10">
                    <p class="fc-input-label-txt pB5">Title</p>
                    <el-input
                      :autofocus="isNew"
                      v-model="cardDataObj.title"
                      class="width100 fc-input-full-border2"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="" :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Asset Category</p>
                  <el-form-item prop="assetCategoryId">
                    <el-select
                      @change="loadFields()"
                      filterable
                      :clearable="true"
                      v-model="cardDataObj.assetCategoryId"
                      placeholder="Asset Category"
                      class="width100 el-input-textbox-full-border pR10"
                    >
                      <el-option
                        v-for="(category, idx) in assetCategory"
                        :key="idx"
                        :label="category.displayName"
                        :value="category.id"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item>
                    <p class="fc-input-label-txt pB5">Building</p>
                    <el-select
                      filterable
                      :clearable="true"
                      @clear="clearBuildingId"
                      v-model="cardDataObj.buildingId"
                      placeholder="Select Building"
                      class="width100 report-dropDown el-input-textbox-full-border"
                    >
                      <el-option
                        v-for="(building, idx) in buildings"
                        :key="idx"
                        :label="building.name"
                        :value="building.id"
                      >
                      </el-option>
                      <el-option
                        key="all"
                        :label="'All'"
                        :value="-1"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="columns" class="mB10">
                    <p class="fc-input-label-txt pB5">Columns</p>
                    <draggable
                      v-model="cardDataObj.columns"
                      :options="{
                        animation: 150,
                        easing: 'cubic-bezier(1, 0, 0, 1)',
                      }"
                    >
                      <el-row
                        v-for="(obj, index) in cardDataObj.columns"
                        class="row mB10"
                        :key="index"
                        :gutter="10"
                      >
                        <el-col :span="1">
                          <inline-svg
                            src="drag-drop"
                            class="vertical-middle cursor-drag icon-right"
                            iconClass="icon icon-sm-md"
                            style="cursor: -webkit-grabbing;"
                          ></inline-svg>
                        </el-col>
                        <el-col :span="3">
                          <el-select
                            v-model="obj.type"
                            @change="loadFields(obj.type, index)"
                            placeholder="Type"
                            class="report-dropDown el-input-textbox-full-border"
                          >
                            <el-option
                              v-for="(type, idx) in types"
                              :key="idx"
                              :label="type.label"
                              :value="type.value"
                            >
                            </el-option>
                          </el-select>
                        </el-col>
                        <el-col :span="4" v-if="obj.type === 'field'">
                          <el-select
                            v-model="obj.fieldName"
                            filterable
                            @change="setFieldModuleName(index)"
                            placeholder="Field"
                            class="report-dropDown el-input-textbox-full-border"
                            :clearable="true"
                          >
                            <el-option
                              v-for="(field, idx) in metaFields"
                              :key="idx"
                              :label="field.displayName"
                              :value="field.name"
                            >
                            </el-option>
                          </el-select>
                        </el-col>
                        <el-col :span="4" v-else>
                          <div
                            v-if="
                              obj.fieldObj ||
                                obj.fieldName == null ||
                                obj.fieldName == undefined ||
                                obj.fieldName == ''
                            "
                          >
                            <el-select
                              @change="setReadingModuleName(index)"
                              v-model="obj.fieldObj"
                              filterable
                              placeholder="Reading"
                              class="report-dropDown el-input-textbox-full-border"
                              :clearable="true"
                            >
                              <el-option
                                v-for="(field, idx) in readingFields"
                                :key="idx"
                                :label="field.displayName"
                                :value="`${field.name}_${field.id}`"
                              >
                              </el-option>
                            </el-select>
                          </div>
                          <div v-else>
                            <el-select
                              @change="setReadingModuleName(index)"
                              v-model="obj.fieldName"
                              filterable
                              placeholder="Reading"
                              class="report-dropDown el-input-textbox-full-border"
                              :clearable="true"
                            >
                              <el-option
                                v-for="(field, idx) in readingFields"
                                :key="idx"
                                :label="field.displayName"
                                :value="field.name"
                              >
                              </el-option>
                            </el-select>
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <el-input
                            v-model="obj.label"
                            type="text"
                            placeholder="Display label"
                            class="fc-input-full-border-select2"
                          >
                          </el-input>
                        </el-col>
                        <el-col :span="4" v-if="obj.type === 'reading'">
                          <el-select
                            v-model="obj.yAggr"
                            placeholder="Aggr"
                            class="width100 el-input-textbox-full-border"
                          >
                            <el-option
                              v-for="(fn, index) in aggregateFunctions"
                              :label="fn.label"
                              :value="fn.value"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </el-col>
                        <el-col
                          :span="4"
                          v-if="
                            obj.type === 'reading' && cardDataObj.controllable
                          "
                        >
                          <el-checkbox v-model="obj.controlAction"
                            >ControlAction</el-checkbox
                          >
                        </el-col>
                        <el-col :span="2">
                          <div
                            :key="obj.id + index + ''"
                            class="reading-controls"
                          >
                            <img
                              src="~assets/add-icon.svg"
                              style="height:18px;width:18px;margin-right: 10px;"
                              class="delete-icon pointer vertical-middle"
                              @click="addRow()"
                              v-if="
                                index === cardDataObj.columns.length - 1 &&
                                  cardDataObj.columns.length < 30
                              "
                            />
                            <img
                              src="~assets/remove-icon.svg"
                              v-if="cardDataObj.columns.length !== 1"
                              style="height:18px;width:18px;margin-right: 3px;"
                              class="delete-icon pointer vertical-middle"
                              @click="deleteRow(index)"
                            />
                          </div>
                        </el-col>
                      </el-row>
                    </draggable>

                    <el-row class="mB10">
                      <el-col :span="24">
                        <el-form-item prop="dateRange" class="mB10">
                          <p class="fc-input-label-txt pB5">Period</p>
                          <el-select
                            v-model="cardDataObj.dateRange"
                            placeholder="Please select a period"
                            class="width40 el-input-textbox-full-border"
                          >
                            <template
                              v-for="(dateRange, index) in dateOperators"
                            >
                              <el-option
                                :label="dateRange.label"
                                :value="dateRange.value"
                                :key="index"
                              ></el-option>
                            </template>
                          </el-select>
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row class="mB10">
                      <el-col :span="24">
                        <el-checkbox v-model="cardDataObj.displayMode"
                          >Flip Table</el-checkbox
                        >
                      </el-col>
                    </el-row>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="Filters" name="filter">
            <el-form
              :model="cardStateObj"
              :label-position="'top'"
              class="width70"
            >
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="Criteria" class="mB10">
                    <div class="reading-table-criteria-container">
                      <p class="fc-modal-sub-title">
                        {{ $t('setup.users_management.criteria') }}
                      </p>
                      <p class="fc-sub-title-desc">
                        {{
                          $t(
                            'setup.users_management.specify_rules_for_assigment_rules'
                          )
                        }}
                      </p>
                      <criteria-builder
                        v-model="cardDataObj.filterCriteria"
                        :moduleName="cardDataObj.moduleName"
                      ></criteria-builder>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="" v-if="cardDataObj.sorting" :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Sorting</p>
                  <el-form-item prop="sorting">
                    <el-select
                      v-model="cardDataObj.sorting.fieldName"
                      filterable
                      placeholder="Field Name"
                      class="report-dropDown el-input-textbox-full-border"
                      :clearable="true"
                    >
                      <el-option
                        v-for="(field, idx) in metaFields"
                        :key="idx"
                        :label="field.displayName"
                        :value="field.name"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Order</p>
                  <el-form-item prop="sorting">
                    <el-select
                      v-model="cardDataObj.sorting.order"
                      filterable
                      placeholder="Field Name"
                      class="report-dropDown el-input-textbox-full-border"
                      :clearable="true"
                    >
                      <el-option
                        v-for="(order, idx) in sortingList"
                        :key="idx"
                        :label="order.label"
                        :value="order.value"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row class="" v-if="cardDataObj.perPage">
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Limit</p>
                  <el-form-item prop="perPage">
                    <el-input-number
                      v-model="cardDataObj.perPage"
                      :min="5"
                      :max="130"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pB5">Page</p>
                  <el-form-item prop="perPage">
                    <el-input-number
                      v-model="cardDataObj.page"
                      :min="1"
                      :max="100"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="mB10">
                <el-col :span="8">
                  <el-form-item prop="dateRange" class="mB10">
                    <p class="fc-input-label-txt pB5">Asset Action</p>
                    <el-select
                      v-model="cardStateObj.styles.columnAction"
                      placeholder="Please select a period"
                      class="width100 pR20 el-input-textbox-full-border"
                    >
                      <template v-for="(columnAction, index) in columnActions">
                        <el-option
                          :label="columnAction.label"
                          :value="columnAction.value"
                          :key="index"
                        ></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="Styles" name="styles">
            <el-form :model="cardStateObj" :label-position="'top'">
              <el-row class="mB10 mT20">
                <el-col :span="8">
                  <el-form-item prop="primaryColor" class="mB10">
                    <p class="fc-input-label-txt pB5">Theme</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-select
                        filterable
                        v-model="cardStateObj.styles.theme"
                        placeholder="Asset Category"
                        class="report-dropDown el-input-textbox-full-border"
                      >
                        <el-option
                          v-for="(theme, idx) in tableThemes"
                          :key="idx"
                          :label="theme.label"
                          :value="theme.value"
                        >
                        </el-option>
                      </el-select>
                    </div>
                  </el-form-item>
                  <el-form-item prop="group_row_fieldName" class="mB10">
                    <p class="fc-input-label-txt pB5">Group Row</p>
                    <div
                      class="d-flex mT5 card-color-container fc-color-picker"
                    >
                      <el-select
                        clearable
                        filterable
                        v-model="cardStateObj.styles.group_row_fieldName"
                        placeholder="Asset Category"
                        class="report-dropDown el-input-textbox-full-border"
                      >
                        <el-option
                          v-for="(column, idx) in previewData.columns"
                          :key="idx"
                          :label="column.label"
                          :value="column.fieldName"
                        >
                        </el-option>
                      </el-select>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="CUSTOM SCRIPT" v-if="nameSpaces.length">
            <div class="p10">
              <el-checkbox
                v-model="enableBoxchecked"
                @change="changescriptModeInt()"
                >Enable Custom Script</el-checkbox
              >
              <div v-if="enableBoxchecked == true" class="mT30">
                <div v-if="nameSpaces.length">
                  <p class="fc-input-label-txt pB5">
                    Select Function
                  </p>
                  <el-select
                    placeholder="Please Select a Script"
                    class="width100 el-input-textbox-full-border"
                    v-model="customScriptId"
                  >
                    <el-option-group
                      v-for="(nameSpace, index) in nameSpaces"
                      :key="index"
                      :label="nameSpace.name"
                    >
                      <el-option
                        v-for="(func, index) in nameSpace.functions"
                        :key="index"
                        :label="func.name"
                        :value="func.id"
                      >
                      </el-option>
                    </el-option-group>
                  </el-select>
                </div>
                <div v-else class="fc-input-label-txt mT30">
                  No functions available. Kindly add some functions and check.
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onGoBack()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import draggable from 'vuedraggable'
import Config from '../base/Config'
import { isEmpty } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import { CriteriaBuilder } from '@facilio/criteria'
import {
  dateOperators,
  aggregateFunctions,
  predefinedColors,
  modules,
  tableThemes,
  sortingList,
} from 'pages/card-builder/card-constants'
export default {
  name: 'readingTable1',
  extends: Config,
  props: [
    'isNew',
    'cardType',
    'onClose',
    'onGoBack',
    'onCardSave',
    'onCardUpdate',
    'closePopup',
  ],
  mixins: [DateHelper],
  components: {
    CriteriaBuilder,
    draggable,
  },

  data() {
    return {
      cardLayout: `table_layout_1`,
      activeTab: 'config',
      isPreviewLoading: false,
      resourceProps: [
        'title',
        'dateRange',
        'moduleName',
        'assetCategoryId',
        'displayMode',
        'controllable',
        'columns',
        'excludeEmptyReadings',
        'buildingId',
        'filterCriteria',
        'sorting',
        'perPage',
        'page',
      ],
      cardDataObj: {
        title: '',
        moduleName: 'asset',
        assetCategoryId: null,
        buildingId: -1,
        columns: [],
        dateRange: 'Today',
        displayMode: false,
        controllable: false,
        excludeEmptyReadings: false,
        filterCriteria: {},
        sorting: {
          fieldName: null,
          order: 'asc',
        },
        perPage: 25,
        page: 1,
      },
      cardStateObj: {
        canResize: true,
        styles: {
          columnAction: 1,
          theme: 'default',
          showCriteria: false,
          group_row_fieldName: null,
        },
      },
      cardActions: {
        default: {
          actionType: 'showTrend',
        },
      },
      types: [
        {
          label: 'Field',
          value: 'field',
        },
        {
          label: 'Reading',
          value: 'reading',
        },
      ],
      columnActions: [
        {
          label: 'Open Summary',
          value: 1,
        },
        {
          label: 'Open Graphics',
          value: 2,
        },
        {
          label: 'None',
          value: 3,
        },
      ],
      controlAction: false,
      readingFields: [],
      controlpointsload: [],
      metaFields: [],
      result: null,
      validationRules: {},
      predefinedColors: predefinedColors,
      dateOperators: dateOperators,
      aggregateFunctions: aggregateFunctions,
      emptyRow: {
        label: null,
        fieldName: null,
        type: 'field',
        yAggr: 'lastValue',
        moduleName: null,
        width: 200,
      },
      predefindRow: {
        label: 'Name',
        fieldName: 'name',
        type: 'field',
        yAggr: 'lastValue',
        moduleName: null,
        width: 200,
      },
      modules,
      tableThemes,
      sortingList,
    }
  },
  created() {
    this.$store.dispatch('loadBuildings')
    this.$store.dispatch({
      type: 'loadAssetCategory',
      forceUpdate: true,
    })
    this.loadControlPoints()
  },
  mounted() {
    this.addEmptyRow()
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
      buildings: state => state.buildings,
    }),
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: null,
            value: {},
            period: null,
            unit: null,
            columns: this.cardDataObj.columns,
          }
    },
  },
  methods: {
    clearBuildingId() {
      this.cardDataObj.buildingId = -1
    },
    loadControlPoints() {
      let { filters } = this
      let self = this
      self.loading = true
      let url = `v2/controlAction/getControllablePoints?page=1&perPage=1000`
      if (filters) {
        url += `&filters=${encodeURIComponent(
          JSON.stringify(filters)
        )}&includeParentFilter=true`
      }
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.controlpointsload = response.data.result.controllablePoints
            ? response.data.result.controllablePoints
            : []
          this.loading = false
        }
      })
    },
    resetCriteria(option) {
      let { cardDataObj } = this
      this.$set(this.cardStateObj.styles, 'showCriteria', option)
      if (this.cardStateObj.styles.showCriteria) {
        this.$set(cardDataObj, 'filterCriteria', {})
      }
    },
    getCriteria(criteria) {
      let { cardDataObj } = this
      this.$set(cardDataObj, 'filterCriteria', criteria)
    },
    serializeProperty(prop, data) {
      if (prop === 'filterCriteria' && data.filterCriteria) {
        if (this.isEmptyCriteria(data.filterCriteria)) {
          return {}
        }
      }
    },
    isEmptyCriteria(criteria) {
      if (
        criteria?.conditions &&
        Object.keys(criteria.conditions).length &&
        criteria.conditions[1].operatorId
      ) {
        return false
      }

      return true
    },
    addEmptyRow() {
      let { columns } = this.cardDataObj
      if (columns && !columns.length) {
        let predefindRow = this.$helpers.cloneObject(this.predefindRow)
        this.cardDataObj.columns.push(predefindRow)
      } else {
        this.loadFields()
      }
    },
    loadFields(type, index) {
      if (type === 'field') {
        this.$util
          .loadModuleMeta(this.cardDataObj.moduleName)
          .then(response => {
            this.metaFields = response.fields
          })
      } else if (type === 'reading') {
        this.$util
          .loadAssetReadingFields(-1, this.cardDataObj.assetCategoryId)
          .then(fields => {
            this.readingFields = fields
          })
      } else {
        this.$util
          .loadModuleMeta(this.cardDataObj.moduleName)
          .then(response => {
            this.metaFields = response.fields
          })
        this.$util
          .loadAssetReadingFields(-1, this.cardDataObj.assetCategoryId)
          .then(fields => {
            this.readingFields = fields
          })
      }
      if (index) {
        this.cardDataObj.columns[index].label = null
        this.cardDataObj.columns[index].fieldName = null
        this.cardDataObj.columns[index].yAggr = 'lastValue'
        this.cardDataObj.columns[index].moduleName = null
      }
    },
    setReadingModuleName(index) {
      let field
      if (!isEmpty(this.cardDataObj?.columns[index]?.fieldObj)) {
        field = this.readingFields.find(
          rl =>
            rl.id ===
            parseInt(this.cardDataObj?.columns[index]?.fieldObj.split('_')[1])
        )
        this.cardDataObj.columns[index].fieldName = field.name
      } else {
        field = this.readingFields.find(
          rl => rl.name === this.cardDataObj?.columns[index]?.fieldName
        )
      }
      if (field) {
        if (this.isControllable(field.id)) {
          this.cardDataObj.controllable = true
        } else {
          this.cardDataObj.controllable = false
        }
        this.cardDataObj.columns[index].moduleName = field.module.name
        this.cardDataObj.columns[index].label = field.displayName
      }
    },
    isControllable(id) {
      let obj = this.controlpointsload.filter(function(point) {
        return point.fieldId == id
      })
      if (obj.length > 0) {
        return true
      }
    },
    setFieldModuleName(index) {
      let field = this.metaFields.find(
        rl => rl.name === this.cardDataObj.columns[index].fieldName
      )
      if (field) {
        this.cardDataObj.columns[index].label = field.displayName
        this.cardDataObj.columns[index].moduleName = field.module.name
      }
    },
    addRow() {
      let emptyRow = this.$helpers.cloneObject(this.emptyRow)
      this.cardDataObj.columns.push(emptyRow)
    },
    deleteRow(index) {
      this.cardDataObj.columns.splice(index, 1)
      this.$forceUpdate()
    },
    validateField() {
      let validator = function(rule, value, callback) {
        let { columns } = this.cardDataObj
        columns.forEach(column => {
          if (!this.validateColumnField(column)) {
            callback(new Error('Fields can not be empty'))
          }
        })
        callback()
      }.bind(this)

      return {
        columns: {
          trigger: 'change',
          validator,
        },
      }
    },
    validateColumnProperty() {
      let { columns } = this.cardDataObj
      let valid = false
      columns.forEach(column => {
        if (!this.validateColumnField(column)) {
          valid = true
        }
      })
      return valid
    },
    validateProperty() {
      return {
        columns: () => this.validateColumnProperty(),
        buildingId: () => false,
        displayMode: () => false,
        filterCriteria: () => false,
        sorting: () => false,
      }
    },
    validateColumnField(column) {
      let hasReading = false
      if (column.type === 'field') {
        ;['label', 'fieldName'].forEach(prop => {
          hasReading = !isEmpty(column[prop])
        })
      } else {
        ;['label', 'fieldName', 'yAggr', 'moduleName'].forEach(prop => {
          hasReading = !isEmpty(column[prop])
        })
      }
      return hasReading
    },
  },
}
</script>

<style lang="scss" scoped>
.card-wrapper {
  width: 100%;
  height: 400px;
}

.card-builder-popup .container .section {
  flex-basis: 94%;
}

.column-drag-icon {
  position: absolute;
  left: 2px;
  top: 0px;
}

.criteria-container {
  height: auto;
}
</style>
