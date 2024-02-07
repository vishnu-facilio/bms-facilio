<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
    >
      <div v-if="!isLoading" class="fc-pm-main-content-H">
        {{ formDisplayName }}
      </div>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDataId="moduleDataId"
        :moduleDisplayName="formDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="!isNew"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'SpaceForm',
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: [
    'visibility',
    'isNew',
    'building',
    'floor',
    'site',
    'spaceobj',
    'spaceParent',
    'isSummaryEdit',
  ],
  data() {
    return {
      forms: [],
      isSaving: false,
    }
  },
  computed: {
    formDisplayName() {
      return this.formObj?.displayName || 'Space'
    },
    moduleDataId() {
      return this.spaceobj?.id
    },
    moduleName() {
      return 'space'
    },
    moduleDisplayName() {
      return this.formObj?.module?.displayName || 'Space'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    async loadFormsList(moduleName) {
      let url = `/v2/forms?moduleName=${moduleName}`

      this.isLoading = true
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let selectedForms = []
        let forms = data.forms
        if (this.spaceParent && this.spaceParent.id) {
          selectedForms = forms.filter(form =>
            form.name.includes('default_space_web_space')
          )
        } else if (this.floor && this.floor.id) {
          selectedForms = forms.filter(form =>
            form.name.includes('default_space_web_floor')
          )
        } else if (this.building && this.building.id) {
          selectedForms = forms.filter(form =>
            form.name.includes('default_space_web_building')
          )
        } else if (this.site && this.site.id) {
          selectedForms = forms.filter(form =>
            form.name.includes('default_space_web_site')
          )
        }
        this.$set(this, 'forms', selectedForms)
      }
    },
    afterSaveHook({ error = {} }) {
      if (!error) {
        this.$emit('refreshlist')
        this.closeDialog()
      }
    },
    afterSerializeHook({ data }) {
      if (!isEmpty(data.parentSpace)) {
        data.parentSpace = { id: data.parentSpace }
      }
      return data
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    modifyFieldPropsHook(field) {
      let { name, value } = field || {}
      if (isEmpty(value)) {
        if (name === 'building') {
          return { ...field, value: this.building?.id }
        } else if (name === 'site') {
          return { ...field, value: this.site?.id }
        } else if (name === 'floor') {
          return { ...field, value: this.floor?.id }
        } else if (name === 'parentSpace') {
          return { ...field, value: this.spaceParent?.id }
        }
      }
    },
  },
}
</script>
