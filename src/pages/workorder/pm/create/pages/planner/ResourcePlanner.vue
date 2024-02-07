<template>
  <div class="mT5">
    <div v-if="isLoading" class="d-flex mT50 planner-body">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <template v-else>
      <div class="section-sub-heading">
        {{ `${resourcePlaceholder} ${$t('maintenance.pm.resource_planner')}` }}
      </div>
      <div class="flex flex-no-wrap mT10 justify-between">
        <div class="section-description width60">
          {{ getFormattedText($t('maintenance.pm.resource_planner_desc')) }}
        </div>
        <el-button class="add-config-btn" @click="setSelectorField">{{
          getFormattedText($t('maintenance.pm.add_resources'), true)
        }}</el-button>
      </div>
      <div v-if="isConfigured" class="resource-planner-section">
        <div class="planner-text text-center width70 mT5">
          {{ heading }}
        </div>
        <div class="mT10 mB10">
          <div class="add-link" @click="openResourceSheet">
            {{ getFormattedText($t('maintenance.pm.view_resources'), true) }}
          </div>
        </div>
        <div class="width40 text-center" v-if="!isConfigured">
          {{ getFormattedText($t('maintenance.pm.resource_section_desc')) }}
        </div>
      </div>
      <ResourceSelector
        v-if="showResourceSelector"
        :canShowLookupWizard.sync="showResourceSelector"
        :field="selectorField"
        @setLookupFieldValue="setResourceSelection"
        @closeWizard="closeWizard"
        :planner="planner"
        :pmRecord="pmRecord"
      />
      <PlannerFieldSelector
        v-if="canShowFieldSelector"
        :closeDialog="() => (canShowFieldSelector = false)"
        :bulkCreateResources="bulkCreateResources"
        :reloadPlanner="loadResourcePlanner"
        :pmRecord="pmRecord"
        :isBulkSelect="isBulkSelect"
        :totalCount="totalCount"
      />
      <PlannerSheet
        v-if="showPlannerSheet"
        :closeDialog="closePlannerSheet"
        :planner="planner"
        :pmRecord="pmRecord"
        :resourcePlaceholder="resourcePlaceholder"
      />
    </template>
  </div>
</template>

<script>
import ResourceSelector from './ResourceSelector'
import PlannerFieldSelector from './PlannerFieldSelector'
import PlannerSheet from './planner-sheet/PlannerSheet'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import {
  getResourcePlaceholder,
  getPlaceholderText,
} from '../../utils/pm-utils.js'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

const ASSIGNMENT_HASH = {
  ASSETS: 'asset',
  SPACES: 'space',
  ASSETCATEGORY: 'asset',
  SPACECATEGORY: 'space',
  SITES: 'site',
  BUILDINGS: 'building',
  FLOORS: 'floor',
}
export default {
  props: ['pmRecord', 'planner'],
  components: { ResourceSelector, PlannerFieldSelector, PlannerSheet, Spinner },
  created() {
    this.loadResourcePlanner()
  },
  data: () => ({
    showResourceSelector: false,
    selectorField: {},
    canShowFieldSelector: false,
    selectedResources: [],
    selectedResourceFilter: {},
    isBulkSelect: false,
    moduleName: 'pmResourcePlanner',
    resourcesCount: 0,
    showPlannerSheet: false,
    isLoading: false,
    jpCount: 0,
    assigneeCount: 0,
    totalCount: 0,
  }),
  computed: {
    isConfigured() {
      let { resourcesCount } = this || {}
      return resourcesCount > 0
    },
    heading() {
      let { resourcesCount } = this || {}

      return `${this.$t(
        'maintenance.pm.resources_conf_1'
      )} ${resourcesCount} ${this.getFormattedPlannerText(
        this.$t('maintenance.pm.resources_conf_2')
      )}`
    },
    resourcePlaceholder() {
      let { pmRecord } = this || {}
      return getResourcePlaceholder(pmRecord, true)
    },
  },
  methods: {
    closePlannerSheet() {
      this.showPlannerSheet = false
      this.loadResourcePlanner()
    },
    async loadResourcePlanner() {
      this.isLoading = true
      let { moduleName, planner } = this || {}
      let { id } = planner || {}
      let params = {
        withCount: true,
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
        params
      )
      if (isEmpty(error)) {
        let count = this.$getProperty(meta, 'pagination.totalCount')
        this.resourcesCount = count
        await this.computeResourceAllotments(list)
      } else {
        this.$message.error(error.message || 'Error occured')
      }
      this.isLoading = false
    },
    computeResourceAllotments(resources) {
      if (!isEmpty(resources)) {
        let jpMap = []
        let assigneeMap = []

        jpMap = resources.map(resource => {
          let { jobPlan } = resource || {}
          let { id: jobPlanId } = jobPlan || {}
          if (!isEmpty(jobPlanId)) {
            return jobPlanId
          }
          return false
        })
        assigneeMap = resources.map(resource => {
          let { assignedTo } = resource || {}
          let { id: assignedToId } = assignedTo || {}
          if (!isEmpty(assignedToId)) {
            return assignedToId
          }
          return false
        })
        jpMap = jpMap.filter(jp => {
          return jp
        })
        assigneeMap = assigneeMap.filter(assignee => {
          return assignee
        })

        jpMap = [...new Set(jpMap)]
        assigneeMap = [...new Set(assigneeMap)]
        this.jpCount = jpMap.length
        this.assigneeCount = assigneeMap.length
      }
    },
    setSelectorField() {
      let { pmRecord } = this || {}
      let { assignmentTypeEnum } = pmRecord
      let name = ASSIGNMENT_HASH[assignmentTypeEnum] || 'site'
      let sites = this.$getProperty(this, 'pmRecord.sites')
      sites = sites.map(site => `${site.id}`)
      let filter = {
        site: { operatorId: 36, value: sites },
      }
      if (assignmentTypeEnum === 'SPACECATEGORY') {
        let spaceCategoryId = this.$getProperty(pmRecord, 'spaceCategory.id')
        filter = {
          ...filter,
          spaceCategory: { operatorId: 36, value: [`${spaceCategoryId}`] },
        }
      } else if (assignmentTypeEnum === 'ASSETCATEGORY') {
        let assetCategoryId = this.$getProperty(pmRecord, 'assetCategory.id')
        filter = {
          ...filter,
          category: { operatorId: 36, value: [`${assetCategoryId}`] },
        }
      }
      this.selectorField = {
        field: { lookupModule: { name } },
        multiple: true,
        config: {},
        filters: filter,
      }
      this.showResourceSelector = true
    },
    openResourceSheet() {
      this.showPlannerSheet = true
    },
    setResourceSelection(data) {
      this.showResourceSelector = false
      let { field, isBulkSelect, totalCount } = data || {}
      let filters = this.$getProperty(data, 'filters', {})
      if (isBulkSelect) {
        this.isBulkSelect = isBulkSelect
        let fieldFilters = this.$getProperty(field, 'filters', {})
        this.selectedResourceFilter = { ...filters, ...fieldFilters }
        this.selectedResources = []
        this.totalCount = totalCount
      } else {
        let { selectedItems } = field || {}
        this.selectedResources = selectedItems
        this.isBulkSelect = false
        this.selectedResourceFilter = {}
      }
      this.$nextTick(() => {
        this.canShowFieldSelector = true
      })
    },
    closeWizard() {
      this.showResourceSelector = false
    },
    getFormattedPlannerText(text) {
      let { assigneeCount, jpCount } = this
      if (jpCount === 1 && assigneeCount === 1) {
        text = this.$t('maintenance.pm.default_planner_msg')
      }
      if (jpCount > 1) {
        text = text.replace(
          'default Job Plan',
          this.$t('maintenance.pm.multiple_jp')
        )
      }
      if (assigneeCount === 1) {
        text = text.replace(
          'multiple Assignees',
          this.$t('maintenance.pm.default_assign')
        )
      }
      if (jpCount === 0 && assigneeCount === 0) {
        text = text.replace(
          ' with a default Job Plan & multiple Assignees.',
          '.'
        )
      } else {
        if (jpCount === 0) {
          text = text.replace('a default Job Plan &', '')
        }
        if (assigneeCount === 0) {
          text = text.replace(' & multiple Assignees.', '.')
        }
      }

      text = this.getFormattedText(text)
      return text
    },
    async bulkCreateResources(associations) {
      let { assignedTo, jobPlan } = associations || {}
      let {
        selectedResources,
        selectedResourceFilter,
        isBulkSelect,
        moduleName,
        pmRecord,
        planner,
        selectorField,
      } = this || {}
      let { id: pmId } = pmRecord || {}
      let { id: plannerId } = planner || {}

      let url = ''
      let params = {}

      if (isBulkSelect) {
        url = 'v3/bulkAction/create'
        if (!isEmpty(selectedResourceFilter)) {
          selectedResourceFilter = encodeURIComponent(
            JSON.stringify(selectedResourceFilter)
          )
          selectedResourceFilter = url += `?filters=${selectedResourceFilter}`
        }
        let scopeModuleName = this.$getProperty(
          selectorField,
          'field.lookupModule.name'
        )
        params = {
          moduleName: scopeModuleName,
          dataModuleName: moduleName,
          data: {
            assignedTo: { id: assignedTo },
            jobPlan: { id: jobPlan },
            pmId,
            planner: { id: plannerId },
          },
        }
      } else {
        selectedResources = selectedResources.map(resources => {
          let { value } = resources || {}
          return {
            resource: { id: value },
            assignedTo: { id: assignedTo },
            jobPlan: { id: jobPlan },
            pmId,
            planner: { id: plannerId },
          }
        })

        url = `v3/modules/bulkCreate/${moduleName}`
        params = {
          data: { [moduleName]: selectedResources },
          moduleName,
        }
      }

      let response = await API.post(url, params)

      return response
    },
    getFormattedText(text, isUpperCase) {
      let { pmRecord, resourcesCount } = this || {}

      return getPlaceholderText({
        pmRecord,
        text,
        isUpperCase,
        isSingular: resourcesCount === 1,
      })
    },
  },
}
</script>

<style lang="scss">
.new-add-triger-dialog .el-dialog__body {
  padding: 0;
}
.new-add-triger-dialog .el-dialog {
  width: 70% !important;
}
.trigger-dialog-body {
  height: 100%;
  max-height: 600px;
  overflow: hidden;
}
.resource-planner-section {
  width: 100%;
  background-color: #f5f5f5;
  padding: 25px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0px 15px;
}
.planner-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.14;
  letter-spacing: 0.5px;
  color: #324056;
}
.add-button {
  width: 100%;
  border-radius: 3px;
  padding: 12px 30px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: center;
  box-shadow: 0 2px 4px 0 rgb(230 230 230 / 50%) !important;
  border: solid 1px #39b2c2;
  color: #ffffff;
  background-color: #39b2c2;
  margin-top: 10px;
  &:hover {
    border: solid 1px #39b2c2;
    background: #3cbfd0 !important;
    color: #fff;
  }
}
.add-link {
  font-size: 13px;
  color: #343f9c;
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
.add-config-btn {
  margin-top: -25px;
  height: 40px;
  width: 120px;
  &:hover {
    border: solid 1px #39b2c2;
    background: #fff;
    color: #39b2c2;
  }
}
</style>
