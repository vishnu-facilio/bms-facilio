<template lang="">
  <div class="sfw-container">
    <FContainer v-if="isLoading" class="spinner-position">
      <FSpinner :size="40" ref="content-container"></FSpinner>
    </FContainer>
    <div
      v-else-if="$validation.isEmpty(detailsLayout)"
      class="sfw-empty-state-container"
    >
      <inline-svg
        class="self-center"
        src="svgs/custom-empty-state"
        iconClass="icon text-center icon-xxxxlg"
      />
      <div class="mT10 label-txt-black f14 self-center">
        {{ $t('common._common.error_summary_widget_field') }}
      </div>
    </div>
    <FContainer
      padding="containerNone containerLarge"
      style="height:100%;overflow:hidden"
      ref="content-container"
      v-else
    >
      <FieldsGroup
        style="overflow:hidden"
        :detailsLayout="detailsLayout"
        :details="details"
        :config="config"
        :sites="sites"
        @mounted="callResize"
        @redirectToSummary="redirectToSummary"
      ></FieldsGroup>
    </FContainer>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <PreviewFile
      :visibility.sync="showPreview"
      v-if="showPreview"
      :previewFile="previewFile"
      :files="[previewFile]"
    ></PreviewFile>
  </div>
</template>
<script>
import { FieldsGroup } from '@facilio/ui/new-app'
import PreviewFile from '@/PreviewFile'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import { getBaseURL } from 'util/baseUrl'
import { FContainer, FSpinner } from '@facilio/design-system'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  props: ['details', 'moduleName', 'detailsLayout', 'siteList'],
  mixins: [FetchViewsMixin],
  components: {
    FieldsGroup,
    PreviewFile,
    FContainer,
    FSpinner,
  },
  data() {
    return {
      isLoading: false,
      exportDownloadUrl: null,
      showPreview: false,
      previewFile: null,
      sites: [],
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    config() {
      let { multiCurrency } = this
      return {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
        org: this.$account.org,
        multiCurrency,
        baseUrl: getBaseURL(),
      }
    },
  },
  methods: {
    async init() {
      this.isLoading = true
      await this.fetchSites()
      this.$nextTick(() => {
        this.isLoading = false
      })
    },
    async fetchSites() {
      let { details } = this
      let siteId = details?.siteId || null

      if (isEmpty(siteId)) return
      if (isEmpty(this.siteList) && !isEmpty(siteId)) {
        let { error, options } = await getFieldOptions({
          field: { lookupModuleName: 'site', skipDeserialize: true },
          defaultIds: [siteId],
          perPage: 1,
        })
        if (!error && !isEmpty(options[siteId])) {
          this.sites = options
        }
      } else this.sites = this.siteList
    },
    async redirectToSummary(field) {
      let { moduleName, id } = field || {}
      let viewname = await this.fetchView(moduleName)
      let routerPath = null
      let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
      if (name) {
        routerPath = this.$router.resolve({ name, params: { viewname, id } })
      }
      !!routerPath && window.open(routerPath?.href, '_blank')
    },
  },
}
</script>
<style lang="scss">
.view-more {
  flex-grow: 1;
  padding: 12px;
  .view-text {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
.sfw-container {
  height: 100%;
  .spinner-position {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .footer-part {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
    padding: 10px;
  }
}
</style>
