<template>
  <FormLayout
    :subHeadings="subHeading"
    :breadcrumbHeadings="breadcrumb"
    :title="title"
    :isLoading="loading"
    :isSaving="isSaving"
    @onSave="saveView"
    @onCancel="onCancel"
    class="view-creation-form"
  >
    <template #spinner-section>
      <spinner :show="loading" size="80"></spinner>
    </template>
    <template #naming-section>
      <Naming
        ref="naming-section"
        :saveAsNew="saveAsNew"
        :viewDetail="viewDetail"
        :isViewClone="isViewClone"
        :appId="appId"
        :moduleName="moduleName"
        :isLoading="viewGrpLoading"
      />
    </template>
    <template #sorting-header-section>
      <el-switch
        v-model="isSortingEnable"
        active-color="rgba(57, 178, 194, 0.8)"
        inactive-color="#e5e5e5"
        class="pL20"
      ></el-switch>
    </template>
    <template #sorting-section>
      <Sorting
        v-if="isSortingEnable"
        ref="sorting-section"
        :moduleName="moduleName"
        :viewDetail="viewDetail"
      />
    </template>
    <template #custcolm-section>
      <VisualizationType
        ref="visualization-section"
        :viewDetail="viewDetail"
        :saveAsNew="saveAsNew"
        :isNewView="isNewView"
        :moduleName="moduleName"
        :canShowCalendarView="canShowCalendarView"
      ></VisualizationType>
    </template>
    <template v-if="!saveAsNew && !defaultView" #specific-section>
      <div class="fc-sub-title-desc">
        {{
          $t(
            'viewsmanager.schedules.specify_criteria_for_this_view_to_teams_and_users'
          )
        }}
      </div>
      <CriteriaBuilder
        ref="criteria-builder"
        v-model="viewCriteria"
        :moduleName="moduleName"
        :disabled="defaultView"
        :isOneLevelEnabled="true"
      />
    </template>
    <template #filter-section v-else>
      <div class="view-tags">
        <template v-if="!$validation.isEmpty(viewDetail.filters)">
          <div class="fc-grey-text-input-label mB10">
            {{ $t('filters.views.existing_filters') }}
          </div>
          <FTags
            class="mB10"
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
            :moduleName="moduleName"
            :key="`ftags-new-${moduleName}`"
            :hideActionBtn="true"
            :hideClose="true"
          ></FTags
        ></template>
      </div>
    </template>
    <template #share-section>
      <ShareWith
        ref="sharing-section"
        :isNewView="isNewView"
        :saveAsNew="saveAsNew"
        :viewDetail="viewDetail"
        :appId="appId"
      />
    </template>
  </FormLayout>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { FormLayout } from '@facilio/ui/setup'
import Naming from './components/Naming.vue'
import VisualizationType from './components/VisualizationType.vue'
import Sorting from './components/Sorting.vue'
import { CriteriaBuilder } from '@facilio/criteria'
import ShareWith from './components/ShareWith.vue'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from './routeUtil'
import { calendarSupportModules } from './calendarSupportUtil'

export default {
  props: ['moduleName'],
  components: {
    Naming,
    FormLayout,
    VisualizationType,
    CriteriaBuilder,
    ShareWith,
    Sorting,
    FTags: () => import('newapp/components/search/FTags'),
  },
  data() {
    return {
      viewDetail: null,
      isSaving: false,
      loading: true,
      viewCriteria: null,
      breadcrumb: [],
      viewGrpLoading: false,
      isSortingEnable: false,
      appList: [],
      subHeading: [
        {
          displayName: this.$t('viewsmanager.naming.heading_name'),
          id: 'naming',
        },
        {
          displayName: this.$t('viewsmanager.sharing_permission.heading_name'),
          id: 'share',
        },
      ],
    }
  },
  async created() {
    await this.getAppList()
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.loadGroupViewsList()
    this.constructBreadCrumb()
    if (this.viewname) {
      await this.loadViewDetails()
      if (!this.canShowCalendarView) this.computeSort()
    }
    this.constructSideBarList()

    this.loading = false
  },
  computed: {
    ...mapState({ metaInfo: state => state.view.metaInfo }),
    viewname() {
      return this.$attrs?.viewname || this.$route?.query.viewname || null
    },
    isNewView() {
      return isEmpty(this.$attrs?.viewname) || this.isViewClone
    },
    appId() {
      let { viewCloneAppId, currentAppId } = this
      return viewCloneAppId || currentAppId
    },
    currentAppId() {
      let currentAppId = this.$getProperty(this.$route, 'query.appId', null)
      return currentAppId
    },
    title() {
      let { viewname } = this
      return isEmpty(viewname)
        ? this.$t('viewsmanager.list.add_view')
        : this.isViewClone
        ? this.$t('viewsmanager.list.clone_view')
        : this.$t('viewsmanager.list.edit_view')
    },
    appliedFilters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      return !isEmpty(search) ? JSON.parse(this.$route.query.search) : null
    },
    saveAsNew() {
      let { query } = this.$route
      let { saveAsNew } = query || {}
      return saveAsNew
    },
    defaultView() {
      let { viewDetail } = this
      return viewDetail?.default
    },
    isViewClone() {
      let { viewCloneAppId, viewname } = this
      return viewCloneAppId && viewname
    },
    viewCloneAppId() {
      return this.$getProperty(this.$route, 'query.viewCloneAppId') || null
    },
    isCustom() {
      let { module: moduleObj } = this.metaInfo || {}
      let { custom } = moduleObj || {}
      return custom
    },
    canShowCalendarView() {
      let isFeaturedApp = (this.appList || []).some(
        app => app?.id == this.appId
      )
      return (
        (calendarSupportModules.includes(this.moduleName) || this.isCustom) &&
        this.$helpers.isLicenseEnabled('CALENDAR_VIEW') &&
        isFeaturedApp
      )
    },
  },
  methods: {
    ...mapActions({
      saveNewView: 'view/saveNewView',
      editView: 'view/editView',
      loadModuleMeta: 'view/loadModuleMeta',
    }),
    async getAppList() {
      let { data, error } = await API.get(
        `v2/application/fetchList?moduleName=${this.moduleName}`
      )
      if (!error)
        this.appList = (data.application || []).filter(
          app =>
            app.appCategoryEnum === 'FEATURE_GROUPING' ||
            (app.appCategoryEnum === 'WORK_CENTERS' &&
              app.name === 'Maintenance')
        )
    },
    computeSort() {
      let { viewDetail } = this
      let { sortFields } = viewDetail || {}
      this.isSortingEnable = !isEmpty(sortFields)
    },
    async loadViewDetails() {
      let { viewname, moduleName, currentAppId: appId } = this
      let url = `/v2/views/${viewname}`
      let params = { moduleName, appId, fromBuilder: true }
      let { data, error } = await API.get(url, params)

      if (!error) {
        this.viewDetail = data.viewDetail || {}
        this.viewCriteria = this.viewDetail.criteria
        if (this.isViewClone) {
          this.viewDetail = {
            ...this.viewDetail,
            groupId: null,
            name: null,
            id: -1,
          }
        }
      }
    },
    async loadGroupViewsList() {
      let { moduleName, appId } = this
      let data = {
        moduleName,
        appId,
        restrictPermissions: true,
      }

      this.viewGrpLoading = true
      await this.$store.dispatch('view/loadGroupViews', data)
      this.viewGrpLoading = false
    },
    constructBreadCrumb() {
      let moduleRoute = this.getModuleListRoute()

      if (moduleRoute) {
        this.breadcrumb.push({
          name: this.$t('viewsmanager.list.module_list'),
          route: moduleRoute,
        })
      }

      if (!this.saveAsNew || !this.isViewClone) {
        let viewManagerRoute = this.getViewManagerRoute()

        if (viewManagerRoute) {
          this.breadcrumb.push({
            name: this.$t('viewsmanager.list.manage_views'),
            route: viewManagerRoute,
          })
        }
      }
      let name = this.isViewClone
        ? `${this.$t('viewsmanager.list.clone_view')}`
        : !this.isNewView
        ? `${this.$t('viewsmanager.list.edit_view')}`
        : `${this.$t('viewsmanager.list.add_view')}`
      this.breadcrumb.push({ name })
    },
    constructSideBarList() {
      if (this.canShowCalendarView) {
        this.subHeading.splice(2, 0, {
          displayName: this.$t('viewsmanager.visualization_type.heading_name'),
          id: 'custcolm',
        })
      } else {
        // when calendar license is disabled following old flow
        this.subHeading.splice(
          2,
          0,
          {
            displayName: this.$t('viewsmanager.customize.heading_name'),
            id: 'custcolm',
          },
          {
            displayName: this.$t('viewsmanager.sorting.heading_name'),
            id: 'sorting',
          }
        )
      }
      if (this.saveAsNew) {
        // splicing criteria and replaced by filter object
        this.subHeading.splice(1, 0, {
          displayName: 'Filters',
          id: 'filter',
        })
      }
      if (!this.defaultView) {
        this.subHeading.splice(1, 0, {
          displayName: this.$t('viewsmanager.criteria.heading_name'),
          id: 'specific',
        })
      }
    },
    getModuleListRoute() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        if (name) return { name }
      } else {
        let route = findRouterForModuleInApp(moduleName, pageTypes.LIST) || {}
        if (route) return { ...route, params: { moduleName } }
      }
      return null
    },
    getViewManagerRoute() {
      let { moduleName, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.VIEW_MANAGER)
        if (name) return { name, query }
      } else {
        let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
        if (route) return { ...route, query }
      }
      return null
    },
    async saveView() {
      let isValid = await this.validateViewData()
      if (!isValid) return

      let payload = this.constructViewData()

      this.isSaving = true
      if (this.isNewView) {
        this.saveNewView(payload)
          .then(payload => {
            let { name: viewname } = payload || {}
            let moduleRoute = this.getModuleListRoute()

            this.$message.success('View created successfully!')
            if (this.saveAsNew && moduleRoute) {
              let { name, params = {} } = moduleRoute || {}
              this.$router.replace({
                name,
                params: { ...params, viewname },
                query: {},
              })
            } else {
              this.onCancel()
            }
          })
          .catch(error => {
            if (error?.message === 'Name already taken') {
              this.$message.error('View name already taken in this app!')
            } else {
              this.$message.error('View creation failed!')
            }
          })
          .finally(() => {
            this.isSaving = false
          })
      } else {
        this.editView(payload)
          .then(() => {
            this.$message.success(this.$t('filters.views.edit_success'))
            this.onCancel()
          })
          .catch(() => {
            this.$message.error('View edited failed!')
          })
          .finally(() => {
            this.isSaving = false
          })
      }
    },
    async validateViewData() {
      let namingValidate = await this.$refs['naming-section'].validate()
      let visualValidate = await this.$refs['visualization-section'].validate()
      let criteriaValidate = !isEmpty(this.$refs['criteria-builder'])
        ? this.$refs['criteria-builder']?.validate()
        : true

      return namingValidate && criteriaValidate && visualValidate
    },
    constructViewData() {
      let {
        appId,
        viewname,
        appliedFilters,
        saveAsNew,
        viewCriteria: criteria,
        moduleName,
        canShowCalendarView,
      } = this

      let { visualization, sortConfigData } = this.$refs[
        'visualization-section'
      ].serializeData()
      let naming = this.$refs['naming-section'].serializeData()
      let sharingData = this.$refs['sharing-section'].serializeData()

      if (!canShowCalendarView) {
        let sortComponent = this.$refs['sorting-section']
        sortConfigData = { orderBy: null, orderType: null }
        if (!isEmpty(sortComponent)) {
          sortConfigData = sortComponent.serializeData()
        }
      }

      let paramObj = { moduleName, ...sortConfigData }
      let viewObj = {
        ...naming,
        ...sharingData,
        ...visualization,
        appId,
      }

      if (saveAsNew) {
        viewObj = {
          ...viewObj,
          includeParentCriteria: true,
          filtersJson: JSON.stringify(appliedFilters || {}),
        }
        paramObj = { ...paramObj, parentView: viewname }
      } else if (!isEmpty(criteria) && !this.defaultView) {
        viewObj = { ...viewObj, criteria }
      }

      return { ...paramObj, view: viewObj }
    },
    onCancel() {
      let { appId, moduleName } = this
      let route = this.getViewManagerRoute()
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { appId },
        })
    },
  },
}
</script>
<style lang="scss" scoped>
.view-creation-form {
  padding-left: 0px !important;
}
</style>
