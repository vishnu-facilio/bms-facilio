<script>
import { loadApps, getFilteredApps } from 'util/appUtil'
import { mapGetters } from 'vuex'
import { API } from '@facilio/api'
export default {
  props: [
    'module',
    'record',
    'notify',
    'parentModule',
    'isServicePortal',
    'setNoOfNotesLength',
  ],
  async created() {
    await this.availablePortalApps()
    if (this.canShowSharing) {
      this.newComment.commentSharing = this.defaultSharingApps
    }
    this.loadComments()
  },
  data() {
    return {
      isAddingNotes: false,
      loading: true,
      selectedPortal: [],
      portalApps: [],
      defaultSharingApps: [],
      comments: [],
      newComment: {
        parentModuleLinkName: this.module,
        parentId: this.record.id,
        body: null,
        notifyRequester: false,
        commentSharing: [],
      },
      commentFocus: false,
    }
  },
  watch: {
    record: function(recordObj) {
      this.loadComments()
    },
    comments: function(newVal) {
      if (
        this.record &&
        typeof this.record === 'object' &&
        this.setNoOfNotesLength
      ) {
        //this.record.noOfNotes = this.comments.length
        this.setNoOfNotesLength(this.comments.length)
      }
    },
  },
  computed: {
    showNotifyRequester() {
      if (typeof this.notify !== 'undefined') {
        return this.notify
      }
      return true
    },
    ...mapGetters(['getCurrentUser']),
  },
  methods: {
    async availablePortalApps() {
      let { error, data } = await API.get(
        '/v3/commentPreferences/sharingOptions'
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let { licensedPortalApps, commentSharingPreferences } = data
        this.portalApps = licensedPortalApps
        this.defaultSharingApps = commentSharingPreferences
      }
    },
    async loadComments() {
      if (!this.record || typeof this.record !== 'object') {
        return
      }
      this.loading = true
      this.newComment.parentId = this.record.id

      let url = '/note/get?module='
      if (this.isServicePortal) {
        url = '/notelist?module='
      }

      return this.$http
        .get(
          url +
            this.module +
            '&parentId=' +
            this.record.id +
            '&parentModuleName=' +
            this.parentModule
        )
        .then(response => {
          this.loading = false
          this.comments = response.data
        })
        .catch(error => {
          this.loading = false
          this.comments = []
        })
    },
    reset() {
      this.newComment = {
        parentModuleLinkName: this.module,
        parentId: this.record.id,
        body: null,
        notifyRequester: false,
      }
      if (this.canShowSharing) {
        this.newComment.commentSharing = this.defaultSharingApps
      }
    },
    async addComment() {
      if (this.newComment.body) {
        if (this.isServicePortal) {
          this.newComment.notifyRequester = true
        }

        let param = {
          note: this.newComment,
          module: this.module,
          parentModuleName: this.parentModule,
        }

        if (this.module === 'basealarmnotes') {
          param.alarmOccurrenceId = this.record.occurrence
            ? this.record.occurrence.id
            : -1
        }
        this.isAddingNotes = true

        let url = 'v2/notes/add'
        if (this.$helpers.isLicenseEnabled('THROW_403_WEBTAB')) {
          let { record,parentModule,module } = this
          url = `v2/notes/${module}/${parentModule}/add/${record.id}`
        }
        let { data, error } = await API.post(url, param)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.reset()
          this.comments.push(data.Notes)
          this.closeComment()
          this.isAddingNotes = false
        }
      }
    },
  },
}
</script>
