<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title"> {{ moduleDisplayName }} </template>
    <template slot="header">
      <AdvancedSearchWrapper
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :hideSaveView="true"
      ></AdvancedSearchWrapper>
      <CustomButton
        v-if="isBulkActionLicenseEnabled"
        :key="`${moduleName}_${viewname}_${POSITION.LIST_TOP}`"
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        @onSuccess="onCustomButtonSuccess"
      ></CustomButton>
      <CreateButton @click="redirectToFormCreation">
        New {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton>
    </template>
    <template #header-2>
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
    </template>
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
      <div
        class="portal-table-header-actions"
        v-if="
          !$validation.isEmpty(selectedListItemsIds) &&
            isBulkActionLicenseEnabled
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
          class="custom-button"
          @onSuccess="onCustomButtonSuccess"
        ></CustomButton>
      </div>
      <CommonList
        :viewDetail="viewDetail"
        :records="records"
        :columnConfig="columnConfig"
        :slotList="slotList"
        :moduleName="moduleName"
        :refreshList="onCustomButtonSuccess"
        @selection-change="selectItems"
        :canShowCustomButton="isBulkActionLicenseEnabled"
        :hideListSelect="!isBulkActionLicenseEnabled"
      >
        <template #[slotList[0].criteria]="{record}">
          <div class="d-flex fw5 label-txt-black ellipsis">
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
                {{ $getProperty(record, mainFieldName, '---') || '---' }}
              </div>
            </el-tooltip>
          </div>
        </template>
        <template v-if="hasActionPermissions" #[slotList[1].name]="{record}">
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
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  props: ['attachmentsModuleName'],
  data() {
    return {
      columnConfig: {
        fixedColumns: ['title'],
        availableColumns: [],
        showLookupColumns: false,
      },
    }
  },

  computed: {
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'title' }),
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
    mainFieldName() {
      return 'title'
    },
  },
}
</script>
