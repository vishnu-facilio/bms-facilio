<template>
  <div class="mT15">
    <el-radio-group
      v-model="colorCustomization.customizationType"
      @change="resetValue"
      class="mB25"
    >
      <el-radio :label="customizationTypes.FIELD" class="fc-radio-btn">
        {{ $t('common.header.field') }}
      </el-radio>
      <el-radio :label="customizationTypes.CONDITION" class="fc-radio-btn">
        {{ $t('common._common.condition') }}
      </el-radio>
    </el-radio-group>

    <div class="color-picker">
      <div class="flex-middle justify-between">
        <div class="fc-input-label-txt pB0">
          {{ $t('setup.scheduler.color_picked_for_condition') }}
        </div>
        <div class="mL10">
          <el-select
            v-if="
              colorCustomization.customizationType === customizationTypes.FIELD
            "
            v-model="colorCustomization.customizationFieldId"
            placeholder="Select the field"
            class="fc-input-full-border2 fc-box-size"
            filterable
            @change="resetOptions"
          >
            <el-option
              v-for="colorfield in selectFieldList"
              :key="colorfield.id"
              :label="colorfield.displayName"
              :value="colorfield.id"
            ></el-option>
          </el-select>
          <div
            v-else
            @click="openCriteriaDialog"
            :class="[
              'flex-middle justify-end',
              loading ? 'cursor-not-allowed' : 'pointer',
            ]"
            class="height40"
          >
            <div class="config justify-start">
              <i class="el-icon-plus fw-bold "></i>
              {{ $t('setup.scheduler.configure_criteria') }}
            </div>
          </div>
        </div>
      </div>

      <div v-if="loading || isColorCustomEnabled" class="pT20 border-top7 mT20">
        <el-row v-if="loading">
          <el-col
            v-for="index in customizationTypes"
            :key="`loading-${index}`"
            :span="12"
            class="mB15 flex-middle pL10 pR10"
          >
            <div class="mR10">
              <span class="square loading-shimmer"></span>
            </div>
            <div class="flex-grow">
              <span class="lines loading-shimmer"></span>
            </div>
          </el-col>
        </el-row>

        <el-row v-else>
          <el-col
            v-for="(valueObj, index) in colorCustomization.values"
            :key="`color-field-option-${index}`"
            :span="12"
            class="mB10 flex-middle pL10 pR10"
            style="min-height: 40px;"
          >
            <div class="mR10">
              <ColorPicker
                :selectedColor="valueObj.customization.eventColor"
                @currentColor="
                  colorVal => setColorValue(colorVal, valueObj.customization)
                "
              >
                <template #reference>
                  <InlineSvg
                    src="svgs/color-picker/arrow-down"
                    class="color-picker-popup"
                    :style="{
                      'background-color': valueObj.customization.eventColor,
                    }"
                    iconClass="icon icon-xxs margin-auto fill-white"
                  ></InlineSvg>
                </template>
              </ColorPicker>
            </div>
            <div class="option-text">
              {{ valueObj.label }}
            </div>
          </el-col>
          <el-col
            :span="12"
            class="mB10 flex-middle pL10 pR10"
            style="min-height: 40px;"
          >
            <div class="mR10">
              <ColorPicker
                :selectedColor="
                  colorCustomization.defaultCustomization.eventColor
                "
                @currentColor="
                  colorVal =>
                    setColorValue(
                      colorVal,
                      colorCustomization.defaultCustomization
                    )
                "
              >
                <template #reference>
                  <InlineSvg
                    src="svgs/color-picker/arrow-down"
                    class="color-picker-popup"
                    :style="{
                      'background-color':
                        colorCustomization.defaultCustomization.eventColor,
                    }"
                    iconClass="icon icon-xxs margin-auto fill-white"
                  ></InlineSvg>
                </template>
              </ColorPicker>
            </div>
            <div class="option-text">{{ `Default` }}</div>
          </el-col>
        </el-row>
      </div>
    </div>
    <ConditionManagerPicker
      v-if="showCriteriaDialog"
      :moduleName="moduleName"
      :availableFields="conditionManagerList"
      :selectedList="selectedConditionList"
      @updateList="updateCriteriaList"
      @onSave="setColorForCriteria"
      @onClose="showCriteriaDialog = false"
    ></ConditionManagerPicker>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { dataTypes, lookupType, colorPalette } from './schedulerViewUtil'
import ColorPicker from './components/ColorPicker.vue'
import ConditionManagerPicker from './components/ConditionManagerPicker.vue'
import cloneDeep from 'lodash/cloneDeep'
const customizationTypes = {
  FIELD: 1,
  CONDITION: 2,
}
export default {
  props: ['viewDetails', 'moduleFields', 'moduleName', 'saveAsNew', 'moduleId'],
  components: { ColorPicker, ConditionManagerPicker },

  data() {
    return {
      optionLoading: false,
      showCriteriaDialog: false,
      colorCustomization: {
        customizationType: customizationTypes.FIELD,
        customizationFieldId: null,
        defaultCustomization: { eventColor: '#4d95ff' },
        values: [],
      },
      conditionLoading: false,
      conditionManagerList: [],
      selectedConditionList: [],
      customizationTypes,
    }
  },
  async created() {
    if (!this.isNew || this.saveAsNew) {
      await this.deserialize()
    }
    await this.loadConditionManager()
  },
  computed: {
    isNew() {
      return isEmpty(this.$route.params.viewname)
    },
    selectFieldList() {
      let { BOOLEAN, LOOKUP, ENUM, SYSTEM_ENUM } = dataTypes
      let { PICK_LIST } = lookupType
      let { moduleFields } = this
      return (moduleFields || []).filter(fld => {
        let { dataType, lookupModule } = fld
        let { type } = lookupModule || {}
        return (
          [BOOLEAN, ENUM, SYSTEM_ENUM].includes(dataType) ||
          (LOOKUP === dataType && type === PICK_LIST)
        )
      })
    },
    isColorCustomEnabled() {
      let { colorCustomization } = this
      let { values } = colorCustomization || {}
      return !isEmpty(values)
    },
    loading() {
      return this.optionLoading || this.conditionLoading
    },
  },
  methods: {
    async deserialize() {
      let { recordCustomization } = this.viewDetails || {}

      if (!isEmpty(recordCustomization)) {
        let {
          values,
          customizationType,
          defaultCustomization,
          customizationFieldId,
        } = recordCustomization || {}

        let { FIELD } = customizationTypes
        values = values.map(val => {
          let valueObj = {}
          let { customization, namedCriteriaId, fieldValue } = val || {}

          customization = JSON.parse(customization) || {}
          if (customizationType === FIELD)
            valueObj = { customization, fieldValue }
          else valueObj = { customization, namedCriteriaId }

          return valueObj
        })
        defaultCustomization = (defaultCustomization &&
          JSON.parse(defaultCustomization)) || { eventColor: '#4d95ff' }
        this.colorCustomization = {
          customizationType,
          values,
          defaultCustomization,
        }
        if (customizationType === FIELD) {
          this.colorCustomization = {
            ...this.colorCustomization,
            customizationFieldId,
          }
          await this.loadOptions()
        }
      }
    },
    async loadOptions() {
      this.optionLoading = true

      let { BOOLEAN, LOOKUP, ENUM, SYSTEM_ENUM } = dataTypes
      let { selectFieldList, colorCustomization } = this
      let { customizationFieldId: fieldId, values } = colorCustomization || {}
      let selectedField = selectFieldList.find(f => f.id === fieldId) || {}
      let { dataType } = selectedField || {}

      if (BOOLEAN === dataType) {
        let { falseVal, trueVal } = selectedField || {}

        if (isEmpty(values)) {
          values = [
            {
              fieldValue: 'false',
              label: falseVal || 'False',
              customization: this.getColor(),
            },
            {
              fieldValue: 'true',
              label: trueVal || 'True',
              customization: this.getColor(),
            },
          ]
        } else {
          values = values.map(option => {
            let { fieldValue } = option || {}
            let label =
              fieldValue === 'true' ? trueVal || 'True' : falseVal || 'False'

            return { ...option, label }
          })
        }
      } else if ([ENUM, SYSTEM_ENUM].includes(dataType)) {
        let { enumMap } = selectedField || {}

        if (isEmpty(values)) {
          values = Object.entries(enumMap || {}).reduce(
            (colorMap, [optionId, label]) => {
              let optionObj = {
                fieldValue: parseInt(optionId),
                label,
                customization: this.getColor(),
              }
              colorMap.push(optionObj)
              return colorMap
            },
            []
          )
        } else {
          values = values.map(option => {
            let { fieldValue } = option || {}
            return { ...option, label: enumMap[fieldValue] }
          })
        }
      } else if (LOOKUP === dataType) {
        let { PICK_LIST } = lookupType
        let { lookupModule } = selectedField || {}
        let { name, type } = lookupModule || {}
        let { moduleId } = this
        let filters = {}
        if (name === 'ticketstatus')
          filters = {
            parentModuleId: { operatorId: 9, value: [`${moduleId}`] },
          }

        if (type === PICK_LIST) {
          let { data, error } = await API.get('v2/module/data/list', {
            moduleName: name,
            filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          })
          if (!error) {
            if (isEmpty(values)) {
              values = (data.moduleDatas || []).map(option => {
                let customization = {}
                let { id, colour = null, displayName, name } = option || {}

                if (colour !== null) {
                  customization = { eventColor: colour }
                } else {
                  customization = this.getColor()
                }
                return {
                  fieldValue: id,
                  label: displayName || name,
                  customization,
                }
              })
            } else {
              let optionMap = (data.moduleDatas || []).reduce(
                (optionMapObj, option) => {
                  let { id, displayName, name } = option
                  optionMapObj[id] = displayName || name
                  return optionMapObj
                },
                {}
              )
              values = values.map(option => {
                let { fieldValue } = option || {}
                return { ...option, label: optionMap[fieldValue] }
              })
            }
          }
        }
      }
      this.$set(this.colorCustomization, 'values', values)
      this.optionLoading = false
    },
    openCriteriaDialog() {
      if (!this.conditionLoading) {
        let { customizationType, values } = this.colorCustomization || {}
        let selectedConditionIds =
          customizationType === 2 ? values.map(v => v.namedCriteriaId) : []

        this.selectedConditionList = selectedConditionIds.map(criteriaId => {
          let criteriaObj = this.conditionManagerList.find(
            criteria => criteria.id === criteriaId
          )
          return criteriaObj
        })
        this.showCriteriaDialog = true
      }
    },
    setColorForCriteria(list) {
      this.optionLoading = true
      this.$nextTick(() => {
        this.colorCustomization.values = list.map(valObj => {
          let { id, name, customization } = valObj || {}
          return {
            namedCriteriaId: id,
            label: name,
            customization: customization || this.getColor(),
          }
        })
        this.optionLoading = false
      })
    },
    getColor() {
      let randomNumber = parseInt(Math.random() * 100)
      let pickColor = colorPalette[randomNumber % 21]

      return { eventColor: pickColor }
    },
    setColorValue(colorVal, customization) {
      let { eventColor } = colorVal || {}
      this.$set(customization, 'eventColor', eventColor)
    },
    resetOptions() {
      this.colorCustomization.values = []
      this.loadOptions()
    },
    resetValue(type) {
      let { defaultCustomization } = this.colorCustomization || {}
      let { FIELD } = customizationTypes
      this.colorCustomization = {
        customizationType: type,
        values: [],
        defaultCustomization,
      }
      if (type === FIELD) {
        this.colorCustomization = {
          ...this.colorCustomization,
          customizationFieldId: null,
        }
      } else {
        this.selectedConditionList = []
      }
    },
    async loadConditionManager() {
      this.conditionLoading = true

      let { moduleName } = this
      let { error, data } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let conditionManagerList = (data.namedCriteriaList || []).map(
          condition => {
            let { id, name } = condition || {}
            return {
              id,
              name,
              customization: this.getColor(),
            }
          }
        )

        let { customizationType, values } = this.colorCustomization || {}
        let conditionVsColor = {}

        if (customizationType === 2) {
          conditionVsColor = values.reduce((colorMap, valueObj) => {
            let { namedCriteriaId, customization } = valueObj

            colorMap[namedCriteriaId] = customization
            return colorMap
          }, {})

          conditionManagerList = (conditionManagerList || []).map(condition => {
            let { id, customization } = condition || {}
            return {
              ...condition,
              customization: conditionVsColor[id] || customization,
            }
          })

          let namedCriteriaIdVsName = conditionManagerList.reduce(
            (namedCriteriaOptions, criteriaObj) => {
              let { id, name } = criteriaObj || {}
              namedCriteriaOptions[id] = name
              return namedCriteriaOptions
            },
            {}
          )
          this.colorCustomization.values = values.map(valueObj => {
            let { namedCriteriaId } = valueObj
            valueObj.label = namedCriteriaIdVsName[namedCriteriaId]
            return valueObj
          })
        }
        this.conditionManagerList = conditionManagerList
      }
      this.conditionLoading = false
    },
    updateCriteriaList(criteria) {
      let { id, name } = criteria || {}
      let criteriaObj = { id, name, customization: this.getColor() }

      this.conditionManagerList.push(criteriaObj)
      this.selectedConditionList.push(criteriaObj)
    },
    serialize() {
      if (this.isColorCustomEnabled) {
        let recordCustomization = cloneDeep(this.colorCustomization)
        let { values, defaultCustomization } = recordCustomization || {}

        values = values.map(val => {
          let { customization } = val

          val.customization = JSON.stringify(customization)
          delete val.label
          return val
        })
        defaultCustomization = JSON.stringify(defaultCustomization)
        return {
          recordCustomization: {
            ...recordCustomization,
            values,
            defaultCustomization,
          },
        }
      } else {
        return {}
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.color-picker {
  .lines {
    height: 20px;
    width: 90%;
    border-radius: 5px;
  }
  .square {
    height: 28px;
    width: 28px;
    border-radius: 5px;
  }
  .header-text {
    text-transform: uppercase;
    color: #333;
    font-size: 11px;
    letter-spacing: 1px;
    font-weight: 700;
  }
  .option-text {
    font-size: 14px;
    font-weight: 400;
    letter-spacing: 0.4px;
    color: #324056;
  }
  .fill-blue {
    svg {
      path {
        fill: #3ab2c1;
      }
    }
  }
  .config {
    color: #3ab2c1;
    margin-left: 5px;
    font-size: 14px;
    letter-spacing: 0.46px;
  }
  .color-picker-popup {
    height: 28px;
    width: 28px;
    display: flex;
    border-radius: 5px;
    border: 1px solid rgb(0, 0, 0, 0.1);
  }
  .fc-box-size {
    width: 200px;
  }
}
</style>
