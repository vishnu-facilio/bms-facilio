<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
    >
      <div class="f-form-header pT10 pB10 pL40 pR40 d-flex">
        <div class="f-form-title mT10">{{ formDisplayName }}</div>
        <el-select
          v-if="forms.length > 1"
          v-model="selectedForm"
          value-key="name"
          class="fc-input-full-border-select2 mL-auto width25"
        >
          <el-option
            v-for="(form, index) in forms"
            :key="index"
            :value="form"
            :label="form.displayName"
          ></el-option>
        </el-select>
      </div>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="formDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :moduleDataId="moduleDataId"
        :isEdit="!$validation.isEmpty(moduleDataId)"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        @save="saveRecord"
        @cancel="closeDialog"
        :isV3Api="isV3Api"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'FloorForm',
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'floorObj', 'building'],
  computed: {
    formDisplayName() {
      return this.formObj?.displayName || 'Floor'
    },
    moduleDisplayName() {
      return this.formObj?.module?.displayName || 'Floor'
    },
    moduleDataId() {
      return this.floorObj?.id
    },
    moduleName() {
      return 'floor'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    afterSaveHook({ error = {} }) {
      if (!error) {
        this.$emit('refreshlist')
        this.closeDialog()
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    modifyFieldPropsHook(field) {
      let { name, value } = field || {}
      if (isEmpty(value)) {
        if (name === 'building') {
          return { ...field, value: this.building?.id }
        } else if (name === 'siteId' || name === 'site') {
          return { ...field, value: this.building?.siteId }
        }
      }
    },
  },
}
</script>
