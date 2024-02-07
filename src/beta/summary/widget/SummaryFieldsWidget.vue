<template lang="">
  <div class="sfw-container">
    <portal :to="`footer-${widget.id}-${widget.name}`">
      <div
        v-if="needsShowMore && !$validation.isEmpty(detailsLayout)"
        class="view-more"
      >
        <FContainer
          @click="toggleVisibility"
          class=" fc-link-animation pointer text-capitalize letter-spacing0_3 bold margin-auto view-text"
          ><fc-icon
            group="navigation"
            size="16"
            name="diagonal-expand"
            color="#0059D6"
          ></fc-icon>
          <FText
            style="margin-left:4px"
            color="backgroundPrimaryPressed"
            appearance="headingMed14"
            >{{ showMoreLinkText }}</FText
          ></FContainer
        >
      </div></portal
    >
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
        :title="widgetTitle"
        :detailsLayout="detailsLayout"
        :details="details"
        :config="config"
        :sites="sites"
        :getRoute="getRoute"
        @mounted="callResize"
        @redirectToSummary="redirectToSummary"
      ></FieldsGroup>
    </FContainer>
    <FModal
      :visible="canShowInPopOver"
      @cancel="() => (canShowInPopOver = !canShowInPopOver)"
      :title="widgetTitle"
      :hideFooter="true"
      size="L"
    >
      <FContainer
        padding="containerNone containerXxLarge"
        style="height:100%;overflow:hidden"
      >
        <FieldsGroup
          style="overflow:hidden"
          :title="widgetTitle"
          :detailsLayout="detailsLayout"
          :details="details"
          :config="config"
          :sites="sites"
          :insidePop="true"
          :getRoute="getRoute"
          @mounted="callResize"
          @redirectToSummary="redirectToSummary"
        ></FieldsGroup>
      </FContainer>
    </FModal>

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
import { FContainer, FSpinner, FText, FModal } from '@facilio/design-system'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  props: [
    'layoutParams',
    'details',
    'moduleName',
    'primaryFields',
    'widget',
    'disableAutoResize',
    'siteList',
    'calculateDimensions',
    'resizeWidget',
    'hideTitleSection',
  ],
  mixins: [FetchViewsMixin],
  components: {
    FieldsGroup,
    PreviewFile,
    FContainer,
    FSpinner,
    FText,
    FModal,
  },
  data() {
    return {
      title: null,
      isLoading: false,
      exportDownloadUrl: null,
      showPreview: false,
      previewFile: null,
      needsShowMore: false,
      canShowInPopOver: false,
      sites: [],
      detailsLayout: [],
      defaultWidgetHeight: (this.layoutParams || {}).h,
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    showMoreLinkText() {
      return this.$t('common._common.view_more')
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    widgetTitle() {
      return this.widget?.displayName
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
      this.detailsLayout =
        this.$getProperty(this.widget, 'widgetDetail.groups') || []
      await this.fetchSites()
      this.$nextTick(() => {
        this.isLoading = false
      })
    },
    getRoute(moduleName) {
      return findRouteForModule(moduleName, pageTypes.OVERVIEW)
    },
    callResize() {
      if (isEmpty(this.detailsLayout)) this.resizeWidget({ height: 264 })
      else if (!this.disableAutoResize) this.autoResize()
      else this.$emit('autoResize', this.$refs['content-container'])
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
    toggleVisibility() {
      this.canShowInPopOver = !this.canShowInPopOver
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (!container) return

        let height = container.scrollHeight || container.$el.scrollHeight
        let width = container.scrollWidth || container.$el.scrollWidth

        let dimensions = this.calculateDimensions({ height, width })

        if (isEmpty(dimensions)) return
        let { h } = dimensions || {}
        if (h <= this.defaultWidgetHeight) {
          this.needsShowMore = false
        } else {
          let { defaultWidgetHeight = 24 } = this
          this.needsShowMore = h > defaultWidgetHeight ? true : false
          this.defaultWidgetHeight = defaultWidgetHeight
        }
      })
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
