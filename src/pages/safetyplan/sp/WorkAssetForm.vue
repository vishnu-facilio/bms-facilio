<template>
  <div>
    <el-form>
      <el-dialog
        :title="$t('common.safetyPlan.workAssets')"
        :visible.sync="showMsgPopup"
        width="40%"
        :append-to-body="true"
        style="z-index: 9999999999;"
        class="agents-dialog fc-dialog-center-container dialog-padding"
      >
        <div
          class="label-txt-black line-height24 height300 overflow-y-scroll pB50"
        >
          <el-form-item prop="asset" label="Asset">
            <FLookupField
              :model.sync="selectedAsset"
              :field="assetField"
              :disabled="!$validation.isEmpty(selectedSpace)"
              @showLookupWizard="openCloseWizard('asset', true)"
            />
          </el-form-item>
          <el-form-item prop="space" label="Space">
            <FLookupField
              :model.sync="selectedSpace"
              :disabled="!$validation.isEmpty(selectedAsset)"
              :field="spaceField"
              @showLookupWizard="openCloseWizard('space', true)"
            />
          </el-form-item>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel f13" @click="showDialog(false)">{{
            $t('agent.agent.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save f13"
            :disabled="!selectedAsset && !selectedSpace"
            @click="saveRecord()"
            >{{ $t('common.failure_class.save') }}</el-button
          >
        </div>
      </el-dialog>
    </el-form>
    <V3LookupFieldWizard
      v-if="canShowWizard"
      :canShowLookupWizard.sync="canShowWizard"
      :selectedLookupField="wizardField"
      @setLookupFieldValue="setLookUpValue"
    ></V3LookupFieldWizard>
  </div>
</template>
<script>
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from '@/forms/FLookupField'

export default {
  props: ['details', 'availableAssets', 'availableSpaces', 'moduleDisplayName'],
  components: { V3LookupFieldWizard, FLookupField },
  data() {
    return {
      canShowWizard: false,
      wizardField: null,
      selectedAsset: null,
      selectedSpace: null,
      selectedModule: null,
      showMsgPopup: true,
    }
  },
  computed: {
    assetField() {
      let { availableAssets } = this || {}
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        multiple: false,
        forceFetchAlways: true,
        selectedItems: [],
        filters: {
          siteId: {
            operatorId: 9,
            value: [`${this.details?.siteId}`],
          },
        },
      }
      if (availableAssets.length) {
        let { filters } = field || {}
        let { id } = filters || {}
        field = {
          ...field,
          filters: {
            ...filters,
            id: { ...id, operatorId: 37, value: availableAssets },
          },
        }
      }
      return field
    },
    spaceField() {
      let { availableSpaces } = this
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'basespace',
        field: {
          lookupModule: {
            name: 'basespace',
            displayName: 'Space',
          },
        },
        multiple: false,
        forceFetchAlways: true,
        selectedItems: [],
        filters: {
          siteId: {
            operatorId: 9,
            value: [`${this.details?.siteId}`],
          },
        },
      }
      if (!isEmpty(availableSpaces)) {
        field['filters']['id'] = {
          operatorId: 37,
          value: this.availableSpaces,
        }
      }
      return field
    },
  },
  watch: {
    showMsgPopup: {
      handler(oldVal, newVal) {
        if (oldVal != newVal) {
          this.$emit('closeDialog')
        }
      },
    },
  },
  methods: {
    init() {
      this.selectedSpace = null
      this.selectedAsset = null
      this.wizardField = null
    },
    openCloseWizard(moduleName, canShow) {
      if (['asset', 'space'].includes(moduleName)) {
        this.wizardField =
          moduleName === 'asset' ? this.assetField : this.spaceField
        this.selectedModule = moduleName
        this.canShowWizard = canShow
      } else {
        this.canShowWizard = false
      }
    },
    setLookUpValue(selectedValue) {
      let { selectedModule } = this
      let field = this.$getProperty(selectedValue, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      if (!isEmpty(selectedData)) {
        let data = selectedData[0]
        if (selectedModule === 'asset') this.selectedAsset = data?.value
        else if (selectedModule === 'space') this.selectedSpace = data?.value
      }
      this.openCloseWizard(false)
    },
    showDialog(canShow) {
      this.init()
      if (!canShow) this.canShowWizard = canShow
      this.$emit('closeDialog')
    },
    async saveRecord() {
      let { moduleDisplayName, selectedAsset, selectedSpace } = this
      let safetyPlanId = this.details.id
      let successMsg = `${moduleDisplayName} added successfully!`
      if (selectedAsset || selectedSpace) {
        let data = {
          asset: {
            id: selectedAsset,
          },
          space: {
            id: selectedSpace,
          },
          safetyPlan: { id: safetyPlanId },
        }
        this.isSaving = true
        let response = await API.createRecord('workAsset', {
          data,
        })
        let { error } = response || {}
        this.isSaving = false
        if (error) {
          this.$message.error(
            error?.message || this.$t('common.safetyPlan.error_occured')
          )
        } else {
          successMsg && this.$message.success(successMsg)
          this.showDialog(false)
          if (!isEmpty(selectedAsset)) {
            this.$emit('getWorkAssetHazards', selectedAsset, 'asset')
          } else {
            this.$emit('getWorkAssetHazards', selectedSpace, 'space')
          }
        }
      }
    },
  },
}
</script>
