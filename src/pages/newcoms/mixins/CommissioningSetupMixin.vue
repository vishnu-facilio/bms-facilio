<script>
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { deepCloneObject } from 'util/utility-methods'
import cloneDeep from 'lodash/cloneDeep'
import { API } from '@facilio/api'

const defaultHotTableProps = {
  rowHeaders: true,
  manualColumnResize: true,
  manualRowResize: true,
  autoColumnSize: true,
  renderAllRows: false,
  scrollH: 'auto',
  scrollV: 'auto',
  stretchH: 'all',
}

export default {
  methods: {
    loadDefaultMetricUnits() {
      API.get('/units/getDefaultMetricUnits').then(response => {
        let { data = {} } = response || {}
        this.metricsUnits = data
      })
    },
    initializeConstants() {
      // Initializing config map
      this.dataConfigMap = {
        name: {
          data: 'name',
          renderer: 'input',
          cellType: 'text',
          filters: true,
        },
        deviceName: {
          data: 'deviceName',
          renderer: 'input',
          cellType: 'text',
        },
        controllerName: {
          data: 'controllerName',
          renderer: 'input',
          cellType: 'text',
        },
        categoryId: {
          data: 'categoryId',
          editor: 'select',
          renderer: 'select',
          onValueChange: prop => this.onCategoryChange(prop),
          placeHolderText: `${this.$t('commissioning.sheet.select_category')}`,
          cellType: 'select',
        },
        resourceId: {
          data: 'resourceId',
          editor: 'lookup',
          renderer: 'lookup',
          specialOptionsFetch: true,
          onValueChange: prop => this.onAssetChange(prop),
          placeHolderText: `${this.$t('commissioning.sheet.select_asset')}`,
          cellType: 'lookup',
          cachedMapKey: 'resourceOptions',
        },
        fieldId: {
          data: 'fieldId',
          editor: 'select',
          renderer: 'select',
          specialOptionsFetch: true,
          onValueChange: prop => this.onReadingChange(prop),
          getOptionsFn: prop => this.getReadingOptions(prop),
          placeHolderText: `${this.$t('commissioning.sheet.select_reading')}`,
          cellType: 'select',
          cachedMapKey: 'readingOptions',
        },
        unit: {
          data: 'unit',
          editor: 'select',
          renderer: 'select',
          specialOptionsFetch: true,
          getOptionsFn: prop => this.getUnitOptions(prop),
          isDynamicPlaceHolderText: true,
          cellType: 'select',
          cachedMapKey: 'unitOptions',
        },
        instanceType: {
          data: 'instanceTypeLabel',
          renderer: 'input',
          cellType: 'text',
          filters: true,
        },
        instanceNumber: {
          data: 'instanceNumber',
          renderer: 'input',
          cellType: 'text',
          filters: true,
        },
        enumInputValues: {
          data: '',
          renderer: 'input',
          cellType: 'text',
          isSetValuesColumn: true,
        },
      }
      // To store options for different categories, each category will be having its own readings, assets options
      this.cachedOptionsMap = {}
      this.pasteOptionsMap = {}
    },
    init() {
      let { logId } = this
      let fetchInstances = `v2/commissioning/${logId}`
      this.$set(this, 'isLoading', true)
      API.get(fetchInstances)
        .then(({ data, error }) => {
          if (error) {
            let { message } = error || {}
            this.$message.error(message)
          } else {
            let { log = {}, fields = {}, resourceList = {}, unit = {} } = data
            let {
              points = [],
              agentName,
              controllers = [],
              clientMeta = {},
              headers = [],
              publishedTime,
            } = log || {}
            let { commissioningTabs } = clientMeta || {}
            if (!isEmpty(commissioningTabs)) {
              this.$set(this, 'commissioningTabs', commissioningTabs)
            }
            this.$set(this, 'isPublished', !isEmpty(publishedTime))
            this.$set(this, 'agentName', agentName)
            if (!isEmpty(controllers)) {
              let controllersName = ''
              let controllerCount = 0
              let hoverControllersName = ''
              let hoverControllerCount = 0
              controllers.forEach(controller => {
                controllerCount++
                let { name } = controller
                if (!isEmpty(controllersName) && controllerCount <= 3) {
                  controllersName = `${controllersName}, ${name}`
                } else if (isEmpty(controllersName)) {
                  controllersName = `${name}`
                }
                if (!isEmpty(hoverControllersName) && controllerCount > 3) {
                  hoverControllersName = `${hoverControllersName}, ${name}`
                } else if (
                  isEmpty(hoverControllersName) &&
                  controllerCount > 3
                ) {
                  hoverControllersName = `${name}`
                  hoverControllerCount = controllers.length - 3
                }
              })
              if (hoverControllerCount != 0) {
                this.hoverControllersName = hoverControllersName
                this.hoverControllerCount = hoverControllerCount
              }
              this.$set(this, 'controllersName', controllersName)
            }
            this.fieldsMap = fields || {}
            this.resourceListMap = resourceList || {}
            this.unitsMap = unit || {}
            this.instances = this.deserializePoints(points)
            let filteredInstances = this.getFilteredInstances({
              searchText: '',
            })
            this.setFilteredInstances(filteredInstances)
            this.headers = (headers || []).map(header => {
              return {
                ...header,
                header: this.$t(`commissioning.sheet.header_${header.name}`),
              }
            })
            this.constructHotTableData()
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => this.$set(this, 'isLoading', false))
    },
    deserializePoints(points) {
      let { fieldsMap } = this
      if (!isEmpty(points)) {
        points.forEach(point => {
          let {
            instanceType,
            dataType,
            fieldId,
            resourceId,
            categoryId,
            suggestedResourceId,
            suggestedFieldId,
            suggestedCategoryId,
          } = point
          let instanceTypeLabel = Constants.AgentInstancePoint[instanceType]
          point.instanceTypeLabel = instanceTypeLabel
          if (isEmpty(dataType) && !isEmpty(fieldId) && !isEmpty(resourceId)) {
            let type = (fieldsMap[fieldId] || {}).dataType || ''
            if (!isEmpty(type) && [4, 8].includes(type)) {
              point.dataType = type
              point.canShowSetInputIcon = true
            }
          }
          point.suggestedColumns = []

          if (isEmpty(categoryId) && !isEmpty(suggestedCategoryId)) {
            point.categoryId = suggestedCategoryId[0]
            point.suggestedColumns.push('categoryId')
          }
          if (isEmpty(resourceId) && !isEmpty(suggestedResourceId)) {
            point.resourceId = suggestedResourceId[0]
            point.suggestedColumns.push('resourceId')
          }
          if (isEmpty(fieldId) && !isEmpty(suggestedFieldId)) {
            point.fieldId = suggestedFieldId[0]
            point.suggestedColumns.push('fieldId')
          }
        })
      }
      return points
    },
    constructHotTableData() {
      let { hotTableSettingsData } = this
      hotTableSettingsData.colHeaders = this.constructHotTableHeaders()
      hotTableSettingsData.columns = this.constructHotColumns()
      hotTableSettingsData = Object.assign(
        defaultHotTableProps,
        hotTableSettingsData
      )
      // To render one data extra below the viewport
      hotTableSettingsData.viewportRowRenderingOffset = 1
      hotTableSettingsData.columnSorting = {
        initialConfig: {
          column: 0,
          sortOrder: 'asc'
        }
      }

      this.$set(this, 'hotTableSettingsData', hotTableSettingsData)
    },
    constructHotTableHeaders() {
      let { headers } = this
      let clonedHeaders = cloneDeep(headers)
      let headersArr = clonedHeaders.map(column => column.header)
      return headersArr.concat(['', ''])
    },
    constructHotColumns() {
      let { dataConfigMap, headers } = this
      let clonedDataConfigMap = cloneDeep(dataConfigMap)
      let clonedHeaders = cloneDeep(headers)
      let columnsConfigArr = []
      clonedHeaders.forEach(columnObj => {
        let dataConfigObj = {}
        if (!isEmpty(columnObj)) {
          let { name: columnName, editable } = columnObj
          dataConfigObj = clonedDataConfigMap[columnName]
          let { editor, specialOptionsFetch } = dataConfigObj || {}
          if (editor === 'lookup') {
            dataConfigObj.field = this.constructLookupField(columnName)
          } else if (editor === 'select') {
            /*
             Specialoptionsfetch boolean is to diffenentiate from normal behaviour of select box fields
             In General, entire column will have same set of dropdown options, but here
             every field have different options depending on the selected category.
            */
            if (specialOptionsFetch) {
              dataConfigObj.field = this.constructSpecialSelectField(columnName)
            } else {
              dataConfigObj.options = this.constructFieldOptions(columnObj)
            }
          }
          dataConfigObj.readOnly = !editable
          columnsConfigArr.push(dataConfigObj)
        } else {
          columnsConfigArr.push({
            type: 'text',
          })
        }
      })
      return columnsConfigArr
    },
    constructLookupField(columnName) {
      let { resourceListMap } = this
      let lookupFieldObj = {
        isDataLoading: false,
        options: [],
        optionsCache: {},
        lookupModuleName: 'asset',
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        forceFetchAlways: true,
        filters: {},
      }
      if (columnName === 'resourceId') {
        lookupFieldObj.optionsCache = resourceListMap
      }
      return deepCloneObject(lookupFieldObj)
    },
    constructSpecialSelectField(columnName) {
      let { fieldsMap } = this
      let field = deepCloneObject({
        optionsCache: {},
        isSelectOptionsLoading: false,
        options: [],
      })
      if (columnName === 'fieldId') {
        let cachedOptions = {}
        Object.entries(fieldsMap).forEach(([key, value]) => {
          cachedOptions = {
            ...cachedOptions,
            [key]: value['name'],
          }
        })
        field.optionsCache = cachedOptions
      } else if (columnName === 'unit') {
        field.optionsCache = this.unitsMap
      }
      return field
    },
    constructFieldOptions(columnObj) {
      let { categoryList } = this
      let { name } = columnObj
      let options = []
      if (name === 'categoryId' && !isEmpty(categoryList)) {
        return categoryList
      }
      return options
    },
  },
}
</script>
