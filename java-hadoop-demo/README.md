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
* writeable 尽量重用
    * 不推荐以下写法，这样会导致程序分配出成千上万个短周期的对象。Java垃圾收集器就要为此做很多的工作
    * ```
        for (String word : words) {
                      output.collect(new Text(word), new IntWritable(1));
              }
      ```
    * 推荐以下写法：
    * ```
         IntWritable one = new IntWritable(1);
         public void map(...) {
              for (String word: words) {
                    wordText.set(word);
                    output.collect(wordText, one);
               }
            }
      ```
* 性能调优的一些建议：
    1. NumberFormat 相当慢，尽量避免使用它。
    2. String.split—不管是编码或是解码UTF8的字符串都是慢的超出你的想像，使用合适的Writable类型。
    3. 使用StringBuffer.append来连接字符串

### stream
*   见python

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
* Join
    * 两表关联
    * ***使用DistributedCache.addCacheFile()将文件加入到所有Map的缓存里,最好在setup中执行***
* 使用第三方jar
    * FileSystem fs = FileSystem.get(URI.create(args[0]), conf);
    * DistributedCache.addFileToClassPath(new Path("/user/lib/jedis-2.0.0.jar"), conf, fs);




