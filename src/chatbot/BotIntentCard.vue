<template>
  <div class="bot-intent-card-container">
    <!-- Initial 3 options shown in chat window -->
    <div
      class="lookup-card"
      v-for="(suggestion, index) in quickList"
      :key="index"
      @click="intentSelected(suggestion)"
    >
      <div class="lookup-label">{{ suggestion.suggestion }}</div>
    </div>
    <button class="lookup-show-more" @click="expandCard">
      Show more
      <i class="right-arrow"></i>
    </button>
    <!-- Lookup UI on clicking show more -->
    <!-- <transition name="fc-bot-expand"> -->
    <div v-if="isExpanded" class="bot-lookup-expanded">
      <div class="lookup-search-area">
        <el-input
          ref="input"
          placeholder="type and hit enter to search"
          class="search-bar"
          size="medium"
          prefix-icon="el-icon-search"
          v-model.lazy="tempSearch"
          @keyup.native.enter="searchSuggestions"
        ></el-input>
      </div>

      <spinner
        style="margin-top: 150px;"
        v-if="loadingSuggestions"
        :show="loadingSuggestions"
        :size="80"
      ></spinner>

      <div class="scroll-container" ref="scrollContainer" v-else>
        <div class="lookup-list">
          <div
            class="lookup-list-item"
            v-for="(suggestion, index) in searchResultSuggestions"
            :key="index"
            @click="intentSelected(suggestion)"
          >
            <div class="lookup-label">
              {{ suggestion.suggestion }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- </transition> -->
  </div>
</template>

<script>
export default {
  created() {},

  mounted() {
    //this.loadQuickList()
  },
  props: ['suggestions', 'isExpanded'],
  data() {
    return {
      tempSearch: null,
      search: null,

      loadingSuggestions: true,
    }
  },
  computed: {
    quickList() {
      return this.suggestions.slice(0, 3)
    },
    searchResultSuggestions() {
      if (this.search) {
        let suggestions = this.suggestions.filter(e => {
          return e.suggestion.toLowerCase().includes(this.search.toLowerCase())
        })
        return suggestions
      } else {
        return this.suggestions
      }
    },
  },
  methods: {
    intentSelected(record) {
      this.$emit('intentSelected', record)
    },

    expandCard() {
      this.loadingSuggestions = true
      this.$emit('update:isExpanded', true)
      //no loading from server for now
      window.setTimeout(() => {
        this.loadingSuggestions = false
      }, 500)
      this.$nextTick(() => {
        this.$refs['input'].focus()
      })
      this.tempSearch = null
      this.search = null
    },
    searchSuggestions() {
      this.search = this.tempSearch
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';
.bot-intent-card-container {
  align-self: flex-end;

  .lookup-card {
    display: flex;
    letter-spacing: 0.5px;
    color: $bot-base-color;
    min-height: 50px;
    border-radius: 8px;
    border: solid 1px $bot-base-color;
    padding: 9px 12px;
    margin-bottom: 12px;
    cursor: pointer;

    .lookup-id {
      font-size: 11px;
      margin-bottom: 8px;
    }
    .lookup-label {
      font-size: 14px;
      margin-top: auto;
      margin-bottom: auto;
    }
    &:hover {
      background: $bot-base-color;
      color: #ffffff;
    }
  }

  .lookup-show-more {
    margin-top: 5px;
    font-size: 13px;
    letter-spacing: 0.4px;
    color: #73649e;
    outline: none;
    cursor: pointer;
    float: right;
    border: none;
  }
  .right-arrow {
    border: solid #73649e;
    border-width: 1px 1px 0px 0px;
    display: inline-block;
    padding: 3px;
    transform: rotate(45deg);
  }

  .bot-lookup-expanded {
    background: $bot-background;
    position: absolute;
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    z-index: 2;
    top: 0;
    left: 0;
  }
  .lookup-search-area {
    padding: 5px 25px 20px 25px;
    background-color: $bot-base-color;
  }
  .search-bar {
    width: 100%;
  }
  .el-input .el-input__inner {
    padding: 0px 35px 0px 35px;
    border-radius: 20px;
    border: none;
    height: 36px;
  }
  .el-icon-search {
    color: $lookup-secondary-color;
  }
  .scroll-container {
    // border-bottom: solid 1px #e5e4e4;
    flex-grow: 1;
    height: 0;
    overflow-y: scroll;
  }

  .lookup-list-item {
    font-size: 14px;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding: 20px 10px 20px 30px;
    border-bottom: 1px solid $bot-border-color;
    cursor: pointer;
    // &:hover {
    //   background: $bot-base-color;
    //   color: #ffffff !important ;
    // }

    .lookup-id {
      color: $lookup-secondary-color;
      margin-right: 10px;
    }
    .lookup-label {
      letter-spacing: 0.5px;
      color: $bot-base-color;
    }
  }
}
</style>
