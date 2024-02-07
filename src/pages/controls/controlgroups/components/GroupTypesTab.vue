<template>
  <div>
    <div v-if="isLoading" class="mL10 mT70">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else-if="sections" class="group-type-container">
      <div
        v-for="(section, index) in sections"
        :key="index"
        class="group-section-container"
      >
        <div class="d-flex justify-between pB10">
          <div>
            <div class="bold f16">{{ section.name }}</div>
            <div class="pT5 pB5 f12">{{ typeMap[section.type - 1] }}</div>
          </div>
        </div>
        <el-collapse-transition>
          <div>
            <template v-for="(category, index) in section.categories">
              <CategoryTable
                :key="index"
                :category="category"
                :group="group"
                :isSummary="true"
                class="pB20"
              />
            </template>
          </div>
        </el-collapse-transition>
      </div>
    </div>
  </div>
</template>

<script>
import CategoryTable from './CategoryTable'
export default {
  props: ['group', 'groupSections', 'isLoading'],
  components: { CategoryTable },
  data() {
    return {
      typeMap: ['HVAC', 'Lightning', 'Elevator'],
      controlPointsList: null,
    }
  },
  computed: {
    sections() {
      let { groupSections } = this
      return groupSections.map(section => {
        let currCategories = section.categories.map(category => {
          let { assetCategory, controlAssets } = category
          let { controlFields } = controlAssets[0]
          return {
            assetList: controlAssets,
            type: assetCategory,
            controlPoints: controlFields,
          }
        })
        return { ...section, isCollapsed: true, categories: currCategories }
      })
    },
  },
}
</script>

<style scoped>
.group-section-container {
  background-color: #ffffff;
  width: 831px;
  padding: 20px 30px 8px 30px;
  margin-bottom: 10px;
}
.group-type-container {
  height: calc(100vh - 200px) !important;
  overflow: scroll;
}
</style>
