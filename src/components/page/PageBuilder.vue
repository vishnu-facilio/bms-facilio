<template>
  <component
    v-if="showPage && pagebuilderComponent"
    v-bind="$attrs"
    :is="pagebuilderComponent"
    :module="module"
    @forceOldPage="forceOld = true"
  ></component>
</template>
<script>
import { mapState } from 'vuex'

export default {
  props: ['module'],
  data() {
    return {
      forceOld: false,
      showPage: false,
    }
  },
  components: {
    NewPageBuilder: () =>
      import('src/newapp/components/pagebuilder/NewPageBuilder'),
    OldPageBuilder: () => import('./PageBuilderDep'),
  },
  async created() {
    try {
      if (this.$helpers.isLicenseEnabled('PAGE_BUILDER'))
        await this.$store.dispatch('pagebuilder/loadNewPageEnabledModules')
    } catch (e) {
      //API error handling
    }
    this.$nextTick(() => {
      this.showPage = true
    })
  },
  computed: {
    ...mapState({
      newPageEnabledModules: state => state.pagebuilder.newPageEnabledModules,
    }),
    pagebuilderComponent() {
      let { newPageEnabledModules, module, forceOld } = this
      let isNew = (newPageEnabledModules || []).includes(module)
      let showNew = !forceOld && (this.showNewPageBuilder || isNew)
      return showNew ? 'NewPageBuilder' : 'OldPageBuilder'
    },
    showNewPageBuilder() {
      let { query } = this.$route
      let { showNewPageBuilder = false } = query
      return showNewPageBuilder === true || showNewPageBuilder === 'true'
    },
  },
}
</script>
