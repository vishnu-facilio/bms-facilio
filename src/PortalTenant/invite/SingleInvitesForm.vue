<script>
import InviteForm from 'pages/visitors/invites/InviteSingleForm.vue'
export default {
  extends: InviteForm,

  methods: {
    afterSerializeHook({ data }) {
      let { visitorTypeId: id } = this
      data = {
        ...data,
        requestedBy: { id: this.$portaluser.ouid },
        visitorType: { id },
      }
      return data
    },
  },
}
</script>
