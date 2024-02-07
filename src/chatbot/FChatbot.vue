<template>
  <div class="f-chat-bot-container">
    <transition name="fc-slide-up">
      <div v-show="isOpen" class="f-chat-bot-full">
        <div class="header">
          <inline-svg
            v-if="showBotBack"
            @click.native="botBackClick"
            class="bot-back pointer"
            src="svgs/chatbot/back_icon"
            iconClass="icon icon-md"
          ></inline-svg>
          <inline-svg
            src="svgs/chatbot/chatbot_icon"
            iconClass="icon icon-xxl"
          ></inline-svg>
          <p class="bot-title">
            Facilio bot
          </p>
          <inline-svg
            @click.native="minimize"
            class="bot-close pointer"
            src="svgs/chatbot/close_icon"
            iconClass="icon icon-sm"
          ></inline-svg>
        </div>

        <spinner
          v-if="loadingMessages"
          :show="loadingMessages"
          :size="80"
        ></spinner>

        <div class="body" v-else>
          <div class="message-scroll-container" ref="scrollContainer">
            <div class="observer-node" ref="observerNode"></div>
            <spinner
              v-if="loadingMore"
              :show="loadingMore"
              :size="50"
            ></spinner>
            <div
              class="message-container"
              :class="[{ 'suggestions-open': suggestions }]"
            >
              <div
                v-for="(message, index) in messages"
                :key="index"
                :class="[
                  'message-wrapper',
                  message.isUserMessage ? 'user-message' : 'bot-message',
                  {
                    'carousel-message':
                      message.chatBotResponseType == 6 &&
                      Array.isArray(message.chatBotConfirmationResponse.fields),
                  },
                ]"
              >
                <bot-attachment
                  v-if="message.attachment"
                  :attachment="message.attachment"
                >
                </bot-attachment>
                <!-- single confirmation card -->
                <bot-confirmation
                  v-else-if="
                    message.chatBotResponseType == 6 &&
                      !Array.isArray(message.chatBotConfirmationResponse.fields)
                  "
                  :botMessage="message"
                >
                </bot-confirmation>
                <!-- Status card -->
                <bot-confirmation
                  v-else-if="message.chatBotResponseType == 11"
                  :botMessage="message"
                >
                </bot-confirmation>
                <!-- multiple confirmation card -->
                <bot-card-carousel
                  v-else-if="
                    message.chatBotResponseType == 6 &&
                      Array.isArray(message.chatBotConfirmationResponse.fields)
                  "
                  :botMessage="{ chatBotConfirmationResponse: message }"
                >
                </bot-card-carousel>
                <bot-list
                  v-else-if="message.chatBotResponseType == 10"
                  :botMessage="message"
                >
                </bot-list>
                <div v-else ref="message" :class="['text-message']">
                  <vue-markdown
                    class="vue-markdown"
                    :linkify="false"
                    :source="
                      message.isUserMessage
                        ? message.chatBotResponse.label
                        : message.chatBotResponse
                    "
                  >
                  </vue-markdown>
                </div>

                <div class="timestamp">
                  {{ message.timeStamp | botTime }}
                </div>
              </div>
              <!-- Message end here , based on last bot question type , the input is rendered below for user to pick -->
              <bot-button-input
                v-if="lastBotMessage.chatBotResponseType == 4 && !awaitingReply"
                :options="lastBotMessage.chatBotResponseOptions"
                @optionSelected="optionSelected"
              >
              </bot-button-input>
              <bot-lookup-input
                :botMessage="lastBotMessage"
                :isExpanded.sync="isLookupExpanded"
                v-if="lastBotMessage.chatBotResponseType == 7 && !awaitingReply"
                @messageReady="renderFinished"
                @recordSelected="lookupSelected"
              >
              </bot-lookup-input>
              <bot-date-time-slot
                :dateSlots="lastBotMessage.slots"
                v-if="lastBotMessage.chatBotParamType == 6 && !awaitingReply"
                @change="handleDateTime"
              >
              </bot-date-time-slot>
              <bot-intent-card
                v-if="
                  lastBotMessage.chatBotResponseType == 8 &&
                    !awaitingReply &&
                    suggestions
                "
                :suggestions="suggestions"
                :isExpanded.sync="isIntentCardExpanded"
                @intentSelected="intentSelected"
              >
              </bot-intent-card>

              <bot-is-typing
                v-if="awaitingReply || awaitingRender"
                ref="botIsTyping"
              >
              </bot-is-typing>
            </div>
          </div>
          <div class="bot-input-container">
            <bot-suggestions
              v-if="!awaitingReply && lastBotMessage.chatBotResponseType != 8"
              :suggestions="suggestions"
              @suggestion="handleSuggestion"
            >
            </bot-suggestions>

            <bot-file-input
              v-if="lastBotMessage.chatBotParamType == 9"
              :disabled="awaitingReply"
              @attached="handleAttachment"
            >
            </bot-file-input>
            <!-- <bot-date-time-picker
              v-else-if="lastBotMessage.chatBotParamType == 6"
              :disabled="awaitingReply"
              @change="handleDateTime"
            >
            </bot-date-time-picker> -->

            <el-input
              v-else
              ref="userInput"
              :disabled="!isUserInputEnabled"
              resize="none"
              class="fc-input-txt chat-bot-textarea"
              type="textarea"
              :placeholder="inputPlaceHolderText"
              v-model.lazy="userInput"
              @keyup.native.enter="userTextEntered"
            >
            </el-input>
          </div>
        </div>
      </div>
    </transition>

    <transition name="fc-scale-in-center" appear>
      <div @click="maximize" v-if="!isOpen" class="chat-bot-collapsed pointer">
        <inline-svg
          @click.native="minimize"
          src="svgs/chatbot/chatbot_icon"
          iconClass="icon icon-xlg"
        ></inline-svg>
      </div>
    </transition>
  </div>
</template>

<script>
import './style/chatbot.scss'
import './style/botEffects.scss'
import botTime from './BotTimeStamp'
import getProperty from 'dlv'
import VueMarkdown from 'vue-markdown'

import BotIsTyping from './BotIsTyping'
import BotButtonInput from './BotButtonInput'
import BotLookupInput from './BotLookupInput'
import BotSuggestions from './BotSuggestions'
import BotConfirmation from './BotConfirmation'
import BotCardCarousel from './BotCardCarousel'
import BotFileInput from './BotFileInput'
import BotIntentCard from './BotIntentCard'
import BotAttachment from './BotAttachment'
import BotDateTimeSlot from './BotDateTimeSlot'
import BotList from './BotList'

export default {
  name: 'chat-bot',
  components: {
    BotFileInput,
    VueMarkdown,
    BotIsTyping,
    BotButtonInput,
    BotLookupInput,
    BotSuggestions,
    BotConfirmation,
    BotCardCarousel,
    BotIntentCard,
    BotAttachment,
    BotList,
    BotDateTimeSlot,
  },
  props: ['suggestions'],
  filters: { botTime },
  data() {
    return {
      loadingMore: false,
      canLoadMore: true,
      loadingMessages: false,
      isOpen: false,
      isInitialOpen: true,
      userInput: '',
      awaitingReply: false,
      awaitingRender: false,
      messages: [],
      // suggestions: null,
      selectedOption: null,

      isLookupExpanded: false,
      isIntentCardExpanded: false,
    }
  },
  computed: {
    showBotBack() {
      return this.isLookupExpanded || this.isIntentCardExpanded
    },

    lastBotMessage() {
      let lastBotMessage = this.messages
        .slice()
        .reverse()
        .find(message => !message.isUserMessage)
      if (!lastBotMessage) {
        return { chatBotResponseType: 1 }
      } else {
        return lastBotMessage
      }
    },
    inputPlaceHolderText() {
      let message = ''
      if (this.messages.length === 0) {
        message = 'Start a conversation'
      } else if (
        this.lastBotMessage.chatBotResponseType === 4 ||
        this.lastBotMessage.chatBotResponseType === 7
      ) {
        message = 'Choose an option'
      } else {
        message = 'Reply to facilio bot'
      }
      return message
    },
    isUserInputEnabled() {
      return (
        (!this.awaitingReply && this.lastBotMessage.chatBotResponseType == 1) ||
        this.lastBotMessage.chatBotResponseType == 6 ||
        this.lastBotMessage.chatBotResponseType == 8 ||
        this.lastBotMessage.chatBotResponseType == 10 ||
        this.lastBotMessage.chatBotResponseType == 11
      )
    },
  },
  methods: {
    historyLoaded(messageHistory, isFullReload) {
      this.messages.unshift(...messageHistory)

      if (isFullReload) {
        //first time fetch ,on  open
        this.loadingMessages = false

        this.$nextTick(() => {
          this.scrollLastMessageToView()
          this.focusInput()
          this.$nextTick(() => {
            let scrollContainer = this.$refs['scrollContainer']
            //check if messages overflow the container ie if scroll is enabled, if not scroll no history present to load
            if (scrollContainer.scrollHeight == scrollContainer.clientHeight) {
              this.canLoadMore = false
            } else {
              this.setUpIntersectionObserver()
            }
          })
        })
      } else {
        //lazy fetch

        if (messageHistory.length == 0) {
          this.canLoadMore = false
        }
        this.loadingMore = false
        this.$nextTick(() => {
          this.restoreScrollPosition()
        })
      }
    },

    destroyed() {
      this.disconnectIntersectionObserver()
    },
    disconnectIntersectionObserver() {
      if (this.intersectionObserver) {
        this.intersectionObserver.disconnect()
        this.intersectionObserver = null
      }
    },
    setUpIntersectionObserver() {
      let options = {
        root: this.$refs['scrollContainer'],
        rootMargin: '0px 0px 0px 0px',
      }

      this.intersectionObserver = new IntersectionObserver(
        this.handleIntersection,
        options
      )
      this.intersectionObserver.observe(this.$refs['observerNode'])
    },
    handleIntersection([entry]) {
      if (entry && entry.isIntersecting) {
        if (!this.loadingMore && this.canLoadMore) {
          this.loadMore()
        }
      }
    },
    /*
    On appeding content to top dynamically current scrollTop content gets push down ,so scroll in the opposite direction to compensate for it
    new scrollPosition=newAddedContentHeight+oldScrollTop ,we can get only total height and not height of new content after render , so use
    below  instead
     */
    recordScrollPosition() {
      let node = this.$refs['scrollContainer']
      this.previousScrollHeightMinusScrollTop =
        node.scrollHeight - node.scrollTop
    },
    restoreScrollPosition() {
      let node = this.$refs['scrollContainer']
      node.scrollTop =
        node.scrollHeight - this.previousScrollHeightMinusScrollTop
    },
    loadMore() {
      this.loadingMore = true
      this.recordScrollPosition()
      this.$emit('loadMessages', {
        isFullReload: false,
        lastStartTime: this.messages[0].timeStamp,
      })
    },
    pushUserMessageToList(message, fromSocket = false) {
      if (fromSocket) {
        let isDuplicate = this.isDuplicateMessage(
          'USER_MESSAGE',
          message.uniqueID
        )
        if (isDuplicate) return
      }

      this.messages.push({
        chatBotResponse: message,
        isUserMessage: true,
        timeStamp: this.$helpers.getOrgMoment(),
      })
      this.awaitingReply = true
      this.$nextTick(() => {
        this.scrollLastMessageToView()
      })
    },
    sendUserMessage(message) {
      this.$emit('messageEntered', message)
      this.pushUserMessageToList(message)
    },
    lookupSelected(record) {
      this.isLookupExpanded = false
      this.sendUserMessage(record)
    },
    intentSelected(suggestion) {
      this.isIntentCardExpanded = false
      this.sendUserMessage({ id: null, label: suggestion.suggestion })
    },
    handleDateTime(dateTimeMessage) {
      this.sendUserMessage(dateTimeMessage)
    },
    optionSelected(option) {
      this.sendUserMessage(option)
    },
    handleSuggestion(suggestion) {
      this.userInput = ''
      this.sendUserMessage({ id: null, label: suggestion.suggestion })
    },
    handleAttachment(attachment) {
      this.awaitingReply = true
      this.$emit('attached', attachment)
    },
    focusInput() {
      if (this.isUserInputEnabled) {
        this.$refs['userInput'] && this.$refs['userInput'].focus()
      }
    },
    isDuplicateMessage(messageType, uniqueID) {
      let serverMessageHistory
      if (messageType == 'BOT_MESSAGE') {
        serverMessageHistory = this.messages.filter(e => !e.isUserMessage)
      } else if (messageType == 'USER_MESSAGE') {
        serverMessageHistory = this.messages.filter(e => e.isUserMessage)
      }

      let isDuplicate = serverMessageHistory.find(e => {
        return e.uniqueID == uniqueID
      })
      return isDuplicate
    },
    messageRecieved(serverMessage, fromSocket = false) {
      //for attachment user message(attachment image) itself can be shown only after server message recieved ,
      //check if message already present , WMS after resp case and discard

      let isDuplicate = this.isDuplicateMessage(
        'BOT_MESSAGE',
        serverMessage.uniqueID
      )
      if (isDuplicate) return

      // To do change this, as soon as user attaches file , use fileObj to generate a user message,dont wait for bot resp
      if (serverMessage.chatbotAttachment) {
        this.messages.push({
          chatBotResponse: null,
          attachment: serverMessage.chatbotAttachment,
          isUserMessage: true,
          timeStamp: this.$helpers.getOrgMoment(),
          // uniqueID:serverMessage.uniqueID
        })
      }
      //unique ID is per conv ie User/bot message pair not per message, when resp arrives , last message is user message, put resp unique ID in user message too
      let lastUserMessage = this.messages[this.messages.length - 1]
      lastUserMessage.uniqueID = serverMessage.uniqueID

      this.handleMessageEdit(serverMessage)

      this.messages.push(...serverMessage.messages)

      if (serverMessage.messages[0].chatBotResponseType == 7) {
        //need to wait for lookup component to render before removing loader
        this.awaitingRender = true
      }
      this.serverMessageLoaded()
    },
    handleMessageEdit(serverMessage) {
      let prevMessage = serverMessage.messages[0].previousValue

      if (
        prevMessage &&
        serverMessage.messages[0].chatBotResponseType == 1 &&
        serverMessage.messages[0].chatBotParamType == 1
      ) {
        this.userInput = prevMessage
      }
    },
    renderFinished() {
      this.awaitingRender = false
      this.$nextTick(() => {
        this.scrollLastMessageToView()
        this.focusInput()
      })
    },
    serverMessageLoaded() {
      this.awaitingReply = false
      this.$nextTick(() => {
        this.scrollLastMessageToView()
        this.focusInput()
      })
    },
    scrollLastMessageToView() {
      let scrollContainer = this.$refs['scrollContainer']
      scrollContainer.scrollTop = scrollContainer.scrollHeight
    },

    userTextEntered(event) {
      let message = { label: this.userInput.trim() }
      this.sendUserMessage(message)
      this.userInput = ''
    },
    botBackClick() {
      this.isLookupExpanded = false
      this.isIntentCardExpanded = false
    },

    minimize() {
      this.isOpen = false
    },
    maximize() {
      this.isOpen = true

      if (this.isInitialOpen) {
        //only on initial open socket is registered and message history loaded
        this.isInitialOpen = false
        this.loadingMessages = true
        this.$emit('loadMessages', { isInitialOpen: true, isFullReload: true })
      } else {
        this.$nextTick(() => {
          // this.scrollLastMessageToView()//v -if does not remember scroll position , need to use v show
          this.focusInput()
        })
      }
    },
    reInitBot() {
      //called when socket reconnects

      this.loadingMessages = true

      this.loadingMore = false
      this.canLoadMore = true
      ;(this.userInput = ''), (this.awaitingReply = false)
      this.awaitingRender = false
      this.messages = []
      this.selectedOption = null
      this.isLookupExpanded = false
      this.isIntentCardExpanded = false
      this.disconnectIntersectionObserver()

      if (!this.isOpen) {
        //bot is closed , wait for next open to sync messages
        this.isInitialOpen = true
        return
      }
      this.$emit('loadMessages', {
        isInitialOpen: this.isInitialOpen,
        isFullReload: true,
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.observer-node {
  width: 100%;
  // margin: 0px auto;
}
.carousel-message {
  width: 100% !important;
}
</style>
