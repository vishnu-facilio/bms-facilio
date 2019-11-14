import pandas as pd

import statsmodels.api as sm
import statsmodels.formula.api as smf

from datetime import datetime, timedelta
import time
import pytz

import operator

from sys import argv
import json

import warnings
warnings.filterwarnings("ignore")

dependentVariable = argv[1]
method = argv[2]
numberOfVariables = argv[4]
dataFile = argv[3] 


def constructMLRmodel(dataFile, dependentVariable, method, numberOfVariables):
    data = pd.read_csv(dataFile)
    numberOfVariables = int(numberOfVariables)
    method = int(method)
    if numberOfVariables == -1:
        numberOfVariables = None
       
    response = MLR.buildMLRmodel(data,
                            dependentVariable,
                            method,
                            numberOfVariables)
    response = json.dumps(response)
    return response


def getTimePeriod(startDate, endDate):
    date = []
    date.append(startDate)
    time = startDate
    while time < endDate:
        time = time + timedelta(days=1)
        date.append(time)  
    return date


class MLR(object):

    @staticmethod
    def buildMLRmodel(data, dependentVariable, method, numberOfVariables): 
        
        finalData = data
        
        if method == 0:
            response = MLR.pValueCorrectedModel(finalData, dependentVariable, numberOfVariables)
        else:
            response = MLR.vifBackwardElimination(finalData, dependentVariable, numberOfVariables)
       
        return response
    
    @staticmethod
    def pValueCorrectedModel(data, dependentVariable, numberOfVariables=None):
        if numberOfVariables != None:
            response = MLR.pValueModelWithNoOfVariables(data, dependentVariable, numberOfVariables)
        else:
            response = MLR.pValueModelWithoutNoOfVariables(data, dependentVariable)
        return response
    
    @staticmethod
    def vifBackwardElimination(data, dependentVariable, numberOfVariables=None):
        if numberOfVariables != None:
            response = MLR.vifBackwardEliminationWithNoOfVariables(data, dependentVariable, numberOfVariables)
        else:
            response = MLR.vifBackwardEliminationWithoutNoOfVariables(data, dependentVariable)
        return response
            
    @staticmethod
    def getExpAndResponseVariables(data, dependentVariable):
        responseVariable = dependentVariable
        explanatoryVariable = data.drop(responseVariable, axis=1)
        return list(explanatoryVariable.columns.values), responseVariable
      
    @staticmethod
    def modelFit(y, x):
        x = sm.add_constant(x)
        model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
        return model, model.rsquared_adj
    
    @staticmethod
    def getModelStats(y, x):
        model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
        return model, model.rsquared_adj
  
    @staticmethod
    def backwardElimination(data, dependentVariable, numberOfVariables):
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]
        x = sm.add_constant(x)
        model, adjRsquared = MLR.getModelStats(y, x)
        result = []
        response = MLR.extractModelValues(model, dependentVariable, x.columns)
        result.append(response)
        columnList = x.columns
        colPosition = len(columnList) - 1
              
        while colPosition >= 2:
            tempX = x.drop(columnList[colPosition], axis=1, inplace=False)
            model, newAdjRsquared = MLR.getModelStats(y, tempX)
            result.append(MLR.extractModelValues(model, dependentVariable, tempX.columns))            
            if newAdjRsquared > adjRsquared:
                x = tempX
                model = model
            
            if numberOfVariables != None:
                if len(tempX.columns) == (numberOfVariables + 1) or len(x.columns) == (numberOfVariables + 1):
                    break
            
            colPosition -= 1
        return x.columns, result
    
    @staticmethod
    def vifBackwardEliminationWithNoOfVariables(data, dependentVariable, numberOfVariables):
        neededColumn, response = list(MLR.backwardElimination(data, dependentVariable, numberOfVariables))

        neededColumn = list(filter(lambda x: x != 'const', neededColumn))
        neededColumn.append(dependentVariable)
        data = data[neededColumn]
        count = len(data.columns)
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]
        if len(neededColumn) == (numberOfVariables + 1):
            x = sm.add_constant(x)
            model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
            response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
        else:
            
            while count >= 1:
                vif_dict = []
                vif = MLR.removeMulticollinearVariables(data, dependentVariable)
                vif_dict.append(vif)
                maxVIFcount = dict((k, v) for k, v in vif.items() if v > 5)
                
                if len(maxVIFcount) == 0:
                    sortedVIF = dict(sorted(vif.items(), key=operator.itemgetter(0), reverse=True))
                    
                    noOfvariablesToBeEliminated = len(vif) - numberOfVariables
                    keyList = list(sortedVIF.keys())
                    for i in range(0, noOfvariablesToBeEliminated):
                        keyList.pop(0)
                    x = data.drop(y, axis=1, inplace=False)
                    x = x[keyList]
                    x = sm.add_constant(x)
                    model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                    response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                    break
                else:
                    maxVIF = max(maxVIFcount.items(), key=operator.itemgetter(1))
                    x = data.drop(y, axis=1, inplace=False)
                    x = data.drop(maxVIF[0], axis=1, inplace=False)
                    x = sm.add_constant(x)
                    model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                    response.append(MLR.extractModelValues(model, dependentVariable, x.columns))

                    if len(x.columns) == (numberOfVariables + 1): 
                        break
                    data = data.drop(maxVIF[0], axis=1, inplace=False)
                
                count -= 1
        
        coef_dict = {}
        for coef, features in zip(model.params , x.columns):
            coef_dict[features] = coef 

        finalDict = {}          
        for i in range(0, len(response)): 
            if i == (len(response)-1): 
                summaryTable = response[i]
                
        for i in range(0, len(vif_dict)): 
            if i == (len(vif_dict)-1): 
                summaryVIF = vif_dict[i]  
        
        dictionary = {}
        dictionary['result'] = coef_dict
        dictionary['summary'] = summaryTable
        dictionary['vif'] = summaryVIF
        
        table = {}
        table['Tables'] = response
        table['VIF'] = vif_dict
        finalDict['data'] = dictionary
        finalDict['steps'] = table
        print(finalDict)

        return finalDict
    
    @staticmethod
    def vifBackwardEliminationWithoutNoOfVariables(data, dependentVariable):
        ind, dep = MLR.getExpAndResponseVariables(data, dependentVariable)
        neededColumn, response = list(MLR.backwardElimination(data, dependentVariable, numberOfVariables=None))

        neededColumn = list(filter(lambda x: x != 'const', neededColumn))
        neededColumn.append(dependentVariable)
        data = data[neededColumn]
        count = len(data.columns)
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]
       
        while count >= 1:
            vif_dict = []
            vif = MLR.removeMulticollinearVariables(data, dependentVariable)
            vif_dict.append(vif)
            maxVIFcount = dict((k, v) for k, v in vif.items() if v > 5)
            
            if len(maxVIFcount) == 0:
                x = data.drop(y, axis=1, inplace=False)
                x = sm.add_constant(x)
                model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                break
            else:
                maxVIF = max(maxVIFcount.items(), key=operator.itemgetter(1))
                x = data.drop(y, axis=1, inplace=False)
                x = data.drop(maxVIF[0], axis=1, inplace=False)
                x = sm.add_constant(x)
                model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                data = data.drop(maxVIF[0], axis=1, inplace=False)
            
            count -= 1
        
        coef_dict = {}
        for coef, features in zip(model.params , x.columns):
            coef_dict[features] = coef
        
        finalDict = {}          
        for i in range(0, len(response)): 
            if i == (len(response)-1): 
                summaryTable = response[i]
                
        for i in range(0, len(vif_dict)): 
            if i == (len(vif_dict)-1): 
                summaryVIF = vif_dict[i]  
        
        dictionary = {}
        dictionary['result'] = coef_dict
        dictionary['summary'] = summaryTable
        dictionary['vif'] = summaryVIF
        
        table = {}
        table['Tables'] = response
        table['VIF'] = vif_dict
        finalDict['data'] = dictionary
        finalDict['steps'] = table
        print(finalDict)

        return finalDict
    
    @staticmethod
    def extractModelValues(model, dependentVariable, independentVariables):
        coef_dict = {}
        for coef, features in zip(model.params , independentVariables):
            coef_dict[features] = coef
        diagnostic = model.summary2().tables[2]
        
        table = {}
        
        table['AttributeList'] = model.params.index.tolist()
        coef = model.params
        std_err = model.bse
        t = model.tvalues
        pValue = model.pvalues
        confInt = model.conf_int()       
        for j in range(0, len(table['AttributeList'])):  
            i = table['AttributeList'][j]
            table[i] = {}
            table[i]['coef'] = coef[j]
            table[i]['std_err'] = std_err[j]
            table[i]['t'] = t[j]
            table[i]['P>|t|'] = pValue[j]
            table[i]['0.025'] = confInt[0][j]
            table[i]['0.975'] = confInt[1][j]
            
        result = {}
        result['y'] = dependentVariable
        result['Model'] = 'OLS'
        result['Method'] = 'Least Squares'
        result['Date'] = datetime.today().strftime('%Y-%m-%d')
        result['Time'] = time.strftime("%H:%M:%S")
        result['No_observations'] = int(model.nobs)
        result['Df_Residuals'] = int(model.df_resid)
        result['Df_Model'] = int(model.df_model)
        result['Covariance_Type'] = model.cov_type
        result['R_squared'] = model.rsquared
        result['Adj_Rsquared'] = model.rsquared_adj
        result['F_statistic'] = model.fvalue
        result['Prob_F_statisic'] = model.f_pvalue
        result['Log_likelihood'] = model.llf
        result['AIC'] = model.aic
        result['BIC'] = model.bic
        result['Table'] = table
        result['Omnibus'] = diagnostic[1][0]  # ['omni']
        result['Prob_Omnibus'] = diagnostic[1][1]  # ['omnipv']
        result['Skew'] = diagnostic[1][2]  # ['skew']
        result['Kurtosis'] = diagnostic[1][3]  # ['kurtosis']
        result['Durbin_Watson'] = diagnostic[3][0]
        result['Jarque_Bera_JB'] = diagnostic[3][1]  # ['jb']
        result['Prob_JB'] = diagnostic[3][2]  # ['jbpv']
        result['Cond_No'] = diagnostic[3][3]  # ['condno']
        result['Coefficients'] = coef_dict
        
        return result
     
    @staticmethod
    def getMaxPvalueVariable(model):
        p_value = model.pvalues.to_frame()
        p_value = p_value[1:]
        insignificantVariables = p_value[p_value[0] > 0.05]
        if len(insignificantVariables) > 0:
            maxP_value = insignificantVariables.loc[insignificantVariables.idxmax()] 
            removableVariable = maxP_value.index
            return removableVariable
        else:
            return None
    
    @staticmethod    
    def finalModel(data, dependentVariable, numberOfVariables):
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]   
        x = sm.add_constant(x)
        model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
        response = []
        result = MLR.extractModelValues(model, dependentVariable, x.columns)
        response.append(result)
        
        count = len(x.columns) - 1
        removedVariable = []
        while count >= 1:
            removableVariable = MLR.getMaxPvalueVariable(model) 
            if removableVariable != None:
                removedVariable.append(removableVariable.tolist())
                x = x.drop(removableVariable, axis=1, inplace=False)
                x = sm.add_constant(x)
                model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                if numberOfVariables != None:
                    if len(x.columns) == (numberOfVariables + 1):
                        break                
            else:
                break
            count -= 1
        return model , removedVariable, response
    
    @staticmethod
    def removeMulticollinearVariables(data, dependentVariable):
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        x_vars = data[expNames]
        x_var_name = x_vars.columns
        dic = {}
        for i in range(0, x_var_name.shape[0]):
            y = x_vars[x_var_name[i]]
            x = x_vars[x_var_name.drop(x_var_name[i])]
            x = sm.add_constant(x)
            rsq = smf.ols(formula="y~x", data=x_vars, missing='drop').fit().rsquared
            vif = round(1 / (1 - rsq), 2)
            dic[x_var_name[i]] = vif
        return dic
    
    @staticmethod
    def splitTrainTestData(data, timeZone, startDate, endDate):
        start = datetime.strptime(startDate, "%Y-%m-%d")
        end = datetime.strptime(endDate, "%Y-%m-%d")
        tz = pytz.timezone(timeZone)
        st = tz.localize(start) 
        startTimestamp = int(st.timestamp()) * 1000
        et = tz.localize(end)
        endTimestamp = int(et.timestamp()) * 1000
        trainData = data[(data.TTIME < startTimestamp)]
        fullData = data[(data.TTIME > startTimestamp) & (data.TTIME <= endTimestamp)]
        trainData = trainData.drop(['TTIME'], axis=1)
        testData = fullData.drop(['TTIME'], axis=1)
        return trainData, testData, fullData
    
    @staticmethod
    def pValueModelWithNoOfVariables(data, dependentVariable, numberOfVariables):
        
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]
        count = len(x.columns)
        model, removedVariable, response = MLR.finalModel(data, dependentVariable, numberOfVariables)
        
        removedVariable = [val for sublist in removedVariable for val in sublist]
        for i in removedVariable:
            if i not in data:
                pass
            else:
                data = data.drop(i, axis=1)
                
        if len(data.columns) == (numberOfVariables + 1):
            x = data.drop(y, axis=1, inplace=False)
            x = sm.add_constant(x)
            model = model
            
        else:
            while count >= 1:
                vif_dict = []
                vif = MLR.removeMulticollinearVariables(data, dependentVariable)
                vif_dict.append(vif)
                maxVIFcount = dict((k, v) for k, v in vif.items() if v > 5)
                
                if len(maxVIFcount) == 0:
                    sortedVIF = dict(sorted(vif.items(), key=operator.itemgetter(0), reverse=True))
                    noOfvariablesToBeEliminated = len(vif) - numberOfVariables
                    keyList = list(sortedVIF.keys())
                    for i in range(0, noOfvariablesToBeEliminated):
                        keyList.pop(0)
                    x = data.drop(y, axis=1, inplace=False)
                    x = x[keyList]
                    x = sm.add_constant(x)
                    model = model
                    response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                    break
                else:
                    maxVIF = max(maxVIFcount.items(), key=operator.itemgetter(1))
                    x = data.drop(y, axis=1, inplace=False)
                    x = data.drop(maxVIF[0], axis=1, inplace=False)
                    x = sm.add_constant(x)
                    model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                    response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                    data = data.drop(maxVIF[0], axis=1, inplace=False)
                count -= 1
        
        coef_dict = {}
        for coef, features in zip(model.params , x.columns):
            coef_dict[features] = coef
        
        finalDict = {}          
        for i in range(0, len(response)): 
            if i == (len(response)-1): 
                summaryTable = response[i]
                
        for i in range(0, len(vif_dict)): 
            if i == (len(vif_dict)-1): 
                summaryVIF = vif_dict[i]  
        
        dictionary = {}
        dictionary['result'] = coef_dict
        dictionary['summary'] = summaryTable
        dictionary['vif'] = summaryVIF
        
        table = {}
        table['Tables'] = response
        table['VIF'] = vif_dict
        finalDict['data'] = dictionary
        finalDict['steps'] = table
        print(finalDict)
        
        return finalDict
    
    @staticmethod
    def pValueModelWithoutNoOfVariables(data, dependentVariable):
        exp, res = MLR.getExpAndResponseVariables(data, dependentVariable)
        expNames = list(exp)
        y = data[[res]]
        x = data[expNames]
        count = len(x.columns)
        model, removedVariable, response = MLR.finalModel(data, dependentVariable, numberOfVariables=None)
        removedVariable = [val for sublist in removedVariable for val in sublist]
        for i in removedVariable:
            if i not in data:
                pass
            else:
                data = data.drop(i, axis=1)

        while count >= 1:
            vif_dict = []
            vif = MLR.removeMulticollinearVariables(data, dependentVariable)
            vif_dict.append(vif)
            maxVIFcount = dict((k, v) for k, v in vif.items() if v > 5)
            if len(maxVIFcount) == 0:
                x = data.drop(y, axis=1, inplace=False)
                x = sm.add_constant(x)
                model = model
                response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                break
            else:
                maxVIF = max(maxVIFcount.items(), key=operator.itemgetter(1))
                x = data.drop(y, axis=1, inplace=False)
                x = data.drop(maxVIF[0], axis=1, inplace=False)
                x = sm.add_constant(x)
                model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
                response.append(MLR.extractModelValues(model, dependentVariable, x.columns))
                data = data.drop(maxVIF[0], axis=1, inplace=False)
            count -= 1
        
        coef_dict = {}
        for coef, features in zip(model.params , x.columns):
            coef_dict[features] = coef
        
        finalDict = {}          
        for i in range(0, len(response)): 
            if i == (len(response)-1): 
                summaryTable = response[i]
                
        for i in range(0, len(vif_dict)): 
            if i == (len(vif_dict)-1): 
                summaryVIF = vif_dict[i]  
        
        dictionary = {}
        dictionary['result'] = coef_dict
        dictionary['summary'] = summaryTable
        dictionary['vif'] = summaryVIF
        
        table = {}
        table['Tables'] = response
        table['VIF'] = vif_dict
        finalDict['data'] = dictionary
        finalDict['steps'] = table
        print(finalDict)

        return finalDict
        
    @staticmethod
    def predictTestData(testData, dependentVariable, model, coef_dict):
        exp, res = MLR.getExpAndResponseVariables(testData, dependentVariable)
        expNames = list(exp)
        x = testData[expNames]
        variables = []
        for i in expNames:
            if i in coef_dict.keys():
                variables.append(i)
        x = x[variables]
        x = sm.add_constant(x)

        predictedData = model.predict(x)
        return predictedData
                
    @staticmethod
    def multicollinearity(data, dependentVariable):
        model = MLR.finalModel(data, dependentVariable)
        r2 = model.rsquared
        vif = 1 / (1 - r2)
        return vif
    
    @staticmethod
    def vif_cal(data, dependentVariable):
        model, removedVariable = MLR.finalModel(data, dependentVariable) 
        indDep, dep = MLR.getExpAndResponseVariables(data, dependentVariable)  #
        data = data.drop(dep, axis=1)
        removedVariable = [val for sublist in removedVariable for val in sublist]
        for i in removedVariable:
            x_vars = data.drop(removedVariable, axis=1)
        xVars_names = x_vars.columns
        dic = {}
        count = len(xVars_names)
        while count >= 1:
            for i in range (0, xVars_names.shape[0]):
                y = x_vars[xVars_names[i]]
                x = x_vars[xVars_names.drop(xVars_names[i])]
                x = sm.add_constant(x)
                r2 = smf.ols(formula="y~x", data=x_vars, missing='drop').fit().rsquared
                vif = round(1 / (1 - r2), 2)
                dic[xVars_names[i]] = vif
            count -= 1
        return dic
    
    @staticmethod
    def removeMultiCollinearity(data, dependentVariable):
        model, removedVariable = MLR.finalModel(data, dependentVariable)
        vif = MLR.vif_cal(data, dependentVariable)
        removedVariable = [val for sublist in removedVariable for val in sublist]
        for i in removedVariable:
            df = data.drop(removedVariable, axis=1)
        max_vif = max(vif.items(), key=operator.itemgetter(1))[0]
        final_df = df.drop(max_vif, axis=1)
        exp, res = MLR.getExpAndResponseVariables(final_df, dependentVariable)
        expNames = list(exp)
        y = final_df[[res]]
        x = final_df[expNames]
        x = sm.add_constant(x)
        model = sm.OLS(y.astype(float), x.astype(float), missing='drop').fit()
        coef_dict = {}
        for coef, features in zip(model.params , expNames):
            coef_dict[features] = coef
        
        return coef_dict 

    
if __name__ == '__main__':
    constructMLRmodel(dataFile, dependentVariable, method, numberOfVariables)    

