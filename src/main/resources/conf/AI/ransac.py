import sys
import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression, RANSACRegressor
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import PolynomialFeatures
from sklearn.metrics import r2_score, mean_squared_error 

def ModifiedPolynomialRegression(degree=2, **kwargs):
    ransac = RANSACRegressor(LinearRegression(**kwargs),
                    max_trials=200, min_samples=0.2, random_state=0)
    return make_pipeline(PolynomialFeatures(degree), ransac)

df =pd.read_csv(sys.argv[1])

rowsWithNonZeroY= df[df['y'] > 0] # filter out zero y  because in some data median 'y' can be zero
x = rowsWithNonZeroY['x'].values.reshape(-1,1)
y = rowsWithNonZeroY['y'].values.reshape(-1,1)

try:
    pipeline=ModifiedPolynomialRegression(degree=int(sys.argv[2]))
    model = pipeline.fit(x, y)
except:
    print('Error')
    sys.exit(-1)

y_pred = model.predict(x)
rmse_test = np.sqrt(mean_squared_error(y, y_pred))
r2_test = r2_score(y, y_pred)

df['y1'] = model.predict(df['x'].values.reshape(-1,1))

with pd.option_context('display.max_rows', None, 'display.max_columns', None):  # more options can be specified also
    print(df.to_string(columns=['x', 'y','y1'], index=False))

ransac = pipeline.steps[1][1] # get the ransac regressor object
coef = ransac.estimator_.coef_[0]  # coeffecients are in 2 D array 1 * n 
intercept = ransac.estimator_.intercept_

coef = intercept.tolist() + coef.tolist()[1:]  #remove the constant term
 
# order of output is rmse, rsquared, constant , x , x ^ 2 , x ^ 3 ...
print(rmse_test , r2_test , coef)