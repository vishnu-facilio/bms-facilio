<template>
  <div>
    <div class="overflow-scroll f-subform-wrapper">
      <div v-if="isLoading">
        <div class="subform-wrapper-loaders">
          <div class="subform-header-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
          <div class="subform-header-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
          <div class="subform-header-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
        </div>
        <div v-for="load in [1, 2]" :key="load" class="subform-wrapper-loaders">
          <div class="subform-child-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
          <div class="subform-child-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
          <div class="subform-child-loader">
            <FieldLoader :isLoading="isLoading" />
          </div>
        </div>
      </div>
      <template v-else>
        <div
          v-for="(subForm, subFormIndex) in subForms"
          :key="`${subFormIndex} ${subForm.id}`"
        >
          <f-sub-form
            :ref="`subform ${subFormIndex} ${subForm.id}`"
            :key="`subform ${subFormIndex} ${subForm.id}`"
            :form="subForm"
            :formIndex="subFormIndex"
            :isLiveSubForm="true"
            :isSaving="isSaving"
            :isFirstofType="subFormIndex === 0"
            :isLastofType="subFormIndex === subForms.length - 1"
            :canShowRemove="subForms.length !== 1"
            :isV3Api="isV3Api"
            :parentSiteId="siteId"
            :subFormModuleData="subFormsArr[subFormIndex].moduleData"
            :isEdit="isEdit"
            @removeLineItem="removeSubFormSection"
            @subFormDirtyChange="subFormDirtyChange"
            @onModelChange="
              ({ field, formModel }) =>
                onModelChange({ field, formModel, subFormIndex })
            "
          ></f-sub-form>
        </div>
      </template>
    </div>
    <div class="add-btn-container mT15">
      <el-button class="add-lineitem-btn" @click="addSubFormSection"
        >Add Item</el-button
      >
    </div>
  </div>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty, isObject, areValuesEmpty } from '@facilio/utils/validation'
import DataCreationMixin from '@/mixins/DataCreationMixin'
import FieldLoader from '@/forms/FieldLoader'

export default {
  mixins: [DataCreationMixin],
  components: {
    FSubForm: () => import('./FSubForm.vue'),
    FieldLoader,
  },
  props: [
    'subFormsArr',
    'isSaving',
    'siteId',
    'initialSubForm',
    'isV3Api',
    'isEdit',
    'isLoading',
  ],
  data() {
    return {
      dirtyRecordIds: [],
      deletedRecordIds: [],
      subFormModels: [],
      triggerOnLoad: true,
    }
  },
  computed: {
    subForms: {
      get() {
        return this.subFormsArr
      },
      set(value) {
        this.$emit('update:subFormsArr', value)
      },
    },
    ruleFieldIds() {
      return this.$getProperty(this, 'subFormsArr.0.ruleFieldIds', [])
    },
  },
  methods: {
    subFormDirtyChange(value) {
      // Records that are actually changed are tracked here, so that on edit only
      // those records are sent and the sane can be used for validation,
      // only interacted records should undergo validation
      let { dirtyRecordIds } = this
      let currDirtyRecord = dirtyRecordIds.find(id => id === value.id)
      if (isEmpty(currDirtyRecord) && !isEmpty(value.id))
        this.dirtyRecordIds.push(value.id)
    },
    onModelChange(props) {
      let { formModel, field, subFormIndex } = props
      let { subFormModels, subForms, triggerOnLoad } = this
      let { length } = subFormModels || {}
      // If the change is on an existing subform change that model alone
      // if not we push the new model
      if (length === 0 || length - 1 < subFormIndex) {
        subFormModels.push(formModel)
      } else {
        subFormModels = subFormModels.map((model, index) => {
          if (index === subFormIndex) return formModel
          else return model
        })
      }
      this.subFormModels = subFormModels
      // We need to call on load rules for edit case
      let triggerAddDeleteRules = false
      if (
        isEmpty(field) &&
        (subForms || []).length === (subFormModels || []).length
      ) {
        triggerAddDeleteRules = true
      }
      // We need to track the model that is changed, so that we
      // send that to execute form rules api, therefore rule will be
      // applied only for the changed model
      let modifiedSubFormModel = subFormModels.map((model, index) => {
        if (index === subFormIndex)
          return { ...formModel, sub_form_action: 'edit' }
        else return model
      })
      this.$emit('onSubFormModelChange', {
        model: modifiedSubFormModel,
        subFormName: subForms[0].name,
        field,
        triggerAddDeleteRules,
        params: { subFormId: subForms[0].id },
        triggerOnLoad,
      })
      if (triggerOnLoad) this.triggerOnLoad = false
    },
    addSubFormSection() {
      let { subForms, initialSubForm, ruleFieldIds } = this
      if (subForms.length > 24) {
        this.$message.error('Subform records should not be greater than 25')
      } else {
        let formObj = cloneDeep(initialSubForm)
        if (!isEmpty(formObj)) {
          formObj = { ...formObj, ruleFieldIds }
          let { sections } = formObj
          if (!isEmpty(sections)) {
            sections.forEach(section => {
              let { fields } = section
              if (!isEmpty(fields)) {
                fields.forEach(field => {
                  if (
                    field.displayTypeEnum === 'FILE' &&
                    !isEmpty(field.fileObj)
                  ) {
                    field.fileObj = null
                  }
                })
              }
            })
          }
          this.$set(formObj, 'sections', sections)
          subForms.push(formObj)
        }
        this.$set(this, 'subForms', subForms)
      }
    },
    removeSubFormSection(index) {
      let { subForms, isEdit, subFormModels } = this
      let currSubForm = subForms[index] || {}

      let recordId = this.$getProperty(currSubForm, 'recordId')

      if (!isEmpty(recordId) && isEdit) {
        this.deletedRecordIds.push(recordId)
      }
      subForms.splice(index, 1)
      this.$set(this, 'subForms', subForms)
      let modifiedSubFormModel = cloneDeep(subFormModels)
      subFormModels.splice(index, 1)
      this.$set(this, 'subFormModels', subFormModels)

      // Deleted record should be denoted to the server hence that subform,
      // eventhough it is deleted, we should add sub_form_action prop as delete
      modifiedSubFormModel = modifiedSubFormModel.map((model, currIndex) => {
        if (currIndex === index) return { sub_form_action: 'delete' }
        else return model
      })

      this.$emit('onSubFormModelChange', {
        model: modifiedSubFormModel,
        subFormName: subForms[0].name,
        field: {},
        triggerAddDeleteRules: true,
        params: { subFormId: subForms[0].id },
      })
    },
    executeSubFormRules(subFormsRulesList) {
      // Here we iterate the rules array which will have length same as the
      // number of sub form records.
      let { subForms, $refs } = this
      subFormsRulesList.forEach((subFormRules, index) => {
        // Here we access each sub form component and call form rules handler
        // for all the actions using refs.
        let currentSubForm = subForms[index]
        let { id: formId } = currentSubForm || {}
        let subFormElement = $refs[`subform ${index} ${formId}`]
        if (!isEmpty(subFormElement)) {
          subFormElement[0].formRulesHandler((subFormRules || {}).actions)
        }
      })
    },
    serializeSubFormData(formObj, formModel) {
      let { siteId, isFileTypeFields } = this
      let { sections, id } = formObj
      let finalObj = {
        datum: [],
      }
      if (!isEmpty(sections)) {
        sections.forEach(section => {
          let { fields } = section
          if (!isEmpty(fields)) {
            fields.forEach(field => {
              let { name, field: fieldObj, displayTypeEnum } = field
              let { dataType } = fieldObj || {}
              let value = formModel[name]
              if (name !== 'siteId') {
                let dataObj = {
                  name,
                  value,
                }

                if (isFileTypeFields.includes(displayTypeEnum)) {
                  dataObj.name = `${name}Id`
                } else if (
                  isObject(value) &&
                  !isEmpty(value.id) &&
                  name !== 'siteId' &&
                  (dataType !== 7 || (fieldObj && !fieldObj.default))
                ) {
                  dataObj.value = value.id
                }

                if (isObject(dataObj.value)) {
                  if (!areValuesEmpty(dataObj.value)) {
                    dataObj.valueMap = dataObj.value
                    delete dataObj.value
                    finalObj.datum.push(dataObj)
                  }
                } else if (!isEmpty(dataObj.value)) {
                  dataObj.value = String(dataObj.value)
                  finalObj.datum.push(dataObj)
                }
              }
            })
          }
        })
      }
      finalObj.datum.push({
        name: 'formId',
        value: id,
      })
      if (!isEmpty(siteId)) {
        finalObj.datum.push({
          name: 'siteId',
          value: siteId,
        })
      }
      return finalObj
    },
    saveRecord(props = {}) {
      let { skipDeserialize = false, skipSubFormValidation = false } = props
      let {
        subForms,
        $refs,
        isV3Api,
        siteId,
        dirtyRecordIds,
        isEdit,
        deletedRecordIds,
        initialSubForm,
      } = this
      let { module } = subForms[0] || {}
      let lookupFieldId = this.$getProperty(
        initialSubForm,
        'sections.0.lookupFieldId'
      )
      let { name: moduleName } = module || {}
      let promises = []
      let subFormData = {
        moduleName,
        data: [],
      }
      let subFormDataV3Format = {
        [moduleName]: [
          {
            fieldId: lookupFieldId,
            data: [],
            deleteIds: [...deletedRecordIds],
          },
        ],
      }

      if (!isEmpty(subForms)) {
        subForms.forEach((subForm, index) => {
          let { id } = subForm
          let subFormElement = $refs[`subform ${index} ${id}`]
          if (!isEmpty(subForm) && !isEmpty(subFormElement)) {
            promises.push(
              subFormElement[0]
                .saveRecord({ skipSubFormValidation })
                .then(formData => {
                  if (!areValuesEmpty(formData)) {
                    let serializedSubFormData = formData
                    if (isV3Api) {
                      let serializedData = this.serializedData(
                        subForm,
                        formData
                      )
                      serializedData.formId = subForm.id
                      if (!isEmpty(siteId)) {
                        serializedData.siteId = siteId
                      }
                      if (isEdit) {
                        let isDirty = dirtyRecordIds.includes(subForm.recordId)
                        if (isDirty || isEmpty(subForm.recordId)) {
                          subFormDataV3Format[moduleName][0].data.push({
                            ...serializedData,
                            id: subForm.recordId,
                          })
                        }
                      } else {
                        subFormDataV3Format[moduleName][0].data.push(
                          serializedData
                        )
                      }
                    } else {
                      if (!skipDeserialize) {
                        serializedSubFormData = this.serializeSubFormData(
                          subForm,
                          formData
                        )
                      }
                      subFormData.data.push(serializedSubFormData)
                    }
                  }
                })
                .catch(() => {
                  throw new Error()
                })
            )
          }
        })
      }

      return new Promise((resolve, reject) => {
        Promise.all(promises)
          .then(() => {
            let data = isV3Api ? subFormDataV3Format : subFormData
            resolve(data)
          })
          .catch(() => {
            reject()
          })
      })
    },
  },
}
</script>
<style scoped lang="scss">
.width720 {
  max-width: 720px;
}
.subform-wrapper-loaders {
  display: flex;
}
.subform-child-loader {
  width: 100%;
  padding: 10px;
  border: 0.6px solid #ededed;
  border-top: none;
  .field-loading {
    height: 33px;
  }
}
.subform-header-loader {
  width: 100%;
  border: 0.6px solid #ededed;
  padding: 15px 10px !important;
  background: #f7f9fb;
  border-bottom: none;
}
</style>
