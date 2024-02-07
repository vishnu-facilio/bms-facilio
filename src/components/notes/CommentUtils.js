import Vue from 'vue'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export const getCommentService = () => {
  return new Vue({
    data() {
      return {
        module: '',
        parentModule: '',
        portalApps: [],
        defaultSharingApps: [],
        record: {},
        forWO: false,
        isServicePortal: false,
        peopleFromRecordFields: null,
        testCount: 0,
      }
    },
    computed: {
      canShowSharing() {
        let {
          appCategory: { PORTALS },
        } = this.$constants
        if (getApp() && getApp().appCategory === PORTALS) {
          return false
        } else {
          return true
        }
      },
    },
    methods: {
      async getSharingApps() {
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

      async getPeopleFieldForRecord() {
        let { parentModule, record } = this
        let param = {
          moduleName: parentModule,
          recordId: record.id,
        }
        let { data, error } = await API.post(
          'v3/people/util/recordPeople',
          param
        )
        if (!error) {
          let { peopleFromRecordFields } = data || []
          this.peopleFromRecordFields = peopleFromRecordFields
        }
      },

      async updateCommentSharing(comment) {
        let { module, parentModule } = this
        let param = {
          note: comment,
          module: module,
          parentModuleName: parentModule,
          noteId: comment.id,
        }
        let { error } = await API.post('v2/notes/updateSharing', param)
        return { error }
      },
      async save(newComment, isEdit) {
        let { isServicePortal, module, parentModule, record } = this
        if (isServicePortal) {
          newComment.notifyRequester = true
        }
        newComment.parentModuleLinkName = module
        newComment.parentId = record.id
        newComment.notifyRequester = false

        let url = isEdit ? 'v2/notes/update' : 'v2/notes/add'
        //changed API to new format once its stable we can remove this and the new API can be there
        if (this.$helpers.isLicenseEnabled('THROW_403_WEBTAB')) {
          let addOrUpdate = isEdit ? 'update' : 'add'
          url = `v2/notes/${module}/${parentModule}/${addOrUpdate}/${record.id}`
        }
        let param = {
          note: newComment,
          module: module,
          parentModuleName: parentModule,
        }

        if (module === 'basealarmnotes') {
          param.alarmOccurrenceId = record.occurrence
            ? record.occurrence.id
            : -1
        }

        let { error, data } = await API.post(url, param)
        if (error) {
          return { error }
        } else {
          return { data: data.Notes }
        }
      },
      async delete(note) {
        let { module, parentModule, record } = this
        let url = '/v2/notes/delete'
        if (this.$helpers.isLicenseEnabled('THROW_403_WEBTAB')) {
          url = `v2/notes/${module}/${parentModule}/delete/${record.id}`
        }
        let params = {
          noteId: note.id,
          module: module,
          parentModuleName: parentModule,
        }

        let { error } = await API.delete(url, params)
        return { error }
      },
      async fetchComments(parentNote) {
        let { record } = this
        if (!record || typeof record !== 'object') {
          return
        }
        let listNameSpace = '/note/get?module='
        if (this.isServicePortal) {
          listNameSpace = '/notelist?module='
        }
        let url =
          listNameSpace +
          this.module +
          '&parentId=' +
          this.record.id +
          '&parentModuleName=' +
          this.parentModule

        url =
          url +
          (!isEmpty(parentNote)
            ? '&parentNoteId=' + parentNote.id
            : '&onlyFetchParentNotes=true')

        let { data, error } = await API.get(url)
        if (error) {
          return { error }
        } else {
          return { data: data }
        }
      },
    },
  })
}
