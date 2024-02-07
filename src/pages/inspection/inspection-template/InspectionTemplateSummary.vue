<template>
  <div class="qanda-template-summary">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-if="!$validation.isEmpty(record)" class="header pT10 pB15 pL20 pR20">
      <div class="qanda-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="qanda-id mT10">
              <i
                v-if="$account.portalInfo"
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
              ></i>
              #{{ record && record.id }}
            </div>
            <div class="qanda-name mb5 d-flex">
              {{ record && record.name }}

              <div
                v-if="isStateFlowEnabled && currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="d-flex flex-direction-row align-center"
        style="margin-left: auto;"
      >
        <CustomButton
          class="p10"
          :record="record"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj(true)"
          @onError="() => {}"
        />
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="record.id"
            :moduleName="moduleName"
            :record="record"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>
        <el-button @click="openQAndABuilder" class="execute-now-btn">{{
          $t('qanda.template.qanda_builder')
        }}</el-button>

        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          @command="dropDownActions"
        >
          <span class="el-dropdown-link">
            <inline-svg
              src="svgs/menu"
              class="vertical-middle"
              iconClass="icon icon-md"
            >
            </inline-svg>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="'executeTrigger'">{{
              $t('qanda.template.execute_now')
            }}</el-dropdown-item>
            <el-dropdown-item
              :command="'editRecord'"
              v-if="$hasPermission(`${moduleName}:UPDATE`)"
              >{{ $t('qanda.template.edit') }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <Page
      v-if="!$validation.isEmpty(record) && !loading"
      :key="record.id"
      :module="moduleName"
      :id="record.id"
      :details="record"
      :primaryFields="primaryFields"
      :isV3Api="true"
    ></Page>
    <router-view
      :key="`child-${id}`"
      @reloadlist="loadInspectionRecord"
    ></router-view>
    <QandaExecuteNow
      v-if="canShowExecuteWizard"
      :beforeClose="() => (canShowExecuteWizard = false)"
      :moduleName="moduleName"
      :record="record"
    />
  </div>
</template>

<script>
import Page from '@/page/PageBuilder'
import { API } from '@facilio/api'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import CustomButton from '@/custombutton/CustomButton'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import QandaExecuteNow from '../common/QandaExecuteNow'

export default {
  props: ['moduleName'],
  components: { Page, CustomButton, TransitionButtons, QandaExecuteNow },
  data() {
    return {
      record: null,
      loading: true,
      activeTab: 'summary',
      primaryFields: [],
      POSITION: POSITION_TYPE,
      canShowExecuteWizard: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    ...mapGetters(['getTicketStatus']),
    moduleDisplayName() {
      return 'Inspection'
    },
    id() {
      let paramId = this.$route.params.id
      return paramId && paramId !== 'null'
        ? parseInt(this.$route.params.id)
        : ''
    },
    isStateFlowEnabled() {
      let hasState = this.$getProperty(this.record, 'moduleState.id')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && isEnabled
    },
    isApprovalEnabled() {
      let { record } = this
      let { approvalFlowId, approvalStatus } = record || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    currentModuleState() {
      let { moduleName, record } = this
      let currentStateId = this.$getProperty(record, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
  },
  watch: {
    id: {
      handler() {
        this.loadInspectionRecord()
      },
      immediate: true,
    },
  },
  methods: {
    refreshObj() {
      this.loadInspectionRecord()
    },
    async loadInspectionRecord() {
      let { moduleName, id } = this
      this.loading = true
      let { [moduleName]: data, error } = await API.fetchRecord(moduleName, {
        id,
      })
      if (error) {
        this.$message.error('Error Occured' || error.message)
      } else {
        this.record = data
      }
      this.loading = false
    },
    openQAndABuilder() {
      let { $route = {} } = this
      let viewname = this.$getProperty($route, 'params.viewname', 'all')
      if (isWebTabsEnabled()) {
        this.$router.push({
          path: 'builder',
          query: {
            pageNo: 1,
          },
          params: {
            viewname,
          },
        })
      } else {
        this.$router.push({
          name: 'inspection-builder',
          query: {
            pageNo: 1,
          },
          params: {
            viewname,
          },
        })
      }
    },
    async executeTrigger() {
      let { record, moduleDisplayName } = this
      let { creationType, id } = record
      if (creationType === 2) {
        this.canShowExecuteWizard = true
      } else {
        let { moduleName } = this
        let { error } = await API.post(
          `v3/qanda/template/execute?moduleName=${moduleName}&id=${id}`,
          {}
        )
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(`${moduleDisplayName} is executed`)
        }
      }
    },
    editRecord() {
      let { record } = this || {}
      let { id } = record || {}
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT)
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'inspectionTemplateEdit',
          params: { id },
        })
      }
    },
    dropDownActions(action) {
      if (!isEmpty(this[action])) this[action]()
    },
  },
}
</script>

<style lang="scss" scoped>
.qanda-template-summary {
  .header {
    background: #fff;
    display: flex;
  }
  .qanda-details {
    flex-grow: 1;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .qanda-details .qanda-id {
    font-size: 12px;
    color: #39b2c2;
  }
  .qanda-details .qanda-name {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
  }
  .execute-now-btn {
    border-radius: 3px;
    padding: 8px 18px;
    cursor: pointer;
    border: solid 1px #39b2c2;
    background-color: #39b2c2;
    color: #fff;
    letter-spacing: 1.1px;
    text-align: center;
    text-transform: uppercase;
    font-weight: 500;
    font-size: 12px;
    float: right;
    height: 40px;
    cursor: pointer;
    &:hover {
      background-color: #3cbfd0 !important;
      color: #fff !important;
    }
  }

  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 3px 12px 4px;
    font-weight: bold;
  }
}
</style>
