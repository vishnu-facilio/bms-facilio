<template>
  <base-dialog-box
    :visibility.sync="visibility"
    :onConfirm="saveFolderName"
    :onCancel="closeDialog"
    :disableCancel="false"
    cancelText="Cancel"
    :confirmText="'Save'"
    :title="'Edit Folder Name'"
    width="490px"
  >
  <div class="dialog-content-body" slot="body" style="margin:20px;display:flex;">
      <span style="width:150px;text-align:center;padding-top:10px;">Folder Name : </span>
      <el-input v-model="newFolderName" ref="rename" class="fc-input-full-border-select2 name-field">
      </el-input>
  </div>
  </base-dialog-box>
</template>
<script>
import BaseDialogBox from './BaseDialogBox.vue'
import { API } from '@facilio/api'

export default {
    name:'PivotFolderRename',
    props:['visibility','folderObject'],
    data(){
        return {
            newFolderName:''
        }
    },
    watch:{
        folderObject:{
            handler(newState){
                this.newFolderName=newState?.name
        },
        immediate:true
    }
    },
    components:{
        BaseDialogBox
    },
    methods: {
        saveFolderName(){

      let newName = this.newFolderName
      if (newName !== '' && newName !== null) {
        let reportFolder = {}
        reportFolder['name'] = newName
        reportFolder['id'] = this.folderObject.id
        API.put('/v3/report/folder/update', { reportFolder: reportFolder })
          .then(response => {
            if (response.error) {
              this.$message({
                message: this.$t('common.wo_report.cannot_rename_folder'),
                type: 'error',
              })
            } else {
             // folder.name = response.data.reportFolder.name
            }
          })
          .catch(() => {
            this.$message({
              message: this.$t('common.wo_report.cannot_rename_folder'),
              type: 'error',
            })
          })
      } else {
        this.$message({
          message: this.$t('common.wo_report.foldername_cannot_empty'),
          type: 'error',
        })
      }
      this.$emit('updateFolderList',{name : newName,id:this.folderObject.id})
    },
    closeDialog(){
        this.$emit('cancelRename')
    }

    },
}

</script>
<style>

</style>
