<template>
  <div>
    <div v-if="mailToggle">
      <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
        Import scheduled
      </div>
      <div class="fc-heading-border-width43 mT10 height2px"></div>
      <div>
        <div class="pT40">
          <div class="label-txt-black pB20">Your Import has been scheduled</div>
          <el-checkbox @change="setMail" v-model="mailResponse"
            >Mail me the Response</el-checkbox
          >
        </div>
        <div class="flex-middle flex-col justify-content-center height300">
          <div>
            <spinner show="true" size="90"></spinner>
          </div>
        </div>
      </div>
    </div>
    <div v-if="data && data.status === 8">
      <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
        Imported Successful
      </div>
      <div class="fc-heading-border-width43 mT10 height2px"></div>
      <div class="pT40">
        <div class="label-txt-black">
          {{ successModuleMessage() + ' been added successfully' }}
        </div>
        <el-row class="pT20">
          <el-col :span="3">
            <div class="fc-black3-16">
              <span class="green-txt2-16">{{ added }}</span> Added
            </div>
          </el-col>
          <el-col :span="3">
            <div class="fc-black3-16">
              <span class="green-txt2-16">{{ updated }}</span> Updated
            </div>
          </el-col>
          <el-col :span="3">
            <div class="fc-black3-16">
              <span class="fc-red-status f16">{{ skipped }}</span> Skipped
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="mT30" v-if="data && data.status === 9">
      <span class="red-txt-small">Import Failed. Try again.</span>
      <br />
      <span>Error Message: {{ errorMessage }}</span>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="finalDone" class="fc-full-btn-fill-green f13"
        >Done</el-button
      >
    </div>
  </div>
</template>

<script>
import ImportHelper from './ImportHelper'
export default {
  props: ['importProcessContext'],
  mixins: [ImportHelper],
  data() {
    return {
      skippedLogFileLink: null,
      added: 0,
      data: null,
      updated: 0,
      skipped: 0,
      failed: 0,
      preventExcessReturns: 0,
      mailToggle: true,
      mailResponse: false,
      interval: null,
      errorMessage: '',
    }
  },
  mounted() {
    this.data = this.importProcessContext
    this.interval = setInterval(this.checkStatus, 40000)
  },
  destroyed() {
    clearInterval(this.interval)
  },
  methods: {
    finalDone() {
      this.$emit('finalDone')
    },
    checkStatus() {
      this.$http
        .post('/import/checkImport', {
          importProcessContext: this.importProcessContext,
        })
        .then(response => {
          this.data = response.data.importProcessContext
          if (this.data.status === 8 && this.preventExcessReturns === 0) {
            this.added = this.data.entries
            this.updated = this.data.updateEntries
            this.skipped = this.data.skipEntries
            this.preventExcessReturns = 1
            this.mailToggle = false
            if (this.data.skipEntries !== null) {
              let importJobMetaJSON = JSON.parse(this.data.importJobMeta)
              if (importJobMetaJSON !== null) {
                if ('skippedLogFileLink' in importJobMetaJSON) {
                  this.skippedLogFileLink = importJobMetaJSON.skippedLogFileLink
                }
              }
            }
            clearInterval(this.interval)
          } else if (this.data.status === 9) {
            let meta = JSON.parse(this.data.importJobMeta)
            if (meta != null && typeof meta.errorMessage !== 'undefined') {
              this.errorMessage = meta.errorMessage
            }
            this.mailToggle = false
            clearInterval(this.interval)
          }
        })
    },
    uploadEmit() {
      this.$emit('FinalDone', 0)
    },
    setMail(val) {
      if (val) {
        this.data.mailSetting = 1
      } else {
        this.data.mailSetting = -1
      }
      let param = {
        id: this.data.id,
        mailSetting: this.data.mailSetting,
      }
      this.$http.post('/v2/import/updateImportProcessContext', {
        importProcessContext: param,
      })
    },
    successModuleMessage() {
      let name = null
      if (
        this.importProcessContext.module.name === 'purchasedTool' ||
        this.importProcessContext.module.name === 'purchasedItem'
      ) {
        name =
          this.importProcessContext.module.name === 'purchasedTool'
            ? 'Tool'
            : 'Item'
      } else if (
        this.importProcessContext.module.name.toLowerCase() === 'asset' ||
        (this.importProcessContext.module.extendModule &&
          this.importProcessContext.module.extendModule.name.toLowerCase() ===
            'asset')
      ) {
        name = 'Asset'
      } else if (this.importProcessContext.module.name) {
        name = this.stringWithCaps(this.importProcessContext.module.name)
      }
      if (this.importProcessContext.importMode === 2) {
        name += ' Reading'
      }
      name = this.added === 1 ? name + ' has ' : name + 's' + ' have '
      return name
    },
  },
}
</script>
<style></style>
