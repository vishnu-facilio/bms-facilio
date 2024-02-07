import { selectLookupFieldValidator } from '@/handsontable/base/CustomValidator'
import { CustomEditorWrapper } from '@/handsontable/base/CustomEditor'
import { CustomRendererWrapper } from '@/handsontable/base/CustomRenderer'
import { isEmpty } from '@facilio/utils/validation'

const supportedComponentHash = {
  input: {
    visibilityKey: 'canShowInput',
    refKey: 'inputComponent',
  },
  select: {
    visibilityKey: 'canShowSelect',
    refKey: 'selectComponent',
  },
  picker: {
    visibilityKey: 'canShowPicker',
    refKey: 'pickerComponent',
  },
  lookup: {
    visibilityKey: 'canShowLookup',
    refKey: 'lookUpComponent',
  },
}

export default {
  methods: {
    getHotTableInstance() {
      return this.$refs['hotTableInstance']
    },
    getPhysicalRowIndex(currentRow) {
      let { physicalRowArr } = this
      if (!isEmpty(physicalRowArr)) {
        return physicalRowArr[currentRow]
      }
      return currentRow
    },

    getHotCustomEditor(componentName) {
      let compInstance = this
      const editorWrapper = CustomEditorWrapper(compInstance, componentName)
      return editorWrapper
    },
    getHotCustomRenderer(componentName) {
      let compInstance = this
      const rendererWrapper = CustomRendererWrapper(compInstance, componentName)
      return rendererWrapper
    },
    getValidateFn(column) {
      let { cellType } = column
      if (cellType === 'select' || cellType === 'lookup') {
        return selectLookupFieldValidator
      }
    },
    getVisibilityKey(name) {
      let key = (supportedComponentHash[name] || {}).visibilityKey
      return key || ''
    },
    getRefElement(componentInstance, name) {
      let key = (supportedComponentHash[name] || {}).refKey
      return new Promise(resolve => {
        if (!isEmpty(key)) {
          componentInstance.$nextTick(() => {
            resolve(componentInstance.$refs[key] || {})
          })
        }
      })
    },
    // For special options fetch
    getLookupOptionsForColumn(props) {
      let { activeRowIndex, activeColumnIndex } = this
      return this.getLookupOptions({
        picklistOptions: props,
        row: activeRowIndex,
        col: activeColumnIndex,
      })
        .then(options => {
          return { options }
        })
        .catch(error => {
          return error
        })
    },
    getSelectOptionsForColumn(props) {
      let { activeSelectField } = this
      activeSelectField.isSelectOptionsLoading = true
      let promise = this.getSelectOptions(props).then(options => {
        return options
      })
      return Promise.all([promise]).finally(
        () => (activeSelectField.isSelectOptionsLoading = false)
      )
    },
    getOptionLabel(value) {
      let { activeLookupField } = this
      let { optionsCache } = activeLookupField || {}
      if (!isEmpty(optionsCache)) {
        return optionsCache[value]
      }
      return ''
    },
  },
}
