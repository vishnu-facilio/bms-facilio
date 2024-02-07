<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.announcement.announcements') }}
    </template>
    <template slot="header">
      <AdvancedSearchWrapper
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :hideSaveView="true"
      ></AdvancedSearchWrapper>
      <CreateButton @click="redirectToFormCreation">
        {{ $t('portal.announcement.new_announcement') }}
      </CreateButton>
    </template>
    <template #header-2>
      <pagination
        :total="recordCount"
        :perPage="perPage"
        :currentPageCount="currentPageCount"
      ></pagination>
    </template>
    <div v-if="showLoading" class="list-loading">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(records)" class="list-empty-state">
      <inline-svg
        src="svgs/community-empty-state/announcements"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('tenant.announcement.no_data') }}
      </div>
    </div>
    <div
      v-else
      class="d-flex flex-col overflow-scroll people-announcements-list"
    >
      <div
        v-for="record in records"
        :key="record.id"
        class="container flex-row flex-middle"
      >
        <div v-if="!record.isRead" class="new-badge">
          <div class="text">{{ $t('tenant.announcement.new') }}</div>
        </div>
        <ListAttachments
          :record="record"
          :id="record.parentId"
          :module="attachmentsModuleName"
          customClass="announcement-list-preview mR30"
        >
          <template slot="no-image">
            <InlineSvg
              src="clip"
              iconClass="icon fill-grey icon-xxlll vertical-middle object-contain fill-blue4"
            ></InlineSvg>
          </template>
          <template slot="no-data">
            <inline-svg
              src="svgs/community-features/announcements-list"
              iconClass="icon icon icon-xxl vertical-middle object-contain"
            ></inline-svg>
          </template>
        </ListAttachments>
        <router-link
          :to="redirectToOverview(record.id)"
          class="title-content d-flex flex-col mR100"
        >
          <div class="title textoverflow-ellipsis">
            {{ record.title }}
          </div>
          <div v-if="record.longDescription" class="description mT5">
            {{ record.longDescription | htmlToText }}
          </div>
        </router-link>
        <div class="additional-info d-flex flex-col">
          <div class="flex-middle">
            <UserAvatar
              size="xs mR5"
              v-if="record.createdBy"
              :user="getCreatedByUserObj(record)"
              :name="false"
            ></UserAvatar>
            <div class="text">
              {{ getCreatedByUserObj(record).name }}
            </div>
          </div>
          <div class="mT15 flex-middle">
            <inline-svg
              src="svgs/community-features/clock1"
              iconClass="icon icon icon-sm vertical-middle object-contain mR15"
            ></inline-svg>
            <div class="text">
              {{ $options.filters.formatDate(record.createdTime, true) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <router-view @refreshList="loadRecords(true)" />
  </PageLayout>
</template>

<script>
import UserAvatar from '@/avatar/User'
import ListAttachments from '@/relatedlist/ListAttachmentPreview'
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import { mapGetters } from 'vuex'
import getProperty from 'dlv'
import { isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  extends: ModuleList,
  props: ['attachmentsModuleName'],
  components: {
    UserAvatar,
    ListAttachments,
  },
  computed: {
    ...mapGetters(['getUser', 'getCurrentUser']),
  },
  methods: {
    getCreatedByUserObj(record) {
      let { createdBy: { id } = {} } = record || {}
      let user
      if (
        this.$validation.isEmpty(this.supplements) ||
        !this.supplements.hasOwnProperty('createdBy')
      ) {
        user = this.getUser(id)
      } else {
        user = this.supplements?.createdBy[id] || null
      }
      return user || {}
    },
    async loadRecords(force = false) {
      let { moduleName, viewname, filters, page, perPage } = this

      await API.cancel({ uniqueKey: `${moduleName}_LIST` })
      await API.cancel({ uniqueKey: `${moduleName}_CUSTOM_BUTTON` })

      try {
        this.isLoading = true
        this.currentPageCount = null
        let params = {
          fetchOnlyViewGroupColumn: true,
          viewName: viewname,
          filters: !this.$validation.isEmpty(filters)
            ? JSON.stringify(filters)
            : null,
          page: page,
          perPage: perPage || 50,
          withoutCustomButtons: true,
          force: null,
        }

        let config = { force, uniqueKey: `${moduleName}_LIST` }
        let { list, error, meta } = await API.fetchAll(
          moduleName,
          params,
          config
        )
        if (!error) {
          this.records = list
          this.supplements = getProperty(
            meta,
            'supplements.peopleannouncement',
            null
          )
          if (isArray(this.records)) {
            this.currentPageCount = this.records.length
            this.isLoading = false
          }
        }
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.list_error')
        )
        this.isLoading = false
      }
    },
    async loadCount(force = false) {
      let { moduleName, viewname, filters, recordCount } = this

      API.cancel({ uniqueKey: `${moduleName}_LIST_COUNT` })

      try {
        recordCount = null
        let params = { moduleName, viewname, filters, force }
        if (this.$validation.isEmpty(filters)) {
          params.filters = {}
        }
        params.filters['people'] = {
          operatorId: 9,
          value: [this.getCurrentUser().peopleId.toString()],
        }

        recordCount = await this.modelDataClass.fetchRecordsCount(params)
        this.recordCount = recordCount
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.unable_to_fetch_count')
        )
        this.recordCount = null
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.empty-state-container {
  display: flex;
  flex-direction: column;
  text-align: center;
  margin-bottom: 200px;
}

.people-announcements-list {
  height: 100%;
  .container {
    padding: 25px;
    border: solid 1px #e8ebf1;
    background-color: #ffffff;
    margin-bottom: 10px;
    position: relative;

    .new-badge {
      position: absolute;
      left: 0;
      top: 0;
      padding: 3px 8px;
      border-radius: 3px;
      background-color: #ecfff2;
      .text {
        font-size: 10px;
        font-weight: 500;
        letter-spacing: 1px;
        color: #54a36d;
        text-transform: uppercase;
      }
    }

    .attachments {
      width: 60px;
      height: 60px;
      border-radius: 8.6px;
      background-color: #f0f4ff;
    }

    .title-content {
      cursor: pointer;
      .title {
        width: 50vw;
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        color: #324056;
      }
      .description {
        width: 50vw;
        font-size: 14px;
        line-height: 1.43;
        letter-spacing: 0.5px;
        color: #324056;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .additional-info {
      .text {
        font-size: 13px;
        letter-spacing: 0.46px;
        color: #324056;
      }
    }
  }
}
</style>
