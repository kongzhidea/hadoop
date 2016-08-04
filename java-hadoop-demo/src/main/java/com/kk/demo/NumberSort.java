package com.kk.demo;

import com.kk.util.ConsoleLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Arrays;

public class NumberSort extends Configured implements Tool {

    public static class Map extends
            Mapper<LongWritable, Text, IntWritable, IntWritable> {

        IntWritable ONE = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            if (value == null || StringUtils.isBlank(value.toString())) {
                return;
            }
            int num = NumberUtils.toInt(value.toString());
            context.write(new IntWritable(num), ONE);
        }

    }

    public static class Reduce extends
            Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

        private static IntWritable count = new IntWritable(1);

        protected void reduce(IntWritable key, Iterable<IntWritable> values,
                              Context context) throws IOException, InterruptedException {
            for (IntWritable value : values) {
                context.write(count, key);
                count.set(count.get() + 1);
            }
        }

    }

    /**
     * 自定义partition函数; Map的结果，会通过partition分发到Reducer上 分片从0开始。
     * <p/>
     * 输入是Map的结果对<key,
     * value>和Reducer的数目，输出则是分配的Reducer（整数编号）。就是指定Mappr输出的键值对到哪一个reducer上去
     * 。系统缺省的Partitioner是HashPartitioner
     * ，它以key的Hash值对Reducer的数目取模，得到对应的Reducer。这样保证如果有相同的key值
     * ，肯定被分配到同一个reducre上。如果有N个reducer，编号就为0,1,2,3……(N-1)。
     *
     * @author kk
     */
    public static class Partion extends Partitioner<IntWritable, IntWritable> {

        @Override
        public int getPartition(IntWritable key, IntWritable value,
                                int numPartitions) {
            long max = Integer.MAX_VALUE;
            long bound = max / numPartitions + 1;
            long keyNum = key.get();
            for (int i = 0; i < numPartitions; i++) {
                if (keyNum < bound * (i + 1) && keyNum > bound * i) {
                    return i;
                }
            }
            return -1;
        }

    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        job.setJobName("NumberSort");
        job.setJarByClass(NumberSort.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        //设置分片
        job.setPartitionerClass(Partion.class);

        // 设置reducer数量 可以在hadoop jar *.jar className -Dmapred.reduce.tasks=2
        // 这样来指定reduce数量
        job.setNumReduceTasks(2);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        boolean ret = job.waitForCompletion(true);
        return ret ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.asList(args));

        int ret = ToolRunner.run(new Configuration(), new NumberSort(), args);
        System.exit(ret);
    }
}
