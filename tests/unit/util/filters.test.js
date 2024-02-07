import {
  lowerCase,
  upperCase,
  firstUpperCase,
  pascalCase,
  singularize,
  splitGet,
  reverseText,
  weekly,
  prettyBytes,
} from 'src/util/filters'

describe('filters utils', () => {
  it('lowercase method should accept string as a parameter and return the lowercased string', () => {
    expect(lowerCase('TEST')).toEqual('test')
  })

  it('uppercase method should accept string as a parameter and return the uppercased string', () => {
    expect(upperCase('test123')).toEqual('TEST123')
  })

  it('firstUpperCase method should accept string as a parameter and return the firstUpperCased string', () => {
    expect(firstUpperCase('xyz')).toEqual('Xyz')
  })

  it('pascalCase method should accept string as a parameter and return the pascalCased string', () => {
    expect(pascalCase('nextThing')).toEqual('Nextthing')
  })

  it('singularize method should accept string as a parameter and return the singularized string', () => {
    expect(singularize('cars')).toEqual('car')
  })

  it('splitGet method should accept string as a parameter and return the splitted string with given index', () => {
    expect(splitGet('next_ranger_build', '_', 2)).toEqual('build')
    expect(splitGet(null)).toEqual(null)
  })

  it('reverseText method should accept string as a parameter and return the reversed string', () => {
    expect(reverseText('better')).toEqual('retteb')
  })

  it('weekly method should accept period as parameter and return the specified date', () => {
    const mockDate = new Date('2023-05-01T12:34:56')
    jest.spyOn(global, 'Date').mockImplementation(() => mockDate)
    expect(weekly('week')).toEqual('01 - 01 May 2023')
    expect(weekly('month')).toEqual('May 2023')
    expect(weekly('day')).toEqual('01 May 2023')
    // Restore the original date implementation
    global.Date.mockRestore()
  })

  it('prettyBytes method should the bytes in prettied form', () => {
    expect(prettyBytes(2)).toEqual('2 B')
    expect(prettyBytes(2048)).toEqual('2.05 kB')
    expect(prettyBytes(20482048)).toEqual('20.5 MB')
    expect(prettyBytes(204820482048)).toEqual('205 GB')
  })
})
