### py文件需要时unix格式， vim中  set ff=unix即可。  py文件需要是可执行权限

### 执行方式
* hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-streaming-1.2.1.jar -mapper ./mapper.py -reducer ./reducer.py -input /kk/data/wordcount/input -output /kk/data/wordcount/output -file ./mapper.py -file ./reducer.py
* cat input |./mapper.py |./reducer.py