<template>
  <div class="bot-lookup-container" v-if="!loadingQuickList">
    <!-- Initial 3 options shown in chat window -->
    <div
      class="lookup-card"
      v-for="(lookup, index) in quickList"
      :key="index"
      @click="lookupSelected(lookup)"
    >
      <div class="lookup-id">#{{ lookup.id }}</div>
      <div class="lookup-label">{{ lookup.label }}</div>
    </div>
    <button class="lookup-show-more" @click="expandLookup">
      Show more
      <i class="right-arrow"></i>
    </button>
    <!-- Lookup UI on clicking show more -->
    <div v-if="isExpanded" class="bot-lookup-expanded">
      <div class="lookup-search-area">
        <el-input
          ref="input"
          placeholder="type and hit enter to search"
          class="search-bar"
          size="medium"
          prefix-icon="el-icon-search"
          v-model.lazy="lookupSearch"
          @keyup.native.enter="loadlookups"
        ></el-input>
      </div>

      <spinner
        style="margin-top: 150px;"
        v-if="loadingLookups"
        :show="loadingLookups"
        :size="80"
      ></spinner>

      <div class="scroll-container" ref="scrollContainer" v-else>
        <div class="lookup-list">
          <div
            class="lookup-list-item"
            v-for="(lookup, index) in lookups"
            :key="index"
            @click="lookupSelected(lookup)"
          >
            <div class="lookup-id">#{{ lookup.id }}</div>
            <div class="lookup-label">
              {{ lookup.label }}
            </div>
          </div>
        </div>
        <div class="observer-node" ref="observerNode"></div>
        <spinner v-if="loadingMore" :show="loadingMore" :size="50"></spinner>
      </div>
    </div>
  </div>
</template>

<script>
import { deepCloneObject } from 'util/utility-methods'
export default {
  created() {},
  destroyed() {
    this.clearObserver()
  },

  mounted() {
    this.loadQuickList().then(() => {
      this.loadingQuickList = false
      this.$emit('messageReady')
    })
  },
  props: ['botMessage', 'isExpanded'],
  data() {
    return {
      loadingQuickList: true,
      lookupSearch: null,
      quickList: [],
      lookups: [],
      loadingLookups: true,
      pageCount: null,

      perPage: 20,
      pageNumber: 1,

      //infinite scroll states
      canLoadMore: true,
      loadingMore: false,
    }
  },
  methods: {
    getCriteria() {
      let criteria = deepCloneObject(this.botMessage.criteria)

      delete criteria.regEx
      return criteria
    },
    getOrderBy() {
      if (this.botMessage.orderBy) {
        return this.botMessage.orderBy.split(' ')[0]
      }
    },
    getOrderType() {
      if (this.botMessage.orderBy) {
        return this.botMessage.orderBy.split(' ')[1]
      }
    },
    clearObserver() {
      if (this.intersectionObserver) {
        this.intersectionObserver.disconnect()
        this.intersectionObserver = null
      }
    },
    lookupSelected(record) {
      this.$emit('recordSelected', record)
    },
    async getLookups(perPage, page, search, moduleName) {
      if (!this.mainFieldName) {
        //get main field from modulemeta
        let resp = await this.$http.get(`v2/modules/meta/${moduleName}`)
        let moduleMeta = resp.data.result.meta
        this.mainFieldName = moduleMeta.fields.find(
          field => field.mainField
        ).name
      }

      let params = {
        perPage,
        page,
        criteria: this.getCriteria(),
        orderBy: this.getOrderBy(),
        orderType: this.getOrderType(),
        overrideSorting: true,
      }
      if (search) {
        params.filters = JSON.stringify({
          [this.mainFieldName]: {
            operatorId: 5,
            value: [search],
          },
        })
      }

      let resp = await this.$http.post(
        `v2/module/data/list?moduleName=${moduleName}`,
        params
      )
      let moduleDatas = resp.data.result.moduleDatas
      let options = []

      // if (resp) {
      //   options = Object.keys(resp).map(key => {
      //     return {
      //       id: Number.parseInt(key),
      //       label: resp[key],
      //     }
      //   })
      // }

      if (moduleDatas) {
        options = moduleDatas.map(record => {
          return { id: record.id, label: record[this.mainFieldName] }
        })
      }
      return options
    },
    loadQuickList() {
      return this.getLookups(3, 1, null, this.botMessage.moduleName).then(
        lookups => {
          this.quickList = lookups
        }
      )
    },
    expandLookup() {
      this.$emit('update:isExpanded', true)
      // this.clearObserver()
      this.lookupSearch = null
      this.loadlookups()
    },

    loadlookups() {
      //called initially and whenever search param changes, load inital items and set up lazy loading on scroll

      this.loadingLookups = true
      this.clearObserver()
      this.lookups = []
      this.pageNumber = 1

      //inital load of list , once loaded remove full loader and set up intersection observer for lazy load
      this.getLookups(
        this.perPage,
        this.pageNumber,
        this.lookupSearch,
        this.botMessage.moduleName
      ).then(lookups => {
        this.lookups = lookups
        this.loadingLookups = false
        this.$nextTick(() => {
          this.$refs['input'].focus()
          this.setUpIntersectionObserver()
        })
      })
    },
    loadMore() {
      this.loadingMore = true
      this.pageNumber++
      this.getLookups(
        this.perPage,
        this.pageNumber,
        this.lookupSearch,
        this.botMessage.moduleName
      ).then(lookups => {
        if (lookups.length == 0) {
          this.canLoadMore = false
        }
        this.loadingMore = false
        this.lookups.push(...lookups)
      })
    },
    handleIntersection([entry]) {
      if (entry && entry.isIntersecting) {
        if (!this.loadingMore && this.canLoadMore) {
          this.loadMore()
        }
      }
    },
    setUpIntersectionObserver() {
      this.loadingMore = false
      this.canLoadMore = true
      let scrollContainer = this.$refs['scrollContainer']
      //check if messages overflow the container ie if scroll is enabled, if not scroll no history present to load
      if (scrollContainer.scrollHeight == scrollContainer.clientHeight) {
        this.canLoadMore = false
      }
      if (!this.intersectionObserver) {
        //move as singleton while writing  lazy scroll seperate comp
        let options = {
          root: this.$refs['scrollContainer'],
          rootMargin: '0px 0px 0px 0px',
        }
        this.intersectionObserver = new IntersectionObserver(
          this.handleIntersection,
          options
        )
        this.intersectionObserver.observe(this.$refs['observerNode'])
      }
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';
.bot-lookup-container {
  align-self: flex-end;

  .lookup-card {
    max-width: 250px;
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
