<template>
  <div>
    <FieldLoader v-if="isLoading" :isLoading="isLoading"></FieldLoader>
    <el-select
      v-else
      v-model="modelVal"
      filterable
      :clearable="!$validation.isEmpty(isClearable) && isClearable"
      :remote-method="remoteMethod"
      :loading="loading"
      placeholder="Select"
      class="fc-input-full-border-select2 width100"
      :disabled="disableSiteField"
      @change="onSiteChange()"
      :remote="true"
    >
      <el-option
        v-for="(option, index) in sitesList"
        :key="index"
        :label="option.label"
        :value="option.value"
        :data-test-selector="`${option.label}`"
      >
        <span class="fL">{{ option.label }}</span>
        <span
          v-if="getIsSiteDecommissioned(option)"
          v-tippy
          :title="$t('setup.decommission.decommissioned')"
          class="select-float-right-text13"
          ><fc-icon
            group="alert"
            class="fR pT10 pR10"
            name="decommissioning"
            size="16"
          ></fc-icon
        ></span>
      </el-option>
    </el-select>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import { mapActions } from 'vuex'
import { getFieldOptions, getIsSiteDecommissioned } from 'util/picklist'
import debounce from 'lodash/debounce'
import FieldLoader from '@/forms/FieldLoader'

export default {
  name: 'FSiteField',
  components: { FieldLoader },
  props: [
    'model',
    'canDisable',
    'isEdit',
    'resetFields',
    'filter',
    'isClearable',
    'isBuilder',
    'field',
  ],
  data() {
    return {
      sites: null,
      loading: false,
      selectText: null,
      isLoading: false,
    }
  },
  computed: {
    modelVal: {
      get() {
        return this.model
      },
      set(value) {
        value = isEmpty(value) ? null : value
        this.$emit('update:model', value)
        // Have to filter the options in space asset chooser, if site is already selected.
        this.$emit('update:filter', { site: Number(value) })
      },
    },
    currentSiteId() {
      let { sitesList, isBuilder, selectText } = this
      let siteId = Number(this.$cookie.get('fc.currentSite'))
      let currentSiteId = siteId > 0 ? siteId : -1
      if (
        isEmpty(currentSiteId) &&
        sitesList.length === 1 &&
        !isBuilder &&
        isEmpty(selectText)
      ) {
        let [site] = sitesList || []
        let { value } = site || {}
        return value || null
      }
      return currentSiteId
    },
    isCurrentSiteSelected() {
      let { currentSiteId } = this
      return !isEmpty(currentSiteId)
    },
    disableSiteField() {
      let { isCurrentSiteSelected, canDisable, isEdit } = this
      return isCurrentSiteSelected || canDisable || isEdit
    },
    sitesList() {
      let { sites = {} } = this
      return sites || []
    },
  },
  created() {
    this.loadSites()
    this.setSite()
    this.getIsSiteDecommissioned = getIsSiteDecommissioned
  },
  watch: {
    currentSiteId(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.setSite()
      }
    },
    field: {
      handler() {
        this.loadSites()
      },
      deep: true,
    },
    modelVal: {
      async handler(newVal) {
        let { sitesList } = this || {}
        let hasValue = (sitesList || []).some(site => site.value === newVal)
        if (!hasValue) {
          this.isLoading = true
          await this.loadSites()
          this.isLoading = false
        }
      },
    },
  },
  methods: {
    ...mapActions(['loadSite']),
    async loadSites() {
      let { field, modelVal } = this || {}
      let params = {
        field: {
          ...field,
          lookupModuleName: 'site',
          skipDeserialize: false,
        },
      }
      if (!isEmpty(modelVal)) {
        params['defaultIds'] = [modelVal]
      }
      let { options, error } = await getFieldOptions(params)
      if (isEmpty(error)) {
        this.sites = options
      }
    },
    remoteMethod: debounce(async function(query) {
      this.loading = true
      let { field } = this || {}
      if (!isEmpty(query)) {
        this.selectText = query
      } else {
        this.selectText = null
      }
      let { options, error } = await getFieldOptions({
        field: { ...field, lookupModuleName: 'site', skipDeserialize: false },
        searchText: query,
      })
      if (isEmpty(error)) {
        this.sites = options
      }
      this.loading = false
    }, 1000),
    setSite() {
      let { modelVal, currentSiteId, isEdit } = this
      // Skip setting site id for edit case
      if (
        !isEdit &&
        !isEmpty(currentSiteId) &&
        Number(modelVal) !== currentSiteId
      ) {
        this.$set(this, 'modelVal', currentSiteId)
        this.$emit('handleChange')
      }
    },
    onSiteChange() {
      // Here model returns the previous site id
      let { resetFields, model } = this
      let siteId = isEmpty(model) ? null : model
      if (resetFields) {
        this.$emit('handleSiteSwitch', siteId)
      }
      this.$emit('handleChange')
    },
  },
}
</script>
