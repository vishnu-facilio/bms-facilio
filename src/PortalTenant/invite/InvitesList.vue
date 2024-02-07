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
      <template v-if="hasPermission('CREATE')">
        <el-dropdown
          v-if="!disableGroupInvite"
          trigger="click"
          @command="openInviteForm"
        >
          <el-button class="fc-create-btn create-btn">
            {{ $t('common.products.new_invite') }}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="single">
              {{ $t('common.header.single_invite') }}
            </el-dropdown-item>
            <el-dropdown-item command="bulk">
              {{ $t('common.header.group_invite') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button
          v-else
          class="fc-create-btn create-btn uppercase"
          @click="openInviteForm('single')"
        >
          {{ $t('common.products.new_invite') }}
        </el-button>
      </template>
      <VisitsAndInvitesForm
        v-if="showFormVisibility"
        :moduleName="moduleName"
        :formMode="formMode"
        @onClose="showFormVisibility = false"
      ></VisitsAndInvitesForm>
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
    <div
      v-if="$validation.isEmpty(records) && !showLoading"
      class="list-empty-state"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="mT10 fc-black-dark f16 fw6">
        {{ $t('home.visitor.visitor_invites_no_data') }}
      </div>
      <div class="fc-grayish f14 line-height30">
        {{ $t('tenant.vendor.no_invites_in_view') }}
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
              :content="$getProperty(record, 'visitorName', '---') || '---'"
              placement="top-end"
              :open-delay="600"
            >
              <div class="self-center width200px pL10">
                <span class="list-main-field">
                  {{ $getProperty(record, 'visitorName', '---') || '---' }}
                </span>
              </div>
            </el-tooltip>
          </router-link>
        </template>
        <template v-if="hasActionPermissions" #[slotList[2].name]="{record}">
          <div class="d-flex text-center">
            <i
              v-if="canShowEdit && record.canEdit()"
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
    <router-view></router-view>
  </PageLayout>
</template>
<script>
import VisitorAvatar from '@/avatar/VisitorAvatar'
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: ModuleList,
  components: { VisitorAvatar, VisitsAndInvitesForm },

  data() {
    return {
      columnConfig: {
        fixedColumns: ['id', 'visitorName'],
        availableColumns: [],
        showLookupColumns: false,
      },
      showFormVisibility: false,
      formMode: 'single',
    }
  },
  computed: {
    mainFieldNamesList() {
      return []
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
          name: 'visitorName',
          criteria: JSON.stringify({ name: 'visitorName' }),
          columnAttrs: {
            'min-width': 110,
            label: 'visitorName',
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
    disableGroupInvite() {
      if (
        this.$helpers.isLicenseEnabled('DISABLE_GROUP_INVITE') ||
        this.$helpers.isLicenseEnabled('GROUP_INVITES')
      ) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    openInviteForm(mode) {
      this.formMode = mode
      this.showFormVisibility = true
    },
    editModule(record) {
      let { moduleName } = this
      let route = findRouteForModule(moduleName, pageTypes.EDIT)
      if (route) {
        let { id, visitorType, formId } = record
        let { id: visitorTypeId } = visitorType || {}
        this.$router.push({
          name: route.name,
          params: { id },
          query: { id, visitorTypeId, formId },
        })
      }
    },
  },
}
</script>
