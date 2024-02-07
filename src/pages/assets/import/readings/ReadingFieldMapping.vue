<template>
  <div class="import-field-mapping-body">
    <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
      FIELD MAPPING
    </div>
    <div class="fc-heading-border-width43 mT10 height2px"></div>
    <div class="height100 pT20">
      <div>
        <div class="border-bottom13 pB25">
          <span
            @click="toggleTemplateDialog"
            class="fR import-template-save-txt pointer"
            >Save this as Template</span
          >
        </div>
      </div>
      <div>
        <el-row :disabled="lockAssetName" :gutter="30" class="mT40">
          <el-col :span="10">
            <el-radio
              @change="loadSpecificModules"
              v-model="radio"
              label="single"
              class="fc-radio-btn"
              >{{ getOptionLabel('single') }}</el-radio
            >
            <div class="import-radio-description">
              Map points to a single asset
            </div>
          </el-col>
          <el-col :span="12">
            <el-radio
              @change="loadReadingsForAllModule"
              v-model="radio"
              label="multiple"
              class="fc-radio-btn"
              >{{ getOptionLabel('multiple') }}</el-radio
            >
            <div class="import-radio-description">
              Map points to a multiple assets
            </div>
          </el-col>
        </el-row>
        <el-row v-if="radio === 'single'" class="mT20">
          <el-col :span="8">
            <div class="pT30 fc-input-label-txt">Name</div>
          </el-col>
          <el-col :span="12">
            <el-select
              :disabled="lockAssetName"
              filterable
              @change="loadReadingsForSpecificModule"
              v-model="parentId"
              placeholder="Select"
              class="mT20 fc-input-full-border2"
            >
              <el-option
                v-for="(sub, index) in moduleNameList"
                :key="index"
                :label="sub.name"
                :value="sub.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <div v-if="radio === 'multiple'">
          <el-row class="mT40">
            <el-col :span="8">
              <div class="mT10 fc-input-label-txt">Identity Column</div>
            </el-col>
            <el-col :span="12" class="el-select-block">
              <el-select
                @remove-tag="clearIdentityColumns"
                v-model="uniqueColumn"
                multiple
                filterable
                collapse-tags
                default-first-option
                placeholder="Select Identity column"
                class="fc-input-full-border2"
              >
                <el-option
                  v-for="(item, index1) in filteredColumnHeadings"
                  :key="index1"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select>
              <span></span>
            </el-col>
          </el-row>
          <el-row
            v-for="(column, index2) in uniqueMap || []"
            :key="index2"
            class="mT20"
          >
            <el-col :span="8">
              <div class="mT10 fc-input-label-txt">{{ column }}</div>
            </el-col>
            <el-col :span="12">
              <el-select
                @clear="clearIdentityColumns(column)"
                v-model="uniqueMappingValues[column]"
                clearable
                filterable
                placeholder="Select"
                class="fc-input-full-border2"
              >
                <el-option
                  v-for="(value, index3) in importProcessContext.fields"
                  :key="index3"
                  :label="
                    importProcessContext.facilioFieldMapping[value]
                      ? importProcessContext.facilioFieldMapping[value]
                          .displayName
                      : value
                  "
                  :value="value"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <el-row class="mT20">
          <el-col :span="8">
            <div class="mT10 label-txt-red">Time</div>
          </el-col>
          <el-col :span="8">
            <el-select
              @change="getDateFormatForTimeColumn"
              v-model="timeMapping"
              clearable
              filterable
              placeholder="Select Time column"
              class="fc-input-full-border2"
            >
              <el-option
                v-for="(column1, index4) in filteredColumnHeadings"
                clearable
                :key="index4"
                :label="column1"
                :value="column1"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select
              v-if="timeMappingDateFormats != null"
              :placeholder="timeMappingDateFormats[0]"
              v-model="timeMappingDateFormatString"
              class="fc-input-full-border2"
            >
              <el-option
                v-for="(dateFormat, dateFormatIndex) in timeMappingDateFormats"
                :label="dateFormat"
                :value="dateFormat"
                :key="dateFormatIndex"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <div v-if="showReadings" class="mT40">
        <el-table
          :data="columnHeading"
          height="410"
          v-loading="tableDataLoading"
          class="import-table width100"
        >
          <template slot="empty">
            <div class="mT50">
              <img src="~statics/noData-light.png" width="100" height="100" />
              <div class="mT10 label-txt-black f14">
                No field mapping available
              </div>
            </div>
          </template>
          <el-table-column
            prop="name"
            label="COLUMNS"
            :width="radio === 'single' ? 150 : 300"
          >
            <template v-slot="field">
              <div class="label-txt-black">{{ field.row.heading }}</div>
            </template>
          </el-table-column>
          <el-table-column
            label="READINGS"
            :width="radio === 'single' ? 250 : 300"
          >
            <template v-slot="field">
              <el-select
                clearable
                filterable
                @change="getDateFormat(field.row.heading)"
                v-model="fieldMappingValues[field.row.heading]"
                placeholder="Select"
                class="fc-input-full-border-select2"
                :class="[
                  radio === 'single' ? 'width170px' : 'import-select-width',
                ]"
              >
                <el-option
                  v-for="(reading, index6) in Object.keys(readingsList)"
                  :key="index6"
                  :value="Object.keys(readingsList)[index6]"
                  :label="
                    readingsList[Object.keys(readingsList)[index6]].displayName
                  "
                ></el-option>
              </el-select>
              <el-tooltip
                v-if="dateToggles[field.row.heading].dateToggle"
                content="Choose a date format"
                class="item"
                effect="dark"
                placement="right-start"
              >
                <el-popover
                  v-model="dateToggles[field.row.heading].datePopoverToggle"
                  trigger="click"
                  width="200"
                  placement="right"
                >
                  <div
                    class="date-format pointer pB10"
                    @click="setDateFormat(field.row.heading, columnDateFormat)"
                    v-bind:class="{
                      active:
                        dateFormatMapping[field.row.heading] ===
                        columnDateFormat,
                    }"
                    v-for="(columnDateFormat, columnDateFormatIndex) in field
                      .row.dateFormats"
                    :key="columnDateFormatIndex"
                  >
                    {{ columnDateFormat }}
                  </div>
                  <i
                    v-if="dateToggles[field.row.heading].dateToggle"
                    @click="toggleDateFormatPopover(field.row.heading)"
                    class="el-icon-date"
                    :class="{
                      'fc-red3': !dateFormatMapping[field.row.heading],
                    }"
                    slot="reference"
                  ></i>
                </el-popover>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="SAMPLE DATA">
            <template v-slot="field">
              <div class="fc-grey3-text14">{{ field.row.sample }}</div>
            </template>
          </el-table-column>
          <el-table-column
            label="INPUT UNIT"
            v-if="radio === 'single'"
            width="150"
          >
            <template v-slot="field">
              <div class="fc-grey3-text14">
                {{ getInputUnit(fieldMappingValues[field.row.heading]) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="DISPLAY UNIT"
            v-if="radio === 'single'"
            width="150"
          >
            <template v-slot="field">
              <div class="fc-grey3-text14">
                {{
                  $getProperty(
                    readingsList,
                    `${fieldMappingValues[field.row.heading]}.unit`
                  )
                }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else>
        <div class="text-align-center mT40 border-top6">
          <img
            class="mT50"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="goBacktoBeginning" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button
        :loading="loading"
        @click="beginDataValidation"
        type="primary"
        class="modal-btn-save"
      >
        Proceed to next
        <img
          src="~assets/arrow-pointing-white-right.svg"
          width="17px"
          class="fR"
        />
      </el-button>
    </div>
    <div>
      <el-dialog
        :visible.sync="newTemplateDialog"
        :fullscreen="false"
        open="top"
        width="30%"
        :title="$t('setup.setupLabel.add_template')"
        :append-to-body="true"
        custom-class="fc-dialog-center-container"
      >
        <div class="height200">
          <p class="fc-input-label-txt">Template Name</p>
          <div>
            <el-input
              :placeholder="$t('common._common.enter_name')"
              v-model="templateName"
              class="fc-input-full-border2"
            ></el-input>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="newTemplateDialog = false"
            >CANCEL</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="checkForName"
            :loading="loading"
            >DONE</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import importHelper from '../v1/ImportHelper'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { isDateField, isDateTimeField } from '@facilio/utils/field'
export default {
  props: ['importProcessContext'],
  mixins: [importHelper],
  data() {
    return {
      tableDataLoading: false,
      loading: false,
      lockAssetName: false,
      dateToggles: {},
      saveToggle: true,
      existingTemplateNames: [],
      templateName: '',
      newTemplateDialog: false,
      showReadings: false,
      timeMapping: '',
      timeMappingDateFormatString: '',
      timeMappingDateFormats: [],
      dateFormatMapping: {},
      uniqueColumn: [],
      moduleName: null,
      uniqueMappingValues: {},
      fieldMappingValues: {},
      importTemplateContext: null,
      moduleNameList: {},
      uniqueMappingColumns: [],
      readingsList: {},
      templateCheck: true,
      parentId: null,
      moduleMap: null,
      radio: 'single',
      template: null,
      moduleMeta: null,
      templateList: null,
      unitIdVsObjMap: {},
      recordFieldIdVsRDM: {},
    }
  },
  computed: {
    filteredColumnHeadings() {
      let columns = []
      if (this.importProcessContext) {
        for (let column of this.importProcessContext.columnHeadings) {
          if (!isEmpty(column)) {
            columns.push(column)
          }
        }
        return columns
      }
      return []
    },
    columnHeading() {
      let columnHeadings = []
      if (this.timeMapping === '') {
        for (let index in this.importProcessContext.columnHeadings) {
          if (
            !isEmpty(this.importProcessContext.columnHeadings[index]) &&
            !this.uniqueMap.includes(
              this.importProcessContext.columnHeadings[index]
            )
          ) {
            let temp = {}
            temp['heading'] = this.importProcessContext.columnHeadings[index]
            temp['sample'] = this.importProcessContext.firstRow[
              this.importProcessContext.columnHeadings[index]
            ]
            temp['dateFormats'] = []
            columnHeadings.push(temp)
          }
        }
      } else {
        for (let index in this.importProcessContext.columnHeadings) {
          if (
            !isEmpty(this.importProcessContext.columnHeadings[index]) &&
            this.importProcessContext.columnHeadings[index] !==
              this.timeMapping &&
            !this.uniqueMap.includes(
              this.importProcessContext.columnHeadings[index]
            )
          ) {
            let temp = {}
            temp['heading'] = this.importProcessContext.columnHeadings[index]
            temp['sample'] = this.importProcessContext.firstRow[
              this.importProcessContext.columnHeadings[index]
            ]
            temp['dateFormats'] = []
            columnHeadings.push(temp)
          }
        }
      }
      return columnHeadings
    },
    uniqueMap() {
      if (this.uniqueMappingColumns.length !== 0) {
        return this.uniqueMappingColumns
      } else {
        let temp = []
        if (this.uniqueColumn.length !== 0) {
          for (let index in this.uniqueColumn) {
            temp.push(this.uniqueColumn[index])
          }
        }
        return temp
      }
    },
    computedModuleName() {
      let temp = {}
      if (Object.keys(this.uniqueMappingValues).length === 0) {
        temp['baseModule'] = this.moduleMeta['baseModule']
        temp['subModule'] = this.moduleMeta['module']
        temp['subModuleFilter'] = this.moduleMeta.subModuleFilter
          ? this.moduleMeta.subModuleFilter
          : ''
        if (this.moduleMeta['baseModule'] === 'asset') {
          temp['moduleForClient'] = this.moduleMeta.moduleForClient
          for (let key in this.moduleNameList) {
            if (this.moduleNameList[key].id === this.parentId) {
              temp['moduleName'] = this.moduleNameList[key].name
              temp['parentId'] = this.moduleNameList[key].id
            } else {
              continue
            }
          }
        } else {
          temp['parentId'] = this.parentId
          for (let key in this.moduleNameList) {
            if (this.moduleNameList[key].id === this.parentId) {
              temp['moduleName'] = this.moduleNameList[key].name
            } else {
              continue
            }
          }
        }
      } else {
        temp['baseModule'] = this.moduleMeta['baseModule']
        temp['subModule'] = this.moduleMeta['module']
        temp['parentId'] = this.moduleMeta['parentId']
        temp['moduleForClient'] = this.moduleMeta.moduleForClient
        temp['subModuleFilter'] = this.moduleMeta.subModuleFilter
          ? this.moduleMeta.subModuleFilter
          : ''
      }
      return temp
    },
  },
  mounted() {
    this.$emit('hideBanner', false)
  },
  created() {
    this.moduleMeta = this.$getProperty(
      this.importProcessContext,
      'importJobMetaJson.moduleInfo'
    )
    this.$store.dispatch('loadAssetCategory')
    this.createDateToggles()

    this.loadDefaultUnitMetrics()
    this.$http('/v2/importTemplate/getnames')
      .then(({ data: { responseCode, result, message } }) => {
        if (responseCode === 0) {
          let { importTemplateContext } = result
          this.templateList = importTemplateContext
          for (let template of importTemplateContext || []) {
            this.existingTemplateNames.push(template.templateName)
          }
          this.loadData()
        } else {
          throw new Error(message)
        }
      })
      .catch(({ message }) => {
        this.$message.error(message)
      })
  },
  methods: {
    loadData() {
      if (!isEmpty(this.importProcessContext.templateId)) {
        this.template = this.templateList.find(
          i => i.id === this.importProcessContext.templateId
        )
      }
      if (!isEmpty(this.template)) {
        if (this.template.fieldMappingString !== '{}') {
          let fieldMapping = JSON.parse(this.template.fieldMappingString)
          for (let keys in Object.keys(fieldMapping)) {
            let field = Object.keys(fieldMapping)[keys]
            let columnName = fieldMapping[Object.keys(fieldMapping)[keys]]
            if (field === 'sys__ttime') {
              this.timeMapping = columnName
            } else {
              this.fieldMappingValues[columnName] = field
            }
          }
        }
        if (this.template.uniqueMappingString !== '{}') {
          let uniqueMapping = JSON.parse(this.template.uniqueMappingString)
          for (let keys in Object.keys(uniqueMapping)) {
            let field = Object.keys(uniqueMapping)[keys]
            let columnName = uniqueMapping[Object.keys(uniqueMapping)[keys]]
            this.uniqueMappingValues[columnName] = field
            this.uniqueMappingColumns.push(columnName)
            this.uniqueColumn.push(columnName)
          }
          this.radio = 'multiple'
        }
        if (this.template.templateMeta !== '{}') {
          let templateMeta = JSON.parse(this.template.templateMeta)
          let dateFormats = templateMeta['dateFormats']
          for (let dateFormatColumn in dateFormats) {
            if (dateFormatColumn === this.timeMapping) {
              if (dateFormats[dateFormatColumn] === 'TimeStamp') {
                this.timeMappingDateFormats = null
                this.timeMappingDateFormatString = 'TimeStamp'
              } else {
                this.timeMappingDateFormatString = dateFormats[this.timeMapping]
              }
            } else {
              if (dateFormats[dateFormatColumn] === 'TimeStamp') {
                this.$set(
                  this.dateFormatMapping,
                  dateFormatColumn,
                  dateFormats[dateFormatColumn]
                )
                this.$set(
                  this.dateToggles[dateFormatColumn],
                  'dateToggle',
                  false
                )
              } else {
                this.$set(
                  this.dateFormatMapping,
                  dateFormatColumn,
                  dateFormats[dateFormatColumn]
                )
                this.$set(
                  this.dateToggles[dateFormatColumn],
                  'dateToggle',
                  true
                )
              }
            }
          }
        }
        if (this.radio !== 'multiple') {
          let moduleInfo = JSON.parse(this.template.module)
          this.parentId = moduleInfo.parentId
          this.radio = 'single'
        }
        this.lockAssetName = true
        this.loadSpecificModules()
        this.saveToggle = true
        this.templateCheck = false
        this.$nextTick(() => {
          this.showReadings = true
        })
      } else {
        this.loadSpecificModules()
      }
    },
    getOptionLabel(mode) {
      let temp = mode === 'single' ? 'Single ' : 'Multiple '
      temp =
        temp +
        (this.moduleMeta
          ? this.moduleMeta.baseModule === 'space'
            ? 'Space'
            : 'Asset'
          : 'Asset')
      return temp
    },
    clearIdentityColumns(columnName) {
      if (this.uniqueColumn.includes(columnName)) {
        if (this.uniqueMappingValues[columnName] === '') {
          delete this.uniqueMappingValues[columnName]
        }
      } else {
        delete this.uniqueMappingValues[columnName]
      }
    },
    createDateToggles() {
      for (let index in this.columnHeading) {
        let columnName = this.columnHeading[index].heading
        let temp = {}
        temp['dateToggle'] = false
        temp['datePopoverToggle'] = false
        this.$set(this.dateToggles, columnName, temp)
      }
    },
    setDateFormat(columnHeading, dateFormat) {
      this.$set(this.dateFormatMapping, columnHeading, dateFormat)
      this.$set(this.dateToggles[columnHeading], 'datePopoverToggle', false)
    },
    getDateFormatForTimeColumn() {
      if (this.timeMapping === '') {
        this.timeMapping = ''
        this.timeMappingDateFormatString = ''
        this.timeMappingDateFormats = []
      } else {
        let temp = this.getDateFormats(
          this.importProcessContext.firstRow[this.timeMapping]
        )
        if (temp === null) {
          temp = []
          temp.push('Incompatible format')
          this.timeMappingDateFormats = temp
        } else if (temp.length === 1) {
          if (temp[0] === 'TimeStamp') {
            this.timeMappingDateFormats = null
            this.timeMappingDateFormatString = 'TimeStamp'
          } else {
            this.timeMappingDateFormats = temp.slice()
          }
        } else {
          this.timeMappingDateFormats = temp.slice()
        }
      }
    },
    getDateFormat(columnName) {
      if (
        this.fieldMappingValues[columnName] === '' ||
        this.fieldMappingValues[columnName] === null
      ) {
        this.dateToggles[columnName].dateToggle = false
        this.dateToggles[columnName].datePopoverToggle = false
        delete this.fieldMappingValues[columnName]
        delete this.dateFormatMapping[columnName]
        for (let key in this.columnHeading) {
          if (this.columnHeading[key].heading === columnName) {
            this.columnHeading[key].dateFormats = []
            break
          }
        }
      } else {
        if (this.isDateOrDateTimeField(this.fieldMappingValues[columnName])) {
          for (let key in this.columnHeading) {
            if (this.columnHeading[key].heading === columnName) {
              let temp = this.getDateFormats(
                this.importProcessContext.firstRow[columnName]
              )
              if (temp === null) {
                this.columnHeading[key].dateFormats.push('Incompatible Format')
                this.$set(this.dateToggles[columnName], 'dateToggle', true)
              } else if (temp.length === 1) {
                if (temp[0] === 'TimeStamp') {
                  this.$set(this.dateToggles[columnName], 'dateToggle', false)
                  this.$set(this.dateFormatMapping, columnName, 'TimeStamp')
                } else {
                  this.columnHeading[key].dateFormats = temp
                  this.$set(this.dateFormatMapping, columnName, temp[0])
                  this.$set(this.dateToggles[columnName], 'dateToggle', true)
                }
              } else {
                this.columnHeading[key].dateFormats = temp
                this.$set(this.dateToggles[columnName], 'dateToggle', true)
              }
            }
          }
        }
      }
      // check if the selected field is a date field
    },

    isDateOrDateTimeField(fieldName) {
      let field = this.readingsList[fieldName] || {}

      return !isEmpty(field)
        ? isDateField(field) || isDateTimeField(field)
        : false
    },
    goBacktoBeginning() {
      this.$emit('begin', 0)
    },
    checkForName() {
      if (this.existingTemplateNames.includes(this.templateName)) {
        this.$message.error({
          showClose: true,
          message: 'Template name needs to be unique',
        })
      } else {
        this.newTemplateDialog = false
      }
    },
    loadSpecificModules() {
      if (this.moduleMeta['baseModule'] === 'asset') {
        let assetCategory = this.$store.state.assetCategory
        let id = 0
        for (let assetIndex in assetCategory) {
          let category = assetCategory[assetIndex]
          if (
            category.name.toLowerCase() ===
            this.moduleMeta.moduleForClient.toLowerCase()
          ) {
            id = category.id
          }
        }
        let filter = { category: { operator: 'is', value: [id + ''] } }
        this.$http
          .get(
            '/asset/all?filters=' + encodeURIComponent(JSON.stringify(filter))
          )
          .then(response => {
            let assets = response.data.assets
            this.moduleNameList = {}
            for (let i = 0; i < assets.length; i++) {
              let asset = assets[i]
              this.moduleNameList[asset.id] = asset
            }
            if (this.template !== null) {
              if (this.radio === 'single') {
                this.loadReadingsForSpecificModule()
              } else {
                this.loadReadingsForAllModule()
              }
            }
          })
      } else {
        this.$http
          .post('/reading/getallspacetypereadings', {
            spaceType: this.moduleMeta['module'] + 's',
          })
          .then(response => {
            this.moduleMap = response.data.moduleMap
            let keys = Object.keys(response.data.spaces)
            this.moduleNameList = {}
            for (let i = 0; i < keys.length; i++) {
              let details = response.data.spaces[keys[i]]
              this.moduleNameList[details.name] = details
            }
            if (this.template !== null) {
              if (this.radio === 'single') {
                this.loadReadingsForSpecificModule()
              } else {
                this.loadReadingsForAllModule()
              }
            }
          })
      }
    },
    loadReadingsForSpecificModule() {
      this.readingsList = {}
      if (this.moduleMeta['baseModule'] === 'asset') {
        let categoryId = 0
        for (let key in this.moduleNameList) {
          let entry = this.moduleNameList[key]
          if (entry.id === this.parentId) {
            categoryId = entry.category.id
          }
        }
        this.tableDataLoading = true
        Promise.all([
          this.loadRDM(),
          this.loadAssetReadings(categoryId),
        ]).finally(() => {
          this.tableDataLoading = false
        })
      } else {
        Promise.all([this.loadRDM(), this.loadSpaceReadings()]).finally(() => {
          this.tableDataLoading = false
        })
      }
    },
    loadReadingsForAllModule() {
      this.readingsList = {}
      if (this.moduleMeta['baseModule'] === 'asset') {
        this.$http(
          '/reading/getassetreadings?excludeEmptyFields=false&parentCategoryId=' +
            this.moduleMeta['parentId']
        ).then(response => {
          let tempObject = {}
          for (let j = 0; j < response.data.length; j++) {
            tempObject[j] = response.data[j]
          }
          this.moduleMap = tempObject
          for (let moduleMapKey in this.moduleMap) {
            let temp = this.moduleMap[moduleMapKey]
            if (temp.length !== 0) {
              let fields = temp.fields
              for (let j = 0; j < fields.length; j++) {
                let tempModule = {}
                tempModule['name'] = temp.name
                fields[j].module = tempModule
                this.$set(
                  this.readingsList,
                  temp.name + '__' + fields[j].name,
                  fields[j]
                )
              }
            }
          }
          this.showReadings = true
        })
      } else {
        this.$http
          .post('/reading/getallspacetypereadings', {
            spaceType: this.moduleMeta['module'] + 's',
          })
          .then(response => {
            this.moduleMap = response.data.moduleMap
            for (let moduleMapKey in this.moduleMap) {
              let temp = this.moduleMap[moduleMapKey]
              if (temp.length !== 0) {
                for (let i = 0; i < temp.length; i++) {
                  let fields = temp[i].fields
                  for (let j = 0; j < fields.length; j++) {
                    let tempModule = {}
                    tempModule['name'] = temp[i].name
                    fields[j].module = tempModule
                    this.readingsList[temp[i].name + '__' + fields[j].name] =
                      fields[j]
                  }
                }
              }
            }
            if (this.moduleMeta.hasOwnProperty('subModuleFilter')) {
              this.filterForSubModuleFields()
            }
            this.showReadings = true
          })
      }
    },
    populateImportProcessContext(data) {
      let keys = Object.keys(data)
      for (let i = 0; i < keys.length; i++) {
        let fields = Object.keys(data[keys[i]].fields)
        for (let j = 0; j < fields.length; j++) {
          let temp = data[keys[i]].fields[j]
          let module = {}
          module['name'] = data[keys[i]].name
          temp['module'] = module
          this.readingsList[module.name + '__' + temp.name] = temp
        }
      }
      if (this.moduleMeta.hasOwnProperty('subModuleFilter')) {
        this.filterForSubModuleFields()
      }
      this.$forceUpdate()
    },
    filterForSubModuleFields() {
      let finalFields = {}
      for (let field in this.readingsList) {
        let fieldNameSplit = field.split('__')
        if (
          fieldNameSplit &&
          fieldNameSplit.length !== 0 &&
          fieldNameSplit[0] === this.moduleMeta.subModuleFilter
        ) {
          finalFields[field] = this.readingsList[field]
        }
      }
      this.readingsList = finalFields
    },
    checkForDateMapsBeforeImport() {
      if (this.timeMappingDateFormatString === '') {
        return false
      }
      for (let key in this.dateToggles) {
        let column = this.dateToggles[key]
        if (column.dateToggle) {
          if (
            typeof this.dateFormatMapping[key] !== 'undefined' &&
            this.dateFormatMapping[key] !== null
          ) {
            continue
          } else {
            return false
          }
        } else {
          continue
        }
      }
      return true
    },
    beginDataValidation() {
      this.importProcessContext.importMode = 2
      if (this.checkForDateMapsBeforeImport()) {
        let fieldMapping = {}
        let uniqueMapping = {}
        this.$set(
          this.dateFormatMapping,
          this.timeMapping,
          this.timeMappingDateFormatString
        )
        if (this.timeMapping !== null) {
          fieldMapping['sys__ttime'] = this.timeMapping
        }
        for (let keys in Object.keys(this.fieldMappingValues)) {
          let columnName = Object.keys(this.fieldMappingValues)[keys]
          let field = this.fieldMappingValues[columnName]
          fieldMapping[field] = columnName
        }
        for (let keys in Object.keys(this.uniqueMappingValues)) {
          let columnName = Object.keys(this.uniqueMappingValues)[keys]
          let field = this.uniqueMappingValues[columnName]
          uniqueMapping[field] = columnName
        }
        if (this.template !== null) {
          if (!this.templateCheck) {
            this.template.fieldMappingString = JSON.stringify(fieldMapping)
            this.template.uniqueFieldMappingString = JSON.stringify(
              uniqueMapping
            )
            let templateMeta = this.template.templateMeta
            if (templateMeta === null) {
              let temp = {}
              temp['dateFormats'] = this.dateFormatMapping
              this.template.templateMeta = JSON.stringify(temp)
            } else {
              let tempMeta = JSON.parse(this.template.templateMeta)
              tempMeta['dateFormats'] = this.dateFormatMapping
              this.template.templateMeta = JSON.stringify(tempMeta)
            }
            this.saveImportTemplate('updateTemplate', this.template)
          }
        } else {
          this.loading = true
          this.$http('/v2/importTemplate/getnewtemplate')
            .then(response => {
              this.importTemplateContext =
                response.data.result.importTemplateContext
              this.importTemplateContext.fieldMappingString = JSON.stringify(
                fieldMapping
              )
              this.importTemplateContext.uniqueMappingString = JSON.stringify(
                uniqueMapping
              )
              let temp = {}
              temp['dateFormats'] = this.dateFormatMapping
              this.importTemplateContext.templateMeta = JSON.stringify(temp)
              if (!this.templateCheck) {
                this.importTemplateContext.save = 1
              }
              this.importTemplateContext.templateName = this.templateName
              this.importTemplateContext.module = JSON.stringify(
                this.computedModuleName
              )
              this.saveImportTemplate('save', this.importTemplateContext)
            })
            .catch(({ message }) => {
              this.loading = false
              this.$message.error(message)
            })
        }
      } else {
        this.$message.error(
          'Please map all date fields to a format before importing'
        )
      }
    },
    saveImportTemplate(mode, templateContext) {
      this.loading = true
      this.$http
        .post('/v2/importTemplate/' + mode, {
          importTemplateContext: templateContext,
        })
        .then(response => {
          this.importProcessContext.templateId = response.data.result.templateId
          this.$http
            .post('/import/beginimportvalidation', {
              importProcessContext: this.importProcessContext,
            })
            .then(response => {
              this.loading = false
              if (response.status === 200) {
                this.$emit('moveToValidation', true)
                this.$emit('setTimeFormat', this.timeMappingDateFormatString)
                this.$emit(
                  'update:importProcessContext',
                  response.data.importProcessContext
                )
              }
            })
            .catch(error => {
              this.$message('Unable to begin Import validation')
              this.loading = true
              this.$emit('moveToValidation', false)
            })
        })
    },
    toggleTemplateDialog() {
      this.newTemplateDialog = !this.newTemplateDialog
    },
    loadSpaceReadings() {
      return this.$http
        .get(
          `/reading/getspacespecificreadings?parentId=${this.parentId}&excludeEmptyFields=false`
        )
        .then(response => {
          this.populateImportProcessContext(response.data)
          this.showReadings = true
        })
    },
    loadAssetReadings(categoryId) {
      return this.$http
        .get(
          `/reading/getassetreadings?excludeEmptyFields=false&parentCategoryId=${categoryId}`
        )
        .then(response => {
          let tempObject = {}
          for (let j = 0; j < response.data.length; j++) {
            tempObject[j] = response.data[j]
          }
          this.moduleMap = tempObject
          for (let moduleMapKey in this.moduleMap) {
            let temp = this.moduleMap[moduleMapKey]
            if (temp.length !== 0) {
              let fields = temp.fields
              for (let j = 0; j < fields.length; j++) {
                let tempModule = {}
                tempModule['name'] = temp.name
                fields[j].module = tempModule
                this.$set(
                  this.readingsList,
                  temp.name + '__' + fields[j].name,
                  fields[j]
                )
              }
            }
          }
          this.showReadings = true
        })
    },
    loadRDM() {
      this.recordFieldIdVsRDM = {}
      return this.$util.loadLatestReading(this.parentId, false).then(fields => {
        fields.forEach(field => {
          this.$setProperty(this.recordFieldIdVsRDM, `${field.fieldId}`, field)
        })
      })
    },
    getInputUnit(moduleKey) {
      let fieldObj = this.$getProperty(this.readingsList, `${moduleKey}`, null)
      let siUnitId = this.$getProperty(fieldObj, 'metricEnum.siUnitId', -1)
      if (!isEmpty(fieldObj)) {
        let rdmObj = this.$getProperty(
          this.recordFieldIdVsRDM,
          `${fieldObj.id}`,
          null
        )
        if (!isEmpty(rdmObj) && !isEmpty(rdmObj.unit)) {
          let symbol = this.$getProperty(
            this.unitIdVsObjMap,
            `${rdmObj.unit}.symbol`
          )
          if (!isEmpty(symbol)) {
            return symbol
          }
        }
      }
      return this.$getProperty(this.unitIdVsObjMap, `${siUnitId}.symbol`, '')
    },

    loadDefaultUnitMetrics() {
      this.unitIdVsObjMap = {}
      API.get('v2/getReadingFieldUnits').then(({ error, data }) => {
        if (error) {
          let { message } = error
          this.$message.error(
            message || 'Error occured while fetching field units'
          )
        } else {
          let metricsWithUnits = this.$getProperty(
            data,
            'readingFieldUnits.MetricsWithUnits',
            {}
          )
          Object.keys(metricsWithUnits).forEach(key => {
            metricsWithUnits[key].forEach(obj => {
              this.$setProperty(this.unitIdVsObjMap, `${obj.unitId}`, obj)
            })
          })
        }
      })
    },
  },
}
</script>
