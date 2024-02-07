import Handsontable from 'handsontable'
import { isEmpty, isNumber } from '@facilio/utils/validation'
import setProperty from 'dset'

export const CustomEditorWrapper = (componentInstance, componentName) => {
  let customEditorHash = {}
  class CustomEditor extends Handsontable.editors.BaseEditor {
    constructor(...args) {
      super(...args)
    }
    init() {}
    prepare(row, col, prop) {
      // Invoke the original method...
      Handsontable.editors.BaseEditor.prototype.prepare.apply(this, arguments)
      componentInstance.$set(componentInstance, 'activeRowIndex', row)
      componentInstance.$set(componentInstance, 'activeColumnIndex', col)
      componentInstance.$set(componentInstance, 'activePropName', prop)
    }
    open() {
      // Display editor components in the cell
      let canShow = componentInstance.getVisibilityKey(componentName)
      componentInstance.$set(componentInstance, 'activeEditorInstance', this)
      componentInstance.$set(componentInstance, canShow, true)
      componentInstance
        .getRefElement(componentInstance, componentName)
        .then(refElement => {
          if (!isEmpty(refElement)) {
            this.refElement = refElement
            this.attachEventListeners()
            this.renderCustomEditor()
          }
        })
    }
    focus() {
      componentInstance
        .getRefElement(componentInstance, componentName)
        .then(refElement => {
          if (!isEmpty(refElement)) {
            refElement.focus()
          }
        })
    }
    getValue() {
      let { activePropName, activeRowIndex, hotTableData } = componentInstance
      let physicalRow = componentInstance.getPhysicalRowIndex(activeRowIndex)
      let currentData = hotTableData[physicalRow]
      let currentValue = componentInstance.$getProperty(
        currentData,
        activePropName,
        null
      )
      return currentValue || ''
    }
    setValue() {}
    close() {
      this.detachEventListeners()
      let canShow = componentInstance.getVisibilityKey(componentName)
      componentInstance.$set(componentInstance, canShow, false)
    }
    attachEventListeners() {
      let scrollableElement = document.getElementsByClassName('wtHolder')[0]
      if (scrollableElement) {
        scrollableElement.addEventListener('scroll', this, { passive: true })
      }
    }
    detachEventListeners() {
      let scrollableElement = document.getElementsByClassName('wtHolder')[0]
      if (scrollableElement) {
        scrollableElement.removeEventListener('scroll', this)
      }
    }
    handleEvent(event) {
      let { type } = event
      switch (type) {
        case 'scroll': {
          this.renderCustomEditor()
          break
        }
      }
    }
    renderCustomEditor() {
      let refElement = this.refElement
      let rootElement = this.instance.rootElement
      let tdElement = document.getElementsByClassName('current')[0]
      let rootElementWidth = Handsontable.dom.outerWidth(rootElement)
      let width = tdElement ? Handsontable.dom.outerWidth(tdElement) : null
      let height = tdElement ? Handsontable.dom.outerHeight(tdElement) : null
      let { xPos, yPos } = this.findElementPosition(tdElement)
      let { xPos: xRootPos, yPos: yRootPos } = this.findElementPosition(
        rootElement
      )
      if (!isEmpty(refElement)) {
        // sets select dimensions to match cell size
        refElement.$el.style.height = `${height}px`
        refElement.$el.style.width = `${width}px`
        // make sure that list positions matches cell position
        refElement.$el.style.top = `${yPos}px`
        refElement.$el.style.left = `${xPos}px`
        refElement.$el.style.margin = '0px'
        // Hide reference element if tdElement is hidden due to scrolling
        if (xPos + width > xRootPos + rootElementWidth || yPos < yRootPos) {
          refElement.$el.classList.add('hide')
        } else {
          refElement.$el.classList.remove('hide')
          if (componentName === 'input') {
            refElement.focus()
          }
        }
      }
    }
    findElementPosition(el) {
      let xPos = 0
      let yPos = 0
      while (el) {
        if (el.tagName == 'BODY') {
          // deal with browser quirks with body/window/document and page scroll
          let xScroll = el.scrollLeft || document.documentElement.scrollLeft
          let yScroll = el.scrollTop || document.documentElement.scrollTop
          xPos += el.offsetLeft - xScroll + el.clientLeft
          yPos += el.offsetTop - yScroll + el.clientTop
        } else {
          // for all other non-BODY elements
          xPos += el.offsetLeft - el.scrollLeft + el.clientLeft
          yPos += el.offsetTop - el.scrollTop + el.clientTop
        }

        el = el.offsetParent
      }
      return { xPos, yPos }
    }
    addExistingOption(props) {
      let { options, currentValue, optionsCache } = props
      let isAlreadyExists = (options || []).find(
        option => option.value === currentValue
      )
      let label = optionsCache[currentValue]
      let value = Number(currentValue)
      if (!isAlreadyExists && !isEmpty(label)) {
        options.unshift({
          label,
          value,
        })
      }
    }
    fetchFromPasteData(props) {
      let { currentValue, row, prop } = props
      let physicalRow = componentInstance.getPhysicalRowIndex(row)
      let { hotTableData, hotTableSettingsData } = componentInstance
      let { uniqueDataKey } = hotTableSettingsData || {}
      let rowData = hotTableData[physicalRow]
      let id = rowData[uniqueDataKey] || {}
      let pasteDataMap = componentInstance.pasteDataMap
      // Replace the string with respective id from pasteData
      if (
        !isEmpty(pasteDataMap) &&
        !isEmpty(componentInstance.pasteDataMap[id]) &&
        !isNumber(currentValue)
      ) {
        let pasteValue = componentInstance.pasteDataMap[id][prop]
        if (!isEmpty(pasteValue)) {
          currentValue = pasteValue
          setProperty(
            componentInstance.hotTableSettingsData.data[physicalRow],
            prop,
            pasteValue
          )
          // Once replaced, remove the prop from pasteData
          delete componentInstance.pasteDataMap[id][prop]
        }
      }
      return currentValue
    }
  }

  class CustomSelectEditor extends CustomEditor {
    open(...args) {
      let { cellProperties, instance, col, row } = this
      let { field, specialOptionsFetch, prop } = cellProperties || {}
      let { optionsCache } = field || {}
      let currentValue = instance.getDataAtCell(row, col)
      currentValue = this.fetchFromPasteData({
        currentValue,
        row,
        prop,
      })
      if (specialOptionsFetch) {
        componentInstance.getSelectOptionsForColumn({ row, col }).then(data => {
          let [options] = data
          field.options = options
        })
        if (!isEmpty(currentValue)) {
          this.addExistingOption({
            options: field.options,
            currentValue,
            optionsCache,
          })
        }
      }
      super.open(...args)
    }
    close(...args) {
      let { cellProperties } = this
      let { field, specialOptionsFetch } = cellProperties || {}
      if (specialOptionsFetch) {
        field.options = []
      }
      super.close(...args)
    }
  }
  class CustomLookupEditor extends CustomEditor {
    constructor(...args) {
      super(...args)
    }
    open(...args) {
      let { cellProperties, instance, col, row } = this
      let { field, prop } = cellProperties || {}
      let { optionsCache } = field || {}
      let currentValue = instance.getDataAtCell(row, col)
      currentValue = this.fetchFromPasteData({
        currentValue,
        row,
        prop,
      })
      if (!isEmpty(currentValue)) {
        this.addExistingOption({
          options: field.options,
          currentValue,
          optionsCache,
        })
      }
      super.open(...args)
    }
    close(...args) {
      let { cellProperties } = this
      let { field, specialOptionsFetch } = cellProperties || {}
      if (specialOptionsFetch) {
        field.options = []
      }
      super.close(...args)
    }
  }

  class CustomPicker extends CustomEditor {}

  customEditorHash = {
    input: CustomEditor,
    select: CustomSelectEditor,
    picker: CustomPicker,
    lookup: CustomLookupEditor,
  }
  return customEditorHash[componentName]
}
