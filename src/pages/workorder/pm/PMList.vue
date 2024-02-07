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
        <button
          class="fc-create-btn create-btn"
          @click="redirectToFormCreation()"
        >
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
    <template
      #sub-header-selectedall
      v-if="
        showBulkActionBar() && this.$helpers.isLicenseEnabled('BULK_UPDATE')
      "
    >
      <div class="bulk-update-bar">
        <NewBulkActionBar
          ref="bulk-action-bar"
          visible="true"
          :totalCount="recordCount"
          :perPage="perPage"
          :isAllSelected.sync="isAllSelected"
          :moduleName="moduleName"
          @change="validateTotalCount"
          :selectedItem="selectedListItemsIds.length"
          :limit="bulkActionAcount"
        />
      </div>
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
                class="btn btn--tertiary pointer"
                @click="OpenBulkUpdate()"
                :disabled="isBulkUpdateVisible"
                v-if="hasPermission('EDIT,UPDATE') && enablePmBulkUpdate()"
              >
                {{ $t('common._common.bulk_update') }}
              </button>
              <button
                class="btn btn--tertiary pointer mL10"
                @click="deleteRecords(selectedListItemsIds)"
                :disabled="isDisable"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
              <button
                class="btn btn--tertiary pointer mL10"
                @click="showWarningDialog('Publish')"
              >
                {{ $t('maintenance.pm.publish') }}
              </button>
              <button
                class="btn btn--tertiary pointer"
                @click="showWarningDialog('Unpublish')"
                :disabled="isDisable"
              >
                {{ $t('maintenance.pm.unpublish') }}
              </button>
            </div>
            <CustomButton
              v-if="!isDisable"
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
              :modelDataClass="modelDataClass"
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              class="custom-button"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>

            <template v-if="enablePmBulkUpdate">
              <new-bulk-update-viewer
                v-if="bulkAction"
                module="plannedmaintenance"
                :ref="'bulk-update-dialog'"
                :fieldlist="bulkActionFieldList"
                :recordCount="recordCount"
                @submit="newbulkActionHandler"
                @closed="handleBulkActiondialogClose"
                :content-css="{
                  padding: '0px',
                  background: '#f7f8fa',
                  Width: '10vw',
                  Height: '30vh',
                }"
              ></new-bulk-update-viewer>
            </template>
            <template v-else>
              <bulk-update-viewer
                v-if="bulkAction"
                module="plannedmaintenance"
                :fieldlist="bulkActionFieldList"
                @submit="bulkActionHandler"
                @closed="closeBulkActionDialog"
                :content-css="{
                  padding: '0px',
                  background: '#f7f8fa',
                  Width: '10vw',
                  Height: '30vh',
                }"
              ></bulk-update-viewer>
            </template>
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
            <template #[slotList[0].criteria]="{record}">
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
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
            <template #[slotList[1].name]="{record}">
              <div class="d-flex">
                <div class="fc-id">{{ '#' + record.id }}</div>
              </div>
            </template>
            <template #[slotList[2].name]="{record}">
              <div class="d-flex text-center">
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
    <template v-if="isBulkPublish">
      <BulkPublish
        v-if="showDialog"
        :showDialog="showDialog"
        :dialogType="dialogType"
        :moduleName="moduleName"
        :stateUpdating="stateUpdating"
        @closeDialog="handleBulkActiondialogClose"
        @saveAction="saveAction"
        :showStatus="showStatus"
        :statusMessage="statusMessage"
      ></BulkPublish>
    </template>
    <template v-else>
      <PMWarningDialog
        v-if="showDialog"
        :showDialog="showDialog"
        :dialogType="dialogType"
        :moduleName="moduleName"
        :stateUpdating="stateUpdating"
        @closeDialog="closeDialog"
        @saveAction="saveAction"
      />
    </template>
  </CommonListLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'
import PMWarningDialog from 'pages/workorder/jobplan/JPWarningDialog'
import NewBulkUpdateViewer from '@/NewBulkUpdateViewer'
import NewBulkActionBar from 'src/newapp/list/NewBulkActionBar'
import moment from 'moment-timezone'
import BulkUpdateViewer from '@/BulkUpdateViewer'
import BulkPublish from 'pages/workorder/jobplan/NewBulkPublish.vue'
export default {
  name: 'PMList',
  extends: CommonModuleList,
  data: () => ({
    showStatus: false,
    statusMessage: '',
    bulkActionFieldList: ['vendor'],
    bulkAction: false,
    bulkActionAcount: 0,
    isPublishLoading: false,
    isUnpublishLoading: false,
    showDialog: false,
    dialogType: null,
    stateUpdating: false,
    currentRecordId: null,
    isDisable: false,
    isBulkUpdateVisible: false,
    isAllSelected: false,
    updateBulkRecord: true,
    isBulkPublish: false,
    moduleFieldList: [],
  }),
  components: {
    PMWarningDialog,
    BulkUpdateViewer,
    NewBulkActionBar,
    NewBulkUpdateViewer,
    BulkPublish,
  },
  mounted() {
    this.updateLimit()
  },
  computed: {
    moduleDisplayName() {
      return this.$t('maintenance.pm.pm')
    },
    parentPath() {
      return `/app/wo/pm/`
    },
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
  },
  methods: {
    async updateLimit() {
      let actionName = 'ppmbulkupdate'
      let params = {
        actionName,
      }
      let { data } = await API.get(`v3/bulkaction/limit/fetch`, params)
      if (data) {
        this.bulkActionAcount = data.limit
      }
    },
    showBulkActionBar() {
      if (!isEmpty(this.selectedListItemsObj)) {
        this.isAllSelected = false
        this.isDisable = false
        this.isBulkUpdateVisible = false
        return true
      } else {
        this.isAllSelected = true
        return false
      }
    },
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: { viewname, id },
          query: $route.query,
        }

        return name && route
      } else {
        return {
          name: 'pm-summary',
          params: { moduleName, viewname, id },
          query: $route.query,
        }
      }
    },
    async publishPm(isPublish) {
      this.stateUpdating = true
      let { selectedListItemsIds } = this || {}
      if (this.isBulkPublish) {
        await this.newPublish(isPublish)
      } else {
        let params = {
          pmIds: selectedListItemsIds,
        }
        let url = `v3/plannedmaintenance/${
          isPublish ? 'bulkpublish' : 'bulkunpublish'
        }`
        let { error } = await API.post(url, params)
        if (!isEmpty(error)) {
          this.$message.error(error.message || 'Error occured')
        } else {
          let successMsg = ''
          if (isPublish) {
            successMsg = this.$t('maintenance.pm.bulk_publish_success')
          } else {
            successMsg = this.$t('maintenance.pm.bulk_unpublish_success')
          }
          this.$message.success(successMsg)
          await this.loadRecords()
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
        }
        this.stateUpdating = false
        this.closeDialog()
      }
    },
    resetdialogStatus() {
      this.statusMessage = ''
      this.showStatus = false
    },
    async newPublish() {
      this.showStatus = false
      this.statusMessage = ''
      let { filters, viewname, moduleName } = this
      let { error } = await API.post(
        'v3/bulkupdate/plannedmaintenance/publish',
        {
          moduleName,
          filters: JSON.stringify(filters),
          viewName: viewname,
        }
      )
      if (!isEmpty(error)) {
        this.showStatus = true
        this.statusMessage = error.message || 'Error occured'
      } else {
        this.showStatus = true
        this.statusMessage = 'Records are published successfully'
      }
      this.stateUpdating = false
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
        this.$router.push({
          name: 'pm-create',
          params: {
            moduleName,
          },
        })
      }
    },
    editModule(record) {
      let { pmStatus, id } = record || {}
      let isActive = PUBLISHED_STATUS[pmStatus] === 'Published'
      this.currentRecordId = id
      if (isActive) {
        this.showWarningDialog('Edit')
      } else {
        this.editRecord()
      }
    },
    async editRecord() {
      let { moduleName, currentRecordId } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name &&
          this.$router.push({
            name,
            params: {
              id: currentRecordId,
            },
          })
      } else {
        this.$router.push({
          name: 'pm-create',
          params: {
            moduleName,
          },
          query: {
            id: currentRecordId,
          },
        })
      }
      this.currentRecordId = null
    },
    async deleteRecords(idList) {
      let { moduleDisplayName, moduleName } = this
      let value = await this.$dialog.confirm({
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
        rbClass: 'pmv2-edit-dialog-btn',
        className: 'pmv2-delete-dialog',
      })

      if (value) {
        this.isLoading = true

        try {
          await this.modelDataClass.delete(moduleName, idList)
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          await this.refreshRecordDetails(true)
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
        this.isLoading = false
      }
    },
    removeMoreVisibility(record, isOpen) {
      let { id } = record || {}
      let element = this.$refs[`pm-more-${id}`] || {}
      let { classList } = element || {}

      if (isOpen) {
        classList.remove('visibility-hide-actions')
      } else {
        classList.add('visibility-hide-actions')
      }
    },
    showWarningDialog(status) {
      this.showDialog = true
      this.dialogType = status
    },
    closeDialog() {
      this.showDialog = false
      this.dialogType = null
    },
    async saveAction(status) {
      if (status === 'Edit') {
        this.editRecord()
      } else {
        let isPublish = status === 'Publish'
        await this.publishPm(isPublish)
      }
    },
    async newbulkActionHandler(actions) {
      //await this.getRecordCount()
      if (this.isDisable) {
        this.executeBulkAction(actions)
      } else {
        this.bulkActionHandler(actions)
      }
    },
    async getRecordCount() {
      let { moduleName, viewname, filters } = this
      let recordCount = null
      try {
        let params = { moduleName, viewname, filters }
        recordCount = await this.modelDataClass.fetchRecordsCount(params)
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.unable_to_fetch_count')
        )
      }
      return recordCount
    },
    async executeBulkAction(actions) {
      // each action represents a field update
      let fields = {}

      actions.forEach(action => {
        let { field, valueArray, fieldObj } = action || {}
        if (field === 'vendor') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else {
          if (!isEmpty(fieldObj)) {
            let fieldObject = fieldObj[0]

            //date and dateTime Handling
            if (
              (fieldObject.dataType == 6 || fieldObject.dataType == 5) &&
              !isEmpty(action.value)
            ) {
              fields[field] = moment(action.value).valueOf()
            } else {
              // other
              fields[field] = action.value
            }
          } else {
            fields[field] = action.value
          }
        }
      })

      let datas = []
      datas.push(fields)

      if (!this.updateBulkRecord) {
        let { error } = await this.bulkUpdateWithV3(datas)
        this.showLoading = false
        if (this.$refs['bulk-update-dialog']) {
          if (error) {
            let message = error?.message ? error.message : ''
            this.$refs['bulk-update-dialog'].updateAPICallback({
              message: message,
              error: true,
            })
          } else {
            this.$refs['bulk-update-dialog'].updateAPICallback({
              message: 'Records updated successfully.',
            })
          }
        }
      }
    },

    handleBulkActiondialogClose() {
      this.bulkAction = false
      this.showDialog = false
      this.dialogType = null
      this.resetdialogStatus()
      this.resetSelectAll()
      this.loadRecords()
    },

    async bulkActionHandler(actions) {
      // each action represents a field update
      let fields = {}

      actions.forEach(action => {
        let { field, valueArray, fieldObj } = action || {}
        if (field === 'vendor') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else {
          if (!isEmpty(fieldObj)) {
            let fieldObject = fieldObj[0]

            //date and dateTime Handling
            if (
              (fieldObject.dataType == 6 || fieldObject.dataType == 5) &&
              !isEmpty(action.value)
            ) {
              fields[field] = moment(action.value).valueOf()
            } else {
              // other
              fields[field] = action.value
            }
          } else {
            fields[field] = action.value
          }
        }
      })

      let data = []
      for (let woID of this.selectedListItemsIds) {
        data.push({ id: woID, ...fields })
      }
      this.bulkUpdate(data)

      this.closeBulkActionDialog()
      this.resetSelectAll()
    },
    resetSelectAll() {
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
    bulkUpdate(data) {
      let { moduleName } = this
      const body = {
        plannedmaintenance: data,
      }
      return API.post(`v3/modules/bulkPatch/${moduleName}`, {
        data: body,
        moduleName,
      })
        .then(({ error }) => {
          if (error) {
            this.$message.error(this.$t(error.message))
          } else {
            this.$message.success('Update successful')
          }
        })
        .then(() => {
          this.resetSelectAll()
          this.closeBulkActionDialog()
          this.loadRecords()
        })
        .catch(() => {
          this.$message.error('Updation failed')
        })
    },
    async bulkUpdateWithV3(fields) {
      let { moduleName, viewname, filters } = this
      this.isSaving = true

      let viewName = viewname

      const body = {
        plannedmaintenance: fields,
      }
      this.showLoading = true
      return await API.post(`v3/bulkupdate/plannedmaintenance/update`, {
        data: body,
        moduleName,
        viewName,
        filters: JSON.stringify(filters),
        count: this.recordCount,
      }).catch(() => {
        this.$message.error('Updation failed')
      })
    },
    hasUpdatePermission() {
      return this.$hasPermission(`${this.moduleName}:UPDATE`)
    },
    closeBulkActionDialog() {
      this.bulkAction = false
    },
    OpenBulkUpdate() {
      if (this.isDisable) {
        if (this.recordCount > this.bulkActionAcount) {
          let dialogObj = {
            title: `Bulk update limit exceeds`,
            htmlMessage: `Bulk update can be performed upto ${this.bulkActionAcount}$ records . Please ensure your selection does not exceed this limit.`,
            rbDanger: true,
            rbLabel: 'Confirm',
          }
          this.$dialog.confirm(dialogObj).then(value => {
            if (value) {
              this.isAllSelected = true
            }
          })
        } else {
          this.bulkAction = true
        }
      } else {
        this.bulkAction = true
        API.get(`v3/bulkupdate/workorder/fetchFields`, {
          moduleName: 'plannedmaintenance',
        })
      }
    },
    validateTotalCount(selectedItem) {
      if (!selectedItem) {
        this.isDisable = true
        this.updateBulkRecord = false
        this.isBulkPublish = true
        if (this.recordCount > this.bulkActionAcount) {
          this.isBulkUpdateVisible = true
        }
      } else {
        this.isDisable = false
        this.isBulkUpdateVisible = false
        this.updateBulkRecord = true
        this.isBulkPublish = false
      }
    },
    clearSelection() {
      this.bulkAction = false
      this.isAllSelected = false
    },
    enablePmBulkUpdate() {
      return this.$helpers.isLicenseEnabled('BULK_UPDATE')
    },
  },
}
</script>

<style lang="scss">
.bulk-update-bar {
  .bulk-action-container {
    padding: 5px;
    background-color: #f3f4f5;
    border-bottom: 1px solid #ebeef5;
  }
}
.pmv2-edit-dialog,
.pmv2-delete-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 30px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 185px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .pmv2-edit-dialog-btn {
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
.pmv2-delete-dialog {
  .f-dialog-content {
    padding-top: 15px;
    min-height: 170px;
  }
}
</style>
