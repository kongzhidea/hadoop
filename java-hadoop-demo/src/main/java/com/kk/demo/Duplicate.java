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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Arrays;

public class Duplicate extends Configured implements Tool {
    private static ConsoleLogger logger = new ConsoleLogger();

    public static class Map extends
            Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(value, new Text());
        }

    }

    public static class Reduce extends
            Reducer<Text, Text, Text, Text> {

        protected void reduce(Text key, Iterable<Text> values,
                              Context context) throws IOException, InterruptedException {
            context.write(key, new Text());
        }

    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        job.setJobName("Duplicate");
        job.setJarByClass(Duplicate.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        boolean ret = job.waitForCompletion(true);
        return ret ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.asList(args));

        // 通过toolrunner启动
        int ret = ToolRunner.run(new Configuration(), new Duplicate(), args);
        System.exit(ret);
    }
}

