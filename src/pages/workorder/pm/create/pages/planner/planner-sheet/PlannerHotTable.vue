<script>
import HotTableWrapper from 'src/pages/newcoms/HotTableWrapper'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
export default {
  extends: HotTableWrapper,
  methods: {
    toggleSelectAll() {
      this.$emit('toggleSelectAll')
    },
    setupHotTableSettingsData() {
      let { hotTableSettingsData } = this
      let compInstance = this
      let { columns, colHeaders, uniqueDataKey } = hotTableSettingsData
      // By default, uniqueDataKey is id
      if (isEmpty(uniqueDataKey)) {
        hotTableSettingsData.uniqueDataKey = 'id'
      }
      // Strech Columns to full width
      hotTableSettingsData.colWidths = [10, undefined, undefined, undefined]
      hotTableSettingsData.stretchH = 'all'
      // Only allow vertical drag to fill
      hotTableSettingsData.fillHandle = {
        direction: 'vertical',
      }
      /*
        Here normal function is used instead of arrow function to aviod parent scoping
        While copying from handson, have to convert id to corresponding label
      */
      if (!isEmpty(columns)) {
        columns.forEach((column, index) => {
          if (!isEmpty(column.editor)) {
            column.editor = this.getHotCustomEditor(column.editor)
          }
          if (!isEmpty(column.renderer)) {
            column.renderer = this.getHotCustomRenderer(column.renderer)
          }
          if (isEmpty(column.validateFn)) {
            column.validateFn = this.getValidateFn(column)
          }
          if (column.filters) {
            let svgContent = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../../../../../assets/filter.svg`)
            let styledSvg = `<svg class="icon icon-sm filter-icon"`
            svgContent = (svgContent || {}).default || ''
            svgContent = svgContent.replace(/<svg/, styledSvg)
            colHeaders[index] = `${colHeaders[index]} ${svgContent}`
          }
        })
      }
      /* Hottable hooks setup */
      hotTableSettingsData.beforeCopy = function beforeCopyHandler(
        selectedRowsArr,
        coords
      ) {
        let [selectedCellsCoords = {}] = coords
        let { startRow, startCol } = selectedCellsCoords
        selectedRowsArr.forEach((row, rowIndex) => {
          let currentRow = startRow + rowIndex
          if (!isEmpty(row)) {
            row.forEach((cellValue, cellIndex) => {
              let currentCol = startCol + cellIndex
              let cellProperties = this.getCellMeta(currentRow, currentCol)
              let { cellType, options = [], field = {} } = cellProperties || {}
              if (['select', 'lookup'].includes(cellType)) {
                let { optionsCache } = field || {}
                let optionsArr = field.options || options
                let selectedOption =
                  optionsArr.find(option => option.value === cellValue) || {}
                let selectedLabel = selectedOption.label
                if (!isEmpty(optionsCache) && isEmpty(selectedLabel)) {
                  selectedLabel = optionsCache[cellValue]
                }
                selectedRowsArr[rowIndex][cellIndex] =
                  selectedLabel || cellValue
              }
            })
          }
        })
      }
      hotTableSettingsData.beforeCut = function beforeCutHandler(
        selectedRowsArr,
        coords
      ) {
        let [selectedCellsCoords = {}] = coords
        let { startRow, startCol } = selectedCellsCoords
        let categoryRowIndexArr = []
        selectedRowsArr.forEach((row, rowIndex) => {
          let currentRow = startRow + rowIndex
          if (!isEmpty(row)) {
            row.forEach((cellValue, cellIndex) => {
              let currentCol = startCol + cellIndex
              let cellProperties = this.getCellMeta(currentRow, currentCol)
              let { cellType, options = [], field = {}, prop, row: rowValue } =
                cellProperties || {}
              if (prop === 'categoryId') {
                categoryRowIndexArr.push(rowValue)
              }
              if (['select', 'lookup'].includes(cellType)) {
                let { optionsCache } = field || {}
                let optionsArr = field.options || options
                let selectedOption =
                  optionsArr.find(option => option.value === cellValue) || {}
                let selectedLabel = selectedOption.label
                if (!isEmpty(optionsCache) && isEmpty(selectedLabel)) {
                  selectedLabel = optionsCache[cellValue]
                }
                selectedRowsArr[rowIndex][cellIndex] =
                  selectedLabel || cellValue
              }
            })
          }
        })
        if (!isEmpty(categoryRowIndexArr)) {
          categoryRowIndexArr.forEach(row => {
            compInstance.resetActiveRowData &&
              compInstance.resetActiveRowData({
                activeRowIndex: row,
              })
          })
        }
      }
      hotTableSettingsData.beforePaste = function beforePasteHandler(
        selectedRowsArr,
        coords
      ) {
        compInstance.$emit('update:isPasteInProgress', true)
        compInstance.handlePasteEvent(selectedRowsArr, coords, this)
      }
      hotTableSettingsData.afterChange = function(changes, source) {
        // Skip validation for change triggered due to paste event
        if (!isEmpty(changes) && source !== 'CopyPaste.paste') {
          let promises = []
          let changedRows = new Set()
          for (let [, rowData] of changes.entries()) {
            let [row, prop, , newValue] = rowData
            let physicalRow = compInstance.getPhysicalRowIndex(row)
            let {
              cellsErrorMap,
              hotTableData,
              hotTableSettingsData,
              pasteDataMap,
            } = compInstance
            let { uniqueDataKey } = hotTableSettingsData || {}
            let data = hotTableData[physicalRow]
            let id = !isEmpty(data) ? data[uniqueDataKey] : null

            let columnsArr = this.getCellMetaAtRow(row) || []
            let selectedColumn =
              columnsArr.find(column => column && column.prop === prop) || {}
            if (!isEmpty(selectedColumn)) {
              let { validateFn, cellType } = selectedColumn || {}
              let isValidEntry = true
              if (isFunction(validateFn)) {
                let rowsData = this.getDataAtRow(row)
                let dataFromPasteMap = pasteDataMap[id]
                  ? pasteDataMap[id][prop]
                  : null

                isValidEntry = validateFn({
                  cellValue: dataFromPasteMap || newValue,
                  rowsData,
                  cellType,
                })
              }
              let isAlreadyInvalid = (cellsErrorMap[id] || []).includes(prop)
              compInstance.pushOrRemoveError({
                isValidEntry,
                isAlreadyInvalid,
                id,
                propName: prop,
              })
            }
            changedRows.add(row)
          }
          if (source === 'Autofill.fill') {
            // To execute hook for each row change only for autofill event
            for (let item of changedRows.values()) {
              promises.push(
                compInstance.onAutoFillRowChange({
                  index: item,
                })
              )
            }
          }
          // To execute hook for each row change
          for (let item of changedRows.values()) {
            promises.push(
              compInstance.onRowChange({
                index: item,
              })
            )
          }
          Promise.all(promises).then(() => this.render())
        }
      }
      hotTableSettingsData.afterDocumentKeyDown = function(event) {
        let { key } = event
        let [range] = this.getSelectedRange() || []
        compInstance.handleAfterDocumentKeyDown({ range }, key)
      }
      hotTableSettingsData.beforeOnCellMouseDown = function(e, coords) {
        let parentElem = e.target.parentElement.nodeName
        let svgClassName = e.target.classList.value
        let isSvgElement =
          svgClassName.includes('filter-icon') ||
          ['g', 'svg'].includes(parentElem)
        if (coords.row < 0 && isSvgElement) {
          compInstance.openFilterDialog(coords.col)
        }
        if (svgClassName === 'selector-field') {
          compInstance.toggleSelectAll()
        }
      }
      hotTableSettingsData.beforeAutofill = function() {
        let [range] = this.getSelectedRange()
        compInstance.selectedRange = range
      }
      hotTableSettingsData.beforeChange = function(changes, source) {
        if (source === 'Autofill.fill') {
          // When autofill happens fill pasteDataMap for those rows with the
          // correct values to use when saving

          let { selectedRange } = compInstance
          let { from, to } = selectedRange
          let { pasteDataMap } = compInstance

          changes.forEach((change, index) => {
            let {
              hotTableSettingsData: { data, uniqueDataKey },
            } = compInstance
            let [rowIndex, colKey] = change
            let selectionRowLength = to.row - from.row + 1

            let startRowIndex
            let referenceRowIndex

            if (changes[0][0] < from.row) {
              // Direction of autofill is towards the top
              startRowIndex = to.row
              referenceRowIndex = startRowIndex - (index % selectionRowLength)
            } else {
              // Direction of autofill is down
              startRowIndex = from.row
              referenceRowIndex = startRowIndex + (index % selectionRowLength)
            }

            let referenceRow = data[referenceRowIndex]
            let referenceId = referenceRow[uniqueDataKey]
            let valueToPaste = pasteDataMap[referenceId]

            let targetRow = data[rowIndex]
            let targetId = targetRow[uniqueDataKey]

            if (valueToPaste && !isEmpty(valueToPaste[colKey])) {
              if (pasteDataMap[targetId]) {
                pasteDataMap[targetId][colKey] = cloneDeep(valueToPaste[colKey])
              } else {
                pasteDataMap[targetId] = {
                  [colKey]: valueToPaste[colKey],
                }
              }
            }
          })
        }
      }
    },
  },
}
</script>
