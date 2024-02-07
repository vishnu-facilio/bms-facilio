<template>
  <div class="fc-setup-summary">
    <div v-if="isLoading" class="p20 fc-setup-page-loader-height">
      <SetupLoader>
        <template #setupLoading>
          <spinner :show="isLoading" size="80"></spinner>
        </template>
      </SetupLoader>
    </div>
    <div v-else>
      <el-header class="fc-setup-summary-header pT10 people-header">
        <div class="flex-middle fc-setup-breadcrumb">
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupHomeRoute"
          >
            {{ $t('common.products.home') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupPeopleRoute"
          >
            {{ $t('setup.users_management.people') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div class="fc-breadcrumbBold-active"># {{ peopleId }}</div>
        </div>
        <div class="display-flex-between-space pT10">
          <div class="d-flex">
            <Avatar
              size="lg"
              :user="{ name: getPersonProperty('name', '---') }"
            ></Avatar>
            <div class="d-flex flex-middle fc-grey-txt18">
              {{ getPersonProperty('name', '---') }}
            </div>
          </div>
          <el-dropdown
            @command="onOptionsSelect($event, selectedPerson)"
            class="pointer"
          >
            <span class="el-dropdown-link">
              <i class="el-icon-more fc-black2-18 text-left rotate-90 "></i>
            </span>

            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="edit">{{
                $t('setup.users_management.edit_person')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-tabs v-model="peopleSummaryTab">
        <el-tab-pane
          :label="$t('setup.users_management.summary')"
          name="summary"
        >
          <div class="fc-grey3-text14 bold">
            {{ $t('setup.users_management.basic_information') }}
          </div>
          <el-card
            shadow="never"
            class="fc-setup-summary-card heightInitial mB20 mT15"
          >
            <el-row :gutter="24">
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.approvalprocess.name') }}
                </div>
                <div class="fc-black-14 text-left bold mT5   break-word">
                  {{ getPersonProperty('name', '---') }}
                </div>
              </el-col>
              <el-col :span="8">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.from_address.email') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ getPersonProperty('email', '---') }}
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.phone') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ getPersonProperty('phone', '---') }}
                </div>
              </el-col>
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.people_type') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ peopleType }}
                </div>
              </el-col>
              <el-col :span="6" class="mT15">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.is_person_labour') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ isLabour }}
                </div>
              </el-col>
            </el-row>
          </el-card>
          <div class="fc-grey3-text14 bold">
            {{ $t('setup.users_management.system_info') }}
          </div>
          <el-card
            shadow="never"
            class="fc-setup-summary-card heightInitial mB20 mT15"
          >
            <el-row :gutter="24">
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.created_by') }}
                </div>
                <div class="fc-black-14 text-left bold mT5   break-word">
                  {{ getCreatorInfo('sysCreatedBy', '---') }}
                </div>
              </el-col>
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.created_time') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ getCreatorTime('sysCreatedTime', '---') }}
                </div>
              </el-col>
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.modified_by') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ getCreatorInfo('sysModifiedBy', '---') }}
                </div>
              </el-col>
              <el-col :span="6">
                <div class="fc-grey2-text12 bold line-height20">
                  {{ $t('setup.users_management.modified_time') }}
                </div>
                <div class="fc-black-14 text-left bold mT5 break-word">
                  {{ getCreatorTime('sysModifiedTime', '---') }}
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>
        <!-- <el-tab-pane
          :label="$t('setup.users_management.roles_and_access')"
          name="roles"
        >
          <div class="fc-people-summary-scroll">
            <el-card
              shadow="never"
              class="fc-setup-summary-card-p0"
              :class="[
                userAssociated ? 'user-associated' : 'user-not-associated',
              ]"
              v-if="!hideUser"
            >
              <div class="display-flex-between-space summary-sub-header pT10">
                <div class="section-header pB10">
                  {{ $t('setup.users_management.user') }}
                </div>
                <div
                  class="text-center context-actions pointer pB10 mR15 mT7"
                  v-if="canShowUserActions"
                >
                  <i
                    class="el-icon-edit edit-icon pR15"
                    data-arrow="true"
                    :title="$t('common._common.edit')"
                    v-tippy
                    @click="editUser()"
                  ></i>
                  <i
                  class="el-icon-delete fc-delete-icon"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  v-tippy
                  @click="deleteUser()"
                ></i>
                </div>
              </div>
              <template v-if="!userAssociated">
                <div
                  class="association-empty-state text-center flex-middle flex-center-vH"
                >
                  <InlineSvg
                    src="svgs/list-empty"
                    class="mL20"
                    iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
                  ></InlineSvg>

                  <div class="fc-black-dark f18 bold pL50 line-height10">
                    {{ $t('setup.users_management.no_user_access') }}
                  </div>
                  <el-button
                    class="setup-el-btn mT30 mL20"
                    @click="loadUserForm()"
                  >
                    {{ $t('setup.users_management.add_as_user') }}
                  </el-button>
                </div>
              </template>
              <template v-else>
                <el-row class="context-area" :gutter="24">
                  <el-col :span="6">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.approvalprocess.name') }}
                    </div>
                    <div
                      @click="openUserSummary()"
                      class="fc-black-14 text-left bold mT5 main-field-column  break-word"
                    >
                      {{ getContextProperty('userContext', 'name', '---') }}
                    </div>
                  </el-col>
                  <el-col :span="7">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.from_address.email') }}
                    </div>
                    <div class="fc-black-14 text-left bold mT5 break-word">
                      {{ getContextProperty('userContext', 'email', '---') }}
                    </div>
                  </el-col>
                  <el-col :span="5">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.users_management._role') }}
                    </div>
                    <div class="fc-black-14 text-left bold mT5 break-word">
                      {{ getContextProperty('userContext', 'role', '---') }}
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.users_management._status') }}
                    </div>
                    <el-switch
                      class="mT5 pointer-events-none"
                      v-if="inviteAcceptedState"
                      :value="inviteAcceptedState"
                    >
                    </el-switch>
                    <div
                      v-else
                      class="text-left bold mT5 reinvite-txt pointer"
                      @click="reInvite()"
                    >
                      {{ $t('setup.users_management.reinvite') }}
                    </div>
                  </el-col>
                </el-row>
              </template>
            </el-card>
            <el-card
              shadow="never"
              class="mT30 fc-setup-summary-card-p0"
              :class="[
                laborAssociated ? 'user-associated' : 'user-not-associated',
              ]"
              v-if="canShowLabourWidget && !hideLabour"
            >
              <div class="display-flex-between-space summary-sub-header  pT10">
                <div class="section-header pB10">
                  {{ $t('setup.users_management.labor') }}
                </div>
                <div
                  v-if="!$validation.isEmpty(selectedPerson.labourContext)"
                  class="text-center context-actions pointer mR15 mT7 pB10"
                >
                  <i
                    class="el-icon-edit edit-icon pR15"
                    data-arrow="true"
                    :title="$t('common._common.edit')"
                    v-tippy
                    @click="editLabour()"
                  ></i>
                  <i
                    class="el-icon-delete fc-delete-icon"
                    data-arrow="true"
                    :title="$t('common._common.delete')"
                    v-tippy
                    @click="deleteLabour()"
                  ></i>
                </div>
              </div>
              <template v-if="!laborAssociated">
                <div
                  class="association-empty-state  text-center flex-middle flex-center-vH"
                >
                  <InlineSvg
                    src="svgs/list-empty"
                    class="mL20"
                    iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
                  ></InlineSvg>

                  <div class="fc-black-dark f18 bold pL50 line-height10">
                    {{ $t('setup.users_management.not_labor') }}
                  </div>
                  <el-button
                    class="setup-el-btn mT30 mL40"
                    @click="loadLaborForm"
                  >
                    {{ $t('setup.users_management.convert_as_labor') }}
                  </el-button>
                </div>
              </template>
              <template v-else>
                <el-row class="context-area" :gutter="24">
                  <el-col :span="6">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.approvalprocess.name') }}
                    </div>
                    <div class="fc-black-14 text-left bold mT5 break-word">
                      {{ getContextProperty('labourContext', 'name', '---') }}
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.from_address.email') }}
                    </div>
                    <div class="fc-black-14 text-left bold mT5 break-word">
                      {{ getContextProperty('labourContext', 'email', '---') }}
                    </div>
                  </el-col>
                  <el-col :span="4">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.labour.form.rate_per_hour') }}
                    </div>
                    <div class="fc-black-14 text-left bold mT5 break-word">
                      {{ laborCost }}
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="fc-grey2-text12 bold line-height20">
                      {{ $t('setup.users_management._status') }}
                    </div>
                    <el-switch
                      class="mT5 pointer-events-none"
                      :value="
                        getContextProperty(
                          'labourContext',
                          'availability',
                          false
                        )
                      "
                    >
                    </el-switch>
                  </el-col>
                </el-row>
              </template>
            </el-card>
          </div>
        </el-tab-pane> -->
      </el-tabs>
      <EditUser
        v-if="showUserForm"
        :user="selectedUser"
        :isNew="isNewUser"
        :appId="appId"
        :applications="appInfo"
        :fromPeople="true"
        @close="showUserForm = false"
        @save="reLoadPerson"
      ></EditUser>
      <LabourForm
        v-if="showLaborForm"
        :isNew="isNewLabour"
        :editLabourDetails="selectedLabour"
        :fromPeople="true"
        @onSubmit="reLoadPerson"
        @onClose="showLaborForm = false"
      ></LabourForm>
      <NewPersonForm
        v-if="showPersonDialog"
        :showPersonDialog="showPersonDialog"
        :selectedPerson="selectedPerson"
        :isNew="false"
        @closeDialog="showPersonDialog = false"
        @savePerson="reLoadPerson"
      />
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import SetupLoader from 'pages/setup/components/SetupLoader'
import Avatar from '@/Avatar'
import { isEmpty } from '@facilio/utils/validation'
import { formatDate } from 'src/util/filters'
import { mapGetters } from 'vuex'
import EditUser from 'pages/setup/users/EditUser'
import { getApp } from '@facilio/router'
import LabourForm from '../Labour/LabourForm.vue'
import NewPersonForm from './NewPersonForm'

export default {
  name: 'PeopleSummary',
  data() {
    return {
      isLoading: false,
      selectedPerson: null,
      peopleSummaryTab: 'summary',
      peopleTypeHash: {
        1: 'Tenant Contact',
        2: 'Vendor Contact',
        3: 'Employee',
        4: 'Client Contact',
        5: 'Occupant',
        6: 'Default',
        7: 'Others',
      },
      showUserForm: false,
      showLaborForm: false,
      selectedUser: null,
      selectedLabour: null,
      isNewUser: true,
      isNewLabour: true,
      showPersonDialog: false,
    }
  },
  components: { SetupLoader, Avatar, EditUser, LabourForm, NewPersonForm },
  computed: {
    ...mapGetters(['getUser']),
    canShowLabourWidget() {
      return this.$helpers.isLicenseEnabled('RESOURCES')
    },
    peopleId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}

      return parseInt(id) || null
    },
    peopleType() {
      let { selectedPerson, peopleTypeHash } = this
      let { peopleType } = selectedPerson || {}

      return !isEmpty(peopleType) ? peopleTypeHash[peopleType] : '---'
    },
    canShowUserActions() {
      let { superAdmin, selectedPerson } = this
      let { userContext } = selectedPerson || {}

      return !isEmpty(userContext) && !superAdmin
    },
    inviteAcceptedState() {
      return this.getContextProperty('userContext', 'inviteAcceptStatus', false)
    },
    hideUser() {
      let { selectedPerson } = this
      let { user, userContext } = selectedPerson || {}

      return user && isEmpty(userContext)
    },
    laborCost() {
      return `${this.$currency}${this.getContextProperty(
        'labourContext',
        'cost',
        '---'
      )}`
    },
    hideLabour() {
      let { selectedPerson } = this
      let { labour, labourContext } = selectedPerson || {}

      return labour && isEmpty(labourContext)
    },
    userAssociated() {
      let { selectedPerson } = this
      let { userContext } = selectedPerson || {}
      return !isEmpty(userContext)
    },
    laborAssociated() {
      let { selectedPerson } = this
      return this.$getProperty(selectedPerson, 'labour', false)
    },
    moduleName() {
      return 'people'
    },
    isLabour() {
      let { laborAssociated } = this
      return laborAssociated
        ? this.$t('common.products.yes')
        : this.$t('common.products._no')
    },
    appId() {
      let { id } = getApp() || {}
      return id
    },
    appInfo() {
      return getApp() || {}
    },
    superAdmin() {
      return (
        this.getContextProperty('userContext', 'role', '---') ===
        'Super Administrator'
      )
    },
  },
  created() {
    this.loadPersonSummary()
  },
  methods: {
    async loadPersonSummary() {
      this.isLoading = true
      let { peopleId, moduleName } = this
      let { people, error } = await API.fetchRecord(moduleName, {
        id: peopleId,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.selectedPerson = people
      }
      this.isLoading = false
    },
    setupHomeRoute() {
      this.$router.replace({ name: 'setup' })
    },
    setupPeopleRoute() {
      this.$router.replace({ name: 'setup.people' })
    },
    loadUserForm() {
      let { selectedPerson } = this
      let { id: peopleId, email, name, phone } = selectedPerson || {}

      this.selectedUser = { email, peopleId, name, phone }
      this.showUserForm = true
    },
    loadLaborForm() {
      let { selectedPerson } = this
      let { id: peopleId, email, name, phone } = selectedPerson || {}

      this.selectedLabour = { email, people: { id: peopleId }, name, phone }
      this.showLaborForm = true
    },
    getPersonProperty(property, defaultValue) {
      let { selectedPerson } = this
      return this.$getProperty(selectedPerson, property, defaultValue)
    },
    getCreatorTime(property, defaultValue) {
      let { selectedPerson } = this
      let time = this.$getProperty(selectedPerson, property, null)

      return !isEmpty(time) ? formatDate(time) : defaultValue
    },
    getCreatorInfo(property, defaultValue) {
      let { selectedPerson } = this
      let ouid = this.$getProperty(selectedPerson, `${property}.uid`, null)

      return !isEmpty(ouid) ? this.getUser(ouid).name : defaultValue
    },
    getContextProperty(context, property, defaultValue) {
      let { selectedPerson } = this
      let { [`${context}`]: record } = selectedPerson || {}

      if (property === 'role') {
        let { role } = record || {}
        return this.$getProperty(role, 'name', {})
      } else {
        return this.$getProperty(record, property, defaultValue)
      }
    },
    onOptionsSelect(command, person) {
      if (command === 'edit') {
        this.editPerson(person)
      }
    },
    editUser() {
      let { selectedPerson } = this
      let { userContext } = selectedPerson || {}

      this.selectedUser = userContext
      this.isNewUser = false
      this.showUserForm = true
    },
    deleteUser() {
      let { selectedPerson } = this
      let { userContext: user } = selectedPerson || {}

      const formData = new FormData()
      formData.append('user.uid', user.uid)
      formData.append('user.ouid', user.ouid)
      this.$dialog
        .confirm({
          title: this.$t('setup.users_management.delete_user_msg'),
          message: `${this.$t(
            'setup.users_management.delete_user_msg2'
          )} User ${user.name} ?`,
          rbDanger: true,
          rbLabel: this.$t('setup.users_management.delete'),
        })
        .then(value => {
          if (value) {
            API.post('/setup/deleteUser', formData).then(({ error }) => {
              if (error) {
                this.$message.error(
                  this.$t('setup.users_management.unable_to_delete')
                )
              } else {
                this.$message.success(
                  this.$t('setup.users_management.user_delete_success')
                )
                this.loadPersonSummary()
                this.$store.dispatch('loadUsers', true)
              }
            })
          }
        })
    },
    async reInvite() {
      let { selectedPerson } = this
      let { userContext: user } = selectedPerson || {}
      let { error } = await API.post('setup/resendinvite', {
        userId: user.id,
        isPortal: true,
      })
      if (!error) {
        this.$message.success(
          this.$t('setup.users_management.invitation_send_success')
        )
      } else {
        this.$message.error(this.$t('setup.users_management.error_occured'))
      }
    },
    editPerson(person) {
      this.showPersonDialog = true
      this.selectedPerson = person
    },
    editLabour() {
      let { selectedPerson } = this
      let { labourContext } = selectedPerson || {}

      this.selectedLabour = labourContext
      this.isNewLabour = false
      this.showLaborForm = true
    },
    openUserSummary() {
      let { selectedPerson, appId } = this
      let { userContext: user } = selectedPerson || {}

      this.$router.push({
        name: 'userSummary',
        params: {
          id: user.id,
          appId,
        },
      })
    },
    async deleteLabour() {
      let { selectedPerson } = this
      let { labourContext } = selectedPerson || {}
      let { id: laborId } = labourContext || {}
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management.delete_labor'),
        message: this.$t('setup.users_management.are_you_sure_delete_people'),
        rbDanger: true,
        rbLabel: 'Delete',
      })

      if (value) {
        let { error } = await API.deleteRecord('labour', laborId)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.loadPersonSummary()
          this.$message.success(
            this.$t('setup.users_management.delete_labor_successfully')
          )
        }
      }
    },
    reLoadPerson() {
      this.showUserForm = false
      this.showLaborForm = false
      this.showPersonDialog = false
      this.loadPersonSummary()
    },
    closeDialog(canClose) {
      if (canClose) {
        this.showPersonDialog = false
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.association-empty-state {
  display: flex;
  flex-direction: column;
}
.people-header {
  height: 80px !important;
}
.user-associated {
  height: fit-content;
}
.user-not-associated {
  height: 300px;
}
.section-header {
  font-size: 16px;
  font-weight: 500;
  margin-left: 15px;
  margin-top: 10px;
}
.context-area {
  height: 150px;
  padding: 30px;
}
.context-actions {
  font-size: 16px;
}
.reinvite-txt {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
  }
}
.summary-sub-header {
  border-bottom: 0.3px solid #d8d8d8;
}
.fc-people-summary-scroll {
  height: calc(100vh - 225px);
  overflow: hidden;
  overflow-y: scroll;
}
</style>
