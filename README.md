# hadoop

### hadoop 常见shell命名
* alias hls="hadoop fs -ls "
* alias hlsr="hadoop fs -lsr "
* alias hmkdir="hadoop fs -mkdir "
* alias hmv="hadoop fs -mv "
* alias hcat="hadoop fs -cat "
* alias hrm="hadoop fs -rm "
* alias hrmr="hadoop fs -rmr "
* alias hput="hadoop fs -put "
* alias hget="hadoop fs -get "
* alias hcp="hadoop fs -cp "
* alias hdu="hadoop fs -du "
* alias hdus="hadoop fs -dus "
* alias htail="hadoop fs -tail "
* alias htest="hadoop fs -test "
* alias htext="hadoop fs -text "
* alias htouchz="hadoop fs -touchz"





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