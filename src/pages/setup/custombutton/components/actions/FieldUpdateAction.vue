<template>
  <div>
    <ActionConfiguredHelper
      :isActionConfigured="isUpdateFieldsConfigured"
      helperText="Update Fields after button execution"
      @edit="editAction()"
      @reset="reset(fieldContext)"
    />

    <field-update-dialog
      v-if="showFieldDialog"
      :statusFieldName="statusFieldName"
      :fieldChange="fieldChange"
      :moduleFields="moduleFields"
      :module="module"
      :picklistOptions="picklistOptions"
      :addRow="addRow"
      :actionSave="actionSave"
      :fieldUpdateValue.sync="showFieldDialog"
    ></field-update-dialog>
  </div>
</template>

<script>
import { actionsHash } from './actionHash'
import FieldUpdateDialog from '@/fields/FieldUpdateDialog'
import { isEmpty } from '@facilio/utils/validation'
import ActionConfiguredHelper from './ActionConfiguredHelper'
import { getFieldOptions } from 'util/picklist'
import { mapState, mapGetters } from 'vuex'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: [
    'moduleFields',
    'module',
    'showFieldUpdateDialog',
    'existingAction',
    'actionType',
  ],
  components: { FieldUpdateDialog, ActionConfiguredHelper },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketStatus', this.module)
    this.$store.dispatch('loadApprovalStatus')
  },
  data() {
    return {
      isUpdateFieldsConfigured: false,
      actionHash: actionsHash,
      fieldContext: '',
      picklistOptions: {},
      fieldChange: {
        actionType: 13,
        templateJson: {
          fieldMatcher: [],
        },
      },
      showFieldDialog: this.showFieldUpdateDialog,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      assetCategory: state => state.assetCategory,
      ticketpriority: state => state.ticketPriority,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getTicketStatusPickList']),
  },
  watch: {
    showFieldUpdateDialog: {
      handler: function(newVal) {
        this.showFieldDialog = newVal
      },
    },
    showFieldDialog: function() {
      if (!this.showFieldDialog) {
        this.$emit('update:showFieldUpdateDialog', false)
      }
    },

    moduleFields() {
      if (this.existingAction) {
        this.deserialize()
      }
    },
    actionType(newVal) {
      if (newVal === actionsHash.FIELD_UPDATE.name) {
        this.addRow()
      }
    },
  },
  methods: {
    deserialize() {
      let actions = this.existingAction
      if (!isEmpty(actions)) {
        actions.forEach(action => {
          if (parseInt(action.actionType) === 13) {
            this.isUpdateFieldsConfigured = true

            let index = 0
            if (!isEmpty(action.template)) {
              let originalTemplate = action.template.originalTemplate

              for (let key in originalTemplate) {
                this.addRow()
                if (originalTemplate.hasOwnProperty(key) && !isEmpty(key)) {
                  let fieldMatcher = this.fieldChange.templateJson.fieldMatcher[
                    index
                  ]
                  let field = this.moduleFields.filter(
                    field => field.name === key
                  )

                  !isEmpty(field) &&
                    this.setFieldMatcher(
                      fieldMatcher,
                      field,
                      key,
                      originalTemplate
                    )
                  this.$emit('setProperties', this.fieldChange)
                  this.statusFieldName(fieldMatcher, index)

                  index++
                }
              }
              this.fieldContext = action
            }
          }
        })
      }
    },
    setFieldMatcher(fieldMatcher, field, key, originalTemplate) {
      fieldMatcher.field = key
      fieldMatcher.value = originalTemplate[key]

      if (field[0].dataTypeEnum._name === 'LOOKUP') {
        if (isEmpty(originalTemplate[key].id)) {
          fieldMatcher.valueArray = originalTemplate[key]
          fieldMatcher.value = originalTemplate[key]
        } else {
          fieldMatcher.valueArray = originalTemplate[key].id
          fieldMatcher.value.id = originalTemplate[key].id
        }
      } else if (
        field[0].dataTypeEnum._name === 'DATE_TIME' ||
        field[0].dataTypeEnum._name === 'DATE'
      ) {
        fieldMatcher.dateObject = this.$helpers.secTodaysHoursMinu(
          originalTemplate[key] / 1000
        )
      }
      fieldMatcher.fieldObj = field
    },
    addRow() {
      this.$getProperty(this.fieldChange, 'templateJson.fieldMatcher', []).push(
        {
          field: '',
          isSpacePicker: false,
          value: null,
          parseLabel: null,
          valueArray: null,
          dateObject: {},
          fieldObj: null,
        }
      )
    },
    async loadPickList(moduleName, fieldName) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.picklistOptions, fieldName, options)
      }
    },
    loadSpecialTypePickList(specialType, fieldName) {
      let pickOption = {}
      if (specialType === 'users') {
        let { users: userList } = this || {}
        pickOption['$' + '{LOGGED_USER}'] = 'Current User'
        userList.forEach(user => {
          let { id, name } = user || {}
          pickOption[id] = name
        })
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'groups') {
        let { groups: groupList } = this || {}

        groupList.forEach(group => {
          let { groupId, name } = group || {}
          pickOption[groupId] = name
        })
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'basespace') {
        let { spaces: spaceList } = this
        spaceList.forEach(space => {
          let { id, name } = space || {}
          pickOption[id] = name
        })
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'alarmType') {
        this.$set(
          this.picklistOptions,
          fieldName,
          this.$constants.AlarmCategory
        )
      } else if (specialType === 'sourceType') {
        this.$set(this.picklistOptions, fieldName, this.$constants.SourceType)
      }
    },
    statusFieldName(selectedField, index) {
      let field = this.moduleFields.filter(
        field => field.name === selectedField.field
      )
      let fieldMatcher = this.fieldChange.templateJson.fieldMatcher[index]

      fieldMatcher.fieldObj = field
      fieldMatcher.isSpacePicker = false
      if (!isEmpty(field)) {
        fieldMatcher.columnName = field[0].completeColumnName
        if (field[0].dataTypeEnum._name === 'ENUM') {
          this.$set(this.picklistOptions, field[0].name, field[0].enumMap)
        }
        if (field[0].dataTypeEnum._name === 'LOOKUP' && field[0].specialType) {
          this.loadSpecialTypePickList(field[0].specialType, field[0].name)
        } else if (
          field[0].dataTypeEnum._name === 'LOOKUP' &&
          field[0].lookupModule
        ) {
          if (field[0].lookupModule.name === 'ticketstatus') {
            this.$set(
              this.picklistOptions,
              field[0].name,
              this.getTicketStatusPickList(this.module)
            )
          } else if (field[0].lookupModule.name === 'ticketpriority') {
            // handling key value pair to match existing flow pattern
            let priority = {}
            this.ticketpriority.forEach(d => {
              priority[d.id] = d.displayName
            })
            this.$set(this.picklistOptions, field[0].name, priority)
          } else {
            this.loadPickList(field[0].lookupModule.name, field[0].name)
          }
        }
      }
    },

    actionSave(data) {
      data = this.formatFieldUpdateValue(data)
      this.$emit('update:actionType', actionsHash.FIELD_UPDATE.name)
      this.isUpdateFieldsConfigured = true
      this.showFieldDialog = false
      this.fieldContext = data
      this.$emit('setProperties', data)
    },

    editAction() {
      this.isUpdateFieldsConfigured = true
      this.showFieldDialog = true
      this.$forceUpdate()
    },
    reset(action) {
      this.isUpdateFieldsConfigured = false
      this.$emit('resetAction', action)
    },
    formatFieldUpdateValue(data) {
      let fieldMatcher = this.$getProperty(data, 'templateJson.fieldMatcher')
      if (!isEmpty(fieldMatcher)) {
        let removeEmptyFields = fieldMatcher.filter(
          fieldItem => !isEmpty(fieldItem.field) && !isEmpty(fieldItem.value)
        )
        data.templateJson.fieldMatcher = removeEmptyFields
      }
      return data
    },
  },
}
</script>

<style></style>
