<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.contact_directory.contact_directory') }}
    </template>
    <template slot="header">
      <AdvancedSearchWrapper
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
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
        {{ $t('portal.contactdirectory.add_contact_directory') }}
      </CreateButton>
    </template>
    <template slot="header-2">
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

    <div v-else-if="$validation.isEmpty(records)" class="list-empty-state">
      <inline-svg
        src="svgs/emptystate/tenant"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('tenant.contact_directory.no_data') }}
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
          :moduleName="moduleName"
          :position="POSITION.LIST_BAR"
          :selectedRecords="selectedListItemsObj"
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
        <template #[slotList[1].criteria]="{record}">
          <div class="text-align-center">
            <ListAttachments
              :record="record"
              :module="attachmentsModuleName"
              customClass="small-preview"
            ></ListAttachments>
          </div>
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
import ListAttachments from '@/relatedlist/ListAttachmentPreview'
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  props: ['attachmentsModuleName'],
  title: 'Contact Directory',
  components: {
    ListAttachments,
  },
  data() {
    return {
      columnConfig: {
        fixedColumns: ['title', 'contactName'],
        availableColumns: [],
        showLookupColumns: false,
      },
      perPage: 10,
    }
  },
  computed: {
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'contactName' }),
        },
        {
          criteria: JSON.stringify({ name: 'attachmentPreview' }),
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
      return 'contactName'
    },
  },
}
</script>
