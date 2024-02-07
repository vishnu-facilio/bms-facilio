<template v-if="groupinvite">
  <PageLayout :moduleName="moduleName">
    <template slot="title"> {{ moduleDisplayName }} </template>
    <template slot="header">
      <AdvancedSearchWrapper
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :hideSaveView="true"
        >
        </AdvancedSearchWrapper>
        <button
        class="fc-create-btn create-btn"
        style="margin-top: -10px;"
        @click="openInviteForm"
      >
        {{ $t('common.header.group_invite') }}
      </button>

      <VisitsAndInvitesForm
        v-if="showFormVisibility"
        :moduleName="inviteModule"
        :formMode="formMode"
        @onClose="showFormVisibility = false"
      ></VisitsAndInvitesForm>
    </template>
      <template slot="header-2" v-if="recordCount">
        <pagination :total="recordCount" :perPage="50"></pagination>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.sort')"
        placement="bottom"
      >
        <Sort
          :moduleName="moduleName"
          :key="moduleName + '-sort'"
          @sortChange="loadRecords"
        >
        </Sort>
      </el-tooltip>
    </template>

    <div class="list-container" style="height: 100%;">
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

      <template v-if="!showLoading && !$validation.isEmpty(records)">
        <div class="portal-common-list">
          <CommonList
            :viewDetail="viewDetail"
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
                  <div class="self-center mL5 width200px">
                    <span class="list-main-field">
                      {{ $getProperty(record, mainFieldName, '---') || '---' }}
                    </span>
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template
              v-if="hasActionPermissions"
              #[slotList[2].name]="{record}"
            >
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
      </template>
    </div>

    <router-view></router-view>
  </PageLayout>
</template>
<script>
import VisitorAvatar from '@/avatar/VisitorAvatar'
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  extends: ModuleList,
  components: { VisitorAvatar, VisitsAndInvitesForm },

  data() {
    return {
      columnConfig: {
        fixedColumns: ['visitor'],
        availableColumns: [],
        showLookupColumns: false,
      },
      showFormVisibility: false,
      formMode: 'bulk',
      inviteModule: 'invitevisitor',
    }
  },
  computed: {
    groupinvite() {
      return this.$helpers.isLicenseEnabled('GROUP_INVITES') ? true : false
    },
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
    openInviteForm() {
      this.formMode = 'bulk'
      this.showFormVisibility = true
    },
    editModule(record) {
      //let data = this.records.find(record => record.id === id)
      let { id, formId, visitorTypeId } = record
      let query = {
        formId: formId,
        formMode: 'bulk',
        visitorTypeId: visitorTypeId,
      }

      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({
            name,
            params: { id },
            query,
          })
        }
      } else {
        this.$router.push({
          name: 'group-invites-edit',
          params: { id },
          query,
        })
      }
    },
    getCreationRoute() {
      this.openInviteForm()
    },
  },
}
</script>
