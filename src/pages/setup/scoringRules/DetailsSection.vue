<template>
  <div>
    <div id="details-header" class="section-header">
      {{ $t('setup.setup.score_details') }}
    </div>

    <el-form
      ref="details"
      :rules="rules"
      :model="scoreDetails"
      label-width="180px"
      label-position="left"
      class="p50 pT10 pR70 pB30"
    >
      <el-form-item :label="$t('common.products.name')" prop="name">
        <el-input
          class="fc-input-full-border2"
          autofocus
          v-model="scoreDetails.name"
          :placeholder="$t('common._common.enter_rule_name')"
        />
      </el-form-item>

      <el-form-item
        :label="$t('common.wo_report.report_description')"
        prop="description"
      >
        <el-input
          v-model="scoreDetails.description"
          class="fc-input-full-border-select2"
          :placeholder="$t('common.wo_report.report_description')"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 2 }"
          resize="none"
        />
      </el-form-item>

      <el-form-item :label="$t('common.products.site')" prop="site">
        <Lookup
          v-model="scoreDetails.siteId"
          :field="fields.site"
          :hideLookupIcon="true"
          @recordSelected="setSelectedValue"
          @showLookupWizard="showLookupWizardSite"
        >
        </Lookup>
      </el-form-item>

      <el-form-item
        :label="$t('common.wo_report.rating_scale')"
        prop="scoreType"
      >
        <el-radio-group
          v-model="scoreDetails.scoreType"
          @change="setScoreRange"
        >
          <el-radio
            v-for="(scoreType, typeName) in scoreTypes"
            :key="scoreType"
            :label="scoreType"
            class="fc-radio-btn"
          >
            {{ typeName }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        v-if="scoreDetails.scoreType === scoreTypes.Number"
        prop="scoreRange"
        class="pB20"
      >
        <el-slider
          v-model="scoreDetails.scoreRange"
          :marks="rangeMarks"
          :step="5"
          :min="5"
          :max="25"
          class="fc-range-selection pL10"
        >
        </el-slider>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { Lookup } from '@facilio/ui/forms'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  props: ['details', 'isNew'],

  data() {
    return {
      fields,
      scoreDetails: {},
      scoreTypes: {
        Percentage: 1,
        Number: 2,
      },
      rangeMarks: {
        0: '0',
        5: '5',
        10: '10',
        15: '15',
        20: '20',
        25: '25',
      },
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },
  components: { Lookup },
  created() {
    this.scoreDetails = this.details
    this.$store.dispatch('loadSites')
  },

  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
  },

  methods: {
    setScoreRange() {
      let { scoreTypes, scoreDetails } = this
      let { scoreType } = scoreDetails
      let scoreRange = scoreType === scoreTypes.Percentage ? 100 : 5

      this.$set(this.scoreDetails, 'scoreRange', scoreRange)
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    async validate() {
      try {
        return await this.$refs['details'].validate()
      } catch {
        return false
      }
    },
  },
}
</script>
