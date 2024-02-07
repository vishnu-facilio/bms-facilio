<script>
import $helpers from 'util/helpers'
import { cloneArray, deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import { serializeProps } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'
import http from 'util/http'

const DELAY = 5000
const windowMessage = {
  action: 'setup-form-close',
}

export default {
  mounted() {
    this.debounceSaveRecord = $helpers.debounce((props = {}) => {
      this.saveFormRecord(props)
    }, DELAY)
  },
  methods: {
    loadUnusedFields(moduleName, formId) {
      let fetchUnusedFieldsUrl = `/v2/forms/fields/unusedlist?moduleName=${moduleName}&formId=${formId}`
      return http
        .get(fetchUnusedFieldsUrl)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { fields } = result
            return fields
          } else {
            throw new Error(message)
          }
        })
        .catch(err => {
          throw new Error(err)
        })
    },
    findSelectedField(sections, element) {
      return sections
        .reduce((res, { fields }) => [...res, ...fields], [])
        .find(field => field.name === element.name)
    },
    isFieldActive({ name, formId }) {
      let { activeField } = this
      if (!isEmpty(activeField)) {
        let { formId: activeFieldFormId } = activeField
        return (
          `${activeFieldFormId}${(activeField || {}).name}` ===
          `${formId}${name}`
        )
      }
      return false
    },
    getIconSrc(name) {
      let svgName = Constants.FIELDS_ICON_HASH[name] || 'single-line'
      return `${svgName}`
    },
    deserializeSectionData(sections, skipEmptySectionHandling = false) {
      if (!skipEmptySectionHandling) {
        let sectionsLength = sections.length + 1
        // Pushing empty section object, to support adding new section.
        sections.push({
          name: `section_${sectionsLength}`,
          id: -1,
          showLabel: false,
          fields: [],
        })
      }
      return sections.map(section => {
        let { subFormId, subForm } = section
        let subFormsArr = []
        if (!isEmpty(subFormId)) {
          subFormsArr = [subForm]
          this.$set(section, 'subFormsArr', subFormsArr)
        }
        if (!isEmpty(section.fields)) {
          section.fields = this.deserializeData(section.fields, sections)
        } else {
          section.fields = []
        }
        return section
      })
    },
    saveFormRecord({
      redirect = false,
      subFormObj = {},
      skipSettingSave = false,
    }) {
      let { sections, formProperties: form = {}, hasError, $route } = this
      let isSetupPage = this.$getProperty($route.query, 'setup', false)

      let formObj = isEmpty(subFormObj) ? form : subFormObj
      let _form = this.serializeFormData(formObj)
      let _sections = isEmpty(subFormObj)
        ? cloneArray(sections)
        : cloneArray(subFormObj.sections)
      let data = {
        form: _form,
      }
      data.form.sections = this.serializeData(_sections, !isEmpty(subFormObj))
      if (!hasError) {
        if (!skipSettingSave) {
          this.isSaving = true
        }
        return http
          .post('/v2/forms/fields/init', data)
          .then(({ data: { message, responseCode, result = {} } }) => {
            if (responseCode === 0) {
              if (!skipSettingSave) {
                this.isSaving = false
              }
              if (redirect) {
                if (isSetupPage) {
                  this.sendMessage()
                }
                this.redirectToFormsList()
              } else {
                return this.deserializeFormData(result)
              }
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
            if (!skipSettingSave) {
              this.isSaving = true
            }
          })
      }
    },
    sendMessage() {
      let origin = window.location.origin
      let stringifiedMessage = JSON.stringify(windowMessage)
      window.parent.postMessage(stringifiedMessage, origin)
    },
    serializeFormData(form = {}) {
      let formObj = deepCloneObject(form)
      let labelPosition = ''
      let labelAlignmentHash = Constants.FORMS_LABELALIGNMENT_ENUMHASH
      Object.entries(labelAlignmentHash).forEach(([key, value]) => {
        if (formObj.labelPosition === value) {
          labelPosition = Number(key)
        }
      })
      if (typeof (formObj || {}).labelPosition !== 'number') {
        formObj.labelPosition = labelPosition
      }
      return formObj
    },
    serializeData(sections = [], skipEmptyRow) {
      // To remove the empty row, which is added manually to support drag
      if (!skipEmptyRow) {
        let sectionLastIndex = sections.length - 1
        sections.splice(sectionLastIndex, 1)
      }
      sections = sections.map(section => {
        let _section = deepCloneObject(section)
        let { subFormId } = _section
        if (!isEmpty(subFormId)) {
          _section = this.serializeSubFormSection(_section)
        } else {
          _section.fields = _section.fields.reduce((result, field) => {
            let _field = deepCloneObject(field)
            result.push(this.serializeFields(_field))
            return result
          }, [])
        }
        return _section
      })
      return sections
    },
    serializeSubFormSection(formSection = []) {
      let { subForm } = formSection || {}

      let _subForm = deepCloneObject(subForm)
      delete _subForm.fields

      let { sections: subFormSections = [] } = _subForm || {}
      subFormSections = subFormSections.map(section => {
        let { fields } = section || {}
        fields = fields.map(field => {
          return this.serializeFields(field)
        })
        return { ...section, fields }
      })
      let serializedSection = {
        ...formSection,
        subForm: { ..._subForm, sections: subFormSections },
      }
      delete serializedSection.subFormsArr
      return serializedSection
    },
    serializeFields(field = {}, resourceProps) {
      let { currentSiteId } = this
      let { name } = field || {}
      let resourceProperties = isEmpty(resourceProps)
        ? Constants.FIELDS_RESOURCE_PROPS
        : resourceProps
      let serializedField = serializeProps(field, resourceProperties)
      // Have to stringfy the value before sending it to the server
      let skipStringify =
        field.value === null || typeof field.value === 'string'
      serializedField.value = skipStringify
        ? field.value
        : JSON.stringify(field.value)
      // Skip site field value update, if global site is not 'All'
      if (name === 'siteId' && !isEmpty(currentSiteId)) {
        delete serializedField['value']
      }
      return serializedField
    },
  },
}
</script>
