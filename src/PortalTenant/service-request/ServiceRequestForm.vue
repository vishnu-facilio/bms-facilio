<script>
import ServiceRequestForm from 'pages/servicerequest/ServiceRequestForm.vue'

export default {
  extends: ServiceRequestForm,

  methods: {
    afterSerializeHook({ data }) {
      data = {
        ...data,
        requestedBy: { id: this.$portaluser.ouid },
        sourceType: 1,
      }
      return data
    },
  },
}
</script>
