<template>
  <div
    class="formbuilder-fullscreen-popup vm-template-container form-customization-container"
  >
    <div class="setting-header pT10 pB10">
      <div class="d-flex flex-direction-column">
        <div class="mT10 mB10 f18 bold">{{ title }}</div>
      </div>
      <div class="fR align-center">
        <AsyncButton buttonClass="vm-cancel-btn" :clickAction="back">
          {{ $t('common._common.cancel') }}
        </AsyncButton>
        <AsyncButton buttonClass="vm-save-btn" :clickAction="save">
          {{ $t('common._common._save') }}
        </AsyncButton>
      </div>
    </div>
    <div class="d-flex grey-bg">
      <div class="sla-sidebar pT10">
        <a
          id="vmtemplate-link"
          @click="scrollTo('vmtemplate')"
          class="sla-link active"
        >
          {{ $t('asset.virtual_meters.virtual_meter_template') }}
        </a>
        <a
          id="meterDetails-link"
          @click="scrollTo('meterDetails')"
          class="sla-link"
        >
          {{ $t('asset.virtual_meters.meters_details') }}
        </a>
        <a
          id="readingDetails-link"
          @click="scrollTo('readingDetails')"
          class="sla-link"
        >
          {{ $t('asset.virtual_meters.reading_details') }}
        </a>
      </div>
      <div class="scroll-container vm-template">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <VMTemplate
            id="vmtemplate-section"
            ref="vmtemplate-section"
            :isNew="!isEditForm"
            :vmInfo="virtualMeter"
            class="mB20"
            @updateVirtualMeter="updateVirtualMeter"
          ></VMTemplate>
          <VMDetailsForm
            id="meterDetails-section"
            ref="meterDetails-section"
            class="mB20"
            :isNew="!isEditForm"
            :vmInfo="virtualMeter"
            @updateVirtualMeter="updateVirtualMeter"
          ></VMDetailsForm>
          <ReadingDetailsForm
            id="readingDetails-section"
            ref="readingDetails-section"
            class="mB20 height74vh"
            :isNew="!isEditForm"
            :vmInfo="virtualMeter"
            @updateVirtualMeter="updateVirtualMeter"
          >
          </ReadingDetailsForm>
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>

<script>
import AsyncButton from 'src/components/AsyncButton'
import VMTemplate from './VMTemplate.vue'
import ReadingDetailsForm from './ReadingDetailsForm.vue'
import VMDetailsForm from './VMDetailsForm.vue'
import SidebarScrollMixin from 'src/pages/setup/sla/mixins/SidebarScrollMixin'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { vmDataModel, serializeVM, deserializeVM } from './vmUtil'
import { facilioMicro } from 'util/microEmbed'

export default {
  components: {
    AsyncButton,
    VMTemplate,
    ReadingDetailsForm,
    VMDetailsForm,
  },
  mounted() {
    this.$nextTick(this.registerScrollHandler)
  },
  mixins: [SidebarScrollMixin],
  async created() {
    let { isEditForm } = this
    if (isEditForm) {
      await this.prefillVMTemplate()
      this.isNew = false
    }
  },
  data() {
    return {
      moduleName: 'virtualMeterTemplate',
      isLoading: false,
      virtualMeter: cloneDeep(vmDataModel),
      rootElementForScroll: '.scroll-container',
      sidebarElements: [
        '#vmtemplate-link',
        '#meterDetails-link',
        '#readingDetails-link',
      ],
      sectionElements: [
        '#vmtemplate-section',
        '#meterDetails-section',
        '#readingDetails-section',
      ],
      isNew: true,
    }
  },
  computed: {
    title() {
      let { isNew } = this
      return isNew
        ? this.$t('asset.virtual_meters.new_virtual_meter_template')
        : this.$t('asset.virtual_meters.edit_virtual_meter_template')
    },
    isEditForm() {
      return this.$getProperty(this, '$route.params.id', false)
    },
  },
  methods: {
    async save() {
      let { virtualMeter, moduleName, isEditForm } = this
      virtualMeter = serializeVM(virtualMeter)
      let { readings } = virtualMeter || {}
      let isTemplateValid = await this.$refs['vmtemplate-section'].validate()
      let isMeterDetailsValid = await this.$refs[
        'meterDetails-section'
      ].validate()
      let isReadingsValid = await this.$refs[
        'readingDetails-section'
      ].validateVMReadings(readings)

      if (isTemplateValid && isMeterDetailsValid && isReadingsValid) {
        let promise
        let { id } = virtualMeter || {}
        if (!isEditForm) {
          promise = await API.createRecord(moduleName, {
            data: virtualMeter,
          })
        } else {
          promise = await API.updateRecord(moduleName, {
            data: virtualMeter,
            id,
          })
        }
        let { virtualMeterTemplate, error } = promise || {}

        if (isEmpty(error)) {
          if (!isEditForm) {
            let { id } = virtualMeterTemplate || {}
            facilioMicro.emit('saveVMTemplate', { type: 'create', id })
          } else {
            facilioMicro.emit('saveVMTemplate', { type: 'edit', id })
          }
        } else {
          facilioMicro.emit('saveVMTemplate', {
            type: 'error',
            message: error?.message || 'Error Occured',
          })
        }
      }
    },
    back() {
      let { virtualMeter, isEditForm } = this
      if (!isEditForm) {
        facilioMicro.emit('cancel', { navigateTo: 'list' })
        //this.redirectToList()
      } else {
        let { id } = virtualMeter || {}
        facilioMicro.emit('cancel', { navigateTo: 'summary', id })
        //this.redirectToSummary(id)
      }
    },
    redirectToList() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({
          name: 'VirtualMeterTemplateList',
        })
      }
    },
    redirectToSummary(id) {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({
            name,
            params: {
              id,
              viewname: 'all',
            },
          })
        }
      } else {
        this.$router.push({
          name: 'VirtualMeterTemplateSummary',
          params: {
            id,
            viewname: 'all',
          },
        })
      }
    },
    updateVirtualMeter(value) {
      let { virtualMeter } = this
      this.virtualMeter = { ...virtualMeter, ...value }
    },
    async prefillVMTemplate() {
      this.isLoading = true
      let id = this.$getProperty(this, '$route.params.id')
      await this.loadSummary(id)
      this.$nextTick(() => {
        this.isLoading = false
      })
    },
    async loadSummary(id, force = true) {
      let { moduleName } = this
      let { virtualMeterTemplate, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force }
      )
      if (!error) {
        this.virtualMeter = deserializeVM(virtualMeterTemplate)
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
    },
  },
}
</script>
<style lang="scss">
.grey-bg {
  background-color: #f8f9fa;
}
.form-customization-container {
  height: 100vh;
  position: fixed;
  top: 50px;
  left: 0;
  right: 0;
  padding-left: 0px !important;
}
.vm-template-container {
  border-left: 1px solid #e3e7ed;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }

  .sla-sidebar {
    background-color: #fff;
    min-width: 350px;
    height: 100vh;
    margin-right: 20px;
  }

  .scroll-container {
    flex-grow: 1;
    margin: 20px 20px 0 0;
    overflow-y: scroll;
    max-height: calc(100vh - 100px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }
  .vm-section-header {
    font-size: 14px;
    font-weight: 500;
    margin: 0;
    padding: 28px 50px 20px;
    position: sticky;
    top: 0;
    width: 100%;
    background: #fff;
    z-index: 13;
    box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
  }

  .sla-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 27px;
    margin: 0;
    color: #324056;
    font-size: 14px;
    border-left: 4px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f2f5fa;
      color: #324056;
      font-weight: 500;
      border-left: 4px solid #0074d1;
    }
  }

  .el-form {
    width: 95%;
    max-width: 998px;
    padding-right: 20%;
  }

  .el-table::before {
    background: initial;
  }
  .el-table th .cell {
    letter-spacing: 1.6px;
    color: #385571;
    padding-left: 0;
  }
  .el-table--border td:first-child .cell {
    padding-left: initial;
  }
  .el-table__empty-block {
    border-bottom: 1px solid #ebeef5;
  }
  .el-table__row .actions {
    visibility: hidden;
  }
  .el-table__row:hover .actions {
    visibility: visible;
  }

  .sla-criteria .fc-modal-sub-title {
    color: #385571;
  }

  .vm-cancel-btn {
    width: 85px;
    height: 35px;
    border-radius: 4px;
    border: solid 1px #c6cdd2;
    text-transform: capitalize;
    color: #1d384e;
    font-weight: 500;
    font-size: 14px;
    margin-left: 15px !important;
    padding-top: 10px;
    &:hover {
      background-color: #0074d1;
      color: #ffffff;
    }
    &:active {
      color: #324056;
      background-color: #ffffff;
      border: solid 1px #0074d1;
    }
  }
  .vm-save-btn {
    width: 85px;
    height: 35px;
    border-radius: 4px;
    border-color: transparent;
    background-color: #0074d1;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    text-transform: capitalize;
    padding-top: 10px;
    &:hover {
      background-color: #0074d1;
      color: #ffffff;
    }
    &:active {
      color: #fff;
      background-color: #0074d1;
      border: transparent;
    }
  }

  .disable-save {
    pointer-events: none;
    opacity: 0.4;
  }
  .height74vh {
    min-height: 74vh;
  }

  .template-heading,
  .el-form-item__label {
    font-size: 14px;
    font-weight: 500;
    color: #324056;
  }
  .el-input__inner,
  .el-textarea__inner {
    border-bottom: 1.2px solid #9ca7b0 !important;
  }
}
</style>
