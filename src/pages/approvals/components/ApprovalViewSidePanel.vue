<template>
  <transition name="fade">
    <div class="view-panel">
      <div class="view-list-container">
        <div class="header-container">
          <div class="label-txt-black fwBold text-uppercase">
            {{ $t('common._common.views') }}
          </div>
          <inline-svg
            src="svgs/arrow"
            class="rotate-90 pointer d-flex back-icon mL-auto"
            iconClass="icon icon-xs"
            @click.native="closeViewSidePanel()"
          ></inline-svg>
        </div>
        <div class="search-container mT20 mb25">
          <el-input
            placeholder="Search"
            v-model="searchText"
            type="search"
            :prefix-icon="'el-icon-search'"
            class="fc-input-full-border2"
          >
          </el-input>
        </div>
        <div class="views-group">
          <div v-for="group in filteredList" :key="group.id">
            <template v-if="!$validation.isEmpty(group.views)">
              <div
                v-if="$validation.isEmpty(searchText)"
                class="folder-name pB10 mT20 text-uppercase"
              >
                {{ group.displayName || group.name }}
              </div>
              <div
                v-for="(view, index) in group.views"
                :key="index"
                class="view-item pT10 pB15 pointer"
                :class="isActiveView(view) ? 'active' : ''"
                @click="changeView(view, group)"
              >
                <div class="view-name ellipsis">
                  {{ view.displayName }}
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import ApprovalViewMixin from './ApprovalViewMixin'

export default {
  props: [
    'moduleName',
    'canShowViewsSidePanel',
    'pathPrefix',
    'retainFilters',
    'moduleList',
  ],
  mixins: [ApprovalViewMixin],
  data() {
    return {
      searchText: '',
    }
  },
  async created() {
    await this.loadApprovalModules()
  },
  computed: {
    filteredList() {
      if (isEmpty(this.searchText)) {
        return this.groupViews
      } else {
        return this.groupViews.reduce((res, group) => {
          let filteredViews = group.views.filter(view =>
            view.displayName.toLowerCase().includes(this.searchText)
          )
          if (!isEmpty(filteredViews))
            res.push({
              ...group,
              views: filteredViews,
            })
          return res
        }, [])
      }
    },
  },
  methods: {
    isActiveView({ name = '' } = {}) {
      let { currentViewName } = this
      return currentViewName === name
    },
    changeView(view, group) {
      this.goToView(view, group)
      this.closeViewSidePanel()
    },
    closeViewSidePanel() {
      this.$emit('update:canShowViewsSidePanel', false)
    },
  },
}
</script>
<style lang="scss">
.view-panel {
  display: flex;
  flex-direction: column;
  .view-list-container {
    overflow: hidden;
    flex: 1;
    width: 288px;
    padding: 20px 15px 0px;
    background-color: #ffffff;
    border: 1px solid #eee;
    border-top: none;
    .header-container {
      display: flex;
      margin-right: -15px;
      .back-icon {
        border: 1px solid #dddedf;
        border-top-right-radius: 3px;
        border-top-left-radius: 3px;
        border-bottom: none;
        padding: 4px;
        .icon {
          fill: #615e88;
        }
      }
    }
    .search-container {
      .fc-input-full-border2 {
        &.el-input {
          .el-input__inner {
            padding-left: 30px !important;
          }
        }
      }
    }
    .views-group {
      overflow: scroll;
      height: calc(100vh - 150px);
      margin: 0 -15px;
      padding-bottom: 100px;
    }
    .folder-name {
      font-size: 12px;
      font-weight: 500;
      letter-spacing: 1px;
      color: #ff3184;
      padding: 0 15px;
    }
    .view-name {
      font-size: 14px;
      letter-spacing: 0.5px;
      color: #324056;
    }
  }
  .view-item {
    padding: 0 20px;
    text-transform: capitalize;
    font-size: 14px;
    color: #333;
    height: 40px;
    &:hover,
    &.active {
      background-color: #f1f4f8;
    }
  }
}
</style>
