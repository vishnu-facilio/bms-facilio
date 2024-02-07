<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div class="fL mL10">
              <div class="custom-module-id">#{{ record && record.id }}</div>
              <div class="custom-module-name d-flex mT5">
                {{ record[mainFieldKey] }}
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <el-dropdown
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 mtb5 pointer"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div @click="editRecord">
                  {{ $t('common._common.edit') }}
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="addSafetyHazard">
                  {{ $t('common.products.add_safety_hazards') }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
      <V3LookupFieldWizard
        v-if="canShowWizard"
        :canShowLookupWizard.sync="canShowWizard"
        :selectedLookupField="hazardField"
        @setLookupFieldValue="saveRecord"
      ></V3LookupFieldWizard>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'src/pages/custom-module/CustomModuleSummary.vue'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  components: {
    V3LookupFieldWizard,
  },
  computed: {
    hazardField() {
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'hazard',
        field: {
          lookupModule: {
            name: 'hazard',
            displayName: 'Hazards',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        additionalParams: {
          excludeAvailableHazards: this.record?.id,
        },
      }
      return field
    },
  },
  data() {
    return {
      canShowWizard: false,
      notesModuleName: 'safetyPlanNotes',
      attachmentsModuleName: 'safetyPlanAttachments',
    }
  },
  methods: {
    editRecord() {
      let { moduleName, id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'safetyplan-edit',
          params: { moduleName, id },
        })
      }
    },
    addSafetyHazard() {
      this.excludeAvailableRecords()
      this.canShowWizard = true
    },
    closeLookUpWizard() {
      this.canShowWizard = false
    },
    async saveRecord(hazardData) {
      this.isLoading = true
      let records = []
      let field = this.$getProperty(hazardData, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      selectedData.map(record => {
        records.push({
          safetyPlan: {
            id: this.record?.id,
          },
          hazard: {
            id: record?.value,
          },
        })
      })
      let url = 'v3/modules/data/bulkCreate'
      let params = {
        data: {
          safetyPlanHazard: records,
        },
        moduleName: 'safetyPlanHazard',
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (!error) {
        this.refreshData()
      }
      this.isLoading = false
      this.closeLookUpWizard()
    },
    excludeAvailableRecords() {},
  },
}
</script>
