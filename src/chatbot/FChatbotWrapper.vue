<template>
  <f-chatbot
    @messageEntered="handleUserMessage"
    @attached="handleUserMessage($event, true)"
    @loadMessages="loadMessages"
    ref="chatbot"
    :suggestions="suggestions"
  >
  </f-chatbot>
</template>

<script>
import FChatbot from './FChatbot'
import { mapActions, mapState } from 'vuex'
// import http from './botHttp'
export default {
  name: 'chat-bot-wrapper',
  components: {
    FChatbot,
  },
  data() {
    return {
      messageHistory: [],
      loadingMessages: false,
      suggestions: null,
      isSocketRegistered: false,
    }
  },
  computed: {
    ...mapState({
      socketIsConnected: state => state.socket.isConnected,
    }),
  },
  watch: {
    socketIsConnected(isConnected) {
      if (this.isSocketRegistered && isConnected) {
        this.$refs['chatbot'].reInitBot()
      }
    },
  },
  methods: {
    ...mapActions({
      subscribeToChatBotChannel: 'chatbot/subscribeToChatBotChannel',
    }),
    async handleUserMessage(message, isAttachment) {
      try {
        let resp = null

        if (isAttachment) {
          this.awaitingResp = true
          resp = await this.$http.post('v2/cb/chat', message)
          this.awaitingResp = false
        } else {
          this.awaitingResp = true
          resp = await this.$http.post('v2/cb/chat', { chatMessage: message })
          this.awaitingResp = false
        }
        let serverMessage = this.processBotResponse(resp.data.result)

        this.$refs['chatbot'].messageRecieved(serverMessage)
      } catch (e) {
        this.$message.warning('Error occured getting bot response')
      }
    },

    async loadMessages(event) {
      // this.loadingMessages = true
      this.messageHistory = []
      let resp = await this.getChatHistory(
        event.isFullReload,
        event.lastStartTime
      )

      this.messageHistory = this.processChatHistory(
        resp.data.result.chatBotSessions,
        event.isFullReload
      )

      this.$refs['chatbot'].historyLoaded(
        this.messageHistory,
        event.isFullReload
      )
      //just now bot has been opened ,setup websocket listener
      if (!this.isSocketRegistered && event.isInitialOpen) {
        this.isSocketRegistered = true
        this.subscribeToChatBotChannel(this.botSocketMessage)
      }
    },

    botSocketMessage(socketMessage) {
      if (this.awaitingResp) {
        return
      }

      if (socketMessage.messageType == 'USER_MESSAGE') {
        this.$refs['chatbot'].pushUserMessageToList(socketMessage.message, true)
      } else if (socketMessage.messageType == 'BOT_MESSAGE') {
        let serverMessage = this.processBotResponse(socketMessage.message)
        this.$refs['chatbot'].messageRecieved(serverMessage, true)
      }
    },

    async getChatHistory(isFullReload, lastStartTime) {
      let paginateJSON = {
        page: 1,
        perPage: 5,
        orderType: 'asc',
        orderBy: 'startTime',
      }
      if (!isFullReload) {
        paginateJSON.startTime = lastStartTime
      }

      return this.$http.post('/v2/cb/getChatMessages', paginateJSON)
    },

    processChatHistory(chatBotSessions, isFullReload) {
      let messageList = []
      chatBotSessions.reverse()
      chatBotSessions.forEach(chatBotSession => {
        messageList.push({
          chatBotResponse: chatBotSession.queryJson,
          isUserMessage: true,
          timeStamp: chatBotSession.startTime,
        }) //user message
        if (!chatBotSession.chatBotSessionConversations) {
          //no convo,so resp recieved in session itself ,bot response MUST exist at this point
          let responses = this.parseBotRespJsonFromHistory(
            chatBotSession.response,
            chatBotSession.startTime
          )

          //add endtime here instead
          messageList.push(...responses)
          if (isFullReload) {
            this.suggestions = chatBotSession.suggestion
              ? JSON.parse(chatBotSession.suggestion)
              : null
          }
        } else {
          //this session spawned a conversation , parse it to load message list
          chatBotSession.chatBotSessionConversations.forEach(
            chatBotSessionConversation => {
              let queries = this.parseBotRespJsonFromHistory(
                chatBotSessionConversation.query,
                chatBotSessionConversation.requestedTime
              )

              messageList.push(...queries)
              let userResponse = chatBotSessionConversation.responseJson
              if (userResponse) {
                //user can replied to conversation
                messageList.push({
                  chatBotResponse: userResponse,
                  isUserMessage: true,
                  timeStamp: chatBotSessionConversation.respondedTime,
                  attachment: chatBotSessionConversation.attachment,
                })
              } //awaiting user reply
              if (isFullReload) {
                this.suggestions = chatBotSessionConversation.suggestion
                  ? JSON.parse(chatBotSessionConversation.suggestion)
                  : null
              }
            }
          )
          if (chatBotSession.response) {
            if (isFullReload) {
              this.suggestions = chatBotSession.suggestion
                ? JSON.parse(chatBotSession.suggestion)
                : null
            }
            let responses = this.parseBotRespJsonFromHistory(
              chatBotSession.response,
              chatBotSession.endTime
            )

            messageList.push(...responses)
          } //else awaiting user reply
        }
      })

      return messageList
    },
    // Called for bot responses in history
    parseBotRespJsonFromHistory(query, timeStamp) {
      // TO DO , need to change the Datastructure for this  , when multiple messages recieved in a single response they need to be clubbed together and only one timestamp must be shown
      let botQuery = null
      try {
        botQuery = JSON.parse(query)
        botQuery.forEach(query => {
          query.timeStamp = timeStamp
        })
      } catch (e) {
        // eslint-disable no-empty
      }
      return botQuery
    },

    //called for botresponse in current request
    processBotResponse(result) {
      let messages = JSON.parse(result.chatBotMessageJson)
      messages.forEach(message => {
        message.timeStamp = this.$helpers.getOrgMoment()
        message.uniqueID = result.uniqueID
      })
      this.suggestions = result.chatBotSuggestions
      return {
        messages: messages,
        uniqueID: result.uniqueID,
        chatbotAttachment: result.chatbotAttachment,
        // previousChatBotSessionConversation:
        // result.previousChatBotSessionConversation,
      }
    },
  },
}
</script>

<style></style>
