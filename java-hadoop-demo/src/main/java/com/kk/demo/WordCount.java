package com.kk.demo;

import com.kk.util.ArgsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {
	public static final Log logger = LogFactory.getLog(WordCount.class);

	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Arrays.asList(args));
		// 过滤参数，如果第一个参数是类名则过滤掉
		args = ArgsUtil.filterClass(args);
		System.out.println(Arrays.asList(args));

		Configuration conf = new Configuration();

		// 过滤参数
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		System.out.println(Arrays.asList(otherArgs));
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}

		// job 名称
		Job job = new Job(conf, "wordcount");

		// jbo执行的类
		job.setJarByClass(WordCount.class);

		// 输出格式
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// 设置 map reduce执行类
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		// 设置输入和输出格式
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// input和output文件， output目录不能存在
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		// 执行job
		job.waitForCompletion(true);
	}
}
