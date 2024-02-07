import {
  getResponseModuleName,
  getTemplateModuleName,
} from 'src/pages/setup/survey/SurveyUtil'

describe('SurveyUtil', () => {
  it('getResponseModuleName method should return respective response module name', () => {
    expect(getResponseModuleName('workorder')).toBe('surveyResponse')
  })

  it('getTemplateModuleName method should return respective template module name', () => {
    expect(getTemplateModuleName('workorder')).toBe('surveyTemplate')
  })
})
