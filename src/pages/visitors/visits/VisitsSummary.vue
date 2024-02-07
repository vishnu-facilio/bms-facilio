<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-page-dialog-right"
    :before-close="goToList"
  >
    <div v-if="loading" class="height100 flex-middle">
      <spinner :show="true" size="80"></spinner>
    </div>
    <div v-else class="container pB50">
      <div class="header-section border-bottom1px">
        <div class="d-flex flex-col">
          <div class="record-id bold">#{{ record.id }}</div>
          <div class="flex-middle mT10">
            <VisitorAvatar
              module="visitorlog"
              size="lg"
              :recordData="record"
            ></VisitorAvatar>
            <div class="record-name">{{ record.visitorName }}</div>
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
            </div>
          </div>
        </div>
        <div
          v-if="isStateFlowEnabled"
          class="header-actions d-flex flex-row align-center"
        >
          <TransitionButtons
            class="mR10"
            :key="record.id"
            :moduleName="moduleName"
            :record="record"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="loadData(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </div>
      </div>
      <page
        v-if="record && record.id"
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :isSidebarView="true"
        :skipMargins="true"
        :hideScroll="true"
        :isV3Api="true"
      ></page>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import Page from '@/page/PageBuilderFluid'
import TransitionButtons from '@/stateflow/TransitionButtons'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: [
    'viewname',
    'id',
    'moduleName',
    'notesModuleName',
    'attachmentsModuleName',
    'groupinviteInviteSummary',
  ],
  components: { Page, TransitionButtons, VisitorAvatar },
  data() {
    return {
      loading: true,
      record: { id: this.currentId },
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
      moduleMeta: state => state.view.metaInfo,
    }),
    currentId() {
      let { id } = this
      return id ? parseInt(id) : null
    },
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    moduleState() {
      let currentStateId = this.$getProperty(this.record, 'moduleState.id')
      let currentState = (this.states || []).find(
        state => state.id === currentStateId
      )

      return currentState ? currentState.status : null
    },
    isStateFlowEnabled() {
      return this.$getProperty(this.record, 'moduleState.id')
    },
    listRouteName() {
      return 'visits-list'
    },
    primaryFields() {
      return [
        'host',
        'checkInTime',
        'checkOutTime',
        'visitorType',
        'visitorName',
        'avatar',
      ]
    },
  },
  watch: {
    currentId: {
      async handler(newVal, oldVal) {
        if (newVal && newVal !== oldVal) {
          this.loading = true
          this.loadData().finally(() => {
            this.loading = false
          })
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadData(force) {
      let { currentId, moduleName } = this
      let config = force ? { force } : {}

      let { error, [moduleName]: record } = await API.fetchRecord(
        moduleName,
        {
          id: currentId,
        },
        config
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.record = record
      }
    },
    goToList() {
      if(this.groupinviteInviteSummary) {
        this.$emit('closeDialog')
      }
      else{
        let { viewname, listRouteName, moduleName } = this
      let query = this.$route?.query || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.replace({ name, params: { viewname }, query })
        }
      } else {
        this.$router.replace({
          name: listRouteName,
          params: { viewname },
          query,
        })
      }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
}
.header-section {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 25px 30px;

  .record-id {
    font-size: 12px;
    color: #39b2c2;
  }
  .record-name {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.5px;
    word-break: break-word;
    margin-left: 10px;
  }
  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 5px 18px;
  }
}
</style>
