<template>
  <div class="sr-email-thread-widget">
    <div class="sr-email-thread">
      <div v-if="checkFilterState() || details.description">
        <div class="filter-by">{{ $t('common._common.filter_by') }}</div>
        <div class="flex-middle">
          <div
            class="filter-by-all filter-all mR15 d-flex"
            :class="{ emailAll }"
            @click="filterAll"
          >
            <fc-icon
              v-if="emailAll"
              group="action"
              name="doubletick"
              size="25"
              color="#fff"
              class="pL10"
            ></fc-icon>
            <fc-icon
              v-else
              group="action"
              name="doubletick"
              size="25"
              color="#5B75FF"
              class="pL10"
            ></fc-icon>

            <div class="pL5 pT5">{{ $t('jobplan.all') }}</div>
          </div>
          <div
            class="filter-by-all filter-by-thread d-flex mR15 pR10 pT10 pL10"
            :class="{ emailCommentOnly }"
            @click="filterOnlyComment"
          >
            <fc-icon
              v-if="emailCommentOnly"
              group="default"
              name="comment"
              size="20"
              color="#fff"
            ></fc-icon>
            <fc-icon
              v-else
              group="default"
              name="comment"
              size="20"
              color="#5B75FF"
            ></fc-icon>

            <div class="pL5 pR5">{{ $t('common._common.only_comment') }}</div>
          </div>
          <div
            class="filter-by-all filter-by-thread pR10 pT10 pL10"
            :class="{ emailThreadOnly }"
            @click="filterOnlyThread"
          >
            <InlineSvg
              v-if="emailThreadOnly"
              src="svgs/service-requests/Thread_outline"
              iconClass="icon icon-lg vertical-middle"
            ></InlineSvg>
            <InlineSvg
              v-else
              src="svgs/service-requests/thread-fill"
              iconClass="icon icon-lg vertical-middle"
            ></InlineSvg>
            {{ $t('common._common.only_thread') }}
          </div>
        </div>
      </div>
      <div>
        <div
          v-if="descriptionCheck() && !loading"
          class="description mT15 pointer overflow-x  d-flex flex-row"
        >
          <div class="desc-avatar-style">
            <UserAvatar
              size="md"
              :user="$getProperty(details, 'requester') || {}"
              :name="false"
            ></UserAvatar>
          </div>
          <div class="width-per">
            <div class="d-flex display-flex-between-space">
              <div class="d-flex">
                <InlineSvg
                  v-if="checkDescriptionFromEMail"
                  src="svgs/service-requests/others"
                  iconClass="icon vertical-middle fill-white show-hover icon-up icon-algin"
                ></InlineSvg>
                <InlineSvg
                  v-else
                  src="svgs/service-requests/sr-description"
                  iconClass="icon vertical-middle fill-white show-hover icon-up icon-algin"
                ></InlineSvg>
                <div class="description-name-txt">{{ requester() }}</div>
              </div>
              <div
                class="date-txt d-flex date-content-align date-txt-color f12"
              >
                {{ getDate(details.sysCreatedTime) }}
              </div>
            </div>
            <div v-if="details.description" class="content-color desc-align">
              <div
                v-html="getDetails"
                class="desc-cursor pm-summary-tab-subject pT10 space-preline"
              ></div>
            </div>
          </div>
        </div>
        <div v-if="attachmentForDescription()" class="mL50">
          <AttachmentsFragment
            :attachmentsList="
              getExternalAttachments(
                $getProperty(details, 'emailToModuleDataRecord.attachmentsList')
              )
            "
          />
        </div>
        <div v-if="loading" class="d-flex flex-middle">
          <spinner :show="true" :size="80" class="mT50"></spinner>
        </div>
        <div v-if="checkFilterState() && !loading" class="margin-align mT10">
          <div
            v-if="hiddenConversationCount > 0"
            class="thread-expander expander-margin"
          >
            <div @click="expandAllConversation()" class="expander-bubble">
              <spinner v-if="fetchAllLoader" :show="true" :size="20"></spinner>
              <span v-else class="msg-count">{{
                hiddenConversationCount
              }}</span>
              <InlineSvg
                v-if="!fetchAllLoader"
                src="svgs/expand-arrow"
                iconClass="icon icon-md vertical-middle fill-white show-hover"
              ></InlineSvg>
            </div>
          </div>
          <div
            v-for="(mailContent, index) in emailThreads"
            :key="`mail-${index}`"
            class="mail-content pL40"
            @mouseenter.stop="replyMoreToggleFunction(index)"
            @mouseleave="replyMoreToggleFunction(index)"
            @click.stop="toggleContent(mailContent)"
            :class="[
              mailContent.messageType === 3 && 'notes',
              mailContent.messageType === 2 && 'publicNotes',
              mailContent.messageType === 1 && 'reply',
              mailContent.hideContent && 'pointer',
            ]"
          >
            <div class="display-flex-between-space pointer">
              <div class="d-flex flex-row">
                <div class="change-margin">
                  <UserAvatar
                    size="md"
                    :user="
                      $getProperty(mailContent, 'fromUser') ||
                        $getProperty(mailContent, 'fromPeople') ||
                        {}
                    "
                    :name="false"
                  ></UserAvatar>
                </div>
                <div
                  v-if="mailContent.messageType === messageTypeEnum.reply"
                  class="replyBtnIcon"
                >
                  <div v-if="mailContent.fromType === messageTypeEnum.client">
                    <InlineSvg
                      src="svgs/service-requests/others"
                      iconClass="icon icon-sm15 vertical-middle fill-white show-hover icon-up"
                    ></InlineSvg>
                  </div>
                  <div v-else>
                    <InlineSvg
                      src="svgs/service-requests/ic-reply"
                      iconClass="icon icon-sm15 vertical-middle fill-white show-hover icon-up"
                    ></InlineSvg>
                  </div>
                </div>
                <div
                  v-if="
                    mailContent.messageType === messageTypeEnum.publicNotes ||
                      mailContent.messageType === messageTypeEnum.privateNotes
                  "
                  class="replyBtnIcon comment-icon-align"
                >
                  <InlineSvg
                    src="svgs/service-requests/comment-ic"
                    iconClass="icon icon-s vertical-middle fill-white show-hover shape"
                  ></InlineSvg>
                </div>
                <div class="mL5 requester-name-txt">
                  {{
                    mailContent.from
                      ? $helpers.getNameAndEMail(mailContent.from).name
                      : ''
                  }}
                  <div
                    v-if="mailContent.to || mailContent.cc || mailContent.bcc"
                    class="mail-content-to f14"
                  >
                    {{ getMailList(mailContent) }}
                    <el-popover
                      placement="bottom-left"
                      width="auto"
                      trigger="hover"
                      class="popover-element"
                    >
                      <div slot="reference" class="more">
                        {{ getCount(mailContent) }}
                      </div>
                      <div>
                        <div
                          v-if="
                            mailContent.from &&
                              mailContent.messageType === messageTypeEnum.reply
                          "
                          class="flex-container"
                        >
                          <div class="popover-msg">
                            {{ $t('asset.performance.from') }}
                          </div>
                          <div class="popover-value">
                            <div
                              v-for="(from,
                              index) in getDisplayNameAndEmailFromEMail(
                                mailContent.from
                              )"
                              :key="index"
                              class="display"
                            >
                              <div class="name">{{ from.name }}</div>
                              <div class="email">{{ from.email }}</div>
                            </div>
                          </div>
                        </div>
                        <div v-if="mailContent.to" class="flex-container">
                          <div
                            v-if="
                              mailContent.messageType === messageTypeEnum.reply
                            "
                            class="popover-msg"
                          >
                            {{ $t('asset.performance.to') }}
                          </div>
                          <div v-else class="popover-msg">
                            {{ $t('asset.performance.notify_to') }}
                          </div>
                          <div class="popover-value">
                            <div
                              v-for="(from,
                              index) in getDisplayNameAndEmailFromEMail(
                                mailContent.to
                              )"
                              :key="index"
                              class="display"
                            >
                              <div class="name">{{ from.name }}</div>
                              <div class="email">{{ from.email }}</div>
                            </div>
                          </div>
                        </div>
                        <div v-if="mailContent.bcc" class="flex-container">
                          <div class="popover-msg">
                            {{ $t('asset.performance.bcc') }}
                          </div>
                          <div class="popover-value">
                            <div
                              v-for="(from,
                              index) in getDisplayNameAndEmailFromEMail(
                                mailContent.bcc
                              )"
                              :key="index"
                              class="display"
                            >
                              <div class="name">{{ from.name }}</div>
                              <div class="email">{{ from.email }}</div>
                            </div>
                          </div>
                        </div>
                        <div v-if="mailContent.cc" class="flex-container">
                          <div class="popover-msg">
                            {{ $t('asset.performance.cc') }}
                          </div>
                          <div class="popover-value">
                            <div
                              v-for="(from,
                              index) in getDisplayNameAndEmailFromEMail(
                                mailContent.cc
                              )"
                              :key="index"
                              class="display"
                            >
                              <div class="name">{{ from.name }}</div>
                              <div class="email">{{ from.email }}</div>
                            </div>
                          </div>
                        </div>
                        <div
                          v-if="mailContent.sysCreatedTime"
                          class="flex-container"
                        >
                          <div class="popover-msg">
                            {{ $t('quotation.common.date') }}
                          </div>
                          <div class="popover-value name">
                            <span>{{
                              getDate(mailContent.sysCreatedTime)
                            }}</span>
                          </div>
                        </div>
                      </div>
                    </el-popover>
                  </div>
                </div>
              </div>
              <div class="date-txt d-flex date-txt-color">
                <span>{{ getDate(mailContent.sysCreatedTime) }}</span>
                <div
                  v-if="condition(mailContent, index)"
                  class="d-flex reply-more-toggle"
                >
                  <div @click.stop="getIndexForShowMail(index, 0)">
                    <el-tooltip
                      effect="dark"
                      :content="'Reply'"
                      placement="top"
                    >
                      <InlineSvg
                        src="svgs/service-requests/reply"
                        iconClass="icon icon-md vertical-middle icon-height-width"
                      ></InlineSvg>
                    </el-tooltip>
                  </div>
                  <div @click.stop="getIndexForShowMail(index, 1)">
                    <el-tooltip
                      effect="dark"
                      :content="'Reply All'"
                      placement="top"
                    >
                      <InlineSvg
                        src="svgs/service-requests/sr-reply-all"
                        iconClass="icon icon-md vertical-middle icon-height-width fill-lite-grey"
                      ></InlineSvg>
                    </el-tooltip>
                  </div>
                  <!-- <div >
                    <el-popover
                    placement="bottom-left"
                      width="auto"
                      trigger="hover"
                      class="popover-element"
                  >
                    <div slot="reference" class="more">
                      <el-tooltip
                        effect="dark"
                        :content="'More'"
                        placement="top"
                      >
                        <InlineSvg
                          src="svgs/service-requests/ic-more-vert"
                          iconClass="icon icon-md vertical-middle"
                        ></InlineSvg>
                      </el-tooltip>
                    </div>
                    <div @click.stop="getIndexForShowMail(index, 1)">
                      <el-tooltip
                        effect="dark"
                        :content="'Reply All'"
                        placement="top"
                      >
                        <InlineSvg
                          src="svgs/service-requests/sr-reply-all"
                          iconClass="icon icon-md"
                        ></InlineSvg>
                      </el-tooltip>
                    </div>
                  </el-popover>
                </div> -->
                </div>
              </div>
            </div>
            <div class="mL2 mail-body border-bottom-mailer">
              <div
                class="content-align content-color"
                :class="[
                  mailContent.hideContent && 'hide-content',
                  'overflow-x',
                ]"
              >
                <FHtml :content="htmlRenderContent(mailContent)" />
              </div>
              <div
                v-if="
                  !$validation.isEmpty(
                    getExternalAttachments(mailContent.attachmentsList)
                  )
                "
                class="mT5"
              >
                <AttachmentsFragment
                  :attachmentsList="
                    getExternalAttachments(mailContent.attachmentsList)
                  "
                />
              </div>
              <div
                v-if="
                  !mailContent.hideContent &&
                    mailContent.containsQuote &&
                    !mailContent.showBlockQuote
                "
                class="flex-row mT20"
                :class="{ reduceSize }"
              >
                <button
                  @click="toggleBlockQuote(mailContent)"
                  :disabled="mailContent.blockQuoteLoading"
                  class="facilio-bq"
                ></button>
                <spinner
                  v-if="mailContent.blockQuoteLoading"
                  :show="true"
                  :size="25"
                  class="initial mL10"
                ></spinner>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!loading && emptyState" class="empty-state">
          <InlineSvg
            v-if="emailAll"
            src="svgs/service-requests/empty-state"
            iconClass="icon icon-lg vertical-middle icon-height-width"
          ></InlineSvg>
        </div>
        <div
          v-if="checkEmptyState() && emptyStateForFilter"
          class="empty-state"
        >
          <InlineSvg
            src="svgs/service-requests/comment-no-found"
            iconClass="icon icon-lg vertical-middle icon-height-width"
          ></InlineSvg>
        </div>
        <div v-if="checkEmptyState() && emptyStateForFilter">
          <div v-if="emailCommentOnly" class="not-found">
            {{ $t('common._common.no_comment_found') }}
          </div>
          <div v-else-if="emailThreadOnly" class="not-found">
            {{ $t('common._common.no_thread_found') }}
          </div>
          <div v-else class="not-found">
            {{ $t('common._common.no_conversation_found') }}
          </div>
        </div>
        <div v-if="!loading" :class="{ emptyState }">
          <div
            v-if="!mailerVisibility"
            class="button-groups"
            :class="{ buttonStyle }"
          >
            <div class="btn-top-align">
              <el-button
                v-if="!$helpers.isPortalUser() && (emailAll || emailThreadOnly)"
                @click="showMailer(1)"
                class="email-btn mL10 comment-btn reply-btn"
                :class="{ replyBtn }"
              >
                <div class="d-flex align-center" @mouseover="commentBtnColor()">
                  <InlineSvg
                    src="svgs/service-requests/sr-reply-all-white"
                    iconClass="icon icon-sm vertical-middle svg-reply-all-white"
                  ></InlineSvg>
                  <span class="mL5">{{ $t('common._common.reply_all') }}</span>
                </div>
              </el-button>
            </div>
            <el-button
              v-if="emailAll || emailCommentOnly"
              @click="showMailer(3)"
              class="mL10 email-btn comment-btn commentBtn"
              :class="{ commentOnly }"
              ><div class="d-flex align-center" @mouseover="commentBtnColor()">
                <i class="el-icon-chat-square email-icon"></i>
                {{ $t('common._common.comment') }}
              </div></el-button
            >
          </div>
          <Mailer
            v-if="mailerVisibility"
            :visibility.sync="mailerVisibility"
            :messageType="messageType"
            :details="details"
            :previousMail="getLastThread"
            @close="closeMailer()"
            @saved="record => saved(record)"
          ></Mailer>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import Mailer from '../components/ServiceRequestMailer'
import planer from 'planer'
import AttachmentsFragment from '../components/MailAttachmentsFragment'
import { sanitize } from '@facilio/utils/sanitize'
import { FHtml } from '@facilio/ui/app'
import { getUnRelatedModuleList } from 'src/util/relatedFieldUtil.js'

export default {
  props: ['details'],
  components: {
    UserAvatar,
    Mailer,
    AttachmentsFragment,
    FHtml,
  },
  data() {
    return {
      emailAll: true,
      visible: false,
      emailCommentOnly: false,
      emailThreadOnly: false,
      replyBtn: true,
      commentBtn: false,
      emailThreads: [],
      mailerVisibility: false,
      messageType: 1,
      loading: true,
      commentOnly: false,
      reduceSize: false,
      loadData: false,
      oneTimeLoaderForAll: true,
      oneTimeLoaderForComment: false,
      oneTimeLoaderForThread: false,
      typeVsIcon: {
        1: 'email',
        2: 'public',
        3: 'locked',
      },
      totalCount: null,
      fetchAllLoader: false,
      messageTypeEnum: {
        reply: 1,
        publicNotes: 2,
        privateNotes: 3,
        client: 1,
        admin: 2,
      },
      canDisableFilter: false,
      emptyState: true,
      buttonStyle: false,
      pageCount: 1,
      emptyStateForFilter: true,
      showDescription: false,
      descriptionTextContent: '',
      replyMoreToggle: -1,
      getIndex: -1,
      getSelectedIcon: '',
      imageList: [],
    }
  },
  created() {
    if (!isEmpty(this.details?.emailToModuleDataRecord)) {
      this.replaceAllImgSrcWithPreviewUrl(this.details?.emailToModuleDataRecord)
    }
    let doc = new Document()
    let desc = this.$getProperty(this, 'details.description', '')
    let description = this.$getProperty(
      this.details,
      'emailToModuleDataRecord.htmlContent',
      desc
    )
    let details = sanitize(description)
    this.descriptionTextContent = planer.extractFrom(details, 'text/html', doc)
    this.fetchAllEmailThreads()
  },
  computed: {
    getLastThread() {
      if (this.getIndex != -1) {
        if (this.getSelectedIcon == 'reply') {
          let emailObj = this.$getProperty(
            this.emailThreads,
            `${this.getIndex}`,
            null
          )
          delete emailObj.bcc
          delete emailObj.cc
          return emailObj || this.details?.emailToModuleDataRecord || {}
        } else {
          let emailObj1 = this.$getProperty(
            this.emailThreads,
            `${this.getIndex}`,
            null
          )
          delete emailObj1.bcc
          return emailObj1 || this.details?.emailToModuleDataRecord || {}
        }
      }
      return (
        [...(this.emailThreads || [])]
          .reverse()
          .find(thread => thread.messageType === 1) ||
        this.details?.emailToModuleDataRecord ||
        {}
      )
    },
    checkDescriptionFromEMail() {
      return sanitize(
        this.$getProperty(
          this,
          'details.emailToModuleDataRecord.htmlContent',
          false
        )
      )
    },
    getDetails() {
      return sanitize(
        this.$getProperty(
          this.details,
          'emailToModuleDataRecord.htmlContent',
          false
        ) ||
          this.details.description ||
          ''
      )
    },
    hiddenConversationCount() {
      return Number(this.totalCount || 0) - (this.emailThreads || []).length
    },
  },
  methods: {
    async fetchAllEmailThreads(fetchAll = false) {
      if (
        (this.emailAll && this.oneTimeLoaderForAll) ||
        (this.emailCommentOnly && this.oneTimeLoaderForComment) ||
        (this.emailThreadOnly && this.oneTimeLoaderForThread)
      ) {
        let filters = {
          dataModuleId: {
            operatorId: 9,
            value: [String(this.details?.moduleId)],
          },
          recordId: { operatorId: 9, value: [String(this.details?.id)] },
        }
        if (this.emailThreadOnly) {
          filters.messageType = { operatorId: 9, value: ['1'] }
        }
        if (this.emailCommentOnly) {
          filters.messageType = { operatorId: 9, value: ['2', '3'] }
        }
        let params = {
          viewName: 'all',
          filters: JSON.stringify(filters),
          withCount: true,
          orderBy: 'id',
          orderType: 'desc',
          page: this.pageCount,
        }

        if (!fetchAll) {
          params.perPage = 3
        }
        if (fetchAll) {
          this.fetchAllLoader = true
          if (this.emailThreads.length < 50) {
            this.pageCount = 1
          } else {
            this.pageCount++
          }
          params.page = this.pageCount
        } else {
          this.loading = true
          this.pageCount = 1
          this.emailThreads = []
        }
        params.page = this.pageCount
        let { error, list, meta } = await getUnRelatedModuleList(
          'serviceRequest',
          'emailConversationThreading',
          params
        )
        if (error) {
          let { message } = error
          this.$message.error(
            message || 'Error occurred while fetching email threads'
          )
        } else {
          if (list) {
            list.forEach(record => {
              this.setVariablesForRecord(record)
            })
          }
          if (this.emailThreads.length < 50) {
            this.emailThreads = (list || []).reverse()
            this.pageCount = 1
          } else {
            if (this.pageCount > 1) {
              let allEmailThreads = (list || []).reverse()
              this.emailThreads.forEach(record => {
                allEmailThreads.push(record)
              })
              this.emailThreads = allEmailThreads
            }
            params.page = this.pageCount
          }
          if (this.emailThreads.length > 0) {
            this.canDisableFilter = true
            this.buttonStyle = false
          }
          if (this.emailThreads.length > 0 || this.details.description) {
            this.emptyState = false
          }

          let { emailThreads = [] } = this
          let emailThreadsLength = emailThreads.length

          let { details } = this

          let descriptionContent = this.$getProperty(
            details,
            'description',
            null
          )
          if (emailThreadsLength < 1 && !isEmpty(descriptionContent)) {
            this.showDescription = true
          } else if (emailThreadsLength === 1 && isEmpty(descriptionContent)) {
            this.emailThreads[0].hideContent = false
          } else {
            this.showDescription = false
          }

          this.totalCount = this.$getProperty(
            meta,
            'pagination.totalCount',
            null
          )
          if (
            isEmpty(this.emailThreads) &&
            this.emailAll &&
            !this.details.description
          ) {
            this.emptyState = true
            this.mailerVisibility = false
          }
          if (!isEmpty(this.emailThreads)) {
            this.buttonStyle = false
          }
          if (
            !isEmpty(this.emailThreads) &&
            (this.emailCommentOnly || this.emailThreadOnly)
          ) {
            this.emptyStateForFilter = false
          }
        }
      }
      if (this.emailAll) {
        this.oneTimeLoaderForAll = false
        this.oneTimeLoaderForComment = true
        this.oneTimeLoaderForThread = true
        if (
          !this.$getProperty(this, 'details.description') &&
          this.emptyState
        ) {
          this.buttonStyle = true
        } else {
          this.buttonStyle = false
        }
      }
      if (this.emailCommentOnly) {
        this.oneTimeLoaderForAll = true
        this.oneTimeLoaderForComment = false
        this.oneTimeLoaderForThread = true
      }
      if (this.emailThreadOnly) {
        this.oneTimeLoaderForAll = true
        this.oneTimeLoaderForComment = true
        this.oneTimeLoaderForThread = false
      }
      this.fetchAllLoader = false
      this.loading = false
    },
    condition(mailContent, index) {
      let { $getProperty, $helpers, replyMoreToggle, messageTypeEnum } =
        this || {}
      return (
        index == replyMoreToggle &&
        !$helpers.isPortalUser() &&
        ($getProperty(mailContent, 'messageType') === messageTypeEnum.reply ||
          $getProperty(mailContent, 'messageType') === messageTypeEnum.client)
      )
    },
    borderCheck() {
      let { emailAll, details, hiddenConversationCount } = this || {}
      let description = details || {}
      if (emailAll && description && hiddenConversationCount <= 0) {
        return true
      }
      return false
    },
    checkFilterState() {
      let { emptyState, emailLength, emailAll } = this || {}
      let state = !emptyState && !(emailLength() && emailAll)
      return state
    },
    checkEmptyState() {
      let { loading, emailLength, emailThreadOnly, emailCommentOnly } =
        this || {}
      let state =
        (!loading && emailLength() && emailThreadOnly) ||
        (!loading && emailLength() && emailCommentOnly)
      if (state) {
        this.buttonStyle = true
      }
      return state
    },
    emailLength() {
      return isEmpty(this.emailThreads)
    },
    getCount(mailContent) {
      let count = 0
      if (mailContent.to) {
        count += mailContent.to.split(',').length
      }
      if (mailContent.bcc) {
        count += mailContent.bcc.split(',').length
      }
      if (mailContent.cc) {
        count += mailContent.cc.split(',').length
      }
      if (count > 5) {
        return '+' + count - 5 + ' more'
      } else {
        return 'more'
      }
    },
    getIndexForShowMail(index, val) {
      if (val == 0) {
        this.getSelectedIcon = 'reply'
      } else if (val == 1) {
        this.getSelectedIcon = 'replyAll'
      }
      this.getIndex = index
      this.showMailer(1)
    },
    getMailList(mailContent) {
      let emailValues = mailContent
      let emailNameList = []
      let count = 0
      if (emailValues.to) {
        let to = emailValues.to.split(',')
        if (count < 5) {
          to.forEach(val => {
            if (count < 5) {
              emailNameList.push(this.$helpers.getNameAndEMail(val).name)
              count += 1
            }
          })
        }
      }
      if (emailValues.bcc) {
        let bcc = emailValues.bcc.split(',')
        if (count < 5) {
          bcc.forEach(val => {
            if (count < 5) {
              emailNameList.push(this.$helpers.getNameAndEMail(val).name)
              count += 1
            }
          })
        }
      }
      if (emailValues.cc) {
        let cc = emailValues.cc.split(',')
        if (count < 5) {
          cc.forEach(val => {
            if (count < 5) {
              emailNameList.push(this.$helpers.getNameAndEMail(val).name)
              count += 1
            }
          })
        }
      }
      if (emailNameList) {
        return 'To : ' + emailNameList.join(', ')
      } else {
        return ''
      }
    },
    showMailer(value) {
      this.messageType = value
      this.mailerVisibility = true
      this.emptyState = false
      this.emptyStateForFilter = false
      if (this.emailAll) {
        this.oneTimeLoaderForAll = true
      } else if (this.emailCommentOnly) {
        this.oneTimeLoaderForComment = true
      } else if (this.emailThreadOnly) {
        this.oneTimeLoaderForThread = true
      }
    },
    replyBtnColor() {
      this.commentBtn = false
    },
    commentBtnColor() {
      this.commentBtn = true
    },
    closeMailer() {
      this.emptyStateForFilter = true
      this.fetchAllEmailThreads()
      this.mailerVisibility = false
      this.getIndex = -1
      this.getSelectedIcon = ''
    },

    saved(record) {
      this.setVariablesForRecord(record)
      this.emailThreads = [...this.emailThreads, record]
      this.closeMailer()
    },
    setVariablesForRecord(record) {
      record.hideContent = true
      this.replaceAllImgSrcWithPreviewUrl(record)
      if (isEmpty(record.htmlContent)) {
        record.htmlContent = ''
      }
      if (record.messageType === 1) {
        record.blockQuoteLoading = false
        record.showBlockQuote = false
        let doc = new Document()
        let mainContent = planer.extractFrom(
          record.htmlContent,
          'text/html',
          doc
        )
        if (!isEmpty(mainContent) && mainContent != record.htmlContent) {
          record.containsQuote = true
        }
        record.mainContent = mainContent
      } else {
        record.mainContent = record.htmlContent
      }
    },
    toggleContent(mailContent) {
      mailContent.hideContent = !mailContent.hideContent
    },
    toggleDescription() {
      this.showDescription = !this.showDescription
    },
    toggleBlockQuote(record) {
      record.showBlockQuote = !record.showBlockQuote
      this.toggleContent(record)
    },
    getFormattedData(date) {
      let orgMoment = this.$helpers.getOrgMoment(date)
      return `${orgMoment.fromNow()} (${orgMoment.format(
        'ddd, MMMM Do YYYY, h:mm:ss a'
      )})`
    },
    getFormattedDay(date) {
      let orgMoment = this.$helpers.getOrgMoment(date)
      return `${orgMoment.format('D MMM, YYYY')}`
    },
    getDate(date) {
      let orgMoment = this.$helpers.getOrgMoment(date)
      return `${orgMoment.format('D MMM YYYY, h:mm a')}`
    },
    getDisplayNameFromEMail(to) {
      let list = to.split(',')
      let listOfNames = []
      list.forEach(address => {
        listOfNames.push(this.$helpers.getNameAndEMail(address).name)
      })
      return listOfNames.join(', ')
    },
    getDisplayNameAndEmailFromEMail(to) {
      let list = to.split(',')
      let listOfNames = []
      list.forEach(address => {
        listOfNames.push(this.$helpers.getNameAndEMail(address))
      })
      return listOfNames
    },
    getExternalAttachments(list) {
      return (list && list.filter(at => at.type !== 2)) || []
    },
    attachmentForDescription() {
      let {
        $validation,
        $getProperty,
        descriptionCheck,
        getExternalAttachments,
        details,
      } = this || {}

      let attachmentList = $getProperty(
        details,
        'emailToModuleDataRecord.attachmentsList'
      )
      let externalAttachment = getExternalAttachments(attachmentList)
      let hasAttachment = !$validation.isEmpty(externalAttachment)

      return hasAttachment && descriptionCheck()
    },
    replaceAllImgSrcWithPreviewUrl(record) {
      let regExVal = /src=['"]cid:([^'"]+)['"]/
      let regExExecValues = this.$helpers.execRegex(
        record.htmlContent,
        new RegExp(regExVal, 'g')
      )
      regExExecValues.forEach(value => {
        let cid = value[1]
        let attachment =
          record.attachmentsList &&
          record.attachmentsList.find(
            at => at.contentId === cid && at.type === 2
          )
        if (isEmpty(attachment)) {
          console.warn('No matching attachments found for content id:', cid)
        } else {
          record.htmlContent = record.htmlContent.replace(
            regExVal,
            `src="${this.$helpers.getImagePreviewUrl(
              attachment.fileId
            )}" data-id="${cid}"`
          )
        }
      })
    },
    // https://stackoverflow.com/questions/33215775/new-to-regex-adding-a-whitespace-after-all-closing-tags
    appendWhiteSpace(el) {
      let walk = document.createTreeWalker(el, NodeFilter.SHOW_ELEMENT, null)
      let node = walk?.nextNode()
      while (node) {
        if (node?.nodeName !== 'SREL') {
          let ws_node = document.createTextNode(' ')
          node?.parentNode.insertBefore(ws_node, node?.nextSibling)
        }
        node = walk?.nextNode()
      }
      return el?.firstChild?.textContent || el?.firstChild?.innerText || ''
    },
    addWhiteSpaceToNodes(content) {
      let doc = document.createDocumentFragment()
      let wrapper = document.createElement('srel')
      wrapper.innerHTML = this.$sanitize(content)
      doc.appendChild(wrapper)
      return !isEmpty(content) ? this.appendWhiteSpace(doc) : ''
    },
    expandAllConversation() {
      if (this.emailAll) {
        this.oneTimeLoaderForAll = true
      } else if (this.emailCommentOnly) {
        this.oneTimeLoaderForComment = true
      } else if (this.emailThreadOnly) {
        this.oneTimeLoaderForThread = true
      }
      this.fetchAllEmailThreads(true)
    },
    filterAll() {
      this.emailAll = true
      this.emailCommentOnly = false
      this.emailThreadOnly = false
      this.commentOnly = false
      this.loadData = false
      this.fetchAllEmailThreads()
      this.closeMailer()
    },
    filterOnlyComment() {
      this.emailAll = false
      this.emailCommentOnly = true
      this.emailThreadOnly = false
      this.commentOnly = true
      this.emptyStateForFilter = true
      this.loadData = false
      if (this.canDisableFilter) {
        this.fetchAllEmailThreads()
      }
      this.closeMailer()
    },
    filterOnlyThread() {
      this.emailAll = false
      this.emailCommentOnly = false
      this.emailThreadOnly = true
      this.commentOnly = false
      this.emptyStateForFilter = true
      this.loadData = false
      if (this.canDisableFilter) {
        this.fetchAllEmailThreads()
      }
      this.closeMailer()
    },
    descriptionCheck() {
      let { checkFilterState, details, emailAll } = this || {}
      return (
        (checkFilterState() || details.description) &&
        emailAll &&
        this.details.description
      )
    },
    replyMoreToggleFunction(key) {
      if (this.replyMoreToggle == -1) {
        this.replyMoreToggle = key
      } else {
        this.replyMoreToggle = -1
      }
    },
    requester() {
      let requesterName = this.$getProperty(this, 'details.requester.name')
      let requesterEmail = this.$getProperty(this, 'details.requester.email')
      return requesterName || requesterEmail
    },
    htmlRenderContent(mailContent) {
      let { $sanitize } = this || {}
      let { mainContent, htmlContent, showBlockQuote } = mailContent || {}

      let blockQuote = showBlockQuote
        ? $sanitize(htmlContent)
        : $sanitize(mainContent)

      return blockQuote
    },
  },
}
</script>
<style lang="scss">
.sr-email-thread {
  border-radius: 10px;
  font-size: 14px;
  .email-btn {
    width: 111px;
    height: 32px;
    text-align: center;
    margin: 0 0 0 8px;
    padding-bottom: 28px;
    border-radius: 4px;
    border: solid 1px #acdcdf;
    background-color: #fff;
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.5px;
    color: #3ab2c1;
    .email-icon {
      width: 16px;
      height: 16px;
      margin: 5px 4px 2px 0;
      object-fit: contain;
      color: #3ab2c1;
      font-weight: bolder;
    }
  }
  .replyBtn {
    background-color: #3ab2c1;
    color: #fff;
    .email-icon {
      color: #fff;
    }
  }
  .replyBtn:hover {
    background-color: #3fccdf;
    color: #fff;
  }
  .commentBtn:hover {
    background-color: #3fccdf;
    color: #fff;
    .email-icon {
      color: #fff;
    }
  }
  .commentOnly {
    background-color: #3ab2c1;
    color: #fff;
    .email-icon {
      color: #fff;
    }
  }
  .commentOnly:hover {
    background-color: #3fccdf;
    color: #fff;
    .email-icon {
      color: #fff;
    }
  }
  .filter-by {
    background-color: #fff;
    font-weight: 500;
    font-size: 12px !important;
    letter-spacing: 0.5px;
    color: #191a45;
    margin-bottom: 4px;
    margin-top: -10px;
  }
  .filter-by-all {
    font-size: 14px !important;
    padding: 7px;
    border-radius: 18px;
    text-align: center;
    margin: 4px 24px 0 0;
    font-weight: 500 !important;
    i {
      color: #5b75ff;
      margin-right: 2px;
      text-align: center;
    }
  }
  .filter-by-all:hover {
    cursor: pointer;
  }
  .filter-all {
    width: 100px;
  }
  .filter-all:hover {
    background-color: #eef1ff;
    color: #191a45;
    i {
      color: #5b75ff;
    }
  }
  .filter-by-comment:hover {
    background-color: #eef1ff;
    color: #191a45;
    i {
      color: #5b75ff;
    }
  }
  .filter-by-thread:hover {
    background-color: #eef1ff;
    color: #191a45;
    i {
      color: #5b75ff;
    }
  }
  .requester {
    padding: 10px;
    margin: -10px 4px 0 -10px;
    font-size: 14px !important;
    font-weight: 600;
    letter-spacing: 0.5px;
    color: #191a45;
  }
  .description {
    font-size: 14px;
    font-weight: normal;
    color: #191a45;
    padding: 10px 20px 13px 15px;
    text-align: justify;
    text-justify: inter-word;
    line-height: 18px;
    width: auto !important;
    width: 100vw;
    position: relative;
    margin-left: -20px;
    margin-right: -20px;
    word-break: break-all;
    div {
      margin-left: 3px;
      margin-top: 6px;
    }
    span {
      font-size: 14px;
      font-weight: bold;
      margin-left: 5px;
      color: #2e2e49;
    }
    .dec-margin {
      margin-left: 0.5px;
    }
    .desc-avatar-style {
      margin: -21px 0px 0px -17px;
    }
    .desc-align {
      margin-left: 4px;
      margin-top: -4px;
      .desc-cursor {
        cursor: default !important;
      }
    }
    .icon-algin {
      margin-left: -2px;
      margin-top: -25px;
    }
    .description-name-txt {
      font-weight: 500;
      color: #324056;
      margin: -6px 0px 0px 5px;
    }
    .req-content-align {
      width: fit-content;
    }
    .date-content-align {
      text-align: end;
      margin-top: -1px;
      color: #949cb3 !important;
    }
    .width-per {
      width: 100%;
    }
  }
  .not-found {
    font-size: 21px;
    letter-spacing: 0.95px;
    text-align: center;
    color: #191a45;
  }
  .border-bottom {
    border-bottom: solid 1px #f2f4fa;
    margin-top: 10px;
    margin-bottom: 8px;
    margin-left: -20px;
    margin-right: -20px;
  }
  .date {
    letter-spacing: 0.5px;
    margin-left: 5px;
    font-size: 14px;
    color: #b8b8b8;
    font-weight: 600;
  }
  .border-bot {
    border-bottom: solid 2px #f2f4fa;
    margin: 6px -20px -10px -20px;
  }
  .emailAll {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff;
    }
  }
  .emailAll:hover {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff;
    }
  }
  .emailCommentOnly {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff;
    }
  }
  .emailCommentOnly:hover {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff;
    }
  }
  .emailThreadOnly {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff;
    }
  }
  .emailThreadOnly:hover {
    background-color: #5b75ff;
    color: #fff;
    i {
      color: #fff !important;
    }
  }
  .sr-property {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #2e2e49;
    margin-top: -10px;
    display: flex;
    flex-direction: row;
    .property-txt {
      font-weight: bold;
    }
    div:nth-of-type(1) {
      flex-grow: 3;
    }
    div:nth-of-type(2) {
      flex-grow: 2;
    }
    .view-more-txt {
      text-align: end;
      margin-left: -30px;
    }
    .ml {
      margin-left: -20px;
    }
  }
  .content-align {
    margin-top: -10px;
    margin-left: -10px;
  }
  .date-txt-color {
    color: #65686f;
    font-size: 14px;
    font-weight: 500;
    .reply-more-toggle {
      div {
        margin: -2px 0px 0 7px;
      }
    }
  }
  .content {
    margin-left: -20px;
    margin-right: -20px;
    padding-left: 40px !important;
  }
  .replyBtnIcon {
    color: #5b75ff;
    padding-left: 10px;
  }
  .mail-content {
    font-size: large;
  }
  .mail-content-to {
    color: #686b70;
    margin: 10px 0px 10px -20px;
    font-weight: normal;
  }
  .popover-element {
    .more {
      cursor: pointer;
      color: #0053cc;
      float: right;
      margin-left: 10px;
      border: none;
    }
    .more:hover {
      color: #0761ce;
    }
  }
  .border {
    border-radius: 12px;
  }
  .flex-container {
    display: flex;
    flex-direction: row;
    margin-bottom: 12px;
  }
  .popover-msg {
    width: 58px;
    margin: 0 5px 0 0;
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #686b70;
  }
  .popover-value {
    margin: 0 2px 0 0px;
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #353538;
  }
  .display {
    display: flex;
  }
  .name {
    margin-right: 15px;
    margin-left: 5px;
  }
  .email {
    letter-spacing: 0.5px;
    color: #686b70;
  }
  .reply-btn {
    .email-icon {
      margin-left: 10px;
    }
    span {
      margin-top: 2px;
    }
  }
  .margin-align {
    margin-left: -20px;
    margin-right: -20px;
  }
  .svg-reply {
    margin-left: 10px;
    margin-right: 5px;
  }
  .shape {
    height: 16px;
    width: 16px;
    margin-top: -3px;
  }
  .btn-top-align {
    float: left;
  }
  .empty-state {
    display: flex;
    justify-content: center;
    .icon-height-width {
      height: 250px !important;
      width: 300px !important;
    }
  }
  .emptyState {
    display: flex;
    justify-content: center;
  }
  .change-margin {
    margin-left: -20px;
  }
  .icon-up {
    margin-top: -8px;
  }
  .buttonStyle {
    display: flex;
    justify-content: center;
  }
  .show-description {
    max-height: 88px !important;
    overflow: hidden;
    word-break: break-all;
    margin-left: -10px;
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .expander-margin {
    margin-bottom: -16px;
  }
  .content-color {
    color: #686b70;
  }
  .comment-icon-align {
    margin-top: -2px;
  }
  .requester-align {
    margin: 8px 0px -10px 0px;
  }
}
</style>
