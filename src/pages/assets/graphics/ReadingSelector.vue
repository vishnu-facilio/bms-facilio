<template>
  <f-popover
    placement="right"
    popper-class="fbVariablePopper"
    v-model="popoverVisible"
    :title="$t('setup.formulaBuilder.set_variable')"
    trigger="click"
    width="290"
    @save="saveVariable"
  >
    <template slot="content">
      <div>
        <el-row align="middle" style="margin:0px;padding-top:20px;">
          <el-col :span="24">
            <div class="add">
              <el-select
                @change="
                  loadRelatedModule(readingInfo),
                    (readingInfo.aggregation = null)
                "
                v-model="readingInfo.module"
                style="width:100%"
                class="fc-input-full-border-select2"
                :placeholder="$t('setup.formulaBuilder.choose_module')"
              >
                <el-option key="asset" label="Asset" value="asset"></el-option>
                <el-option key="space" label="Space" value="space"></el-option>
                <el-option
                  key="assetreading"
                  label="Asset Reading"
                  value="assetreading"
                ></el-option>
                <el-option
                  key="spacereading"
                  label="Space Reading"
                  value="spacereading"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <!-- this.assetCategory.id -->
        <el-row
          v-if="
            readingInfo.module === 'asset' ||
              readingInfo.module === 'assetreading'
          "
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col
            :span="24"
            class="mB20"
            v-if="readingInfo.module === 'assetreading'"
          >
            <div class="add">
              <el-select
                @change="
                  loadRelatedModule(readingInfo), (readingInfo.assetId = null)
                "
                filterable
                v-model="readingInfo.assetCategoryId"
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Asset Category"
                v-if="$store.state.assetCategory"
              >
                <el-option
                  v-for="(category, index) in $store.state.assetCategory"
                  :key="index"
                  :label="category.displayName"
                  :value="category.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="add">
              <el-select
                @change="
                  loadRelatedModule(readingInfo),
                    (readingInfo.readingFieldId = null)
                "
                :multiple="true"
                collapse-tags
                filterable
                v-model="readingInfo.assetId"
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Asset"
              >
                <el-option
                  v-for="asset in assetList"
                  :key="'asset' + asset.id"
                  :label="asset.name"
                  :value="asset.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            readingInfo.module === 'spacereading' ||
              readingInfo.module === 'space'
          "
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                @change="loadRelatedModule(readingInfo)"
                filterable
                v-model="readingInfo.spaceId"
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Space"
              >
                <el-option
                  v-for="space in spaceList"
                  :key="'space' + space.id"
                  :label="space.name"
                  :value="space.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="readingInfo.module === 'space'"
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.spaceFieldId"
                filterable
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Field"
              >
                <el-option
                  v-for="field in spaceFields"
                  :key="'spacefields' + field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="readingInfo.module === 'spacereading' && readingInfo.spaceId"
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.spaceReadingFieldId"
                filterable
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Reading"
                @change="readingInfo.aggrCondOperator = null"
              >
                <el-option
                  v-for="field in getSpaceReadings(readingInfo.spaceId)"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="readingInfo.module === 'asset'"
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.assetFieldId"
                filterable
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Field"
              >
                <el-option
                  v-for="field in assetFields"
                  :key="'assetfields' + field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            readingInfo.module === 'assetreading' &&
              readingInfo.assetCategoryId &&
              readingInfo.assetId
          "
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.readingFieldId"
                :multiple="true"
                collapse-tags
                filterable
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Reading"
                @change="readingInfo.aggrCondOperator = null"
              >
                <el-option
                  v-for="field in getAssetReadings(readingInfo.assetCategoryId)"
                  :key="'assetreadings' + field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            readingInfo.module === 'assetreading' ||
              readingInfo.module === 'spacereading'
          "
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.aggregation"
                @change="readingInfo.dateRange = null"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Aggregation"
                style="width:100%;"
              >
                <el-option
                  v-for="(label, value) in aggregateOptions"
                  :key="'aggregation' + value"
                  :label="label"
                  :value="value"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            readingInfo.aggregation && readingInfo.aggregation !== 'lastValue'
          "
          align="middle"
          style="margin:0px;padding-top:20px;"
        >
          <el-col :span="24">
            <div class="add">
              <el-select
                v-model="readingInfo.dateRange"
                style="width:100%"
                class="form-item fc-input-full-border-select2 width280px"
                placeholder="Choose Date Range"
              >
                <el-option
                  v-for="(label, value) in dateRangeOptions"
                  :key="'dateRangeOptions' + value"
                  :label="label"
                  :value="parseInt(value)"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
      </div>
    </template>
    <slot slot="reference"></slot>
  </f-popover>
</template>

<script>
import FPopover from '@/FPopover'
import util from 'util/util'
export default {
  components: {
    FPopover,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  data() {
    return {
      popoverVisible: false,
      readingInfo: {
        module: '',
        assetCategoryId: '',
        assetId: '',
        spaceId: '',
        assetFieldId: '',
        spaceFieldId: '',
        spaceReadingFieldId: '',
        readingFieldId: '',
        aggrCondOperator: null,
        aggregation: null,
        dateRange: null,
      },
      assetList: null,
      spaceList: null,
      assetFields: null,
      spaceFields: null,
      assetReadings: {},
      spaceReadings: {},
      aggregateOptions: {
        sum: 'Sum',
        count: 'Count',
        avg: 'Average',
        min: 'Minimum',
        max: 'Maximum',
        lastValue: 'Current Value',
      },
      dateRangeOptions: {
        22: 'Today',
        25: 'Yesterday',
        31: 'Current Week',
        30: 'Last Week',
        28: 'Current Month',
        27: 'Last Month',
        44: 'Current Year',
        45: 'Last Year',
      },
    }
  },
  methods: {
    loadRelatedModule(expression) {
      if (expression.module === 'assetreading') {
        if (expression.assetCategoryId) {
          return this.loadAssets(expression.assetCategoryId).then(() =>
            this.loadAssetReadings(expression.assetCategoryId)
          )
        } else {
          this.assetList = null
        }
      } else if (expression.module === 'asset') {
        return this.loadAssets().then(() => this.loadAssetFields())
      } else if (expression.module === 'spacereading') {
        return this.loadSpaces().then(() =>
          this.loadSpaceReadings(expression.spaceId)
        )
      } else if (expression.module === 'space') {
        return this.loadSpaces().then(() => this.loadSpaceFields())
      }
      return Promise.resolve()
    },
    loadAssets(categoryId) {
      let params = { withReadings: true }
      if (categoryId) {
        params.categoryId = categoryId
      }
      return util.loadAsset(params).then(response => {
        if (response.assets) {
          this.assetList = response.assets
        }
      })
    },
    loadAssetFields() {
      if (this.assetFields) {
        return Promise.resolve()
      }
      return util.loadFields('asset').then(fields => {
        this.assetFields = fields
      })
    },
    loadAssetReadings(categoryId) {
      if (this.assetReadings[categoryId]) {
        return Promise.resolve()
      }
      return util.loadAssetReadingFields(-1, categoryId).then(fields => {
        this.assetReadings[categoryId] = fields
        this.$forceUpdate()
      })
    },
    getAssetReadings(assetCategoryId) {
      if (this.assetReadings[assetCategoryId]) {
        return this.assetReadings[assetCategoryId]
      }
      return []
    },
    loadSpaces() {
      if (this.spaceList) {
        return Promise.resolve()
      }
      return util.loadSpace().then(response => {
        if (response.basespaces) {
          this.spaceList = response.basespaces
        }
      })
    },
    loadSpaceFields() {
      if (this.spaceFields) {
        return Promise.resolve()
      }
      return util.loadFields('basespace').then(fields => {
        this.spaceFields = fields
      })
    },
    loadSpaceReadings(spaceId) {
      if (!spaceId) {
        return
      }
      if (this.spaceReadings[spaceId]) {
        return Promise.resolve()
      }
      return util.loadSpaceReadingFields(spaceId).then(fields => {
        this.spaceReadings[spaceId] = fields
        this.$forceUpdate()
      })
    },
    getSpaceReadings(spaceId) {
      if (this.spaceReadings[spaceId]) {
        return this.spaceReadings[spaceId]
      }
      return []
    },
    getSpaceField(id) {
      return this.spaceFields.find(spaceField => spaceField.id === id)
    },
    getAssetField(id) {
      return this.assetFields.find(assetField => assetField.id === id)
    },
    getSpaceName(id) {
      return this.spaceList.length
        ? this.spaceList.find(space => space.id === id).name
        : ''
    },
    getAssetName(id) {
      return this.assetList.length
        ? this.assetList.find(asset => asset.id === id).name
        : ''
    },
    getReadingField(id, objId, type) {
      if (type === 'asset') {
        let field = this.getAssetReadings(objId).find(
          readingField => readingField.id === id
        )
        if (field) {
          return field
        }
      } else if (type === 'space') {
        let field = this.getSpaceReadings(objId).find(
          readingField => readingField.id === id
        )
        if (field) {
          return field
        }
      }
      return null
    },
    getVariables(expression) {
      let variables = []
      if (expression.module === 'asset') {
        if (expression.assetId && expression.assetId.length) {
          for (let at of expression.assetId) {
            variables.push(
              this.getVariableInfo({
                module: expression.module,
                assetCategoryId: expression.assetCategoryId,
                assetId: at,
                spaceId: expression.spaceId,
                assetFieldId: expression.assetFieldId,
                spaceFieldId: expression.spaceFieldId,
                spaceReadingFieldId: expression.spaceReadingFieldId,
                readingFieldId: expression.readingFieldId,
                aggrCondOperator: expression.aggrCondOperator,
                aggregation: expression.aggregation,
                dateRange: expression.dateRange,
              })
            )
          }
        }
      } else if (expression.module === 'assetreading') {
        if (
          expression.assetId &&
          expression.assetId.length &&
          expression.readingFieldId &&
          expression.readingFieldId.length
        ) {
          for (let at of expression.assetId) {
            for (let rd of expression.readingFieldId) {
              variables.push(
                this.getVariableInfo({
                  module: expression.module,
                  assetCategoryId: expression.assetCategoryId,
                  assetId: at,
                  spaceId: expression.spaceId,
                  assetFieldId: expression.assetFieldId,
                  spaceFieldId: expression.spaceFieldId,
                  spaceReadingFieldId: expression.spaceReadingFieldId,
                  readingFieldId: rd,
                  aggrCondOperator: expression.aggrCondOperator,
                  aggregation: expression.aggregation,
                  dateRange: expression.dateRange,
                })
              )
            }
          }
        }
      } else {
        variables.push(this.getVariableInfo(expression))
      }
      return variables
    },
    getVariableInfo(expression) {
      let label = ''
      let resourceName = ''
      let moduleName = ''
      let select = ''
      let dataType = ''
      let fetchType = ''
      let parentId = null
      let unit = ''
      let aggr = ''
      let dateRange = ''

      if (expression.module === 'asset') {
        moduleName = 'asset'
        if (expression.assetId) {
          resourceName = this.getAssetName(expression.assetId)
          label = label + resourceName
          parentId = expression.assetId
        }
        if (expression.assetFieldId) {
          let assetFieldObj = this.getAssetField(expression.assetFieldId)
          label = label + ' - ' + assetFieldObj.displayName
          select = assetFieldObj.name
          dataType = assetFieldObj.dataTypeEnum._name
          fetchType = 'field'
          unit = assetFieldObj.unit
        }
        if (expression.aggregation) {
          label = label + ' - ' + this.aggregateOptions[expression.aggregation]
        }
      } else if (expression.module === 'space') {
        moduleName = 'basespace'
        if (expression.spaceId) {
          resourceName = this.getSpaceName(expression.spaceId)
          label = label + resourceName
          parentId = expression.spaceId
        }
        if (expression.spaceFieldId) {
          let spaceFieldObj = this.getSpaceField(expression.spaceFieldId)
          label = label + ' - ' + spaceFieldObj.displayName
          select = spaceFieldObj.name
          dataType = spaceFieldObj.dataTypeEnum._name
          fetchType = 'field'
          unit = spaceFieldObj.unit
        }
        if (expression.aggregation) {
          label = label + ' - ' + this.aggregateOptions[expression.aggregation]
        }
      } else if (expression.module === 'assetreading') {
        if (expression.assetId) {
          resourceName = this.getAssetName(expression.assetId)
          label = label + resourceName
          parentId = expression.assetId
        }
        if (expression.readingFieldId) {
          let readingFieldObj = this.getReadingField(
            expression.readingFieldId,
            expression.assetCategoryId,
            'asset'
          )
          label = label + ' - ' + readingFieldObj.displayName
          moduleName = readingFieldObj.module.name
          select = readingFieldObj.name
          dataType = readingFieldObj.dataTypeEnum._name
          unit = readingFieldObj.unit
        }
        if (expression.aggregation) {
          label = label + ' - ' + this.aggregateOptions[expression.aggregation]
          if (expression.aggregation !== 'lastValue') {
            fetchType = 'aggrValue'
            aggr = expression.aggregation
            dateRange = expression.dateRange
          } else {
            fetchType = 'liveValue'
          }
        }
        if (expression.dateRange) {
          label += ' - ' + this.dateRangeOptions[expression.dateRange]
        }
      } else if (expression.module === 'spacereading') {
        if (expression.spaceId) {
          resourceName = this.getSpaceName(expression.spaceId)
          label = label + resourceName
          parentId = expression.spaceId
        }
        if (expression.spaceReadingFieldId) {
          let readingFieldObj = this.getReadingField(
            expression.spaceReadingFieldId,
            expression.spaceId,
            'space'
          )
          label = label + ' - ' + readingFieldObj.displayName
          moduleName = readingFieldObj.module.name
          select = readingFieldObj.name
          dataType = readingFieldObj.dataTypeEnum._name
          unit = readingFieldObj.unit
        }
        if (expression.aggregation) {
          label = label + ' - ' + this.aggregateOptions[expression.aggregation]
          if (expression.aggregation !== 'lastValue') {
            fetchType = 'aggrValue'
            aggr = expression.aggregation
            dateRange = expression.dateRange
          } else {
            fetchType = 'liveValue'
          }
        }
        if (expression.dateRange) {
          label += ' - ' + this.dateRangeOptions[expression.dateRange]
        }
      }
      let varName =
        fetchType !== 'field'
          ? this.getUniqueKey(resourceName) + '.reading.' + select
          : this.getUniqueKey(resourceName) + '.' + select
      if (aggr) {
        varName += '.' + aggr
      }
      if (dateRange) {
        varName += '.' + this.getUniqueKey(this.dateRangeOptions[dateRange])
      }
      return {
        label: label,
        key: varName,
        fetchType: fetchType,
        module: moduleName,
        select: select,
        dataType: dataType,
        parentId: parentId,
        unit: unit,
        type: 'custom',
        aggr: aggr,
        dateRange: this.dateRangeOptions[dateRange],
      }
    },
    getUniqueKey(label) {
      if (!label) {
        return ''
      }
      return label.replace(/[^A-Z0-9]/gi, '_').toLowerCase()
    },
    saveVariable() {
      this.popoverVisible = false
      let varName = this.getVariables(this.readingInfo)

      this.$emit('add', varName)
      this.readingInfo = {
        module: '',
        assetId: '',
        spaceId: '',
        assetFieldId: '',
        spaceFieldId: '',
        spaceReadingFieldId: '',
        readingFieldId: '',
        aggrCondOperator: null,
        aggregation: null,
        dateRange: null,
      }
    },
  },
}
</script>

<style></style>
