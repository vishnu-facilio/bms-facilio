<template>
  <div class="catalog-container" :class="isApp ? 'catalog-app-container' : ''">
    <div v-if="isLoading" class="loading-container d-flex mT70">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="d-flex height-100">
      <div
        class="catalog-groups-container"
        :class="isApp ? 'height-100' : ''"
        v-if="catalogGroups && catalogGroups.length > 2"
      >
        <div class="catalog-groups-header mT35 pL40 pR10">
          <span class="f13 fwBold fc-black-color text-uppercase break-all"
            >Service Categories</span
          >
        </div>
        <div class="catalog-groups">
          <div
            v-for="(catalog, index) in catalogGroups"
            :key="index"
            class="catalog-group pL40 fc-black-color f14 pR10"
            :class="[isActiveCatalog(catalog) ? 'active' : '']"
            @click="setActiveCatalog(catalog)"
          >
            <inline-svg
              src="svgs/folder"
              class="vertical-middle"
              iconClass="icon icon-sm-md mR10"
            ></inline-svg>
            <span class="vertical-middle">{{ catalog.name }}</span>
          </div>
        </div>
      </div>
      <div class="catalog-items-container" :class="isApp ? 'height-100' : ''">
        <div class="catalog-search mT25 mL35 mB30">
          <el-input
            class="catalog-search-input"
            clearable
            v-model="searchText"
            :placeholder="$t('common._common.search_catalog')"
            @clear="searchCatalogs(true)"
            @keydown.native.enter="searchCatalogs"
          ></el-input>
          <el-button
            class="catalog-search-btn mL-auto"
            @click="searchCatalogs(false)"
          >
            <span>{{ $t('common._common.search') }}</span>
          </el-button>
        </div>
        <div v-if="isCatalogLoading" class="loading-container d-flex mT30">
          <Spinner :show="isCatalogLoading"></Spinner>
        </div>
        <div
          class="d-flex flex-direction-column height-100 justify-center self-center mL35 mR25 mB40"
          v-else-if="$validation.isEmpty(filteredCatalogItems)"
        >
          <div class="d-flex flex-direction-column" style="margin-top: -100px;">
            <inline-svg
              :src="getIconSrc()"
              class="vertical-middle self-center"
              iconClass="icon icon-xxxxlg"
            ></inline-svg>
            <div class="catalog-empty-text">
              {{ emptyText }}
            </div>
            <div v-if="!isEmptySearch" class="d-flex justify-center">
              <el-button
                @click="redirectToSetup()"
                class="config-btn mT15"
                :class="isApp ? 'show' : 'hide'"
              >
                <span class="config-btn-text">{{
                  $t('servicecatalog.setup.configure_now')
                }}</span>
              </el-button>
            </div>
          </div>
        </div>
        <v-infinite-scroll
          v-else
          :loading="loadingMoreCatalogs"
          @bottom="loadCatalogsList({ loadMore: true })"
          :offset="10"
          class="catalog-scroll-container"
        >
          <div class="d-flex flex-direction-row mL35 mR25 flex-wrap mB40">
            <div
              v-for="(catalogItem, index) in filteredCatalogItems"
              :key="index"
              class="catalog-item-card mT10 position-relative"
            >
              <div class="d-flex flex-direction-column justify-content-center">
                <div class="self-center">
                  <inline-svg
                    v-if="$validation.isEmpty(catalogItem.photoUrl)"
                    src="vehicle-delivery"
                    class="vertical-middle catalog-icon"
                    iconClass="icons"
                  ></inline-svg>
                  <img
                    v-else
                    :src="catalogItem.photoUrl"
                    class="inline vertical-middle catalog-icon"
                  />
                </div>
                <div class="bold fc-black-color f16 mT30 catalog-name">
                  {{ catalogItem.name }}
                </div>
                <div class="f13 mT7 catalog-desc">
                  {{ catalogItem.description }}
                </div>
                <el-button
                  class="catalog-request-btn"
                  @click="redirectToCatalogRequest(catalogItem)"
                >
                  <span>{{ $t('common._common.request_now') }}</span>
                </el-button>
              </div>
            </div>
          </div>
          <div v-if="loadingMoreCatalogs" class="catalog-loading-more">
            <Spinner :show="loadingMoreCatalogs"></Spinner>
          </div>
        </v-infinite-scroll>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import VInfiniteScroll from 'v-infinite-scroll'
import CatalogListMixin from '@/mixins/catalogs/CatalogListMixin'

export default {
  props: ['isApp'],
  mixins: [CatalogListMixin],
  components: {
    Spinner,
    VInfiniteScroll,
  },
  async created() {
    this.isLoading = true
    await this.loadCatalogsData()
    await this.loadCatalogsList()
    this.isLoading = false
  },
  data() {
    return {
      catalogGroups: [],
      filteredCatalogItems: [],
      activeCatalog: null,
      searchText: null,
      isLoading: false,
      isCatalogLoading: false,
      loadingMoreCatalogs: false,
      perPage: 50,
      page: 1,
      allCatalogsLoaded: false,
      isEmptySearch: false,
    }
  },
  computed: {
    groupId() {
      let { $route } = this
      let { query } = $route
      let { groupId } = query
      return !isEmpty(groupId) ? Number(groupId) : null
    },
    emptyText() {
      let { isEmptySearch, searchText } = this
      let text = this.$t('servicecatalog.setup.emptytext_category_item')
      let appendText = isEmpty(searchText) ? '' : `with name ${searchText}`
      return isEmptySearch ? `${text} ${appendText}` : text
    },
  },
  methods: {
    getIconSrc() {
      let { isEmptySearch } = this
      return !isEmptySearch
        ? 'svgs/service-catalog-empty'
        : 'svgs/service-catalog-search'
    },
    setActiveCatalog(catalog) {
      let { groupId } = this
      this.$set(this, 'activeCatalog', catalog)
      this.$set(this, 'page', 1)
      this.$set(this, 'allCatalogsLoaded', false)
      if (!isEmpty(groupId)) {
        this.$router.replace({ query: {} })
      }
      this.loadCatalogsList({})
    },
    isActiveCatalog(catalog) {
      let { activeCatalog } = this
      let { id } = catalog
      if (!isEmpty(activeCatalog)) {
        return id === activeCatalog.id
      }
      return false
    },
    searchCatalogs(allowClear = false) {
      let { searchText } = this
      this.$set(this, 'page', 1)
      if (isEmpty(searchText) && allowClear) {
        this.loadCatalogsList({ forceFetch: true }).then(() => {
          this.$set(this, 'isEmptySearch', false)
        })
      } else if (searchText.length > 1) {
        this.loadCatalogsList({ forceFetch: true }).then(() => {
          let { filteredCatalogItems } = this
          this.$set(this, 'isEmptySearch', isEmpty(filteredCatalogItems))
        })
      }
    },
    redirectToCatalogRequest(catalog) {
      let { id: catalogId, groupId, name, externalURL } = catalog
      if (!isEmpty(externalURL)) {
        window.open(externalURL, '_blank', 'noopener,noreferrer')
      } else {
        this.$router.push({
          name: 'service-catalog-request',
          params: {
            catalogId,
          },
          meta: {
            groupId,
            name,
          },
        })
      }
    },
    redirectToSetup() {
      this.$router.push({
        name: 'catalog-setup-list',
      })
    },
  },
}
</script>
<style lang="scss"></style>
