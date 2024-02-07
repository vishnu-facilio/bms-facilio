<script>
import { isEmpty, isFunction, isNumber } from '@facilio/utils/validation'

export default {
  methods: {
    handleSelectOrLookupChange(valueObj) {
      let { dataConfigMap, headers } = this
      let { activeColumnIndex } = valueObj
      let activeColumnName = (headers[activeColumnIndex] || {}).name
      if (!isEmpty(activeColumnName)) {
        let activeColumn = dataConfigMap[activeColumnName]
        if (!isEmpty(activeColumn) && isFunction(activeColumn.onValueChange)) {
          activeColumn.onValueChange(valueObj)
        }
      }
    },
    handleAfterDocumentKeyDown(props, keyName) {
      let { range } = props
      let { from, to } = range || {}
      let { row: startRow, col: startCol } = from || {}
      let { hotTableSettingsData } = this
      let { columns = [] } = hotTableSettingsData || {}
      let { row: endRow, col: endCol } = to || {}

      for (let row = startRow; row <= endRow; row++) {
        for (let column = startCol; column <= endCol; column++) {
          let selectedColumn = columns[column]
          let { data } = selectedColumn || {}
          if (keyName === 'Backspace') {
            if (data === 'categoryId') {
              this.resetActiveRowData({
                activeRowIndex: row,
              })
            } else if (data === 'resourceId' || data === 'fieldId') {
              this.resetInputValue({ rowIndex: row })
            }
          }
        }
      }
    },
    onCategoryChange(valueObj) {
      this.resetActiveRowData(valueObj)
    },
    onAssetChange(props) {
      let { value, activeRowIndex } = props
      let { hotTableData } = this
      let physicalRow = this.getPhysicalRowIndex(activeRowIndex)
      let rowData = hotTableData[physicalRow]
      let { suggestedColumns } = rowData || []
      if (suggestedColumns.includes('resourceId')) {
        let index = suggestedColumns.findIndex(value => value === 'resourceId')
        suggestedColumns.splice(index, 1)
      }
      let fieldId = this.getFieldId({ rowData })
      let categoryId = this.getCategoryId({ rowData })
      this.getReadingOptions({ categoryId, row: activeRowIndex }).then(
        options => {
          let selectedOption = options.find(option => option.value === fieldId)
          if (!isEmpty(selectedOption)) {
            this.constructInputValues({
              selectedOption,
              rowData,
              resourceId: value,
              fieldId,
              physicalRow,
            })
          }
        }
      )
    },
    onReadingChange(props) {
      let { activeRowIndex, value } = props
      let { hotTableSettingsData, hotTableData } = this
      let { columns } = hotTableSettingsData
      let columnData = columns.find(column => column.data === 'fieldId')
      let { field = [] } = columnData || {}
      let physicalRow = this.getPhysicalRowIndex(activeRowIndex)
      let rowData = hotTableData[physicalRow]
      let { suggestedColumns } = rowData || []
      if (suggestedColumns.includes('fieldId')) {
        let index = suggestedColumns.findIndex(value => value === 'fieldId')
        suggestedColumns.splice(index, 1)
      }
      let resourceId = this.getResourceId({ rowData })
      let selectedOption = field.options.find(option => option.value === value)
      if (!isEmpty(selectedOption)) {
        this.constructInputValues({
          selectedOption,
          rowData,
          resourceId,
          fieldId: value,
          physicalRow,
        })
      }
    },
    showSetInputValues(props) {
      let { id } = props || {}
      let { instances } = this
      let selectedInstance = instances.find(instance => instance.id === id)
      if (!isEmpty(selectedInstance)) {
        let { canShowSetInputIcon } = selectedInstance
        return !!canShowSetInputIcon
      }
    },
    constructInputValues(props) {
      let { selectedOption, rowData, resourceId, fieldId } = props
      let { hotTableSettingsData, instances } = this
      let { uniqueDataKey } = hotTableSettingsData
      let id = rowData[uniqueDataKey]
      let selectedInstances = instances.find(instance => instance.id === id)
      if (!isEmpty(selectedOption)) {
        let { dataType } = selectedOption
        let { canShowSetInputIcon } = selectedInstances
        if (
          [4, 8].includes(dataType) &&
          !isEmpty(resourceId) &&
          !isEmpty(fieldId)
        ) {
          selectedInstances.canShowSetInputIcon = true
          // To reset input values when reading type changed
          if (dataType !== selectedInstances.dataType) {
            selectedInstances.inputValues = []
          }
        } else if (canShowSetInputIcon) {
          selectedInstances.canShowSetInputIcon = false
          selectedInstances.inputValues = []
          selectedInstances.dataType = null
        }
      }
    },
    async onPasteEventHook(props) {
      let { pasteOptionsMap } = this
      let {
        preHookData,
        cellType,
        cachedMapKey,
        rowLength,
        startIndex,
        selectedRowsArr,
        rowIndex,
        propName,
        field,
        pasteValue,
        activeRowData,
      } = props || {}
      let { resourceNameVsCategoryMap, fetchReadingsList } = preHookData || {}
      let currentCategoryId = this.getCategoryId({
        rowData: activeRowData,
      })
      let currentReadingId = this.getFieldId({ rowData: activeRowData })
      let promiseHash = []
      if (isEmpty(pasteOptionsMap)) {
        if (!isEmpty(resourceNameVsCategoryMap)) {
          for (let [key, values] of Object.entries(resourceNameVsCategoryMap)) {
            let promise = this.getLookupOptions({
              categoryId: Number(key),
              pasteValuesArr: values,
            }).then(options => {
              if (!isEmpty(pasteOptionsMap[key])) {
                pasteOptionsMap[key].resourceOptions = options || []
              } else {
                pasteOptionsMap[key] = {
                  resourceOptions: options,
                }
              }
            })
            promiseHash.push(promise)
          }
        }
        if (!isEmpty(fetchReadingsList)) {
          for (let [, categoryId] of fetchReadingsList.entries()) {
            promiseHash.push(this.getReadingOptions({ categoryId }))
          }
        }
        await Promise.all(promiseHash)
      }

      let { categoryColIndex, readingColIndex } = this.getColIndices({
        rowLength,
        startIndex,
      })
      if (!isEmpty(categoryColIndex)) {
        currentCategoryId = selectedRowsArr[rowIndex][categoryColIndex] || null
        if (!isNumber(currentCategoryId)) {
          let selectedCategory = this.getSelectedCategoryByLabel(
            currentCategoryId
          )
          currentCategoryId = selectedCategory.value
        }
      }
      if (!currentReadingId && !isEmpty(readingColIndex)) {
        currentReadingId = selectedRowsArr[rowIndex][readingColIndex] || null
      }
      if (cellType === 'select') {
        if (propName === 'unit') {
          await this.getUnitOptions({
            categoryId: currentCategoryId,
            fieldId: currentReadingId,
          })
        }
        return this.handlePasteEventForSelect({
          cachedMapKey,
          currentCategoryId,
          currentReadingId,
          propName,
          field,
          pasteValue,
        })
      } else if (cellType === 'lookup') {
        return this.handlePasteEventForLookup({
          currentCategoryId,
          cachedMapKey,
          field,
          pasteValue,
        })
      }
    },
    handlePasteEventForSelect(props) {
      let {
        cachedMapKey,
        currentCategoryId,
        field,
        pasteValue,
        currentReadingId,
        propName,
      } = props || {}
      let { cachedOptionsMap } = this
      let mapData = cachedOptionsMap[currentCategoryId] || {}
      let unitMapData = cachedOptionsMap[currentReadingId] || {}
      let options =
        propName === 'fieldId'
          ? mapData[cachedMapKey]
          : unitMapData[cachedMapKey]
      if (!isEmpty(options)) {
        let selectedOption =
          options.find(
            option =>
              option.label.toLowerCase().trim() ===
              pasteValue.toLowerCase().trim()
          ) || {}
        let { value, label } = selectedOption || {}
        if (!isEmpty(value)) {
          field.optionsCache[value] = label
          pasteValue = value
        }
      }
      return pasteValue
    },
    handlePasteEventForLookup(props) {
      let { pasteOptionsMap } = this
      let { currentCategoryId, cachedMapKey, field, pasteValue } = props
      let dataMap = pasteOptionsMap[currentCategoryId] || {}
      let options = dataMap[cachedMapKey] || []
      if (!isEmpty(options)) {
        let selectedOption =
          options.find(
            option =>
              option.label.toLowerCase().trim() ===
              pasteValue.toLowerCase().trim()
          ) || {}
        let { label, value } = selectedOption
        if (!isEmpty(value)) {
          field.optionsCache[value] = label
          pasteValue = value
        }
      }
      return pasteValue
    },
    afterPasteHook() {
      // To reset paste cached data for paste event is done
      this.pasteOptionsMap = {}
    },
    async onAutoFillRowChange(props) {
      let { hotTableData } = this
      let { index, rowChanges } = props
      let physicalRow = this.getPhysicalRowIndex(index)
      let activeRowData = hotTableData[physicalRow] || {}
      await this.dragAndDropValidation({
        row: index,
        rowChanges: rowChanges,
        activeRowData,
      })
      return this.setupUnitInputs({ activeRowData })
    },
    async dragAndDropValidation(props) {
      let { $refs, hotTableSettingsData } = this || {}
      let { uniqueDataKey, columns } = hotTableSettingsData || {}
      let { row, rowChanges, activeRowData } = props || {}

      let id = activeRowData[uniqueDataKey]
      for (let [, rowData] of rowChanges.entries()) {
        let [, prop, oldValue, newValue] = rowData
        if (prop == 'categoryId') {
          if (oldValue != newValue || isEmpty(oldValue)) {
            this.$set(activeRowData, 'resourceId', null)
            this.$set(activeRowData, 'fieldId', null)
            this.$set(activeRowData, 'unit', null)
          }
        } else if (prop == 'resourceId') {
          let selectedColumn = (columns || []).find(
            column => column.data === 'resourceId'
          )
          let { field } = selectedColumn || {}
          let { optionsCache } = field || {}
          let resourceValueLabel = optionsCache[newValue] || ''
          if (newValue != oldValue || isEmpty(oldValue)) {
            let categoryId = this.getCategoryId({ rowData: activeRowData })

            if (isNumber(newValue) && isNumber(categoryId)) {
              let selectedOption = {}
              let options = {}
              if (!isEmpty(resourceValueLabel)) {
                options = await this.getLookupOptions({
                  categoryId: categoryId,
                  pasteValuesArr: [`${resourceValueLabel}`],
                })
              } else {
                options = await this.getLookupOptions({
                  categoryId: categoryId,
                })
              }

              selectedOption =
                options.find(option => option.value === newValue) || {}
              if (isEmpty(selectedOption)) {
                $refs['hotTableRef'].pushOrRemoveError({
                  isValidEntry: false,
                  isAlreadyInvalid: false,
                  id,
                  propName: 'resourceId',
                })
                this.$set(activeRowData, 'resourceId', 'Invalid Value')
              } else {
                this.$set(activeRowData, 'resourceId', newValue)
              }
            }
          }
        } else if (prop == 'fieldId') {
          if (newValue != oldValue || isEmpty(oldValue)) {
            let categoryId = this.getCategoryId({ rowData: activeRowData })
            if (isNumber(categoryId) && isNumber(newValue)) {
              let options = await this.getReadingOptions({ categoryId })
              let selectedOption = options.find(
                option => option.value === newValue
              )
              if (isEmpty(selectedOption)) {
                $refs['hotTableRef'].pushOrRemoveError({
                  isValidEntry: false,
                  isAlreadyInvalid: false,
                  id,
                  propName: 'fieldId',
                })
                this.$set(activeRowData, 'fieldId', 'Invalid Value')
              } else {
                this.$set(activeRowData, 'fieldId', newValue)
              }
            }
          }
        } else if (prop == 'unit') {
          if (newValue != oldValue || isEmpty(oldValue)) {
            let fieldId = this.getFieldId({ rowData: activeRowData })
            let categoryId = this.getCategoryId({ rowData: activeRowData })
            if (!isEmpty(fieldId)) {
              if (
                isNumber(fieldId) &&
                isNumber(categoryId) &&
                isNumber(newValue)
              ) {
                let options = await this.getUnitOptions({
                  categoryId: categoryId,
                  fieldId: fieldId,
                })
                let selectedOption = options.find(
                  option => option.value === newValue
                )
                if (isEmpty(selectedOption)) {
                  $refs['hotTableRef'].pushOrRemoveError({
                    isValidEntry: false,
                    isAlreadyInvalid: false,
                    id,
                    propName: 'unit',
                  })
                  this.$set(activeRowData, 'unit', 'Invalid Value')
                } else {
                  this.$set(activeRowData, 'unit', newValue)
                }
              }
            } else {
              this.$set(activeRowData, 'unit', null)
            }
          }
        }
      }
    },
    async setupUnitInputs(props) {
      let { hotTableSettingsData, instances } = this
      let { uniqueDataKey } = hotTableSettingsData || {}
      let { activeRowData } = props
      let id = activeRowData[uniqueDataKey]
      let selectedInstance = instances.find(instance => instance.id === id)
      if (!isEmpty(activeRowData)) {
        let { canShowSetInputIcon } = selectedInstance
        let categoryId = this.getCategoryId({ rowData: activeRowData })
        let resourceId = this.getResourceId({ rowData: activeRowData })
        let fieldId = this.getFieldId({ rowData: activeRowData })
        if (isNumber(resourceId) && isNumber(fieldId)) {
          let options = await this.getReadingOptions({ categoryId })
          let selectedOption = options.find(option => option.value === fieldId)
          if (!isEmpty(selectedOption)) {
            let { dataType } = selectedOption

            if ([4, 8].includes(dataType)) {
              this.$set(selectedInstance, 'canShowSetInputIcon', true)
              // To reset input values when reading type changed
              if (dataType !== selectedInstance.dataType) {
                this.$set(selectedInstance, 'dataType', dataType)
                this.$set(selectedInstance, 'inputValues', [])
              }
            } else if (canShowSetInputIcon) {
              this.resetInputValue({ selectedInstance })
            }
          }
        } else if (canShowSetInputIcon) {
          this.resetInputValue({ selectedInstance })
        }
      }
    },
    preBeforePasteEventHanlder(selectedRowsArr, coords) {
      // Have to reset all selected asset, reading and unit values, if categoryid is different
      let [coordsObj] = coords || []
      let { startRow, startCol } = coordsObj || {}
      let newCurrentCategoryId = null
      let fetchReadingsList = []
      let uniqueReadingsList = []
      let { hotTableData } = this
      let resourceNameVsCategoryMap = {}
      selectedRowsArr.forEach((row, rowIndex) => {
        let activeRow = startRow + rowIndex
        let physicalRow = this.getPhysicalRowIndex(activeRow)
        let activeRowData = hotTableData[physicalRow] || {}
        let endCol = startCol + row.length
        let existingCategoryId = this.getCategoryId({
          rowData: activeRowData,
        })
        let resourceValue = null
        let readingValue = null
        let {
          categoryColIndex,
          readingColIndex,
          resourceColIndex,
        } = this.getColIndices({
          rowLength: endCol,
          startIndex: startCol,
        })
        if (!isEmpty(categoryColIndex)) {
          newCurrentCategoryId =
            selectedRowsArr[rowIndex][categoryColIndex] || null
          let selectedCategory = this.getSelectedCategoryByLabel(
            newCurrentCategoryId
          )
          newCurrentCategoryId = selectedCategory.value

          if (newCurrentCategoryId !== existingCategoryId) {
            this.resetActiveRowData({
              activeRowIndex: activeRow,
            })
          }
        }
        // Constructing filters for fetchting resource based on every category pasted
        if (!isEmpty(resourceColIndex)) {
          newCurrentCategoryId = isEmpty(newCurrentCategoryId)
            ? this.getCategoryId({ row: activeRow })
            : newCurrentCategoryId
          resourceValue = selectedRowsArr[rowIndex][resourceColIndex] || null

          if (!isEmpty(resourceValue) && !isEmpty(newCurrentCategoryId)) {
            let cachedArr = resourceNameVsCategoryMap[newCurrentCategoryId]
            if (isEmpty(cachedArr)) {
              resourceNameVsCategoryMap[newCurrentCategoryId] = [resourceValue]
            } else {
              if (!cachedArr.includes(resourceValue)) {
                cachedArr.push(resourceValue)
              }
            }
          }
        }
        if (!isEmpty(readingColIndex)) {
          newCurrentCategoryId = isEmpty(newCurrentCategoryId)
            ? this.getCategoryId({ row: activeRow })
            : newCurrentCategoryId

          readingValue = selectedRowsArr[rowIndex][readingColIndex] || null
          if (!isEmpty(readingValue)) {
            if (!uniqueReadingsList.includes(readingValue)) {
              uniqueReadingsList.push(readingValue)
            }
            if (!fetchReadingsList.includes(newCurrentCategoryId)) {
              fetchReadingsList.push(newCurrentCategoryId)
            }
          }
        }
      })
      return {
        resourceNameVsCategoryMap,
        fetchReadingsList,
        uniqueReadingsList,
      }
    },
    onSearchChange(event) {
      let { canShowSearchResults, mappedCategory } = this
      let { target } = event || {}
      let { value } = target || {}
      let instances = this.getFilteredInstances({
        searchText: value,
      })
      this.setFilteredInstances(instances)
      if (!isEmpty(mappedCategory)) {
        this.$set(this, 'mappedCategory', null)
      }
      if (!canShowSearchResults) {
        this.$set(this, 'canShowSearchResults', true)
      }
    },
    handleCategoryChange(value) {
      let { filteredInstances, commissioningTabs, activeTab } = this
      let isAnyInstanceAlreadyMapped = filteredInstances.some(
        instance =>
          !isEmpty(instance.categoryId) && value !== instance.categoryId
      )
      if (isAnyInstanceAlreadyMapped) {
        let dialogObj = {
          title: 'Map Category',
          message: 'Are you sure you want to proceed mapping this category?',
          rbDanger: true,
          rbLabel: 'Proceed',
        }
        this.$dialog.confirm(dialogObj).then(canProceed => {
          if (canProceed) {
            this.mapSelectedCategory(value)
          } else {
            let selectedTab = commissioningTabs[Number(activeTab)]
            let { mappedCategoryId = null } = selectedTab || {}
            this.$set(this, 'mappedCategory', mappedCategoryId)
          }
        })
      } else {
        this.mapSelectedCategory(value)
      }
    },
    mapSelectedCategory(value) {
      let { filteredInstances, $refs, activeTab } = this
      if (!isEmpty(filteredInstances)) {
        let instances = filteredInstances.map(instance => {
          let { id: pointId } = instance
          let categoryId = this.getCategoryId({ rowData: instance })
          if (categoryId !== value) {
            if (!isEmpty($refs)) {
              $refs['hotTableRef'].clearRowError(pointId)
            }
            instance.resourceId = null
            instance.unit = null
            instance.fieldId = null
          }
          instance.categoryId = value
          return instance
        })
        if (!activeTab) {
          this.addNewCommissioningTab(value, instances)
        } else {
          this.setFilteredInstances(instances)
        }
      }
    },
    onViewChange(view) {
      let { commissioningTabs, activeTab } = this
      let activeTabSearchText =
        (commissioningTabs[Number(activeTab)] || {}).searchText || ''
      this.$set(this, 'appliedView', view)
      let tabInstances = this.getFilteredInstances({
        searchText: activeTabSearchText,
        allInstances: true,
      })
      this.setFilteredInstances(tabInstances)
    },
    handleTabClick(tabData) {
      let { index } = tabData
      let { commissioningTabs } = this
      let selectedTab = commissioningTabs[Number(index)] || {}
      let { searchText, mappedCategoryId } = selectedTab
      this.$set(this, 'mappedCategory', mappedCategoryId)
      this.$set(this, 'isSearchActive', false)
      this.$set(this, 'canShowSearchResults', false)
      this.$set(this, 'searchText', '')
      this.$set(this, 'filterArr', [])
      let instances = this.getFilteredInstances({
        searchText: searchText,
        allInstances: true,
      })
      this.setFilteredInstances(instances)
    },
    handleSorting(
      currentSortConfig,
      destinationSortConfigs,
      columnSortPlugin,
      compInstance
    ) {
      let { hotTableSettingsData, hotTableData } = this
      let { activeColumn } = compInstance || {}
      let { columns } = hotTableSettingsData || {}
      let isAsc = true
      let { length } = destinationSortConfigs || {}
      let sortConfig = destinationSortConfigs[0] || {}

      let { sortOrder, column } = sortConfig || {}
      let sortColumn =
        length > 0 ? this.$getProperty(columns, `${column}.data`) : 'name'
      isAsc = length > 0 ? sortOrder == 'asc' : isAsc

      let { options } = activeColumn || {}
      let { field } = activeColumn || {}
      let { optionsCache } = field || {}
      hotTableData.sort((a, b) => {
        let la = null
        let lb = null
        if (sortColumn == 'categoryId') {
          la = options.find(option => {
            let { value } = option
            return value == a[sortColumn]
          })
          {
            let { label } = la || {}
            la = label ? label.toLowerCase().trim() : null
          }

          lb = options.find(option => {
            let { value } = option
            return value == b[sortColumn]
          })
          {
            let { label } = lb || {}
            lb = label ? label.toLowerCase().trim() : null
          }
        } else if (sortColumn == 'resourceId' || sortColumn == 'fieldId') {
          let label_a = optionsCache[a[sortColumn]]
          let label_b = optionsCache[b[sortColumn]]
          la = label_a ? label_a.toLowerCase().trim() : null
          lb = label_b ? label_b.toLowerCase().trim() : null
        } else {
          la = a[sortColumn].toLowerCase()
          lb = b[sortColumn].toLowerCase()
        }

        if (la == null) return 1
        if (lb == null) return -1
        if (la < lb) return isAsc ? -1 : 1
        if (la > lb) return isAsc ? 1 : -1
        return 0
      })
      return false
    },
  },
}
</script>
