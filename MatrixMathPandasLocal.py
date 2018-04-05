# Databricks notebook source
# Databricks notebook source
import timeit
import numpy as np

# COMMAND ----------

def makeMatrix(rowCount, colCount): 
    return np.random.uniform(size=(rowCount, colCount))

# COMMAND ----------

def multiply(userCount, itemCount):
    userMatrix = makeMatrix(itemCount, userCount)
    itemMatrix = makeMatrix(itemCount, itemCount)
    return np.matmul(itemMatrix,userMatrix)

# COMMAND ----------

def time(func):
    """A decorator that times function execution."""
    if __debug__:
        def wrapper(*args, **kwargs):
            t0 = timeit.default_timer()
            result = func(*args, **kwargs)
            t1 = timeit.default_timer()
            print('%s elapsed: %f seconds' % (func.__name__, t1 - t0))
            return result
        return wrapper
    else:
        return func

if __name__ == '__main__':

    # COMMAND ----------

    userList = (1000,10000,100000,1000000)
    itemList = (1000,10000,100000,1000000)

    # COMMAND ----------

    for item in itemList:
      for user in userList:
        print("Testing User: %d" % user)
        print("Testing Item: %d" % item)
        time(multiply)(user, item)


# COMMAND ----------


