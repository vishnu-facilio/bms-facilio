<script>
import { isMappedMarkerElement, formatDecimal } from '../elements/Common'

export default {
  /* eslint-disable array-callback-return */
  data() {
    return {
      markerDisplayValue: {},
      subcriptiondata: [],
    }
  },
  methods: {
    formatValue(value) {
      return formatDecimal(value)
    },
    applyValue(obj, showValue) {
      if ([343, 410].includes(window.orgId)) {
        if (isMappedMarkerElement(obj)) {
          if (window.orgId === 410) {
            this.applyMarkerValue(obj, false, false)
          } else {
            this.applyMarkerValue(obj, true, false)
          }
        }
      } else {
        this.preparearkervalue()
        if (showValue === undefined) {
          showValue = this.leagend.showValue || false
        }
        if (isMappedMarkerElement(obj)) {
          let enable = false
          if (this.viewMode && this.viewMode !== 'control_points') {
            enable = true
          }
          this.applyMarkerValue(obj, showValue, enable)
        }
      }
    },
    applyReadingIconValue() {},
    preparearkervalue() {
      let { spaceControllableCategoriesMap } = this
      let data = {}
      Object.keys(spaceControllableCategoriesMap).forEach(rt => {
        this.$set(data, Number(rt), {})
        Object.values(spaceControllableCategoriesMap[rt]).forEach(rl => {
          let name = this.controlCategoryList.find(
            cat => cat.categoryId === rl.controlType
          )._name
          this.$set(
            data[Number(rt)],
            name,
            this.getMarkerDisplayValue(rl.controllableResourceContexts)
          )
        })
      })
      this.markerDisplayValue = data
    },
    getMarkerDisplayValue(resourceContext) {
      let value = {}
      let unit = ''
      let pointMap = {}
      let subcriptiondataList = []
      resourceContext.forEach(rt => {
        this.$set(value, rt.resource.id, {})
        rt.controllablePoints.forEach(rl => {
          let { field } = rt.controllablePointMap[rl.pointId]
          this.$set(pointMap, rl._name, field.dataType)
          let point = rt.controllablePointMap[rl.pointId]
          let val = ''
          if (field.dataType === 4) {
            val = point.value
          } else {
            val =
              point.childRDM && point.childRDM.value !== null
                ? point.childRDM.value
                : ''
          }
          unit = point.field && point.field.unit ? point.field.unit : ''
          this.$set(value[rt.resource.id], rl._name, {
            [field.dataType]: val,
          })
          subcriptiondataList.push({
            moduleName: field.module.name,
            fieldName: field.name,
            parentId: rt.resource.id,
          })
        })
      })
      this.subcriptiondata = subcriptiondataList
      return this.dataTypeBasedValue(null, value, pointMap, unit)
    },
    dataTypeBasedValue(pointname, value, pointMap, unit) {
      if (pointname === null) {
        pointname = Object.keys(pointMap)[0]
      }
      let result = ''
      let datatype = pointMap[pointname]
      let length = 0
      let pointValue = Object.values(value).map(rt => {
        length++
        if (rt[pointname]) {
          return rt[pointname]
        }
      })
      if (datatype === 4) {
        let count = 0

        pointValue.forEach(rt => {
          if (Number(rt[datatype]) > 0) {
            count++
          }
        })
        result = `ON ${count} / ${length}`
      } else if (datatype === 2 || datatype === 3) {
        let min = null
        let max = null

        pointValue.forEach((rt, index) => {
          let v = Number(rt[datatype])
          if (index === 0) {
            min = v
            max = v
          } else {
            if (min > v) {
              min = v
            } else if (min <= v) {
              max = v
            }
          }
        })
        if (min === max) {
          result = `${this.formatValue(min)} ${unit}`
        } else {
          result = `${this.formatValue(min)} ${unit} ~ ${this.formatValue(
            max
          )} ${unit}`
        }
      }
      return result
    },
    applyMarkerValue(obj, showValue, enable) {
      if (!enable) {
        let formatText = ''

        if ([343, 410].includes(window.orgId)) {
          formatText = showValue ? this.getReadingIconValue(obj) : ''
        } else {
          formatText = showValue ? this.getMarkerValue(obj) : ''
        }
        let textWidth = 0
        let rectWidth = 40
        let totalWidth = 40
        obj.forEachObject(object => {
          if (object.type === 'text') {
            object.set('text', formatText)
            textWidth = object.width
          }
        })

        textWidth = textWidth > 2 ? textWidth : 0
        if (showValue) {
          if (textWidth > 0) {
            obj.forEachObject(object => {
              textWidth += 10
              if (object.type === 'text') {
                if ([343, 410].includes(window.orgId)) {
                  object.set('left', 20)
                } else {
                  object.set('left', 10)
                }
              }
              if (object.type === 'rect') {
                totalWidth = rectWidth + textWidth
                object.set('width', totalWidth)
              }
              if (object.type === 'group') {
                let newWidth = -(totalWidth / 2) + 20
                object.set('left', newWidth)
                if ([343, 410].includes(window.orgId)) {
                  object.set('width', totalWidth)
                }
              }
            })
            let groupwith = 40
            let totalGroupWith = groupwith + textWidth
            obj.set('width', totalGroupWith)
          }
        } else {
          obj.forEachObject(object => {
            if (object.type === 'text') {
              object.set('left', 0)
            }
            if (object.type === 'rect') {
              totalWidth = rectWidth + textWidth
              object.set('width', 40)
            }
            if (object.type === 'group') {
              object.set('left', 0)
            }
          })
          obj.set('width', 40)
        }
      }

      this.canvas.requestRenderAll()
    },
    applyCustomeMarkerValue(obj, value) {
      let { markerType } = obj.floorplan
      let formatText = value
      let textWidth = 0
      let rectWidth = 40
      let totalWidth = 40
      obj.forEachObject(object => {
        if (object.type === 'text') {
          object.set('text', formatText)
          textWidth = object.width
        }
      })

      textWidth = textWidth > 2 ? textWidth : 0
      if (textWidth > 0) {
        obj.forEachObject(object => {
          textWidth += 10
          if (object.type === 'text') {
            object.set('left', 20)
          }
          if (object.type === 'path') {
            if (markerType === 'READINGS') {
              object.set('left', -25)
            } else {
              object.set('left', -10)
            }
          }
          if (object.type === 'rect') {
            totalWidth = rectWidth + textWidth + 10
            object.set('width', totalWidth)
          }
          if (object.type === 'group') {
            let newWidth = -(totalWidth / 2) + 30
            object.set('left', newWidth)
          }
        })
        let groupwith = 40
        let totalGroupWith = groupwith + textWidth
        obj.set('width', totalGroupWith)
      }
    },
    getMarkerValue(obj) {
      let { floorplan } = obj
      let { spaceId } = floorplan
      let { markerType } = floorplan
      let value = this.markerDisplayValue
      return value[spaceId] && value[spaceId][markerType]
        ? value[spaceId][markerType]
        : ''
    },
    getReadingIconValue(obj) {
      let { floorplan } = obj
      let { spaceId, assetId } = floorplan
      if (this.areas && this.areas.length && assetId) {
        let area = this.areas.find(rt => rt.spaceId === spaceId)
        if (area && area.assets.length && area.assets[0][assetId]) {
          let assetValue = area.assets[0][assetId]
          return `${
            assetValue.value ? this.formatValue(assetValue.value) : 0
          } ${assetValue.unit ? assetValue.unit : ''}`
        } else {
          return ``
        }
      }
      return ''
    },
  },
}
</script>
