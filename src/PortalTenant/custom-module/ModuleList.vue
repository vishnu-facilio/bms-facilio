<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title"> {{ moduleDisplayName }} </template>
    <template slot="header">
      <template v-if="!isEmpty(viewname)">
        <AdvancedSearchWrapper
          :key="`ftags-list-${moduleName}`"
          :filters="filters"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :hideSaveView="true"
        ></AdvancedSearchWrapper>
      </template>
      <CustomButton
        v-if="isBulkActionLicenseEnabled"
        :key="`${moduleName}_${viewname}_${POSITION.LIST_TOP}`"
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        @onSuccess="onCustomButtonSuccess"
      ></CustomButton>
      <CreateButton @click="redirectToFormCreation">
        {{ createBtnText }}
      </CreateButton>
    </template>
    <template slot="header-2">
      <template v-if="!isEmpty(viewname)">
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
          <FExportSettings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></FExportSettings>
        </el-tooltip>
      </template>
    </template>
    <div v-if="showLoading" class="list-loading">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="isEmpty(viewname)"
      class="portal-view-empty-state-container"
    >
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="line-height20 nowo-label">
        {{ $t('viewsmanager.list.no_view_config') }}
      </div>
    </div>
    <div v-else-if="isEmpty(records)" class="list-empty-state">
      <inline-svg
        src="svgs/emptystate/workorder"
        iconClass="icon text-center icon-xxxxlg height-auto"
      ></inline-svg>
      <div class="line-height20 nowo-label">
        {{ emptyStateText }}
      </div>
    </div>
    <div v-else class="portal-common-list">
      <div
        class="portal-table-header-actions"
        v-if="
          isBulkActionLicenseEnabled &&
            !$validation.isEmpty(selectedListItemsIds)
        "
      >
        <button
          v-if="canShowDelete"
          class="portal-bulk-action-delete"
          @click="deleteRecords(selectedListItemsIds)"
        >
          {{ $t('custommodules.list.delete') }}
        </button>
        <CustomButton
          :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
          :selectedRecords="selectedListItemsObj"
          :moduleName="moduleName"
          :position="POSITION.LIST_BAR"
          @onSuccess="onCustomButtonSuccess"
        ></CustomButton>
      </div>
      <CommonList
        :moduleName="moduleName"
        :viewDetail="viewDetail"
        :records="records"
        :columnConfig="columnConfig"
        :slotList="slotList"
        :redirectToOverview="redirectToOverview"
        :refreshList="onCustomButtonSuccess"
        @selection-change="selectItems"
        :canShowCustomButton="isBulkActionLicenseEnabled"
        :hideListSelect="!isBulkActionLicenseEnabled"
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
  </PageLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import PageLayout from 'src/PortalTenant/components/PageLayout'
import CreateButton from 'PortalTenant/components/CreateButton'
import { mapGetters, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import AdvancedSearchWrapper from 'src/newapp/components/search/AdvancedSearchWrapper'

export default {
  extends: CommonModuleList,
  components: { PageLayout, CreateButton, AdvancedSearchWrapper },
  data() {
    return {
      columnConfig: {
        fixedColumns: ['id', 'name'],
        fixedSelectableColumns: ['photo'],
        availableColumns: [],
        showLookupColumns: false,
      },
    }
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    mainFieldName() {
      return this.mainFieldNamesList[0] || 'name'
    },
    mainFieldNamesList() {
      let { fields } = this.metaInfo || {}
      let mainFields = (fields || []).filter(fld => fld.mainField)
      let mainFieldsNames = (mainFields || []).map(fld => fld.name)

      return mainFieldsNames
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
    hasPagination() {
      let { currentPageCount, recordCount } = this
      return (
        (!isEmpty(currentPageCount) && parseInt(currentPageCount) > 0) ||
        (!isEmpty(recordCount) && parseInt(recordCount) > 0)
      )
    },
    isBulkActionLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('BULK_ACTION_IN_PORTAL')
    },
  },
  watch: {
    mainFieldNamesList: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          let { fixedColumns } = this.columnConfig || {}
          let newFixedCol = newVal.filter(
            fldName => !fixedColumns.includes(fldName)
          )

          fixedColumns = [...fixedColumns, ...newFixedCol]
          this.$set(this.columnConfig, 'fixedColumns', fixedColumns)
        }
      },
      immediate: true,
    },
  },
}
</script>
<style lang="scss" scoped>
.portal-common-list {
  position: relative;
  height: 100%;
  flex-grow: 1;
  overflow: scroll;

  .img-container {
    width: 37px;
    height: 37px;
    border: 1px solid #f9f9f9;
    border-radius: 50%;
  }
}
</style>
<style lang="scss">
.portal-common-list .el-table th > .cell {
  text-overflow: ellipsis;
  overflow: hidden;
  width: 100%;
  white-space: nowrap;
}
</style>
