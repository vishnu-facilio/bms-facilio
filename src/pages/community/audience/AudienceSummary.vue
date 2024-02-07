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
          <div class="record-name">
            {{ record.name }}
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
            </div>
          </div>
        </div>
        <div class="header-actions d-flex flex-row align-center">
          <template v-if="isStateFlowEnabled">
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
          </template>
          <el-dropdown
            v-if="canShowActionButtons"
            class="dialog-dropdown"
            @command="dropdownActionHandler"
          >
            <span class="el-dropdown-link">
              <InlineSvg src="menu" iconClass="icon icon-md"></InlineSvg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <template v-if="hasUpdatePermission">
                <el-dropdown-item v-if="!record.isPublished" command="edit">
                  {{ $t('common._common.edit') }}
                </el-dropdown-item>
              </template>
              <el-dropdown-item v-if="hasDeletePermission" command="delete">
                {{ $t('common._common.delete') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <page
        v-if="record && record.id"
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
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
import Page from '@/page/PageBuilderFluid'
import TransitionButtons from '@/stateflow/TransitionButtons'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['viewname', 'id', 'moduleName'],
  components: { Page, TransitionButtons },
  data() {
    return {
      loading: true,
      record: { id: this.id },
      fieldsMap: {},
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.loadModuleMeta()
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
      moduleMeta: state => state.view.metaInfo,
    }),
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
      let hasState = this.$getProperty(this.record, 'moduleState.id')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && isEnabled
    },
    primaryFields() {
      return ['name', 'audienceSharing', 'filterSharingType']
    },
    canShowActionButtons() {
      let { hasUpdatePermission, hasDeletePermission } = this
      return hasUpdatePermission || hasDeletePermission
    },
    hasUpdatePermission() {
      return this.$hasPermission(`${this.moduleName}:UPDATE`)
    },
    hasDeletePermission() {
      return this.$hasPermission(`${this.moduleName}:DELETE`)
    },
  },
  watch: {
    id: {
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
      let { id, moduleName } = this
      let config = force ? { force } : {}

      let { error, [moduleName]: record } = await API.fetchRecord(
        moduleName,
        {
          id,
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
      let { viewname, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.replace({ name, params: { viewname } })
        }
      } else {
        this.$router.replace({
          name: 'audienceList',
          params: { viewname },
        })
      }
    },
    goToEdit() {
      let { id, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.replace({ name, params: { id } })
        }
      } else {
        this.$router.replace({
          name: 'edit-audience',
          params: { id },
        })
      }
    },
    loadModuleMeta() {
      this.$store
        .dispatch('view/loadModuleMeta', this.moduleName)
        .then(meta => {
          meta.fields.forEach(field => {
            this.$setProperty(this.fieldsMap, `${field.name}`, field)
          })
        })
    },
    dropdownActionHandler(command) {
      if (command === 'edit') {
        this.goToEdit()
      } else if (command === 'delete') {
        this.deleteRecord()
      }
    },
    async deleteRecord() {
      let value = await this.$dialog.confirm({
        title: this.$t(`common._common.delete`),
        message: this.$t(`common._common.delete_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName, id } = this
        let { error } = await API.deleteRecord(moduleName, id)
        if (error) {
          let { message } = error
          this.$message.error(message || 'Error Occured while deleting')
        } else {
          this.$message.success(this.$t(`common._common.delete_success`))
          this.$emit('refreshList')
          this.goToList()
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
.publish-bar {
  background-color: #f5fdff;
  padding: 15px;
  display: flex;
  border-bottom: 1px solid #c8dfe4;
  justify-content: center;
  align-items: center;
  color: #25243e;

  .publish-btn {
    color: #409eff;
    border-color: #409eff;
    background: none;
    margin-left: 20px;
  }
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
  }
  .fc-badge {
    color: #fff;
    background-color: #23b096;
    padding: 5px 18px;
  }
  .dialog-dropdown {
    border-radius: 2px;
    border: solid 1px #dae0e8;
    padding: 10px 0px;
    cursor: pointer;
    .el-dropdown-link {
      padding: 0px 10px;
    }
  }
  .deals-summary-header {
    display: flex;
    flex-direction: row;
    .sub-text {
      font-size: 13px;
      letter-spacing: 0.5px;
      color: #324056;
    }
    .border {
      border-right: solid 1px #edf0f3;
      margin-right: 10px;
      margin-left: 10px;
    }
    .expiry-date {
      font-size: 13px;
      letter-spacing: 0.5px;
      color: #e68829;
    }
  }
}
</style>

<style lang="scss">
.rich-text-container {
  p:empty::after {
    content: '\00A0';
  }
  blockquote {
    padding: 0px 0px 0px 1rem;
    border-left: 3px solid rgba(#0d0d0d, 0.1);
    font-size: 1em;
  }
  img {
    max-width: 100%;
    height: auto;
  }
  p {
    margin-block-start: 0px !important;
    margin-block-end: 0px !important;
  }
  .tableWrapper {
    padding: 1rem 0;
    overflow-x: auto;
  }
  ul {
    padding: 0 1rem;
    list-style-type: disc;
  }
  ol {
    padding: 0 1rem;
    list-style: auto;
  }
  table {
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;
    margin: 0;
    overflow: hidden;

    td,
    th {
      min-width: 1em;
      border: 2px solid #ced4da;
      padding: 3px 5px;
      vertical-align: top;
      box-sizing: border-box;
      position: relative;

      > * {
        margin-bottom: 0;
      }
    }

    th {
      font-weight: bold;
      text-align: left;
      background-color: #f1f3f5;
    }

    .richtext-selectedCell:after {
      z-index: 2;
      position: absolute;
      content: '';
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      background: rgba(200, 200, 255, 0.4);
      pointer-events: none;
    }

    .column-resize-handle {
      position: absolute;
      right: -2px;
      top: 0;
      bottom: -2px;
      width: 4px;
      background-color: #adf;
      pointer-events: none;
    }

    p {
      margin: 0;
    }
  }
  h1 {
    font-size: 2.5em;
    margin: 1.75em 0;
  }
  h2 {
    font-size: 1.85em;
    margin: 1.5em 0;
  }
  h3 {
    font-size: 1.5em;
    margin: 1em 0;
  }
  h1,
  h2,
  h3 {
    font-weight: 300;
  }
  p {
    font-size: 1em;
    margin: 0 0 1em;
    padding: 0;
    letter-spacing: 0;
    -webkit-font-smoothing: antialiased;
  }
}
</style>
