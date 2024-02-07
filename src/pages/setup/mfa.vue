<template>
  <div class="height100 d-flex flex-direction-column overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.header.multi_factor') }}
        </div>
        <div class="heading-description">
          {{ $t('common.header.using_multifactor_authentication') }}
        </div>
      </div>
    </div>
    <div class="container-scroll">
      <el-row>
        <el-col :span="12">
          <div class="d-flex flex-direction-row mL20 mR20 flex-wrap mB10">
            <div class="visitor-hor-card scale-up-left">
              <el-row class="flex-middle">
                <el-col :span="20">
                  <div class="fc-black-15 fwBold">
                    {{ $t('common.wo_report.time_based_otp') }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ $t('common.wo_report.time_based_otp_button') }}
                  </div>
                </el-col>
                <el-col :span="3" class="text-right">
                  <div class="flex-middle">
                    <div class="label-txt-black pL10 fw6">
                      <el-switch
                        v-model="value1"
                        @change="toggleAll()"
                      ></el-switch>
                    </div>
                    <div class="label-txt-black pL10 fw6"></div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <!-- <div class="d-flex flex-direction-row mL20 mR20 flex-wrap mb10">
            <div class="visitor-hor-card scale-up-left">
          <el-row class="flex-middle">
            <el-col :span="20">
              <div class="fc-black-15 fwBold">Mobile Based OTP</div>
              <div class="fc-grey4-13 pT5">
                Registering your mobile number for authentication will help you
                secure your accoutn from unauthorised access
              </div>
            </el-col>
            <el-col :span="3" class="text-right">
              <div class="flex-middle">
                <div class="label-txt-black pL10 fw6">
                  <el-switch v-model="value2" @change="chg(state)"></el-switch>
                </div>
                <div class="label-txt-black pL10 fw6">
                  {{ state }}
                </div>
                  <el-switch v-model="value2"></el-switch>
                </div>
                <div class="label-txt-black pL10 fw6"></div>
            </el-col>
          </el-row>
            </div>
          </div> -->
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import http from 'util/http'
export default {
  data() {
    return {
      value1: null,
      value2: false,
      totpstate: '',
    }
  },
  created() {
    this.loadMfaData()
  },
  methods: {
    toggleAll() {
      if (this.value1 == false) {
        let res = this.$http
          .post('v2/settings/org/totpdisabled', {})
          .then(
            this.$message.success(this.$t('common.wo_report.totp_disabled'))
          )
          .catch(function(error) {
            if (error) {
              console.log(error)
            }
          })
        this.value1 = false
        this.totpstate = false
      } else if (this.value1 == true) {
        let res = this.$http
          .post('v2/settings/org/totpenable', {})
          .then(this.$message.success(this.$t('common.wo_report.totp_enabled')))
          .catch(function(error) {
            if (error) {
              console.log(error)
            }
          })
        this.value1 = true
        this.totpstate = true
      }
    },
    async loadMfaData() {
      let resp = await this.$http.get('v2/settings/org/mfa')

      this.totpstate = resp.data.result.mfaSettings.totpEnabled

      this.value1 = this.totpstate
    },
  },
}
</script>
