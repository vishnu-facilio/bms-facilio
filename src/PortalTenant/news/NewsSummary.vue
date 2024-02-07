<template>
  <div class="height100">
    <div class="news-info-summary">
      <div class="header-title">
        <i
          class="el-icon-back bold pointer"
          content="back"
          arrow
          v-tippy="{ animateFill: false, animation: 'shift-toward' }"
          @click="goBack"
        ></i>
        {{ $t('tenant.news.news_info') }}
      </div>
    </div>
    <spinner
      v-if="isLoading"
      :show="true"
      size="80"
      class="align-center"
    ></spinner>
    <div v-else class="p20 d-flex flex-col scrollable120-y100">
      <NewsCard
        :key="record.id"
        :record="record"
        :moduleName="moduleName"
        :notesModuleName="notesModuleName"
        :commentsList="comments"
        :canShowAllComments="true"
        @onCommentAdded="onCommentAdded"
        :attachmentsModuleName="attachmentsModuleName"
        :attachmentList="attachments"
      ></NewsCard>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import NewsCard from './components/NewsCard'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'viewname',
    'id',
    'moduleName',
    'notesModuleName',
    'attachmentsModuleName',
  ],
  components: {
    NewsCard,
  },
  title() {
    return 'News and Information'
  },
  data() {
    return {
      record: {},
      comments: [],
      attachments: [],
      isLoading: true,
      isNotesLoading: true,
    }
  },
  async created() {
    this.isLoading = true
    await this.loadData()
    await this.loadComments()
    await this.loadAttachments()
    this.isLoading = false
  },
  methods: {
    async loadData() {
      let { id, moduleName } = this

      let { error, [moduleName]: record } = await API.fetchRecord(moduleName, {
        id,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.record = record
      }
    },
    async loadComments() {
      let { error, data } = await API.get('/note/get', {
        module: this.notesModuleName,
        parentId: this.id,
        parentModule: this.moduleName,
      })
      if (error) {
        this.comments = []
      } else {
        this.comments = !isEmpty(data) ? data : []
      }
    },
    async loadAttachments() {
      let { error, data } = await API.get('/attachment', {
        module: this.attachmentsModuleName,
        recordId: this.id,
      })

      if (error) {
        this.attachments = []
      } else {
        this.attachments = data.attachments ? data.attachments : []
      }
    },
    goBack() {
      let { viewname, $router } = this
      let route = findRouteForModule(this.moduleName, pageTypes.LIST)

      route && $router.push({ name: route.name, params: { viewname } })
    },
    onCommentAdded(comment) {
      this.comments.unshift(comment)
    },
  },
}
</script>
<style lang="scss">
.news-info-summary {
  height: 100px;
  padding: 13px 30px;
  width: 100%;
  background: #fff;
  position: relative;
  z-index: 1;
  display: block;
  box-shadow: 0 -3px 25px -7px #a39ea3;
  display: flex;
  align-items: center;

  .header-title {
    font-size: 24px;
    font-weight: 400;
    letter-spacing: 0.67px;
    color: #000000;
    line-height: 40px;
  }
}
</style>
