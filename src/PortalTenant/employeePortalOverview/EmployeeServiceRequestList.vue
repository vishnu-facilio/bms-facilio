<template>
  <div class="h100">
    <EmployeePageLayout :moduleName="moduleName">
      <template slot="title"> {{ moduleDisplayName }} </template>
      <template slot="header">
        <pagination
          :total="recordCount"
          :perPage="perPage"
          :currentPageCount="currentPageCount"
        ></pagination>
        <span class="separator" v-if="recordCount > 0">|</span>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="bottom"
        >
          <Sort
            :moduleName="moduleName"
            :key="moduleName + '-sort'"
            @onSortChange="updateSort"
          >
          </Sort>
        </el-tooltip>
        <span class="separator">|</span>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.search')"
          placement="bottom"
        >
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
          >
          </AdvancedSearch>
        </el-tooltip>
        <!-- <span class="separator">|</span>
        <CreateButton @click="redirectToFormCreation">
          New {{ moduleDisplayName ? moduleDisplayName : '' }}
        </CreateButton> -->
      </template>
      <FTags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></FTags>
      <div v-if="showLoading" class="list-loading">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(records) && !showLoading"
        class="list-empty-state"
      >
        <inline-svg
          src="svgs/emptystate/workorder"
          iconClass="icon text-center icon-xxxxlg height-auto"
        ></inline-svg>
        <div class="line-height20 nowo-label">
          No
          {{ moduleDisplayName ? moduleDisplayName.toLowerCase() : moduleName }}
          available
        </div>
      </div>

      <div
        v-if="!showLoading && !$validation.isEmpty(records)"
        class="portal-common-list"
      >
        <CommonList
          :viewDetail="viewDetail"
          :moduleName="moduleName"
          :records="records"
          :columnConfig="columnConfig"
          :slotList="slotList"
          :redirectToOverview="redirectToOverview"
          :hideListSelect="true"
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
                :content="$getProperty(record, mainFieldName, '---') || '---'"
                placement="top-end"
                :open-delay="600"
              >
                <div class="self-center width200px">
                  <span class="list-main-field">
                    {{ $getProperty(record, mainFieldName, '---') || '---' }}
                  </span>
                </div>
              </el-tooltip>
            </router-link>
          </template>
          <template v-if="hasActionPermissions" #[slotList[2].name]="{record}">
            <div class="d-flex text-center">
              <i
                v-if="canShowEdit && record.canEdit"
                class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                data-arrow="true"
                :title="$t('common._common.edit')"
                @click="editModule(record)"
                v-tippy
              ></i>
              <i
                v-if="canShowDelete"
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
    </EmployeePageLayout>
  </div>
</template>
<script>
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { API } from '@facilio/api'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import { mapGetters, mapState } from 'vuex'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import EmployeePageLayout from 'PortalTenant/employeePortalOverview/EmployeePageLayout.vue'

export default {
  extends: ModuleList,
  components: {
    AdvancedSearch,
    EmployeePageLayout,
  },
  data() {
    return {
      columnConfig: {
        fixedColumns: ['subject'],
        availableColumns: [],
      },
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    filteredViewColumns() {
      let { viewColumns, columnConfig } = this
      let { fixedColumns, fixedSelectableColumns } = columnConfig
      let finalFixedColumns = fixedColumns.concat(fixedSelectableColumns)
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !finalFixedColumns.includes(column.name)
        })
      }
      return []
    },
    canShowEdit() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    canShowDelete() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    hasActionPermissions() {
      let { canShowEdit, canShowDelete } = this
      return canShowEdit || canShowDelete
    },
  },
  methods: {
    canShowActions(record) {
      let { moduleName } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
    getCreationRoute() {
      let { moduleName } = this
      let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

      return name ? { name } : null
    },
    async deleteRecord(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`home.service_request.delete_service_request`),
        message: this.$t(
          `home.service_request.delete_service_request_confirmation`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success(
            this.$t(`home.service_request.service_request_delete_success`)
          )
          this.loadRecords()
        }
      }
    },
  },
}
</script>
<style scoped>
.h100 {
  height: 100%;
}
</style>
