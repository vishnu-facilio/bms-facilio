<template>
  <div class="p30">
    <div class="d-flex flex-direction-row f14">
      <el-row :gutter="20" class="width100">
        <el-col :span="12" class="border-right4 pR20">
          <div class="fc-grey2 f11 text-uppercase bold ">
            {{ $t('common._common.description') }}
          </div>
          <div
            class="fc-black-13 f13 pT10 text-left textoverflow-height-ellipsis max-height50 line-height20 pB40"
            :title="description"
            v-tippy="{ placement: 'top', arrow: true, animation: 'shift-away' }"
          >
            {{ description }}
          </div>
          <div class="fc-grey2 f11 text-uppercase bold">
            {{ $t('common._common.message') }}
          </div>
          <div
            class="fc-black-13 f13 pT10 text-left textoverflow-height-ellipsis max-height50 line-height20"
            :title="message"
            v-tippy="{ placement: 'top', arrow: true, animation: 'shift-away' }"
          >
            {{ message }}
          </div>
          <div class="pT30">
            <el-row>
              <el-col :span="12">
                <div class="fc-grey2 f11 text-uppercase bold">
                  {{ $t('asset.assets.asset_category') }}
                </div>
                <div class="fc-black-13 f13 pT10 text-left">
                  {{ assetCategoryDetail }}
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
        <el-col :span="1" class="mL20 mR20">
          <div class="hr-line-vertical"></div>
        </el-col>
        <el-col :span="11" class="pL40">
          <div>
            <el-row>
              <el-col :span="12">
                <div class="fc-grey2 f11 text-uppercase bold">
                  {{ $t('rule.create.rootcause_impact') }}
                </div>
                <div class="fc-black-13 f13 pT10 text-left">
                  {{ $t('rule.create.root_cause') }}
                  <span
                    v-if="isRootCauseEnabled"
                    class="fc-green-color2 f13 bold pL10"
                    >{{ $t('common._common.enabled') }}</span
                  >
                  <span v-else class="fc-red-color f13 bold pL10">{{
                    $t('common._common.disabled')
                  }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-grey2 f13 text-uppercase bold"></div>
                <div class="fc-black-13 pT25 text-left">
                  {{ $t('rule.create.impact') }}
                  <span
                    v-if="isImpactEnabled"
                    class="fc-green-color2 f13 bold pL10"
                    >{{ $t('common._common.enabled') }}</span
                  >
                  <span v-else class="fc-red-color f13 bold pL10">{{
                    $t('common._common.disabled')
                  }}</span>
                </div>
              </el-col>
            </el-row>
            <div class="pT30">
              <el-row>
                <el-col :span="24">
                  <div class="fc-grey2 f11 text-uppercase bold">
                    Fault To Workorder Creation
                  </div>
                  <div class="pT10">
                    <span
                      v-if="isFaultToWo"
                      class="fc-green-color2 f13 bold  text-left "
                      >{{ $t('common._common.enabled') }}</span
                    >
                    <span v-else class="fc-red-color f13 bold text-left ">{{
                      $t('common._common.disabled')
                    }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details', 'isFaultToWo'],
  created() {
    this.$store.dispatch('loadAssetCategory')
    let { details } = this
    let { alarmRule } = details
    let { alarmTriggerRule } = alarmRule
    let { assetCategoryId } = alarmTriggerRule
    this.loadThresholdFields(assetCategoryId)
  },
  data() {
    return {
      thresholdFields: [],
    }
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
    }),
    description() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let description = ''

      if (moduleName === 'newreadingrules') {
        description = this.$getProperty(details, 'description', '---')
      } else {
        description = this.$getProperty(
          alarmRule,
          'preRequsite.description',
          '---'
        )
      }
      return description
    },
    assetCategoryDetail() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let assetCategoryId = ''
      let assetCategory = ''

      if (moduleName === 'newreadingrules') {
        assetCategoryId = this.$getProperty(details, 'assetCategory.id', -1)
      } else {
        assetCategoryId = this.$getProperty(
          alarmRule,
          'preRequsite.assetCategoryId',
          -1
        )
      }
      if (assetCategoryId > 0) {
        assetCategory = this.getCategoryName(assetCategoryId) || '---'
      }
      return assetCategory
    },
    thresholdMetric() {
      let { details } = this
      let { moduleName } = details
      let fieldId = ''
      let fieldDisplayName = ''

      if (moduleName === 'newreadingrules') {
        fieldId = this.$getProperty(details, 'fieldId', '')
        fieldDisplayName = this.getFieldDisplayName(fieldId)
      } else {
        let { readingField } = details
        fieldDisplayName = this.$getProperty(readingField, 'displayName', '---')
      }
      return fieldDisplayName
    },
    message() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let { alarmTriggerRule } = alarmRule || {}
      let message = ''

      if (moduleName === 'newreadingrules') {
        let { alarmDetails } = details
        message = this.$getProperty(alarmDetails, 'message', '---')
      } else {
        let { actions } = alarmTriggerRule || {}
        message = this.$getProperty(
          actions,
          '0.template.originalTemplate.message',
          '---'
        )
      }
      return message
    },
    isRootCauseEnabled() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let rootCauseEnabled = false

      if (moduleName === 'newreadingrules') {
        let { rca } = details || {}
        let { rcaRuleIds } = rca || {}
        return !isEmpty(rca) && rcaRuleIds.length > 0 ? true : rootCauseEnabled
      } else {
        let { alarmRCARules } = alarmRule || {}
        rootCauseEnabled = alarmRCARules ? alarmRCARules.length > 0 : false
      }
      return rootCauseEnabled
    },
    isImpactEnabled() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let { alarmTriggerRule } = alarmRule || {}
      let impactEnabled = false

      if (moduleName === 'newreadingrules') {
        let { impact } = details || {}
        let { id } = impact || {}
        return id > 0 ? true : impactEnabled
      } else {
        let { actions } = alarmTriggerRule || {}
        let originalTemplate = this.$getProperty(
          actions[0].template,
          'originalTemplate'
        )

        impactEnabled = originalTemplate ? originalTemplate.impact : false
      }
      return impactEnabled
    },
  },
  methods: {
    loadThresholdFields(id) {
      this.thresholdFields = []
      this.$store
        .dispatch('formulabuilder/loadAssetReadings', {
          assetCategoryId: id,
        })
        .then(() => {
          this.thresholdFields = this.$store.getters[
            'formulabuilder/getAssetReadings'
          ](id, true)
        })
    },
    getFieldDisplayName(id) {
      let fieldObj
      this.thresholdFields.filter(field => {
        if (field.id === id) {
          fieldObj = field
        }
        return true
      })
      if (!isEmpty(fieldObj)) {
        let { displayName } = fieldObj
        return displayName
      }
      return '---'
    },
    getCategoryName(assetCategoryId) {
      if (assetCategoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === assetCategoryId
        )
        if (!isEmpty(category)) {
          let { displayName } = category || {}
          return displayName
        }
      }
    },
  },
}
</script>
