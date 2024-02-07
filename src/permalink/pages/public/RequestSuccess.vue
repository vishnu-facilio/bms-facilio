<template>
  <div class="thank-you-container">
    <div class="thankyou-bg">
      <div class="thank-you-txt">Thank You</div>
      <div class="thank-you-desc">
        Your request has been submitted successfully , your ticket Id is
        <span class="ticket-id-blue"> # {{ ticketId }}</span> <br />.
      </div>
      <el-button class="go-back-btn mT10" @click="backToSubmit">
        {{ $t('setup.setupLabel.go_back') }}
      </el-button>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    ticketId() {
      return this.$route.params.id
    },
  },
  methods: {
    backToSubmit() {
      this.$router.push({ path: '/link/submitrequest' })
    },
  },
}
</script>

<style lang="scss" scoped>
.thank-you-container {
  background-color: rgb(244, 244, 244);
  height: 100%;
}
.thankyou-bg {
  width: 100%;
  max-width: 500px;
  height: 250px;
  max-height: 100%;
  background-color: #ffffff;
  margin-left: auto;
  margin-right: auto;
  position: relative;
  top: 170px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 7px;
}
.ticket-id-blue {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #39b2c2;
  margin: 0;
}
.grey-bg {
  height: 100vh;
  background: #fbfbfd;
  padding-left: 0;
}
.layout-page-container {
  padding-top: 0 !important;
}
.thank-you-txt {
  font-size: 28px;
  letter-spacing: 0;
  font-weight: 500;
  text-align: center;
  color: #324056;
  padding-top: 40px;
}
.thank-you-desc {
  font-size: 16px;
  color: #181b3b;
  letter-spacing: 0;
  text-align: center;
  line-height: 19px;
  margin-top: 20px;
  width: 350px;
}

.go-back-btn {
  border: solid 0.7px #f6f2ff;
  background: #4e36a6;
  font-size: 14px;
  color: #ffffff;
  text-align: center;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.8px;
  border-radius: 30px !important;
  padding: 14px 30px;
  &:hover {
    color: white;
    background: #4e36a6d4;
  }
}
</style>
