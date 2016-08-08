#### 安装jar包到本地, examples可看情况自行安装

* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-core -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-core-1.2.1.jar
* mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hadoop-demo -Dversion=1.2.1 -Dpackaging=jar -Dfile=hadoop-examples-1.2.1.jar


### 运行程序
* 单机模拟，在linux环境下，不依赖hadoop环境
    * mvn clean compile exec:java -Dexec.mainClass="com.kk.demo.WordCount" -Dexec.args="/data/hadoop/test/data/wordcount/input /data/hadoop/test/data/wordcount/output"
* 运行在hadoop环境
    * 首先修改pom，修改maven-assembly-plugin， 可执行类。
    * hadoop jar target/hadoop-demo-jar-with-dependencies.jar /kk/data/wordcount/input /kk/data/wordcount/output
    * 运行结束后会在 output目录下面生成part-r-*文件，为结果文件，output目录下面还有log文件
    * 设置recuce数量 默认是1：job.setNumReduceTasks(number);这样output目录下面会有number个part-r-结果文件
        * 单机模拟下 此参数不生效
* map中每行读入数据，然后对行内容进行解析， 输出keyout,valueout
* reduce中 接受map中解析好的keyout,valout， 在reduce为keyin,valuein
* reduce中 keyout,valout对应 job.setOutputKeyClass,job.setOutputValueClass
* reduce中，传入一个key，以及这个key对应的value列表，然后再对value进行处理，输出结果。
* 启动方式有两种
    * main直接定义job，启动
    * 实现Tool接口的run方法，  main方法中通过ToolRunner.run启动
        * 会先解析 args（GenericOptionsParser）
* hadoop中，string用 Text，int用 IntWritable
* FileInputFormat.setInputPaths(job, new Path(args[0]))
    * 可以添加文件，也可以添加目录(自动加载目录下所有文件),也可以使用 通配符

### stream
### 自定义排序
### aggregate

### demo

* WordCount
    * 单词统计，  统计文件内单词个数，每行可以有多个单词
* StatStudentScore
    * 求每个学生的平均成绩
    * 输入文件为每行:name score
* NumberSort
    * 输入文件 每行一个数字
    * 输出文件 每行两列，数字的排名(从1开始) 数字本身
    * 要保证每个reduce中的数字的key是连续的，需要自定义分片规则，将同在一个区间段的数字都放到一个reducer中
    * 输出:有个reduce就输出几个文件，再对文件做处理即可
    * ***Partion 可以设置分区，将key按照一定规则分到不同的reduce***
    * ***hadoop 保证key是有序的。***
* MaxNumber
    * 输入文件 每行一个数字
    * 输出文件:最大值
    * ***combine 相当于本地reduce，然后在转到reduce***
* StatStudentScore2
    * ***修改hadoop默认数据方式***
    * ***TextInputFormat是默认的InputFormat，一行为一个record，key是该行在文件中的偏移量，value是该行的内容。***
    * ***设置输入文件格式KeyValueTextInputFormat,默认按照\t分割***
* Duplicate
    * 数据去重,类似sort -u， 数据每行一个
* Grep
    * 简单Grep，
    * 输出空，使用NullWritable.get()
* TopKNum
    * 利用MapReduce求前K大数(最小堆)
    * ***config 传入main参数到 map，reduce***
    * 重写map,reduce的***setup,cleanup***方法
* Anagrams
    * 寻找变位词集合
    * 给定一本英语单词词典，请找出所有的变位词集。所谓的变位词是指，组成各个单词的字母完全相同，只是字母排列的顺序不同。
    * 输入:pans pots opt snap stop tops
    * 输出:[pans,snap] [opt] [pots,stop,tops]





#### Hadoop不适用的场景
1. 低延迟的数据访问
Hadoop并不适用于需要实时查询和低延迟的数据访问。数据库通过索引记录可以降低延迟和快速响应，这一点单纯的用Hadoop是没有办法代替的。但是如果你真的想要取代一个实时数据库，可以尝试一下HBase来实现数据库实时读写。
2. 结构化数据
Hadoop不适用于结构化数据，却非常适用于半结构化和非结构化数据。Hadoop和RDBMS不同，一般采用分布式存储，因此在查询处理的时候将会面临延迟问题。
3. 数据量并不大的时候
Hadoop一般适用于多大的数据量呢？答案是：TB 或者PB。当你的数据只有几十GB时，使用Hadoop是没有任何好处的。按照企业的需求有选择性的的使用Hadoop，不要盲目追随潮流。Hadoop很强大。但企业在使用Hadoop或者大数据之前，首先要明确自己的目标，再确定是否选对了工具。
4. 大量的小文件
小文件指的是那些size比HDFS的block size(默认64M)小得多的文件。如果在HDFS中存储大量的小文件，每一个个文件对应一个block，那么就将要消耗namenode大量的内存来保存这些block的信息。如果小文件规模再大一些，那么将会超出现阶段计算机硬件所能满足的极限。
5. 太多的写入和文件更新
HDFS是采用的一些多读方式。当有太多文件更新需求，Hadoop没有办法支持。
6. MapReduce可能不是最好的选择
MapReduce是一个简单的并行编程模型。是大数据并行计算的利器，但很多的计算任务、工作及算法从本质上来说就是不适合使用MapReduce框架的。
如果你让数据共享在MapReduce，你可以这样做：
    * 迭代：运行多个 MapReduce jobs ，前一个 MapReduce 的输出结果，作为下一个 MapReduce 的输入。
    * 共享状态信息：但不要分享信息在内存中，由于每个MapReduce的工作是在单个JVM上运行。