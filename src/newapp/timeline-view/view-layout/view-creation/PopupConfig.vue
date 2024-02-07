<template>
  <div>
    <div class="flex-center-row-space">
      <div class="flex-center-row-space">
        <p class="fc-input-label-txt txt-color pB0 flex-middle">
          {{ `Choose fields to show in event popup` }}
        </p>
        <el-tooltip
          :content="$t('setup.scheduler.all_values_selected')"
          placement="top"
        >
          <i class="el-icon-info mT4 mL5"></i>
        </el-tooltip>
      </div>
      <div
        class="configure-btn new-statetransition-config pointer"
        @click="canShowSummaryFields = true"
      >
        <i class="el-icon-plus fw-bold "></i>
        {{
          isSummaryFieldConfigured
            ? $t('common._common.update')
            : $t('common._common.configure')
        }}
      </div>
    </div>
    <FieldPickerDialog
      v-if="canShowSummaryFields"
      :title="`Configure Popup Fields`"
      :availableFields="moduleFields"
      :selectedList="summaryFields"
      itemKey="fieldId"
      @save="list => (summaryFields = list)"
      @close="canShowSummaryFields = false"
    ></FieldPickerDialog>
  </div>
</template>
<script>
import FieldPickerDialog from './components/PopupFieldPicker.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['viewDetails', 'moduleFields', 'saveAsNew'],
  components: { FieldPickerDialog },
  data() {
    return {
      canShowSummaryFields: false,
      summaryFields: [],
    }
  },
  created() {
    if (!this.isNew || this.saveAsNew) {
      let { configJson } = this.viewDetails || {}
      configJson = (configJson && JSON.parse(configJson)) || {}

      let popupFields = configJson['popup-fields'] || []
      popupFields = popupFields.map(fldId => parseInt(fldId))

      this.summaryFields = this.moduleFields.filter(fld =>
        popupFields.includes(fld.id)
      )
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.$route.params.viewname)
    },
    isSummaryFieldConfigured() {
      return !isEmpty(this.summaryFields)
    },
  },
  methods: {
    serialize() {
      let { summaryFields, isSummaryFieldConfigured } = this

      if (isSummaryFieldConfigured) {
        let popupFields = (summaryFields || []).map(fld => `${fld.id}`)
        let configJson = { 'popup-fields': popupFields }

        return { configJson: JSON.stringify(configJson) }
      } else {
        return {}
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.configure-btn {
  color: #3ab2c1;
}
</style>
