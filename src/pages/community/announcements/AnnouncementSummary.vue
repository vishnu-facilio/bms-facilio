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
      <template v-if="!$validation.isEmpty(imageAttachments)">
        <el-carousel
          trigger="click"
          :autoplay="false"
          indicator-position="none"
          class="f-page-dialog-carousel"
        >
          <el-carousel-item v-for="image in imageAttachments" :key="image.id">
            <el-image
              :key="record.id + image.id"
              :src="image ? $prependBaseUrl(image.previewUrl) : ''"
              class="width100 height100"
              fit="cover"
            >
              <div slot="error">
                <div>
                  <InlineSvg
                    src="svgs/photo"
                    iconClass="icon fill-grey icon-xxlll op5"
                  ></InlineSvg>
                </div>
              </div>
            </el-image>
          </el-carousel-item>
        </el-carousel>
      </template>
      <div class="header-section border-bottom1px">
        <div class="d-flex flex-col">
          <div class="record-id bold">#{{ record.id }}</div>
          <div class="record-name">
            {{ record.title }}
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
            </div>
          </div>
        </div>
        <div class="header-actions d-flex flex-row align-center">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="loadData(true)"
            @onError="() => {}"
          />
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
          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              :moduleName="moduleName"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="loadData(true)"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <template v-if="record.isPublished && moduleName === 'announcement'">
            <div class="viewers-count">
              <div
                class="flex-center-row-space width-auto pointer mL10"
                @click="showMoreDialog(false)"
                style="min-width:40px"
              >
                <img
                  src="~assets/svgs/community-features/timeline-announcement.svg"
                  class="mR5"
                  style="width:20px;height:20px"
                />
                <span class="fw6">
                  {{ allCount }}
                </span>
              </div>
              <div
                class="flex-center-row-space width-auto pointer mR10 mL10"
                @click="showMoreDialog(true)"
                style="min-width:40px"
              >
                <img
                  src="~assets/svgs/community-features/read-announcement.svg"
                  class="mR5"
                  style="width:20px;height:20px"
                />
                <span class="fw6">
                  {{ readCount }}
                </span>
              </div>
            </div>
            <div class="count-refresh" @click="getPeopleAnnouncement()">
              <spinner v-if="countLoading" :show="true" size="20"></spinner>

              <img
                v-else
                src="~assets/svgs/community-features/ic-refresh.svg"
                class="mR5"
                style="width:20px;height:20px"
              />
            </div>
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
                <el-dropdown-item
                  v-if="!record.isPublished && !isApprovalEnabled"
                  command="publish"
                >
                  Publish
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="record.isPublished && !record.isCancelled"
                  command="cancel"
                >
                  Cancel
                </el-dropdown-item>
                <el-dropdown-item v-if="!record.isPublished" command="edit">
                  Edit
                </el-dropdown-item>
                <el-dropdown-item command="clone">
                  {{ $t('common._common.clone') }}
                </el-dropdown-item>
              </template>
              <el-dropdown-item v-if="hasDeletePermission" command="delete">
                Delete
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div v-if="!record.isPublished && !isApprovalEnabled" class="publish-bar">
        <span>
          This announcement has not been published yet.
        </span>
        <AsynButton
          size="mini"
          :clickAction="publishAnnouncement"
          buttonClass="publish-btn"
        >
          Publish
        </AsynButton>
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
      <PeopleList
        v-if="showMore"
        :key="record.name"
        :details="record"
        :sharingModuleName="sharingModuleName"
        :showDialog="showMore"
        :isRead="isRead"
      >
      </PeopleList>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import Page from '@/page/PageBuilderFluid'
import { isEmpty } from '@facilio/utils/validation'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import AsynButton from 'src/components/AsyncButton'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import ApprovalBar from '@/approval/ApprovalBar'
import CustomButton from '@/custombutton/CustomButton'
import PeopleList from 'src/pages/community/components/PeopleList.vue'

export default {
  props: [
    'viewname',
    'id',
    'moduleName',
    'notesModuleName',
    'attachmentsModuleName',
  ],
  components: {
    Page,
    TransitionButtons,
    AsynButton,
    PeopleList,
    ApprovalBar,
    CustomButton,
  },
  data() {
    return {
      loading: true,
      record: { id: this.id },
      attachments: [],
      fieldsMap: {},
      allCount: 0,
      POSITION: POSITION_TYPE,
      readCount: 0,
      isRead: null,
      showMore: false,
      countLoading: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.loadModuleMeta()
    this.init()
  },
  mounted() {
    eventBus.$on('listDialogClose', this.closeDialog)
  },
  computed: {
    ...mapState({
      ticketStatus: state => state.ticketStatus,
      moduleMeta: state => state.view.metaInfo,
    }),
    sharingModuleName() {
      return this.$getProperty(
        this.widget,
        'widgetParams.sharingInfoModuleName'
      )
    },
    states() {
      let { ticketStatus, moduleName, $getProperty } = this
      return ticketStatus ? $getProperty(ticketStatus, moduleName) : []
    },
    shouldHideApprovers() {
      return true
    },

    moduleState() {
      let currentStateId = this.$getProperty(this.record, 'moduleState.id')
      let currentState = (this.states || []).find(
        state => state.id === currentStateId
      )

      return currentState ? currentState.displayName : null
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
      let { approvalFlowId, approvalStatus } = record
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    primaryFields() {
      return [
        'title',
        'longDescription',
        'announcementattachments',
        'announcementsharing',
        'audience',
      ]
    },
    imageAttachments() {
      let files = []
      for (let attachment of this.attachments) {
        if (attachment.contentType.includes('image')) {
          files.push(attachment)
        }
      }
      return files
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
    listRouteName() {
      return 'announcementsList'
    },
    editRouteName() {
      return 'edit-announcement'
    },
  },
  watch: {
    id: {
      async handler(newVal, oldVal) {
        if (newVal && newVal !== oldVal) {
          this.loading = true
          this.loadData()
            .then(() => {
              return this.loadAttachments()
            })
            .finally(() => {
              this.loading = false
            })
        }
      },
      immediate: true,
    },
  },
  methods: {
    async init() {
      // this.isLoading = true
      let { moduleName } = this
      if (moduleName === 'announcement') {
        await this.getPeopleAnnouncement()
      }
      this.isLoading = false
    },
    async getPeopleAnnouncement() {
      this.countLoading = true
      let { id } = this
      let filtersAll = JSON.stringify({
        parentId: { operatorId: 9, value: [id.toString()] },
      })
      let filtersRead = JSON.stringify({
        parentId: { operatorId: 9, value: [id.toString()] },
        isRead: { operatorId: 15, value: ['true'] },
      })
      await Promise.all([
        this.getPeopleAnnouncementCount('all', filtersAll),
        this.getPeopleAnnouncementCount('read', filtersRead),
      ])
      this.countLoading = false
    },
    async getPeopleAnnouncementCount(type, filters) {
      let { error, data } = await API.get('/v3/modules/data/count', {
        filters,
        moduleName: 'peopleannouncement',
      })

      if (error) {
        this.$message.error(error.message || 'Error Occcured')
      } else {
        let { count } = data
        if (type === 'all') {
          this.allCount = count
        } else {
          this.readCount = count
        }
      }
    },
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
    async loadAttachments() {
      let { error, data } = await API.get('/attachment', {
        module: this.attachmentsModuleName,
        recordId: this.record.parentId ? this.record.parentId : this.record.id,
      })

      if (error) {
        this.attachments = []
      } else {
        this.attachments = data.attachments ? data.attachments : []
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
          name: this.listRouteName,
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
          name: this.editRouteName,
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
      } else if (command === 'publish') {
        this.publishAnnouncement()
      } else if (command === 'cancel') {
        this.cancelAnnouncement()
      } else if (command === 'clone') {
        this.cloneAnnouncement()
      }
    },
    async publishAnnouncement() {
      let { moduleName } = this
      let { error } = await API.updateRecord(moduleName, {
        id: this.record.id,
        data: {},
        params: {
          publish: true,
        },
      })

      if (error) {
        this.$message.error(
          error.message || 'Error occurred while publishing announcement'
        )
      } else {
        this.loadData(true)
      }
    },
    async cancelAnnouncement() {
      let { moduleName } = this
      let { error } = await API.updateRecord(moduleName, {
        id: this.record.id,
        data: {},
        params: {
          cancel: true,
        },
      })

      if (error) {
        this.$message.error(
          error.message || 'Error occurred while cancelling announcement'
        )
      } else {
        this.loadData(true)
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
    cloneAnnouncement() {
      let { id, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
            query: { clone: true },
          })
      } else {
        this.$router.push({
          path: `/app/cy/announcements/${id}/edit?clone=true`,
        })
      }
    },
    closeDialog() {
      this.showMore = false
    },
    showMoreDialog(isRead) {
      this.isRead = isRead
      this.showMore = true
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
.viewers-count {
  border: 1px solid #dae0e8;
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 44px;
  min-width: 140px;
  width: auto;
  font-size: 16px;
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
}
.count-refresh {
  border-right: 1px solid #dae0e8;
  border-top: 1px solid #dae0e8;
  border-bottom: 1px solid #dae0e8;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  width: 44px;
  margin-right: 20px;
  cursor: pointer;
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
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
