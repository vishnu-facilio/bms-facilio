<template>
  <div class="custom-modules-details">
    <div class="height100 overflow-hidden">
      <div class="container-scroll pT10">
        <ModulesListTemplate
          :isLoading="isLoading"
          :moduleList="moduleList"
          @redirect="redirectToModule"
          @openModuleCreation="openModuleCreation"
          :allowModulesEdit="false"
        ></ModulesListTemplate>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import ModulesListTemplate from 'pages/setup/modules/ModulesListTemplate'

export default {
  components: { ModulesListTemplate },

  data() {
    return {
      isLoading: true,
    }
  },

  created() {
    this.$store.dispatch('loadAssetCategory')
  },

  computed: {
    ...mapState({
      assetcategory: state => state.assetCategory,
    }),
    moduleList() {
      let subModules = []
      if (!isEmpty(this.assetcategory)) {
        subModules = this.assetcategory
      }
      return subModules
    },
  },

  watch: {
    assetcategory: {
      handler(newVal) {
        if (!isEmpty(newVal)) this.isLoading = false
      },
      immediate: true,
    },
  },

  methods: {
    openModuleCreation() {},
    redirectToModule(module) {
      let { moduleName } = module
      let currentPath = this.$router.resolve({
        name: 'modules-details',
        params: { moduleName },
      }).href

      this.$router.push({
        path: `${currentPath}/layouts`,
      })
    },
  },
}
</script>
