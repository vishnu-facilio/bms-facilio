<template>
  <div>
    <div class="visitor-setting-con-width mT20">
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="22">
            <div class="fc-black-15 fwBold">Legal Documents</div>
            <div class="fc-grey4-13 pT5">
              Present your NDA or other legal documents to visitors for
              signature.
            </div>
          </el-col>
          <el-col :span="2" class="text-right">
            <el-switch
              v-model="visitorTypeSettings.ndaEnabled"
              @change="updatePreferences"
            ></el-switch>
          </el-col>
        </el-row>
      </div>
      <div class="visitor-hor-card scale-up-left">
        <el-row class="flex-middle">
          <el-col :span="18">
            <div class="fc-black-15 fwBold">
              Visitor Non-disclosure Agreement
            </div>
          </el-col>
          <el-col :span="6" class="text-right">
            <el-radio-group v-model="mode" class="fc-visitor-radio">
              <el-radio-button label="edit">Edit</el-radio-button>
              <el-radio-button label="preview">Preview</el-radio-button>
            </el-radio-group>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- edit view  -->
    <div v-show="mode === 'edit'" class="mB30">
      <el-input
        type="textarea"
        resize="none"
        :autosize="{ minRows: 30, maxRows: 30 }"
        placeholder="Please input"
        :autofocus="true"
        v-model="visitorTypeSettings.ndaContent"
        class="fc-input-full-border-select2 visitor-textare"
      >
      </el-input>
    </div>

    <!-- preview view -->
    <div
      class="fc-border1 p20 PT0 label-txt-black line-height22 fc-white-bg mB30 space-preline"
      v-show="mode === 'preview'"
    >
      {{ visitorTypeSettings.finalNdaContent }}
    </div>
    <div class="fc-white-bg mB30  mT20" v-show="mode === 'edit'">
      <button
        class="btn btn-primary setup-el-btn fR"
        :loading="isSaving"
        @click="handleSave"
      >
        SAVE
      </button>
    </div>
    <div class="clearboth"></div>

    <!-- preview end -->
  </div>
</template>
<script>
import { updateVisitorSettings } from 'src/devices/VisitorKiosk/VisitorApis'
export default {
  props: ['visitorTypeSettings'],

  data() {
    return {
      mode: 'preview',
      isSaving: false,
      defaultNDAText: `
          1. I may be given access to confidential information belonging to (the “Company”) through my relationship with Company or as a result of
           my access to Company’s premises.

          2. I understand and acknowledge that Company’s trade secrets consist of information and materials that are valuable and not generally known by
           Company’s competitors, including:

          (a) Any and all information concerning Company’s current, future or proposed products, including, but not limited to, computer code, drawings,
          specifications, notebook entries, technical notes and graphs, computer printouts, technical memoranda and correspondence, product development
          agreements and related agreements.

          (b) Information and materials relating to  Company’s purchasing, accounting, and marketing; including, but not limited to, marketing plans,
          sales data, unpublished promotional material, cost and pricing information and customer lists.

          (c) Information of the type described above which Company obtained from another party and which Company treats
          as confidential, whether or not owned or developed by Company.

          (d) Other: __________________________________

          3. In consideration of being admitted to Company’s facilities, I will hold in the strictest confidence any trade secrets or confidential information
          that is disclosed to me. I will not remove any document, equipment or other materials from the premises without Company’s written permission. I will
          not photograph or otherwise record any information to which I may have access during my visit.

          4. This Agreement is binding on me, my heirs, executors, administrators and assigns and inures to the benefit of Company, its successors, and
          assigns.

          5. This Agreement constitutes the entire understanding between Company and me with respect to its subject matter. It supersedes all earlier
          representations and understandings, whether oral or written.
          `,
    }
  },
  methods: {
    updatePreferences() {
      updateVisitorSettings(this.visitorTypeSettings)
    },
    handleSave() {
      this.isSaving = true
      updateVisitorSettings(this.visitorTypeSettings).then(() => {
        this.isSaving = false
        this.mode = 'preview'
      })
    },
    handleCancel() {},
  },
}
</script>
<style>
.fc-visitor-radio .el-radio-button:first-child .el-radio-button__inner {
  padding-left: 35px;
  padding-right: 35px;
}
.visitor-textare .el-textarea__inner {
  line-height: 20px !important;
  padding-left: 0 !important;
}
</style>
