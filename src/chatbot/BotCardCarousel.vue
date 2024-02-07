<template>
  <div class="bot-card-carousal">
    <div class="scroll-container">
      <bot-confirmation
        class="bot-carousel-card-item"
        v-for="(botConfirmationCard, index) in botConfirmationCards"
        :key="index"
        :botMessage="botConfirmationCard"
      >
      </bot-confirmation>
    </div>
    <div
      class="text-message carousel-message"
      v-if="botMessage.chatBotResponse"
    >
      <vue-markdown
        class="vue-markdown"
        :linkify="false"
        :source="botMessage.chatBotResponse"
      >
      </vue-markdown>
    </div>
  </div>
</template>

<script>
import BotConfirmation from './BotConfirmation'
import VueMarkdown from 'vue-markdown'
export default {
  created() {
    let cardResp = this.botMessage.chatBotConfirmationResponse
    this.botConfirmationCards = cardResp.fields.map((ele, index) => {
      return {
        chatBotConfirmationResponse: {
          fieldNameList: cardResp.fieldNameList,
          fields: cardResp.fields[index], //turn multi card resp into list of single card resps
          mainField: cardResp.mainField,
        },
      }
    })
  },
  components: {
    BotConfirmation,
    VueMarkdown,
  },
  props: ['botMessage'],
  data() {
    return {
      botConfirmationCards: [],
    }
  },
}
</script>

<style lang="scss">
.bot-card-carousal {
  overflow-x: scroll;

  // flex container
  .scroll-container {
    display: flex;
    width: 100%;
  }

  // flex-items
  .bot-carousel-card-item {
    flex: 0 0 auto;
    margin-right: 15px;
  }
  .carousel-message {
    margin-top: 10px;
    position: sticky;
    left: 0;
  }
}
</style>
