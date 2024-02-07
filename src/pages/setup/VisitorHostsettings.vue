<template>
  <div>
    <div class="visitor-setting-con-width mT20">
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="22">
            <div class="fc-black-15 fwBold">Host</div>
            <div class="fc-grey4-13 pT5">
              Ask visitors whom they want to meet.
            </div>
          </el-col>
          <el-col :span="2" class="text-right">
            <div>
              <div class="">
                <el-switch
                  v-model="visitorTypeSettings.hostEnabled"
                  @change="updateHostSettings"
                ></el-switch>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="22">
            <div class="fc-black-15 fwBold">Host</div>
            <div class="fc-grey4-13 pT5">
              Host mandatory
            </div>
          </el-col>
          <el-col :span="2" class="text-right">
            <div>
              <div class="">
                <el-switch
                  v-model="visitorTypeSettings.hostSettings.required"
                  @change="updateHostSettings"
                ></el-switch>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="22">
            <div class="fc-black-15 fwBold">Host Approval</div>
            <div class="fc-grey4-13 pT5">
              Visitors must choose a host to proceed
            </div>
          </el-col>
          <el-col :span="2" class="text-right">
            <div>
              <div class="">
                <el-switch
                  v-model="visitorTypeSettings.hostSettings.requireApproval"
                  :disabled="!visitorTypeSettings.hostEnabled"
                  @change="updateHostSettings"
                ></el-switch>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <div class="label-txt-black pB20 fwBold">Host Type</div>
        <el-radio-group
          v-model="visitorTypeSettings.hostSettings.hostType"
          @change="updateHostSettings"
          :disabled="!visitorTypeSettings.hostEnabled"
        >
          <el-radio label="1" class="fc-radio-btn">Tenant</el-radio>
          <el-radio label="3" class="fc-radio-btn">Employee</el-radio>
        </el-radio-group>
      </div>
    </div>
  </div>
</template>

<script>
import { updateVisitorSettings } from 'src/devices/VisitorKiosk/VisitorApis'
export default {
  props: ['visitorTypeSettings'],
  data() {
    return {
      value1: 'Option1',

      options: [
        {
          value: 'Option1',
          label: 'Option1',
        },
        {
          value: 'Option2',
          label: 'Option2',
        },
        {
          value: 'Option3',
          label: 'Option3',
        },
        {
          value: 'Option4',
          label: 'Option4',
        },
        {
          value: 'Option5',
          label: 'Option5',
        },
      ],
      value: '',
    }
  },
  methods: {
    updateHostSettings() {
      updateVisitorSettings(this.visitorTypeSettings)
    },
  },
}
</script>
