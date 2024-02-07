<template>
  <div class="container-fluid  h100 overflow-auto">
    <gridstack-layout
      :layout.sync="sectionCopy"
      rowHeight="15px"
      :column="12"
      :minRows="0"
      :disableOneColumnMode="true"
      :disableResize="true"
      :float="false"
      :static="true"
      :margin="10"
    >
      <gridstack-item
        v-for="(section, index) in sectionCopy"
        :key="section.id"
        :x="section.x"
        :y="section.y"
        :id="section.id"
        :w="section.w"
        :h="section.h"
        :noScroll="true"
      >
        <section-header
          v-if="section.hasOwnProperty('children')"
          :name="section.name"
          :infoMsg="section.infoMsg"
        >
          <gridstack-section
            :id="section.id"
            :item="section"
            rowHeight="15px"
            :margin="10"
            :class="{ 'shadow-widgets': index > 0 }"
          >
            <!-- This is wrong, keeping the positions inside layoutParams property, the correct method should be to use widget.x not widget.layoutParam.x, this is
            a static layout, so this is fine. When widgets are moved the lib will break. -->
            <gridstack-item
              v-for="widget in section.children"
              :id="widget.id"
              :x="widget.layoutParams.x"
              :y="widget.layoutParams.y"
              :w="widget.layoutParams.w"
              :h="widget.layoutParams.h"
              :key="widget.id"
              :className="widget.class"
              :itemContentCustomClass="widget.class"
            >
              <HomePageWidget
                class="widget-items"
                :widget="widget"
              ></HomePageWidget>
            </gridstack-item>
          </gridstack-section>
        </section-header>
      </gridstack-item>
    </gridstack-layout>
  </div>
</template>
<script>
import SectionHeader from './SectionHeader.vue'
import HomePageWidget from 'src/components/homepage/HomePagewidgets.vue'
import {
  GridstackItem,
  GridstackLayout,
  GridstackSection,
} from '@facilio/ui/dashboard'
import cloneDeep from 'lodash/cloneDeep'
export default {
  components: {
    HomePageWidget,
    GridstackLayout,
    GridstackItem,
    GridstackSection,
    SectionHeader,
  },
  data() {
    return {
      sectionCopy: [],
    }
  },
  created() {
    this.sectionCopy = cloneDeep(this.sections) // Our gridstack lib will overwrite the prop which is passed to it, we don't want the prop which is passed to get updated.
  },
  mounted() {},
  props: {
    sections: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  methods: {},
}
</script>
<style scoped>
.parent1 {
  background-color: #f7f8f9;
}
.grid-widget {
  background-color: #f7f8f9;
}
.grid-stack-item-content {
  background-color: #f7f8f9;
}
.section-content {
  background-color: #f7f8f9;
}
</style>
<style lang="scss">
@import 'gridstack/dist/gridstack.min.css';
// Unscoped css to provide css rules to a component which is generic (GridstackItem).
.homepage-shadow-widget {
  box-shadow: 0 1px 7px 0 rgb(0 0 0 / 9%);
}
</style>
