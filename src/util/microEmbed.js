import FacilioMicro from '@facilio/micro'
import packageJson from '../../package.json'

export const facilioMicro = new FacilioMicro({
  name: 'facilio-client',
  version: packageJson.version,
})
