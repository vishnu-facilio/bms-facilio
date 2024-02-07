<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <Spinner :show="showLoading" size="80"></Spinner>
    </div>
    <div v-else class="flex flex-row">
      <div class="cm-side-bar-container">
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRecordId"
          :total="recordCount"
          :currentCount="(records || []).length"
        >
          <template #title>
            <el-row class="cm-sidebar-header">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail }}</div>
              </el-col>
            </el-row>
          </template>

          <template v-slot="{ record }">
            <router-link
              tag="div"
              class="cm-sidebar-list-item label-txt-black main-field-column"
              :to="redirectToOverview(record.id)"
            >
              <div
                class="f14 truncate-text"
                :title="record[mainFieldName] || '---'"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record[mainFieldName] || '---' }}
              </div>
            </router-link>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view :viewname="viewname"></router-view>
      </div>
    </div>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :key="`${moduleName}-list-layout`"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <template v-if="canShowVisualSwitch">
        <visual-type
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>
      </template>
      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        class="custom-button"
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      ></CustomButton>
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn" @click="openFailureClassForm()">
          {{ createBtnText }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12 "
        ></pagination>
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span v-if="hasPermission('EXPORT')" class="separator">|</span>

        <el-tooltip
          v-if="hasPermission('EXPORT')"
          effect="dark"
          :content="$t('common._common.export')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <div v-else-if="isEmpty(viewname)" class="cm-view-empty-state-container">
        <inline-svg
          src="svgs/no-configuration"
          class="d-flex module-view-empty-state"
          iconClass="icon"
        ></inline-svg>
        <div class="mB20 label-txt-black f14 self-center">
          {{ $t('viewsmanager.list.no_view_config') }}
        </div>
        <el-button
          type="primary"
          class="add-view-btn"
          @click="openViewCreation"
        >
          <span class="btn-label">{{ $t('viewsmanager.list.add_view') }}</span>
        </el-button>
      </div>
      <div v-else-if="isEmpty(records)" class="cm-empty-state-container">
        <img
          class="mT20 self-center"
          src="~statics/noData-light.png"
          width="100"
          height="100"
        />
        <div class="mT10 label-txt-black f14 self-center">
          {{ emptyStateText }}
        </div>
      </div>
      <template v-else>
        <div class="cm-list-container">
          <div
            class="column-customization-icon"
            :disabled="!isColumnCustomizable"
            @click="toShowColumnSettings"
          >
            <el-tooltip
              :disabled="isColumnCustomizable"
              placement="top"
              :content="$t('common._common.you_dont_have_permission')"
            >
              <inline-svg
                src="column-setting"
                class="text-center position-absolute icon"
              />
            </el-tooltip>
          </div>

          <div
            class="pull-left table-header-actions"
            v-if="!isEmpty(selectedListItemsIds)"
          >
            <div class="action-btn-slide btn-block">
              <button
                class="btn btn--tertiary pointer"
                @click="deleteRecords(selectedListItemsIds)"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
            </div>
            <CustomButton
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
              :modelDataClass="modelDataClass"
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              class="custom-button"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>
          </div>
          <CommonList
            :key="`viewname-${viewname}`"
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            @selection-change="selectItems"
            :slotList="slotList"
            canShowCustomButton="true"
            :refreshList="onCustomButtonSuccess"
          >
            <template #[slotList[0].name]="{record}">
              <div class="d-flex">
                <div class="fc-id">{{ '#' + record[slotList[0].name] }}</div>
              </div>
            </template>
            <template #[slotList[1].criteria]="{record}">
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
              >
                <div v-if="record[photoFieldName] > 0">
                  <img
                    :src="record.getImage(photoFieldName)"
                    class="img-container"
                  />
                </div>
                <el-tooltip
                  effect="dark"
                  :content="record.name || '---'"
                  placement="top-end"
                  :open-delay="600"
                >
                  <div class="self-center width200px">
                    <span class="list-main-field">{{
                      record.name || '---'
                    }}</span>
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template #[slotList[2].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('EDIT,UPDATE') && record.canEdit()"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="openFailureClassForm(record)"
                  v-tippy
                ></i>
                <i
                  v-if="hasPermission('DELETE')"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  @click="invokeDeleteDialog(record)"
                  v-tippy
                ></i>
              </div>
            </template>
          </CommonList>
        </div>
      </template>
      <ColumnCustomization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></ColumnCustomization>
    </template>
    <portal to="view-manager-link">
      <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
    </portal>

    <new-failure-class
      v-if="showCreateNewDialog"
      ref="edit-failure-class"
      :canShowFormCreation.sync="showCreateNewDialog"
      moduleName="failureclass"
      :dataId="failureClassId"
      :moduleDisplayName="'Failure Class'"
      @closeDialog="closeDialog"
    ></new-failure-class>
    <DeleteDialog
      v-if="showDialog"
      :moduleName="moduleName"
      :errorMap="errorMap"
      :id="deletingRecordId"
      @onClose="showDialog = false"
    ></DeleteDialog>
  </CommonListLayout>
</template>

<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import NewFailureClass from 'src/pages/assets/failureclass/forms/NewFailureClass.vue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import DeleteDialog from 'src/components/failureclass/DeleteDialog.vue'

export default {
  name: 'FailureClass',
  props: ['moduleName', 'viewname'],
  extends: CommonModuleList,
  components: {
    NewFailureClass,
    DeleteDialog,
  },
  data() {
    return {
      showCreateNewDialog: false,
      failureClassId: null,
      deletingRecordId: null,
      errorMap: null,
      showDialog: false,
      currentPageCount: null,
      perPage: 50,
    }
  },
  computed: {
    parentPath() {
      return `/app/at/failureclass`
    },
  },
  methods: {
    async invokeDeleteDialog(moduleData) {
      let { id } = moduleData
      let messageString = this.$t('common.failure_class.confirm_delete')

      let { error } = await API.fetchRecord(
        this.moduleName,
        {
          id,
          fetchChildCount: true,
        },
        { force: true }
      )

      if (!error) {
        let value = await this.$dialog.confirm({
          title: this.$t(`common.failure_class.delete_fc`),
          message: this.$t(messageString),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })

        if (value) {
          let { error } = await API.deleteRecord(this.moduleName, [id])
          if (!error) {
            this.$message.success(
              this.$t('common.failure_class.delete_success')
            )
            this.loadRecords(true)
          } else {
            this.$message.error(error)
          }
        }
      } else {
        this.deletingRecordId = id
        let map = JSON.parse(error.message)
        this.errorMap = map
        this.showDialog = true
      }
    },
    async closeDialog(id) {
      await this.loadRecords(true)
      id > 0 && this.redirectToOverview(id)
      this.showCreateNewDialog = false
    },
    openFailureClassForm(data) {
      if (!isEmpty(data)) {
        let { id } = data
        this.$set(this, 'failureClassId', id)
      } else {
        this.$set(this, 'failureClassId', null)
      }
      this.showCreateNewDialog = true
    },
    getCreatedTime(record) {
      let { sysCreatedTime } = record
      return this.$options.filters.fromNow(sysCreatedTime)
    },
    // route redirection handling
    openList() {
      let { viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'failure-class-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
      this.loadRecords(true)
    },
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = { name, params: { viewname, id }, query }

        return name && route
      } else {
        return {
          name: 'failure-class-summary',
          params: { viewname, id },
          query,
        }
      }
    },
  },
}
</script>
