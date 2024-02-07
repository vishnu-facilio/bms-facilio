<template>
  <div class="p30">
    <div class="header">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'Live Data Card' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section">
        <el-form
          :model="cardDataObj"
          :ref="`${this.cardLayout}_form`"
          :rules="validationRules"
          :label-position="'top'"
        >
          <el-row class="mB10">
            <el-col :span="24">
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

          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item class="mB10">
                <p class="fc-input-label-txt pB5">Asset</p>
                <el-input
                  v-model="selectedResourceName"
                  :disabled="true"
                  type="text"
                  placeholder="Choose Asset"
                  class="fc-input-full-border-select2"
                  @click="pickerVisibility = true"
                >
                  <i
                    @click="pickerVisibility = true"
                    slot="suffix"
                    style=""
                    class="el-input__icon el-icon-search search-icon"
                  ></i>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mB10">
            <el-col :span="7">
              <el-form-item class="mB10">
                <p class="fc-input-label-txt pB0">Show Labels</p>
                <el-checkbox v-model="cardStateObj.canShowLabels"></el-checkbox>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item class="mB10">
                <p class="fc-input-label-txt pB0">Show Icons</p>
                <el-checkbox v-model="cardStateObj.canShowIcons"></el-checkbox>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item class="mB10">
                <p class="fc-input-label-txt pB0">Header Color</p>
                <div class="d-flex mT5 card-color-container">
                  <el-color-picker
                    v-model="cardStateObj.styles.headerColor"
                    :key="'headerColor' + cardStateObj.styles.headerColor"
                    :predefine="predefinedColors"
                    size="small"
                    class="fc-color-picker"
                  ></el-color-picker>
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mT30 mB10">
            <el-col :span="24">
              <el-form-item prop="readings" class="mB10">
                <p class="config-section-header mB0">Readings</p>
                <p class="config-section-header-desc mB20">
                  Configure data points, labels and icons
                </p>
                <div
                  v-for="(item, index) in cardDataObj.readings"
                  :key="`item-${item.id}`"
                  class="d-flex flex-direction-row mB20 position-relative item-row"
                >
                  <el-select
                    v-model="item.readingField"
                    placeholder="Choose Reading"
                    class="fc-input-full-border2 flex-grow mR15"
                    @change="r => setReading(r, index)"
                    value-key="name"
                    filterable
                  >
                    <el-option
                      v-for="reading in assetReadings"
                      :key="reading.id"
                      :value="reading"
                      :label="reading.displayName"
                    ></el-option>
                  </el-select>

                  <el-select
                    v-if="cardStateObj.canShowIcons"
                    v-model="item.icon"
                    :filterable="true"
                    :default-first-option="true"
                    class="fc-input-full-border2 icon-picker mR15"
                    value-key="name"
                    :disabled="!cardStateObj.canShowIcons"
                    placeholder="Icon"
                  >
                    <inline-svg
                      slot="prefix"
                      v-if="item.icon && item.icon.path"
                      :src="item.icon.path"
                      iconClass="icon icon-sm-md"
                      class="mR10"
                    ></inline-svg>
                    <el-option
                      v-for="(icon, idx) in graphicIcons"
                      :key="idx + icon.name"
                      :value="icon"
                      :label="icon.name"
                    >
                      <inline-svg
                        :src="icon.path"
                        class="vertical-middle"
                        iconClass="icon"
                      ></inline-svg>
                    </el-option>
                  </el-select>

                  <el-input
                    v-if="cardStateObj.canShowLabels"
                    v-model="item.label"
                    class="width180px mR15 fc-input-full-border2"
                    placeholder="Enter a label"
                  ></el-input>

                  <div class="d-flex justify-content-start item-controls f16">
                    <img
                      src="~assets/add-icon.svg"
                      style="height:18px;width:18px;"
                      class="pointer mR10"
                      @click="addItem(index)"
                      v-if="
                        index === cardDataObj.readings.length - 1 && index < 10
                      "
                      title="Add Reading"
                      v-tippy
                    />
                    <img
                      src="~assets/remove-icon.svg"
                      v-if="cardDataObj.readings.length !== 1"
                      style="height:18px;width:18px;"
                      class="pointer"
                      @click="removeItem(index)"
                      title="Remove Reading"
                      v-tippy
                    />
                  </div>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardData="previewData"
            :cardState="previewState"
            :loading="isPreviewLoading"
          ></Card>
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
    <space-asset-chooser
      @associate="selectResource"
      :visibility.sync="pickerVisibility"
      :query="quickSearchQuery"
      picktype="asset"
      :showAsset="true"
      :appendToBody="true"
      :closeOnClickModel="true"
      :filter="{}"
    ></space-asset-chooser>
  </div>
</template>
<script>
import Config from '../base/Config'
import Card from './Card'
import { isEmpty } from '@facilio/utils/validation'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import {
  predefinedColors,
  graphicIcons,
} from 'pages/card-builder/card-constants'
import { v4 as uuid } from 'uuid'
import util from 'util/util'

export default {
  name: 'GraphicCard1',
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
  components: { Card, SpaceAssetChooser },
  data() {
    return {
      cardLayout: `graphicalcard_layout_1`,
      isPreviewLoading: false,
      pickerVisibility: false,
      selectedResource: null,
      selectedResourceName: null,
      selectedResourceType: null,
      assetReadings: [],
      quickSearchQuery: null,
      resourceProps: ['title', 'readings'],
      cardDataObj: {
        readings: [
          {
            id: 0,
            label: null,
            icon: null,
            reading: {},
            readingField: {},
          },
        ],
        title: null,
      },
      icon: null,
      cardStateObj: {
        canResize: true,
        canShowLabels: true,
        canShowIcons: true,
        styles: {
          headerColor: '#483a9e',
        },
      },
      layout: {
        w: 24,
        h: 12,
      },
      result: null,
      validationRules: {},
      predefinedColors,
      graphicIcons,
    }
  },
  computed: {
    previewData() {
      return !isEmpty(this.result)
        ? this.result.data
        : {
            title: 'AHU 1',
            lastUpdated: Date.now(),
            values: [
              {
                unit: '°C',
                dataType: 'DECIMAL',
                icon: 'thermometer',
                label: 'Temperature',
                value: 24.0,
              },
              {
                unit: null,
                dataType: 'BOOLEAN',
                icon: 'bulb',
                label: 'Status',
                value: true,
              },
              {
                unit: null,
                dataType: 'BOOLEAN',
                icon: 'supplyfan',
                label: 'Fan Status',
                value: true,
              },
            ],
          }
    },
  },
  methods: {
    selectResource(resource, type) {
      if (isEmpty(resource)) return

      this.selectedResource = resource
      this.selectedResourceName = resource.name
      this.selectedResourceType = type

      if (isEmpty(this.cardDataObj.title)) {
        this.cardDataObj.title = resource.name
      }

      this.pickerVisibility = !this.pickerVisibility
      this.resetSelections()
      this.loadReadings(resource)
    },
    resetSelections() {
      this.cardDataObj.readings.forEach(r => {
        this.$set(r, 'readingField', {})
        this.$set(r, 'reading', {})
      })
    },
    loadReadings(asset) {
      let promise = this.isNew
        ? util.loadAssetReadingFields(-1, asset.category.id)
        : util.loadAssetReadingFields(asset.id)

      return promise.then(fields => {
        this.assetReadings = fields
      })
    },

    setReading(reading, index) {
      if (isEmpty(reading)) {
        return
      }
      let {
        cardDataObj,
        cardDataObj: { readings },
      } = this

      let data = {
        moduleName: reading.module.name,
        fieldName: reading.name,
        parentId: this.selectedResource.id,
        parentName: this.selectedResource.name,
        parentType: this.selectedResourceType,
      }

      this.$setProperty(readings, `${index}.reading`, data)
      if (isEmpty(readings[index].label))
        this.$setProperty(readings, `${index}.label`, reading.displayName)

      this.$set(this, 'cardDataObj', { ...cardDataObj, readings })
    },

    addItem() {
      let { readings } = this.cardDataObj
      if (readings.length < 10)
        this.$set(this.cardDataObj, 'readings', [
          ...readings,
          {
            id: uuid(),
            label: null,
            icon: null,
            reading: {},
            readingField: {},
          },
        ])
    },
    removeItem(index) {
      let { readings } = this.cardDataObj
      let readingArray = [...readings]
      readingArray.splice(index, 1)
      this.$set(this.cardDataObj, 'readings', readingArray)
    },

    validateReadingField(reading) {
      let readingProps = ['moduleName', 'parentId', 'fieldName']
      let hasIcon = false,
        hasLabel = false,
        hasReading = false

      let { canShowIcons, canShowLabels } = this.cardStateObj

      if (canShowIcons) {
        hasIcon = !isEmpty(reading.icon)
      } else {
        hasIcon = true
      }
      if (canShowLabels) {
        hasLabel = !isEmpty(reading.label)
      } else {
        hasLabel = true
      }

      if (isEmpty(reading)) {
        hasReading = false
      } else {
        hasReading = true
      }

      readingProps.forEach(prop => {
        hasReading = !isEmpty(reading['reading'][prop])
      })

      return hasIcon && hasLabel && hasReading
    },
    validateProperty() {
      return {
        readings: data => {
          if (isEmpty(data.readings)) return true
          else {
            let errors = data.readings.map(r => !this.validateReadingField(r))

            return errors.reduce((acc, error) => acc || error, false)
          }
        },
      }
    },
    validateField() {
      let validator = function(rule, value, callback) {
        let { readings } = this.cardDataObj
        readings.forEach(reading => {
          if (!this.validateReadingField(reading)) {
            callback(new Error('Reading Fields can not be empty'))
          }
        })
        callback()
      }.bind(this)

      return {
        readings: {
          trigger: 'change',
          validator,
        },
      }
    },

    serializeProperty(prop, data) {
      if (prop === 'readings') {
        return data.readings.reduce((acc, item) => {
          acc.push({
            label: item.label,
            icon: item.icon ? item.icon.name : '',
            reading: { ...item.reading, yAggr: 'lastValue' },
          })
          return acc
        }, [])
      }
    },

    deserializeProperty(prop, data) {
      if (prop === 'readings') {
        let { readings } = data
        let [readingObj] = readings

        this.$set(this, 'selectedResourceName', readingObj.reading.parentName)
        this.$set(this, 'selectedResourceType', readingObj.reading.parentType)
        this.$set(this, 'selectedResource', {
          id: readingObj.reading.parentId,
          name: readingObj.reading.parentName,
        })

        this.cardDataObj['readings'] = readings

        this.loadReadings(this.selectedResource).then(() => {
          readings.forEach(reading => {
            this.$set(reading, 'id', uuid())

            let iconObj =
              graphicIcons.find(({ name }) => name === reading.icon) || null
            this.$set(reading, 'icon', iconObj)

            let readingField = this.assetReadings.find(
              rf => rf.name === reading.reading.fieldName
            ) || { name: reading.reading.fieldName }
            this.$set(reading, 'readingField', readingField)
          })
        })
      } else this.$set(this.cardDataObj, prop, data[prop])
    },
  },
}
</script>
<style scoped lang="scss">
.header {
  font-size: 22px;
  font-weight: 300;
}
.card-wrapper {
  width: 280px;
}
.search-icon {
  line-height: 0 !important;
  font-size: 15px !important;
  cursor: pointer !important;
  padding-top: 5px;
}
.item-controls {
  align-items: center;
  min-width: 80px;
}
</style>
<style lang="scss">
.icon-picker {
  width: 60px;

  .el-input__prefix {
    background: #fff;
    width: 85%;
    height: 30px;
    top: 5px;
    text-align: center;
    overflow: hidden;
  }
}
</style>
