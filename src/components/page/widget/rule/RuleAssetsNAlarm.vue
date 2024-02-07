<template>
  <div class="p30">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="f14" v-else-if="values">
      <div class="fc-black-13 text-uppercase text-left bold">
        {{ $t('common.header.assets') }}
      </div>
      <div
        class="flex-middle flex-row pB30 border-bottom3 pT10 pointer"
        @click="openAssets()"
      >
        <div class="mR15 summary-widget-icon-bg tarquish">
          <InlineSvg
            src="svgs/asset1"
            iconClass="icon icon-md fc-white-color text-center vertical-baseline"
          ></InlineSvg>
        </div>
        <div class="fc-black-14 pR20 text-left">
          <span v-html="resourceLabels"></span>
        </div>
      </div>
      <div class="fc-black-13 pT30 text-uppercase text-left bold">
        {{ $t('common.header.faults') }}
      </div>
      <div
        class="flex-middle flex-row pT10 pointer"
        @click="values.activeAlarms > 0 ? openAlarm(true) : null"
      >
        <div class="mR15 summary-widget-icon-bg red">
          <InlineSvg
            src="svgs/alarm"
            iconClass="icon icon-md fc-white-color text-center vertical-baseline"
          ></InlineSvg>
        </div>
        <div class="fc-black-14 text-left pR20 line-height20">
          <span
            class="fc-black-14 text-left bold"
            v-if="values.activeAlarms > 0"
            >{{ values.activeAlarms }}</span
          >
          <span class="fc-black-14" v-else
            >{{ $t('common.products.no') }}
          </span>
          <span class="fc-black-14">
            {{ $t('asset.assets.active_faults') }}</span
          >
        </div>
      </div>
      <div
        class="flex-middle flex-row pT20 pointer"
        @click="values.alarmRuleThisWeek > 0 ? openAlarm() : null"
      >
        <div class="mR15 summary-widget-icon-bg orange">
          <InlineSvg
            src="svgs/calendar"
            style="padding-top: 3px; padding-left: 2px"
            iconClass="icon icon-md fc-white-color text-center"
          ></InlineSvg>
        </div>
        <div class="fc-black-14 text-left pR20 line-height20">
          <span v-if="values.alarmRuleThisWeek > 0" class="bold">
            {{ values.alarmRuleThisWeek }}
          </span>
          <span v-else class="fc-black-14"
            >{{ $t('common.products.no') }}
          </span>
          <!-- {{ details.alarmRuleThisWeek ? details.alarmRuleThisWeek : 'No' }} -->
          <span class="fc-black-14">{{
            $t('rule.create.faults_this_week')
          }}</span>
          <span class="bold">
            {{
              values.alarmRuleThisWeek > 0
                ? ' across ' + values.alarmThisWeekResources.length + ' Assets'
                : ''
            }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
import { mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    InlineSvg,
  },
  props: ['details'],
  data() {
    return {
      loading: false,
      values: null,
      category: null,
      assetCount: null,
      selectedResourceList: null,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
    resourceLabels() {
      return this.resourceLabel()
    },
    ruleId() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let ruleId = null
      if (moduleName === 'newreadingrules') {
        ruleId = this.$getProperty(details, 'id', null)
      } else {
        ruleId = this.$getProperty(alarmRule, 'preRequsite.id', null)
      }
      return ruleId
    },
    assetCategoryId() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let assetCategoryId = ''

      if (moduleName === 'newreadingrules') {
        let { assetCategory } = details || {}
        assetCategoryId = this.$getProperty(assetCategory, 'id', '')
      } else {
        let { preRequsite } = alarmRule || {}
        let { assetCategoryId: ruleAssetCategoryId } = preRequsite || {}
        assetCategoryId = ruleAssetCategoryId ? ruleAssetCategoryId : -1
      }
      return assetCategoryId
    },
    specificAssets() {
      let { details } = this || {}
      let { assets } = details || {}
      return assets
    },
  },
  mounted() {
    this.loadAlarmDetails()
  },
  methods: {
    loadAlarmDetails() {
      this.loading = true
      let workFlowId = null
      let { ruleId } = this
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        workFlowId = 50
      } else {
        workFlowId = 102
      }
      this.$util.getdefaultWorkFlowResult(workFlowId, ruleId).then(d => {
        this.values = d
        this.loading = false
      })
    },
    async loadAssetCount() {
      let url = 'asset/assetCount?viewName=all&count=true'
      let query = {
        category: { operatorId: 36, value: [JSON.stringify(this.category.id)] },
      }
      let filterString = encodeURIComponent(JSON.stringify(query))
      url = url + '&filters=' + filterString
      let { data, error } = await API.get(url)

      if (!error) {
        let { assetCount } = data
        this.assetCount = assetCount
      }
    },
    openAssets() {
      let values = []
      let url = '/app/at/assets/all?search='
      let filterObj = null
      if (this.selectedResourceList) {
        this.selectedResourceList.forEach(element => {
          values.push(`${element.id}`)
        })
        filterObj = {
          id: [
            {
              operatorId: 36,
              value: values,
            },
          ],
        }
        url += encodeURIComponent(JSON.stringify(filterObj))
      } else if (!isEmpty(this.specificAssets)) {
        if (this.specificAssets) {
          this.specificAssets.forEach(element => {
            values.push(`${element}`)
          })
        }
        filterObj = {
          id: {
            operatorId: 36,
            value: values,
          },
        }
        url += encodeURIComponent(JSON.stringify(filterObj))
      } else {
        filterObj = {
          category: {
            operatorId: 36,
            value: [JSON.stringify(this.category.id)],
          },
        }
        url += encodeURIComponent(JSON.stringify(filterObj))
      }
      if (isWebTabsEnabled()) {
        let filter = JSON.stringify(filterObj)
        let { name } = findRouteForModule('asset', pageTypes.LIST)
        if (name) {
          this.$router.push({
            name,
            params: { viewname: 'all' },
            query: { search: filter },
          })
        }
      } else {
        this.$router.push({ path: url })
      }
    },
    openAlarm(isActive) {
      let url = '/app/fa/faults/'
      let filterObj = null
      if (!isActive) {
        filterObj = {
          rule: {
            operatorId: 36,
            value: [JSON.stringify(this.ruleId)],
          },
          lastOccurredTime: { operatorId: 31 },
        }
        url += 'all?search=' + encodeURIComponent(JSON.stringify(filterObj))
      } else {
        filterObj = {
          rule: {
            operatorId: 36,
            value: [JSON.stringify(this.ruleId)],
          },
        }
        url +=
          'active?&includeParentFilter=true&search=' +
          encodeURIComponent(JSON.stringify(filterObj))
      }
      if (isWebTabsEnabled()) {
        let filter = JSON.stringify(filterObj)
        let { name } = findRouteForModule('newreadingalarm', pageTypes.LIST)
        if (name) {
          this.$router.push({
            name,
            params: { viewname: isActive ? 'active' : 'all' },
            query: { search: filter },
          })
        }
      } else {
        this.$router.push({ path: url })
      }
    },

    resourceLabel() {
      let { details, category } = this || {}
      let { matchedassetcount } = details || {}
      let { name } = category || {}
      if (this.assetCategoryId > 0) {
        this.category = this.getAssetCategory(this.assetCategoryId)
      }

      if (!isEmpty(this.specificAssets)) {
        this.assetCount = this.specificAssets.length

        let str = this.assetCount > 1 ? (str = ' Assets') : (str = ' Asset')
        return `Specific Assets -
          ${this.category.name}
          (
          ${this.assetCount}
          ${str}
          )`
      } else {
        let ids = this.$getProperty(this, 'details.matchedassetids')
        if (ids) {
          this.selectedResourceList = ids.map(id => ({
            id: id,
          }))
        }

        this.loadAssetCount()

        if (!ids || this.assetCount === matchedassetcount) {
          let str = this.assetCount > 1 ? ' Assets' : 'Asset'
          return `All ${name} ( ${this.assetCount} ${str} )`
        } else if (matchedassetcount === 0) return `No  ${name}`
        else return `${matchedassetcount} ${name} included`
      }
    },
    // resourceLabel() {
    //   let isIncludeResource
    //   // let selectedResourceList
    //   if (this.details.alarmRule.preRequsite.assetCategoryId > 0) {
    //     this.category = this.getAssetCategory(
    //       this.details.alarmRule.preRequsite.assetCategoryId
    //     )
    //     if (
    //       this.details.alarmRule.preRequsite.includedResources &&
    //       this.details.alarmRule.preRequsite.includedResources.length
    //     ) {
    //       isIncludeResource = true
    //       this.selectedResourceList = this.details.alarmRule.preRequsite.includedResources.map(
    //         id => ({ id: id })
    //       )
    //     } else if (this.details.alarmRule.preRequsite.excludedResources) {
    //       this.selectedResourceList = this.details.alarmRule.preRequsite.excludedResources.map(
    //         id => ({ id: id })
    //       )
    //     }
    //     let message
    //     let selectedCount = this.details.alarmRule.preRequsite.excludedResources
    //       ? this.details.alarmRule.preRequsite.excludedResources
    //       : this.details.alarmRule.preRequsite.includedResources
    //       ? this.details.alarmRule.preRequsite.includedResources
    //       : null
    //     if (selectedCount) {
    //       let includeMsg = isIncludeResource ? 'included' : 'excluded'
    //       message =
    //         selectedCount.length +
    //         ' ' +
    //         this.category.name +
    //         (selectedCount > 1 ? 's' : '') +
    //         ' ' +
    //         includeMsg
    //     } else {
    //       message = 'All ' + this.category.name
    //       this.loadAssetCount()
    //     }
    //     return message ? `${message}` : `--`
    //   }
    //   return null
    // },
  },
}
</script>
