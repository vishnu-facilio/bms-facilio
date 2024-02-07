<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else class="flex flex-row">
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
        :key="`ftags-list-${moduleName}`"
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
        <button class="fc-create-btn " @click="redirectToFormCreation()">
          {{ $t('custommodules.list.new') }}
          {{ moduleDisplayName ? moduleDisplayName : '' }}
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
          class="pL15 fc-black-small-txt-12"
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

      <div
        v-if="$validation.isEmpty(records) && !showLoading"
        class="cm-empty-state-container"
      >
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

      <template v-if="!showLoading && !$validation.isEmpty(records)">
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
            v-if="!$validation.isEmpty(selectedListItemsIds)"
          >
            <div class="action-btn-slide btn-block">
              <button
                v-if="hasPermission('DELETE')"
                class="btn btn--tertiary pointer"
                @click="deleteRecords(selectedListItemsIds, true)"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
              <button
                class="btn btn--tertiary pointer mL10"
                @click="showJPStatusUpdateDialog('Publish')"
                :class="{ disabled: actions.Publish.loading }"
              >
                {{ $t('maintenance.pm.publish') }}
              </button>
              <button
                v-if="false"
                class="btn btn--tertiary pointer"
                @click="showJPStatusUpdateDialog('Unpublish')"
                :class="{ disabled: actions.Unpublish.loading }"
              >
                {{ $t('maintenance.pm.unpublish') }}
              </button>
              <button
                v-if="false"
                class="btn btn--tertiary pointer"
                @click="showJPStatusUpdateDialog('Disable')"
                :class="{ disabled: actions.Disable.loading }"
              >
                {{ $t('jobplan.disable') }}
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
                :to="redirectToOverview(record)"
              >
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
              <div
                class="d-flex text-center"
                v-if="canShowActionColumn(record)"
              >
                <i
                  v-if="hasPermission('EDIT,UPDATE') && record.canEdit()"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editModule(record)"
                  v-tippy
                ></i>
                <i
                  v-if="hasPermission('DELETE')"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  @click="deleteRecords([record.id])"
                  v-tippy
                ></i>
              </div>
            </template>
          </CommonList>
        </div>
      </template>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="`/app/wo/${moduleName}/viewmanager`"
        class="view-manager-btn"
      >
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </router-link>
    </portal>
    <JPWarningDialog
      v-if="showDialog"
      :showDialog="showDialog"
      :dialogType="dialogType"
      :moduleName="moduleName"
      :stateUpdating="stateUpdating"
      @closeDialog="closeDialog"
      @saveAction="saveAction"
    />
  </CommonListLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import {
  PUBLISHED_STATUS,
  PUBLISH_STATUS,
} from 'pages/workorder/pm/create/utils/pm-utils.js'
import JPWarningDialog from './JPWarningDialog'

export default {
  extends: CommonModuleList,
  data() {
    return {
      actions: {
        delete: {
          loading: false,
        },
        Publish: {
          loading: false,
        },
        Unpublish: {
          loading: false,
        },
        Disable: {
          loading: false,
        },
      },
      showDialog: false,
      dialogType: null,
      stateUpdating: false,
    }
  },
  components: { JPWarningDialog },
  computed: {
    moduleDisplayName() {
      return 'Job Plan'
    },
    parentPath() {
      return `/app/wo/jobplan/`
    },
  },
  methods: {
    canShowActionColumn(record) {
      let { jpStatus } = record || {}
      return !['Published', 'Disabled'].includes(PUBLISHED_STATUS[jpStatus])
    },
    redirectToOverview(record) {
      let { moduleName, viewname } = this
      let { group, jobPlanVersion: version } = record || {}
      let groupId = this.$getProperty(group, 'id', null)

      version = `v${version}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname,
            id: groupId,
          },
          query: { version },
        }

        return name && route
      } else {
        return {
          name: 'jobPlanSummary',
          params: { moduleName, viewname, id: groupId },
          query: { version },
        }
      }
    },

    //To Bulk Publish JobPlan(s)
    async bulkPublishJP() {
      this.stateUpdating = true
      let { selectedListItemsIds } = this
      let { error } = await API.post('v3/jobPlan/bulkPublish', {
        jobPlanIds: selectedListItemsIds,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('jobplan.jp_published'))
        await this.loadRecords()
        this.selectedListItemsIds = []
        this.selectedListItemsObj = []
      }
      this.$set(this.actions, `Publish`, false)
      this.stateUpdating = false
      this.closeDialog()
    },

    //To Bulk UnPublish,Disable JobPlan
    async updateJPStatus(status, selectedJP) {
      this.stateUpdating = true
      this.$set(this.actions, `${status}`, true)
      let serializedJP = selectedJP.map(jp => {
        return { ...jp, jpStatus: PUBLISH_STATUS[`${status}`] }
      })
      await this.bulkPatchJP(serializedJP, this.$t(`jobplan.${status}_success`))
      this.$set(this.actions, `${status}`, false)
    },
    async bulkPatchJP(jpList, successMessage) {
      let { moduleName } = this
      let data = { [moduleName]: jpList }
      let url = `v3/modules/bulkPatch/${moduleName}`
      let params = { data, moduleName }
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(successMessage)
        await this.loadRecords()
        this.selectedListItemsIds = []
        this.selectedListItemsObj = []
      }
      this.stateUpdating = false
      this.closeDialog()
    },
    editModule(record) {
      let { moduleName } = this
      let { group, jobPlanVersion: version } = record || {}
      let groupId = this.$getProperty(group, 'id', null)

      version = `v${version}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: { id: groupId },
            query: { version },
          })
      } else {
        this.$router.push({
          name: 'edit-jobplan',
          params: { id: groupId },
          query: { version },
        })
      }
    },
    async deleteRecords(idList, isBulk) {
      let value = await this.$dialog.confirm({
        title: this.$t('jobplan.delete_job_plan'),
        message: this.$t('jobplan.are_you_sure_delete_job_plan'),
        rbLabel: this.$t('common._common.delete'),
        rbClass: 'jp-delete-dialog-btn',
        className: 'jp-delete-dialog',
      })

      if (value) {
        if (isBulk) this.actions.delete = true
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)
        if (error) {
          let { message } = error
          this.$message.error(
            message || 'Error Occured while deleting Job Plan'
          )
          this.loading = false
        } else {
          this.$message.success(
            this.$t('jobplan.job_plan_deleted_successfully')
          )
          if (isBulk) this.actions.delete = false
          await this.refreshRecordDetails(true)
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
        }
      }
    },
    redirectToFormCreation() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({ name: 'new-jobplan' })
      }
    },
    showJPStatusUpdateDialog(status) {
      this.showDialog = true
      this.dialogType = status
    },
    closeDialog() {
      this.showDialog = false
      this.dialogType = null
    },
    async saveAction(status) {
      let { selectedListItemsObj } = this
      if (status === 'Publish') {
        await this.bulkPublishJP()
      } else {
        await this.updateJPStatus(status, selectedListItemsObj)
      }
    },
  },
}
</script>

<style scoped lang="scss">
.create-btn {
  margin-top: -10px;
}
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
</style>
<style lang="scss">
.jp-delete-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 15px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 170px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .jp-delete-dialog-btn {
    width: 50%;
    background-color: #39b2c2 !important;
    border: transparent;
    margin-left: 0;
    padding-top: 20px;
    padding-bottom: 20px;
    border-radius: 0;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1.1px;
    text-align: center;
    color: #ffffff;
    &:hover {
      background-color: #3cbfd0 !important;
    }
  }
}
</style>
