<template>
  <div class="bot-confirmation-card">
    <div class="card-area-container">
      <div class="card-id" v-if="cardId">
        <vue-markdown
          class="vue-markdown"
          :linkify="false"
          :source="cardId"
        ></vue-markdown>
      </div>

      <div class="title">{{ mainField && mainField.value }}</div>
      <div class="param-container">
        <div class="param-row" v-for="(param, index) in params" :key="index">
          <div class="param-name">{{ param.name }}</div>
          <div
            class="param-value param-assignee"
            v-if="param.name == 'assignedTo'"
          >
            <div class="assignee-logo">
              {{ param.value.name.substring(0, 1).toUpperCase() }}
            </div>
            <div class="assignee-details">
              <div>{{ param.value.name }}</div>
              <div>{{ param.value.phone || param.value.mobile }}</div>
            </div>
          </div>
          <div class="param-value" v-else>{{ param.value }}</div>
        </div>
      </div>
    </div>
    <div
      class="confirmation-message"
      v-if="botMessage.chatBotResponseType == 6"
    >
      <vue-markdown
        class="vue-markdown"
        :linkify="false"
        :source="botMessage.chatBotResponse"
      ></vue-markdown>
    </div>
  </div>
</template>

<script>
import VueMarkdown from 'vue-markdown'

export default {
  props: ['botMessage'],
  components: {
    VueMarkdown,
  },

  data() {
    return {
      params: [],
      mainField: {},
      cardId: null,
    }
  },
  created() {
    this.processParams()
  },
  methods: {
    processParams() {
      //handle for both status and confirmation card
      let cardResponse
      if (this.botMessage.chatBotResponseType == 11) {
        cardResponse = this.botMessage.chatBotResponse.summaryCard
      } else if (this.botMessage.chatBotResponseType == 6) {
        cardResponse = this.botMessage.chatBotConfirmationResponse
      }

      let { mainField: mainFieldName, fields } = cardResponse

      cardResponse.fieldNameList.forEach(fieldName => {
        if (!fields[fieldName]) {
          return
        }

        if (fieldName == mainFieldName) {
          this.mainField = {
            value: fields[mainFieldName],
            name: mainFieldName,
          }
        } else if (fieldName == 'id') {
          this.cardId = fields['id']
        } else {
          this.params.push({ name: fieldName, value: fields[fieldName] })
        }
      })
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';
.bot-confirmation-card {
  max-width: 280px;
  min-width: 250px;

  .card-area-container {
    padding: 20px 20px 0px 20px;
  }

  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  border-bottom-left-radius: 4px;
  border-top-left-radius: 10px;

  overflow: hidden;
  box-shadow: 0px -1px 4px 0 rgba(233, 233, 226, 0.5);
  border: solid 1px $bot-message-color;
  background-color: $bot-background;

  .title {
    font-size: 15px;
    font-weight: 500;
    color: $bot-title-color;
    line-height: 1.2;
    letter-spacing: 0.5px;
    max-width: 200px;
    margin-bottom: 20px;
  }
  .param-container {
    // margin-top: 20px;
    // margin-top: 20px
    // padding:20px 0px;
  }

  .param-row {
    margin-bottom: 15px;
    width: 100%;
    display: flex;
    align-items: center;
  }
  .param-name {
    width: 50%;

    color: $bot-subtitle-color;
    font-size: 10px;
    margin-top: 1px;

    letter-spacing: 0.9px;
    text-transform: uppercase;
  }

  .param-value {
    width: 50%;
    font-size: 11px;
    font-weight: normal;
    letter-spacing: 0.5px;
    color: $bot-title-color;
    // margin-left: 15px;
  }
  .confirmation-message {
    background: $bot-message-color;
    color: #000000;

    letter-spacing: 0.5px;
    padding: 12px 15px;

    font-size: 14px;
  }

  .card-id {
    margin-bottom: 5px;
    a {
      font-size: 12px;
      font-weight: 500;
      color: $bot-pink-color;
      letter-spacing: 0.5px;
    }
  }
  .param-assignee {
    display: flex;
    align-items: center;
  }
  .assignee-details {
    line-height: 15px;
  }
  .assignee-logo {
    font-size: 9px;
    width: 20px;
    height: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 5px;
    border-radius: 50%;
    background: #a99ff7;
    flex-shrink: 0;
    color: $bot-background;
  }
}
</style>
