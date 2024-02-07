<template>
  <div
    :class="[
      'bot-list-container',
      {
        'empty-list':
          botMessage.textList == null || botMessage.textList.length == 0,
      },
    ]"
  >
    <div
      class="bot-list"
      v-if="botMessage.textList && botMessage.textList.length > 0"
    >
      <div
        v-for="(item, index) in botMessage.textList"
        :key="index"
        :class="[
          'bot-list-item',
          { 'last-item': index == botMessage.textList.length - 1 },
        ]"
      >
        <vue-markdown class="vue-markdown" :linkify="false" :source="item">
        </vue-markdown>
      </div>
    </div>

    <div
      :class="[
        'list-message',
        {
          'empty-list':
            botMessage.textList == null || botMessage.textList.length == 0,
        },
      ]"
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
import VueMarkdown from 'vue-markdown'
export default {
  props: ['botMessage'],
  components: {
    VueMarkdown,
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';

.bot-list-container {
  max-width: 250px;
  min-width: 150px;
  border-radius: 15px;
  border: solid 1.5px $bot-message-color;
  overflow: hidden;
  &.empty-list {
    border-bottom-left-radius: 4px;
    // if no list make the message seem like a standalone text message
  }
  .bot-list {
    padding: 10px 10px 0px 10px;
  }
  .bot-list-item {
    padding: 20px 10px;
    border-bottom: 1px solid $bot-border-color;

    letter-spacing: 0.5px;
    font-size: 13px;
    color: $bot-title-color;
    line-height: 1.3;
  }
  .bot-list-item.last-item {
    border: none;
  }

  .list-message {
    background-color: $bot-message-color;
    color: #000000;
    letter-spacing: 0.5px;
    padding: 12px 15px;
    font-size: 14px;
    line-height: 1.5;
  }
}
</style>
