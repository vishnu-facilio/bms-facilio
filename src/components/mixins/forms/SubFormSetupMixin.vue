<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { API } from '@facilio/api'
import dlv from 'dlv'

export default {
  computed: {
    checkEditSubFormDataEnabled() {
      return this.$helpers.isLicenseEnabled('DISABLE_EDIT_SUB_FORM_DATA')
    },
  },
  methods: {
    constructSubFormValues(props) {
      let { subForm, subFormValue, isEdit, ruleFieldIds } = props
      subForm = { ...subForm, ruleFieldIds }
      if (!isEmpty(subFormValue)) {
        let { data: dataArr } = subFormValue
        if (!isEmpty(dataArr)) {
          let subFormArr = []
          dataArr.forEach(data => {
            let clonedSubForm = deepCloneObject(subForm) || {}
            let { sections } = clonedSubForm
            if (isEdit) {
              let { id } = data
              clonedSubForm = { ...clonedSubForm, recordId: id }
            }
            let clonedSections = deepCloneObject(sections)
            clonedSections.forEach(section => {
              let { fields } = section
              if (!isEmpty(fields)) {
                fields.forEach(field => {
                  let { name, displayTypeEnum } = field
                  let value = data[name]
                  if (name === 'siteId') {
                    field.value = isEmpty(value) ? null : value.id
                  } else if (displayTypeEnum === 'FILE') {
                    if (isEdit) {
                      let { name: fieldName } = field || {}
                      let name = data[`${fieldName}FileName`]
                      let id = data[`${fieldName}Id`]
                      value = { name, id }
                    }
                    let { name, id } = value || {}
                    if (!isEmpty(name)) {
                      field.fileObj = {
                        name: value.name,
                      }
                    }
                    field.value = id
                  } else if (isObject(value)) {
                    field.value = value.id
                  } else if (!isEmpty(value)) {
                    field.value = value
                  }
                })
              }
            })
            clonedSubForm.sections = clonedSections
            subFormArr.push({ ...(clonedSubForm || {}), moduleData: data })
          })
          return subFormArr
        }
      }
      return [deepCloneObject(subForm)]
    },
    async loadSubFormRecords(section, moduleDataId, currentModuleName) {
      let subFormModuleName = this.$getProperty(section, 'subForm.module.name')
      let lookupFieldName = this.$getProperty(section, 'lookupFieldName')
      let moduleName = currentModuleName || {}
      if (subFormModuleName) {
        if (this.checkEditSubFormDataEnabled) {
          let { list } = await API.fetchAll(subFormModuleName, {
            page: 1,
            perPage: 25,
            filters: JSON.stringify({
              [lookupFieldName]: { operatorId: 5, value: [`${moduleDataId}`] },
            }),
          })
          return list
        } else {
          let url = `v3/modules/${moduleName}/${moduleDataId}/relatedList/${subFormModuleName}/${lookupFieldName}`
          let params = { orderBy: 'id', orderType: 'ASC', subFormRecord: true }
          let { data, error } = await API.get(url, params)
          if (error) {
            this.$message.error(error.message || 'Error occured')
          } else {
            return dlv(data, `${subFormModuleName}`, [])
          }
        }
      }
    },
  },
}
</script>
