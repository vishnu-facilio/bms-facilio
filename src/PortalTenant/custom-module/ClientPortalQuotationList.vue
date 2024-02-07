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
        :specialFieldDisplayValue="specialFieldDisplayValue"
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
import ModuleList from './ModuleList.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: ModuleList,
  methods: {
    specialFieldDisplayValue({ field, value }) {
      if (this.$constants.currencyFieldsList.includes(field.name)) {
        let val =
          !isEmpty(value) && value !== '---'
            ? this.$d3.format(',.2f')(value)
            : '0'

        return ['$', '₹'].includes(this.$currency)
          ? this.$currency + val
          : `${val} ${this.$currency}`
      }
      return value
    },
  },
}
</script>
