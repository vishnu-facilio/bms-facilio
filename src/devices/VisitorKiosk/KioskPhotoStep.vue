<template>
  <div class="fc-kiosk-form-con fc-scale-in-center" style="height: 100vh;">
    <div class="fc-kiosk-form position-relative" v-if="!camError">
      <div class="fc-kiosk-photo">
        <div class="fc-kiosk-19">Hi {{ sharedData.name }}</div>
        <div class="fc-check-in-f22 pT10 font-bold">Say Cheese !</div>

        <div class="kiosk-photo-capture-box">
          <vue-web-cam
            ref="webCam"
            v-bind:width="400"
            v-bind:height="400"
            v-bind:selectFirstDevice="true"
            v-if="mountCam && !photoFromCam"
            @error="cameraError"
            @notsupported="camNotSupported"
          >
          </vue-web-cam>
          <!-- When old photo exists load the image from Url -->
          <img
            :src="photoFromCam || prefilledPhotoUrl"
            v-bind:width="400"
            v-bind:height="400"
            v-else
          />
        </div>

        <!-- need retake ,save  buttons -->
        <el-button
          v-if="mountCam && !isPhotoTaken"
          @click="takePhoto"
          class="fc-kiosk-black-btn mT20"
        >
          TAKE PHOTO
        </el-button>

        <el-button v-else @click="handleDone" class="fc-kiosk-black-btn mT20">
          DONE
        </el-button>
      </div>
      <div
        v-if="isPhotoTaken || !mountCam"
        class="kiosk-retake-txt "
        @click="retakePhoto"
      >
        <i class="el-icon-refresh pR5"> </i>Retake Photo
      </div>
    </div>
    <div class="photo-erro" v-if="camError">
      {{ camError }}
    </div>
  </div>
</template>

<script>
import { WebCam } from 'vue-web-cam'
import getProperty from 'dlv'

/* IF Existing photo present , check for settings and prefill it , else take a fresh photo
 */
export default {
  props: ['sharedData', 'photoSettings'],
  components: {
    'vue-web-cam': WebCam,
  },
  data() {
    return {
      prefilledPhotoUrl: null,
      isPhotoTaken: false,
      photoFromCam: null,
      mountCam: false,
      camError: null,
    }
  },
  created() {
    this.prefilledPhotoUrl = getProperty(
      this.sharedData,
      'preFill.avatarUrl',
      null
    )

    if (
      this.photoSettings.retakeReturningUserPhotos ||
      !this.prefilledPhotoUrl
    ) {
      this.mountCam = true
    }
  },

  activated() {
    //when component comes into view after going back second time , camera feed is frozed
    if (this.mountCam && !this.isPhotoTaken) this.$refs['webCam'].resume()
  },
  methods: {
    handleDone() {
      //if cam was mounted use that photo and send in checkin request as file(avatar) and as url (avatarUrl)  for badge in next step

      //camera componen returns base64Url , if photo taken use lasest photo for badge,else use prefilled img from last time

      let photoStepData = {
        avatar: null, //for checkin Req
        avatarUrl: null, //base 64 or existing file url for preview
      }
      if (this.mountCam) {
        photoStepData.avatarUrl = this.photoFromCam
        photoStepData.avatar = this.$helpers.dataURLtoFile(
          this.photoFromCam,
          'avatar.jpeg'
        )
      } else {
        photoStepData.avatarUrl = this.prefilledPhotoUrl
      }

      this.$emit('nextStep', photoStepData)
    },

    takePhoto() {
      //only now taking photo
      this.photoFromCam = this.$refs['webCam'].capture()
      this.$refs['webCam'].stop()
      this.isPhotoTaken = true
    },

    retakePhoto() {
      if (!this.mountCam) {
        this.mountCam = true
      } else {
        this.photoFromCam = null
      }
      this.isPhotoTaken = false
    },
    cameraError() {
      this.camError = 'Please allow camera access'
    },
    camNotSupported() {
      this.camError = 'Device camera not supported'
    },
  },
}
</script>
