<template>
  <div>
    <!-- need to add header org   here -->
    <div class="kiosk-print-header">
      <div class="fc-kiosk-f18 text-center">
        {{ currentAccount.org.name }}
      </div>
    </div>
    <div class="fc-kiosk-print-photo-con">
      <div class="fc-kiosk-print-photo-white-bg fc-scale-in-center">
        <div class="p30">
          <div class="d-flex">
            <div class="width30">
              <div class="kiosk-profile-con">
                <div class="kiosk-image-holder">
                  <img
                    :src="sharedData.avatarUrl"
                    alt=""
                    title=""
                    width="150"
                    height="150"
                  />
                </div>
              </div>
              <div class="kiosk-qr-con mT20 flex-middle justify-content-center">
                <div class="kiosk-qr-holder">
                  <qriously
                    :value="'passCode_' + sharedData.passCode"
                    :size="100"
                    :level="'H'"
                  />
                </div>
              </div>
            </div>
            <div class="width70 pL30">
              <el-row class="border-bottom1px pB20">
                <el-col :span="6">
                  <div class="kiosk-form-label">Name</div>
                </el-col>
                <el-col :span="18">
                  <div class="kiosk-form-data">
                    {{ sharedData.formFields.visitor.name }}
                  </div>
                </el-col>
              </el-row>
              <el-row class="border-bottom1px pT20 pB20">
                <el-col :span="6">
                  <div class="kiosk-form-label">Mobile</div>
                </el-col>
                <el-col :span="18">
                  <div class="kiosk-form-data">
                    {{ sharedData.formFields.visitor.phone }}
                  </div>
                </el-col>
              </el-row>
              <el-row class="border-bottom1px pT20 pB20">
                <el-col :span="6">
                  <div class="kiosk-form-label">Email id</div>
                </el-col>
                <el-col :span="18">
                  <div class="kiosk-form-data">
                    {{ sharedData.formFields.visitor.email }}
                  </div>
                </el-col>
              </el-row>
              <!-- <el-row class="border-bottom1px pT15 pB15">
                      <el-col :span="6">
                        <div class="kiosk-form-label">Company</div>
                      </el-col>
                      <el-col :span="18">
                        <div class="kiosk-form-data">Facilio</div>
                      </el-col>
                    </el-row> -->
              <el-row class="border-bottom1px pT15 pB15 flex-middle">
                <el-col :span="6">
                  <div class="kiosk-form-label">Signature</div>
                </el-col>
                <el-col :span="18">
                  <div class="kiosk-form-data">
                    <img style="width: 150px" :src="sharedData.signatureUrl" />
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>

      <div class="kiosk-actions-btn">
        <el-button class="kiosk-actions-black-fill" @click="handlePrint"
          >PRINT THIS</el-button
        >
        <el-button class="kiosk-actions-black-border" @click="handleDone"
          >DONE</el-button
        >
      </div>

      <div class="fc-kiosk-form-footer">
        <img
          src="~assets/facilio-logo-black.svg"
          style="width: 70px; height: 20px;"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
export default {
  created() {},
  data() {
    return {
      timer: null,
      // signatureBase64: null,
      avatar: null,
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
    ...mapState({
      sharedData: state => state.devices.sharedData,
    }),
  },
  mounted() {
    // this.$helpers.toBase64(this.sharedData.signature).then((result) => {
    //   this.signatureBase64 = result
    // })

    // this.$helpers.toBase64(this.sharedData.avatar).then((result) => {
    //   this.avatar = result
    // })

    //redirect to welcome page after shwoing thanks for 15 sec
    this.timer = window.setTimeout(() => {
      this.$router.replace({ name: 'welcome' })
    }, 30000)
  },
  beforeDestroy() {
    window.clearTimeout(this.timer)
  },
  methods: {
    handleDone() {
      // this.$emit('nextStep',{})
      this.$router.replace({ name: 'welcome' })
    },
    handlePrint() {
      window.print()
    },
  },
}
</script>

<style></style>
