### py文件需要时unix格式， vim中  set ff=unix即可。  py文件需要是可执行权限

### 执行方式
* hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-streaming-1.2.1.jar -mapper ./mapper.py -reducer ./reducer.py -input /kk/data/wordcount/input -output /kk/data/wordcount/output -file ./mapper.py -file ./reducer.py
* cat input |./mapper.py |./reducer.py
* 输入默认为整行

### 使用外部文件
* 假如你跑的job除了输入以外还需要一些额外的文件（side data），有两种选择：
* 大文件
	* 所谓的大文件就是大小大于设置的local.cache.size的文件，默认是10GB。这个时候可以用***-file***来分发。
	* 格式：假如我要加多一个sideData.txt给python脚本用：
	* $HADOOP_HOME/bin/hadoop  jar $HADOOP_HOME/hadoop-streaming.jar \
	    -input iputDir \
	    -output outputDir \
	    -mapper mapper.py \
	    -file mapper.py \
	    -reducer reduer.py \
	    -file reducer.py \
	    -file sideDate.txt
	* 在python脚本里，只要把这个文件当成自己同一目录下的本地文件来打开就可以了。比如：
	* f = open("sideData.txt")
	* 注意这个file是只读的，不可以写。
* 小文件
	* 如果是比较小的文件，想要提高读写速度可以将它放在distributed cache里（也就是每台机器都有自己的一份copy，不需要网络IO就可以拿到数据）。这里要用到的参数是***-cachefile***，写法和用法上一个一样，就是-file改成-cachefile而已。