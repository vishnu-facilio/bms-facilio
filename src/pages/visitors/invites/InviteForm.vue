<template>
  <!-- eslint-disable-next-line vue/require-component-is -->
  <component :is="inviteForm" :beforeSaveHook="beforeSaveHook"></component>
</template>
<script>
/* eslint-disable vue/no-unused-components */

import { isEmpty } from '@facilio/utils/validation'
import FormMixin from '@/mixins/FormMixin'
export default {
  mixins: [FormMixin],
  components: {
    SingleForm: () => import('pages/visitors/invites/InviteSingleForm.vue'),
    GroupForm: () => import('pages/visitors/invites/InviteGroupForm.vue'),
    OldGroupForm:() => import('pages/visitors/invites/OldInviteGroupForm.vue')

  },
  computed: {
    inviteForm() {
      let { query } = this.$route
      let { formMode } = query || {}

      if (!isEmpty(formMode)) {
        if (formMode === 'single') {
          this.isFormSaved = true
          return 'SingleForm'
        } else {
           if(this.$helpers.isLicenseEnabled('GROUP_INVITES')){
            return 'GroupForm'
           }
           else
           return 'OldGroupForm'
        }

      } else {
        this.isFormSaved = true
        return 'SingleForm'
      }
    },
  },
  methods: {
    beforeSaveHook() {
      this.isFormSaved = true
    },
  },
}
</script>
