<template>
  <div>
    <div v-if="isLoading">
      <div v-for="i in 2" :key="i" class="fields-glimpse">
        <div class="loading-shimmer line"></div>
        <div class="loading-shimmer line1 mB10"></div>
      </div>
    </div>
    <glimpse-card
      v-else-if="canShowGlimpse"
      :key="`event-${details.id}`"
      :config="glimpseConfig"
      @redirectToSummary="redirectToLookUpFieldSummary(field)"
    >
      <template #fields>
        <div class="fd-field-details-card width100">
          <field-details
            :columns="columns"
            :detailsLayout="detailsLayout"
            :config="fieldConfig"
          ></field-details>
        </div>
      </template>
    </glimpse-card>
    <div
      v-else-if="!$validation.isEmpty(deletedRecord)"
      class="deleted-glimpse-text"
    >
      <fc-icon group="action" name="info" size="15"></fc-icon>
      {{ deletedRecord }}
    </div>
  </div>
</template>
<script>
import { NewFieldDetails as FieldDetails } from '@facilio/ui/app'
import { getBaseURL } from 'util/baseUrl'
import { mapState, mapActions, mapGetters } from 'vuex'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { findRouterForModuleInApp } from 'src/newapp/viewmanager/routeUtil.js'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import GlimpseCard from 'src/newapp/components/GlimpseCard'
import { moduleByLocalId } from 'src/newapp/viewmanager/calendarSupportUtil'
import { getFieldOptions } from 'src/util/picklist'

export default {
  mixins: [FetchViewsMixin],
  components: { FieldDetails, GlimpseCard },
  props: [
    'details',
    'field',
    'lookupModuleName',
    'displayValue',
    'canShowRedirect',
    'siteList',
    'recordId',
    'moduleName',
    'updateSite',
  ],
  data() {
    return {
      isLoading: false,
      canShowGlimpse: false,
      deletedRecord: '',
      detailsLayout: [],
      glimpseData: [],
      columns: 2,
    }
  },
  async created() {
    this.isLoading = true
    await this.fetchSites()
    await this.fetchSummaryDetails()
    this.deserialize()
    await this.$store.dispatch('getActiveCurrencyList')
  },
  computed: {
    ...mapState('glimpse', {
      glimpseDetails: state => state.glimpseDetails,
      glimpseFields: state => state.glimpseFields,
      deletedGlimpse: state => state.deletedGlimpse,
    }),
    ...mapState({
      currencyList: state => state.activeCurrencies,
      account: state => state.account,
    }),
    ...mapGetters('glimpse', ['getGlimpseFieldId']),
    glimpseModuleState() {
      let displayName =
        this.$getProperty(this.glimpseData, 'moduleState.displayName', null) ||
        null
      return displayName
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.$getProperty(this.account, 'data.currencyInfo') || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    canShowImage() {
      return !isEmpty(this.details?.photoFileName)
    },
    lookupFieldName() {
      let { name } = this.field || {}
      return name
    },
    imageUrl() {
      let { getImage, details } = this
      return getImage(details?.photoId) || ''
    },
    fieldConfig() {
      let { multiCurrency, currencyList } = this
      return {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
        showDivider: false,
        org: this.$account.org,
        multiCurrency,
        currencies: currencyList,
      }
    },
    glimpseId() {
      let { id, localId } = this.details || {}

      return moduleByLocalId.includes(this.lookupModuleName)
        ? localId || id
        : id
    },
    glimpseConfig() {
      let {
        glimpseId,
        canShowImage,
        imageUrl,
        glimpseModuleState,
        canShowRedirect,
        displayValue,
      } = this

      let config = {
        id: glimpseId,
        moduleState: glimpseModuleState,
        displayName: displayValue,
        canShowRedirect,
        canShowImage,
        imageUrl,
      }

      return config
    },
  },
  methods: {
    ...mapActions('glimpse', ['loadGlimpse']),
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
    async fetchSites() {
      let { details } = this
      let siteId = details.siteId || null
      let siteObj = this.getSiteDisplayName(siteId)

      if (isEmpty(siteObj) && !isEmpty(siteId)) {
        let { error, options } = await getFieldOptions({
          field: { lookupModuleName: 'site' },
          defaultIds: [siteId],
          perPage: 1,
        })
        if (!error && !isEmpty(options)) {
          this.updateSite(options)
          this.$emit('updateSite', options)
        }
      }
    },
    async fetchSummaryDetails() {
      let {
        details,
        moduleName,
        lookupModuleName,
        glimpseFields,
        lookupFieldName,
        deletedGlimpse,
        recordId,
      } = this

      let isDeletedRecord = deletedGlimpse[lookupModuleName]

      if (isEmpty(isDeletedRecord)) {
        let hasFieldDetails = glimpseFields[details.id] || null
        if (isEmpty(hasFieldDetails)) {
          await this.loadGlimpse({
            moduleName,
            lookupModuleName,
            id: recordId,
            lookupFieldName,
            recordId: details?.id,
          })
        }
      }
    },
    deserialize() {
      let data = this.glimpseDetails[this.lookupModuleName] || {}
      this.deletedRecord = this.deletedGlimpse[this.details?.id] || {}

      if (isEmpty(this.deletedRecord)) {
        this.canShowGlimpse = true
        let fields = (data.configurationFields || []).map(flt => {
          let { facilioField, sequenceNumber } = flt || {}
          let {
            displayName,
            name,
            dataTypeEnum,
            enumMap = {},
            trueVal,
            falseVal,
            displayType,
          } = facilioField || {}
          let options = { ...enumMap } || {}

          if (dataTypeEnum === 'BOOLEAN') {
            options = { trueVal, falseVal }
          }
          return {
            displayName,
            name,
            sequenceNumber,
            dataTypeEnum,
            options,
            displayType,
          }
        })

        let details = this.glimpseFields[this.details.id]
        let { currencyCode, exchangeRate } = details || {}

        this.glimpseData = details

        let result = (fields || [])
          .flatMap(fld => {
            let value = !isEmpty(details[fld?.name]) ? details[fld?.name] : null

            if (['siteId', 'site'].includes(fld?.name)) {
              let name = this.getSiteDisplayName(details[fld.name])
              value =
                fld?.name === 'siteId' ? name : { ...details[fld.name], name }
            }
            let res = { ...fld, value }

            if (fld.dataTypeEnum === 'MULTI_CURRENCY_FIELD') {
              res = { ...res, currencyCode, exchangeRate }
            }
            return res
          })
          .sort((s1, s2) => s1.sequenceNumber - s2.sequenceNumber)

        this.columns = result.length === 1 ? 1 : 2

        this.detailsLayout = [
          {
            fields: result,
          },
        ]
      }
      this.isLoading = false
    },
    getSiteDisplayName(siteValue) {
      let siteObj = (this.siteList || []).find(
        site => site.value === siteValue?.id || siteValue
      )
      let { label } = siteObj || {}
      return label
    },
    async redirectToLookUpFieldSummary(lookUpField) {
      let { lookupModuleName, field } = lookUpField || {}
      let { lookupModule } = field || {}
      let { name, custom } = lookupModule || {}
      let { id } = this.details
      let moduleName = lookupModuleName || name
      let viewname = await this.fetchView(moduleName)

      if (isEmpty(id)) return

      if (isWebTabsEnabled()) {
        let { name: routeName } =
          findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (routeName) {
          this.$router.push({
            name: routeName,
            params: { viewname, id },
          })
        }
      } else {
        let currentModuleName = custom ? 'custom' : moduleName
        let routeObj =
          findRouterForModuleInApp(currentModuleName, pageTypes.OVERVIEW) || {}

        if (!isEmpty(routeObj))
          this.$router.push(routeObj(id, viewname, moduleName))
      }
    },
  },
}
</script>
<style lang="scss">
.fd-field-details-card {
  .fields {
    display: flex;
    flex-wrap: wrap;
    padding: 0px 8px;

    .fields-container {
      .fields-data-text {
        color: #1d384e;
      }
      .fields-value-text {
        color: #667480;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .line {
      width: 90%;
      height: 12px;
    }
    .line1 {
      width: 70%;
      height: 10px;
    }
  }
  .fields > * {
    flex: 0 0 50%;
  }
}
.fd-field-details-card {
  .f-field-details-container {
    padding: 8px;
  }
}
.fields-glimpse {
  width: 280px;
  .line {
    width: 90%;
    height: 12px;
  }
  .line1 {
    width: 70%;
    height: 10px;
  }
}
.deleted-glimpse-text {
  min-width: 340px;
  font-size: 14px;
  word-break: break-word;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
