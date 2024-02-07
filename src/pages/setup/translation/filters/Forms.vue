<template>
  <div class="filter">
    <FilterDropdown
      v-if="!loadingForms"
      v-model="activeForm"
      :options="options"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40 bR5"></div>
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
      formList: [],
      portfolioCustomModuleList,
      activeForm: null,
      loadingForms: true,
    }
  },
  created() {
    this.loadForms()
  },
  watch: {
    moduleName: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadForms()
        }
      },
      immediate: true,
    },
    activeForm: function() {
      let { moduleName, activeForm, portfolioCustomModuleList } = this
      let filter = {
        loading: false,
        query: {
          formId: activeForm.toString(),
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
      return this.formList.map(form => {
        return {
          id: form.id,
          label: form.displayName,
        }
      })
    },
  },
  methods: {
    async loadForms() {
      this.loadingForms = true
      const { error, data } = await API.get('v2/translationsetup/forms', {
        moduleName: this.moduleName,
        appId: this.appId,
        fetchDisabledForms: true,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.setFilter({
          loading: false,
          query: null,
        })
      } else {
        const { forms } = data
        if (!isEmpty(forms)) {
          this.formList = forms
          this.activeForm = this.formList[0].id
        } else {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingForms = false
    },
  },
}
</script>

<style scoped>
.filter {
  margin: 0 40px;
  padding: 10px 0;
}
</style>
