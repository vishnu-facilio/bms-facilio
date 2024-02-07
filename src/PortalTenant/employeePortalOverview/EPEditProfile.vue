<template>
  <div class="editprofile-portal-container">
    <div class="user-detail-container">
      <div>
        <template v-if="edit">
          <div class="p5">
            <el-form
              :model="userClone"
              label-width="120px"
              class="fc-ep-form"
              label-position="left"
            >
              <el-form-item :label="$t('common.products.full_name')">
                <el-input
                  v-model="userClone.name"
                  class="fc-input-full-border-select2 width80"
                ></el-input>
              </el-form-item>
              <el-form-item :label="$t('setup.setup_profile.email')">
                <el-input
                  disabled
                  v-model="userClone.email"
                  class="fc-input-full-border-select2 width80"
                ></el-input>
              </el-form-item>
              <el-form-item :label="$t('setup.setup_profile.language')">
                <el-select
                  v-model="userClone.language"
                  class="fc-input-full-border-select2 width80"
                  :placeholder="$t('setup.setup.select_language')"
                >
                  <el-option
                    v-for="(language, index) in languageList"
                    :key="index"
                    :label="language.label"
                    :value="language.value"
                  ></el-option>
                </el-select>
              </el-form-item>

              <el-form-item :label="$t('setup.setup_profile.phone')">
                <el-input
                  v-model="userClone.phone"
                  :placeholder="$t('common.placeholders.enter_phone_number')"
                  class="fc-input-full-border-select2 width80"
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="d-flex mT20 pR5 pL5">
            <el-button
              @click="saveProfile"
              :loading="saving"
              class="btn fc-ep-save-btn mR20"
            >
              {{
                saving
                  ? $t('common._common._saving')
                  : $t('common._common._save')
              }}
            </el-button>
            <div class="fc-ep-cancel-btn pointer" @click="cancelEdit">
              {{ 'Cancel' }}
            </div>
          </div>
        </template>
        <template v-else>
          <div class="flex-spacebetween">
            <div class="p5">
              <div class="mB35">
                <div class="fc-text-16">{{ 'Full Name' }}</div>
                <div class="fc-text-14">{{ user.name || '---' }}</div>
              </div>
              <div class="mB25">
                <div class="fc-text-16 mB3">{{ 'E-mail' }}</div>
                <CopyToClipboard :inputText="user.email"></CopyToClipboard>
              </div>
              <div class="mB35">
                <div class="fc-text-16">{{ 'Language' }}</div>
                <div class="fc-text-14">{{ user.language || '---' }}</div>
              </div>
              <div class="mB35">
                <div class="fc-text-16">{{ 'Phone' }}</div>
                <div class="fc-text-14">{{ user.phone || '---' }}</div>
              </div>
              <div></div>
            </div>
            <div>
              <div
                class="btn-edit"
                @click="
                  () => {
                    this.edit = true
                  }
                "
              >
                <div class="mR10">
                  <InlineSvg
                    src="svgs/employeePortal/ic-edit"
                    icon-class="xxxs"
                  ></InlineSvg>
                </div>
                <div class="fc-text-16 mB0">
                  {{ 'Edit' }}
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import InlineSvg from '../../components/InlineSvg.vue'
import { mapState } from 'vuex'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import CopyToClipboard from 'src/PortalTenant/employeePortalOverview/CopyToClipboard.vue'

export default {
  data() {
    return {
      edit: false,
      submit: false,
      languageList: Constants.languages,
      user: {
        name: null,
        email: null,
        timezone: null,
        language: null,
        street: null,
        city: null,
        state: null,
        zip: null,
        country: null,
        phone: null,
        mobile: null,
        uid: -1,
      },
      saving: false,
      userClone: {},
    }
  },
  components: { InlineSvg, CopyToClipboard },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
  },
  created() {
    let language = null
    if (this.account.user.language) {
      language = this.languageList.find(
        language => language.value === this.account.user.language
      )
    }
    let {
      account: { user },
    } = this
    let { name, email, phone, mobile, street, state, city, zip, uid } = user
    this.user = {
      name,
      uid: uid || -1,
      email,
      phone,
      mobile,
      street,
      state,
      city,
      zip,
      language: language ? language.value : null,
    }
  },
  mounted() {
    this.userClone = cloneDeep(this.user)
  },
  methods: {
    cancelEdit() {
      let { user } = this
      this.userClone.name = user.name
      this.userClone.email = user.email
      this.userClone.language = user.language
      this.userClone.phone = user.phone
      this.edit = false
    },
    close() {
      alert('close dialog')
    },
    async saveProfile() {
      this.saving = true
      let userObj = this.$helpers.cloneObject(this.userClone)
      let { error } = await API.post('/account/update', { user: userObj })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.$emit('close', true)
      } else {
        this.$message.success(
          this.$t('setup.setup_profile.user_profile_details')
        )
        this.$helpers.delay(1000).then(() => location.reload())
      }
      this.saving = false
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied to clipboard!',
        type: 'success',
      })
    },
  },
}
</script>
<style scoped>
.flex-spacebetween {
  display: flex;
  justify-content: space-between;
}
.editprofile-portal-container {
  background-color: #ffffff;
  height: inherit;
  width: 600px;
  padding: 20px;
  display: flex;
  margin: auto;
  border-radius: 8px;
}
.user-detail-container {
  max-height: 385px;
  overflow-y: scroll;
  width: 100%;
}
.fc-text-16 {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 16px;
  letter-spacing: normal;
  color: #324056;
  margin-bottom: 12px;
}
.fc-text-14 {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 15px;
  letter-spacing: 0.34px;
  color: #737376;
}
.fc-blue-text-14 {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: 0.34px;
  color: #0053cc;
}
.fc-blue-text-14:hover {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: 0.34px;
  color: #0053cc;
  text-decoration: underline;
}
.copy-img {
  margin-left: 10px;
  height: 32px;
  width: 32px;
  cursor: pointer;
}
.copy-img:hover {
  background-color: #f0f0f0;
  border-radius: 15px;
  cursor: pointer;
}
.mB35 {
  margin-bottom: 35px;
}
.btn-edit {
  display: flex;
  border-radius: 3px;
  padding: 5px 5px 1px 5px;
  cursor: pointer;
}
.btn-edit:hover {
  display: flex;
  border-radius: 3px;
  background-color: #f0f0f0;
  padding: 5px 5px 1px 5px;
  cursor: pointer;
}
.fc-ep-save-btn {
  line-height: 1;
  display: inline-block;
  border: none !important;
  font-size: 12px;
  letter-spacing: 0.7px !important;
  font-weight: 500;
  cursor: pointer;
  color: #ffffff;
  border-radius: 3px;
  background-color: #0053cc;
  width: 100px;
  height: 32px !important;
}
.fc-ep-cancel-btn {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: normal;
  color: #0053cc;
}
.fc-ep-cancel-btn:hover {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: normal;
  color: #0053cc;
  text-decoration: underline;
}
.mB3 {
  margin-bottom: 3px !important;
}
</style>
<style>
.fc-ep-form .el-form-item {
  margin-bottom: 20;
}
</style>
