package com.kk.demo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * 一个简单grep程序，可从文档中提取包含莫些字符串的行
 */
public class Grep extends Configured implements Tool {
    
    public static class GrepMap extends
            Mapper<LongWritable, Text, Text, NullWritable> {

        public void map(LongWritable line, Text value, Context context)
                throws IOException, InterruptedException {
            // 通过Configuration获取参数
            String str = context.getConfiguration().get("grep");
            if (value.toString().contains(str)) {
                context.write(value, NullWritable.get());
            }
        }
    }

    public int run(String[] args) throws Exception {

        if (args.length != 3) {
            System.out.println("Usage: <in> <output> <grep:str>");
            System.exit(1);
        }

        Configuration configuration = getConf();
        // 传递参数
        configuration.set("grep", args[2]);
        Job job = new Job(configuration, "Grep job");

        job.setJarByClass(Grep.class);

        job.setMapperClass(GrepMap.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        int ret = job.waitForCompletion(true) ? 0 : 1;
        return ret;
    }

    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new Grep(), args);
        System.exit(ret);
    }

}
