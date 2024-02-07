import Handsontable from 'handsontable'
import {
  isValidDateTime,
  parseDateTime,
  getFormattedDateTime,
} from 'src/components/handsontable/base/DatePickerUtils'
import { isNullOrUndefined, isEmpty } from '@facilio/utils/validation'

const lookupIconClick = function(
  row,
  column,
  hotInstance,
  cellProperties,
  componentInstance
) {
  hotInstance.selectCell(row, column)
  hotInstance.getActiveEditor().beginEditing()
  componentInstance.showLookupWizard(cellProperties.field, true)
}

const iconClickListener = function(row, column, hotInstance) {
  hotInstance.selectCell(row, column)
  hotInstance.getActiveEditor().beginEditing()
}

const enumIconClickListener = function(componentInstance, row) {
  componentInstance.onEnumIconClick(row)
}

export const CustomRendererWrapper = (componentInstance, componentName) => {
  let customRendererHash = {}

  /*
    TO-DO:
    All icon renderers, optimize  appending icons to DOM ,dont destroy the icon each time ,
    append new value to TD dom instead of clearing with innerHTML=""
  */
  let customInputRenderer = function(
    hotInstance,
    td,
    row,
    column,
    prop,
    value,
    cellProperties
  ) {
    Handsontable.renderers.TextRenderer.apply(this, arguments)
    let { isSetValuesColumn } = cellProperties
    let {
      hotTableData,
      hotTableSettingsData,
      cellsErrorMap,
    } = componentInstance
    let { uniqueDataKey } = hotTableSettingsData || {}
    let isValidEntry = true
    let physicalRow = hotInstance.toPhysicalRow(row)
    let currentRowData = hotTableData[physicalRow]
    let id = !isEmpty(currentRowData) ? currentRowData[uniqueDataKey] : null
    if (!isEmpty(cellsErrorMap) && id) {
      if (!isEmpty(cellsErrorMap[id])) {
        isValidEntry = !cellsErrorMap[id].includes(prop)
      }
    }
    if (!isValidEntry) {
      td.classList.add('htInvalid')
    }
    if (isSetValuesColumn) {
      let canShow = componentInstance._showSetInputValues({ id })
      if (canShow) {
        td.innerHTML = `<span class="mL5 pointer">Set Values <i class="el-icon-edit-outline"></i></span>`
        let icon = td.children[0]
        icon.addEventListener(
          'click',
          enumIconClickListener.bind(this, componentInstance, row),
          { passive: true }
        )
        return td
      }
    }
    return td
  }
  let customSelectRenderer = (
    hotInstance,
    td,
    row,
    column,
    prop,
    value,
    cellProperties
  ) => {
    let { options = [], field = {} } = cellProperties
    let { optionsCache } = field
    let optionsArr = field.options || options || []
    let {
      hotTableData,
      cellsErrorMap,
      hotTableSettingsData,
    } = componentInstance
    let { uniqueDataKey } = hotTableSettingsData || {}
    let physicalRow = hotInstance.toPhysicalRow(row)
    let isValidEntry = true
    let currentRowData = hotTableData[physicalRow]
    let id = !isEmpty(currentRowData) ? currentRowData[uniqueDataKey] : null

    let selectedOption = optionsArr.find(option => option.value === value) || {}
    let selectedLabel = selectedOption.label || ''
    if (!isEmpty(optionsCache) && isEmpty(selectedLabel)) {
      selectedLabel = optionsCache[value] || ''
    }
    if (!isEmpty(value) && isEmpty(selectedLabel)) {
      selectedLabel = `${value}`
    }

    if (!isEmpty(cellsErrorMap) && id) {
      if (!isEmpty(cellsErrorMap[id])) {
        isValidEntry = !cellsErrorMap[id].includes(prop)
      }
    }

    let { suggestedColumns } = currentRowData || []
    if (suggestedColumns) {
      if (suggestedColumns.includes(prop)) {
        td.classList.add('suggestion')
      }
    }

    if (!isValidEntry) {
      td.classList.add('htInvalid')
    }

    let { readOnly } = cellProperties
    if (readOnly) {
      td.innerHTML = `${selectedLabel}<i style="line-height:unset;color:#C0C4CC;float:right;"></i>`
    } else {
      td.innerHTML = `${selectedLabel}<i class="el-select__caret el-input__icon el-icon-arrow-down pointer"style="line-height:unset;color:#C0C4CC;float:right;"></i>`
    }
    let icon = td.children[0]
    icon.addEventListener(
      'click',
      iconClickListener.bind(
        this,
        row,
        column,
        hotInstance,
        cellProperties,
        componentInstance
      ),
      { passive: true }
    )
    return td
  }
  let customDateTimeRenderer = function(
    hotInstance,
    td,
    row,
    column,
    prop,
    value,
    cellProperties
  ) {
    let valueToRender
    let isValidEntry = true
    let {
      hotTableData,
      cellsErrorMap,
      hotTableSettingsData,
    } = componentInstance
    let { uniqueDataKey } = hotTableSettingsData || {}
    let physicalRow = hotInstance.toPhysicalRow(row)
    let currentRowData = hotTableData[physicalRow]
    let id = !isEmpty(currentRowData) ? currentRowData[uniqueDataKey] : null

    if (isNullOrUndefined(value)) {
      valueToRender = ''
    } else {
      if (isValidDateTime(value, true)) {
        valueToRender = getFormattedDateTime(parseDateTime(value, true))
      } else {
        valueToRender = value
      }
    }
    if (!isEmpty(cellsErrorMap) && id) {
      if (!isEmpty(cellsErrorMap[id])) {
        isValidEntry = !cellsErrorMap[id].includes(prop)
      }
    }
    if (!isValidEntry) {
      td.classList.add('htInvalid')
    }

    td.innerHTML = `${valueToRender}<i class="el-input__icon el-icon-time pointer"  style="line-height:unset;color:#C0C4CC;float:right;"></i>`
    let icon = td.children[0]
    icon.addEventListener(
      'click',
      iconClickListener.bind(
        this,
        row,
        column,
        hotInstance,
        cellProperties,
        componentInstance
      ),
      { passive: true }
    )

    return td
  }

  let customLookupRenderer = function(
    hotInstance,
    td,
    row,
    column,
    prop,
    value,
    cellProperties
  ) {
    let displayVal = ''
    let { field } = cellProperties
    let { optionsCache = {} } = field || {}
    if (value) {
      displayVal = optionsCache[value] || value
    }
    let {
      hotTableData,
      cellsErrorMap,
      hotTableSettingsData,
    } = componentInstance
    let { uniqueDataKey } = hotTableSettingsData || {}
    let physicalRow = hotInstance.toPhysicalRow(row)
    let isValidEntry = true
    let currentRowData = hotTableData[physicalRow]
    let id = !isEmpty(currentRowData) ? currentRowData[uniqueDataKey] : null

    let svgContent = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../assets/lookup.svg`)

    let styledSvg = `<svg class="icon icon-sm-md  fc-lookup-icon" style="float:right;"`
    svgContent = (svgContent || {}).default || ''
    svgContent = svgContent.replace(/<svg/, styledSvg)

    td.innerHTML = `${displayVal}${svgContent}`

    if (!isEmpty(cellsErrorMap) && id) {
      if (!isEmpty(cellsErrorMap[id])) {
        isValidEntry = !cellsErrorMap[id].includes(prop)
      }
    }
    if (!isValidEntry) {
      td.classList.add('htInvalid')
    }

    if (!isValidEntry) {
      td.classList.add('htInvalid')
    }

    let { suggestedColumns } = currentRowData || []

    if (suggestedColumns) {
      if (suggestedColumns.includes(prop)) {
        td.classList.add('suggestion')
      }
    }
    let icon = td.children[0]
    icon.addEventListener(
      'click',
      lookupIconClick.bind(
        this,
        row,
        column,
        hotInstance,
        cellProperties,
        componentInstance
      ),
      { passive: true }
    )
    return td
  }

  customRendererHash = {
    select: customSelectRenderer,
    picker: customDateTimeRenderer,
    lookup: customLookupRenderer,
    input: customInputRenderer,
  }

  return customRendererHash[componentName]
}
