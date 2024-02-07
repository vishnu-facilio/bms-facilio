<script>
import { isEmpty } from '@facilio/utils/validation'
import { isMappedMarkerElement, formatDecimal } from '../elements/Common'
const CURRENT_ASSET_FIELD_PREFIX = key => {
  return 'currentAsset.' + key
}
const CURRENT_ASSET_READING_PREFIX = key => {
  return 'currentAsset.reading.' + key
}
export default {
  data() {
    return {
      objectActions: {
        showTrend: {
          enable: true,
        },
        controlAction: {
          enable: false,
          control_list: [
            {
              actionName: null,
              assetId: null,
              assetCategoryId: null,
              fieldId: null,
            },
          ],
        },
        hyperLink: {
          enable: false,
          link_list: [
            {
              actionName: null,
              linkType: null,
              id: null,
              assetId: null,
              url: null,
              target: '_blank',
            },
          ],
        },
        invokeFunction: {
          enable: false,
          function_list: [],
        },
      },
      currentAsset: {
        id: null,
        record: null,
        fields: null,
        readings: null,
      },
    }
  },
  methods: {
    loadVariables() {
      if (this.leagend && this.leagend.assetId) {
        this.variables = []
        this.loadAsset(this.leagend.assetId)
      }
    },
    loadAsset(assetId) {
      if (!assetId) {
        return
      }
      this.variablesLoaded = false
      // this.loadSystemVariables()

      if (!this.currentAsset.fields || !this.currentAsset.fields.length) {
        this.$util.loadFields('asset').then(fields => {
          this.currentAsset.fields = fields
          this.loadAssetSummary(assetId)
        })
      } else {
        this.loadAssetSummary(assetId)
      }
    },
    loadAssetSummary(assetId) {
      this.$http.get('/asset/summary/' + assetId).then(response => {
        this.currentAsset.record = response.data.asset
        this.currentAsset.id = response.data.asset.id
        this.$emit('assetLoaded', response.data.asset)
        this.loadAssetFieldVarialbes()
        this.$util
          .loadAssetReadingFields(-1, response.data.asset.category.id)
          .then(fields => {
            this.currentAsset.readings = fields
            this.loadAssetReadingVariables()
            this.updateValueProps()
            this.loadUsedVariables()
            this.updateVariable()
            // this.updatevalue()
          })
      })
    },
    loadAssetReadingVariables() {
      let variables = []
      if (this.currentAsset.fields && this.currentAsset.fields) {
        for (let field of this.currentAsset.fields) {
          variables.push({
            fetchType: 'field',
            module: field.module.name,
            id: this.currentAsset.record.id,
            parentId: this.currentAsset.record.id,
            select: field.name,
            key: CURRENT_ASSET_FIELD_PREFIX(field.name),
            dataType: field.dataTypeEnum._name,
            label: field.displayName,
            type: 'field',
          })
        }
      }
      if (this.currentAsset.readings && this.currentAsset.readings) {
        for (let reading of this.currentAsset.readings) {
          if (this.$org.id === 210 && this.currentAsset.record.id === 1145442) {
            variables.push({
              fetchType: 'liveValue',
              module: reading.module.name,
              select: reading.name,
              parentId: this.currentAsset.record.id,
              key: CURRENT_ASSET_READING_PREFIX(
                reading.module.name + '_' + reading.name
              ),
              dataType: reading.dataTypeEnum._name,
              label: reading.displayName,
              unit: reading.unit,
              type: 'reading',
            })
          }
          if (
            variables.find(
              v => v.key === CURRENT_ASSET_READING_PREFIX(reading.name)
            ) &&
            reading.default === false
          ) {
            continue
          }
          variables.push({
            fetchType: 'liveValue',
            module: reading.module.name,
            select: reading.name,
            parentId: this.currentAsset.record.id,
            key: CURRENT_ASSET_READING_PREFIX(reading.name),
            dataType: reading.dataTypeEnum._name,
            label: reading.displayName,
            unit: reading.unit,
            type: 'reading',
          })
        }
      }

      this.variables.push(...variables)

      let readings = []
      for (let varObj of this.variables.filter(
        v => v.fetchType === 'liveValue'
      )) {
        readings.push({
          parentId: varObj.parentId + '',
          fieldName: varObj.select,
          moduleName: varObj.module,
        })
      }
    },
    loadAssetFieldVarialbes() {
      let variables = []
      if (this.currentAsset.fields && this.currentAsset.fields) {
        for (let field of this.currentAsset.fields) {
          if (this.currentAsset.record[field.name]) {
            if (typeof this.currentAsset.record[field.name] === 'object') {
              this.liveValues[CURRENT_ASSET_FIELD_PREFIX(field.name)] = {
                value: this.currentAsset.record[field.name].displayName
                  ? this.currentAsset.record[field.name].displayName
                  : this.currentAsset.record[field.name].name,
              }
            } else {
              this.liveValues[CURRENT_ASSET_FIELD_PREFIX(field.name)] = {
                value: this.currentAsset.record[field.name],
              }
            }
          } else {
            this.liveValues[CURRENT_ASSET_FIELD_PREFIX(field.name)] = {
              value: null,
            }
          }
        }
      }

      this.variables.push(...variables)
    },
  },
}
</script>
