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
  val userMatrix = new CoordinateMatrix(rdd1).toBlockMatrix().toLocalMatrix().asML.toDense
  val itemMatrix = new CoordinateMatrix(rdd2).toBlockMatrix().toLocalMatrix().asML.toDense
  userMatrix.multiply(itemMatrix)
}

// COMMAND ----------

def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    print("," + (t1 - t0))
    result
}

// COMMAND ----------

val userList = List(100,1000,10000,100000)
// val userList = List(1000000, 1000000)
val itemList = List(100,1000,10000,100000)
//val itemList = List(100000)

// COMMAND ----------

println(userList.toString.replace("List(",",").replace(")",""))
for(item <- itemList){
  var first = true
  for(user <- userList){
    if(first){
      print(item)
      first = false
    }
    time { multiply(user, item) }
  }  
  println("")
}
