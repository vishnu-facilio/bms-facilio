<template>
  <el-cascader
    v-model="fieldPlaceHolder"
    :options="fieldOptions"
    :props="{
      expandTrigger: 'hover',
      value: 'name',
      label: 'displayName',
      children: 'fields',
    }"
    @change="getPlaceholderFromField()"
    class="fc-input-full-border2 input-with-select flex-grow"
    popper-class="sla-duration-field"
  ></el-cascader>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'

const regex = /[\${}]+/gm

/**
placeholder formats:
subform placeholder : ${moduleName.formName.fieldName}
form / parent form placeholder : ${moduleName.fieldName}
**/

export default {
  props: ['value', 'formId', 'isSubForm', 'selectedFormObj', 'parentForm'],
  data() {
    return {
      fieldPlaceHolder: null,
      fieldOptions: [],
    }
  },
  async created() {
    this.init()
  },
  computed: {
    moduleName() {
      return this.$route.params.moduleName
    },
    subFormModuleName() {
      return this.$getProperty(this, 'selectedFormObj.module.name')
    },
  },
  methods: {
    async init() {
      let { isSubForm, selectedFormObj, parentForm } = this || {}
      if (isSubForm) {
        let subForm = this.loadPlaceholderFieldOptions(
          selectedFormObj,
          this.subFormModuleName
        )
        let form = this.loadPlaceholderFieldOptions(parentForm, this.moduleName)
        await Promise.all([subForm, form])
      } else {
        await this.loadPlaceholderFieldOptions(selectedFormObj, this.moduleName)
      }
      this.fieldPlaceHolder = this.getFieldFromPlaceholder()
    },
    async loadPlaceholderFieldOptions(form, moduleName) {
      let { isSubForm } = this || {}
      let { id: formId } = form || {}
      let { data, error } = await API.get(
        `v3/placeholders/${moduleName}?formId=${formId}`
      )

      if (!error) {
        let { placeholders } = data || {}
        let { fields, moduleFields } = placeholders || {}

        if (!isEmpty(fields)) {
          if (!isEmpty(moduleFields)) {
            Object.keys(moduleFields).forEach(fieldName => {
              let lookupField = fields.find(fld => fld.module === fieldName)
              let moduleLookupFields = moduleFields[fieldName]

              //Handling for SiteId --> Changing displayType as 'NUMBER'
              moduleLookupFields = moduleLookupFields.map(field => {
                let { name } = field || {}
                if (name === 'siteId') {
                  return {
                    ...field,
                    displayType: 'NUMBER',
                    dataType: 'NUMBER',
                    primaryField: null,
                  }
                } else {
                  return field
                }
              })

              lookupField.fields = moduleLookupFields
            })
          }
          if (isSubForm) {
            let { name, displayName } = form || {}
            this.fieldOptions = [
              ...this.fieldOptions,
              { name, displayName, fields },
            ]
          } else {
            this.fieldOptions = fields
          }
        }
      }
    },
    getFieldFromPlaceholder() {
      let { value, isSubForm, parentForm, selectedFormObj, fieldOptions } = this
      if (!isEmpty(value)) {
        let placeholderArray = value.replace(regex, '').split('.')
        let fieldsArray = []

        placeholderArray = placeholderArray.filter((_, i) => i !== 0)

        let modifiedPlaceholderArray = cloneDeep(placeholderArray)
        let currentFormName = (selectedFormObj || {}).name
        let isSubFormWithinSubForm = placeholderArray[0] === currentFormName

        // set field options to appropriate fields array
        // based on the value if it is a subform
        if (isSubForm) {
          if (isSubFormWithinSubForm) {
            let currentOption = fieldOptions.find(
              option => option.name === currentFormName
            )
            fieldOptions = (currentOption || {}).fields
            // if subform and the placeholder is from subform
            // then extra form name will be there, have to remove it
            modifiedPlaceholderArray = modifiedPlaceholderArray.filter(
              (_, i) => i !== 0
            )
          } else {
            currentFormName = (parentForm || {}).name
            let currentOption = fieldOptions.find(
              option => option.name === currentFormName
            )
            fieldOptions = (currentOption || {}).fields
          }
        }

        let [fieldName, primaryField] = modifiedPlaceholderArray

        let field = !isEmpty(fieldName)
          ? fieldOptions.find(f => f.name === fieldName)
          : null

        if (!isEmpty(field)) {
          if (primaryField) {
            // Checking if primary field is a lookup field
            let lookupField =
              field.fields?.find(f => f.name === primaryField) || null

            if (!isEmpty(lookupField)) {
              fieldsArray = [fieldName, primaryField]
            } else {
              fieldsArray = [fieldName]
            }
          } else {
            fieldsArray = [fieldName]
          }
        }

        // if rule is for subform, then form name should be
        // added to the cascade array
        if (isSubForm) {
          let { name: parentFormName } = parentForm || {}
          let { name: subFormName } = selectedFormObj || {}
          let formName = isSubFormWithinSubForm ? subFormName : parentFormName
          placeholderArray = [formName, ...fieldsArray]
        } else {
          placeholderArray = fieldsArray
        }

        return placeholderArray
      }
      return null
    },
    getPlaceholderFromField() {
      let { fieldPlaceHolder, isSubForm, selectedFormObj, moduleName } = this
      let placeholderString = null

      if (!isEmpty(fieldPlaceHolder)) {
        let [fieldName, lookupFieldName] = fieldPlaceHolder
        // have to find the first option from cascade can be a field
        // or can be a form based on if rule is for subform
        let { primaryField, fields } = this.fieldOptions.find(
          f => f.name === fieldName
        )

        if (!isEmpty(lookupFieldName)) {
          let { primaryField: lookupPrimaryField } = fields.find(
            f => f.name === lookupFieldName
          )
          !isEmpty(lookupPrimaryField) &&
            fieldPlaceHolder.push(lookupPrimaryField)
        } else {
          !isEmpty(primaryField) && fieldPlaceHolder.push(primaryField)
        }

        let currentModuleName = moduleName
        let placeholders = cloneDeep(fieldPlaceHolder)

        if (isSubForm) {
          let currentForm = fieldPlaceHolder[0]
          let isSubFormField =
            currentForm === this.$getProperty(selectedFormObj, 'name')

          // if placeholders are for subform then form name,
          // along with module name should be added
          if (!isSubFormField) {
            placeholders = placeholders.splice(1, 1)
          }
        }

        placeholders = [...placeholders]

        placeholderString = `\${${currentModuleName}.${placeholders.join('.')}}`
      }
      this.$emit('input', placeholderString)
    },
  },
}
</script>
