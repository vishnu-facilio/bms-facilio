<template>
  <div>
    <div class="height100 d-flex">
      <div style="flex: 0 0 300px; max-width: 300px;">
        <div
          class="height100vh full-layout-white fc-border-left fc-border-right"
        >
          <div class="row p15 fc-border-bottom pointer">
            <div class="col-1 text-left">
              <i
                class="el-icon-back fw6"
                @click="back"
                style="vertical-align: sub;"
              ></i>
            </div>
            <el-popover
              placement="bottom"
              width="250"
              v-model="toggle"
              popper-class="popover-height asset-popover "
              trigger="click"
              visible-arrow="true"
            >
              <ul>
                <li
                  @click="switchView(view)"
                  v-for="(view, index) in views"
                  :key="index"
                  :class="{ active: currentView === view.name }"
                >
                  {{ view.displayName }}
                </li>
              </ul>
              <span slot="reference" class="line-height20">
                {{ currentViewDetail.displayName }}
                <i
                  class="el-icon-arrow-down el-icon-arrow-down-tv"
                  style="padding-left:8px"
                ></i>
              </span>
            </el-popover>
            <!-- <div class="pointer" @click="toggleQuickSearch">
              <i
                class="fa fa-search fa-search-asste-icon"
                aria-hidden="true"
              ></i>
            </div>-->
            <div class="row" v-if="showQuickSearch">
              <div class="col-12 fc-list-search">
                <div
                  class="fc-list-search-wrapper fc-list-search-wrapper-asset"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="32"
                    height="32"
                    viewBox="0 0 32 32"
                    class="search-icon-asset"
                  >
                    <title>search</title>
                    <path
                      d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                    />
                  </svg>
                  <input
                    ref="quickSearchQuery"
                    autofocus
                    type="text"
                    v-model="quickSearchQuery"
                    @keyup.enter="quickSearch"
                    placeholder="Search"
                    class="quick-search-input-asset"
                  />
                  <svg
                    @click="closeSearch"
                    xmlns="http://www.w3.org/2000/svg"
                    width="32"
                    height="32"
                    viewBox="0 0 32 32"
                    class="close-icon-asset"
                    aria-hidden="true"
                  >
                    <title>close</title>
                    <path
                      d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                    />
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <div class="row sp-navbar2">
            <ul class="sp-ul">
              <v-infinite-scroll
                :loading="loading"
                @bottom="nextPage"
                :offset="20"
                style="height: 100vh; padding-bottom: 100px;overflow-y: scroll;"
              >
                <div
                  class="menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20"
                  @click="getLink(row.id)"
                  v-for="row in customModuleList"
                  :key="row.id"
                  v-bind:class="{ active: id === row.id }"
                >
                  <div class="flex-center-row-space">
                    <div class="fc-id">#{{ row.id }}</div>
                    <!-- <div class="fc-green-label5">Active</div> -->
                  </div>
                  <div
                    class="label pT5 bold"
                    :title="row[mainFieldKey]"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{ row[mainFieldKey] }}
                  </div>
                  <!-- <div class="fc-grey12-10 pT5">
                    FEWA
                  </div> -->
                </div>
              </v-infinite-scroll>
            </ul>
            <ul v-if="loading" class="sp-ul">
              <spinner :show="loading" size="80"></spinner>
            </ul>
            <ul v-if="loadingLists" class="sp-ul">
              <spinner :show="loadingLists" size="60"></spinner>
            </ul>
          </div>
        </div>
      </div>
      <div style="flex: 1;">
        <router-view :key="id"></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import CustomModuleOverviewList from 'pages/base-module-v2/ModuleListOverview'
export default {
  extends: CustomModuleOverviewList,
}
</script>
