<script>
import ModuleSummary from 'src/beta/summary/ModuleSummary.vue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FButtonGroup, FContainer, FButton } from '@facilio/design-system'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'

export default {
  extends: ModuleSummary,
  props: ['viewname'],
  data() {
    return {
      visible: true,
      downloadUrl: null,
    }
  },
  components: {
    FButtonGroup,
    FContainer,
    FButton,
  },
  computed: {
    mainFieldKey() {
      return 'billUid'
    },
    tagProps() {
      let { record } = this || {}
      let moduleState = record.currentModuleState()
      const result = {}
      if (!isEmpty(moduleState)) {
        result.text = moduleState
        result.variant = 'status'
        if (moduleState === 'partiallyDisputed') {
          result.statusType = 'warning'
        } else if (moduleState === 'disputed') {
          result.statusType = 'danger'
        } else {
          result.statusType = 'success'
        }
      }

      return result
    },
    pdfUrl() {
      let { id } = this
      let url
      let appName = getApp().linkName
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/utilityBillspdf?billId=${id}`
      } else {
        url = `/app/pdf/utilityBillspdf?billId=${id}`
      }

      return window.location.protocol + '//' + window.location.host + url
    },
  },
  methods: {
    systemButtons() {
      return (
        <FContainer display="flex">
          {
            <FButtonGroup
              moreOptions={false}
              {...{
                scopedSlots: {
                  buttons: () => {
                    return (
                      <FContainer display="flex" paddingLeft="containerXLarge">
                        <FButton
                          appearance="tertiary"
                          iconGroup="connectivity"
                          iconName="download"
                          iconButton={true}
                          vOn:click={this.downloadPdf}
                        />
                        <FButton
                          appearance="tertiary"
                          iconGroup="device"
                          iconName="printer"
                          iconButton={true}
                          vOn:click={this.openPrintPreview}
                        />
                        <FButton
                          appearance="secondary"
                          iconGroup="connectivity"
                          iconName="download"
                          iconButton={false}
                          iconPosition="suffix"
                          vOn:click={this.openExternalBill}
                        >
                          Source Bill
                        </FButton>
                      </FContainer>
                    )
                  },
                },
              }}
            />
          }
        </FContainer>
      )
    },
    openExternalBill() {
      let { record } = this
      window.open(record.sourceDownloadUrl)
    },
    openPrintPreview() {
      if (this.pdfUrl) window.open(this.pdfUrl)
    },
    async downloadPdf() {
      this.downloadUrl = null

      this.$message({
        message: this.$t('common._common.downloading'),
        showClose: true,
        duration: 0,
      })
      let additionalInfo = {
        showFooter: false,
        footerStyle: 'p {font-size:12px; margin-left:500px}',
        footerHtml:
          '<p>Page  <span class="pageNumber"></span> / <span class="totalPages"></span></p>',
      }
      let { data, error } = await API.post(`/v2/integ/pdf/create`, {
        url: this.pdfUrl,
        additionalInfo,
      })

      this.$message.closeAll()
      if (error) {
        let {
          message = this.$t(
            'common.wo_report.unable_to_fetch_rfq_download_link'
          ),
        } = error
        this.$message.error(message)
      } else {
        let { fileUrl } = data || {}
        this.downloadUrl = fileUrl || null
        window.open(fileUrl, '_blank')
      }
    },
  },
}
</script>
