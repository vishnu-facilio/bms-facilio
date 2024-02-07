<script>
import BulkForm from '@/bulkform/BulkForm'
import { isObject, isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import range from 'lodash/range'
import { isChooserTypeField } from 'util/field-utils'
import { isLookupSimple } from '@facilio/utils/field'
import { requiredFieldValidator } from '@/handsontable/base/CustomValidator'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  props: ['lookupOptions'],
  name: 'invite-bulk-form',
  extends: BulkForm,
  methods: {
    async initBulkForm() {
      //Bulk form has no section , accumulate fields from all section to single list
      let allFields = this.form.sections.reduce((accum, section) => {
        accum.push(...section.fields)
        return accum
      }, [])
      this.constructHotColumns(allFields)
      this.hotConfig.stretchH = 'all'
      this.hotConfig.isinvite = true
    },
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
                if (this.isEdit) {
                  let dialogObj = {
                    title: 'Warning',
                    htmlMessage:
                      'By clicking on Save, all users will receive another invitation. If you wish to re-invite only one user, please make the necessary changes to the specific invite in the Group invite summary.',
                    rbLabel: 'Save',
                    rbDanger: true,
                  }
                  this.$dialog.confirm(dialogObj).then(value => {
                    if (value) {
                      res(formRowsData)
                      this.redirectTolist()
                    }
                  })
                } else {
                  res(formRowsData)
                  this.redirectTolist()
                }
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
    redirectTolist() {
      if(!this.$helpers.isLicenseEnabled('GROUP_INVITES')){
        let moduleName='invitevisitor'
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
      name && this.$router.push({ name })
      }
      else{
        let moduleName = 'groupinvite'
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
      name && this.$router.push({ name })
      }

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
    errorRowIndex() {
      let { hotConfig } = this
      let Required = []
      let celltype = []
      let Data = []
      let fieldRow = []
      let usedRowIndex = this.getUsedRowIndices()
      this.errormessage = ''
      let errorCount = 1
      let index = 0
      let usedindex = 0
      let formRowsData = this.getFormRows()
      let { columns } = hotConfig
      if (this.isEdit) {
        for (let column of columns) {
          let { required, cellType, data } = column
          if (required) {
            Required.push(required)
            celltype.push(cellType)
            Data.push(data)
            fieldRow.push(index)
          }
          index++
        }

        for (const [key, values] of Object.entries(formRowsData)) {
          for (let field in Data) {
            let dataField = ' '
            if (celltype[field] === 'lookup') {
              let data = Data[field].split('.')[0]
              dataField = values[data].id
            } else {
              dataField = values[Data[field]]
            }
            if (dataField == null || dataField == '') {
              let fieldIndex = fieldRow[field]
              this.errormessage += `Row ${usedRowIndex[usedindex] + 1}: ${
                this.hotConfig.fieldnames[fieldIndex]
              } is mandatory
`
              errorCount++
            }
          }
          usedindex++
        }
      } else {
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
      }
      return errorCount - 1
    },

    getHotColumn(field) {
      let { name, displayTypeEnum, required } = field

      let columnObj = {}

      if (isObject(this.formModel[name])) {
        columnObj['data'] = `${name}.id` //map nested object schema to handson column
      } else {
        columnObj['data'] = `${name}`
      }
      if (displayTypeEnum === 'SELECTBOX' && this.isEdit) {
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
        if (this.isEdit) {
          let optionsChache = {}
          let { lookupOptions = [] } = this
          let { lookupModuleName = [] } = field || {}

          lookupOptions[lookupModuleName]?.forEach(option => {
            let { label, value } = option
            optionsChache[value] = label
          })
          columnObj.field.optionsCache = optionsChache
        } else {
          columnObj.field.optionsCache = {}
        }
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
  },
}
</script>
