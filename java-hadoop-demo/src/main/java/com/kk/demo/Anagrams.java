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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.*;

public class Anagrams extends Configured implements Tool {

    public static class Map extends
            Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            if (value == null || StringUtils.isBlank(value.toString())) {
                return;
            }
            char[] chars = value.toString().toCharArray();
            Arrays.sort(chars);
            String ret = new String(chars);
            context.write(new Text(ret), value);
        }
    }

    public static class Reduce extends
            Reducer<Text, Text, Text, Text> {


        protected void reduce(Text key, Iterable<Text> values,
                              Context context) throws IOException, InterruptedException {
            List<String> list = new ArrayList<String>();
            for (Text val : values) {
                list.add(val.toString());
            }
            context.write(key, new Text(StringUtils.join(list, ",")));
        }

    }

    public int run(String[] args) throws Exception {

        Configuration configuration = getConf();
        // 传递参数
        Job job = new Job(configuration, "Anagrams");

        job.setJarByClass(Anagrams.class);

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

        int ret = ToolRunner.run(new Configuration(), new Anagrams(), args);
        System.exit(ret);
    }
}
