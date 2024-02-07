<template>
  <div>
    <div class="header-sidebar-hide">
      <div class="height100 scrollable">
        <div v-if="loading">Loading...</div>
        <div v-else-if="!visitorInvite">
          Invalid visitor invite !!!
        </div>
        <div class="visitor-badge-con-print" v-else>
          <div class="visitor-invite-badge">
            <div
              class="pL20 pR20 pT30 pB30"
              style="border: 2px solid rgb(0, 0, 0,0.1); border-radius: 3px;"
            >
              <div class="d-flex">
                <div class="width30">
                  <div class="kiosk-profile-con">
                    <div
                      class="kiosk-image-holder flex-middle justify-content-center"
                      v-if="visitorInvite.visitorId.avatarUrl"
                    >
                      <img
                        :src="visitorInvite.visitorId.avatarUrl"
                        alt=""
                        title=""
                        width="150"
                        height="150"
                      />
                    </div>
                    <div class="kiosk-image-holder-empty" v-else>
                      <img
                        src="~statics/icons/user.png"
                        alt=""
                        title=""
                        width="70"
                        height="70"
                      />
                    </div>
                  </div>
                  <div
                    class="kiosk-qr-con mT20 flex-middle justify-content-center"
                  >
                    <div class="kiosk-qr-holder">
                      <qriously
                        :value="
                          'invite_' +
                            visitorInvite.inviteId.id +
                            '_' +
                            visitorInvite.visitorId.id
                        "
                        :size="100"
                        :level="'H'"
                      />
                    </div>
                  </div>
                </div>
                <div class="width70 pL30">
                  <el-row class="border-bottom1px pB15 flex-middle">
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Name</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{
                          visitorInvite.visitorId.name
                            ? visitorInvite.visitorId.name
                            : ''
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="border-bottom1px pT15 pB15 flex-middle">
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Mobile</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{
                          visitorInvite.visitorId.phone
                            ? visitorInvite.visitorId.phone
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    class="border-bottom1px pT15 pB15 flex-middle"
                    v-if="visitorInvite.visitorId.email"
                  >
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Email</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{ visitorInvite.visitorId.email }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    class="border-bottom1px pT15 pB15 flex-middle"
                    v-if="
                      visitorInvite.inviteId.inviteHost &&
                        visitorInvite.inviteId.inviteHost.name
                    "
                  >
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Host</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{
                          visitorInvite.inviteId.inviteHost &&
                            visitorInvite.inviteId.inviteHost.name
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="border-bottom1px pT15 pB15 flex-middle">
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Invite Time</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{
                          $helpers
                            .getOrgMoment(
                              visitorInvite.inviteId.expectedStartTime
                            )
                            .format('dddd, MMMM DD') +
                            ' at ' +
                            $helpers
                              .getOrgMoment(
                                visitorInvite.inviteId.expectedStartTime
                              )
                              .format('hh:mm a')
                        }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      visitorInvite: null,
    }
  },
  mounted() {
    this.loadVisitorInfo()
  },
  computed: {
    inviteId() {
      return this.$route.params.inviteid
    },
    visitorId() {
      return this.$route.params.visitorid
    },
  },
  methods: {
    loadVisitorInfo() {
      this.loading = true
      this.visitorInvite = null
      this.$http
        .post('/v2/visitorInvite/getVisitorInviteRel', {
          inviteId: this.inviteId,
          visitorId: this.visitorId,
        })
        .then(response => {
          this.visitorInvite = response.data.result.visitorinviterel
          this.loading = false
        })
    },
  },
}
</script>
