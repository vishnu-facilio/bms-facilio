<script>
import ViewCreationForm from 'src/newapp/viewmanager/ViewCreationForm.vue'
import { isWebTabsEnabled } from '@facilio/router'

export default {
  props: ['moduleName'],
  extends: ViewCreationForm,
  methods: {
    getModuleListRoute() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        return {
          name:
            moduleName === 'building'
              ? 'building-portfolio-module'
              : 'site-portfolio-module',
          params: { moduleName },
        }
      } else {
        return {
          name:
            moduleName === 'building'
              ? 'buildings-portfolio-home'
              : 'sites-portfolio-home',
          params: { moduleName },
        }
      }
    },
    getViewManagerRoute() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        return { name: 'portfolio-viewsmanager-webtab', params: { moduleName } }
      } else {
        return { name: 'portfolio-viewmanager', params: { moduleName } }
      }
    },
  },
}
</script>
