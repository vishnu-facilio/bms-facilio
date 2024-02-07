<template>
  <OldInviteGroupForm v-if="isOldGroupInvite"/>
  <InviteGroupForm v-else/>

</template>
<script>
import InviteGroupForm from 'src/pages/visitors/invites/InviteGroupForm.vue'
import OldInviteGroupForm from'src/pages/visitors/invites/OldInviteGroupForm.vue'
export default {
  components:{
    InviteGroupForm,
    OldInviteGroupForm,
  },
  data(){
    return{
      isOldGroupInvite:true,
    }
  },
  mounted() {
    this.IsOldGroupInvite()
  },
  methods :{
    IsOldGroupInvite(){
      if(!this.$helpers.isLicenseEnabled('GROUP_INVITES'))
      {
        this.isOldGroupInvite=true
      }
      else{
        this.isOldGroupInvite=false

      }
    }
  }

}
</script>
