<template>
  <div>
    <div class="summaryHeader d-flex">
      <div class="primary-field summary-header-heading pT5">
        <div>
          <template v-if="moduleName === 'workorder'">
            <span class="fc-id">#{{ record['serialNumber'] }}</span>
            <div
              class="heading-black18 f16 max-width500px textoverflow-ellipsis"
            >
              {{ record[mainField] }}
            </div>
            <span
              v-if="record.requester"
              class="pT5 pR8 f13 vertical-middle"
              style="color: #2ea2b2;"
              >{{ record.requester.name }}</span
            >
          </template>
          <template v-else>
            <span class="fc-id">#{{ record['id'] }}</span>
            <div class="heading-black22 max-width500px textoverflow-ellipsis">
              {{ record[mainField] }}
            </div>
          </template>
        </div>
      </div>
      <ApprovalButtons
        ref="ApprovalButtons"
        :moduleName="moduleName"
        :record="record"
        v-bind="approvalButtonProps"
        @onSuccess="goToNext"
        @onFailure="onTransitionFailure"
        class="mL-auto"
      ></ApprovalButtons>
    </div>
    <page
      :key="id"
      :id="id"
      :module="moduleName"
      :primaryFields="primaryFields"
      :notesModuleName="`${docsModuleName}notes`"
      :attachmentsModuleName="`${docsModuleName}attachments`"
      :isApprovalView="true"
      :isMetaLoading="isLoading"
      :approvalMeta="approvalMeta"
      :metaFields="metaFields"
      :moduleMeta="metaInfo"
      @record="enhanceRecord"
    ></page>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Page from '@/page/PageBuilder'
import ApprovalButtons from '@/approval/ApprovalButtons'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: ['id', 'moduleName', 'details', 'updateUrl', 'transformFn'],
  components: { Page, ApprovalButtons },

  data() {
    return {
      record: {},
      approvalMeta: {},
      metaInfo: {},
      isLoading: true,
    }
  },

  async created() {
    this.isLoading = true
    this.record = this.details || {}
    await this.loadMeta()
    await this.loadApprovalMeta()
    this.isLoading = false
  },

  computed: {
    metaFields() {
      let { metaInfo, approvalMeta } = this
      let approvalRule = this.$getProperty(approvalMeta, 'approvalRule')
      let metafields = this.$getProperty(metaInfo, 'fields')

      let configJson = JSON.parse(
        this.$getProperty(approvalRule, 'configJson') || '{}'
      )
      let summaryFields = configJson.fields || []
      let fieldIds = [...summaryFields].filter(id => !isEmpty(id))

      let fields = fieldIds
        .map(id => {
          return (metafields || []).find(({ id: fieldId }) => fieldId === id)
        })
        .filter(f => !isEmpty(f))

      return fields
    },
    primaryFields() {
      let fields = this.metaFields.map(field => field.name)
      return fields
    },
    mainField() {
      let metafields = this.$getProperty(this.metaInfo, 'fields', [])
      let mainField = metafields.find(f => f.mainField)

      if (!isEmpty(mainField)) {
        return mainField.name
      } else {
        return 'name'
      }
    },
    docsModuleName() {
      let { moduleName } = this
      if (moduleName === 'workorder') return 'ticket'
      else return moduleName
    },
    approvalButtonProps() {
      let props = {}
      if (isEmpty(this.updateUrl)) {
        props.updateUrl = this.updateUrl
      }
      if (isFunction(this.transformFn)) {
        props.transformFn = this.transformFn
      }
      return props
    },
  },
  watch: {
    details(newValue, oldValue) {
      let { $getProperty: get } = this
      if (get(newValue, 'id') === get(oldValue, 'id')) {
        this.record = this.details
      }
    },
  },
  methods: {
    async enhanceRecord(record) {
      this.record = record
      await this.loadApprovalMeta()
    },
    goToNext() {
      this.$emit('onTransitionSuccesss', this.record)
    },
    onTransitionFailure() {
      this.$message.error('Could not perform transition')
    },
    loadMeta() {
      return API.get('/module/meta', { moduleName: this.moduleName }).then(
        ({ error, data }) => {
          if (!error && !isEmpty(data.meta)) this.metaInfo = data.meta
        }
      )
    },
    loadApprovalMeta() {
      let {
        moduleName,
        record: { id },
      } = this

      if (!isEmpty(id)) {
        return API.post('v2/approval/approvalDetails', {
          moduleName,
          id,
        }).then(({ error, data }) => {
          if (!error) {
            this.approvalMeta = data
          } else {
            this.$message.error('Could not fetch record details')
          }
        })
      } else Promise.resolve()
    },
    refreshRelatedList() {
      eventBus.$emit('refresh-related-list')
    },
  },
}
</script>
<style lang="scss" scoped>
.summaryHeader {
  min-height: 80px;
  padding: 15px 20px 10px 20px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
</style>
