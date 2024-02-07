<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="fL custom-module-details d-flex mL5">
          <div class="custom-module-id">#{{ record && record.id }}</div>
          <div class="custom-module-name d-flex">
            {{ record[mainFieldKey] }}
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="record.isStateFlowEnabled()">
            <TransitionButtons
              class="mR10"
              :key="`${record.id}transitions`"
              :moduleName="moduleName"
              :record="record"
              :disabled="record.isApprovalEnabled()"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <portal to="pagebuilder-sticky-top" v-if="record.isApprovalEnabled()">
            <ApprovalBar
              :moduleName="moduleName"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <el-button
            v-if="showEdit"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>

          <!-- elipsis button -->
          <el-dropdown
            trigger="click"
            class="
              fc-btn-ico-lg
              mL15
              fc-btn-ico-lg
              pT5
              pB5
              pL8
              pR8
              pointer
              height40
              mT4
            "
          >
            <div class="flex-middle" ref>
              <i class="el-icon-more pointer overview-icon-more-btn"></i>
              <span class="pointer mL-auto text-fc-pink child-add"></span>
            </div>

            <el-dropdown-menu slot="dropdown">
              <!-- portal access btn -->
              <el-dropdown-item>
                <div @click="togglePortalAccessDialog(true)">
                  {{ $t('common.header.portal_access') }}
                </div>
              </el-dropdown-item>
              <PortalAccessDialog
                v-if="portalAccessDialog"
                :user="record"
                :appLinkNames="allowedPortalsForAccess"
                :onSave="savePortalUser"
                @onClose="togglePortalAccessDialog(false)"
              ></PortalAccessDialog>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import PortalAccessDialog from 'src/pages/base-module-v2/PortalAccessDialog'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  components: {
    PortalAccessDialog,
  },
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'peoplenotes',
      attachmentsModuleName: 'peopleattachments',

      applicationAccessDialog: false,
      portalAccessDialog: false,
      allowedPortalsForAccess: ['service', 'employee'],
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
    }),
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    moduleName() {
      return 'employee'
    },
    mainFieldKey() {
      return 'name'
    },
    moduleState() {
      let currentStateId = this.$getProperty(this.record, 'moduleState.id')
      let currentState = (this.states || []).find(
        state => state.id === currentStateId
      )

      return currentState ? currentState.status : null
    },
  },
  title() {
    'Employee'
  },
  methods: {
    toggleApplicationAccessDialog(value) {
      this.applicationAccessDialog = value
    },
    togglePortalAccessDialog(value) {
      this.portalAccessDialog = value
    },
    async savePortalUser(userObj) {
      let {
        id,
        email,
        isOccupantPortalAccess,
        employeePortalAccess,
        scopingId,
        permissionSets,
        rolesMap,
      } = userObj || {}
      let params = {
        employees: [
          {
            id,
            isOccupantPortalAccess,
            employeePortalAccess,
            rolesMap,
            scopingId,
            permissionSets,
          },
        ],
      }

      await this.saveUserAccess({ email, params })
    },
    async saveAppAccess(userData) {
      let { email, id, isAppAccess, rolesMap, scopingId, permissionSets } =
        userData || {}
      let params = {
        employees: [{ id, isAppAccess, rolesMap, scopingId, permissionSets }],
      }

      await this.saveUserAccess({ email, params })
    },
    async saveUserAccess(userObj) {
      let { email, params } = userObj

      if (isEmpty(email)) {
        this.$message.error(
          this.$t('Please enter a valid email for enabling application access')
        )
        return
      }
      let { error } = await API.post('/v2/employee/update', params)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
        throw new Error()
      } else {
        this.$message.success(this.$t('people.employee.update_success'))
        this.toggleApplicationAccessDialog(false)
        this.togglePortalAccessDialog(false)
        this.loadRecord()
      }
    },
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'employee-v2-edit',
            params: { id },
          })
      }
    },
  },
}
</script>
