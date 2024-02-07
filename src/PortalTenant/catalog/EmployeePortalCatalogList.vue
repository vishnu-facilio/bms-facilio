<template>
  <div class="sr-main-container height-100">
    <div v-if="isLoading" class="loading-container d-flex mT70">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="height-100">
      <div class="sr-header-container d-flex">
        <div class="d-flex flex-direction-row align-items">
          <div class="mR20">
            <el-select
              v-model="currentActiveCatalog"
              placeholder="Select"
              @change="getActiveCatalog"
            >
              <el-option
                v-for="(catalog, index) in catalogGroups"
                :key="index"
                :label="catalog.name"
                :value="catalog.id"
              >
              </el-option>
            </el-select>
          </div>
          <div>
            <el-input
              clearable
              class="search-input"
              prefix-icon="el-icon-search"
              v-model="searchText"
              :placeholder="$t('common._common.search_catalog')"
              @clear="searchCatalogs(true)"
              @keydown.native.enter="searchCatalogs"
            >
            </el-input>
          </div>
        </div>
        <div class="switch-class">
          <el-switch v-model="switchView" class="view-switch" :width="63">
          </el-switch>
          <div
            class="position-absolute toggle-left pointer"
            @click="switchView = !switchView"
          >
            <inline-svg
              src="svgs/icon-2-grid-view-black-24-dp"
              iconClass="icon-xs"
            ></inline-svg>
          </div>
          <div
            class="position-absolute toggle-right pointer"
            @click="switchView = !switchView"
          >
            <inline-svg src="svgs/icon-floors" iconClass="icon-xs"></inline-svg>
          </div>
        </div>
      </div>
      <div
        class="sr-catalog-items-container calc-height-100"
        :class="isApp ? 'height-100' : ''"
      >
        <div v-if="isCatalogLoading" class="loading-container d-flex mT30">
          <Spinner :show="isCatalogLoading"></Spinner>
        </div>
        <div
          v-else-if="$validation.isEmpty(filteredCatalogItems)"
          class="d-flex flex-direction-column height-100 justify-center self-center mL35 mR25 mB40"
        >
          <div class="d-flex flex-direction-column" style="margin-top: -100px;">
            <inline-svg
              :src="getIconSrc()"
              class="vertical-middle self-center"
              iconClass="icon icon-xxxxlg"
            ></inline-svg>
            <div class="catalog-empty-text self-center">
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
          <template v-if="!switchView">
            <div class="mL20 mR20 flex-wrap mB40 mT35">
              <div
                v-for="(catalogItem, index) in Object.keys(getCatalogItems)"
                :key="index"
                class="catalog-item-card position-relative "
              >
                <div class="catalog-header-name mB15">{{ catalogItem }}</div>
                <div class="d-flex flex-direction-row mB35">
                  <div
                    v-for="(catalog, index1) in getCatalogItems[catalogItem]"
                    :key="index1"
                    class="catalog-items "
                    @click="redirectToCatalogRequest(catalog)"
                  >
                    <div class="self-center img-container">
                      <inline-svg
                        v-if="$validation.isEmpty(catalog.photoUrl)"
                        src="vehicle-delivery"
                        class="vertical-middle icon-catalog"
                        iconClass="icon icon-38"
                      ></inline-svg>
                      <img
                        v-else
                        :src="catalog.photoUrl"
                        class="inline vertical-middle icon-catalog"
                      />
                    </div>
                    <div class="catalog-name mT5">
                      {{ catalog.name }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="flex-wrap mB40 mT35">
              <div
                v-for="(catalogItem, index) in Object.keys(getCatalogItems)"
                :key="index"
                class="catalog-item-card position-relative "
              >
                <div class="list-catalog-header-name mB15 mL20">
                  {{ catalogItem }}
                </div>
                <div class="d-flex flex-direction-column mB25">
                  <div
                    v-for="(catalog, index1) in getCatalogItems[catalogItem]"
                    :key="index1"
                    class="list-catalog-items pL20 pR20 "
                    @click="redirectToCatalogRequest(catalog)"
                  >
                    <div class="flex-direction-row d-flex mT10">
                      <div class="list-img-container">
                        <inline-svg
                          v-if="$validation.isEmpty(catalog.photoUrl)"
                          src="vehicle-delivery"
                          class="vertical-middle icon-catalog"
                          iconClass="icon icon-38"
                        ></inline-svg>
                        <img
                          v-else
                          :src="catalog.photoUrl"
                          class="inline vertical-middle icon-catalog"
                        />
                      </div>
                      <div class="list-catalog-name mL20">
                        {{ catalog.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
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
import CatalogList from 'src/PortalTenant/catalog/CatalogList.vue'

export default {
  extends: CatalogList,
  data() {
    return {
      currentActiveCatalog: 'All',
      switchView: false,
    }
  },
  mounted() {},
  computed: {
    getCatalogItems() {
      let { filteredCatalogItems, catalogGroups } = this
      let obj = {}
      catalogGroups.forEach(element => {
        let catalogs = filteredCatalogItems.filter(
          value => value.groupId === element.id
        )
        if (!isEmpty(catalogs)) {
          obj[element.name] = catalogs
        }
      })
      return obj
    },
  },
  methods: {
    getActiveCatalog(catalogId) {
      let { catalogGroups } = this
      let catalog = catalogGroups.find(element => element.id === catalogId)
      if (!isEmpty(catalog)) {
        this.setActiveCatalog(catalog)
      }
    },
  },
}
</script>
<style scoped>
.switch-class {
  display: flex;
  align-items: center;
  position: relative;
}
.align-items {
  align-items: center;
}
.icon-catalog {
  height: 100%;
  width: 100%;
}
.sr-main-container {
  background-color: #fafbfc;
}
.toggle-left {
  left: 8px;
  z-index: 1;
  top: 18px;
  height: 16px;
}
.toggle-right {
  right: 8px;
  z-index: 1;
  top: 18px;
  height: 16px;
}
.calc-height-100 {
  height: calc(100% - 100px);
}
.mB35 {
  margin-bottom: 35px;
}
.mT35 {
  margin-top: 35px;
}
.sr-header-container {
  height: 62px;
  padding: 5px 20px;
  background: #fff;
  border-bottom: 0.5px solid #ededed;
  position: sticky;
  top: 0;
  z-index: 1;
  justify-content: space-between;
}
.catalog-header-name {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
}
.list-catalog-header-name {
  font-size: 12px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #483db6;
}

.list-catalog-items {
  background-color: #fff;
  height: 60px;
  margin-bottom: 5px;
  cursor: pointer;
}
.list-catalog-items:hover {
  background-color: #fff;
  height: 60px;
  margin-bottom: 5px;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
}
.catalog-items {
  margin: 0 36px 0 0;
  padding: 15px;
  border-radius: 4px;
  border: solid 1px #ededed;
  background-color: #fff;
  height: 120px;
  width: 100px;
  cursor: pointer;
}
.catalog-items:hover {
  margin: 0 36px 0 0;
  padding: 15px;
  border-radius: 4px;
  border: solid 1px #ededed;
  background-color: #fff;
  height: 120px;
  width: 100px;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
}

.catalog-name {
  font-size: 0.73rem;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #12324a;
  margin-top: 7px;
}
.list-catalog-name {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #12324a;
  margin-top: 13px;
}
.list-img-container {
  width: 40px;
  height: 40px;
  border: solid 1px #dcd9ff;
  border-radius: 20px;
  background-color: #f8f7ff;
}
.img-container {
  width: 40px;
  height: 40px;
  margin: auto;
  border: solid 1px #dcd9ff;
  border-radius: 20px;
  background-color: #f8f7ff;
}
</style>
<style>
.view-switch .el-switch__core {
  border-radius: 15px;
}
.search-input.el-input.el-input--prefix.el-input--suffix {
  width: 250px;
}
.view-switch.is-checked .el-switch__core::after {
  left: 47px;
  margin-left: -17px;
}
.view-switch span.el-switch__core {
  height: 26px;
}
.el-switch.is-checked .el-switch__core {
  border-color: #dcdfe6;
  background-color: #dcdfe6;
}
.sr-header-container input.el-input__inner {
  height: 36px !important;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #452d88;
  border: solid 1px #d0d9e2 !important;
  background-color: #ffffff;
  border-radius: 4px;
  padding: 0 10px;
}
.sr-header-container input.el-input__inner:hover {
  border-color: #0053cc !important;
}
.sr-header-container input.el-input__inner:focus {
  border-color: #0053cc !important;
}

.sr-header-container .el-select {
  height: 36px;
  width: 250px;
}
.search-input input.el-input__inner {
  padding: 0 30px;
  border-bottom: 0;
  height: 36px !important;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #452d88;
  border: solid 1px #d0d9e2 !important;
  background-color: #ffffff;
  border-radius: 4px;
}
.search-input input.el-input__inner:hover {
  border-color: #0053cc !important;
}
.search-input i.el-input__icon.el-icon-search {
  line-height: 36px;
}
.view-switch .el-switch__core:after {
  content: '';
  position: absolute;
  top: -4px;
  left: -1px;
  border-radius: 100%;
  -webkit-transition: all 0.3s;
  transition: all 0.3s;
  width: 32px;
  height: 32px;
  box-shadow: 0 0 7px 2px rgba(0, 0, 0, 0.16);
}
.sr-header-container .el-input__suffix-inner .el-input__icon {
  line-height: 32px;
}
.search-input .el-input__prefix {
  display: flex;
  align-items: center;
}
</style>
