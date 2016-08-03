#### 安装jar包到本地, examples可看情况自行安装

* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-core -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-core-1.2.1.jar
* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-demo -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-examples-1.2.1.jar


### 运行程序
* 单机模拟，在linux环境下，不依赖hadoop环境
    * mvn clean compile exec:java -Dexec.mainClass="com.kk.demo.WordCount" -Dexec.args="/data/hadoop/test/data/wordcount/input /data/hadoop/test/data/wordcount/output"
* 运行在hadoop环境
    * 首先修改pom，修改maven-assembly-plugin， 可执行类。
    * hadoop jar target/hadoop-demo-jar-with-dependencies.jar com.kk.demo.WordCount /kk/data/wordcount/input /kk/data/wordcount/output
    * 运行结束后会在 output目录下面生成part-r-*文件，为结果文件，output目录下面还有log文件
    * 设置recuce数量 默认是1：job.setNumReduceTasks(number);这样output目录下面会有number个part-r-结果文件
        * 单机模拟下 此参数不生效
* map中每行读入数据，然后对行内容进行解析， 输出keyout,valueout
* reduce中 接受map中解析好的keyout,valout， 在reduce为keyin,valuein
* reduct中，传入一个key，以及这个key对应的value列表，然后再对value进行处理，输出结果。
* 启动方式有两种
    * main直接定义job，启动
    * 实现Tool接口的run方法，  main方法中通过ToolRunner.run启动
* hadoop中，string用 Text，int用 IntWritable
### stream
### 自定义排序
### combine

### demo

* WordCount
    * 单词统计，  统计文件内单词个数，每行可以有多个单词
* StatStudentScore
    * 求每个学生的平均成绩
    * 输入文件为每行:name score


