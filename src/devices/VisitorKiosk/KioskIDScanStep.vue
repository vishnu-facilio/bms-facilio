<template>
  <div class="fc-kiosk-form-con fc-scale-in-center" style="height: 100vh;">
    <div class="fc-kiosk-form position-relative">
      <div class="fc-kiosk-photo">
        <div class="fc-kiosk-19">Hi {{ sharedData.name }}</div>
        <div class="fc-check-in-f22 pT10 font-bold">
          Please scan your documents
        </div>

        <div class="kiosk-photo-capture-box" v-if="!isDocScanned">
          <vue-web-cam
            ref="webCam"
            v-bind:width="400"
            v-bind:height="400"
            v-bind:selectFirstDevice="true"
          >
          </vue-web-cam>
        </div>
        <div class="doc-preview" v-else>
          <div class="frontSidePreview">
            <img :src="scannedDocs[0]" alt="" class="doc-preview-image" />
          </div>
          <div class="backSidePreview">
            <img :src="scannedDocs[1]" alt="" class="doc-preview-image" />
          </div>
        </div>
        <el-button @click="scanDocument">
          {{ primaryButtonText }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { WebCam } from 'vue-web-cam'

/* IF Existing photo present , check for settings and prefill it , else take a fresh photo
 */
export default {
  props: ['sharedData', 'photoSettings'],
  components: {
    'vue-web-cam': WebCam,
  },
  data() {
    return {
      isDocScanned: false,
      scannedDocs: [], //front and back
      photoFromCam: null,
      docsSide: 'front',
    }
  },
  computed: {
    primaryButtonText() {
      if (this.docsSide == 'front') {
        return 'Scan Front'
      } else if (this.docsSide == 'back' && !this.isDocScanned) {
        return 'Scan Back'
      } else if (this.docsSide == 'back' && this.isDocScanned) {
        return 'Proceed'
      } else {
        return null
      }
    },
  },

  methods: {
    handleDone() {},

    scanDocument() {
      if (this.isDocScanned) {
        //ADD TO SHARED DATA
        let idProof = []

        idProof.push(
          this.$helpers.dataURLtoFile(this.scannedDocs[0], 'proofFront.jpeg'),
          this.$helpers.dataURLtoFile(this.scannedDocs[1], 'proofBack.jpeg')
        )

        this.$emit('nextStep', { idProof })
      } else if (this.docsSide == 'front') {
        this.scannedDocs.push(this.$refs['webCam'].capture())
        this.docsSide = 'back'
      } else if (this.docsSide == 'back') {
        this.scannedDocs.push(this.$refs['webCam'].capture())
        this.isDocScanned = true
        this.$refs['webCam'].stop()
      }
    },
  },
}
</script>

<style lang="scss">
.doc-preview {
  display: flex;
  flex-direction: column;
  justify-items: flex-start;
  align-items: center;
}
.doc-preview-image {
  height: 150px;
  width: 200px;
  height: 150px;
  width: 200px;
}
</style>
