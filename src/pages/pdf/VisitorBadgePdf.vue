<template>
  <div>
    <div class="header-sidebar-hide">
      <div class="height100 scrollable">
        <div v-if="loading">Loading...</div>
        <div v-else-if="$validation.isEmpty(recordList)">
          <div>No Data</div>
        </div>
        <div class="visitor-badge-con-print" v-else>
          <div
            v-for="(visitorLog, index) in recordList"
            :key="index"
            class="visitor-invite-badge"
          >
            <div
              class="pL20 pR20 pT30 pB30"
              style="border: 2px solid rgb(0, 0, 0,0.1); border-radius: 3px;"
            >
              <div class="d-flex">
                <div class="width30">
                  <div class="kiosk-profile-con">
                    <div
                      class="kiosk-image-holder flex-middle justify-content-center"
                      v-if="visitorLog.avatarUrl"
                    >
                      <img
                        :src="$prependBaseUrl(visitorLog.avatarUrl)"
                        alt=""
                        title=""
                        width="150"
                        height="150"
                      />
                    </div>
                    <div class="kiosk-image-holder-empty" v-else>
                      <img
                        src="~assets/user.svg"
                        alt=""
                        title=""
                        width="50"
                        height="150"
                      />
                    </div>
                  </div>
                  <div
                    class="kiosk-qr-con mT20 flex-middle justify-content-center"
                  >
                    <div class="kiosk-qr-holder">
                      <qriously
                        :value="'passCode_' + visitorLog.passCode"
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
                          visitorLog.visitorName
                            ? visitorLog.visitorName
                            : '---'
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
                          visitorLog.visitorPhone
                            ? visitorLog.visitorPhone
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="border-bottom1px pT15 pB15 flex-middle">
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Email id</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{
                          visitorLog.visitorEmail
                            ? visitorLog.visitorEmail
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    class="border-bottom1px pT15 pB15 flex-middle"
                    v-if="visitorLog.purposeOfVisit"
                  >
                    <el-col :span="6">
                      <div class="kiosk-form-label-print">Purpose of visit</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        {{ getPurposeOfVisit(visitorLog) }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    class="pT15 pB15 flex-middle"
                    v-if="visitorLog.visitor.signatureUrl"
                  >
                    <el-col :span="6">
                      <div class="kiosk-form-label-print pT15">Signature</div>
                    </el-col>
                    <el-col :span="16">
                      <div class="kiosk-form-data-print">
                        <img
                          style="width: 150px"
                          :src="visitorLog.visitor.signatureUrl"
                        />
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
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { pascalCase } from '@facilio/utils/filters'

export default {
  data() {
    return {
      loading: false,
      recordList: [],
    }
  },
  created() {
    if (this.visitId) {
      this.loadVisitorLog()
    }
  },
  computed: {
    visitId() {
      let { query } = this.$route
      let { visitId } = query || {}
      return visitId
    },
  },
  methods: {
    async loadVisitorLog() {
      this.loading = true

      let { visitId } = this
      let filters = {
        id: {
          operatorId: 9,
          value: [visitId],
        },
      }
      let params = {
        viewname: 'all',
        page: 1,
        perPage: 50,
        filters: JSON.stringify(filters),
      }
      let { list, error } = await API.fetchAll('visitorlog', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.recordList = list || []
      }
      this.loading = false
    },
    getPurposeOfVisit(visitorLog) {
      let { purposeOfVisit } = visitorLog || {}
      let options = {
        1: 'Meeting',
        2: 'Conference',
        3: 'Delivery',
        4: 'Maintenance',
        5: 'Personal Visit',
        6: 'Inspection',
      }

      return !isEmpty(purposeOfVisit)
        ? pascalCase(options[purposeOfVisit])
        : '---'
    },
  },
}
</script>
