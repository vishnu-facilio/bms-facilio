<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form
      :model="user"
      :label-position="'top'"
      ref="ruleForm"
      @primary="inviteUser('ruleForm')"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('setup.users_management.requester') }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.approvalprocess.name') }}
            </p>
            <el-form-item prop="name">
              <el-input
                v-model="user.name"
                :autofocus="true"
                :placeholder="$t('setup.setup_profile.enter_the_name')"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt pB10">
                {{ $t('setup.users_management.requester_email') }}
              </p>
              <el-form-item prop="email">
                <el-input
                  v-model="user.email"
                  :placeholder="
                    $t('setup.users_management.enter_requester_email')
                  "
                  class="fc-input-full-border-select2 width100"
                ></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt pB10">
                {{ $t('setup.users_management.mobile_number') }}
              </p>
              <el-form-item prop="phone">
                <el-input
                  v-model.number="user.phone"
                  :placeholder="
                    $t('setup.users_management.enter_the_mobile_number')
                  "
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <!-- <el-row>
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt pB10">{{$t("setup.users_management.requester_email")}}</p>
              <el-form-item prop="email">
                <el-input v-model="user.email" :placeholder="$t('setup.users_management.enter_requester_email')"  class="fc-input-txt fc-desc-input width250px"></el-input>
              </el-form-item>
            </div>
          </el-col>

               <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">{{$t("setup.users_management.roles_accessibility")}}</div>
            </div>
        </el-row>-->
        <el-row class="resource-row">
          <el-col :span="12" class="f-element resource-list">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.accessible_spaces') }}
            </p>

            <el-form-item prop="email">
              <el-input
                v-model="selectedResourceLabel"
                disabled
                class="fc-input-full-border-select2 width100"
              >
                <i
                  @click="chooserVisibility = true"
                  slot="suffix"
                  style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                  class="el-input__icon el-icon-search"
                ></i>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="source !== 'tenant'">
          <el-row>
            <el-col :span="12">
              <el-switch v-model.number="user.portal_verified"></el-switch>
              <!-- @change="toggleAttribute(user)" active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"-->
              <span class>{{
                $t('setup.users_management.send_invitation')
              }}</span>
            </el-col>
          </el-row>
        </div>
        <div v-if="source === 'tenant'">
          <el-row class="mT30">
            <div>
              <el-col :span="12">
                <el-switch
                  v-model.number="user.portal_verified"
                  class="mR20"
                  active-color="#ef4f8f"
                  inactive-color="#e5e5e5"
                ></el-switch>
                {{ $t('setup.users_management.allow_portal_access') }}
              </el-col>
            </div>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog('ruleForm')" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="inviteUser('ruleForm')"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
      <space-asset-multi-chooser
        v-if="chooserVisibility"
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="resourceData"
        :resourceType="'2, 3, 4'"
        :showAsset="false"
        :hideBanner="true"
        :selectedResource="selectedeResourceObj"
      ></space-asset-multi-chooser>
    </el-form>
  </el-dialog>
</template>
<script>
import timezones from 'util/data/timezones'
import countries from 'util/data/countries'
import { mapState } from 'vuex'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import ErrorBanner from '@/ErrorBanner'
import Constants from 'util/constant'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: ['visibility', 'rule', 'source'],
  data() {
    return {
      selectedeResourceObj: [],
      selectedResourceLabel: null,
      saving: false,
      formTitle: 'Invite User',
      dialogVisible: false,
      error: false,
      chooserVisibility: false,
      errorText: '',
      user: {
        name: null,
        email: null,
        timezone: null,
        language: null,
        street: null,
        city: null,
        state: null,
        zip: null,
        portal_verified: null,
        country: null,
        phone: null,
        mobile: null,
        roleId: null,
        accessible_spaces: null,
        groups: [],
        userStatus: true,
        accessibleSpace: [],
        // roleId: -1
      },
      timezoneList: timezones,
      countryList: countries,
      languageList: Constants.languages,
    }
  },
  components: {
    SpaceAssetMultiChooser,
    ErrorBanner,
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      roles: state => state.roles,
    }),

    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),

    resourceData() {
      return {
        selectedResources:
          this.edituser &&
          this.edituser.accessibleSpace &&
          this.edituser.accessibleSpace.map(space => ({ id: space })),
      }
    },
  },
  methods: {
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        selectedObj.resourceList.forEach(element => {
          this.user.accessibleSpace.push(element.id)
        })
        this.selectedResourceLabel = this.resourceLabel(
          this.user.accessibleSpace
        )
      }
      this.chooserVisibility = false
    },
    resourceLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = 'Space'
        if (selectedCount) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        }
        return message
      }
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (!rule.name) {
        this.errorText = this.$t('setup.users_management.please_enter_name')
        this.error = true
      } else if (!rule.email) {
        this.errorText = this.$t(
          'setup.users_management.please_enter_the_email'
        )
        this.error = true
      }
      // else  (!rule.phone) {
      //   this.errorText = 'Please enter the Number'
      //   this.error = true
      // }
      else {
        this.errorText = ''
        this.error = false
      }
    },
    toggleAttribute(user) {
      let self = this
      let params = {
        user: {
          ouid: parseInt(user.ouid),
          portal_verified: user.portal_verified,
        },
      }
      self.$http
        .post('/tenant/updateportalaccess', params)
        .then(function(response) {})
        .then(function(response) {
          self.$message.success(
            self.$t('setup.users_management.portal_access_updated_success')
          )
          console.log('value updated ')
        })
    },
    inviteUser(ruleForm) {
      let self = this
      this.validation(self.user)
      if (this.error) {
        return
      }
      if (!this.source && this.source !== 'source') {
        let userObj = this.$helpers.cloneObject(this.user)
        let phone = /^(\+\d{11,13})/
        if (phone.test(userObj.email)) {
          userObj.mobile = userObj.email
          userObj.email = null
        }

        if (this.user.timezone) {
          userObj.timezone = this.user.timezone.value
        }
        if (this.user.language) {
          userObj.language = this.user.language.value
        }
        if (this.user.country) {
          userObj.country = this.user.country.value
        }
        self.saving = true
        this.$http
          .post(
            '/setup/inviterequester?emailVerificationNeeded=' +
              this.user.portal_verified,
            { user: userObj }
          )
          .then(function(response) {
            JSON.stringify(response)
            if (response.status === 200) {
              if (self.user.portal_verified) {
                self.$message.success(
                  self.$t('setup.users_management.invitation_send_success')
                )
              } else {
                self.$message.success('Requester Added Successfully')
              }
              self.saving = false
              self.$emit('update:visibility', false)
              self.$emit('saved')
              self.$refs[ruleForm].resetFields()
            } else {
              alert(JSON.stringify(response))
            }
            // self.$emit('close', true)
          })
          .catch(function(error) {
            self.saving = false

            if (error.response.data.fieldErrors.email[0] != null) {
              self.$message.error(error.response.data.fieldErrors.email[0])
            }
          })
        this.$refs[ruleForm].validate(valid => {
          if (valid) {
            this.$router.push({ path: '/app/setup/resource/pendingRequesters' })
          }
        })
      } else {
        this.saving = false
        this.$emit('update:visibility', false)
        this.$emit('saved', self.user)
      }
    },
    canceled() {
      this.error = false
      this.$emit('canceled')
    },
    close() {
      alert('close dialog')
    },
    save() {
      let self = this
      self.saving = true
    },
    closeDialog(ruleForm) {
      this.$refs[ruleForm].resetFields()
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style>
.styled-select select {
  background: transparent;
  border: none;
  font-size: 14px;
  height: 29px;
  padding: 5px;
  width: 100%;
}
.rounded {
  -webkit-border-radius: 20px;
  -moz-border-radius: 20px;
  border-radius: 20px;
}
.green {
  background-color: #f2fbeb;
}
</style>
