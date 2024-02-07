<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    title=""
    :show-close="false"
    class="planner-selector-dialog"
    width="40%"
    :close-on-click-modal="false"
  >
    <div
      v-if="isSaving && isBulkSelect"
      class="flex flex-col items-center pT25 pB10"
    >
      <div class="progress-dialog-heading">
        {{ $t('maintenance.pm.selection_in_progress') }}
      </div>
      <div class="progress-dialog-sub-heading">
        {{
          `${$t('maintenance.pm.progress_prompt_1')} ${totalCount} ${$t(
            'maintenance.pm.progress_prompt_2'
          )}`
        }}
      </div>
      <spinner :show="isSaving" size="80"></spinner>
    </div>
    <div v-else class="position-relative height280">
      <div class="p20">
        <div class="planner-selector-header">
          {{ selectorTitle }}
        </div>
        <div class="selector-name-label mT15">
          {{ $t('maintenance.wr_list.assignedto') }}
        </div>
        <el-select
          v-model="assoications.assignedTo"
          filterable
          remote
          :remote-method="userSearch"
          clearable
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="user in userOptions"
            :key="user.id"
            :label="user.label"
            :value="user.value"
          ></el-option>
        </el-select>
        <div class="selector-name-label mT15">
          {{ $t('maintenance.pm.job_plan') }}
        </div>
        <Lookup
          v-model="assoications.jobPlan"
          :field="lookupFields.jobplan"
          @showLookupWizard="showLookupWizard"
        ></Lookup>
      </div>

      <div class="modal-dialog-footer planner-selector-footer">
        <div class="pR20 pB20 d-flex">
          <el-button
            type="primary"
            class="pm-ps-save"
            :loading="isSaving"
            @click="saveResources"
            >{{ $t('jobplan.save') }}</el-button
          >
        </div>
      </div>
    </div>
    <template v-if="canShowLookupWizard">
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
    </template>
  </el-dialog>
</template>

<script>
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { Lookup, LookupWizard } from '@facilio/ui/forms'
import {
  JOB_PLAN_SCOPE,
  getPlaceholderText,
  getCategoryFilter,
} from '../../utils/pm-utils.js'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'

export default {
  props: [
    'canShowFieldSelector',
    'closeDialog',
    'bulkCreateResources',
    'reloadPlanner',
    'pmRecord',
    'isBulkSelect',
    'totalCount',
  ],
  components: { Lookup, LookupWizard, FLookupFieldWizard },
  data: () => ({
    assoications: { assignedTo: null, jobPlan: null },
    isSaving: false,
    isLoading: false,
    userOptions: [],
    selectedLookupField: null,
    canShowLookupWizard: false,
  }),
  created() {
    this.initResources()
    this.userSearch = debounce(this.loadUsers, 500)
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    checkNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    lookupFields() {
      let { pmRecord } = this
      let { assignmentTypeEnum } = pmRecord || {}
      let scopeCategory = JOB_PLAN_SCOPE[assignmentTypeEnum]
      let fields = {
        user: {
          isDataLoading: false,
          options: [],
          lookupModuleName: 'users',
          field: {
            lookupModule: {
              name: 'users',
              displayName: 'Users',
            },
          },
          multiple: false,
          filters: {
            user: {
              operatorId: 15,
              value: ['true'],
            },
          },
        },
        jobplan: {
          isDataLoading: false,
          options: [],
          config: {},
          lookupModuleName: 'jobplan',
          field: {
            lookupModule: {
              name: 'jobplan',
              displayName: 'Jobplan',
            },
          },
          filters: {
            jobPlanCategory: { operatorId: 54, value: [`${scopeCategory}`] },
            jpStatus: { operatorId: 54, value: ['2'] },
            ...getCategoryFilter(pmRecord, scopeCategory),
          },
          multiple: false,
        },
      }
      return fields
    },
    selectorTitle() {
      let { pmRecord } = this
      let title = getPlaceholderText({
        pmRecord,
        text: this.$t('maintenance.pm.field_selector_title'),
        replaceAll: true,
      })
      return title
    },
  },
  methods: {
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { options = [], selectedItems = [], lookupModuleName } = field
      let selectedItemIds = []
      let selectedField

      if (!isEmpty(selectedItems)) {
        selectedItemIds = (selectedItems || []).map(item => {
          return item.value
        })
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )

          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      selectedField = lookupModuleName === 'jobplan' ? 'jobPlan' : 'assignedTo'
      if (!isEmpty(selectedItemIds)) {
        this.$set(this.assoications, `${selectedField}`, selectedItemIds[0])
      }
    },
    async initResources() {
      await this.loadUsers()
    },
    async loadUsers(search) {
      this.isLoading = true
      let { id: appId } = getApp()
      let params = { appId, page: 1, perPage: 50, inviteAcceptStatus: true }

      if (!isEmpty(search)) {
        params = { ...params, search }
      }

      let { error, data } = await API.get(`/v2/application/users/list`, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { users } = data || {}
        this.userOptions = (users || []).map(user => {
          let { name, id } = user || {}
          return {
            label: name,
            value: id,
          }
        })
      }
      this.isLoading = false
    },
    async saveResources() {
      let { assoications } = this || {}
      this.isSaving = true
      let response = await this.bulkCreateResources(assoications)
      let { error } = response || {}
      if (error) {
        this.$message.error(
          error.message || 'Error occured while associating resources'
        )
      } else {
        this.closeDialog()
        this.reloadPlanner()
      }
      this.isSaving = false
    },
  },
}
</script>

<style lang="scss">
.planner-selector-dialog {
  .progress-dialog-heading {
    font-size: 24px;
    color: #324056;
    font-weight: 600;
  }
  .progress-dialog-sub-heading {
    font-size: 14px;
    color: #324056;
    margin: 14px 0px 24px;
  }
  .selector-name-label {
    font-size: 14px;
    line-height: normal;
    display: flex;
    word-break: break-word;
    margin-bottom: 15px;
  }
  .el-dialog__header {
    padding: 0px;
  }
  .planner-selector-header {
    font-size: 15px;
    font-weight: 600;
    padding-bottom: 5px;
    word-break: break-word;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .planner-selector-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
  .pm-ps-save {
    width: 90px;
    height: 36px;
    border-radius: 4px;
    border-color: transparent;
    background-color: #3ab2c2;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    text-transform: capitalize;
    text-align: center;
    margin-top: 10px;
    &:hover {
      background-color: #3ab2c2;
      color: #ffffff;
    }
    &:active {
      color: #fff;
      background-color: #3ab2c2;
      border: transparent;
    }
  }
}
</style>
