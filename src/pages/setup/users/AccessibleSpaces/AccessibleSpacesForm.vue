<template>
  <div>
    <el-dialog
      :title="
        isNew
          ? $t('setup.create.add_accessible')
          : $t('setup.create.edit_accessible')
      "
      :visible="true"
      width="40%"
      custom-class="fc-dialog-center-container fc-accessible-space-page"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div class="height300">
        <el-form
          ref="accessibleForm"
          :model="accessible"
          :label-position="'top'"
        >
          <el-form-item>
            <div>
              {{ $t('setup.users_management.sites') }}
              <el-tooltip
                class="item"
                effect="dark"
                :content="
                  $t(
                    'setup.users_management.configuring_spaces_at_site_level_enables_the_user_to_access_the_entire_site'
                  )
                "
                placement="bottom"
              >
                <i class="el-icon-info capp-linkname"></i>
              </el-tooltip>
            </div>
            <FLookupField
              :model.sync="accessible.site"
              :field="fields.site"
              :hideDropDown="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardSite"
            ></FLookupField>
          </el-form-item>
          <el-form-item>
            <div>
              {{ $t('setup.users_management.buildings') }}
              <el-tooltip
                class="item"
                effect="dark"
                :content="
                  $t(
                    'setup.users_management.configuring_spaces_at_building_level_enables_the_user_to_access_the_selected_building_of_that_particular_site'
                  )
                "
                placement="bottom"
              >
                <i class="el-icon-info capp-linkname"></i>
              </el-tooltip>
            </div>
            <FLookupField
              :model.sync="accessible.building"
              :field="fields.building"
              :hideDropDown="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardBuilding"
            ></FLookupField>
          </el-form-item>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="saveAccessible"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </el-dialog>
    <AccessibleLookupWizard
      v-if="canShowLookupWizardSite"
      :summaryId="summaryId"
      :canShowLookupWizard.sync="canShowLookupWizardSite"
      :selectedLookupField="selectedLookupField"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
    ></AccessibleLookupWizard>
    <AccessibleLookupWizard
      v-if="canShowLookupWizardBuilding"
      :summaryId="summaryId"
      :canShowLookupWizard.sync="canShowLookupWizardBuilding"
      :selectedLookupField="selectedLookupField"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValueBuilding"
    ></AccessibleLookupWizard>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import FLookupField from '@/forms/FLookupField'
import AccessibleLookupWizard from 'pages/setup/users/AccessibleSpaces/AccessibleLookupWizard'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: true,
    additionalParams: {
      orderBy: 'name',
      orderType: 'asc',
    },
  },
  building: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'building',
    field: {
      lookupModule: {
        name: 'building',
        displayName: 'Buildings',
      },
    },
    forceFetchAlways: true,
    multiple: true,
  },
}
export default {
  props: ['isNew', 'accessibleData'],
  data() {
    return {
      fields,
      canShowLookupWizardSite: false,
      selectedLookupField: null,
      canShowLookupWizardBuilding: false,
      saving: false,
      accessible: {
        site: null,
        building: null,
      },
    }
  },
  components: {
    FLookupField,
    AccessibleLookupWizard,
  },
  computed: {
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    showLookupWizardSite(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    showLookupWizardBuilding(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardBuilding', canShow)
    },
    setLookupFieldValue({ field }) {
      this.accessible.site = field.selectedItems.map(item => item.value)
      let { options } = this.fields.site
      let selectedItemsInOptions = options.filter(option =>
        this.accessible.site.includes(option.value)
      )
      let selectedItemIdsInOptions = selectedItemsInOptions.map(
        item => item.value
      )
      let selectedItemsNotInOptions = field.selectedItems.filter(
        item => !selectedItemIdsInOptions.includes(item.value)
      )

      this.$set(this.fields.site, 'options', [
        ...options,
        ...selectedItemsNotInOptions,
      ])
    },
    setLookupFieldValueBuilding({ field }) {
      this.accessible.building = field.selectedItems.map(item => item.value)
      let { options } = this.fields.building
      let selectedItemsInOptions = options.filter(option =>
        this.accessible.building.includes(option.value)
      )
      let selectedItemIdsInOptions = selectedItemsInOptions.map(
        item => item.value
      )
      let selectedItemsNotInOptions = field.selectedItems.filter(
        item => !selectedItemIdsInOptions.includes(item.value)
      )

      this.$set(this.fields.building, 'options', [
        ...options,
        ...selectedItemsNotInOptions,
      ])
    },
    saveAccessible() {
      this.$refs['accessibleForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v3/accessiblespace/add'
        let params = {
          ouId: this.summaryId,
          spaceIds: [...this.accessible.site, ...this.accessible.building],
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common._common.accessible_saved_successfully')
          )
          this.$emit('onSave', data.accessibleContext)
          this.closeDialog()
        }
        this.saving = false
      })
    },
  },
}
</script>
<style lang="scss">
.fc-accessible-space-page {
  .fc-lookup-icon {
    margin-right: 17px;
  }
  .f-lookup-chooser .remove-icon {
    display: none;
    position: absolute;
    top: 3px;
    right: 60px;
  }
}
</style>
