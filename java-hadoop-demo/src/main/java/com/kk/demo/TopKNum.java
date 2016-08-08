package com.kk.demo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
import java.util.Comparator;
import java.util.PriorityQueue;

public class TopKNum extends Configured implements Tool {

    public static class Map extends
            Mapper<LongWritable, Text, IntWritable, NullWritable> {

        private int K;
        // 最小堆
        private PriorityQueue<Integer> heap = null;

        // 初始化
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            K = context.getConfiguration().getInt("K", 0);

            heap = new PriorityQueue<Integer>(K, new Comparator<Integer>() {
                public int compare(Integer a0, Integer a1) {
                    return a0 - a1;
                }
            });

        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            if (value == null || StringUtils.isBlank(value.toString())) {
                return;
            }
            int num = NumberUtils.toInt(value.toString());

            add(num);
        }

        // map执行结束
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
            while (heap.size() > 0) {
                context.write(new IntWritable(heap.poll()), NullWritable.get());
            }
        }

        private void add(int temp) {// 实现插入
            if (heap.size() < K) {
                heap.offer(temp);
            } else {
                if (temp > heap.peek()) {
                    heap.poll();
                    heap.offer(temp);
                }
            }
        }

    }

    public static class Reduce extends
            Reducer<IntWritable, NullWritable, IntWritable, NullWritable> {

        private int K;
        // 最小堆
        private PriorityQueue<Integer> heap = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            K = context.getConfiguration().getInt("K", 0);

            heap = new PriorityQueue<Integer>(K, new Comparator<Integer>() {
                public int compare(Integer a0, Integer a1) {
                    return a0 - a1;
                }
            });
        }


        protected void reduce(IntWritable key, Iterable<NullWritable> values,
                              Context context) throws IOException, InterruptedException {
            for (NullWritable item : values) {
                add(key.get());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);

            while (heap.size() > 0) {
                context.write(new IntWritable(heap.poll()), NullWritable.get());
            }
        }

        private void add(int temp) {// 实现插入
            if (heap.size() < K) {
                heap.offer(temp);
            } else {
                if (temp > heap.peek()) {
                    heap.poll();
                    heap.offer(temp);
                }
            }
        }
    }

    public int run(String[] args) throws Exception {

        Configuration configuration = getConf();
        // 传递参数
        configuration.setInt("K", Integer.valueOf(args[2]));
        Job job = new Job(configuration, "TopKNum");

        job.setJarByClass(TopKNum.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);


        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        boolean ret = job.waitForCompletion(true);
        return ret ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.asList(args));

        int ret = ToolRunner.run(new Configuration(), new TopKNum(), args);
        System.exit(ret);
    }
}
