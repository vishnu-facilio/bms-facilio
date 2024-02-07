<template>
  <div>
    <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
      File Mapping
    </div>
    <div class="fc-heading-border-width43 mT10 height2px"></div>
    <div class="height100 overflow-y-scroll pT20">
      <div class>
        <el-form
          :rules="rules"
          :model="fieldMappingForm"
          @submit.native.prevent
          ref="fieldMappingForm"
        >
          <el-table
            :data="fieldMappingForm.columnHeadings"
            height="410"
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
            <el-table-column prop="name" label="FIELDS IN FILE" width="300">
              <template v-slot="data">
                <div class="label-txt-black">{{ data.row }}</div>
              </template>
            </el-table-column>
            <el-table-column label="FIELDS IN FACILIO" width="300">
              <template v-slot="field">
                <el-form-item :prop="replaceSpecialChars(field.row)">
                  <el-select
                    clearable
                    @clear="clearField(field.row)"
                    @change="selectField(field.row), getDateFormat(field.row)"
                    v-model="fieldMappingForm.mappedValues[field.row]"
                    :placeholder="
                      importProcessContext.facilioFieldMapping[
                        fieldMappingForm.mappedValues[field.row]
                      ]
                        ? importProcessContext.facilioFieldMapping[
                            fieldMappingForm.mappedValues[field.row]
                          ].displayName
                        : placeHolder
                    "
                    filterable
                    class="fc-input-full-border-select2 import-select-width"
                  >
                    <el-option
                      :disabled="!value.show"
                      v-for="(value, index2) in fieldList"
                      :label="
                        importProcessContext.facilioFieldMapping[value.value]
                          ? importProcessContext.facilioFieldMapping[
                              value.value
                            ].displayName
                          : value.value
                      "
                      :value="value.value"
                      :key="index2"
                    ></el-option>
                  </el-select>
                  <el-tooltip
                    v-if="dateToggles[field.row].dateToggle"
                    content="Choose a date format"
                    class="item"
                    effect="dark"
                    placement="right-start"
                  >
                    <el-popover
                      v-model="dateToggles[field.row].datePopoverToggle"
                      trigger="click"
                      width="200"
                      placement="right"
                    >
                      <div
                        @click="setDateFormat(field.row, dateFormat)"
                        class="date-format pointer pB10"
                        :class="{
                          active:
                            fieldMappingForm.dateFormatMapping[field.row] ===
                            dateFormat,
                          fw5:
                            fieldMappingForm.dateFormatMapping[field.row] ===
                            dateFormat,
                        }"
                        v-for="(dateFormat, dateFormatIndex) in fieldMappingForm
                          .dateFormatsForColumns[field.row]"
                        :key="dateFormatIndex"
                        :label="dateFormat"
                        :value="dateFormat"
                      >
                        {{ dateFormat }}
                      </div>
                      <el-button
                        @click="toggleDateFormatPopover(field.row)"
                        circle
                        v-if="dateToggles[field.row].dateToggle"
                        class="el-icon-date"
                        :class="getClass(field.row)"
                        slot="reference"
                      ></el-button>
                    </el-popover>
                  </el-tooltip>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="SAMPLE DATA" width="300">
              <template v-slot="field">
                <div
                  class="fc-grey3-text14"
                  v-if="importProcessContext.firstRow[field.row] === null"
                ></div>
                <div class="fc-grey3-text14" v-else>
                  {{ importProcessContext.firstRow[field.row] }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="goBacktoBeginning" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button
        @click="submitForm('fieldMappingForm')"
        :loading="saving.sendMapping"
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
  </div>
</template>
<script>
import ImportHelper from './ImportHelper'
import { isDateField, isDateTimeField } from '@facilio/utils/field'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['importProcessContext', 'asset'],
  mixins: [ImportHelper],
  data() {
    return {
      placeHolder: 'Select',
      show: true,
      fieldMappingForm: {
        mappedValues: {},
        dateFormatsForColumns: {},
        dateFormatMapping: {},
        columnHeadings: [],
      },
      rules: {},
      firstRow: [],
      moduleStaticFields: null,
      dateToggles: {},
      moduleName: null,
      importSetting: {
        INSERT: 1,
        INSERT_SKIP: 2,
        UPDATE: 3,
        UPDATE_NOT_NULL: 4,
        BOTH: 5,
        BOTH_NOT_NULL: 6,
      },
      saving: {
        sendMapping: false,
      },
    }
  },
  created() {
    this.moduleName = this.$route.params.module
    this.createDateTogglesAndFormatArrays()
    this.loadModuleStaticFields()
  },
  mounted() {
    if (this.importProcessContext.fieldMappingJSON !== null) {
      let keys = Object.keys(this.importProcessContext.fieldMappingJSON)
      let meta = JSON.parse(this.importProcessContext.importJobMeta)
      let dateFormats
      if (meta != null) {
        dateFormats = meta.dateFormats
      }
      for (let key in keys) {
        if (this.importProcessContext.fieldMappingJSON[keys[key]] !== null) {
          let field = keys[key].split('__')
          this.$set(
            this.fieldMappingForm.mappedValues,
            this.importProcessContext.fieldMappingJSON[keys[key]],
            field[field.length - 1]
          )
          this.getDateFormat(
            this.importProcessContext.fieldMappingJSON[keys[key]]
          )
          if (typeof dateFormats !== 'undefined') {
            this.$set(
              this.fieldMappingForm.dateFormatMapping,
              this.importProcessContext.fieldMappingJSON[keys[key]],
              dateFormats[this.importProcessContext.fieldMappingJSON[keys[key]]]
            )
          }
        }
      }
    }
    this.initFormRules()
  },
  computed: {
    fieldList() {
      let fields = []
      let tempIgFields = this.$helpers.cloneObject(
        this.importProcessContext.ignoreFields
      )
      for (let ig of tempIgFields) {
        if (this.importProcessContext.importMode === 1 && ig === 'category') {
          if (
            !(
              this.importProcessContext.importSetting === 3 ||
              this.importProcessContext.importSetting === 4
            )
          ) {
            let index = tempIgFields.indexOf(ig)
            tempIgFields.splice(index, 1)
          }
        }
      }
      let tempFields = this.importProcessContext.fields.filter(i => {
        return !tempIgFields.includes(i)
      })
      for (let field of tempFields) {
        let temp = {}
        temp['show'] = true
        temp['value'] = field
        temp['columnName'] = null
        fields.push(temp)
      }
      return fields
    },
    groupedFields() {
      let fields = []
      for (let field of this.importProcessContext.fields) {
        if (this.importProcessContext.facilioFieldMapping[field]) {
          fields.filter(
            moduleField =>
              moduleField.name ===
              this.importProcessContext.facilioFieldMapping[field].module.name
          )
          if (fields.length !== 0) {
            let moduleField = fields[0]
            moduleField.fields.push(field)
          } else {
            let temp = {}
            temp['name'] = this.importProcessContext.facilioFieldMapping[
              field
            ].module.name
            temp['displayName'] = this.importProcessContext.facilioFieldMapping[
              field
            ].module.displayName
            temp['fields'] = []
            temp.fields.push(field)
            fields.push(temp)
          }
        } else {
          // if field does not have any associated module
          fields.filter(
            moduleField =>
              moduleField.name === this.importProcessContext.module.name
          )
          if (fields.length !== 0) {
            let moduleField = fields[0]
            moduleField.fields.push(field)
          } else {
            let temp = {}
            temp['name'] = this.importProcessContext.module.name
            temp['displayName'] = this.importProcssContext.module.displayName
            temp['fields'] = []
            temp.fields.push(field)
          }
        }
      }
      return fields
    },
  },
  methods: {
    getClass(columnName) {
      if (
        this.isDateOrDateTimeField(
          this.fieldMappingForm.mappedValues[columnName]
        )
      ) {
        let dateFormats = this.getDateFormats(
          this.importProcessContext.firstRow[columnName]
        )
        if (dateFormats === null) {
          return 'fc-black'
        } else {
          if (!this.fieldMappingForm.dateFormatMapping[columnName]) {
            return 'fc-red3'
          }
          return 'fc-green'
        }
      }
    },
    replaceSpecialChars(string) {
      return string.replace(/[\:W./,]/, '_')
    },
    initFormRules() {
      console.log(this.fieldMappingForm)
      let rules = {}
      let validateFieldMapping = (rule, value, callback) => {
        if (
          this.isDateOrDateTimeField(
            this.fieldMappingForm.mappedValues[rule.field]
          )
        ) {
          let dateFormats = this.getDateFormats(
            this.importProcessContext.firstRow[rule.field]
          )
          if (dateFormats === null) {
            callback(new Error('Incompatible date format.'))
            return
          } else {
            if (!this.fieldMappingForm.dateFormatMapping[rule.field]) {
              callback(new Error('Please select the date format.'))
              return
            }
          }
        }
        callback()
      }
      for (let column of this.fieldMappingForm.columnHeadings) {
        rules[this.replaceSpecialChars(column)] = {
          validator: validateFieldMapping,
          trigger: 'blur',
        }
      }
      this.rules = rules
    },
    goBacktoBeginning() {
      this.$emit('begin', 0)
    },
    clearField(columnName) {
      let choosenField = this.fieldList.find(
        field => field.columnName === columnName
      )
      if (choosenField) {
        choosenField.show = true
      }
    },
    selectField(columnName) {
      if (this.fieldMappingForm.mappedValues[columnName]) {
        let choosenField = this.fieldList.find(
          field =>
            field.value === this.fieldMappingForm.mappedValues[columnName]
        )
        if (choosenField) {
          choosenField.show = false
          choosenField['columnName'] = columnName
        }
        choosenField.show = true
      }
    },
    loadModuleStaticFields() {
      this.moduleStaticFields = []
      if (this.moduleName) {
        switch (this.moduleName) {
          case 'purchasedItem':
          case 'purchasedTool': {
            this.$http.get('/v2/storeRoom/view/all').then(response => {
              let temp = {}
              temp['name'] = 'storeRoom'
              temp['displayName'] = 'Store Room'
              temp['values'] = []
              temp['value'] = null
              for (let room of response.data.result.storeRooms) {
                let storeRoom = {}
                storeRoom['displayName'] = room.name
                storeRoom['id'] = room.id
                temp.values.push(storeRoom)
              }
              this.moduleStaticFields.push(temp)
            })
            break
          }
        }
      }
    },
    setDateFormat(columnName, dateFormat) {
      this.$set(this.fieldMappingForm.dateFormatMapping, columnName, dateFormat)
      this.$set(this.dateToggles[columnName], 'datePopoverToggle', false)
      this.$refs.fieldMappingForm.validateField(columnName)
    },
    getDateFormat(columnName) {
      if (this.fieldMappingForm.mappedValues[columnName] === '') {
        this.dateToggles[columnName].dateToggle = false
        this.dateToggles[columnName].datePopoverToggle = false
        this.fieldMappingForm.dateFormatsForColumns[columnName] = []
        delete this.fieldMappingForm.mappedValues[columnName]
      } else {
        if (
          this.isDateOrDateTimeField(
            this.fieldMappingForm.mappedValues[columnName]
          )
        ) {
          let dateFormats = this.getDateFormats(
            this.importProcessContext.firstRow[columnName]
          )
          if (dateFormats === null) {
            this.fieldMappingForm.dateFormatsForColumns[columnName] = []
            this.fieldMappingForm.dateFormatsForColumns[columnName].push(
              'Incompatible format'
            )
          } else if (dateFormats.length === 1) {
            this.$set(
              this.fieldMappingForm.dateFormatsForColumns,
              columnName,
              dateFormats
            )
            this.$set(
              this.fieldMappingForm.dateFormatMapping,
              columnName,
              dateFormats[0]
            )
          } else {
            this.$set(
              this.fieldMappingForm.dateFormatsForColumns,
              columnName,
              dateFormats
            )
          }
          this.$set(this.dateToggles[columnName], 'dateToggle', true)
        } else {
          this.$set(this.dateToggles[columnName], 'dateToggle', false)
        }
      }
      this.$refs.fieldMappingForm.validateField(columnName)
    },
    isDateOrDateTimeField(fieldName) {
      let { facilioFieldMapping } = this.importProcessContext || {}
      let field = facilioFieldMapping[fieldName] || {}

      return !isEmpty(field)
        ? isDateField(field) || isDateTimeField(field)
        : false
    },
    checkForProperDateMapping() {
      for (let columnName in this.fieldMappingForm.mappedValues) {
        if (isDateField(this.fieldMappingForm.mappedValues[columnName])) {
          if (
            this.fieldMappingForm.dateFormatMapping.hasOwnProperty(columnName)
          ) {
            continue
          } else {
            return false
          }
        }
      }
      return true
    },
    createDateTogglesAndFormatArrays() {
      if (
        this.importProcessContext &&
        this.importProcessContext.columnHeadings
      ) {
        this.fieldMappingForm.columnHeadings = []
        this.importProcessContext.columnHeadings.filter(i => {
          if (i) {
            {
              this.fieldMappingForm.columnHeadings.push(i)
            }
          }
        })
      }
      for (let columnIndex in this.importProcessContext.columnHeadings) {
        let temp = {}
        temp['dateToggle'] = false
        temp['datePopoverToggle'] = false
        this.$set(
          this.dateToggles,
          this.importProcessContext.columnHeadings[columnIndex],
          temp
        )
        this.$set(
          this.fieldMappingForm.dateFormatsForColumns,
          this.importProcessContext.columnHeadings[columnIndex],
          []
        )
      }
    },
    backToImportSetting() {
      this.$emit('backToSetting', '')
    },
    checkForProperFieldMapping() {
      let importJobMetaJson = null
      let columns = null
      if (this.importProcessContext.importJobMeta) {
        importJobMetaJson = JSON.parse(this.importProcessContext.importJobMeta)
      }
      if (
        this.importProcessContext.importSetting ===
        this.importSetting.INSERT_SKIP
      ) {
        if (importJobMetaJson['insertBy']) {
          columns = importJobMetaJson['insertBy']
        } else {
          return true
        }
      } else if (
        this.importProcessContext.importSetting === this.importSetting.UPDATE ||
        this.importProcessContext.importSetting === this.importSetting.BOTH ||
        this.importProcessContext.importSetting ===
          this.importSetting.UPDATE_NOT_NULL ||
        this.importProcessContext.importSetting ===
          this.importSetting.BOTH_NOT_NULL
      ) {
        columns = importJobMetaJson['updateBy']
      }
      if (columns) {
        for (let column of columns) {
          if (!this.fieldMappingForm.mappedValues.hasOwnProperty(column)) {
            return false
          }
        }
      }
      return true
    },
    getSettingArrayString() {
      if (
        this.importProcessContext.importSetting ===
        this.importSetting.INSERT_SKIP
      ) {
        return 'insertBy'
      } else if (
        this.importProcessContext.importSetting === this.importSetting.UPDATE ||
        this.importProcessContext.importSetting ===
          this.importSetting.UPDATE_NOT_NULL ||
        this.importProcessContext.importSetting === this.importSetting.BOTH ||
        this.importProcessContext.importSetting ===
          this.importSetting.BOTH_NOT_NULL
      ) {
        return 'updateBy'
      }
    },
    mapImportSettingFields() {
      let importMeta = null
      if (
        this.importProcessContext.importJobMeta &&
        this.importProcessContext.importSetting !== this.importSetting.INSERT
      ) {
        let settingString = this.getSettingArrayString()
        importMeta = JSON.parse(this.importProcessContext.importJobMeta)
        let settingColumns = importMeta[settingString]
        let temp = []
        for (let column of settingColumns) {
          if (this.fieldMappingForm.mappedValues[column] === 'id') {
            temp.push(`${this.importProcessContext.module.name}__id`)
          } else {
            temp.push(
              this.importProcessContext.facilioFieldMapping[
                this.fieldMappingForm.mappedValues[column]
              ].module.name +
                '__' +
                this.fieldMappingForm.mappedValues[column]
            )
          }
        }
        importMeta[settingString] = temp
        this.importProcessContext.importJobMeta = JSON.stringify(importMeta)
      } else {
        return
      }
    },
    sendMapping() {
      if (
        this.checkForProperDateMapping() &&
        this.checkForProperFieldMapping()
      ) {
        this.importProcessContext.fieldMapping = {}
        this.importProcessContext.fieldMappingJSON = {}
        for (let key in this.fieldMappingForm.mappedValues) {
          if (this.fieldMappingForm.mappedValues[key] !== '') {
            let fieldMappingKey
            if (
              this.importProcessContext.facilioFieldMapping[
                this.fieldMappingForm.mappedValues[key]
              ]
            ) {
              fieldMappingKey =
                this.importProcessContext.facilioFieldMapping[
                  this.fieldMappingForm.mappedValues[key]
                ].module.name +
                '__' +
                this.fieldMappingForm.mappedValues[key]
            } else {
              fieldMappingKey =
                this.importProcessContext.module.name +
                '__' +
                this.fieldMappingForm.mappedValues[key]
            }
            this.importProcessContext.fieldMapping[fieldMappingKey] = key
          } else {
            continue
          }
        }
        this.mapImportSettingFields()
        this.importProcessContext.fieldMappingString = JSON.stringify(
          this.importProcessContext.fieldMapping
        )
        if (this.importProcessContext.importJobMeta === null) {
          let temp = {}
          temp['dateFormats'] = this.fieldMappingForm.dateFormatMapping
          this.importProcessContext.importJobMeta = JSON.stringify(temp)
        } else {
          let temp = JSON.parse(this.importProcessContext.importJobMeta)
          temp['dateFormats'] = this.fieldMappingForm.dateFormatMapping
          this.importProcessContext.importJobMeta = JSON.stringify(temp)
        }
        let self = this
        let assetId = this.asset
        if (this.asset === '') {
          assetId = null
        }
        this.saving.sendMapping = true
        this.$http
          .post('/import/processImport', {
            importProcessContext: this.importProcessContext,
            assetId: assetId,
          })
          .then(function(response) {
            self.$emit('mappingResponse', response.data)
            self.saving.sendMapping = false
          })
          .catch(function(error) {
            self.saving.sendMapping = false
            self.$message.error('Data import failed! [ ' + error + ']')
          })
      } else {
        if (this.checkForProperFieldMapping() === false) {
          this.$message.error('Please map all Update By columns')
        } else if (this.checkForProperDateMapping() === false) {
          this.$message.error(
            'Please map all date fields to a format before importing'
          )
        } else {
          this.$message.error(
            'Please map all mandatory columns and date fields'
          )
        }
      }
    },
    submitForm(fieldMappingForm) {
      this.$refs[fieldMappingForm].validate(valid => {
        if (valid) {
          this.sendMapping()
        } else {
          this.$message.error(
            'Some fields have incompatible date format. Review the dateformat and try again'
          )
          return false
        }
      })
    },
  },
}
</script>
