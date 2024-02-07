<template>
  <div>
    MARKER
    <el-tabs type="border-card" class="mT30">
      <el-tab-pane label="Style">
        <div>
          <el-row>
            <el-col :span="10" class="p10">
              <p>color</p>
              <el-color-picker
                :predefine="getPredefinedColors()"
                v-model="marker.style.color"
              ></el-color-picker>
            </el-col>
            <el-col :span="10" class="p10">
              <p>Action Type</p>
              <el-select
                v-model="marker.action.type"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(value, key) in markerActions"
                  :key="key"
                  :label="key"
                  :value="value"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="20" class="p10" v-if="marker.action.type === 1">
              <p>URL</p>
              <el-input
                v-model="marker.action.url"
                class="fc-input-full-border2"
              ></el-input>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
      <el-tab-pane label="value">
        <el-row class="mT10 maxWidth90" :gutter="10">
          <el-col :span="8">
            <div>
              <el-select
                v-model="markerValue.type"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(value, key) in valueMap[data.module]"
                  :key="key"
                  :label="key"
                  :value="value.fieldName"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <template v-if="markerValue.type === 'reading'">
            <el-col :span="8">
              <div>
                <el-select
                  v-model="data.readingObj.categoryId"
                  class="fc-input-full-border-select2"
                  @change="setReadingModuleName()"
                >
                  <el-option
                    v-for="(category, index) in assetCategory"
                    :key="index"
                    :label="category.name"
                    :value="category.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="8">
              <div>
                <el-select
                  v-model="data.readingObj.field"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(field, index) in fields"
                    :key="index"
                    :label="readings.fields[field].displayName"
                    :value="readings.fields[field].name"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="8" class="mT10">
              <div>
                <el-select
                  v-model="data.readingObj.period"
                  placeholder="Please select a period"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    :label="dateRange.label"
                    :value="dateRange.queryStr"
                    v-for="(dateRange, index) in getdateOperators()"
                    :key="index"
                    v-if="dateRange.label !== 'Range'"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="8" class="mT10">
              <div>
                <el-select
                  v-model="data.readingObj.aggregation"
                  placeholder="Please select a aggregation"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    :label="agg.label"
                    :value="agg.name"
                    v-for="(agg, index) in aggregateFunctions"
                    :key="index"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </template>
        </el-row>
        <div></div>
      </el-tab-pane>
      <el-tab-pane label="Conditional formatting">
        <div
          v-for="(condition, index) in marker.conditionalFormatting"
          :key="index"
          class="relative criteria-box"
        >
          <el-row class="mT10 maxWidth90" :gutter="10">
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-input
                  v-model="markerValue.type"
                  :disabled="true"
                  class="fc-input-full-border2"
                ></el-input>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-select
                  v-model="condition.operatorId"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(value, key) in NUMBER"
                    :key="key"
                    :label="key"
                    :value="value.operatorId"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-input
                  v-model="condition.value"
                  class="fc-input-full-border2"
                ></el-input>
              </div>
            </el-col>
            <el-col :span="10" class="p10">
              <p>color</p>
              <el-color-picker
                :predefine="getPredefinedColors()"
                v-model="condition.style.color"
              ></el-color-picker>
            </el-col>
          </el-row>
          <div class="contion-action-btn pointer">
            <img
              src="~assets/add-icon.svg"
              style="height:18px;width:18px;"
              class="add-icon"
              v-if="marker.conditionalFormatting.length - 1 === index"
              @click="addContions"
            />
            <img
              src="~assets/remove-icon.svg"
              style="height:18px;width:18px;margin-right: 3px;margin-left: 3px;"
              class="delete-icon"
              v-if="marker.conditionalFormatting.length > 1"
              @click="deleteCondition(index)"
            />
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="User filter">
        <div
          v-for="(condition, index) in marker.conditionalFormatting"
          :key="index"
          class="relative criteria-box"
        >
          <el-row class="mT10 maxWidth90" :gutter="10">
            <el-col :span="8">
              <div class="fc-input-label-txt mb5"></div>
              <div>
                <el-select
                  v-model="data.userFilter.field"
                  filterable
                  placeholder="Select"
                  class="fc-border-select"
                >
                  <el-option
                    v-for="(field, idx1) in moduleFieldList.fields"
                    :key="idx1"
                    :label="field.displayName"
                    :value="field.name"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <div class="contion-action-btn pointer">
            <img
              src="~assets/add-icon.svg"
              style="height:18px;width:18px;"
              class="add-icon"
              v-if="marker.conditionalFormatting.length - 1 === index"
              @click="addContions"
            />
            <img
              src="~assets/remove-icon.svg"
              style="height:18px;width:18px;margin-right: 3px;margin-left: 3px;"
              class="delete-icon"
              v-if="marker.conditionalFormatting.length > 1"
              @click="deleteCondition(index)"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import colors from 'charts/helpers/colors'
import DateHelper from '@/mixins/DateHelper'
export default {
  props: ['marker', 'data', 'markerValue', 'moduleFieldList'],
  mixins: [DateHelper],
  data() {
    return {
      readings: null,
      markerActions: {
        URL: 1,
        Functions: 2,
        Popover: 3,
      },
      valueMap: {
        asset: {
          'Workorder Count': {
            fieldName: 'noOfWorkorders',
          },
          'Alarms Count': {
            fieldName: 'noOfAlarms',
          },
          Reading: {
            fieldName: 'reading',
          },
        },
        site: {
          'Workorder Count': {
            fieldName: 'noOfWorkorders',
          },
          'Alarms Count': {
            fieldName: 'noOfAlarms',
          },
          'Assets Count': {
            fieldName: 'noOfAssets',
          },
          Reading: {
            fieldName: 'reading',
          },
        },
        building: {
          'Workorder Count': {
            fieldName: 'noOfWorkorders',
          },
          'Alarms Count': {
            fieldName: 'noOfAlarms',
          },
          'Assets Count': {
            fieldName: 'noOfAssets',
          },
          Reading: {
            fieldName: 'reading',
          },
        },
      },
      aggregateFunctions: [
        {
          label: 'Sum',
          value: 3,
          name: 'sum',
        },
        {
          label: 'Avg',
          value: 2,
          name: 'avg',
        },
        {
          label: 'Min',
          value: 4,
          name: 'min',
        },
        {
          label: 'Max',
          value: 5,
          name: 'max',
        },
        {
          label: 'Current Value',
          value: 6,
          name: 'lastValue',
        },
      ],
      NUMBER: {
        '<=': {
          dynamicOperator: false,
          operator: '<=',
          operatorId: 12,
          valueNeeded: true,
          _name: 'LESS_THAN_EQUAL',
        },
        ' > ': {
          dynamicOperator: false,
          operator: ' > ',
          operatorId: 78,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'GREATER_THAN',
        },
        ' = ': {
          dynamicOperator: false,
          operator: ' = ',
          operatorId: 74,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'EQUAL',
        },
        ' < ': {
          dynamicOperator: false,
          operator: ' < ',
          operatorId: 76,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'LESS_THAN',
        },
        'is not empty': {
          dynamicOperator: false,
          operator: 'is not empty',
          operatorId: 2,
          valueNeeded: false,
          _name: 'IS_NOT_EMPTY',
        },
        ' != ': {
          dynamicOperator: false,
          operator: ' != ',
          operatorId: 75,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'NOT_EQUAL',
        },
        ' >= ': {
          dynamicOperator: false,
          operator: ' >= ',
          operatorId: 79,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'GREATER_THAN_EQUAL',
        },
        ' <= ': {
          dynamicOperator: false,
          operator: ' <= ',
          operatorId: 77,
          placeHoldersMandatory: true,
          valueNeeded: false,
          _name: 'LESS_THAN_EQUAL',
        },
        'is empty': {
          dynamicOperator: false,
          operator: 'is empty',
          operatorId: 1,
          valueNeeded: false,
          _name: 'IS_EMPTY',
        },
        '!=': {
          dynamicOperator: false,
          operator: '!=',
          operatorId: 10,
          valueNeeded: true,
          _name: 'NOT_EQUALS',
        },
        '<': {
          dynamicOperator: false,
          operator: '<',
          operatorId: 11,
          valueNeeded: true,
          _name: 'LESS_THAN',
        },
        '=': {
          dynamicOperator: false,
          operator: '=',
          operatorId: 9,
          valueNeeded: true,
          _name: 'EQUALS',
        },
        '>': {
          dynamicOperator: false,
          operator: '>',
          operatorId: 13,
          valueNeeded: true,
          _name: 'GREATER_THAN',
        },
        '>=': {
          dynamicOperator: false,
          operator: '>=',
          operatorId: 14,
          valueNeeded: true,
          _name: 'GREATER_THAN_EQUAL',
        },
      },
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.loadReadings()
  },
  computed: {
    assetCategory() {
      return this.$store.state.assetCategory
    },
    fields() {
      if (
        this.readings &&
        this.readings.categoryWithAssets[this.data.readingObj.categoryId]
      ) {
        return Object.keys(
          this.readings.categoryWithFields[this.data.readingObj.categoryId]
        )
      }
      return []
    },
  },
  methods: {
    loadReadings() {
      let self = this
      self.loading = true
      let url = '/asset/getreadings'
      self.$http.get(url).then(function(response) {
        self.readings = response.data
        self.loading = false
      })
    },
    setReadingModuleName() {
      this.data.readingObj.moduleName = this.assetCategory.find(
        rt => rt.id === this.data.readingObj.categoryId
      ).moduleName
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    addContions() {
      let emptyData = {
        output: 'OUTPUT',
        operatorId: 9,
        value: null,
        style: {
          icon: '',
          color: '#933F95',
        },
      }
      this.marker.conditionalFormatting.push(emptyData)
    },
    deleteCondition(index) {
      this.marker.conditionalFormatting.splice(index, 1)
    },
  },
}
</script>

<style>
.tabular-conditionalformatting .el-tabs__content {
  height: 450px;
  overflow: auto;
}

.maxWidth90 {
  max-width: 90%;
}

.contion-action-btn {
  position: absolute;
  right: 0;
  top: 45px;
  z-index: 10;
}
.contion-action-btn .add-icon {
  position: absolute;
  right: 35px;
  top: 10px;
}
</style>
