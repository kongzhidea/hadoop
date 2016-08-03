#### 安装jar包到本地, examples可看情况自行安装

* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-core -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-core-1.2.1.jar
* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-demo -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-examples-1.2.1.jar


### 运行程序
* 单机模拟，在linux环境下，不依赖hadoop环境
    * mvn clean compile exec:java -Dexec.mainClass="com.kk.demo.WordCount" -Dexec.args="/data/hadoop/test/data/wordcount/input /data/hadoop/test/data/wordcount/output"
* 运行在hadoop环境
    * hadoop jar target/hadoop-demo-jar-with-dependencies.jar com.kk.demo.WordCount /kk/data/wordcount/input /kk/data/wordcount/output
    * 运行结束后会在 output目录下面生成part-r-*文件，为结果文件，output目录下面还有log文件
    * 设置recuce数量 默认是1：job.setNumReduceTasks(number);这样output目录下面会有number个part-r-结果文件
        * 单机模拟下 此参数不生效
    *

### demo

* WordCount 单词统计，  统计文件内单词个数，每行可以有多个单词
*


