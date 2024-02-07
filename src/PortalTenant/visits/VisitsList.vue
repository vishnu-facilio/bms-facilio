<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title"> {{ moduleDisplayName }} </template>
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
      <CreateButton @click="showFormVisibility = true">
        New {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton>

      <VisitsAndInvitesForm
        v-if="showFormVisibility"
        :moduleName="moduleName"
        :requestedBy="{ id: $portaluser.ouid }"
        @onClose="showFormVisibility = false"
      ></VisitsAndInvitesForm>
    </template>
    <template slot="header-2">
      <pagination
        :total="recordCount"
        :perPage="perPage"
        :currentPageCount="currentPageCount"
      ></pagination>
      <span class="separator pL10" v-if="recordCount > 0">|</span>
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
        :redirectToOverview="redirectToOverview"
        :moduleName="moduleName"
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
            <VisitorAvatar
              :module="moduleName"
              :name="false"
              size="lg"
              v-if="record"
              :recordData="record"
            ></VisitorAvatar>
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
        <template #[slotList[2].criteria]="{record}">
          <div class="text-align-center">
            <f-list-attachment-preview
              module="newvisitorlogattachments"
              :record="record"
            ></f-list-attachment-preview>
          </div>
        </template>
        <template #[slotList[3].criteria]="{record}">
          <user-avatar
            v-if="record.host"
            size="md"
            :user="record.host"
          ></user-avatar>
          <div v-else>---</div>
        </template>
        <template v-if="hasActionPermissions" #[slotList[4].name]="{record}">
          <div class="d-flex text-center">
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
    <router-view></router-view>
  </PageLayout>
</template>
<script>
import VisitorAvatar from '@/avatar/VisitorAvatar'
import UserAvatar from '@/avatar/User'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  components: {
    VisitorAvatar,
    UserAvatar,
    FListAttachmentPreview,
    VisitsAndInvitesForm,
  },
  data() {
    return {
      columnConfig: {
        fixedColumns: ['visitor'],
      },
      showFormVisibility: false,
    }
  },
  computed: {
    mainFieldName() {
      return 'visitor.name'
    },
    slotList() {
      return [
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
          criteria: JSON.stringify({ name: 'visitor' }),
        },
        {
          criteria: JSON.stringify({ name: 'attachmentPreview' }),
        },
        {
          criteria: JSON.stringify({ name: 'host' }),
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
}
</script>
