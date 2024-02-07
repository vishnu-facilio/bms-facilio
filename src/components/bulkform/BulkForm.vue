<template>
  <div class="bulk-form-container fc-hottable-com-page" v-if="loadHot">
    <HotTableWrapper
      :hotTableSettingsData="hotConfig"
      :selectedSiteId="selectedSiteId"
      :pasteDataMap.sync="pasteDataMap"
      :onRowChange="onRowChange"
      :moduleData="moduleData"
      ref="hotTableWrapper"
    ></HotTableWrapper>
  </div>
</template>

<script>
import { isObject, areValuesEmpty, isEmpty } from '@facilio/utils/validation'
import { requiredFieldValidator } from '@/handsontable/base/CustomValidator'
import HotTableWrapper from 'src/pages/newcoms/HotTableWrapper'
import FWebform from '@/FWebform'
import Constants from 'util/constant'
import { isChooserTypeField } from 'util/field-utils'
import { isLookupSimple } from '@facilio/utils/field'
import { v4 as uuid } from 'uuid'
import range from 'lodash/range'
import cloneDeep from 'lodash/cloneDeep'
import setProperty from 'dset'

export default {
  name: 'bulk-form',
  extends: FWebform,

  components: {
    HotTableWrapper,
  },
  mounted() {
    this.initBulkForm()

    this.loadHot = true
    window.handsonTable = this.$refs['hot']
  },
  data() {
    return {
      loadHot: false,
      hotConfig: {
        renderAllRows: true, //turn off virtual scrolling
        viewportColumnRenderingOffset: 'auto',
        rowHeaders: true,
        data: [],
        dataSchema: {},
        uniqueDataKey: 'uuid',
        colHeaders: [],
        columns: [],
        minRows: 25,
        minSpareRows: 3,
        maxRows: 25,
        undo: true,
        manualColumnResize: true,
        manualRowResize: true,
        autoRowSize: false,
        autoColumnSize: true,
        afterValidate: () => {},
        errormessage: '',
        fieldnames: [],
      },
      pasteDataMap: {},
    }
  },
  methods: {
    initBulkForm() {
      //Bulk form has no section , accumulate fields from all section to single list
      let allFields = this.form.sections.reduce((accum, section) => {
        accum.push(...section.fields)
        return accum
      }, [])
      this.constructHotColumns(allFields)
      this.hotConfig.stretchH = 'all'
    },

    // triggered from outside
    triggerSubmit() {
      let formRowsData = this.getFormRows()
      if (isEmpty(formRowsData)) {
        return Promise.reject({
          type: 'warning',
          message: 'Please enter the values for atleast one row',
        })
      }
      return new Promise((res, rej) => {
        let usedRowIndices = this.getUsedRowIndices()
        if (!isEmpty(usedRowIndices)) {
          let { $refs } = this
          let validationOption = {
            rows: usedRowIndices,
          }
          $refs['hotTableWrapper'].triggerValidate(
            validationOption,
            isValid => {
              if (isValid) {
                res(formRowsData)
              } else {
                let count = this.errorRowIndex()
                let dialogObj = {}
                let classname = ''
                if (count < 10) {
                  classname = 'f-dialog-minheight'
                } else {
                  classname = 'f-dialog-maxheight'
                }

                dialogObj = {
                  title: 'Please correct the following errors to proceed:',
                  htmlMessage: `${this.errormessage}`,
                  lbHide: true,
                  className: `${classname}`,
                }
                this.$dialog.confirm(dialogObj)
              }
            }
          )
        }
      })
    },

    errorRowIndex() {
      let { hotConfig } = this
      let Required = []
      let celltype = []
      let usedRowIndex = this.getUsedRowIndices()
      this.errormessage = ''
      let errorCount = 1
      let index = 0
      let usedindex
      let formRowsData = this.getFormRows()
      let { columns } = hotConfig
      for (let column of columns) {
        let { required, cellType } = column
        Required.push(required)
        celltype.push(cellType)
      }
      usedindex = 0
      for (const [key, values] of Object.entries(formRowsData)) {
        index = 0
        for (let value in values) {
          if (
            Required[index] == true &&
            (celltype[index] === 'lookup' || celltype[index] === 'select')
          ) {
            if (values[value].id == null || values[value].id == '') {
              this.errormessage += `Row ${usedRowIndex[usedindex] + 1}: ${
                this.hotConfig.fieldnames[index]
              } is mandatory
`
              errorCount++
            }
          } else if (
            Required[index] == true &&
            celltype[index] == undefined &&
            (values[value] == null || values[value] == '')
          ) {
            this.errormessage += `Row ${usedRowIndex[usedindex] + 1}: ${
              this.hotConfig.fieldnames[index]
            } is mandatory
`
            errorCount++
          }
          index++
        }
        usedindex++
      }
      return errorCount - 1
    },

    onRowChange(props) {
      let { $refs, hotConfig } = this
      let { data } = hotConfig || {}
      let { index } = props
      let selectedRowData = data[index] || {}
      let { ['uuid']: uuid, ...dataWithoutId } = selectedRowData || {}
      let canClearError = areValuesEmpty(dataWithoutId)
      if (!isEmpty($refs) && canClearError) {
        $refs['hotTableWrapper'].clearRowError(uuid)
      }
    },

    getFormRows() {
      let { uniqueDataKey, data: rows } = this.hotConfig
      let formRowsData = []

      rows.forEach(row => {
        let isUsedRow = this.isRowUsed(row)
        if (isUsedRow) {
          let data = cloneDeep(row)
          let id = data[uniqueDataKey]

          if (this.pasteDataMap[id]) {
            let pasteData = this.pasteDataMap[id]
            Object.entries(pasteData).forEach(([key, value]) => {
              setProperty(data, key, value)
            })
          }

          delete data[uniqueDataKey]
          formRowsData.push(data)
        }
      })
      return formRowsData
    },

    getUsedRowIndices() {
      let rows = this.hotConfig.data
      let usedRowIndices = []
      rows.forEach((row, rowIndex) => {
        let isUsedRow = this.isRowUsed(row)
        if (isUsedRow) {
          usedRowIndices.push(rowIndex)
        }
      })
      return usedRowIndices
    },

    isRowUsed(row) {
      // uuid or id must not be considered when checking for usage
      // TODO check if visible fields are all null because id will be
      // present in edit case and hidden fields might have default values
      // during creation case
      let { uniqueDataKey } = this.hotConfig
      return !areValuesEmpty({ ...row, [uniqueDataKey]: null })
    },

    constructHotColumns(deserializedFormFields) {
      let supportedFields = deserializedFormFields.filter(formField =>
        this.isBulkModeSupported(formField)
      )
      this.hotConfig.fieldnames = supportedFields.map(field => {
        let { displayName, required } = field || {}
        return required ? `${displayName}` : `${displayName}`
      })

      this.hotConfig.colHeaders = supportedFields.map(field => {
        let { displayName, required } = field || {}
        return required
          ? `<span class="required">${displayName}*</span>`
          : `${displayName}`
      })

      if (isEmpty(this.moduleData)) {
        this.hotConfig.dataSchema = cloneDeep(this.formModel)
        this.hotConfig.data = range(0, 25).map(() =>
          cloneDeep({
            ...this.formModel,
            uuid: uuid(),
          })
        )
      } else {
        let temp = 0
        this.hotConfig.dataSchema = cloneDeep(this.formModel)
        this.hotConfig.data = range(0, 25).map(() =>
          cloneDeep({
            ...this.moduleData[temp++],
            uuid: uuid(),
          })
        )
      }

      //get appropriate column object for each field object
      supportedFields.forEach(formField => {
        this.hotConfig.columns.push(this.getHotColumn(formField))
      })
    },

    getHotColumn(field) {
      let { name, displayTypeEnum, required } = field

      let columnObj = {}

      if (isObject(this.formModel[name])) {
        columnObj['data'] = `${name}.id` //map nested object schema to handson column
      } else {
        columnObj['data'] = `${name}`
      }

      //Can be type:'systemTypeString' or type:{renderer,validator,editor} for customtypes'
      //define props for column type and then mix into column object
      let columnTypeObj = {}

      if (displayTypeEnum === 'TEXTBOX' || displayTypeEnum === 'TEXTAREA') {
        columnTypeObj.renderer = 'input'
      } else if (
        displayTypeEnum === 'NUMBER' ||
        displayTypeEnum === 'DECIMAL'
      ) {
        columnTypeObj.type = 'numeric'
      } else if (displayTypeEnum === 'DATETIME') {
        columnTypeObj.editor = 'picker'
        columnTypeObj.pickerType = 'datetime'
        columnTypeObj.renderer = 'picker'
        columnTypeObj.width = 200
      } else if (isLookupSimple(field) || isChooserTypeField(field)) {
        columnObj.editor = 'lookup'
        columnObj.renderer = 'lookup'
        columnObj.cellType = 'lookup'
        columnObj.field = field
        columnObj.field.optionsCache = {}
      } else if (this.isDropdownTypeFields(field.displayTypeEnum)) {
        columnTypeObj.renderer = 'select'
        columnTypeObj.editor = 'select'
        columnObj.cellType = 'select'
        columnTypeObj.options = field.options
      } else if (displayTypeEnum === 'DECISION_BOX') {
        columnTypeObj.type = 'checkbox'
      }

      columnObj = { ...columnObj, ...columnTypeObj }
      columnObj.required = required
      if (required) {
        columnObj.validateFn = requiredFieldValidator
      }

      return columnObj
    },

    isBulkModeSupported(field) {
      return Constants.BULK_MODE_SUPPORTED_DISPLAY_TYPES.includes(
        field.displayTypeEnum
      )
    },
  },
}
</script>

<style lang="scss">
.bulk-form-container {
  position: relative;
  .hot-table-wrapper-container {
    width: 100%;
    overflow: hidden;
  }

  overflow: hidden;
  .handsontable thead th .relative {
    min-width: 150px;
  }
  .required {
    color: #d54141;
  }
}
</style>
