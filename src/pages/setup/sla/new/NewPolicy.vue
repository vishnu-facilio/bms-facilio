<template>
  <div>
    <div
      id="policy-header"
      class="section-header policy-section-header policy-section-header-text"
    >
      {{ $t('setup.setupLabel.policy') }}
    </div>
    <el-form
      :model="slaPolicy"
      :rules="rules"
      ref="slaPolicy-form"
      label-width="180px"
      label-position="left"
      class="p50 pT10 pR70 pB30"
    >
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item
            prop="name"
            :label="$t('setup.setupLabel.sla_name')"
            class="mB10"
          >
            <el-input
              v-model="slaPolicy.name"
              @change="name => $emit('updateTitle', name)"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item
            prop="description"
            :label="$t('space.sites.site_description')"
            class="mB10"
          >
            <el-input
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 4 }"
              class="fc-input-full-border-textarea"
              :placeholder="$t('setup.setupLabel.add_a_decs')"
              v-model="slaPolicy.description"
              resize="none"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item
            prop="siteId"
            :label="$t('space.sites._site')"
            class="mB10"
          >
            <Lookup
              v-model="slaPolicy.siteId"
              :field="fields.site"
              :hideLookupIcon="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardSite"
            ></Lookup>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mT30">
        <el-col :span="24">
          <div>
            <label class="policy-section-header">
              Conditions to apply policy
            </label>
          </div>
          <CriteriaBuilder
            v-model="slaPolicy.criteria"
            :moduleName="module"
            ref="criteria-builder"
          />
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { Lookup } from '@facilio/ui/forms'
import { CriteriaBuilder } from '@facilio/criteria'
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
  name: 'NewPolicy',
  props: {
    policy: {
      type: Object,
    },
  },
  components: { Lookup, CriteriaBuilder },
  data() {
    return {
      fields,
      slaPolicy: {
        name: '',
        description: '',
        siteId: -1,
        criteria: null,
      },
      defaultSite: {
        label: 'All Sites',
        value: -1,
      },
      rules: {},
      isCriteriaRendering: false,
      canWatchForChanges: false,
    }
  },
  computed: {
    id() {
      return this.$route.params.id
    },
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    module() {
      return this.$route.params.moduleName
    },
  },
  watch: {
    policy: {
      handler: function() {
        this.init()
      },
      immediate: true,
    },
    slaPolicy: {
      handler() {
        let { slaPolicy, fields, defaultSite } = this
        let { site } = fields || {}
        let { options } = site || {}
        let { siteId } = slaPolicy || {}
        if (this.canWatchForChanges && !this.isCriteriaRendering)
          this.$emit('modified')
        if (siteId === -1) {
          if (!options.includes(defaultSite)) {
            this.setDefaultSite()
          }
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    init() {
      if (this.isNew) return

      let { id, name, description, siteId, criteria, criteriaId } = this.policy
      this.slaPolicy = {
        ...this.slaPolicy,
        id,
        name,
        description,
        siteId,
        criteria,
        criteriaId,
      }
      this.$nextTick(() => (this.canWatchForChanges = true))
    },
    setDefaultSite() {
      this.fields.site.options.unshift(this.defaultSite)
    },
    updateCriteria(value) {
      this.$set(this.slaPolicy, 'criteria', value)
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
    serialize() {
      let {
        id,
        name,
        description,
        siteId,
        criteria,
        criteriaId = null,
      } = clone(this.slaPolicy)
      let data = {
        name,
        description,
        siteId,
        criteria,
      }
      if (!this.isNew) data.id = id
      if (!isEmpty(criteria) && !isEmpty(criteriaId))
        data.criteriaId = criteriaId

      return data
    },
    async validation() {
      return await this.$refs['criteria-builder'].validate()
    },
  },
}
</script>
<style lang="scss" scoped>
.policy-section-header {
  font-size: 14px !important;
  font-weight: 500;
  letter-spacing: 1.6px;
  margin: 0;
  color: #3ab2c1 !important;
}
.policy-section-header-text {
  padding: 20px 50px 15px !important;
  text-transform: capitalize !important;
}
</style>
