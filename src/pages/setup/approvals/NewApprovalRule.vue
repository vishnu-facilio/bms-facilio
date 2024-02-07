<template>
  <div class="formbuilder-fullscreen-popup approval-policy-container">
    <div class="setting-header">
      <div v-if="!isNew">
        <div class="pointer fc-link fw-normal f13" @click="goBack">
          <inline-svg
            src="left-arrow"
            iconClass="icon icon-sm vertical-text-top mR5"
          ></inline-svg>
          {{ $t('setup.setupLabel.go_back') }}
        </div>
        <div class="mT10 mB5 f22 fw3 letter-spacing0_5">
          {{ policyTitle }}
        </div>
      </div>
      <div class="d-flex flex-direction-column" v-else>
        <div class="mT10 mB10 f22 fw3 letter-spacing0_5">
          {{ $t('setup.approvalprocess.newapprovalprocess') }}
        </div>
      </div>
      <div class="fR stateflow-btn-wrapper">
        <async-button
          buttonClass="asset-el-btn"
          :clickAction="goBack"
          data-test-selector="save-sla-policy"
        >
          {{ $t('common._common.cancel') }}
        </async-button>
        <async-button
          buttonType="primary"
          buttonClass="asset-el-btn"
          :clickAction="save"
          data-test-selector="save-sla-policy"
        >
          {{ $t('common._common._save') }}
        </async-button>
      </div>
    </div>
    <div class="d-flex setup-grey-bg">
      <div class="sla-sidebar pT10">
        <a
          id="details-link"
          @click="scrollTo('details')"
          class="sla-link active"
        >
          {{ $t('common.products.details') }}
        </a>
        <a id="approvers-link" @click="scrollTo('approvers')" class="sla-link">
          {{ $t('common.header.approvers') }}
        </a>
        <a id="resend-link" @click="scrollTo('resend')" class="sla-link">
          {{ $t('common.header.resend') }}
        </a>
        <a
          id="customization-link"
          @click="scrollTo('customization')"
          class="sla-link"
        >
          {{ $t('common._common.customization') }}
        </a>
        <a id="actions-link" @click="scrollTo('actions')" class="sla-link">
          {{ $t('common.products.actions') }}
        </a>
      </div>
      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <Details
            id="details-section"
            ref="details-section"
            :policy="approval"
            :moduleFields="moduleFields"
            @updateTitle="value => (policyTitle = value)"
            @modified="markAsModified"
            class="mB20"
          ></Details>
          <Approvers
            id="approvers-section"
            ref="approvers-section"
            :policy="approval"
            :moduleFields="moduleFields"
            @modified="markAsModified"
            class="mB20"
          ></Approvers>
          <Resend
            id="resend-section"
            ref="resend-section"
            :policy="approval"
            :moduleFields="moduleFields"
            @modified="markAsModified"
            class="mB20"
          ></Resend>
          <Customization
            id="customization-section"
            ref="customization-section"
            :policy="approval"
            :moduleFields="moduleFields"
            :formFields="formFields"
            @modified="markAsModified"
            @setProps="saveProps"
            class="mB20"
          ></Customization>
          <Actions
            id="actions-section"
            ref="actions-section"
            :policy="approval"
            :moduleFields="moduleFields"
            :formFields="formFields"
            @modified="markAsModified"
            class="mB20"
          ></Actions>
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import AsyncButton from '@/AsyncButton'
import SidebarScrollMixin from 'pages/setup/sla/mixins/SidebarScrollMixin'
import Details from './components/Details'
import Approvers from './components/Approvers'
import Resend from './components/Resend'
import Customization from './components/Customization'
import Actions from './components/Actions'
import { API } from '@facilio/api'

export default {
  name: 'NewApproval',
  props: ['id', 'moduleName'],
  mixins: [SidebarScrollMixin],
  components: {
    Details,
    Approvers,
    Resend,
    Customization,
    Actions,
    AsyncButton,
  },
  created() {
    Promise.all([
      this.fetchData(),
      this.fetchMetaFields(),
      this.fetchFormFields(),
    ])
      .then(() => {
        if (!this.isNew) this.policyTitle = this.approval.name
      })
      .finally(() => {
        this.isLoading = false
        this.$nextTick(this.registerScrollHandler)
      })
  },
  data() {
    return {
      isLoading: true,
      hasChanged: false,
      approval: {},
      moduleFields: [],
      formFields: [],
      policyTitle: 'Approval',
      rootElementForScroll: '.scroll-container',
      sidebarElements: [
        '#details-link',
        '#approvers-link',
        '#resend-link',
        '#customization-link',
        '#actions-link',
      ],
      sectionElements: [
        '#details-section',
        '#approvers-section',
        '#resend-section',
        '#customization-section',
        '#actions-section',
      ],
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.id)
    },
  },
  methods: {
    fetchData() {
      if (this.id) {
        return API.get(`v2/approval/view?id=${this.id}`).then(
          ({ error, data }) => {
            if (!error) {
              this.approval = data.approvalRule
            } else {
              this.$message.error(
                this.$t('common._common.could_not_load_details')
              )
            }
          }
        )
      } else {
        return Promise.resolve()
      }
    },
    fetchFormFields() {
      return API.get(`/v2/forms/fields`, {
        moduleName: this.moduleName,
      }).then(({ data, error }) => {
        if (!error) {
          let fields = this.$getProperty(data, 'fields') || []
          this.formFields =
            fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
        }
      })
    },
    fetchMetaFields() {
      return API.get('/module/metafields', {
        moduleName: this.moduleName,
      }).then(({ data, error }) => {
        if (!error) {
          let fields = this.$getProperty(data, 'meta.fields', []) || []
          this.moduleFields =
            fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
        }
      })
    },
    save(canGoBack = true) {
      let details = this.$refs['details-section'].serialize()
      let approvers = this.$refs['approvers-section'].serialize()
      let forms = this.$refs['customization-section'].serialize()
      let actions = this.$refs['actions-section'].serialize()
      let resendApprovers = this.$refs['resend-section'].serialize()

      return API.post(`v2/approval/addOrUpdate`, {
        moduleName: this.moduleName,
        approvalRule: {
          ...details,
          ...approvers,
          ...actions,
          ...forms,
          ...resendApprovers,
          approvalDialogType: 1,
          rejectDialogType: 1,
          shouldFormInterfaceApply: false,
        },
      }).then(({ error }) => {
        if (!error) {
          if (canGoBack) {
            this.$message.success(this.$t('common.products.approval_saved'))
            this.$nextTick(this.goBack)
          }
        } else {
          this.$message.error(
            error?.message || this.$t('common._common.could_not_save_approval')
          )
        }
      })
    },
    saveProps(props) {
      Object.entries(props).forEach(([key, value]) => {
        this.$set(this.approval, key, value)
      })
    },
    goBack() {
      this.$router.push({
        name: 'approvals.list',
        moduleName: this.moduleName,
      })
    },
    markAsModified() {
      /* Should decide whether to allow save based on this property
       * Currently not used because criteria builder doesn't have a reliable way
       * to tell if it has loaded or not
       */
      this.hasChanged = true
    },
  },
}
</script>
<style lang="scss">
.approval-policy-container {
  border-left: 1px solid #e3e7ed;
  margin-left: 60px;
  margin-top: 50px;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }

  .sla-sidebar {
    background-color: #fff;
    min-width: 300px;
    height: 100vh;
    margin-right: 20px;
  }

  .scroll-container {
    flex-grow: 1;
    margin: 20px 20px 0 0;
    overflow-y: scroll;
    max-height: calc(100vh - 150px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }

  .asset-el-btn {
    height: 40px !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
  }

  .section-header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    margin: 0;
    padding: 28px 50px 20px;

    &.anchor-top {
      position: sticky;
      top: 0;
      width: 100%;
      background: #fff;
      z-index: 2;
      box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
    }
  }

  .sla-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 40px;
    margin: 0;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f3f4f7;
    }
  }

  .el-form {
    width: 95%;
    max-width: 998px;
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

  .criteria-condition-block {
    .el-select {
      background-color: #fff;
    }
  }

  .fc-input-label-txt.txt-color,
  .txt-color {
    color: #324056;
  }
  .configure-blue {
    color: #6171db;
  }
  .configured-green {
    color: #5bc293;
  }
}
</style>
