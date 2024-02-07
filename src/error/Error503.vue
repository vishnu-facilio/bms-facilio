<template>
  <div class="error-page column no-wrap error-page-503">
    <el-header class="error-page-503-header">
      <img class="fc-logo" src="~assets/facilio-blue-logo.svg" />
    </el-header>
    <div>
      <div class="error-page-503-block">
        <div class="">
          <div class="error-txt fwBold">We’re under Scheduled Maintenance</div>
          <div class="error-sub-txt">{{ message }}</div>
          <div v-if="startTime" class="">
            <el-tag class="error-page-503-tag">
              {{ startTime | format }} - {{ endTime | format }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="error-image">
        <img
          src="~assets/svgs/maintenance-page.svg"
          style="width: 100%; height: auto;"
        />
        <div class="fc-maintenance-copy">
          Facilio Inc © 2022
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import http from 'util/http.js'
import moment from 'moment-timezone'
export default {
  data() {
    return {
      canGoBack: window.history.length > 1,
      message: null,
      startTime: null,
      endTime: null,
    }
  },
  created() {
    this.fetchDetails()
  },
  filters: {
    format(time) {
      return moment(time)
        .tz(moment.tz.guess())
        .format('DD MMM, hh:mm A z')
    },
  },
  methods: {
    goBack() {
      window.history.go(-1)
    },
    fetchDetails() {
      http
        .get('/v2/fetchAccount')
        .then(() => {
          this.$router.replace({ path: '/app' })
        })
        .catch(error => {
          if (error.response.status === 503) {
            let { message, startTime, endTime } = error.response.data
            this.message = message
            this.startTime = startTime
            this.endTime = endTime
          }
        })
    },
  },
}
</script>

<style>
.error-page-503 {
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
  background: #ffffff !important;
}

.error-page-503 .error-image {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}
.error-page-503-header {
  height: auto;
  padding: 20px 120px;
  border-bottom: 1px solid #ebebed;
}

.error-page .error-card {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-page .error-card > i {
  font-size: 5rem;
}

.error-btn {
  border: solid 0.7px #f6f2ff;
  background: #4e36a6 !important;
  border-radius: 20px;
  font-size: 14px;
  color: #ffffff;
  letter-spacing: 0;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.8px;
  border-radius: 30px !important;
  padding: 14px 30px;
}

.error-page-503 .error-txt {
  font-size: 28px;
  color: #5a56bc !important;
  font-weight: 600;
  letter-spacing: normal;
  padding-bottom: 10px;
}

.error-page-503 .error-sub-txt {
  font-size: 20px;
  color: #5a56bc !important;
  letter-spacing: 0;
  padding-right: 130px;
  padding-bottom: 10px;
  line-height: 30px;
}

.error-page-503 .error-sub-txt2 {
  font-size: 16px;
  color: #5a56bc !important;
  letter-spacing: 0;
  line-height: 19px;
  padding-bottom: 20px;
}
.error-btn1 {
  margin-right: 20px !important;
}
.error-page-503-tag {
  font-size: 14px;
  font-weight: 600;
  background-color: #f1f1ff;
  letter-spacing: 0.45px;
  color: #5a56bc;
  margin-top: 20px;
}
.error-page-503-block {
  max-width: 900px;
  margin-left: auto;
  padding-top: 58px;
}
.fc-maintenance-copy {
  font-size: 18px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.56px;
  text-align: center;
  padding-top: 10px;
  padding-bottom: 10px;
  color: #9486bb;
}
</style>
