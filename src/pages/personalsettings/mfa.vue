<template>
  <div class="container width100 p27">
    <el-row>
      <el-col :span="12" class="p20 pT30 pB30 fc-border4">
        <TimeOTP v-if="formVisibility" :visibility.sync="formVisibility">
        </TimeOTP>
      </el-col>
      <el-col :span="12"> </el-col>
      <!-- <div class="d-flex flex-direction-row mL20 mR30 flex-wrap mB10" >
        <div class="modules-card mL20 mT25 mR25 mB25">
          <div
            class="fc-black-color f16 bold letter-spacing0_5 text-capitalize"
          >
            Mobile Based OTP
          </div>
          <div class="fc-grey4-13 pT5">
            Registering your mobile number for authentication will help you
            secure your accoutn from unauthorised access
          </div>
          <el-button class="setup-el-btn mT30 " @click="MobileBasedOTP()"
            >SETUP NOW {{status}}</el-button
          >
        </div>
      </div> -->
    </el-row>
    <!-- <MobileOTP v-if="formVisibility2" :visibility.sync="formVisibility2">
    </MobileOTP> -->
  </div>
</template>
<script>
import TimeOTP from './mfatimebasedotp'
export default {
  components: {
    TimeOTP,
  },
  data() {
    return {
      formVisibility: true,
    }
  },
}
</script>
