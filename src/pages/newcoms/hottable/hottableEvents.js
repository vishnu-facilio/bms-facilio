import { isEmpty, isFunction, isNumber } from '@facilio/utils/validation'
import { getKeyByValue } from 'util/utility-methods'
import { getFieldOptions } from 'util/picklist'

export default {
  methods: {
    /* Event handling related methods */
    onKeyDownEvent(event) {
      //TO DO , Unable to capture Esc key here , must make sure ESC key press closes the editor
      event.stopPropagation()
    },
    onMouseDownEvent(event) {
      event.stopPropagation()
    },
    handleRecordSelected(option) {
      let { activeRowIndex, activeColumnIndex } = this
      let { value } = option || {}
      if (!isEmpty(option)) {
        this.cacheLookUpLabels(option)
      }
      this.activeEditorInstance.instance.deselectCell()
      this.$emit('onHotTableLookupFieldChange', {
        value,
        activeRowIndex,
        activeColumnIndex,
      })
    },
    handleSelectChange(value) {
      let { activeRowIndex, activeColumnIndex } = this
      // Close editor for on select change
      this.$emit('onHotTableSelectChange', {
        value,
        activeRowIndex,
        activeColumnIndex,
      })
      if (!isEmpty(value)) {
        this.cacheSelectLabels(value)
      }
      this.activeEditorInstance.instance.deselectCell()
    },
    async handlePasteEvent(selectedRowsArr, coords, hotInstance) {
      let { hotTableSettingsData } = this
      let { activeColumn } = this
      let { columns } = hotTableSettingsData || {}
      let selectedColumn = columns.find(
        column => column.data === activeColumn.data
      )
      let preHookData = {}
      selectedRowsArr = this.normalizeSelectedRows(selectedRowsArr, coords)
      let { activeTab } = this
      if (
        activeTab === '0' ||
        activeTab == null ||
        selectedColumn.readOnly === false
      ) {
        preHookData = this.preBeforePasteEventHanlder(selectedRowsArr, coords)
        this.resolvePasteData({
          selectedRowsArr,
          coords,
          hotInstance,
          preHookData,
        })
      }
    },
    async resolvePasteData(props) {
      let { selectedRowsArr, coords, hotInstance, preHookData } = props
      let { hotTableData, pasteDataMap } = this
      let isinvite = this.hotTableSettingsData.isinvite
      let [selectedCellsCoords = {}] = coords
      let { startRow, startCol } = selectedCellsCoords
      for (let [rowIndex, row] of selectedRowsArr.entries()) {
        let currentRow = startRow + rowIndex
        let physicalRow = this.getPhysicalRowIndex(currentRow)
        let activeRowData = hotTableData[physicalRow] || {}
        if (!isEmpty(row)) {
          let rowLength = row.length
          for (let [colIndex, pasteValue] of row.entries()) {
            let currentCol = startCol + colIndex
            let cellProperties = hotInstance.getCellMeta(currentRow, currentCol)
            let rowsData = hotInstance.getDataAtRow(currentRow)
            let {
              cellType,
              specialOptionsFetch,
              options = [],
              prop,
              field,
              cachedMapKey,
            } = cellProperties || {}
            let resolvedCellValue = pasteValue
            if (specialOptionsFetch) {
              resolvedCellValue = await this.onPasteEventHook({
                preHookData,
                cellType,
                cachedMapKey,
                startIndex: startCol,
                rowLength,
                selectedRowsArr,
                rowIndex,
                propName: prop,
                field,
                pasteValue,
                activeRowData,
              })
              this.setPasteData({
                rowData: activeRowData,
                rowsData,
                propName: prop,
                colValue: resolvedCellValue,
                cellProperties,
                pasteDataMap,
                cellType,
                isinvite,
              })
            } else if (['select', 'lookup'].includes(cellType)) {
              if (cellType === 'select') {
                let selectedOption = options.find(option => {
                  let { label } = option
                  return (
                    label.toLowerCase().trim() ===
                    pasteValue.toLowerCase().trim()
                  )
                })
                let { value } = selectedOption || {}
                resolvedCellValue = value || pasteValue
              } else if (cellType === 'lookup') {
                let resolvedLookupValue = await this.pasteEventLookupHandler({
                  field,
                  pasteValue,
                })
                resolvedCellValue = resolvedLookupValue
              }
            }
            this.setPasteData({
              rowData: activeRowData,
              rowsData,
              propName: prop,
              colValue: resolvedCellValue,
              cellProperties,
              pasteDataMap,
              cellType,
              isinvite,
            })
          }
          await this.afterRowPaste({ activeRowData })
        }
      }

      this.$emit('update:pasteDataMap', pasteDataMap)
      if (!isEmpty(hotInstance)) {
        hotInstance.render()
      }
      this.$emit('update:isPasteInProgress', false)
      this.afterPasteHook()
    },
    async pasteEventLookupHandler(props) {
      let { field, pasteValue } = props
      let resolvedLookupValue = ''
      let { optionsCache = {} } = field
      if (!isEmpty(optionsCache)) {
        let keyValue = getKeyByValue(optionsCache, pasteValue)
        if (!isEmpty(keyValue)) {
          resolvedLookupValue = Number(keyValue)
        }
      }
      if (isEmpty(resolvedLookupValue)) {
        field.filters = {
          name: {
            operatorId: 5,
            value: [pasteValue],
          },
        }
        let { options } = (await getFieldOptions({ field })) || {}
        if (!isEmpty(options)) {
          field.options = options
          let selectedOption =
            options.find(
              option =>
                option.label.toLowerCase().trim() ===
                pasteValue.toLowerCase().trim()
            ) || {}
          resolvedLookupValue = selectedOption.value
          if (!isEmpty(resolvedLookupValue)) {
            field.optionsCache[resolvedLookupValue] = selectedOption.label
          }
        } else {
          resolvedLookupValue = pasteValue
        }
        field.filters = {}
      }
      return resolvedLookupValue
    },
    setPasteData(prop) {
      let {
        rowData,
        propName,
        colValue,
        cellProperties,
        pasteDataMap,
        rowsData,
        cellType,
        isinvite,
      } = prop
      let { cellsErrorMap, hotTableSettingsData } = this
      let { uniqueDataKey } = hotTableSettingsData || {}
      let id = !isEmpty(rowData) ? rowData[uniqueDataKey] : null
      let isValidEntry = true
      if (isFunction(cellProperties.validateFn)) {
        isValidEntry = cellProperties.validateFn({
          cellValue: colValue,
          rowsData,
          cellType,
        })
      }
      let isAlreadyInvalid = (cellsErrorMap[id] || []).includes(propName)
      this.pushOrRemoveError({
        isValidEntry,
        isAlreadyInvalid,
        id,
        propName,
      })
      if (id) {
        if (isinvite) {
          if (isEmpty(pasteDataMap[id])) {
            pasteDataMap[id] = {}
          }
          if (['select', 'lookup'].includes(cellType) && isNumber(colValue)) {
            pasteDataMap[id][propName] = colValue
          } else if (
            ['select', 'lookup'].includes(cellType) &&
            !isNumber(colValue)
          ) {
            this.$message.error(this.$t('common._common.invalid_value'))
            pasteDataMap[id][propName] = null
            this.activeEditorInstance.instance.setDataAtCell(
              cellProperties.row,
              cellProperties.col,
              null
            )
          } else {
            pasteDataMap[id][propName] = colValue
          }
        } else {
          if (isEmpty(pasteDataMap[id])) {
            pasteDataMap[id] = {}
          }
          if (['select', 'lookup'].includes(cellType) && isNumber(colValue)) {
            pasteDataMap[id][propName] = colValue
          } else if (
            ['select', 'lookup'].includes(cellType) &&
            !isNumber(colValue)
          ) {
            this.$message.error(this.$t('common._common.invalid_value'))
            pasteDataMap[id][propName] = colValue
          }
        }
      }
    },
    normalizeSelectedRows(selectedRowsArr, coords) {
      let [selectedCellsCoords = {}] = coords
      let { startRow, startCol, endRow, endCol } = selectedCellsCoords
      let selectedArrRowLength = selectedRowsArr.length
      let selectedArrColLength = selectedRowsArr[0].length
      let coordsRowLength = endRow - startRow + 1
      let coordsColLength = endCol - startCol + 1
      let canSkipNormalization = this.skipNormalization({
        endRow,
        startRow,
        endCol,
        startCol,
        selectedArrRowLength,
        selectedArrColLength,
        coordsRowLength,
        coordsColLength,
      })
      if (!canSkipNormalization) {
        let resultArr = []
        let rowCount = 0
        let dataRowCount = 0
        while (rowCount < coordsRowLength) {
          let colCount = 0
          let dataColCount = 0
          if (selectedArrRowLength <= dataRowCount) {
            dataRowCount = 0
          }
          while (colCount < coordsColLength) {
            if (selectedArrColLength <= dataColCount) {
              dataColCount = 0
            }
            if (!isEmpty(resultArr[rowCount])) {
              resultArr[rowCount].push(
                selectedRowsArr[dataRowCount][dataColCount]
              )
            } else {
              resultArr[rowCount] = [
                selectedRowsArr[dataRowCount][dataColCount],
              ]
            }
            colCount++
            dataColCount++
          }
          rowCount++
          dataRowCount++
        }
        return resultArr
      }
      return selectedRowsArr
    },
    skipNormalization(props) {
      let {
        selectedArrRowLength,
        selectedArrColLength,
        coordsRowLength,
        coordsColLength,
      } = props
      return (
        selectedArrRowLength >= coordsRowLength &&
        selectedArrColLength >= coordsColLength
      )
    },
  },
}
