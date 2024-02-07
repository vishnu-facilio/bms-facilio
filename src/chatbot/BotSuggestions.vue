<template>
  <div class="bot-suggestion-bar" v-if="suggestions">
    <div class="scroll-container">
      <button
        class="bot-suggestion-button"
        v-for="(suggestion, index) in suggestions"
        :key="index"
        @click="suggestionClicked(suggestion)"
      >
        {{ suggestion.suggestion }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    suggestions: {
      type: Array,
    },
  },
  methods: {
    suggestionClicked(suggestion) {
      this.$emit('suggestion', suggestion)
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';
.bot-suggestion-bar {
  position: absolute;
  width: 100%;
  top: -59px;
  padding: 10px 20px;
  background: white;
  box-shadow: 0 0px 4px 0 rgba(233, 233, 226, 0.5);

  .bot-suggestion-button {
    font-size: 12px;
    padding: 10px;
    min-width: 50px;
    margin-right: 5px;
    white-space: nowrap;
    color: #3e2a8c;
    border: solid 1px #5b449e;
    border-radius: 20px;
    outline: none;
    cursor: pointer;
    flex: 0 0 auto;
    &:hover {
      background: $bot-base-color;
      color: #ffffff;
    }
  }
  .scroll-container {
    width: 100%;
    overflow-x: scroll;
    display: flex;
  }
}
</style>
