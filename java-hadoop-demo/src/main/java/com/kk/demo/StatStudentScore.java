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

public class StatStudentScore extends Configured implements Tool {
    private static ConsoleLogger logger = new ConsoleLogger();

    public static class Map extends
            Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            logger.info("mapper:" + key.toString() + ".."
                    + value.toString());// 打log，可以在hadoop平台的web服务上访问/complete/task/tasklogs
            String line = value.toString();
            if (StringUtils.isBlank(line)) {
                return;
            }
            String[] conts = StringUtils.split(line);
            if (conts.length != 2) {
                logger.error(line + "格式不正确");
                return;
            }
            String name = conts[0];
            int score = NumberUtils.toInt(conts[1]);
            context.write(new Text(name), new IntWritable(score));
        }

    }

    public static class Reduce extends
            Reducer<Text, IntWritable, Text, IntWritable> {

        protected void reduce(Text key, Iterable<IntWritable> values,
                              Context context) throws IOException, InterruptedException {
            int sum = 0;
            int count = 0;
            for (IntWritable value : values) {
                count++;
                sum += value.get();
            }
            context.write(key, new IntWritable(sum / count));
        }

    }

    public int run(String[] args) throws Exception {
        Job job = new Job();

        job.setJobName("StatStudentScore");
        job.setJarByClass(StatStudentScore.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        boolean ret = job.waitForCompletion(true);
        return ret ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.asList(args));

        // 通过toolrunner启动
//        int ret = ToolRunner.run(new StatStudentScore(), args);
        int ret = ToolRunner.run(new Configuration(), new StatStudentScore(), args);
        System.exit(ret);
    }
}
// 以下为输入样例
// kongzhihui 2
// kongzhihui 5
// kongzhihui 1
// kongzhihui 1
// kongzhihui 1
//
// xupo 7
// xupo 1
// xupo 1
// xupo 4
// xupo 1
//
// xienan 2
// xienan 1
// xienan 9

