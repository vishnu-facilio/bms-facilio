<script>
import ColumnMapping from 'src/pages/assets/import/v1/ColumnMapping'

export default {
  extends: ColumnMapping,
  methods: {
    sendMapping() {
      if (
        this.checkForProperDateMapping() &&
        this.checkForProperFieldMapping()
      ) {
        this.importProcessContext.fieldMapping = {}
        this.importProcessContext.fieldMappingJSON = {}
        for (let key in this.fieldMappingForm.mappedValues) {
          if (this.fieldMappingForm.mappedValues[key] !== '') {
            let fieldMappingKey
            if (
              this.importProcessContext.facilioFieldMapping[
                this.fieldMappingForm.mappedValues[key]
              ]
            ) {
              fieldMappingKey =
                this.importProcessContext.facilioFieldMapping[
                  this.fieldMappingForm.mappedValues[key]
                ].module.name +
                '__' +
                this.fieldMappingForm.mappedValues[key]
            } else {
              fieldMappingKey =
                this.importProcessContext.module.name +
                '__' +
                this.fieldMappingForm.mappedValues[key]
            }
            this.importProcessContext.fieldMapping[fieldMappingKey] = key
          } else {
            continue
          }
        }
        this.mapImportSettingFields()
        this.importProcessContext.fieldMappingString = JSON.stringify(
          this.importProcessContext.fieldMapping
        )
        if (this.importProcessContext.importJobMeta === null) {
          let temp = {}
          temp['dateFormats'] = this.fieldMappingForm.dateFormatMapping
          this.importProcessContext.importJobMeta = JSON.stringify(temp)
        } else {
          let temp = JSON.parse(this.importProcessContext.importJobMeta)
          temp['dateFormats'] = this.fieldMappingForm.dateFormatMapping
          this.importProcessContext.importJobMeta = JSON.stringify(temp)
        }
        let self = this
        let assetId = this.asset
        if (this.asset === '') {
          assetId = null
        }
        this.saving.sendMapping = true
        this.$http
          .post('/v2/import/parseHandler', {
            importProcessContext: this.importProcessContext,
            assetId: assetId,
          })
          .then(function(response) {
            self.$emit('mappingResponse', response.data.result)
            self.saving.sendMapping = false
          })
          .catch(function(error) {
            self.saving.sendMapping = false
            self.$message.error('Data import failed! [ ' + error + ']')
          })
      } else {
        if (this.checkForProperFieldMapping() === false) {
          this.$message.error('Please map all Update By columns')
        } else if (this.checkForProperDateMapping() === false) {
          this.$message.error(
            'Please map all date fields to a format before importing'
          )
        } else {
          this.$message.error(
            'Please map all mandatory columns and date fields'
          )
        }
      }
    },
  },
}
</script>
