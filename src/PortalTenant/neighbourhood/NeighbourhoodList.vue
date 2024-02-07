<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.neighbourhood.neighbourhood') }}
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
        {{ $t('tenant.neighbourhood.add') }}
      </CreateButton>
    </template>
    <template slot="header-2">
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
        src="svgs/community-empty-state/neighbourhood"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('tenant.neighbourhood.no_data') }}
      </div>
    </div>
    <div v-else class="p10 d-flex flex-row overflow-scroll neigh-list-cards">
      <div
        v-for="record in records"
        :key="record.id"
        class="container neighbourhood flex-col flex-middle mR20 mB20"
      >
        <ListAttachments
          :record="record"
          :module="attachmentsModuleName"
          class="width100"
          customClass="neighbourhood-list-preview mR30"
        >
          <template slot="no-image">
            <InlineSvg
              src="clip"
              iconClass="icon fill-grey icon-xxlll vertical-middle object-contain fill-blue4"
            ></InlineSvg>
          </template>
          <template slot="no-data">
            <inline-svg
              src="svgs/community-features/neighbourhood-list"
              iconClass="icon icon icon-xxlg vertical-middle object-contain"
            ></inline-svg>
          </template>
        </ListAttachments>
        <div v-if="record.activeDealsCount > 0" class="deals-count">
          <inline-svg
            src="svgs/community-features/neighbourhood-deals"
            iconClass="icon icon icon-sm-md vertical-middle object-contain mR15"
          ></inline-svg>
          <span class="text">{{
            `${record.activeDealsCount} ${
              record.activeDealsCount > 1 ? `DEALS` : `DEAL`
            } AVAILABLE`
          }}</span>
        </div>
        <div class="p20 width100">
          <router-link
            :to="redirectToOverview(record.id)"
            class="title-content d-flex flex-col pointer"
          >
            <div class="label-txt-black bold textoverflow-ellipsis">
              {{ record.title }}
            </div>
            <div
              v-if="record.description"
              class="label-txt-black mT10 description"
            >
              {{ record.description | htmlToText }}
            </div>
            <div v-else class="fc-txt-color-grey2 mT10 description">
              ---
            </div>
          </router-link>
          <div class="mT20 d-flex flex-col">
            <div
              v-if="record.category"
              class="neighbourgood-category-chip width-fit"
            >
              <div class="chip-text">
                {{ categoryEnumMap[record.category] || '---' }}
              </div>
            </div>
            <div v-if="record.location" class="mT15 flex-middle">
              <inline-svg
                src="svgs/community-features/location"
                iconClass="icon icon icon-sm-md vertical-middle object-contain mR10"
              ></inline-svg>
              <div
                @click="
                  $helpers.openInMap(
                    $getProperty(record, 'location.lat'),
                    $getProperty(record, 'location.lng')
                  )
                "
                class="label-txt-blue4 pointer textoverflow-ellipsis"
              >
                {{
                  `${$getProperty(record, 'location.lat')}, ${$getProperty(
                    record,
                    'location.lng'
                  )}`
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <router-view @refreshList="loadRecords(true)" />
  </PageLayout>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import ListAttachments from '@/relatedlist/ListAttachmentPreview'
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  props: ['attachmentsModuleName'],
  components: {
    ListAttachments,
  },
  computed: {
    ...mapState({
      meta: state => state.view.metaInfo,
    }),
    categoryEnumMap() {
      let { fields } = this.meta || {}
      let categoryField = (fields || []).find(
        field => field.name === 'category'
      )

      return this.$getProperty(categoryField, 'enumMap', {})
    },
  },
  watch: {
    currentView: {
      async handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.isLoading = true
          await Promise.all([
            this.$store.dispatch('view/loadModuleMeta', this.moduleName),
            this.getViewDetail(),
            this.loadRecords(),
          ])
          this.isLoading = false
        }
      },
      immediate: true,
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.isLoading) {
        this.loadRecords(true)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.neigh-list-cards {
  flex-wrap: wrap;
  height: 100%;
  .container {
    width: 300px;
    position: relative;
    border-radius: 10px;
    border: solid 1px #e8ebf1;
    background-color: #ffffff;
    &.neighbourhood {
      height: 370px;
    }
    &.deals {
      height: 383px;
    }
    .deals-count {
      position: absolute;
      top: 103px;
      background-color: #438dff;
      width: 100%;
      padding: 10px 15px;
      .text {
        font-size: 11px;
        font-weight: bold;
        letter-spacing: 0.92px;
        color: #ffffff;
      }
    }
    .title-content {
      height: 110px;
      .description {
        line-height: 1.43;
        -webkit-line-clamp: 4;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}
</style>
