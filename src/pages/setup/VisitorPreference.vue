<template>
  <div class="visitor-setting-con-width mT20">
    <div class="visitor-hor-card scale-up-left">
      <el-row class="flex-middle">
        <el-col :span="22">
          <div class="fc-black-15 fwBold">Visitor photos</div>
          <div class="fc-grey4-13 pT5">
            Capture visitor photos during sign-in for easy recognition and
            increased security.
          </div>
        </el-col>
        <el-col :span="2" class="text-right">
          <div>
            <div class="">
              <el-switch
                v-model="visitorTypeSettings.photoEnabled"
                @change="updatePreferences"
              ></el-switch>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="visitor-hor-card scale-up-left">
      <el-row class="flex-middle">
        <el-col :span="22">
          <div class="fc-black-15 fwBold">Require photo each time</div>
          <div class="fc-grey4-13 pT5">
            Capture a fresh photo on each visit.
          </div>
        </el-col>
        <el-col :span="2" class="text-right">
          <div>
            <div class="">
              <el-switch
                v-model="
                  visitorTypeSettings.photoSettings.retakeReturningUserPhotos
                "
                @change="updatePreferences"
                :disabled="!visitorTypeSettings.photoEnabled"
              ></el-switch>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="visitor-hor-card scale-up-left">
      <el-row class="flex-middle">
        <el-col :span="22">
          <div class="fc-black-15 fwBold">Badge Printing</div>
          <div class="fc-grey4-13 pT5">Automatically print visitor badges.</div>
        </el-col>
        <el-col :span="2" class="text-right">
          <div>
            <div class="">
              <el-switch
                v-model="visitorTypeSettings.badgeEnabled"
                @change="updatePreferences"
              ></el-switch>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="visitor-hor-card scale-up-left">
      <el-row class="flex-middle">
        <el-col :span="22">
          <div class="fc-black-15 fwBold">ID scanning</div>
          <div class="fc-grey4-13 pT5">
            Require that visitors scan a valid, government-issued ID on the
            kiosk to come on-site.
          </div>
        </el-col>
        <el-col :span="2" class="text-right">
          <div>
            <div class="">
              <el-switch
                v-model="visitorTypeSettings.idScanEnabled"
                @change="updatePreferences"
              ></el-switch>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="visitor-hor-card scale-up-left">
      <el-row class="flex-middle">
        <el-col :span="22">
          <div class="fc-black-15 fwBold">
            Require approval for sending invites
          </div>
          <div class="fc-grey4-13 pT5">
            Visitor invites must be approved by ...
          </div>
        </el-col>
        <el-col :span="2" class="text-right">
          <div>
            <div class="">
              <el-switch
                v-model="visitorTypeSettings.approvalRequiredForInvite"
                @change="updatePreferences"
              ></el-switch>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { updateVisitorSettings } from 'src/devices/VisitorKiosk/VisitorApis'
export default {
  props: ['visitorTypeSettings'],
  data() {
    return {
      value1: true,
    }
  },
  methods: {
    updatePreferences() {
      updateVisitorSettings(this.visitorTypeSettings)
    },
  },
}
</script>
