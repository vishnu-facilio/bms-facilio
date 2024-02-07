<template>
  <div class="fc-kiosk-form-con fc-scale-in-center">
    <div class="fc-kiosk-form position-relative">
      <div class="fc-kiosk-agreement-screen">
        <div class="fc-check-in-f22 pT10 font-bold">
          Non-Disclosure Aggreement
        </div>
        <div
          class="fc-kiosk-16 pT20 max-width450 kiosk-agreement-content-scroll"
        >
          {{ ndaContent }}
        </div>

        <div class="fc-check-in-f22 pT40 font-bold">Signature</div>
        <div class="fc-kiosk-form-input">
          <div class="position-relative">
            <VueSignaturePad
              class="fc-kiosk-sign-pad"
              width="385px"
              height="100px"
              ref="signaturePad"
              saveType="image/png"
            />
          </div>
          <!-- <el-input placeholder="Enter the name" v-model="input" class="fc-kiosk-input fc-kiosk-agree-input mT10">
            <span class="focus-border"></span>
          </el-input> -->
          <el-button class="fc-kiosk-input-next" @click="handleAgreementSubmit">
            <img src="~assets/kiosk-forward.svg" height="45" width="45" />
          </el-button>
        </div>
        <div @click="clearSignature" class="kiosk-sign-clear">Clear</div>
        <div class="flex-middle justify-content-space pT10">
          <div class="fc-kiosk-16">
            {{ sharedData.formFields.visitor.name }}
          </div>
          <div class="fc-kiosk-16 mR70">{{ currentDate }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import VueSignaturePad from 'vue-signature-pad'
import { mapGetters } from 'vuex'
export default {
  props: ['sharedData', 'ndaContent'],
  components: { VueSignaturePad },
  data() {
    return {
      currentDate: this.$helpers.getOrgMoment().format('DD-MM-YYYY'),
      input: null,
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
  },
  methods: {
    clearSignature() {
      this.$refs.signaturePad.clearSignature()
    },
    handleAgreementSubmit() {
      if (this.$refs.signaturePad.isEmpty()) {
        alert('Please sign the NDA.')
        return
      }
      let signatureDataUrl = this.$refs.signaturePad.saveSignature().data
      let signImageFile = this.$helpers.dataURLtoFile(
        signatureDataUrl,
        'signature.png'
      )
      this.$emit('nextStep', {
        signature: signImageFile,
        signatureUrl: signatureDataUrl,
      })
    },
  },
}
</script>

<style></style>
