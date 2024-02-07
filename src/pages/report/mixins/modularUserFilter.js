import { mapGetters } from 'vuex'
import { getFieldOptions } from 'util/picklist'

export default {
  computed: {
    ...mapGetters(['getTicketStatusPickList']),
  },
  methods: {
    loadAllValues(conf) {
      let existingConfig = conf['userFilters']
      let self = this
      this.$http('/module/meta?moduleName=' + this.moduleName).then(
        response => {
          let fields = response.data.meta.fields
          let promises = []
          for (let config of existingConfig) {
            config['allValues'] = []
            let field = fields.filter(
              field => field.fieldId === config.fieldId && field.dataType === 7
            )
            if (field.length !== 0) {
              let lookupModuleName = field[0].lookupModule.name
              if (lookupModuleName === 'ticketstatus') {
                let statusObj = this.getTicketStatusPickList(this.moduleName)
                for (let key of Object.keys(statusObj)) {
                  let temp = {}
                  temp[parseInt(key)] = statusObj[key]
                  config.allValues.push(temp)
                }
                continue
              }
              config.lookupModuleName = lookupModuleName
              let picklist = getFieldOptions({
                field: { lookupModuleName, skipDeserialize: true },
              }).then(({ error, options }) => {
                if (error) {
                  this.$message.error(error.message || 'Error Occured')
                } else {
                  for (let key of Object.keys(options)) {
                    let temp = {}
                    temp[parseInt(key)] = options[key]
                    config.allValues.push(temp)
                  }
                }
              })
              promises.push(picklist)
            }
          }
          this.metaField = response.data.meta.fields
          Promise.all(promises).then(() => {
            if (
              this.config &&
              this.config.dimension &&
              this.config.dimension.dimension === null
            ) {
              conf['userFilters'] = existingConfig
              this.config = conf
              this.currentUserFilter = this.config.userFilters[0]
              this.build(this.config, this.moduleResourceField)
            } else if (
              this.hasOwnProperty('userFilters') &&
              this.userFilters === null
            ) {
              this.userFilters = existingConfig
            }
          })
        }
      )
    },
    getDefaultSettingsForField(facilioField, moduleName) {
      let setting = null
      setting = this.$helpers.cloneObject(
        this.getDefaultFilterSettings(
          facilioField.dataTypeEnum.typeAsString.toUpperCase()
        )
      )
      if (
        facilioField?.dataTypeEnum?.typeAsString?.toLowerCase() === 'lookup'
      ) {
        setting.lookupModuleName = facilioField.lookupModule.name
      }
      setting.name = facilioField.displayName
      setting.fieldId = facilioField.fieldId
      if (facilioField.dataType === 7 || facilioField.dataType === 8) {
        this.loadDefaultValues(facilioField, setting, moduleName)
      } else {
        this.fieldConfigs[facilioField.fieldId] = setting
      }
    },
    getDefaultFilterSettings(dataType) {
      return this.defaultSettings[dataType]
    },
    getComponent(componentType) {
      return this.componentType[componentType].element
    },
    loadDefaultValues(facilioField, settings, moduleName) {
      let lookupModuleName = facilioField.lookupModule.name
      this.$set(this.fieldConfigs, facilioField.fieldId, settings)
      if (lookupModuleName === 'ticketstatus') {
        let statusObj = this.getTicketStatusPickList(moduleName)
        for (let key of Object.keys(statusObj)) {
          let temp = {}
          temp[parseInt(key)] = statusObj[key]
          settings.allValues.push(temp)
        }
        this.fieldConfigs[facilioField.fieldId].allValues = settings.allValues
        return
      }
      getFieldOptions({
        field: { lookupModuleName, skipDeserialize: true },
      }).then(({ error, options }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          for (let key of Object.keys(options)) {
            let temp = {}
            temp[parseInt(key)] = options[key]
            settings.allValues.push(temp)
          }
          this.fieldConfigs[facilioField.fieldId].allValues = settings.allValues
        }
      })
    },
  },
  data() {
    return {
      dataStructureForComponent: {
        1: null,
        2: [],
        3: null,
      },
      componentType: {
        1: {
          label: 'singleselect',
          displayLabel: 'Single Select',
          element: 'el-select',
        },
        2: {
          label: 'multipleselect',
          displayLabel: 'Multiple Select',
          element: 'el-select',
        },
        3: {
          label: 'inputbox',
          displayLabel: 'Input box',
          element: 'el-input',
        },
      },
      type: {
        all: 1,
        any: 2,
      },
      defaultSettings: {
        ENUM: {
          fieldId: null,
          dataType: 8,
          chooseValue: {
            type: 1,
            values: [],
            otherEnabled: false,
          },
          name: null,
          component: {
            componentType: 1,
            availableComponents: [1, 2],
            dataType: 8,
          },
          defaultValues: [],
          values: [],
          allValues: [],
        },
        LOOKUP: {
          fieldId: null,
          dataType: 7,
          chooseValue: {
            type: 1,
            values: [],
            otherEnabled: false,
          },
          name: null,
          component: {
            componentType: 1,
            availableComponents: [1, 2],
            dataType: 7,
          },
          defaultValues: [],
          values: [],
          allValues: [],
        },
        DATE: {
          fieldId: null,
          chooseValue: {
            type: 1,
            values: [],
            otherEnabled: false,
          },
          component: {
            componentType: 1,
            availableComponents: [1, 2],
            dataType: 5,
          },
          dataType: 5,
          name: null,
          defaultValues: [],
          values: [],
          allValues: [],
        },
        DATETIME: {
          fieldId: null,
          chooseValue: {
            type: 1,
            values: [],
            otherEnabled: false,
          },
          component: {
            componentType: 1,
            availableComponents: [1, 2],
            dataType: 6,
          },
          dataType: 6,
          name: null,
          defaultValues: [],
          values: [],
          allValues: [],
        },
        // STRING:{
        //   fieldId: null,
        //   allOrAny: 1,
        //   dataType: 1,
        //   displayName: null,
        //   componentType: 'inputbox',
        //   availableComponents:[3],
        //   defaultValue:[],
        //   values:[]
        // }
      },
    }
  },
}
