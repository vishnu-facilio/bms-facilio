<template>
  <div class="formbuilder-fullscreen-popup sla-policy-container">
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
          {{ `Edit ${policyTitle}` }}
        </div>
      </div>
      <div class="d-flex flex-direction-column" v-else>
        <div class="mT10 mB10 f22 fw3 letter-spacing0_5">
          {{ $t('setup.new.new_sla') }}
        </div>
      </div>
      <div class="fR stateflow-btn-wrapper">
        <async-button
          buttonClass="asset-el-btn"
          :clickAction="goBack"
          data-test-selector="save-sla-policy"
        >
          {{ $t('setup.users_management.cancel') }}
        </async-button>
        <async-button
          buttonType="primary"
          buttonClass="asset-el-btn"
          :clickAction="save"
          data-test-selector="save-sla-policy"
        >
          {{ $t('maintenance._workorder.save') }}
        </async-button>
      </div>
    </div>
    <div class="d-flex setup-grey-bg">
      <div class="sla-sidebar pT10">
        <a id="policy-link" @click="scrollTo('policy')" class="sla-link active">
          {{ $t('setup.setupLabel.policy') }}
        </a>
        <a
          id="commitment-link"
          @click="scrollTo('commitment')"
          class="sla-link"
        >
          {{ $t('setup.setupLabel.commitments') }}
        </a>
        <a
          id="escalation-link"
          @click="scrollTo('escalation')"
          class="sla-link"
        >
          {{ $t('setup.setupLabel.escalations') }}
        </a>
      </div>
      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <NewPolicy
            id="policy-section"
            ref="policy-section"
            :policy="slaPolicy"
            :entities="entities"
            @updateTitle="value => (policyTitle = value)"
            @modified="markAsModified"
            class="mB20"
          ></NewPolicy>
          <NewCommitments
            id="commitment-section"
            ref="commitment-section"
            :policy="slaPolicy"
            :entities="entities"
            @modified="markAsModified"
            class="mB20"
          ></NewCommitments>
          <NewEscalations
            id="escalation-section"
            ref="escalation-section"
            :policy="slaPolicy"
            :entities="entities"
            @modified="markAsModified"
            class="mB20"
          ></NewEscalations>
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

import AsyncButton from '@/AsyncButton'
import http from 'util/http'
import NewPolicy from './new/NewPolicy'
import NewCommitments from './new/NewCommitments'
import NewEscalations from './new/NewEscalations'
import SidebarScrollMixin from './mixins/SidebarScrollMixin'

export default {
  name: 'New-SLA',
  props: ['id', 'moduleName'],
  mixins: [SidebarScrollMixin],
  components: {
    NewPolicy,
    NewCommitments,
    NewEscalations,
    AsyncButton,
  },
  created() {
    let { $store } = this

    Promise.all([
      $store.dispatch('loadSites'),
      this.fetchPolicyDetails(),
      this.fetchSLAEntities(),
    ])
      .then(() => {
        if (!this.isNew) this.policyTitle = this.slaPolicy.name
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
      slaPolicy: null,
      policyTitle: 'SLA Policy',
      entities: [],
      rootElementForScroll: '.scroll-container',
      sidebarElements: ['#policy-link', '#commitment-link', '#escalation-link'],
      sectionElements: [
        '#policy-section',
        '#commitment-section',
        '#escalation-section',
      ],
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.id)
    },
  },
  methods: {
    fetchSLAEntities() {
      return this.$http
        .post(`v2/slaEntity/list`, {
          moduleName: this.moduleName,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.entities = data.result.slaEntityList || []
          }
        })
    },
    fetchPolicyDetails() {
      if (this.id)
        return http
          .post(`v2/slaPolicy/getWithChildren`, {
            slaPolicyId: this.id,
          })
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.slaPolicy = data.result.slaPolicy
            } else {
              throw new Error()
            }
          })
          .catch(() => {
            this.$message.error('Could not load SLA Policy')
          })
      else return Promise.resolve()
    },
    async save() {
      let valid = await this.validate()
      if (!valid) return
      let policy = this.$refs['policy-section'].serialize()
      let commitments = this.$refs['commitment-section'].serialize()
      let escalations = this.$refs['escalation-section'].serialize()

      return http
        .post(`v2/slaPolicy/addOrUpdateWithChildren`, {
          moduleName: this.moduleName,
          slaPolicy: {
            ...policy,
            commitments,
            escalations,
          },
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$message.success('SLA Policy saved')
            this.$nextTick(this.goBack)
          } else {
            throw new Error(data.message || '')
          }
        })
        .catch((error = {}) => {
          let message = !isEmpty(error.message)
            ? error.message
            : 'Could not save SLA Policy'

          this.$message.error(message)
        })
    },
    async validate() {
      let policyValid = await this.$refs['policy-section'].validation()
      return policyValid
    },
    goBack() {
      this.$router.push({
        name: 'sla.list',
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
.sla-policy-container {
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
      top: -1px;
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

  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid #39b2c2;
    background-color: #f7feff;
    min-height: 36px;
    &:hover {
      border: 1px solid #39b2c2;
      background-color: #f7feff;
    }
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
}
</style>
