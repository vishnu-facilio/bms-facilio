<template>
  <base-dialog-box
    :visibility.sync="visibility"
    :onConfirm="addNewFolder"
    :onCancel="closeDialog"
    :disableCancel="false"
    cancelText="Cancel"
    :confirmText="'Create'"
    :title="'Create New Folder'"
    width="490px"
  >
  <div class="dialog-content-body" slot="body" style="margin:20px;display:flex;">
      <span style="width:150px;text-align:center;padding-top:10px;">New Folder Name : </span>
      <el-input v-model="newFolderName" ref="rename" class="fc-input-full-border-select2 name-field">
      </el-input>
  </div>
  </base-dialog-box>
</template>
<script>
import BaseDialogBox from './BaseDialogBox.vue'
import { API } from '@facilio/api'
import ReportHelper from 'pages/report/mixins/ReportHelper'

export default {
  name:'PivotNewFolder',
  props:['visibility','folderObject'],
  data(){
      return {
          newFolderName:'',
          newFolderError:false
      }
  },
  components:{
      BaseDialogBox
  },
  mixins: [ReportHelper],
  methods: {
    addNewFolder() {
      let newFolder = {}
      if (this.newFolderName === '' || this.newFolderName === null) {
        this.newFolderError = true
      } else {
        newFolder['name'] = this.newFolderName
        let moduleName = this.getCurrentModule().module
        API.post('/v3/report/folder/create', {
          reportFolder: newFolder,
          moduleName: moduleName,
        }).then(response => {
          if (response.error) {
            this.$message.error('Error while creating report folder')
          } else {
            this.newFolderName = ''
            let newFolder = response.data.reportFolder
            newFolder.reports = []
            this.$emit('newFolderCreated',newFolder)
          }
        })
      }
    },
  closeDialog(){
      this.$emit('closeNewFolderDialog')
  }

  },
}

</script>
<style>

</style>
