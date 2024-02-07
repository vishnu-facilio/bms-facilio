<template>
  <div class="weather-station-search">
    <div
      class="flex-middle position-relative"
      :class="{ mR30: !isSearchDisabled }"
    >
      <i
        v-if="showSearchIcon"
        class="el-icon-search pointer fw6 fc-black3-16"
        :class="{ 'cursor-not-allowed': isSearchDisabled }"
        @click="openSearchInput"
      ></i>
      <div v-if="showSearchInput">
        <el-input
          :placeholder="$t('common._common.search')"
          v-model="searchData"
          class="fc-input-full-border2"
          @change="updateSearchData"
        >
        </el-input>
        <div>
          <i
            class="el-icon-close fc-close-icon-search pointer"
            @click="closeSearchInput"
          >
          </i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'WeatherStationSearch',
  props: ['value', 'isSearchDisabled'],
  data() {
    return {
      showSearchIcon: true,
      showSearchInput: false,
      searchtext: null,
    }
  },
  computed: {
    searchData: {
      get() {
        return this.value
      },
      set(newVal) {
        if (!isEmpty(newVal)) {
          this.$emit('input', newVal)
        } else {
          this.$emit('input', newVal)
          this.$emit('updateSearchData', newVal)
        }
      },
    },
  },
  methods: {
    openSearchInput() {
      if (!this.isSearchDisabled) {
        this.showSearchIcon = false
        this.showSearchInput = true
      }
    },
    closeSearchInput() {
      this.showSearchIcon = true
      this.showSearchInput = false
      this.$emit('input', null)
      this.$emit('updateSearchData', null)
    },
    updateSearchData(newVal) {
      this.$emit('updateSearchData', newVal)
    },
  },
}
</script>

<style lang="scss" scoped>
.weather-station-search {
  .cursor-not-allowed {
    cursor: not-allowed;
  }
}
</style>
