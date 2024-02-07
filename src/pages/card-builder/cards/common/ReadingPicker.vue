<template>
  <div>
    <el-row
      v-for="(obj, index) in readings"
      class="row mB10"
      :key="obj.id + index + 'row'"
      :gutter="10"
    >
      <el-col :span="!isDynamicKpi() ? 12 : isMultiMode ? 8 : 9">
        <el-form-item
          v-if="options['parentId'].type === 'multiple' || index === 0"
        >
          <el-input
            v-model="obj.parentName"
            :disabled="true"
            type="text"
            placeholder="Space/Asset"
            class="fc-input-full-border-select2"
          >
            <i
              @click="showPicker(index)"
              slot="suffix"
              style="
                line-height: 0px !important;
                font-size: 16px !important;
                cursor: pointer;
              "
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
          <space-asset-chooser
            v-if="obj.pickerVisibility"
            @associate="
              (resource, resourceType) =>
                associate(index, resource, resourceType)
            "
            :visibility.sync="obj.pickerVisibility"
            :query="quickSearchQuery"
            :picktype="options.pickerModule ? options.pickerModule : null"
            :showAsset="true"
            :closeOnClickModel="true"
          ></space-asset-chooser>
        </el-form-item>
        <el-form-item v-else>
          <el-input
            v-model="readings[0].parentName"
            :disabled="true"
            type="text"
            placeholder="Space/Asset"
            class="fc-input-full-border-select2"
          >
            <i
              slot="suffix"
              style="line-height: 0px !important; font-size: 16px !important;"
              class="el-input__icon el-icon-search"
            ></i>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="!isDynamicKpi() ? 12 : isMultiMode ? 9 : 10">
        <el-form-item
          v-if="options['fieldName'].type === 'multiple' || index === 0"
        >
          <div v-if="isRender">
            <div v-if="loading">
              <FieldLoader :isLoading="true"> </FieldLoader>
            </div>
            <div
              v-else-if="
                obj.selectedFieldId ||
                  obj.fieldName == null ||
                  obj.fieldName == undefined ||
                  obj.fieldName == ''
              "
            >
              <el-select
                v-model="obj.selectedFieldId"
                filterable
                placeholder="Reading"
                class="report-dropDown el-input-textbox-full-border"
                :clearable="true"
                @change="() => setDatapoint(index, obj.selectedFieldId)"
              >
                <el-option
                  v-for="(field, idx) in readingFields"
                  :key="idx"
                  :label="readingName(field)"
                  :value="
                    JSON.stringify({
                      id: field.id,
                      name: field.name,
                      kpiType: field.kpiType,
                    })
                  "
                >
                </el-option>
              </el-select>
            </div>
            <div v-else>
              <el-select
                v-model="obj.fieldName"
                filterable
                placeholder="Reading"
                class="report-dropDown el-input-textbox-full-border"
                :clearable="true"
                @change="() => setDatapoint(index, obj.fieldName)"
              >
                <el-option
                  v-for="(field, idx) in readingFields"
                  :key="idx"
                  :label="readingName(field)"
                  :value="field.name"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </el-form-item>
        <el-form-item v-else>
          <el-select
            v-model="readings[0].fieldName"
            filterable
            placeholder="Reading"
            class="report-dropDown el-input-textbox-full-border"
            disabled
          >
            <el-option
              v-for="(field, idx) in readingFields"
              :key="idx"
              :label="readingName(field)"
              :value="field.name"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col
        v-if="options['yAggr'] && isDynamicKpi()"
        :span="isMultiMode ? 4 : 5"
      >
        <el-form-item
          v-if="options['yAggr'].type === 'multiple' || index === 0"
        >
          <el-select
            v-model="obj.yAggr"
            placeholder="Aggr"
            class="width100 el-input-textbox-full-border"
            @change="emitReading"
          >
            <el-option
              v-for="(fn, index) in aggregateFunctions"
              :label="fn.label"
              :value="fn.value"
              :key="index"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-else>
          <el-select
            v-model="readings[0].yAggr"
            placeholder="Aggr"
            class="width100 el-input-textbox-full-border"
            disabled
          >
            <el-option
              v-for="(fn, index) in aggregateFunctions"
              :label="fn.label"
              :value="fn.value"
              :key="index"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <div
        v-if="isMultiMode"
        :key="obj.id + index + ''"
        class="reading-controls"
      >
        <img
          src="~assets/add-icon.svg"
          style="margin-right: 6px;"
          class="delete-icon pointer"
          @click="addRow()"
          v-if="index === readings.length - 1 && readings.length < 10"
        />
        <img
          src="~assets/remove-icon.svg"
          v-if="readings.length !== 1"
          style="margin-right: 3px;"
          class="delete-icon pointer"
          @click="deleteRow(index)"
        />
      </div>
    </el-row>
  </div>
</template>

<script>
import { v4 as uuid } from 'uuid'
import { aggregateFunctions } from 'pages/card-builder/card-constants'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FieldLoader from '@/forms/FieldLoader'

export default {
  name: 'ReadingPicker',
  components: {
    SpaceAssetChooser,
    FieldLoader,
  },
  props: {
    gaugeCard: {
      type: Boolean,
    },
    options: {
      required: true,
      type: Object,
      default: () => ({
        pickerModule: 'asset',
      }),
    },
    initialReading: {
      type: Object,
    },
  },
  data() {
    return {
      loading: false,
      selectedIndex: -1,
      quickSearchQuery: '',
      readingFields: [],
      readings: [
        {
          id: uuid(),
          pickerVisibility: false,
          parentType: null,
          parentName: null,
          parentId: null,
          moduleName: null,
          fieldName: null,
          fieldId: null,
          yAggr: null,
          selectedFieldId: null,
        },
      ],
      fieldName: null,
      aggregateFunctions,
      isRender: true,
    }
  },
  created() {
    if (!isEmpty(this.initialReading))
      this.deserializeReading(this.initialReading)
  },
  computed: {
    isMultiMode() {
      return !isEmpty(
        Object.values(this.options).find(rt => rt.type === 'multiple')
      )
    },
  },
  methods: {
    isDynamicKpi() {
      return !(this.readings[0].kpiType === 'DYNAMIC')
    },
    readingName(field) {
      return !isEmpty(field.displayName) ? field.displayName : field.name
    },
    setDatapoint(index, fieldName) {
      const decimal = 3
      let fieldobj
      try {
        let fieldNameObj = JSON.parse(fieldName)
        if (!isEmpty(fieldNameObj) && typeof fieldNameObj === 'object') {
          this.readings[index].selectedFieldId = fieldName
          fieldobj = this.readingFields.find(rt => {
            let condition =
              rt.id === fieldNameObj.id && rt.name === fieldNameObj.name
            if (!isEmpty(fieldNameObj.kpiType)) {
              return rt.kpiType === fieldNameObj.kpiType && condition
            }
            return condition
          })
        }
      } catch (e) {
        fieldobj = this.readingFields.find(rt => rt.name === fieldName)
        this.readings[index].selectedFieldId = {
          id: fieldobj.id,
          name: fieldobj.name,
        }
      } finally {
        this.readings[index].kpiType = fieldobj.kpiType
        this.readings[index].moduleName = fieldobj?.module?.name
        this.readings[index].fieldName = fieldobj.name
        this.readings[index].fieldId = fieldobj.id
        this.readings[index].dataType = fieldobj?.dataType || decimal
        this.isRender = false
        this.emitReading()
        this.$nextTick(() => {
          this.isRender = true
        })
      }
    },

    emitReading() {
      let { readings } = this
      this.$emit('onReadingSelect', this.serializeReading(readings))
    },

    validateReading(readings) {
      return readings.reduce((acc, reading) => {
        Object.values(reading).forEach(value => {
          if (isEmpty(value)) acc = false
        })

        return acc
      }, true)
    },

    serializeReading(readings) {
      let formattedReading = {}
      let reading = readings[0]

      for (let [key, value] of Object.entries(this.options)) {
        if (value.type === 'single') {
          this.$set(formattedReading, key, reading[key])
        } else if (value.type === 'multiple') {
          this.$set(
            formattedReading,
            key,
            readings.map(rt => rt[key])
          )
        }
      }
      return formattedReading
    },

    deserializeReading(initialReading) {
      this.loading = true
      if (this.isMultiMode) {
        let readings = []

        let readingCount = Object.values(initialReading).reduce(
          (acc, value) => {
            if (isArray(value)) acc = value.length
            return acc
          },
          1
        )

        for (let i = 0; i < readingCount; i++) readings.push(this.getEmptyObj())

        Object.entries(initialReading).forEach(([key, value]) => {
          if (isArray(value)) {
            readings.forEach((reading, index) => (reading[key] = value[index]))
          } else {
            readings.forEach(r => (r[key] = value))
          }
        })

        this.$set(this, 'readings', readings)
        this.loadReadingFields(0, readings[0].parentId, readings[0].parentType)
      } else {
        this.$set(this.readings, 0, { ...initialReading, id: uuid() })
        let { readings } = this
        this.loadReadingFields(0, readings[0].parentId, readings[0].parentType)
      }
    },

    loadReadingFields(index, parentId, resourceType) {
      if (!isEmpty(parentId)) {
        let promise =
          resourceType === 'space'
            ? this.$util.loadSpaceReadingFields(parentId)
            : this.$util.loadAssetReadingFields(parentId)

        return promise.then(async readingFields => {
          let dynamicKpisList = []
          if (this.gaugeCard === true && resourceType === 'asset') {
            let { data, error } = await API.get(
              '/v3/readingKpi/analytics/getDynamicKpisForAsset',
              {
                assetId: parentId,
              }
            )
            if (!error) {
              let { dynamicKpis } = data || {}

              if (!isEmpty(dynamicKpis)) {
                dynamicKpisList = dynamicKpis.map(kpi => {
                  kpi.kpiType = 'DYNAMIC'
                  return kpi
                })
              }
            }
          }
          this.readingFields = [...readingFields, ...dynamicKpisList]
          this.loading = false
        })
      }
      this.loading = false
    },

    showPicker(index) {
      this.$set(this.readings, index, {
        ...this.readings[index],
        pickerVisibility: true,
      })
    },

    associate(index, resource, resourceType) {
      this.readings[index].pickerVisibility = false
      this.readings[index].parentName = resource.name
      this.readings[index].parentId = resource.id
      this.readings[index].parentType = resourceType
      this.readings[index].selectedFieldId = null
      this.readings[index].fieldName = null
      this.readings[index].kpiType = null
      this.readings[index].moduleName = null
      this.readings[index].fieldId = null
      this.readings[index].dataType = null
      this.loadReadingFields(index, resource.id, resourceType)

      this.emitReading()
    },

    getEmptyObj() {
      return {
        id: uuid(),
        pickerVisibility: false,
        parentType: null,
        parentName: null,
        moduleName: null,
        fieldName: null,
        parentId: null,
        fieldId: null,
        yAggr: null,
      }
    },

    addRow() {
      let { readings } = this
      if (this.readings.length < 10) {
        this.$set(this, 'readings', [...readings, this.getEmptyObj()])
        this.$forceUpdate()
      }
    },

    deleteRow(index) {
      this.readings.splice(index, 1)
      this.$forceUpdate()
    },
  },
}
</script>
<style lang="scss" scoped>
.reading-controls {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 45px;
  margin-left: 10px;

  .delete-icon {
    position: relative;
    height: 18px;
    width: 18px;
  }
}
</style>
