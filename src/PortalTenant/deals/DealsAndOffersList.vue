<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.dealsandoffers.deals_offer') }}
    </template>
    <template slot="header">
      <template v-if="!isEmpty(viewname)">
        <AdvancedSearchWrapper
          :key="`ftags-list-${moduleName}`"
          :filters="filters"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :hideSaveView="true"
        ></AdvancedSearchWrapper>
      </template>
      <CreateButton @click="redirectToFormCreation">
        {{ $t('tenant.dealsandoffers.add') }}
      </CreateButton>
    </template>
    <template slot="header-2">
      <pagination
        :total="recordCount"
        :perPage="perPage"
        :currentPageCount="currentPageCount"
      ></pagination>
    </template>
    <div class="overflow-hidden">
      <spinner
        v-if="showLoading"
        :show="true"
        size="80"
        class="align-center"
      ></spinner>
      <div v-else-if="$validation.isEmpty(records)" class="list-container">
        <div class="list-empty-state">
          <inline-svg
            src="svgs/community-empty-state/deals"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="q-item-label nowo-label">
            {{ $t('tenant.dealsandoffers.no_data') }}
          </div>
        </div>
      </div>
      <div v-else class="p20 d-flex flex-row overflow-scroll neigh-list-cards">
        <div
          v-for="record in records"
          :key="record.id"
          class="container deals flex-col flex-middle mR20 mB20"
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
                src="svgs/community-features/deals-list"
                iconClass="icon icon icon-xxlg vertical-middle object-contain"
              ></inline-svg>
            </template>
          </ListAttachments>
          <div class="p20 width100">
            <router-link
              class="title-content d-flex flex-col pointer"
              :to="redirectToOverview(record.id)"
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

            <div class="mT20 d-flex flex-col deals-info">
              <div class="expiry-date mT20 flex flex-row">
                <inline-svg
                  src="svgs/community-features/clock2"
                  iconClass="icon icon icon-sm-md vertical-middle object-contain mR15"
                ></inline-svg>
                <template v-if="record.expiryDate">
                  {{ $t('tenant.dealsandoffers.expire') }}
                  {{ $options.filters.formatDate(record.expiryDate, true) }}
                </template>
                <div v-else class="fc-txt-color-grey2">
                  Not mentioned
                </div>
              </div>
              <div
                v-if="$getProperty(record, 'neighbourhood.location.lat')"
                class="mT15 flex-middle"
              >
                <inline-svg
                  src="svgs/community-features/location"
                  iconClass="icon icon icon-sm-md vertical-middle object-contain mR18"
                ></inline-svg>
                <div
                  @click="
                    $helpers.openInMap(
                      $getProperty(record, 'neighbourhood.location.lat'),
                      $getProperty(record, 'neighbourhood.location.lng')
                    )
                  "
                  class="label-txt-black5 textoverflow-ellipsis"
                >
                  {{ $getProperty(record, 'neighbourhood.title') }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <router-view @refreshList="refreshList()" />
  </PageLayout>
</template>
<script>
import ListAttachments from '@/relatedlist/ListAttachmentPreview'
import ModuleList from 'PortalTenant/custom-module/ModuleList'

export default {
  extends: ModuleList,
  props: ['attachmentsModuleName'],
  components: {
    ListAttachments,
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
    .deals-info {
      border-top: solid 1px #edf0f3;
      .expiry-date {
        font-size: 13px;
        letter-spacing: 0.46px;
        color: #e68829;
      }
    }
  }
}
</style>
