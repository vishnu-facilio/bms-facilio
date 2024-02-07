<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    width="90%"
    top="2vh"
    class="planner-sheet-dialog"
    :show-close="false"
  >
    <template slot="title">
      <div class="flex items-center pB10">
        <div class="lookup-wizard-title">
          {{ getFormattedText($t('maintenance.pm.resource_sheet'), true) }}
        </div>
        <div class="ml-auto flex items-center">
          <div
            @click="closeDialog"
            class="el-dialog__close el-icon el-icon-close close-icon cursor-pointer resource-icons"
          ></div>
        </div>
      </div>
    </template>
    <div class="fc-bulk-form-page">
      <div v-if="isLoading" class="d-flex mT50 planner-body">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <template v-else>
        <div class="bulk-action-container">
          <el-button
            :loading="isDeleting"
            class="bulk-action-button mR10"
            @click="deleteResources"
            :disabled="!canShowBulkActions"
            ><i class="el-icon-delete pR3"></i>
            {{ $t('asset.assets.delete') }}
          </el-button>
          <el-button
            :loading="isJobPlanAssigning"
            class="bulk-action-button mR10"
            :disabled="!canShowBulkActions"
            @click="showLookupWizard('jobplan')"
          >
            <i class="el-icon-document-checked pR3"></i>
            {{ $t('maintenance.pm.update_jp') }}
          </el-button>

          <el-dropdown @command="setBulkAssignedTo" trigger="click">
            <el-button
              :loading="isAssigning"
              class="bulk-action-button mR10"
              :disabled="!canShowBulkActions"
            >
              <i class="el-icon-user pR3"></i>
              {{ $t('maintenance.pm.add_assignee') }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="user in users"
                :key="user.id"
                :command="user.id"
                >{{ user.name }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div>
          <BulkActionBar
            ref="bulk-action-bar"
            :visible="showBulkBar"
            :totalCount="totalCount"
            :perPage="perPage"
            :isAllSelected.sync="isAllSelected"
            :moduleName="moduleName"
          />
        </div>
        <div class="bulk-form-data-creation">
          <ResourceBulkForm
            ref="resource-bulk-form"
            :form.sync="formObj"
            :moduleName="moduleName"
            moduleDisplayName="Resource Selector"
            :canShowPrimaryBtn="false"
            :canShowSecondaryBtn="false"
            :resourcePlannerList="resourcePlannerList"
            :isEdit="true"
            :lookupOptions="lookupOptions"
            @onRowChange="setModelData"
            @setFilters="props => loadResources(props)"
            @toggleSelectAll="selectAllResources"
          />
        </div>
        <div class="width100">
          <div class="pagination-container">
            <pagination
              :currentPage.sync="page"
              :total="totalCount"
              :perPage="perPage"
              class="self-center"
            ></pagination>
          </div>
        </div>
      </template>
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
import Pagination from 'pageWidgets/utils/WidgetPagination'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import { LookupWizard } from '@facilio/ui/forms'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import ResourceBulkForm from './ResourceBulkForm'
import { v4 as uuid } from 'uuid'
import { mapGetters, mapState } from 'vuex'
import {
  JOB_PLAN_SCOPE,
  getPlaceholderText,
  getCategoryFilter,
} from '../../../utils/pm-utils.js'
import BulkActionBar from 'src/newapp/list/BulkActionBar'
import cloneDeep from 'lodash/cloneDeep'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  props: ['closeDialog', 'planner', 'pmRecord', 'resourcePlaceholder'],
  components: {
    Pagination,
    Spinner,
    ResourceBulkForm,
    BulkActionBar,
    LookupWizard,
    FLookupFieldWizard,
  },
  data: () => ({
    page: 1,
    perPage: 25,
    totalCount: 0,
    isLoading: false,
    moduleName: 'pmResourcePlanner',
    resourcePlannerList: [],
    lookupOptions: { resource: [], assignedTo: [], jobPlan: [] },
    model: {},
    isDeleting: false,
    bulkAssignedTo: null,
    isAssigning: false,
    isJobPlanAssigning: false,
    showBulkActionBar: false,
    isAllSelected: false,
    selectedLookupField: null,
    canShowLookupWizard: false,
  }),

  created() {
    this.loadResources()
  },
  computed: {
    ...mapGetters(['getUser']),
    ...mapState({
      users: state => state.users,
    }),
    checkNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    formObj() {
      let { resourcePlaceholder } = this
      return {
        appId: 63,
        description: null,
        displayName: 'Resource Form',
        id: 5818,
        labelPosition: 1,
        labelPositionEnum: 'TOP',
        labelPositionVal: 'top',
        name: 'resource_form',
        orgId: 173,
        ruleFieldIds: null,
        sections: [
          {
            fields: [
              {
                allowCreate: false,
                config: null,
                configJSON: null,
                configStr: null,
                createFormName: null,
                displayName: resourcePlaceholder,
                displayType: 1,
                displayTypeEnum: 'TEXTBOX',
                displayTypeVal: 'text',
                field: {
                  columnName: 'VISITOR_PHONE',
                  completeColumnName: 'BaseVisit.resource',
                  dataType: 1,
                  dataTypeEnum: 'STRING',
                  default: true,
                  disabled: false,
                  displayName: resourcePlaceholder,
                  displayType: 'TEXTBOX',
                  displayTypeInt: 1,
                  fieldId: 1763779,
                  id: 1763779,
                  inputName: 'basevisit.resource',
                  isSystemUpdated: null,
                  mainField: false,
                  maxLength: -1,
                  modifiedTime: 0,
                  moduleId: 156242,
                  name: 'resource',
                  orgId: 173,
                  placeHolder: null,
                  regex: null,
                  required: false,
                  sequenceNumber: -1,
                  sourceBundle: 0,
                  styleClass: null,
                  tableAlias: null,
                },
                fieldId: 1763779,
                formId: 5821,
                hideField: false,
                id: 113207,
                isDisabled: false,
                lookupModuleName: null,
                name: 'resource',
                required: true,
                sequenceNumber: 1,
                showField: true,
                showFloorPlanConfig: null,
                span: 1,
                validations: {
                  maxLength: 255,
                },
                value: null,
              },
              {
                allowCreate: false,
                config: null,
                configJSON: null,
                configStr: null,
                createFormName: null,
                displayName: 'Job Plan',
                displayType: 1,
                displayTypeEnum: 'TEXTBOX',
                displayTypeVal: 'text',
                field: {
                  columnName: 'JOB_PLAN',
                  completeColumnName: 'BaseVisit.JOB_PLAN',
                  dataType: 1,
                  dataTypeEnum: 'STRING',
                  default: true,
                  disabled: false,
                  displayName: 'Job Plan',
                  displayType: 'TEXTBOX',
                  displayTypeInt: 1,
                  fieldId: 1763779,
                  id: 1763779,
                  inputName: 'basevisit.jobPlan',
                  isSystemUpdated: null,
                  mainField: false,
                  maxLength: -1,
                  modifiedTime: 0,
                  moduleId: 156242,
                  name: 'jobPlan',
                  orgId: 173,
                  placeHolder: null,
                  regex: null,
                  required: false,
                  sequenceNumber: -1,
                  sourceBundle: 0,
                  styleClass: null,
                  tableAlias: null,
                },
                fieldId: 1763779,
                formId: 5821,
                hideField: false,
                id: 113207,
                isDisabled: false,
                lookupModuleName: null,
                name: 'jobPlan',
                required: true,
                sequenceNumber: 1,
                showField: true,
                showFloorPlanConfig: null,
                span: 1,
                validations: {
                  maxLength: 255,
                },
                value: null,
              },
              {
                allowCreate: false,
                config: null,
                configJSON: null,
                configStr: null,
                createFormName: null,
                displayName: 'Assigned To',
                displayType: 1,
                displayTypeEnum: 'TEXTBOX',
                displayTypeVal: 'text',
                field: {
                  columnName: 'ASSIGNED_TO',
                  completeColumnName: 'BaseVisit.ASSIGNED_TO',
                  dataType: 1,
                  dataTypeEnum: 'STRING',
                  default: true,
                  disabled: false,
                  displayName: 'Assigned To',
                  displayType: 'TEXTBOX',
                  displayTypeInt: 1,
                  fieldId: 1763779,
                  id: 1763779,
                  inputName: 'basevisit.assignedTo',
                  isSystemUpdated: null,
                  mainField: false,
                  maxLength: -1,
                  modifiedTime: 0,
                  moduleId: 156242,
                  name: 'assignedTo',
                  orgId: 173,
                  placeHolder: null,
                  regex: null,
                  required: false,
                  sequenceNumber: -1,
                  sourceBundle: 0,
                  styleClass: null,
                  tableAlias: null,
                },
                fieldId: 1763779,
                formId: 5821,
                hideField: false,
                id: 113207,
                isDisabled: false,
                lookupModuleName: null,
                name: 'assignedTo',
                required: true,
                sequenceNumber: 1,
                showField: true,
                showFloorPlanConfig: null,
                span: 1,
                validations: {
                  maxLength: 255,
                },
                value: null,
              },
            ],
            formId: 5818,
            id: 28081,
            lookupFieldId: -1,
            lookupFieldName: null,
            name: 'visitor',
            numberOfSubFormRecords: -1,
            orgId: 173,
            sectionType: -1,
            sectionTypeEnum: null,
            sequenceNumber: 1,
            showLabel: false,
            subForm: null,
            subFormId: -1,
            subFormValue: null,
            subFormValueStr: null,
          },
        ],
        showInMobile: true,
        showInWeb: true,
        siteIds: null,
        stateFlowId: -1,
      }
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
    bulkActionFilter() {
      let { planner } = this || {}
      let { id } = planner || {}
      return { planner: { operatorId: 36, value: [`${id}`] } }
    },
    showBulkBar() {
      let { showBulkActionBar, totalCount, perPage } = this || {}
      return showBulkActionBar && totalCount > perPage
    },
    selectedResources() {
      let { model } = this || {}
      let selectedRecords = []
      Object.entries(model).forEach(([key, value]) => {
        value = value || model[key]
        if (value['#selectorField']) selectedRecords.push(value)
      })
      return selectedRecords
    },
    selectedResourceIds() {
      let { selectedResources, resourcePlannerList } = this || {}
      return selectedResources.map(resource => {
        let { uuid } = resource || {}
        let currResource = resourcePlannerList.find(
          resourceRecord => resourceRecord.uuid === uuid
        )
        return (currResource || {}).id
      })
    },
    canShowBulkActions() {
      let { selectedResources } = this || {}
      return !isEmpty(selectedResources)
    },
    totalCountPerPage() {
      let { perPage, totalCount } = this || {}
      if (totalCount > perPage) return perPage
      else return totalCount
    },
  },
  watch: {
    async page() {
      let { showBulkActionBar } = this || {}
      await this.loadResources()
      if (showBulkActionBar) {
        this.showBulkActionBar = false
        this.model = {}
      }
    },
    canShowBulkActions(val) {
      if (!val) this.showBulkActionBar = val
    },
    selectedResources(val) {
      let { totalCountPerPage, page } = this || {}
      let { length } = val || {}
      if (page === 1 && totalCountPerPage !== length) {
        this.showBulkActionBar = false
      }
    },
  },
  methods: {
    showLookupWizard(field) {
      let { lookupFields } = this
      let selectedLookupField = cloneDeep(lookupFields[`${field}`] || {})

      this.$set(this, 'selectedLookupField', selectedLookupField)
      this.$set(this, 'canShowLookupWizard', true)
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
      let selectedValue = selectedItemIds[0]
      if (lookupModuleName === 'jobplan') {
        this.setJobPlans(selectedValue)
      } else {
        this.setBulkAssignedTo(selectedValue)
      }
      this.selectedLookupField = null
      this.canShowLookupWizard = false
    },
    selectAllResources(value) {
      let { totalCount, showBulkActionBar } = this || {}
      let isSelected = !showBulkActionBar
      if (!isEmpty(value)) {
        isSelected = value
      }
      this.$refs['resource-bulk-form'].toggleSelectAll(totalCount, isSelected)
      this.$nextTick(() => {
        let bulkSelect = document.getElementsByClassName('bulk-select-checkbox')
        let bulkCheckbox = bulkSelect[1]
        bulkCheckbox.checked = isSelected
      })
      this.showBulkActionBar = isSelected
    },
    getFormattedText(text, isUpperCase) {
      let { pmRecord } = this || {}

      return getPlaceholderText({ pmRecord, text, isUpperCase })
    },
    async setBulkAssignedTo(id) {
      this.isAssigning = true
      let {
        moduleName,
        selectedResourceIds,
        isAllSelected,
        resourcePlaceholder,
      } = this || {}
      let successMessage = this.$t('maintenance.pm.resource_updated', {
        resourcePlaceholder,
      })
      if (isAllSelected) {
        let { bulkActionFilter } = this || {}
        let params = {
          assignedTo: { id },
        }
        let filters = encodeURIComponent(JSON.stringify(bulkActionFilter))
        let bulkActionComponent = this.$refs['bulk-action-bar']
        let response = await bulkActionComponent.updateRecord(params, filters)
        this.handleNotification(response, successMessage)
      } else {
        let params = {
          moduleName,
          data: {
            [moduleName]: selectedResourceIds.map(selectedId => {
              return { id: selectedId, assignedTo: { id } }
            }),
          },
        }
        let response = await API.post(
          `v3/modules/bulkPatch/${moduleName}`,
          params
        )
        this.handleNotification(response, successMessage)
      }
      this.isAssigning = false
    },
    async setJobPlans(id) {
      this.isJobPlanAssigning = true
      let {
        moduleName,
        selectedResourceIds,
        isAllSelected,
        resourcePlaceholder,
      } = this || {}
      let successMessage = this.$t('maintenance.pm.resource_updated', {
        resourcePlaceholder,
      })
      if (isAllSelected) {
        let { bulkActionFilter } = this || {}
        let params = {
          jobPlan: { id },
        }
        let filters = encodeURIComponent(JSON.stringify(bulkActionFilter))
        let bulkActionComponent = this.$refs['bulk-action-bar']
        let response = await bulkActionComponent.updateRecord(params, filters)
        this.handleNotification(response, successMessage)
      } else {
        let params = {
          moduleName,
          data: {
            [moduleName]: selectedResourceIds.map(selectedId => {
              return { id: selectedId, jobPlan: { id } }
            }),
          },
        }
        let response = await API.post(
          `v3/modules/bulkPatch/${moduleName}`,
          params
        )
        this.handleNotification(response, successMessage)
      }
      this.isJobPlanAssigning = false
    },
    handleNotification(response, successMessage) {
      let { error } = response || {}
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(successMessage)
        this.loadResources()
        this.model = {}
        this.showBulkActionBar = false
      }
    },
    async deleteResources() {
      this.isDeleting = true
      let {
        selectedResourceIds,
        moduleName,
        isAllSelected,
        resourcePlaceholder,
      } = this || {}
      let successMessage = this.$t('maintenance.pm.resource_deleted', {
        resourcePlaceholder,
      })
      if (isAllSelected) {
        let { bulkActionFilter } = this || {}
        let filters = encodeURIComponent(JSON.stringify(bulkActionFilter))
        let bulkActionComponent = this.$refs['bulk-action-bar']
        let response = await bulkActionComponent.deleteRecord(filters)
        this.handleNotification(response, successMessage)
      } else {
        let response = await API.deleteRecord(moduleName, [
          ...selectedResourceIds,
        ])
        this.handleNotification(response, successMessage)
      }
      this.isDeleting = false
    },
    setModelData({ data }) {
      let { model } = this || {}
      let { uuid } = data || {}
      let modifiedData = { ...model, [uuid]: data }
      this.model = modifiedData
    },
    async loadResources(props) {
      this.isLoading = true
      let { filters } = props || {}
      let { moduleName, planner, page, perPage } = this || {}
      let { id } = planner || {}
      let params = {
        withCount: true,
        page,
        perPage,
      }
      if (!isEmpty(filters)) {
        params = { ...params, filters: JSON.stringify(filters) }
      }
      let relatedFieldName = getRelatedFieldName('pmPlanner', moduleName)
      let relatedConfig = {
        moduleName: 'pmPlanner',
        id,
        relatedModuleName: moduleName,
        relatedFieldName,
      }
      let { error, list, meta } = await API.fetchAllRelatedList(
        relatedConfig,
        params,
        { force: true }
      )
      if (isEmpty(error)) {
        let count = this.$getProperty(meta, 'pagination.totalCount')
        this.totalCount = count
        let data = list.map(resourcePlanner => {
          let { lookupOptions } = this || {}
          let { id } = resourcePlanner || {}
          let resourceId = this.$getProperty(resourcePlanner, 'resource.id')
          let resourceName = this.$getProperty(resourcePlanner, 'resource.name')
          let jobPlanName = this.$getProperty(resourcePlanner, 'jobPlan.name')
          let jobPlanId = this.$getProperty(resourcePlanner, 'jobPlan.id')
          let decommission = this.$getProperty(
            resourcePlanner,
            'resource.decommission'
          )
          let assignedToName = this.$getProperty(
            resourcePlanner,
            'assignedTo.name'
          )
          let assignedToId = this.$getProperty(resourcePlanner, 'assignedTo.id')
          let { assignedTo, resource, jobPlan } = lookupOptions || {}
          resource = [...resource, { label: resourceName, value: resourceId }]
          assignedTo = [
            ...assignedTo,
            { label: assignedToName, value: assignedToId },
          ]
          jobPlan = [...jobPlan, { label: jobPlanName, value: jobPlanId }]
          this.lookupOptions = {
            ...lookupOptions,
            assignedTo,
            resource,
            jobPlan,
          }
          return {
            assignedTo: assignedToName,
            resource: resourceName,
            jobPlan: jobPlanName,
            decommission: isEmpty(decommission) ? false : decommission,
            uuid: uuid(),
            id,
          }
        })
        this.resourcePlannerList = data
      } else {
        this.$message.error(error.message || 'Error occured')
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss">
.planner-sheet-dialog {
  overflow: scroll;
  .el-dialog__body {
    padding: 0px;
  }
}
span:has(> div.selector-field) {
  width: 100%;
}
</style>

<style lang="scss" scoped>
.resource-icons {
  padding: 5px;
  border: solid 1px transparent;
  border-radius: 3px;
  &:hover {
    color: #615e88;
    background: #f5f6f8;
    border: 1px solid #dae0e8;
  }
}
.bulk-form-container {
  height: calc(100vh - 300px);
}
.pagination-container {
  width: 100%;
  padding-top: 10px;
  padding-bottom: 10px;
  background-color: #f4f4f4;
  color: #8f8f8f;
  font-size: 13px;
  text-transform: uppercase;
  font-weight: 500;
  display: flex;
  justify-content: center;
}
.lookup-wizard-title {
  font-size: 14px;
  font-weight: 700;
  color: rgb(50, 64, 86);
  text-transform: uppercase;
  line-height: 24px;
}
.bulk-action-container {
  background-color: #f6fbff;
  border-top: solid 1px #e6ebef;
  border-bottom: solid 1px #e6ebef;
  padding: 10px 20px;
}
.bulk-action-button {
  letter-spacing: 0.3px;
  text-transform: uppercase;
  font-weight: 500;
  background-color: #fff;
  color: #324056;
  border: 1px solid #dadfe3;
  font-size: 12px;
  padding: 7px 15px 6px;
  border-radius: 2px;
  &:hover {
    background-color: #f3f5f7;
    color: #324056;
    border: 1px solid #dadfe3;
  }
}
</style>
