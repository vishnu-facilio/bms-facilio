<script>
import CommentInput from 'src/components/notes/CommentInput.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CommentInput,
  props: ['value', 'parentModule', 'recordId'],
  created() {
    this.initiateCommentService()
  },
  data() {
    return {
      expandTextBox: true,
      canShowAvatar: false,
    }
  },
  computed: {
    isFormComments() {
      return true
    },
    forWO() {
      let { parentModule } = this
      return parentModule === 'workorder'
    },
  },
  methods: {
    inputHandler() {
      let comment = this.setupNewComment()
      if (isEmpty(comment?.body)) {
        this.$emit('input', {})
      } else {
        this.$emit('input', comment)
        this.$emit('clearError')
      }
    },
    async initiateCommentService() {
      this.CommentService.parentModule = this.parentModule
      this.CommentService.forWO = this.forWO
      this.$set(this.CommentService.record, 'id', this.recordId)
      if (this.CommentService.canShowSharing) {
        await this.CommentService.getSharingApps()
        this.commentSharing = this.CommentService.defaultSharingApps
      }
    },
  },
}
</script>

<style></style>
