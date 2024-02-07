<template>
  <div>
    <div class="vm-section-header">
      {{ $t('asset.virtual_meters.vm_templates') }}
    </div>
    <el-form
      ref="vm-template-form"
      :model="vmTemplateObj"
      :rules="validationRules"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30"
    >
      <div class="section-container flex-container">
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.vm_template_name')}`"
            prop="name"
          >
            <el-input
              v-model="vmTemplateObj.name"
              class="fc-input-full-border2 width60"
              :disabled="!isNew"
              :placeholder="
                `${this.$t('asset.virtual_meters.enter_vm_template_name')}`
              "
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.description')}`"
            prop="description"
          >
            <el-input
              v-model="vmTemplateObj.description"
              type="textarea"
              class="mT3 fc-input-full-border-textarea width60"
              :disabled="!isNew"
              :autofocus="true"
              :min-rows="2"
              :autosize="{ minRows: 4, maxRows: 5 }"
              :placeholder="`${this.$t('asset.virtual_meters.description')}`"
            ></el-input>
          </el-form-item>
        </div>
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.meters.utilityType')}`"
            prop="utilityType"
          >
            <FLookupField
              :key="`utilitytype`"
              :model.sync="vmTemplateObj.utilityType"
              :field="utilityTypeField"
              :isRemoteField="false"
              :disabled="!isNew"
              class="width60"
            ></FLookupField>
          </el-form-item>
        </div>
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.scope')}`"
            prop="scope"
          >
            <el-select
              v-model="vmTemplateObj.scope"
              :placeholder="$t('asset.virtual_meters.select_scope')"
              class="fc-input-full-border-select2 width60"
              :disabled="!isNew"
            >
              <el-option
                v-for="item in scopeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('common._common.sites')}`"
            prop="siteIds"
          >
            <FLookupField
              :key="`siteIds`"
              ref="siteIds"
              :model.sync="vmTemplateObj.siteIds"
              :field="siteField"
              :disabled="!isNew"
              @showLookupWizard="showLookupWizard"
              class="width60"
              @recordSelected="constructBuildingsFilter()"
            ></FLookupField>
          </el-form-item>
        </div>
        <div
          class="vm-form-item"
          v-if="canShowLookup && isBuildingNeeded(vmTemplateObj.scope)"
        >
          <el-form-item
            :label="`${this.$t('common._common.buildings')}`"
            prop="buildingIds"
          >
            <FLookupField
              :key="`buildingIds`"
              ref="buildingIds"
              :model.sync="vmTemplateObj.buildingIds"
              :field="buildingField"
              :disabled="!isNew"
              @showLookupWizard="showLookupWizard"
              class="width60"
            ></FLookupField>
          </el-form-item>
        </div>
        <div v-if="vmTemplateObj.scope === 3" class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.space_category')}`"
            prop="spaceCategory"
          >
            <FLookupField
              :key="`spacecategory`"
              :model.sync="vmTemplateObj.spaceCategory"
              :field="spaceCategoryField"
              :isRemoteField="false"
              :disabled="!isNew"
              @recordSelected="onSpaceCategorySelected"
              class="width60"
            ></FLookupField>
          </el-form-item>
        </div>
        <div v-if="vmTemplateObj.scope === 4" class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.asset_category')}`"
            prop="assetCategory"
          >
            <FLookupField
              :key="`assetcategory`"
              :model.sync="vmTemplateObj.assetCategory"
              :field="assetCategoryField"
              :isRemoteField="false"
              :disabled="!isNew"
              @recordSelected="onAssetCategorySelected"
              class="width60"
            ></FLookupField>
          </el-form-item>
        </div>
        <div class="vm-rel-form-item">
          <el-form-item
            :label="
              `${this.$t('asset.virtual_meters.associate_vm_relationship')}`
            "
            prop="relationShipId"
          >
            <el-select
              data-test-selector="relationShipId"
              ref="relationShipId"
              key="relationShipId"
              v-model="vmTemplateObj.relationShipId"
              filterable
              collapse-tags
              :placeholder="$t('asset.virtual_meters.select_relationship')"
              @visible-change="fetchRelationOptions"
              class="fc-input-full-border-select2 width60 fc-tag"
              remote
              :remote-method="remoteRelationRequest"
              :disabled="!relationSelectAllowed || !isNew"
            >
              <div slot="empty" class="min-width420">
                <div class="el-select-dropdown__empty">
                  {{ $t('common._common.empty_option_text') }}
                </div>
                <div class="vm-form-item">
                  <el-button
                    @click="addRelationship()"
                    class="add-vm-relationship-container"
                  >
                    {{ $t('asset.virtual_meters.new_relationship') }}</el-button
                  >
                </div>
              </div>
              <el-option
                v-for="(option, index) in relationshipOptions"
                :key="index"
                :label="option.name"
                :value="option.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </div>
    </el-form>
    <div v-if="canShowLookupWizard">
      <LookupWizard
        v-if="checkNewLookupWizardEnabled"
        :canShowLookupWizard.sync="canShowLookupWizard"
        :field="selectedLookupField"
        @setLookupFieldValue="setLookupFieldValue"
      ></LookupWizard>
      <FLookupFieldWizard
        v-else
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import { isEmpty } from 'util/validation'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { LookupWizard } from '@facilio/ui/forms'
import { API } from '@facilio/api'
import { vmScopeHash } from './vmUtil'
import debounce from 'lodash/debounce'

export default {
  components: {
    FLookupField,
    FLookupFieldWizard,
    LookupWizard,
  },
  props: ['isNew', 'vmInfo'],
  data() {
    return {
      vmTemplateObj: {
        name: '',
        description: '',
        utilityType: {
          id: null,
        },
        assetCategory: {
          id: null,
        },
        spaceCategory: {
          id: null,
        },
        scope: null,
        assets: null,
        spaces: null,
        siteIds: [],
        buildingIds: [],
        relationShipId: null,
      },
      validationRules: {
        name: [
          {
            required: true,
            message: 'Please enter VM template name',
            trigger: 'blur',
          },
        ],

        utilityType: [
          {
            required: true,
            message: 'Please select utility type',
            trigger: 'change',
          },
        ],

        assetCategory: [
          {
            required: true,
            message: 'Please select asset category',
            trigger: 'change',
          },
        ],

        spaceCategory: [
          {
            required: true,
            message: 'Please select space category',
            trigger: 'change',
          },
        ],

        scope: [
          {
            required: true,
            message: 'Please select scope',
            trigger: 'change',
          },
        ],

        siteIds: [
          {
            required: true,
            message: 'Please select sites',
            trigger: 'change',
          },
        ],

        buildingIds: [
          {
            required: true,
            message: 'Please select buildings',
            trigger: 'change',
          },
        ],

        relationShipId: [
          {
            required: true,
            message: 'Please select relationship',
            trigger: 'change',
          },
        ],
      },
      utilityTypeField: {
        lookupModuleName: 'utilitytype',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
        placeHolderText: `${this.$t(
          'asset.virtual_meters.select_utilityType'
        )}`,
      },
      siteField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'site',
        placeHolderText: `${this.$t('asset.virtual_meters.select_sites')}`,
        field: {
          lookupModule: {
            name: 'site',
            displayName: 'Site',
          },
        },
        multiple: true,
      },
      buildingField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'building',
        placeHolderText: `${this.$t('asset.virtual_meters.select_buildings')}`,
        field: {
          lookupModule: {
            name: 'building',
            displayName: 'Building',
          },
        },
        filters: {},
        multiple: true,
      },
      assetCategoryField: {
        lookupModuleName: 'assetcategory',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
        placeHolderText: `${this.$t(
          'asset.virtual_meters.select_asset_category'
        )}`,
      },
      spaceCategoryField: {
        lookupModuleName: 'spacecategory',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
        placeHolderText: `${this.$t(
          'asset.virtual_meters.select_space_category'
        )}`,
      },
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        placeHolderText: `${this.$t('asset.virtual_meters.select_asset')}`,
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        multiple: true,
      },
      spaceField: {
        lookupModuleName: 'space',
        field: {
          lookupModule: {
            name: 'space',
            displayName: 'Space',
          },
        },
        options: [],
        isDataLoading: false,
        multiple: true,
        placeHolderText: `${this.$t('asset.virtual_meters.select_space')}`,
      },
      scopeOptions: [
        {
          value: 1,
          label: 'All Floors',
        },
        {
          value: 2,
          label: 'All Spaces',
        },
        {
          value: 3,
          label: 'Space Category',
        },
        {
          value: 4,
          label: 'Asset Category',
        },
        {
          value: 7,
          label: 'All Buildings',
        },
        {
          value: 8,
          label: 'Sites',
        },
      ],
      canShowLookupWizard: false,
      selectedLookupField: null,
      selectedAssetCategoryLabel: '',
      selectedSpaceCategoryLabel: '',
      canShowLookup: true,
      relationshipOptions: [],
      newRelationName: null,
    }
  },
  created() {
    this.vmTemplateObj = this.vmInfo
    this.remoteRelationRequest = debounce(this.getRelationRemote, 500)
    if(!this.isNew) {
      this.fetchRelationOptions()
    }
  },
  computed: {
    checkNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    relationSelectAllowed() {
      let { vmTemplateObj } = this
      let { utilityType, scope } = vmTemplateObj || {}
      return !isEmpty(utilityType) && !isEmpty(scope)
    },
  },
  watch: {
    vmTemplateObj: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.$emit('updateVirtualMeter', newVal)
        }
      },
      deep: true,
    },
  },
  methods: {
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    onSpaceCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { spaceField, vmTemplateObj } = this
      let { selectedItems } = spaceField || {}
      if (!isEmpty(label)) {
        this.selectedSpaceCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        spaceField.selectedItems = []
        this.$set(vmTemplateObj, 'spaces', null)
      }
    },
    onAssetCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { assetField, vmTemplateObj } = this
      let { selectedItems } = assetField || {}
      if (!isEmpty(label)) {
        this.selectedAssetCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        assetField.selectedItems = []
        this.$set(vmTemplateObj, 'assets', null)
      }
    },
    validate() {
      return new Promise(resolve => {
        this.$refs['vm-template-form'].validate(valid => {
          if (valid) {
            resolve(true)
          } else {
            resolve(false)
          }
        })
      })
    },
    isBuildingNeeded(scope) {
      return (
        !isEmpty(scope) &&
        (scope == 1 || scope == 2 || scope == 3 || scope == 4)
      )
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { selectedItems = [], lookupModuleName } = field
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = (selectedItems || []).map(item => {
          return item.value
        })
      }
      if (lookupModuleName === 'site') {
        this.$setProperty(this, 'vmTemplateObj.siteIds', selectedItemIds)
      } else if (lookupModuleName === 'building') {
        this.$setProperty(this, 'vmTemplateObj.buildingIds', selectedItemIds)
      }
      this.selectedLookupField = null
      this.canShowLookupWizard = false
    },
    constructBuildingsFilter() {
      let { vmTemplateObj } = this
      let { siteIds } = vmTemplateObj || {}
      let buildingFilters = {}
      let values = []
      if (!isEmpty(siteIds)) {
        siteIds.forEach(id => {
          values.push(id.toString())
        })
      }
      if (!isEmpty(values)) {
        buildingFilters = {
          siteId: {
            operatorId: 9,
            value: values,
          },
        }
      }
      this.$setProperty(this, 'buildingField.filters', buildingFilters)
      this.$setProperty(this, 'vmTemplateObj.buildingIds', [])
      this.canShowLookup = false
      this.$nextTick(() => {
        this.canShowLookup = true
      })
    },
    async getCategoryRecordById(id, moduleName) {
      let {
        [moduleName]: record = {},
        error,
      } = await API.fetchRecord(moduleName, { id })
      if (!error) {
        return record
      } else {
        this.$message.error(error?.message || 'Error Occured')
      }
    },
    async loadModuleMeta(scopeModulename) {
      let { data, error } = await API.get(`v2/modules/meta/${scopeModulename}`)
      if (error) {
        this.$message.error(this.$t('asset.virtual_meters.error_loading_module_meta'))
      } else {
        return this.$getProperty(data, 'meta', {}) || {}
      }
      return {}
    },
    async addRelationship() {
      let { newRelationName, vmTemplateObj } = this
      let { utilityType, scope, assetCategory, spaceCategory } = vmTemplateObj || {}
      let selectedUtilityType = await this.getCategoryRecordById(
        utilityType,
        'utilitytype'
      )
      let vmScope = scope
      let lookupModuleName = this.$getProperty(vmScopeHash, `${vmScope}`, null)
      if (!isEmpty(lookupModuleName) && !isEmpty(selectedUtilityType)) {
        let fromId = null
        let fromModuleDisplayName = ''
        if(lookupModuleName === 'assetCategory'){
          let selectedAssetCategory = await this.getCategoryRecordById(
          assetCategory,
          'assetcategory'
        )
          fromId = this.$getProperty(selectedAssetCategory,'assetModuleID')
          fromModuleDisplayName = this.$getProperty(selectedAssetCategory,'displayName')
        }
        else if(lookupModuleName === 'spaceCategory'){
          let selectedSpaceCategory = await this.getCategoryRecordById(
          spaceCategory,
          'spacecategory'
        )
          fromId = this.$getProperty(selectedSpaceCategory,'spaceModuleID')
          fromModuleDisplayName = this.$getProperty(selectedSpaceCategory,'displayName')
        }
        else{
          let moduleMeta = await this.loadModuleMeta(lookupModuleName)
          fromId = this.$getProperty(moduleMeta, 'module.moduleId')
          fromModuleDisplayName = this.$getProperty(
          moduleMeta,
          'module.displayName'
        )
        }
        let toId = this.$getProperty(selectedUtilityType, 'meterModuleID')
        let utilityTypeName = this.$getProperty(selectedUtilityType, 'name')
        let params = {
          relation: {
            name: `${newRelationName}`,
            fromModuleId: fromId,
            toModuleId: toId,
            relationName: `${fromModuleDisplayName} to ${utilityTypeName}`,
            reverseRelationName: `${utilityTypeName} to ${fromModuleDisplayName}`,
            relationType: 1,
            relationCategory: 3,
          },
        }
        let { error, data } = await API.post('v2/relation/addOrUpdate', params)
        if (isEmpty(error)) {
          this.getRelationOptions()
        } else {
          this.$message.error(
            error.message ||
              this.$t('asset.virtual_meters.error_creating_relationship')
          )
        }
      }
    },
    async getRelationRemote(searchText) {
      this.getRelationOptions(searchText)
      this.newRelationName = searchText
    },
    fetchRelationOptions() {
      this.getRelationOptions()
    },
    async getRelationOptions(searchText) {
      let { vmTemplateObj } = this
      let { utilityType, scope, assetCategory, spaceCategory } = vmTemplateObj || {}
      let selectedUtilityType = await this.getCategoryRecordById(
        utilityType,
        'utilitytype'
      )
      let vmScope = scope
      let lookupModuleName = this.$getProperty(vmScopeHash, `${vmScope}`, null)
      if (!isEmpty(lookupModuleName) && !isEmpty(selectedUtilityType)) {
        let fromId = null
        if(lookupModuleName === 'assetCategory'){
          let selectedAssetCategory = await this.getCategoryRecordById(
          assetCategory,
          'assetcategory'
        )
          fromId = this.$getProperty(selectedAssetCategory,'assetModuleID')
        }
        else if(lookupModuleName === 'spaceCategory'){
          let selectedSpaceCategory = await this.getCategoryRecordById(
          spaceCategory,
          'spacecategory'
        )
          fromId = this.$getProperty(selectedSpaceCategory,'spaceModuleID')
        }
        else{
          let moduleMeta = await this.loadModuleMeta(lookupModuleName)
          fromId = this.$getProperty(moduleMeta, 'module.moduleId')
        }
        let toId = this.$getProperty(selectedUtilityType, 'meterModuleID')
        let params = {
          fromModuleId: fromId,
          toModuleId: toId,
          relationType: 1,
          relationCategory: 3,
        }
        if (!isEmpty(searchText)) {
          params = { ...params, search: searchText }
        }
        let { error, data } = await API.get(
          'v2/relation/fetchRelations',
          params
        )
        if (isEmpty(error)) {
          this.relationshipOptions = this.$getProperty(data, 'relationList')
        }
      }
    },
  },
}
</script>
<style lang="scss">
.vm-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
  .el-form-item__label {
    width: 160px !important;
  }
}
.add-vm-relationship-container {
  border: none;
  width: 100%;
  text-align: left;
  color: #0074d1;
  font-weight: 400;
}
.min-width420 {
  min-width: 420px;
}
.vm-rel-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
  .el-form-item__label {
    width: 350px !important;
  }
}
</style>
