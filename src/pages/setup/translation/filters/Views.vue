<template>
  <div class="filter">
    <FilterDropdown
      v-if="!loadingViews"
      v-model="activeView"
      :options="options"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40"></div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FilterDropdown from './FilterDropdown.vue'
import { isEmpty } from '@facilio/utils/validation'
const portfolioCustomModuleList = ['site', 'space', 'floor', 'building']
export default {
  props: ['moduleName', 'appId', 'setFilter'],
  components: {
    FilterDropdown,
  },
  data() {
    return {
      viewList: [],
      activeView: null,
      loadingViews: true,
      portfolioCustomModuleList,
    }
  },
  created() {
    this.loadViews()
  },
  watch: {
    moduleName: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadViews()
        }
      },
      immediate: true,
    },
    activeView: function() {
      let { moduleName, activeView, portfolioCustomModuleList } = this
      let filter = {
        loading: false,
        query: {
          viewId: activeView.toString(),
        },
      }
      if (portfolioCustomModuleList.includes(moduleName)) {
        filter.query = { moduleName, ...filter.query }
      }
      this.setFilter(filter)
    },
  },
  computed: {
    options() {
      const op = this.viewList.map(view => {
        return {
          id: view.id,
          label: view.displayName,
        }
      })
      return op
    },
  },
  methods: {
    setFilterNotLoading() {
      this.setFilter({
        loading: false,
        query: null,
      })
    },
    async loadViews() {
      this.loadingViews = true
      const { error, data } = await API.get('v2/translationsetup/viewList', {
        moduleName: this.moduleName,
        appId: this.appId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const { groupViews } = data
        if (!isEmpty(groupViews)) {
          let list = []
          groupViews.forEach(groupView => {
            const { views } = groupView
            const data = (views || []).map(view => {
              const { displayName, id } = view
              return {
                displayName,
                id,
              }
            })
            list = [...list, ...data]
          })
          this.viewList = list
          const [view] = list || []
          const { id } = view || {}
          if (id) {
            this.activeView = id
          } else {
            this.setFilterNotLoading()
          }
        } else {
          this.setFilterNotLoading()
        }
      }
      this.loadingViews = false
    },
  },
}
</script>

<style lang="scss" scoped>
.filter {
  margin: 0 40px;
  padding: 10px 0;
}
</style>
