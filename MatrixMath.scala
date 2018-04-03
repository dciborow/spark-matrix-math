spark.sparkContext.setLogLevel("WARN")
val sqlContext = spark.sqlContext

// Databricks notebook source
import org.apache.spark.sql.functions._
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, MatrixEntry}

// COMMAND ----------

def makeMatrix(rowCount: Int, colCount: Int) = {
  val rows = sqlContext.range(0, rowCount)
  val cols = sqlContext.range(0, colCount)
  
  cols.crossJoin(rows)
    .withColumn("rand",rand())
    .rdd.map(item => MatrixEntry(item.getLong(0).toInt, item.getLong(1).toInt, item.getDouble(2)))
}

// COMMAND ----------

def multiply(userCount: Int, itemCount: Int) = {
  val rdd1 = makeMatrix(itemCount, userCount)
  val rdd2 = makeMatrix(itemCount, itemCount)
  val userMatrix = new CoordinateMatrix(rdd1).toBlockMatrix().cache()
  val itemMatrix = new CoordinateMatrix(rdd2).toBlockMatrix().cache()
  userMatrix.multiply(itemMatrix)
}

// COMMAND ----------

def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
}

// COMMAND ----------

val userList = List(100,1000,10000,100000,1000000)
val itemList = List(100,1000,10000,100000,1000000)

// COMMAND ----------

for(item <- itemList){
  for(user <- userList){
    println("Testing User: " + user)
    println("Testing Item: " + item)
    time { multiply(user, item) }
  }
}
