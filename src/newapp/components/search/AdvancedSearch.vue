<template>
  <div>
    <div @click="openCloseSearch(true)" class="d-flex align-center">
      <slot name="icon">
        <InlineSvg
          src="svgs/search"
          class="d-flex mT3 cursor-pointer"
          iconClass="icon icon-sm self-center mR5"
        ></InlineSvg>
      </slot>
    </div>
    <el-dialog
      v-if="canShowSearchWiz"
      :title="title"
      :visible.sync="canShowSearchWiz"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="advance-search-container"
    >
      <AdvancedSearch
        :moduleName="moduleName"
        :searchParam="searchParam"
        :hideQuery="hideQuery"
        :filterList="filterList"
        @applyFilters="applyFilters"
        @closeDialog="() => openCloseSearch(false)"
      />
    </el-dialog>
  </div>
</template>
<script>
import { isWebTabsEnabled } from '@facilio/router'
import { AdvancedSearch } from '@facilio/criteria'
import { isEmpty } from '@facilio/utils/validation'
import { mapActions } from 'vuex'
export default {
  props: [
    'moduleName',
    'moduleDisplayName',
    'filterList',
    'hideQuery',
    'onSave',
  ],

  components: {
    AdvancedSearch,
  },
  data() {
    return {
      searchText: '',
      canShowSearchWiz: false,
      isSaving: false,
      searchableFields: [],
      isOpenWizPressed: false,
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
      document.addEventListener('keyup', this.keyUpHandler)
    }
  },
  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
      document.removeEventListener('keyup', this.keyUpHandler)
    }
  },
  computed: {
    title() {
      let { moduleDisplayName } = this
      let title = `${this.$t(
        'commissioning.sheet.filter'
      )} ${moduleDisplayName} ${this.$t('filters.search.by')}`
      return title
    },
    searchParam() {
      let { $route } = this
      let { query } = $route || {}
      let { search } = query || {}
      if (!isEmpty(search)) {
        return search
      }
      return ''
    },
  },
  mounted() {
    this.fetchSearchableFields({ moduleName: this.moduleName })
  },
  methods: {
    ...mapActions({
      fetchSearchableFields: 'search/fetchSearchableFields',
    }),
    openCloseSearch(canShow) {
      this.searchText = ''
      this.canShowSearchWiz = canShow
    },

    applyFilters(filters) {
      let { hideQuery } = this
      if (hideQuery) {
        this.onSave({ filters })
      } else {
        this.$store.dispatch('search/applyFilters', filters)
      }
      this.openCloseSearch(false)
    },

    keyDownHandler(e) {
      if (e.shiftKey && e.code === 'Slash') {
        this.isOpenWizPressed = true
      } else {
        let { userAgentData, platform } = navigator
        let { platform: userPlatform } = userAgentData || {}
        let currentOS = userPlatform || platform
        let isMac = currentOS.toUpperCase().indexOf('MAC') >= 0

        if (
          ((isMac && e.metaKey) || (!isMac && e.ctrlKey)) &&
          e.key === 'Enter'
        ) {
          this.applyFilter()
        }
      }
    },
    keyUpHandler() {
      if (this.isOpenWizPressed) {
        this.openCloseSearch(true)
        this.isOpenWizPressed = false
      }
    },
  },
}
</script>
<style lang="scss">
.advance-search-container {
  &.is-fullscreen {
    width: 32%;
    overflow: hidden;
    height: 100vh;
    display: flex;
    flex-direction: column;
  }
  .el-dialog__body {
    padding: 0px;
    display: flex;
    flex-direction: column;
    overflow: scroll;
    height: 100%;
    .search-container {
      .search-box {
        .el-input__inner {
          height: 40px;
          border-radius: 0px;
          padding: 15px 40px;
          border: solid 1px #d0d9e2;
          font-size: 14px;
          letter-spacing: 0.4px;
          color: #324056;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        .el-input__prefix {
          left: 15px;
        }
      }
    }
    .field-container {
      padding: 30px 25px 0px;
      overflow: auto;
      flex-grow: 1;
      .field-item {
        margin-bottom: 25px;
        .field-label {
          display: flex;
          margin-bottom: 10px;
          .name {
            padding-left: 15px;
            font-size: 14px;
            letter-spacing: 0.5px;
            color: #324056;
          }
        }
      }
    }
    .btn-container {
      display: flex;
    }
  }
  margin-right: 0;
  border-radius: 0px;
}
</style>
