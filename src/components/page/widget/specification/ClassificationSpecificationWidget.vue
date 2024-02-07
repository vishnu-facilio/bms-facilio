<template>
  <div class="classification-specification-container">
    <div v-if="isLoading" class="flex-middle height-100">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(canShowWidget)"
      class="classification-specification-empty-state"
    >
      <inline-svg src="svgs/list-empty" iconClass="icon icon-130"></inline-svg>
      <div class="classification-specification-empty-state-text">
        {{ $t('setup.classification.empty_state_association') }}
      </div>
      <el-button
        type="primary"
        class="classification-specification-add-btn"
        @click="canShowSpecificationWizard = true"
      >
        {{ $t('setup.classification.associate_classification') }}
      </el-button>
    </div>

    <div v-else class="classification-association-container">
      <field-details
        ref="fieldDetails"
        :columns="noOfColumns"
        :detailsLayout="detailsLayout"
        :config="config"
      ></field-details>
      <div class="specification-right-content d-flex p20">
        <el-dropdown @command="handleCommand">
          <i class="el-icon-edit pointer"></i>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="editItem in editDropDownList"
              :key="`${editItem}-index`"
              :command="editItem"
              >{{ editItem }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
        <div @click="showConfirmDelete">
          <el-tooltip
            effect="dark"
            :content="$t('setup.classification.remove_classification')"
            placement="top-end"
          >
            <inline-svg
              src="svgs/spacemanagement/ic-delete"
              class="mL20 pointer"
              iconClass="icon icon-md"
            ></inline-svg>
          </el-tooltip>
        </div>
      </div>
    </div>
    <SpecificationWizard
      v-if="canShowSpecificationWizard"
      isAttributeInherited="true"
      :fromModuleName="moduleName"
      @onSave="selectedClassification"
      @specificationAttributes="v => (attributesList = v)"
      @onClose="canShowSpecificationWizard = false"
    ></SpecificationWizard>
    <SpecificationAttributes
      v-if="canShowSpecificationAttributes"
      :selectedAttribute="attributesList"
      @onClose="onCancelAttributes"
      @onSave="saveAttribute"
    ></SpecificationAttributes>
  </div>
</template>
<script>
import { NewFieldDetails as FieldDetails } from '@facilio/ui/app'
import SpecificationWizard from 'src/newapp/components/classification/SpecificationWizard'
import SpecificationAttributes from './SpecificationAttributes'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import { dataTypes, dataTypeKey } from './classificationUtil'
import { mapGetters, mapActions } from 'vuex'
import { formatDate } from 'src/util/filters.js'

export default {
  props: [
    'moduleName',
    'id',
    'resizeWidget',
    'layoutParams',
    'calculateDimensions',
  ],
  components: { SpecificationWizard, SpecificationAttributes, FieldDetails },
  data() {
    return {
      detailsLayout: null,
      canShowSpecificationWizard: false,
      canShowSpecificationAttributes: false,
      classificationData: null,
      attributesList: null,
      isLoading: false,
      editDropDownList: [
        this.$t('setup.classification.attributes.edit_attribute'),
      ],
    }
  },
  async created() {
    await this.loadMetricUnits()
    await this.init()
    this.autoResize()
  },
  computed: {
    noOfColumns() {
      return !isEmpty(this.attributesList) ? 4 : 1
    },
    config() {
      return {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
        showDivider: false,
        org: this.$account.org,
      }
    },
    canShowWidget() {
      let { details } = this
      return details.classification || []
    },
    //Check for Workorder due to internal dependency
    details() {
      let { moduleName, $attrs } = this
      let { details } = $attrs || {}
      let { workorder = {} } = details || {}

      return moduleName === 'workorder' ? workorder : details
    },
    ...mapGetters({
      getMetrics: 'metricUnits/getMetrics',
      getMetricsUnit: 'metricUnits/getMetricsUnit',
    }),
  },
  methods: {
    ...mapActions({
      loadMetricUnits: 'metricUnits/loadMetricUnits',
    }),
    async init() {
      let { classification } = this.details || {}

      if (!isEmpty(classification)) {
        let { name, id, attributes } = classification || {}

        this.classificationData = { name, id }
        this.attributesList = attributes
        this.detailsLayout = await this.serializeDetailsLayout()
      }
    },
    autoResize(reset) {
      setTimeout(() => {
        let { h: layoutHeight } = this.layoutParams || {}

        if (reset) {
          this.resizeWidget({ h: layoutHeight })
          return
        }

        let container = this.$refs['fieldDetails']?.$el

        if (!container) return

        let height = container.scrollHeight + 20
        let width = container.scrollWidth
        let { h: actualHeight } = this.calculateDimensions({ height, width })
        let h =
          actualHeight > layoutHeight || actualHeight < layoutHeight
            ? actualHeight
            : layoutHeight

        if (actualHeight !== layoutHeight) this.resizeWidget({ h })
      })
    },
    async serializeDetailsLayout() {
      let serializedFields = await this.serializeFields()
      let { name } = this.classificationData || {}
      let classificationField = [
        {
          name: this.$t('setup.classification.classification'),
          value: name,
        },
      ]
      let data = [
        {
          name: this.$t('setup.classification.classification'),
          fields: classificationField,
        },
        {
          name: this.$t('setup.classification.attributes.attributes'),
          fields: serializedFields,
        },
      ]
      return data
    },
    async serializeFields() {
      if (!isEmpty(this.attributesList)) {
        let { DATE_TIME, BOOLEAN, DECIMAL, NUMBER } = dataTypes || {}
        let fields = (this.attributesList || []).map(attr => {
          let { name, value, fieldType, unitId, metric } = attr || {}

          if (fieldType === DATE_TIME) {
            value = value ? formatDate(value, true) : null
          }
          value = !isEmpty(value) ? value : null
          let result = {
            displayName: name,
            value,
            name,
            dataTypeEnum: dataTypeKey[fieldType],
          }
          if (fieldType === BOOLEAN) {
            result = { ...result, options: { trueVal: 'Yes', falseVal: 'No' } }
          } else if (fieldType === DECIMAL || fieldType === NUMBER) {
            let unit = this.getUnitName(metric, unitId)
            result = { ...result, options: { unit } }
          }
          return result
        })

        return fields
      } else {
        return [
          {
            name: this.$t('setup.classification.attributes.attributes'),
            value: this.$t('setup.classification.no_attribute_associated'),
          },
        ]
      }
    },
    getUnitName(metric, unitId) {
      let options = this.getMetricsUnit({ metricId: metric })
      let unitLabel = ''
      options.forEach(unit => {
        let { label, value } = unit
        if (value === unitId) unitLabel = label
      })
      return unitLabel
    },
    async selectedClassification(item) {
      this.classificationData = item
      this.canShowSpecificationAttributes = true
    },
    async saveAttribute(item) {
      this.canShowSpecificationWizard = false
      let attributesData = await this.filteredAttributes(item)

      this.saveClassification(attributesData)
    },
    filteredAttributes(attributes) {
      let { NUMBER, DECIMAL } = dataTypes || {}
      let serializedAttribute = (attributes || []).map(attr => {
        let { fieldType, value } = attr || {}

        if (fieldType === NUMBER)
          value = !isEmpty(value) ? parseInt(value) : null
        else if (fieldType === DECIMAL)
          value = !isEmpty(value) ? parseFloat(value) : null

        return { ...attr, value }
      })

      return serializedAttribute
    },
    serialize(attributesData = null) {
      let { id, moduleName, classificationData } = this
      let { id: classificationId } = classificationData || []
      let classification = null

      if (!isEmpty(classificationId)) {
        classification = { id: classificationId, attributes: attributesData }
      }

      return { data: { classification }, id, moduleName }
    },
    async saveClassification(attributesData = null) {
      this.isLoading = true
      this.canShowSpecificationAttributes = false

      let params = this.serialize(attributesData)
      let { error } = await API.post('v3/modules/data/patch', params)

      if (error) {
        this.$message.error(error.message)
      } else {
        this.$message.success(
          this.$t('setup.classification.associate_successfully')
        )
        eventBus.$emit('refresh-overview')
      }
      this.isLoading = false
      this.autoResize(isEmpty(this.detailsLayout))
    },
    onCancelAttributes() {
      this.canShowSpecificationWizard = false
      this.canShowSpecificationAttributes = false
    },
    handleCommand() {
      this.canShowSpecificationAttributes = true
    },
    showConfirmDelete() {
      let dialogObj = {
        title: `${this.$t('setup.classification.delete_associate')}`,
        htmlMessage: `${this.$t('setup.classification.delete_associate_msg')}`,
        rbDanger: true,
        rbLabel: this.$t('common.login_expiry.rbLabel'),
      }

      this.$dialog.confirm(dialogObj).then(async value => {
        if (value) {
          this.classificationData = null
          this.isLoading = true

          let params = this.serialize()
          let { error } = await API.post('v3/modules/data/patch', params)

          if (!error) {
            this.detailsLayout = null
            this.$message.success(this.$t('setup.classification.delete_sucess'))
            eventBus.$emit('refresh-overview')
          }
          this.isLoading = false
        }
      })
    },
  },
}
</script>
<style lang="scss">
.classification-specification-container {
  .classification-specification-empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: auto;
    height: 100%;

    .classification-specification-empty-state-text {
      font-size: 16px;
      font-weight: 500;
      margin-top: 20px;
    }
    .classification-specification-add-btn {
      background-color: rgb(57, 178, 194);
      height: 42px;
      border: none;
      text-transform: uppercase;
      font-size: 12px;
      letter-spacing: 0.7px;
      font-weight: bold;
      cursor: pointer;
      color: #ffffff;
      border-radius: 3px;
      margin-top: 20px;
    }
  }
  .classification-association-container {
    padding: 4px;
    letter-spacing: 0.5px;
    .selected-attribute-row {
      flex-wrap: wrap;
    }
    .specification-right-content {
      position: absolute;
      top: 0;
      right: 0;
    }
    .selected-attribute-row > * {
      flex: 0 22%;
    }
    .attribute-name {
      padding-bottom: 8px;
      color: #797b80;
    }
    .attribute-value {
      width: 200px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
  .specification-right-content {
    .el-dropdown {
      display: inline-block;
      position: relative;
      color: #606266;
      font-size: 16px;
    }
  }
}
</style>
