<template>
  <PageLayout :moduleName="moduleName">
    <template slot="header">
      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="bottom"
      >
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        >
        </AdvancedSearch>
      </el-tooltip>
    </template>
    <Tags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></Tags>
    <spinner
      v-if="isLoading"
      :show="true"
      size="80"
      class="align-center"
    ></spinner>
    <div
      v-else-if="$validation.isEmpty(moduleRecordList)"
      class="list-container"
    >
      <div class="list-empty-state">
        <inline-svg
          src="svgs/community-empty-state/news-info"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="q-item-label nowo-label">
          {{ $t('tenant.news.no_data') }}
        </div>
      </div>
    </div>
    <infinite-scroll
      v-else
      :loading="isLoadMoreNews"
      class="p20 d-flex flex-col overflow-y-scroll"
      @bottom="loadMoreRecords"
      :offset="20"
    >
      <NewsCard
        v-for="record in moduleRecordList"
        :key="record.id"
        :record="record"
        :moduleName="moduleName"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        @onCommentAdded="comment => onCommentAdded(record.id, comment)"
        @onShowAllComments="openRecordSummary(record)"
      ></NewsCard>
    </infinite-scroll>
    <div v-if="isLoadMoreNews" class="spinner-space">
      <spinner :show="true" size="50"></spinner>
    </div>
  </PageLayout>
</template>
<script>
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import InfiniteScroll from 'v-infinite-scroll'
import PageLayout from 'src/PortalTenant/components/PageLayout'
import ViewMixinHelper from '@/mixins/ViewMixin'
import NewsCard from './components/NewsCard'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Tags from 'newapp/components/search/FTags'

export default {
  props: ['viewname', 'moduleName', 'notesModuleName', 'attachmentsModuleName'],
  mixins: [ViewMixinHelper],
  components: {
    PageLayout,
    NewsCard,
    InfiniteScroll,
    AdvancedSearch,
    Tags,
  },
  title() {
    return 'News and Information'
  },
  data() {
    return {
      moduleRecordList: [],
      listCount: null,
      isLoading: true,
      page: 1,
      isLoadMoreNews: false,
    }
  },
  created() {
    this.$store.dispatch('loadUsers')
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
  },
  computed: {
    currentView() {
      return this.viewname
    },
    filters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName || ''
    },
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.getViewDetail()
          this.loadRecords()
        }
      },
      immediate: true,
    },
    filters(newValue, oldValue) {
      if (isEmpty(newValue) || (isEmpty(oldValue) && !isEmpty(newValue)))
        this.page = 1
      this.loadRecords()
    },
  },
  methods: {
    async loadRecords({ force = false, loadMore = false } = {}) {
      let { filters, currentView, moduleName, includeParentFilter, page } = this
      if (isEmpty(currentView)) return

      let config = force ? { force } : {}
      if (loadMore) {
        this.isLoadMoreNews = true
        this.page = page + 1
      } else {
        this.isLoading = true
      }

      let { list, meta: { pagination } = {}, error } =
        (await API.fetchAll(
          moduleName,
          {
            viewName: currentView,
            page: this.page,
            perPage: 10,
            withCount: true,
            filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
            includeParentFilter,
          },
          config
        )) || {}

      if (error) {
        this.$message.error(
          error.message || 'Unable to fetch News and Information'
        )
      } else {
        if (loadMore) {
          this.moduleRecordList = [...this.moduleRecordList, ...list]
        } else {
          this.moduleRecordList = list
        }
        this.listCount = (pagination || {}).totalCount || null
      }

      this.isLoading = false
      this.isLoadMoreNews = false
    },
    loadMoreRecords() {
      if (this.listCount > this.moduleRecordList.length) {
        this.loadRecords({ loadMore: true })
      }
    },
    getViewDetail() {
      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
      }
    },
    openRecordSummary({ id }) {
      let { moduleName, viewname, $router } = this
      let route = findRouteForModule(moduleName, pageTypes.OVERVIEW)

      route && $router.push({ name: route.name, params: { id, viewname } })
    },
    onCommentAdded(recordId, comment) {
      let index = this.moduleRecordList.findIndex(({ id }) => id === recordId)
      this.moduleRecordList[index][this.notesModuleName].unshift(comment)
    },
  },
}
</script>

<style lang="scss">
.spinner-space {
  height: 100px;
  width: 53%;
  margin-left: 20px;
}
</style>
