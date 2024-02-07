<template>
  <div>
    <FormLayout
      :isLoading="isLoading"
      :subHeadings="subHeadings"
      :breadcrumbHeadings="breadcrumb"
      :title="title"
      :saveAction="save"
      :isSaving="isSaving"
      @onCancel="goBack"
      class="form-layout"
    >
      <template #spinner-section>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
      <template #naming-section>
        <NamingDetails
          ref="details-section"
          :viewDetail="viewDetails"
          :moduleName="moduleName"
          :saveAsNew="saveAsNew"
          :appId="appId"
          :isLoading="viewGrpLoading"
        ></NamingDetails>
      </template>
      <template #criteria-section>
        <div class="fc-sub-title-desc">
          {{ $t('setup.scheduler.specify_criteria_for_records') }}
        </div>
        <el-form>
          <el-form-item class="mB0">
            <CriteriaBuilder
              v-model="criteria"
              :moduleName="moduleName"
              ref="criteria-builder"
              :isOneLevelEnabled="true"
            />
          </el-form-item>
        </el-form>
      </template>
      <template #filter-section>
        <div class="view-tags">
          <div class="wo-save-subH text-uppercase">
            {{ $t('maintenance.pm_list.filters') }}
          </div>
          <template v-if="!$validation.isEmpty(filters)">
            <div class="fc-grey-text-input-label mB10">
              {{ $t('filters.views.existing_filters') }}
            </div>
            <FTags
              class="mB10  timeline-ftag-border-less "
              :showOnlyExisting="true"
              :hideActionBtn="true"
              :hideClose="true"
              :key="`ftags-existing-${moduleName}`"
            ></FTags>
          </template>
          <template v-if="!$validation.isEmpty(appliedFilters)"
            ><div class="fc-grey-text-input-label mB10">
              {{ $t('filters.views.new_filters') }}
            </div>
            <FTags
              class="timeline-ftag-border-less "
              :moduleName="moduleName"
              :key="`ftags-new-${moduleName}`"
              :hideActionBtn="true"
              :hideClose="true"
            ></FTags
          ></template>
        </div>
      </template>
      <template #grouping-section>
        <GroupingDetails
          ref="grouping-section"
          :viewDetails="viewDetails"
          :isNew="isNew"
          :moduleId="moduleId"
          :saveAsNew="saveAsNew"
          :moduleFields="moduleFields"
          class="mB10"
        ></GroupingDetails>
      </template>
      <template #calender-section>
        <CalenderDetails
          ref="calender-section"
          :viewDetails="viewDetails"
          :isNew="isNew"
          :saveAsNew="saveAsNew"
          :moduleFields="moduleFields"
          :weekendsList="weekendsList"
          :updateWeekEnd="updateWeekEnd"
          class="mB10"
        ></CalenderDetails>
      </template>
      <template #events-section>
        <ActionEventsConfig
          ref="events-section"
          :viewDetails="viewDetails"
          :isNew="isNew"
          :saveAsNew="saveAsNew"
          class="mB10"
        ></ActionEventsConfig>
      </template>
      <template #popup-section>
        <PopupConfig
          ref="popup-section"
          :viewDetails="viewDetails"
          :isNew="isNew"
          :saveAsNew="saveAsNew"
          :moduleFields="moduleFields"
          class="mB10"
        ></PopupConfig>
      </template>
      <template #color-section>
        <ColorCustomization
          ref="color-section"
          :viewDetails="viewDetails"
          :isNew="isNew"
          :moduleName="moduleName"
          :saveAsNew="saveAsNew"
          :moduleFields="moduleFields"
          :moduleId="moduleId"
        ></ColorCustomization>
      </template>
      <template #share-section>
        <ShareWith
          ref="sharing-section"
          :isNewView="isNew"
          :saveAsNew="saveAsNew"
          :viewDetail="viewDetails"
          :appId="appId"
        />
      </template>
      <div class="mT50"></div>
    </FormLayout>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { mapActions } from 'vuex'
import { findRouteForTab, pageTypes, isWebTabsEnabled } from '@facilio/router'
import { FormLayout } from '@facilio/ui/setup'
import { CriteriaBuilder } from '@facilio/criteria'
import NamingDetails from './view-creation/NamingDetails.vue'
import GroupingDetails from './view-creation/GroupingDetails.vue'
import CalenderDetails from './view-creation/CalenderDetails.vue'
import ActionEventsConfig from './view-creation/ActionEventsConfig.vue'
import PopupConfig from './view-creation/PopupConfig.vue'
import ColorCustomization from './view-creation/ColorCustomization.vue'
import ShareWith from 'src/newapp/viewmanager/components/ShareWith.vue'

export default {
  name: 'TimelineViewForm',
  props: ['moduleName'],
  components: {
    NamingDetails,
    GroupingDetails,
    CalenderDetails,
    ActionEventsConfig,
    PopupConfig,
    ColorCustomization,
    FormLayout,
    CriteriaBuilder,
    FTags: () => import('src/newapp/timeline-view/components/FTags'),
    ShareWith,
  },
  data() {
    return {
      moduleId: null,
      isLoading: true,
      moduleFields: [],
      weekendsList: [],
      criteria: null,
      filters: null,
      isSaving: false,
      viewDetails: null,
      viewGrpLoading: false,
      breadcrumb: [],
      subHeadings: [
        { displayName: 'Naming', id: 'naming' },
        { displayName: 'Grouping', id: 'grouping' },
        { displayName: 'Calender', id: 'calender' },
        { displayName: this.$t('common.header.events'), id: 'events' },
        { displayName: 'Popup', id: 'popup' },
        { displayName: 'Color', id: 'color' },
        {
          displayName: this.$t('viewsmanager.sharing_permission.heading_name'),
          id: 'share',
        },
      ],
    }
  },
  async created() {
    this.generatePath()
    this.loadGroupViewsList()
    await this.fetchCurrentView()
    await this.fetchMetaFields()
    await this.loadWeekendList()
    this.deserializeCriteria()
    this.isLoading = false
  },
  computed: {
    viewname() {
      return this.$attrs?.viewname || this.$route?.query.viewname || null
    },
    viewCloneAppId() {
      return this.$getProperty(this.$route, 'query.viewCloneAppId') || null
    },
    isViewClone() {
      let { viewCloneAppId, viewname } = this
      return viewCloneAppId && viewname
    },
    appliedFilters() {
      let { query } = this.$route
      let { search } = query || {}

      return !isEmpty(search) ? JSON.parse(search) : null
    },
    isNew() {
      let { viewname, saveAsNew = false } = this
      return isEmpty(viewname) || saveAsNew
    },
    appId() {
      let { viewCloneAppId, currentAppId } = this
      return viewCloneAppId || currentAppId
    },
    currentAppId() {
      let currentAppId = this.$getProperty(this.$route, 'query.appId', null)
      return currentAppId
    },
    saveAsNew() {
      let { query } = this.$route
      let { saveAsNew } = query || {}
      return saveAsNew
    },
    title() {
      return this.isNew
        ? this.$t('setup.scheduler.new_timeline_view')
        : this.isViewClone
        ? this.$t('viewsmanager.list.clone_view')
        : this.$t('setup.scheduler.edit_timeline_view')
    },
  },
  methods: {
    ...mapActions({
      saveNewView: 'view/saveNewView',
      editView: 'view/editView',
    }),
    async loadGroupViewsList() {
      let { moduleName, appId } = this
      let data = {
        moduleName,
        appId,
        restrictPermissions: true,
        groupType: 2,
        viewType: 2,
      }

      this.viewGrpLoading = true
      await this.$store.dispatch('view/loadGroupViews', data)
      this.viewGrpLoading = false
    },
    async fetchCurrentView() {
      let { viewname, moduleName, currentAppId: appId, isViewClone } = this

      let url = `/v2/views/${viewname}`
      let params = { moduleName, appId }
      let { data, error } = await API.get(url, params)

      if (!error) {
        let { viewDetail } = data || {}
        this.viewDetails = viewDetail
        if (isViewClone && !isEmpty(viewDetail)) {
          this.viewDetails = {
            ...this.viewDetails,
            groupId: null,
            name: null,
            id: -1,
          }
        }
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
    },
    async fetchMetaFields() {
      let { moduleName } = this

      if (!isEmpty(moduleName)) {
        let { data, error } = await API.get('/module/metafields', {
          moduleName,
        })

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let fields = this.$getProperty(data, 'meta.fields', []) || []
          this.moduleId = this.$getProperty(data, 'meta.module.moduleId', null)
          this.moduleFields =
            fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
        }
      }
    },
    async loadWeekendList() {
      let { error, data } = await API.get('v2/weekends/list')

      if (error) {
        this.$message.error(
          error.message ||
            this.$t('setup.scheduler.failed_to_fetch_weekends_list')
        )
      } else {
        this.weekendsList = data?.weekends || []
      }
    },
    updateWeekEnd(weekendObj, canDelete) {
      let { id, name, value } = weekendObj || {}
      let index = this.weekendsList.findIndex(w => w.id === id)

      if (!isEmpty(index)) {
        if (!canDelete) {
          this.weekendsList.splice(index, 1, weekendObj)
        } else {
          this.weekendsList.splice(index, 1)
        }
      } else {
        this.weekendsList.push({ id, name, value })
      }
    },
    async validate() {
      let detailsValidate = await this.$refs['details-section'].validate()
      let groupValidate = await this.$refs['grouping-section'].validate()
      let calenderValidate = await this.$refs['calender-section'].validate()
      let criteriaValidate = !isEmpty(this.$refs['criteria-builder'])
        ? this.$refs['criteria-builder']?.validate()
        : true

      return (
        detailsValidate && groupValidate && calenderValidate && criteriaValidate
      )
    },
    getViewManagerRoute() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name: manageViewName } = findRouteForTab(
          pageTypes.TIMELINE_VIEW_MANAGER,
          {
            moduleName,
          }
        )
        return !isEmpty(manageViewName) ? { name: manageViewName } : {}
      } else {
        return { name: 'resource-scheduler-viewmanager' }
      }
    },
    generatePath() {
      let { moduleName, saveAsNew } = this
      let resourceSchedulerPath = {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.TIMELINE_LIST, { moduleName })
        resourceSchedulerPath = !isEmpty(name) ? { name } : {}
      } else {
        resourceSchedulerPath = { path: '/app/wo/timeline' }
      }
      this.breadcrumb = [
        {
          name: 'Resource Scheduler',
          route: resourceSchedulerPath,
        },
        { name: this.title },
      ]

      let manageViewBC = {
        name: 'Manage views',
        route: this.getViewManagerRoute(),
      }
      let criteriaHeading = { displayName: 'Criteria', id: 'criteria' }
      let filterHeading = { displayName: 'Filter', id: 'filter' }

      if (saveAsNew) {
        this.subHeadings.splice(1, 0, filterHeading)
      } else {
        this.subHeadings.splice(1, 0, criteriaHeading)
        this.breadcrumb.splice(1, 0, manageViewBC)
      }
    },
    async save() {
      let isValid = await this.validate()
      if (!isValid) return

      this.isSaving = true

      let { isNew, saveAsNew, moduleName, criteria, appId, isViewClone } = this
      let filterObj = {}

      if (this.saveAsNew) {
        filterObj = { filtersJson: JSON.stringify(this.appliedFilters || {}) }
      }

      let sharingData = this.$refs['sharing-section'].serializeData()
      let { viewSharing, isLocked } = sharingData
      let params = {
        moduleName,
        view: {
          ...this.$refs['details-section'].serializeData(),
          ...this.$refs['grouping-section'].serialize(),
          ...this.$refs['calender-section'].serialize(),
          ...this.$refs['events-section'].serialize(),
          ...this.$refs['popup-section'].serialize(),
          ...this.$refs['color-section'].serialize(),
          ...filterObj,
          ...(!this.saveAsNew ? { criteria } : {}),
          isLocked,
          viewSharing,
          type: 2,
          appId,
        },
      }

      if (isNew || isViewClone) {
        try {
          let { name: viewname } = await this.saveNewView(params)

          this.$message.success(
            this.$t('common.wo_report.view_created_successfully')
          )
          if (saveAsNew) {
            if (isWebTabsEnabled()) {
              let { name: routeName } =
                findRouteForTab(pageTypes.TIMELINE_LIST, { moduleName }) || {}

              if (routeName) {
                this.$router.push({ name: routeName, params: { viewname } })
              }
            } else this.$router.push({ path: `/app/wo/timeline/${viewname}` })
          } else {
            this.goBack()
          }
        } catch (error) {
          let { message } = error || {}
          let errorMsg =
            message === 'Name already taken'
              ? this.$t('setup.scheduler.name_already_taken')
              : this.$t('common.wo_report.view_creation_failed')

          this.$message.error(errorMsg)
        } finally {
          this.isSaving = false
        }
      } else {
        try {
          await this.editView(params)
          this.$message.success(this.$t('filters.views.edit_success'))
          this.goBack()
        } catch (error) {
          this.$message.error(this.$t('common.wo_report.view_edited_failed'))
        } finally {
          this.isSaving = false
        }
      }
    },
    goBack() {
      let { appId, moduleName } = this
      let route = this.getViewManagerRoute()
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { appId },
        })
    },
    deserializeCriteria() {
      let { viewDetails } = this
      if (!this.isNew || this.saveAsNew) {
        let { criteria, filters } = viewDetails || {}
        this.criteria = criteria
        this.filters = filters
      }
    },
  },
}
</script>
<style lang="scss">
.timeline-view-form-container {
  border-left: 1px solid #e3e7ed;
  margin-left: 60px;
  margin-top: 50px;
  padding-left: 0px !important;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }

  .sla-sidebar {
    background-color: #fff;
    min-width: 300px;
    height: 100vh;
    margin-right: 20px;
  }

  .scroll-container {
    flex-grow: 1;
    margin: 20px 20px 0 0;
    overflow-y: scroll;
    max-height: calc(100vh - 150px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }

  .asset-el-btn {
    height: 40px !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
  }

  .section-header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    margin: 0;
    padding: 28px 50px 20px;

    &.anchor-top {
      position: sticky;
      top: 0;
      width: 100%;
      background: #fff;
      z-index: 2;
      box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
    }
  }

  .sla-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 40px;
    margin: 0;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f3f4f7;
    }
  }

  .el-form {
    width: 95%;
    max-width: 998px;
  }

  .el-table::before {
    background: initial;
  }
  .el-table th .cell {
    letter-spacing: 1.6px;
    color: #385571;
    padding-left: 0;
  }
  .el-table--border td:first-child .cell {
    padding-left: initial;
  }
  .el-table__empty-block {
    border-bottom: 1px solid #ebeef5;
  }
  .el-table__row .actions {
    visibility: hidden;
  }
  .el-table__row:hover .actions {
    visibility: visible;
  }

  .sla-criteria .fc-modal-sub-title {
    color: #385571;
  }

  .criteria-condition-block {
    .el-select {
      background-color: #fff;
    }
  }

  .fc-input-label-txt.txt-color,
  .txt-color {
    color: #324056;
  }
  .configure-blue {
    color: #6171db;
  }
  .configured-green {
    color: #5bc293;
  }

  .delete-icon {
    color: #ec7c7c;
    font-size: 16px;
    cursor: pointer;
    margin-left: 10px;
  }
}
.timeline-ftag-border-less {
  box-shadow: none !important;
  border: none !important;
}
.app-info-icon {
  font-size: 14px;
  opacity: 0.3;
}
</style>
