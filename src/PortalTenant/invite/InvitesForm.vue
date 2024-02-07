<template>
  <!-- eslint-disable-next-line vue/require-component-is -->
  <component :is="inviteForm" :beforeSaveHook="beforeSaveHook"></component>
</template>
<script>
/* eslint-disable vue/no-unused-components */

import InviteForm from 'pages/visitors/invites/InviteForm.vue'
export default {
  extends: InviteForm,
  components: {
    SingleForm: () => import('PortalTenant/invite/SingleInvitesForm.vue'),
    GroupForm: () => import('PortalTenant/invite/GroupInvitesForm.vue'),
    OldGroupForm: () => import('PortalTenant/invite/OldGroupInvitesForm.vue'),
  },
}
</script>
