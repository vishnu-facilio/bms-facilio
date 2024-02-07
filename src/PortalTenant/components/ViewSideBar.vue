<template>
  <transition name="fade">
    <div class="portal-view-sidebar-fixed">
      <div class="side-bar-view">
        {{ $t('common._common.views') }}
      </div>
      <div class="portal-view-search-bar">
        <el-input
          placeholder="Search"
          v-model="searchText"
          type="search"
          :prefix-icon="'el-icon-search'"
          class="fc-input-full-border2"
        ></el-input>
      </div>
      <div class="portal-view-sidebar">
        <div
          v-for="(group, grpIndex) in filteredList"
          :key="`${group.id}_${grpIndex}`"
          :class="[$validation.isEmpty(searchText) && 'mB10']"
        >
          <template v-if="!$validation.isEmpty(group.views)">
            <div v-if="$validation.isEmpty(searchText)" class="view-grp-name">
              {{ group.displayName || group.name }}
            </div>
            <div
              v-for="(view, index) in group.views"
              :key="index"
              class="view-item"
              :class="isActiveView(view) ? 'active' : ''"
              @click="changeView(view, group)"
            >
              <el-tooltip
                :content="view.displayName"
                placement="top"
                :manual="view.displayName.length < 25"
                :open-delay="1000"
              >
                <div class="ellipsis">
                  {{ view.displayName }}
                </div>
              </el-tooltip>
            </div>
          </template>
        </div>
      </div>
    </div>
  </transition>
</template>
<script>
import ViewsSidePanel from 'pages/views/ViewsSidePanel'
export default {
  extends: ViewsSidePanel,
}
</script>
<style lang="scss">
.portal-view-sidebar-fixed {
  width: 240px;
  background-color: #fff;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border: 1px solid #f4f4f4;

  .portal-view-search-bar {
    display: flex;
    line-height: 59px;
    justify-content: center;
    align-items: center;

    .fc-input-full-border2 {
      padding: 0 15px;
      margin: auto;
      flex-grow: 1;
    }
    .el-input__inner {
      padding-left: 30px !important;
      height: 32px !important;
    }
    .el-input__prefix {
      left: 20px;

      .el-input__icon.el-icon-search {
        line-height: 32px;
      }
    }
  }

  .portal-view-sidebar {
    overflow: scroll;

    .view-grp-name {
      letter-spacing: 1px;
      font-size: 14px;
      font-weight: 500;
      color: #ff3184;
      padding: 20px 30px;
    }
    .view-item {
      padding: 10px 30px;
      text-transform: capitalize;
      font-size: 14px;
      color: #324056;
      height: 40px;
      cursor: pointer;

      &.active {
        background-color: rgba(202, 202, 202, 0.15);
      }
    }
  }
  .side-bar-view {
    padding: 20px 17px;
    font-weight: 500;
    letter-spacing: 0.5px;
    line-height: 13px;
  }
  & + .module-list-container .portal-common-list .table-border {
    border-left-width: 0px;
  }
}
</style>
