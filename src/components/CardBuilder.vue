<template>
  <div>
    <el-dialog
      custom-class="f-card-builder"
      :modal-append-to-body="false"
      :visible.sync="visibility"
      :width="assetcardEdit ? '30%' : '80%'"
      top="0%"
      title="Card Builder"
      :before-close="closedialog"
    >
      <div class="editLoading" v-if="editLoading">
        <i class="el-icon-loading"></i>
      </div>
      <div class="f-builder-body">
        <div class="row">
          <div class="col-8 assetcard-layout" v-if="!assetcardEdit">
            <div class="row">
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'AHU' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/AHU.png"
                  @click="cardData.cardType = 'AHU'"
                  style="height:170px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'PUMP' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/pump.png"
                  @click="cardData.cardType = 'PUMP'"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'PUMP1' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/pump.png"
                  @click="cardData.cardType = 'PUMP1'"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'LIGHT' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/Light-layout.png"
                  @click="cardData.cardType = 'LIGHT'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/AHUMaincard1.png"
                  @click="cardData.cardType = 'HVAC'"
                  style="height:170px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'WATER_TANK' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/Water-tank.png"
                  @click="cardData.cardType = 'WATER_TANK'"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC2' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/AHUMaincard3.jpg"
                  @click="cardData.cardType = 'HVAC2'"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC3' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/AHUMaincard2.png"
                  @click="cardData.cardType = 'HVAC3'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC4' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC4.png"
                  @click="cardData.cardType = 'HVAC4'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC5' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC5.png"
                  @click="cardData.cardType = 'HVAC5'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC6' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC6.png"
                  @click="cardData.cardType = 'HVAC6'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC7' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC7.png"
                  @click="cardData.cardType = 'HVAC7'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC8' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC8.png"
                  @click="cardData.cardType = 'HVAC8'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC9' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC9.png"
                  @click="cardData.cardType = 'HVAC9'"
                  style="height:140px;width:110px;"
                />
              </div>
              <div
                class="col-4 cb-layout-section"
                v-bind:class="{ active: cardData.cardType === 'HVAC10' }"
              >
                <img
                  class="card-icons"
                  src="~statics/cardIcon/HVAC10.png"
                  @click="cardData.cardType = 'HVAC10'"
                  style="height:140px;width:110px;"
                />
              </div>
            </div>
          </div>
          <div
            class="assetcard-dataChooser"
            :class="assetcardEdit ? 'col-12' : 'col-4'"
          >
            <el-form
              :model="cardData"
              ref="AssetCardForm"
              :label-position="'top'"
            >
              <el-row>
                <el-col :span="24">
                  <el-form-item prop="category">
                    <p class="grey-text2">Select category</p>
                    <el-select
                      style="width: 90%"
                      filterable
                      collapse-tags
                      v-model="cardData.selectedCategoryId"
                      @change="loadAssetbyCategory()"
                    >
                      <el-option
                        v-for="(category, index) in assetCategory"
                        :key="index"
                        :label="category.name"
                        :value="category.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row v-if="assetList && cardData.cardType">
                <el-col :span="24">
                  <el-form-item prop="category">
                    <p class="grey-text2">Select assets</p>
                    <el-select
                      style="width: 90%"
                      filterable
                      collapse-tags
                      v-model="cardData.parentId"
                      @change="loadNewReadingFileds()"
                    >
                      <el-option
                        v-for="(asset, key) in assetscategory"
                        :key="key"
                        :label="assetList[key]"
                        :value="key"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row
                v-if="fieldList && cardData.parentId"
                v-for="(readingKey, key) in templates[cardData.cardType]"
                :key="key"
              >
                <el-col :span="8">
                  <el-form-item class="pT10">
                    <img
                      class="card-icons"
                      :src="
                        getIconPath(templates[cardData.cardType][key].icon.path)
                      "
                      v-if="
                        key &&
                          templates[cardData.cardType][key] &&
                          templates[cardData.cardType][key].icon &&
                          templates[cardData.cardType][key].icon.property &&
                          templates[cardData.cardType][key].icon.property ===
                            'URL'
                      "
                    />
                    <span
                      v-if="
                        key &&
                          templates[cardData.cardType][key] &&
                          templates[cardData.cardType][key].icon &&
                          templates[cardData.cardType][key].icon.property &&
                          templates[cardData.cardType][key].icon.property ===
                            'TEXT'
                      "
                      >{{ templates[cardData.cardType][key].icon.path }}</span
                    >
                    <div
                      v-if="
                        key &&
                          templates[cardData.cardType][key] &&
                          templates[cardData.cardType][key].icon &&
                          templates[cardData.cardType][key].icon.property &&
                          templates[cardData.cardType][key].icon.property ===
                            'BUTTON'
                      "
                      class="button-filed"
                      :class="templates[cardData.cardType][key].icon.size"
                    >
                      {{ templates[cardData.cardType][key].icon.path }}
                    </div>
                  </el-form-item>
                </el-col>
                <el-col
                  :span="16"
                  v-if="
                    templates[cardData.cardType][key].inputType === 'SELECT'
                  "
                >
                  <el-form-item prop="category">
                    <p class="grey-text2">
                      {{ 'Select ' + readingKey.displayName + ' Field' }}
                    </p>
                    <el-select
                      style="width: 90%"
                      filterable
                      collapse-tags
                      v-model="templates[cardData.cardType][key].value"
                      @change="loadField(key)"
                    >
                      <el-option
                        v-for="(field, index) in fieldList"
                        :key="index"
                        :label="field.field.displayName"
                        :value="field.field.id"
                        v-if="
                          templates[cardData.cardType][key].Rkey !== 'all' &&
                            field.field[
                              templates[cardData.cardType][key].Rkey
                            ] === templates[cardData.cardType][key].Rvalue
                        "
                      ></el-option>
                      <el-option
                        v-for="(field, index) in fieldList"
                        :key="index"
                        :label="field.field.displayName"
                        :value="field.field.id"
                        v-if="templates[cardData.cardType][key].Rkey === 'all'"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col
                  :span="16"
                  v-if="templates[cardData.cardType][key].inputType === 'TEXT'"
                >
                  <el-form-item prop="category">
                    <p class="grey-text2">Enter the text</p>
                    <el-input
                      v-model="templates[cardData.cardType][key].value"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </div>
      </div>
      <span slot="footer" class="modal-dialog-footer row">
        <el-button class="col-6 modal-btn-cancel" @click="closedialog"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          class="col-6 modal-btn-save"
          @click="update"
          v-if="assetcardEdit"
          >Update</el-button
        >
        <el-button
          type="primary"
          class="col-6 modal-btn-save"
          @click="save"
          v-else
          >Ok</el-button
        >
      </span>
    </el-dialog>
    <template></template>
  </div>
</template>

<script>
import cardHelper from '@/mixins/AssetCardHelper'
import { mapState } from 'vuex'

export default {
  mixins: [cardHelper],
  props: ['visibility', 'addAssetCard', 'assetcardEdit'],
  data() {
    return {
      loading: false,
      editLoading: false,
      readings: null,
      assetList: null,
      cardtype: [
        'AHU',
        'PUMP',
        'LIGHT',
        'WATER_TANK',
        'HVAC',
        'HVAC2',
        'HVAC3',
        'HVAC4',
        'HVAC5',
        'HVAC6',
        'HVAC7',
        'PUMP1',
      ],
      workflowTemplate: {
        name: null,
        aggregateString: 'lastValue',
        fieldName: null,
        fieldId: -1,
        moduleName: 'energydata',
        criteria: {
          pattern: '1',
          conditions: {
            '1': {
              fieldName: 'parentId',
              operatorId: 9,
              sequence: '1',
              value: null,
            },
          },
        },
        orderByFieldName: 'ttime',
        sortBy: 'desc',
        limit: 1,
      },
      iconsUrl: {
        name: '',
        runStatus: 'cardIcon/off-on-icon.svg',
        autoStatus: '',
        temperature: '',
        tripStatus: '',
        valveFeedback: '',
      },
      assetscategory: null,
      fieldList: null,
      cardData: {
        selectedCategoryId: null,
        parentId: null,
        fieldIds: null,
        cardType: 'AHU',
      },
    }
  },

  created() {
    this.$store.dispatch('loadAssetCategory')
  },

  mounted() {
    this.loadReadings()
    if (this.assetcardEdit) {
      this.editLoading = true
    }
  },

  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },

  methods: {
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    editCardBuilder() {
      if (this.addAssetCard) {
        this.cardData = this.addAssetCard.cardData
          ? this.addAssetCard.cardData
          : null
        this.fieldList = this.addAssetCard.fieldList
          ? this.addAssetCard.fieldList
          : null
        this.tools = this.addAssetCard.tools ? this.addAssetCard.tools : null
        this.templates[this.cardData.cardType] = this.addAssetCard.templates
          ? this.addAssetCard.templates
          : null
        this.categoryWithAssets(this.readings)
        this.editLoading = false
      }
    },
    save() {
      let self = this
      this.$emit('update:dialogVisible', false)
      let expressionsData = []
      let metadata = {}
      Object.keys(this.templates[this.cardData.cardType]).forEach(rt => {
        if (
          self.templates[this.cardData.cardType][rt].inputType !== 'TEXT' &&
          self.templates[this.cardData.cardType][rt].value !== null
        ) {
          expressionsData.push(
            self.constractData(self.templates[this.cardData.cardType][rt], rt)
          )
        }
      })
      Object.keys(this.templates[this.cardData.cardType]).forEach(rt => {
        if (
          self.templates[this.cardData.cardType][rt].inputType === 'TEXT' &&
          self.templates[this.cardData.cardType][rt].value !== null
        ) {
          metadata[rt] = self.templates[this.cardData.cardType][rt].value
        }
        /* temp checkonly */
        metadata.cardType = this.cardData.cardType
      })
      let data = {
        templates: this.templates[this.cardData.cardType],
        parentId: this.cardData.parentId,
        expressions: expressionsData,
        metaJson: metadata,
        cardType: this.cardData.cardType,
        cardData: this.cardData,
        fieldList: this.fieldList,
        tools: this.tools,
      }
      this.$emit('save', data)
    },
    update() {
      let self = this
      this.$emit('update:dialogVisible', false)
      let expressionsData = []
      let metadata = {}
      Object.keys(this.templates[this.cardData.cardType]).forEach(rt => {
        if (
          self.templates[this.cardData.cardType][rt].inputType !== 'TEXT' &&
          self.templates[this.cardData.cardType][rt].value !== null
        ) {
          expressionsData.push(
            self.constractData(self.templates[this.cardData.cardType][rt], rt)
          )
        }
      })
      Object.keys(this.templates[this.cardData.cardType]).forEach(rt => {
        if (
          self.templates[this.cardData.cardType][rt].inputType === 'TEXT' &&
          self.templates[this.cardData.cardType][rt].value !== null
        ) {
          metadata[rt] = self.templates[this.cardData.cardType][rt].value
        }
        /* temp checkonly */
        metadata.cardType = this.cardData.cardType
      })
      let data = {
        templates: this.templates[this.cardData.cardType],
        parentId: this.cardData.parentId,
        expressions: expressionsData,
        metaJson: metadata,
        cardType: this.cardData.cardType,
        cardData: this.cardData,
        fieldList: this.fieldList,
        tools: this.tools,
      }
      this.$emit('update', data)
    },
    constractData(rawData, key) {
      let data = this.$helpers.cloneObject(this.workflowTemplate)
      if (rawData) {
        if (rawData.inputType !== 'TEXT') {
          data.name = key
          data.fieldName =
            rawData.readings && rawData.readings.name
              ? rawData.readings.name
              : null
          data.fieldId =
            rawData.readings && rawData.readings.id ? rawData.readings.id : null
          data.criteria.conditions['1'].value = this.cardData.parentId
          data.moduleName =
            rawData.readings &&
            rawData.readings.module &&
            rawData.readings.module.name
              ? rawData.readings.module.name
              : 'energydata'
          return data
        }
      }
    },
    loadAssetbyCategory() {
      this.cardData.parentId = null
      this.categoryWithAssets(this.readings)
    },
    loadReadings() {
      let self = this
      self.$http.get('/asset/getreadings').then(function(response) {
        self.readings = response.data
        self.assetList =
          response.data && response.data.assets ? response.data.assets : null
        if (self.assetcardEdit) {
          self.editCardBuilder()
        }
      })
    },
    categoryWithAssets(data) {
      if (data && this.cardData.selectedCategoryId && data.categoryWithAssets) {
        this.assetscategory =
          data.categoryWithAssets[this.cardData.selectedCategoryId]
      }
    },
    loadAssetCategory() {
      if (
        this.cardData.parentId &&
        this.readings.fields &&
        this.readings.fields
      ) {
        this.fieldList = []
        let fields = Object.values(this.readings.fields)
        let fieldIdList = this.readings.categoryWithAssets[
          this.cardData.selectedCategoryId
        ][Number(this.cardData.parentId)]
        this.fieldList = fields.filter(rt => {
          for (let index = 0; index < fieldIdList.length; index++) {
            const element = fieldIdList[index]
            if (parseInt(element) === rt.id) {
              rt.toggle = false
              return rt
            }
          }
        })
      }
    },
    loadNewReadingFileds() {
      let self = this
      Object.keys(this.tools).forEach(rt => {
        this.tools[rt].value = null
      })
      if (
        this.cardData.parentId &&
        this.readings.fields &&
        this.readings.fields
      ) {
        this.fieldList = []
        this.$util
          .loadLatestReading(Number(this.cardData.parentId))
          .then(fields => {
            self.fieldList = fields
          })
      }
    },
    loadField(key) {
      this.templates[this.cardData.cardType][
        key
      ].readings = this.readings.fields[
        this.templates[this.cardData.cardType][key].value
      ]
    },
    getIconPath(iconPath) {
      if (iconPath.indexOf('cardIcon') > -1) {
        let path = []
        path = iconPath.split('/')
        iconPath = path[path.length - 1]
      }
      return require('statics/cardIcon/' + iconPath)
    },
  },
}
</script>
<style>
.assetcard-dataChoose {
  max-height: 60vh;
  overflow: auto;
}
.f-card-builder .el-dialog__header {
  display: inline;
}
.button-filed.big {
  width: 30px;
  height: 30px;
  background: #e3e1e1;
  border-radius: 20px;
  height: 90pz;
  max-width: 21% !important;
  text-align: center;
  align-items: center;
  position: absolute;
  top: 10px;
  font-size: 16px;
  font-weight: 500;
  display: inline-flex;
  padding-left: 10px;
}
.assetcard-dataChooser {
  max-height: 70vh;
  overflow: auto;
}
.assetcard-dataChooser {
  max-height: 70vh;
  overflow: auto;
}

.assetcard-layout .card-icons {
  width: 100px;
  height: 120px;
  margin: auto;
  cursor: pointer;
}

.assetcard-layout .cb-layout-section {
  height: 200px;
  align-items: center;
  margin: auto;
  display: inline-flex;
  /* cursor: pointer; */
  /* padding: 30px; */
  background: #f7f8f9;
}
.assetcard-layout .cb-layout-section.active {
  background: #f4d3e1;
}
.assetcard-layout {
  padding-right: 30px;
  padding-bottom: 15px;
}
.editLoading {
  width: calc(100% - 0px);
  height: calc(100% - -2px);
  z-index: 2000;
  background: #000;
  position: absolute;
  opacity: 0.2;
  cursor: not-allowed;
  margin-left: -20px;
  top: 0;
}
.editLoading i.el-icon-loading {
  font-size: 60px;
  color: #fff;
  position: absolute;
  top: 25%;
  left: 45%;
  text-align: center;
  align-items: center;
}
.f-card-builde .el-dialog__body {
  min-height: 70vh;
}
.col-8.assetcard-layout {
  height: 65vh;
  overflow: auto;
}
</style>
