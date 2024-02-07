<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      :model="team"
      :rules="rules"
      :label-position="'top'"
      ref="teamForm"
      label-width="120px"
      class="fc-form"
    >
      <!-- header -->

      <div class="new-header-container new-header-text fc-setup-modal-title">
        {{ formHeader }}
      </div>
      <!-- body -->
      <div class="new-body-modal mT10">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item
              prop="name"
              :label="$t('setup.users_management.team_name')"
              :required="true"
            >
              <el-input
                :autofocus="true"
                v-model="team.name"
                :placeholder="$t('setup.users_management.enter_your_team')"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="mT10">
          <el-col :span="24">
            <el-form-item
              prop="description"
              :label="$t('setup.approvalprocess.description')"
            >
              <el-input
                :placeholder="$t('common._common.enter_desc')"
                v-model="team.description"
                autoComplete="off"
                :autosize="{ minRows: 3, maxRows: 6 }"
                type="textarea"
                resize="none"
                class="fc-input-full-border-select2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              prop="siteId"
              :label="$t('setup.users_management.site')"
            >
              <Lookup
                v-model="team.siteId"
                :field="fields.site"
                :hideLookupIcon="true"
              ></Lookup>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              prop="members"
              :label="$t('setup.users_management.people')"
              :required="true"
            >
              <Lookup
                v-model="team.members"
                :field="fields.people"
                :hideDropDown="true"
                @showLookupWizard="showPeopleLookupWizard"
              ></Lookup>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item prop="email" :label="$t('setup.setup_profile.email')">
              <el-input
                v-model="team.email"
                type="text"
                autocomplete="off"
                :placeholder="
                  $t('setup.users_management.please_enter_the_email')
                "
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}</el-button
        >
        <el-button
          type="primary"
          @click="saveTeam()"
          :loading="saving"
          class="modal-btn-save"
          >{{ saveBtnText }}
        </el-button>
      </div>
    </el-form>
    <template v-if="canShowLookupWizard">
      <LookupWizard
        v-if="checkNewLookupWizardEnabled"
        :canShowLookupWizard.sync="canShowLookupWizard"
        :field="selectedLookupField"
        @setLookupFieldValue="setPeople"
      ></LookupWizard>
      <FLookupFieldWizard
        v-else
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        @setLookupFieldValue="setPeople"
      ></FLookupFieldWizard>
    </template>
  </el-dialog>
</template>
<script>
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import { Lookup, LookupWizard } from '@facilio/ui/forms'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'

const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
  people: {
    isDataLoading: false,
    options: [],
    config: {},
    lookupModuleName: 'people',
    field: {
      lookupModule: {
        name: 'people',
        displayName: 'People',
      },
    },
    filters: {
      user: {
        operatorId: 15,
        value: ['true'],
      },
    },
    multiple: true,
  },
}

export default {
  name: 'NewTeam',
  props: ['isNew', 'visibility', 'selectedTeam'],
  components: { Lookup, LookupWizard, FLookupFieldWizard },
  data() {
    return {
      fields,
      selected: [],
      saving: false,
      team: {
        name: '',
        description: '',
        members: [],
        siteId: null,
        email: '',
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t('setup.users_management.please_enter_name'),
            trigger: 'blur',
          },
        ],
        members: [
          {
            required: true,
            message: this.$t(
              'setup.users_management.please_select_people_for_team'
            ),
            trigger: 'blur',
          },
        ],
      },
      selectedLookupField: null,
      canShowLookupWizard: false,
    }
  },
  computed: {
    checkNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    formHeader() {
      let { isNew } = this
      return isNew
        ? this.$t('setup.users_management.new_team')
        : this.$t('setup.users_management.edit_team')
    },
    saveBtnText() {
      let { saving } = this
      return saving
        ? this.$t('common._common._saving')
        : this.$t('common._common._save')
    },
    moduleName() {
      return 'peopleGroup'
    },
  },
  created() {
    this.$store.dispatch('loadSite')
    let { isNew, selectedTeam } = this
    if (!isNew && !isEmpty(selectedTeam)) {
      this.team = cloneDeep(selectedTeam)
      this.deserializeTeam()
    }
  },
  methods: {
    showPeopleLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    deserializeTeam() {
      let { team } = this
      let { members = [] } = team || {}
      let memberIds = []
      let memberObj = []
      let serializedMembers = members.map(member => {
        let { people, id } = member || {}
        let { id: peopleId } = people || {}
        let obj = { id, people: { id: peopleId } }
        memberObj.push(obj)
        memberIds.push(id)
        return peopleId
      })
      this.$set(this.team, 'members', serializedMembers)
      this.$set(this.team, 'memberIds', memberIds)
      this.$set(this.team, 'memberObj', memberObj)
    },
    setPeople(props) {
      let { field } = props
      let { options = [], selectedItems = [] } = field
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = (selectedItems || []).map(item => {
          return item.value
        })
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )

          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      this.$set(this.team, 'members', selectedItemIds)
    },
    validateEmail(email) {
      if (isEmpty(email)) {
        return true
      } else {
        email = email.trim()
        let emailRegx = /^(([^<>()\\[\]\\.,;:\s@"]+(\.[^<>()\\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        if (!emailRegx.test(email)) {
          return false
        }
        return true
      }
    },
    closeDialog() {
      this.selectedLookupField = null
      this.$emit('closeDialog', true)
    },
    serializeTeam(team) {
      let { members = [] } = team || {}
      let { memberObj = [] } = team || {}
      let peopleGroupMember = members.map(memberId => {
        return { people: { id: memberId } }
      })
      let deltedMemberIds = memberObj.map(old => {
        let id = members.find(newPeopleId => newPeopleId === old.people.id)
        if (isEmpty(id)) return old.id
      })
      deltedMemberIds = deltedMemberIds.filter(n => n)
      delete team['members']
      return { ...team, deltedMemberIds, peopleGroupMember, isActive: true }
    },
    async saveTeam() {
      let valid = await this.$refs['teamForm'].validate()
      let { team } = this
      let { memberObj } = team || {}
      let { email } = team || {}
      let emailValid = this.validateEmail(email)
      if (valid && emailValid) {
        this.isSaving = true
        if (!valid) return
        let { team, moduleName, isNew } = this
        team = this.serializeTeam(team)
        let promise
        if (isNew) {
          promise = await API.createRecord(moduleName, {
            data: team,
          })
        } else {
          let { id } = team || {}
          let { peopleGroupMember } = team || {}
          team.peopleGroupMember = peopleGroupMember.map(member => {
            let existingMemberobj = memberObj.find(
              existingMember => existingMember.people.id == member.people.id
            )
            return !isEmpty(existingMemberobj)
              ? { ...existingMemberobj }
              : { ...member }
          })
          delete team['memberObj']
          delete team['memberIds']
          let { deltedMemberIds } = team || []
          if (!isEmpty(deltedMemberIds)) {
            let { error } = await API.deleteRecord(
              'peopleGroupMember',
              deltedMemberIds
            )
            if (error) {
              let { message } = error
              this.$message.error(
                message ||
                  this.$t(`setup.users_management.delete_team_member_failure`)
              )
            } else {
              this.$message.success(
                this.$t(`setup.users_management.delete_team_member_success`)
              )
            }
          }
          delete team['deltedMemberIds']
          promise = await API.updateRecord(moduleName, { id, data: team })
        }

        let { error } = promise || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$emit('teamSaved')
          this.closeDialog()
          if (isNew) {
            this.$message.success(
              this.$t('setup.users_management.team_created')
            )
          } else {
            this.$message.success(
              this.$t('setup.users_management.team_updated')
            )
          }
        }

        this.isSaving = false
      } else {
        if (!emailValid) {
          this.$message.error(this.$t('setup.users_management._valid_email'))
        }
      }
    },
  },
}
</script>
