<template>
  <el-dialog
    :visible="true"
    width="30%"
    top="10vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center"
    :append-to-body="true"
    :show-close="false"
    :title="$t('qanda.template.execute_trigger')"
    :before-close="beforeClose"
  >
    <div v-if="loading" class="full-layout-white height100 text-center">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <div class="pR20 pL20 pT30 pB30">
        <div class="field-label">
          {{ fieldLabel }}
        </div>
        <FLookupField
          class="width100"
          :model.sync="resource"
          :fetchOptionsOnLoad="true"
          :canShowLookupWizard="showLookupFieldWizard"
          :field="fieldObj"
          @showLookupWizard="showLookupWizard"
          @setLookupFieldValue="setLookupFieldValue"
          :hideLookupIcon="!isInspectionModule"
        ></FLookupField>
      </div>
      <div class="d-flex mT-auto">
        <el-button class="modal-btn-cancel" @click="beforeClose">
          {{ $t('qanda.template.cancel') }}
        </el-button>
        <el-button
          :loading="saving"
          class="modal-btn-save m0"
          @click="save"
          type="primary"
        >
          {{ $t('qanda.template.save') }}
        </el-button>
      </div>
    </div>
    <FLookupFieldWizard
      v-if="showLookupFieldWizard"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :selectedLookupField="fieldObj"
      @setLookupFieldValue="setLookupFieldValue"
    ></FLookupFieldWizard>
  </el-dialog>
</template>

<script>
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['beforeClose', 'moduleName', 'record'],
  components: { FLookupField, FLookupFieldWizard },
  data() {
    return {
      showLookupFieldWizard: false,
      resource: {},
      saving: false,
      fieldObj: {},
      loading: false,
    }
  },
  computed: {
    isInspectionModule() {
      let { moduleName } = this
      return moduleName.includes('inspection')
    },
    fieldLabel() {
      let { isInspectionModule } = this
      return isInspectionModule
        ? this.$t('qanda.template.select_resources')
        : this.$t('qanda.template.select_sites')
    },
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      let { isInspectionModule, record } = this
      let lookupModuleName = 'site'
      let displayName = isInspectionModule ? 'Resources' : 'Sites'
      let placeHolderText = isInspectionModule
        ? 'Select Resources'
        : 'Select Sites'
      let fieldObj

      if (!isInspectionModule) {
        let { siteApplyTo, sites } = record || {}
        if (!siteApplyTo && !isEmpty(sites)) {
          let filters = {
            id: { operatorId: 36, value: sites.map(site => `${site.id}`) },
          }
          fieldObj = { filters }
        }
      } else {
        fieldObj = { displayTypeEnum: 'WOASSETSPACECHOOSER' }
        let { assignmentType, buildings, spaceCategory, assetCategory, sites } =
          record || {}

        let param = {
          assignmentTypeId: assignmentType,
          baseSpaceIds: (buildings || []).map(building => building.id),
          spaceCategoryId: (spaceCategory || {}).id,
          assetCategoryId: (assetCategory || {}).id,
          siteIds: (sites || []).map(site => site.id),
          qAndATemplateId: this.record.id,

          moduleName: this.moduleName,
        }
        Object.keys(param).forEach(paramProperty => {
          if (isEmpty(param[paramProperty])) delete param[paramProperty]
        })
        let { data, error } = await API.post(
          'v3/resourceAllocation/getModuleAndCrtieriaFromConfig',
          param
        )
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { criteria, module } = data || {}
          fieldObj = {
            clientCriteria: criteria,
          }
          lookupModuleName = module
        }
      }

      fieldObj = {
        isDataLoading: false,
        options: [],
        lookupModuleName: lookupModuleName,
        field: {
          lookupModule: {
            name: lookupModuleName,
            displayName: displayName,
          },
        },
        placeHolderText: placeHolderText,
        forceFetchAlways: true,
        isDisabled: false,
        multiple: true,
        ...fieldObj,
      }
      let assignmentTypeEnum = this.$getProperty(record, 'assignmentTypeEnum')
      if (
        !isEmpty(assignmentTypeEnum) &&
        assignmentTypeEnum === 'ASSET_CATEGORY'
      ) {
        fieldObj.filters = {
          ...fieldObj.filters,
          storeRoom: {
            operatorId: 1,
          },
        }
      }
      this.fieldObj = fieldObj
      this.loading = false
    },
    setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field
      this.resource = selectedItems.map(item => item.value)
      this.showLookupFieldWizard = false
    },
    showLookupWizard(field, canShow) {
      this.showLookupFieldWizard = canShow
    },
    async save() {
      this.saving = true
      let {
        moduleName,
        record,
        resource,
        beforeClose,
        isInspectionModule,
      } = this
      let { id } = record || {}
      let param = {
        resources: resource.map(currResource => {
          return { id: currResource }
        }),
      }
      let { error } = await API.post(
        `v3/qanda/template/execute?moduleName=${moduleName}&id=${id}`,
        param
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        beforeClose()
        this.$message.success(
          `${isInspectionModule ? 'Inspection' : 'Induction'} is triggered`
        )
      }
      this.saving = false
    },
  },
}
</script>

<style scoped>
.field-label {
  color: #385571;
  letter-spacing: 0.7px;
  margin-bottom: 12px;
}
</style>
<style lang="scss">
.fc-dialog-center-container {
  span.el-input__prefix {
    right: 5px;
    left: 95%;
    z-index: 10;
  }
}
</style>
