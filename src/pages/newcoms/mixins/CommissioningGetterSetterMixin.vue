<script>
import { isEmpty, isNumber, isFunction } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import { API } from '@facilio/api'

export default {
  methods: {
    getPhysicalRowIndex(currentRowIndex) {
      let { $refs } = this
      if (!isEmpty($refs)) {
        return $refs['hotTableRef'].getPhysicalRowIndex(currentRowIndex)
      }
      return currentRowIndex
    },
    getFilteredInstances(props) {
      let {
        searchText = '',
        allInstances = false,
        fetchTabCount = false,
      } = props
      let { instances, commissioningTabs, appliedView } = this
      let points = []
      points = instances.filter(point => {
        let filteredTabs = commissioningTabs.filter(
          tab => tab.name !== 'allpoints'
        )
        return !filteredTabs.some(tab => {
          let { searchText } = tab
          return point.name
            .toLowerCase()
            .trim()
            .includes(searchText.toLowerCase().trim())
        })
      })
      if (!isEmpty(searchText)) {
        let instanceArr = allInstances ? instances : points
        points = instanceArr.filter(point =>
          point.name
            .toLowerCase()
            .trim()
            .includes(searchText.toLowerCase().trim())
        )
      }
      if (!isEmpty(appliedView) && !fetchTabCount) {
        points = this.getViewAppliedInstances({ points, view: appliedView })
      }
      return points
    },
    getViewAppliedInstances(props) {
      let { points, view } = props
      if (view === 'mapped') {
        points = points.filter(instance => {
          let { resourceId, fieldId } = instance
          return !isEmpty(resourceId) && !isEmpty(fieldId)
        })
      } else if (view === 'unmapped') {
        points = points.filter(instance => {
          let { resourceId, fieldId } = instance
          return isEmpty(resourceId) || isEmpty(fieldId)
        })
      }
      return points
    },
    // Returns assets depending on the selected category
    getLookupOptions(props) {
      let { row, categoryId, field, picklistOptions, pasteValuesArr } = props
      let physicalRow = this.getPhysicalRowIndex(row)
      let { hotTableData, hotTableSettingsData } = this
      let rowSelectedCategoryId =
        categoryId || (hotTableData[physicalRow] || {}).categoryId || null
      if (!isNumber(rowSelectedCategoryId)) {
        rowSelectedCategoryId = this.getCategoryId({
          rowData: hotTableData[physicalRow],
        })
      }
      if (!isEmpty(rowSelectedCategoryId) && isNumber(rowSelectedCategoryId)) {
        let { columns } = hotTableSettingsData
        let selectedColumnIndex = (columns || []).findIndex(
          column => column.data === 'resourceId'
        )
        let filters = {
          category: {
            operator: 'is',
            value: [`${rowSelectedCategoryId}`],
          },
        }
        let fieldObj = field || (columns[selectedColumnIndex] || {}).field || {}

        if (!isEmpty(pasteValuesArr)) {
          filters.name = {
            operatorId: 3,
            value: pasteValuesArr,
          }
          if (isEmpty(picklistOptions)) {
            picklistOptions = {}
          }
          picklistOptions.perPage = pasteValuesArr.length
        }
        fieldObj.filters = {
          ...(fieldObj.filters || {}),
          ...filters,
        }

        return getFieldOptions({ field: fieldObj, ...picklistOptions }).then(
          ({ options }) => {
            fieldObj.filters = {}
            return options
          }
        )
      }
      return Promise.resolve([])
    },
    getSelectOptions(props) {
      let { row, col } = props
      let { hotTableData, dataConfigMap, headers } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let activeColumnName = (headers[col] || {}).name
      if (!isEmpty(activeColumnName)) {
        let activeColumn = dataConfigMap[activeColumnName]
        if (!isEmpty(activeColumn) && isFunction(activeColumn.getOptionsFn)) {
          return activeColumn.getOptionsFn({
            ...activeRowData,
            row,
          })
        }
      }
      return Promise.resolve([])
    },
    // Returns reading options for reading column, depending on the selected category
    getReadingOptions(rowData) {
      let { categoryId } = rowData
      // To replace data from pasteData
      if (!isNumber(categoryId)) {
        categoryId = this.getCategoryId({ rowData })
      }
      let { cachedOptionsMap } = this
      let isOptionsAlreadyCached = false
      let cachedOptions = cachedOptionsMap[categoryId]
      if (!isEmpty(cachedOptions)) {
        isOptionsAlreadyCached = !isEmpty(cachedOptions.readingOptions)
      }
      if (
        !isEmpty(categoryId) &&
        isNumber(categoryId) &&
        !isOptionsAlreadyCached
      ) {
        return this.fetchReading({ categoryId }).then(readings => {
          let readingOptions = []
          if (!isEmpty(readings)) {
            readings.forEach(reading => {
              readingOptions.push({
                label: reading.displayName,
                value: reading.fieldId,
                metricEnum: reading.metricEnum,
                dataType: reading.dataType,
                values: reading.values || [],
                trueVal: reading.trueVal || '',
                falseVal: reading.falseVal || '',
              })
            })
            if (!isEmpty(cachedOptionsMap[categoryId])) {
              cachedOptionsMap[categoryId].readingOptions = readingOptions
            } else {
              cachedOptionsMap[categoryId] = {
                readingOptions: readingOptions,
              }
            }
          }
          return readingOptions
        })
      }
      return Promise.resolve(
        isOptionsAlreadyCached ? cachedOptions.readingOptions : []
      )
    },
    async fetchReading(props) {
      let { categoryId } = props
      let url = `v2/readings/assetcategory?id=${categoryId}&excludeEmptyFields=false`

      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { readings } = data || {}
        return readings || []
      }
      return []
    },
    // Returns unit options for unit column depending on the selected reading
    async getUnitOptions(rowData) {
      await this.getReadingOptions(rowData)
      let { fieldId, categoryId } = rowData
      let { metricsUnits, cachedOptionsMap } = this
      // To replace data from pasteData
      if (!isNumber(categoryId)) {
        categoryId = this.getCategoryId({ rowData })
      }
      // To replace data from pasteData
      if (!isNumber(fieldId)) {
        fieldId = this.getFieldId({ rowData })
      }
      let cachedOptions = cachedOptionsMap[fieldId]
      let isOptionsAlreadyCached = false
      if (!isEmpty(cachedOptions)) {
        isOptionsAlreadyCached = !isEmpty(cachedOptions.unitOptions)
      }
      if (!isOptionsAlreadyCached && !isEmpty(metricsUnits)) {
        let { metricWithUnits } = metricsUnits || {}
        let { readingOptions = [] } = cachedOptionsMap[categoryId] || {}
        let selectedReading = {}
        if (!isEmpty(fieldId)) {
          if (!isNumber(fieldId)) {
            selectedReading = this.getSelectedReadingByLabel(
              fieldId,
              readingOptions
            )
          } else {
            selectedReading = this.getSelectedReadingById(
              fieldId,
              readingOptions
            )
          }
        }
        let { metricEnum } = selectedReading || {}
        let { _name } = metricEnum || {}
        if (!isEmpty(_name) && !isEmpty(metricWithUnits)) {
          let selectedMetric = metricWithUnits[_name] || []
          let metricOptions = selectedMetric.map(metric => {
            let { unitId, symbol } = metric || {}
            return {
              label: symbol,
              value: unitId,
            }
          })
          if (!isEmpty(cachedOptionsMap[fieldId])) {
            cachedOptionsMap[fieldId].unitOptions = metricOptions
          } else {
            cachedOptionsMap[fieldId] = {
              unitOptions: metricOptions,
            }
          }
          return Promise.resolve(metricOptions || [])
        }
      }
      return Promise.resolve(
        isOptionsAlreadyCached ? cachedOptions.unitOptions : []
      )
    },
    // Returns place holder value for units depending on the selected reading
    getDynamicPlaceHolder(props) {
      let { row } = props || {}
      let { cachedOptionsMap, hotTableData, metricsUnits } = this
      let { orgUnitsList } = metricsUnits || {}
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      if (!isEmpty(activeRowData)) {
        let categoryId = this.getCategoryId({ rowData: activeRowData })
        let fieldId = this.getFieldId({ rowData: activeRowData })
        let { readingOptions = [] } = cachedOptionsMap[categoryId] || {}
        let selectedReading = this.getSelectedReadingById(
          fieldId,
          readingOptions
        )
        let { metricEnum } = selectedReading || {}
        let { _name } = metricEnum || {}
        if (!isEmpty(_name)) {
          let orgUnit = orgUnitsList.find(
            unit => (unit.metricEnum || {})._name === _name
          )
          if (!isEmpty(orgUnit) && !isEmpty(orgUnit.unitEnum)) {
            let { symbol } = orgUnit.unitEnum
            return symbol || ''
          }
        }
      }

      return ''
    },
    getSelectedReadingById(fieldId, readingOptions) {
      let selectedReading = readingOptions.find(
        option => option.value === fieldId
      )
      return selectedReading || {}
    },
    getSelectedReadingByLabel(fieldLabel, readingOptions) {
      let selectedReading = readingOptions.find(
        option => option.label === fieldLabel
      )
      return selectedReading || {}
    },
    getSelectedCategoryByLabel(categoryLabel) {
      let { categoryList } = this
      let selectedCategory =
        (categoryList || []).find(
          category =>
            category.label.toLowerCase().trim() ===
            (categoryLabel || '').toLowerCase().trim()
        ) || {}
      return selectedCategory
    },
    getSelectedResourceByLabel(resourceLabel, resourceOptions) {
      let selectedResource = resourceOptions.find(
        option => option.label === resourceLabel
      )
      return selectedResource || {}
    },
    getInstanceCount(searchText) {
      let instanceCount = this.getFilteredInstances({
        searchText,
        allInstances: true,
        fetchTabCount: true,
      })
      return (instanceCount || []).length
    },
    getMapCategoryLabel() {
      let { activeTab, commissioningTabs } = this
      let selectedTab = commissioningTabs[Number(activeTab)]
      let { mappedCategoryId } = selectedTab || {}
      return isEmpty(mappedCategoryId)
        ? this.$t('commissioning.sheet.mapcategory')
        : this.$t('commissioning.sheet.change_category')
    },
    getSpecialFilterValue(prop) {
      let { hotTableData } = this
      let { row } = prop
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let categoryId = this.getCategoryId({ rowData: activeRowData })
      let filters = {}
      if (!isEmpty(categoryId)) {
        filters = {
          category: {
            operator: 'is',
            value: [`${categoryId}`],
          },
        }
      }
      return filters
    },
    getPasteDataValue(key, options) {
      if (key === 'categoryId') {
        return this.getCategoryId(options)
      } else if (key === 'resourceId') {
        return this.getResourceId(options)
      } else if (key === 'fieldId') {
        return this.getFieldId(options)
      } else if (key === 'unit') {
        return this.getUnitId(options)
      } else if (key === 'pointId') {
        return this.getPointId(options)
      }
    },
    getPointId({ row, rowData }) {
      let { hotTableData, pasteDataMap } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let { id: pointId } = rowData || activeRowData
      if (!isNumber(pointId)) {
        let id = (pasteDataMap[pointId] || {}).id
        if (!isEmpty(id)) {
          pointId = id
        }
      }
      return pointId
    },
    getCategoryId({ row, rowData }) {
      let { hotTableData, pasteDataMap } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let { categoryId, id: pointId } = rowData || activeRowData
      if (!isNumber(categoryId)) {
        let id = (pasteDataMap[pointId] || {}).categoryId
        if (!isEmpty(id)) {
          categoryId = id
        }
      }
      return categoryId
    },
    getUnitId({ row, rowData }) {
      let { hotTableData, pasteDataMap } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let { unit, id: pointId } = rowData || activeRowData
      if (!isNumber(unit)) {
        let id = (pasteDataMap[pointId] || {}).unit
        if (!isEmpty(id)) {
          unit = id
        }
      }
      return unit
    },
    getResourceId({ row, rowData }) {
      let { hotTableData, pasteDataMap } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let { resourceId, id: pointId } = rowData || activeRowData
      if (!isNumber(resourceId)) {
        let id = (pasteDataMap[pointId] || {}).resourceId
        if (!isEmpty(id)) {
          resourceId = id
        }
      }
      return resourceId
    },
    getFieldId({ row, rowData }) {
      let { hotTableData, pasteDataMap } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow] || {}
      let { fieldId, id: pointId } = rowData || activeRowData
      if (!isNumber(fieldId)) {
        let id = (pasteDataMap[pointId] || {}).fieldId
        if (!isEmpty(id)) {
          fieldId = id
        }
      }
      return fieldId
    },
    getColIndices(props) {
      let { hotTableSettingsData } = this
      let { columns } = hotTableSettingsData || {}
      let { rowLength, startIndex } = props
      let categoryColIndex = null
      let readingColIndex = null
      let resourceColIndex = null
      let currentIndex = 0
      while (startIndex <= rowLength) {
        if (columns[startIndex].data === 'categoryId') {
          categoryColIndex = currentIndex
        }
        if (columns[startIndex].data === 'resourceId') {
          resourceColIndex = currentIndex
        }
        if (columns[startIndex].data === 'fieldId') {
          readingColIndex = currentIndex
        }
        startIndex++
        currentIndex++
      }
      return { categoryColIndex, readingColIndex, resourceColIndex }
    },
    setFilteredInstances(instances) {
      this.$set(this, 'filteredInstances', instances)
    },
    setColumnOptions(options, columnName) {
      let { hotTableSettingsData } = this
      let { columns } = hotTableSettingsData
      let selectedColumnIndex = (columns || []).findIndex(
        column => column.data === columnName
      )
      if (!isEmpty(selectedColumnIndex)) {
        this.$set(columns[selectedColumnIndex], 'options', options)
      }
    },
    // Have to reset all selected asset, reading and unit values
    resetActiveRowData(props) {
      let { activeRowIndex } = props
      let { hotTableData, $refs, hotTableSettingsData, instances } = this
      let { uniqueDataKey } = hotTableSettingsData
      let physicalRow = this.getPhysicalRowIndex(activeRowIndex)
      let activeRowData = hotTableData[physicalRow] || {}
      let { suggestedColumns } = activeRowData || []
      if (suggestedColumns.includes('categoryId')) {
        let index = suggestedColumns.findIndex(value => value === 'categoryId')
        suggestedColumns.splice(index, 1)
      }
      let id = activeRowData[uniqueDataKey]
      let selectedInstance = instances.find(instance => instance.id === id)
      // Have to clear invalid cells in a row
      if (!isEmpty($refs)) {
        $refs['hotTableRef'].clearRowError(id)
      }
      if (!isEmpty(activeRowData)) {
        this.$set(activeRowData, 'resourceId', null)
        this.$set(activeRowData, 'unit', null)
        this.$set(activeRowData, 'fieldId', null)
        this.resetInputValue({ selectedInstance })
      }
    },
    resetInputValue(props) {
      let { rowIndex, selectedInstance } = props
      if (!isEmpty(rowIndex) && isEmpty(selectedInstance)) {
        let { hotTableData, hotTableSettingsData, instances } = this
        let { uniqueDataKey } = hotTableSettingsData || {}
        let physicalRow = this.getPhysicalRowIndex(rowIndex)
        let activeRowData = hotTableData[physicalRow]
        let id = activeRowData[uniqueDataKey]
        selectedInstance = instances.find(instance => instance.id === id)
      }
      if (!isEmpty(selectedInstance)) {
        this.$set(selectedInstance, 'canShowSetInputIcon', false)
        this.$set(selectedInstance, 'dataType', null)
        this.$set(selectedInstance, 'inputValues', [])
      }
    },
  },
}
</script>
