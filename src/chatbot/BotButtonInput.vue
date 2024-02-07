<template>
  <div class="bot-resp-button-group">
    <button
      class="bot-resp-button"
      v-for="(option, index) in options"
      :key="index"
      @click="optionClicked(option)"
    >
      {{ option.label }}
    </button>
  </div>
</template>
<script>
export default {
  props: ['options'],
  methods: {
    optionClicked(option) {
      this.$emit('optionSelected', option)
    },
  },
}
</script>
<style lang="scss">
@import './style/_variables.scss';
.bot-resp-button-group {
  align-self: flex-end;
  display: flex;
  // flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
}
.bot-resp-button {
  font-size: 14px;
  padding: 12px;
  margin-right: 5px;
  margin-bottom: 5px;
  color: $bot-base-color;
  border: solid 1px #5b449e;
  border-radius: 20px;
  outline: none;
  cursor: pointer;
  &:hover {
    background: $bot-base-color;
    color: #ffffff;
  }
}
</style>
