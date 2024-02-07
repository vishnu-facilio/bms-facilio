<template>
  <div class="container news-card flex-col">
    <div class="d-flex flex-col pT30 pB10 pL25 pR25">
      <div class="d-flex">
        <div class="mR10">
          <UserAvatar
            size="lg"
            v-if="recordObj.sysCreatedBy"
            :user="recordObj.sysCreatedBy"
            :name="false"
          ></UserAvatar>
        </div>
        <div class="d-flex flex-col">
          <div>
            <span class="bold f15">
              {{ $getProperty(recordObj, 'sysCreatedBy.name') }}
            </span>
            {{ publishInfo }}
          </div>
          <div class="mT5 f12 text-fc-grey">
            {{ recordObj.sysCreatedTime }}
          </div>
        </div>
      </div>
      <div class="d-flex flex-col mT20">
        <div class="bold textoverflow-ellipsis">
          {{ recordObj.title }}
        </div>
        <div class="mT5 description richtext-preview">
          <div
            v-if="recordObj.description"
            v-html="recordObj.description"
          ></div>
          <div v-else>---</div>
        </div>
      </div>
    </div>
    <ImageGrid :images="imageAttachments"></ImageGrid>
    <Comments
      v-if="recordObj.commentsAllowed"
      :record="record"
      :comments="comments"
      :moduleName="moduleName"
      :notesModuleName="notesModuleName"
      :canShowAllComments="canShowAllComments"
      @onShowAllComments="onShowAllComments"
      @onCommentAdded="onCommentAdded"
    ></Comments>
  </div>
</template>
<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import UserAvatar from '@/avatar/User'
import ImageGrid from './ImageGrid'
import orderBy from 'lodash/orderBy'
import { formatDate } from 'util/filters'
import { sanitize } from '@facilio/utils/sanitize'
import Comments from './Comments'

const sharingTypes = {
  1: 'tenant unit',
  2: 'role',
  3: 'people',
  4: 'building',
}

export default {
  props: [
    'record',
    'moduleName',
    'notesModuleName',
    'attachmentsModuleName',
    'canShowAllComments',
    'commentsList',
    'attachmentList',
  ],
  components: {
    UserAvatar,
    ImageGrid,
    Comments,
  },
  computed: {
    comments() {
      let {
        record,
        notesModuleName,
        canShowAllComments,
        commentsList,
        $getProperty,
      } = this
      let comments = canShowAllComments
        ? commentsList
        : $getProperty(record, notesModuleName, [])

      return orderBy(comments, ['createdTime'], ['desc'])
    },
    attachments() {
      let { record, attachmentsModuleName, attachmentList, $getProperty } = this

      return !isEmpty(attachmentList)
        ? attachmentList
        : $getProperty(record, attachmentsModuleName, [])
    },
    imageAttachments() {
      let { attachments = [] } = this

      return attachments
        .filter(a => isObject(a))
        .filter(a => a.contentType.includes('image'))
    },
    publishInfo() {
      let { record, $getProperty } = this
      let publishedTo = $getProperty(record, 'newsandinformationsharing') || []
      let sharingType = $getProperty(publishedTo, `0.sharingType`)
      let sharingInfo = 'shared a post with '
      if (sharingTypes[sharingType] === 'people') {
        sharingInfo = sharingInfo + 'you'
      } else {
        sharingInfo = sharingInfo + 'your ' + sharingTypes[sharingType]
      }
      return sharingInfo
    },
    recordObj() {
      let { record } = this
      let {
        sysCreatedBy,
        sysCreatedTime,
        description,
        title,
        commentsAllowed = false,
      } = record

      return {
        title,
        description: sanitize(description),
        sysCreatedBy,
        sysCreatedTime: formatDate(sysCreatedTime),
        commentsAllowed,
      }
    },
  },
  methods: {
    onCommentAdded(...args) {
      this.$emit('onCommentAdded', ...args)
    },
    onShowAllComments() {
      this.$emit('onShowAllComments')
    },
  },
}
</script>
<style lang="scss" scoped>
.news-card {
  min-width: 700px;
  max-width: 55%;
  background-color: #fff;
  margin-bottom: 20px;

  .description {
    line-height: 1.43;
    letter-spacing: 0.5px;
  }
}
</style>
