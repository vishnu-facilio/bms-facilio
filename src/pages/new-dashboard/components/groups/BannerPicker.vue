<template>
  <el-radio-group v-model="selectedBanner">
    <el-row v-for="banner in banners" :key="banner.label" class="banner-input">
      <el-radio
        class="fc-radio-btn"
        :label="banner.label"
        @change="updateBanner(banner)"
      >
        <div
          class="banner"
          :style="{
            'background-image': `url(${require(`src/assets/svgs/dashboard/banners/${banner.label}.svg`)})`,
            'background-size': 'cover',
          }"
        ></div
      ></el-radio>
    </el-row>
  </el-radio-group>
</template>

<script>
import { cloneDeep, isEqual } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
const FIRST_ITEM_INDEX = 0
export default {
  model: {
    prop: 'banner',
    event: 'change',
  },
  props: {
    banner: {
      type: Object,
      required: true,
    },
  },
  created() {
    this.setDefaultBanner()
  },
  data() {
    return {
      banners: [
        {
          label: 'voilet',
          borderColor: '#bc5bb1',
          backgroundColor: '#FBF9FF',
        },
        {
          label: 'orange',
          borderColor: '#e9913b',
          backgroundColor: '#FFFDFB',
        },
        {
          label: 'teal',
          borderColor: '#4a798c',
          backgroundColor: '#F9FDFF',
        },
        {
          label: 'red',
          borderColor: '#ff6c6a',
          backgroundColor: '#FFF7F7',
        },
        {
          label: 'dark-green',
          borderColor: '#458462',
          backgroundColor: '#F9FFFB',
        },
        {
          label: 'purple',
          borderColor: '#644d9e',
          backgroundColor: '#FFFAFE',
        },
        {
          label: 'light-green',
          borderColor: '#70b599',
          backgroundColor: '#F4FFFA',
        },
        {
          label: 'blue',
          borderColor: '#487cd8',
          backgroundColor: '#FAFBFF',
        },
        // {
        //   label: 'test1',
        //   borderColor: '#487cd8',
        // },
        // {
        //   label: 'test2',
        //   borderColor: '#487cd8',
        // },
      ],

      selectedBanner: {},
    }
  },
  methods: {
    updateBanner(banner) {
      this.$emit('change', banner)
    },
    setDefaultBanner() {
      const { banner, banners } = this
      const { label } = banners.find(b => isEqual(b, banner)) ?? {}
      if (!isEmpty(label)) {
        this.selectedBanner = label
      } else {
        this.selectedBanner = banners[FIRST_ITEM_INDEX].label
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.banner {
  width: 544px;
  height: 36px;
  position: absolute;
  top: -7px;
  left: 35px;
}
.banner-input {
  margin: 25px 0;
}
</style>
