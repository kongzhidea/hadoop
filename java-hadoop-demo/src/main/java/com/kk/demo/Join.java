package com.kk.demo;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 多表关联,类似linux的join；但是其中一张表较小
 * 
 * 举例:
 * 
 * 输入为2个文件，文件一内容如下
	•空格分割：用户名 手机号 年龄
	•内容样例
	•Tom 1314567890 14
	•文件二内容
	•空格分割：手机号 地市
	•内容样例
	•13124567890 hubei
	•需要统计出的汇总信息为 用户名 手机号 年龄 地市


	•使用DistributedCache.addCacheFile()将文件加入到所有Map的缓存里,最好在setup中执行
	•在Map函数里读取该文件，进行Join
	•  将结果输出到reduce
	•需要注意的是
	•DistributedCache需要在生成Job作业前使用
 * @author kk
 * 
 */
public class Join {
	/**
	 * 
	 * @author kk
	 * 
	 */
	public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

		private Map<String, String> joinData = new HashMap<String, String>();

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			try {
				Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context
						.getConfiguration());
				if (null != cacheFiles && cacheFiles.length > 0) {
					String line;
					String[] tokens;
					BufferedReader br = new BufferedReader(new FileReader(
							cacheFiles[0].toString()));
					try {
						while ((line = br.readLine()) != null) {
							if (StringUtils.isBlank(line)) {
								continue;
							}
							tokens = StringUtils.split(line);
							joinData.put(tokens[0], tokens[1]);

						}
					} finally {
						br.close();
					}
				}
				System.out.println(joinData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			if (StringUtils.isBlank(value.toString())) {
				return;
			}
			String[] conts = StringUtils.split(value.toString());
			String joinValue = joinData.get(conts[1]);
			if (null != joinValue) {
				context.write(new Text(value.toString()), new Text(joinValue));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: <infile-1> <infile-2(small)>  <out>");
			System.exit(1);
		}
		Job job = new Job(conf, Join.class.getName());

		job.setJarByClass(Join.class);

		// DistributedCache的原理是将小的那个文件复制到所有节点上。,在mapper的setUp中去取
		DistributedCache.addCacheFile(new Path(args[1]).toUri(),
				job.getConfiguration());
		// 只设置mapper
		job.setMapperClass(MyMapper.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);//

		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
// 输入
// file1
// Tom 13124567890 14
// file2
// 13124567890 hubei
// 输出
// Tom 1314567890 14 hubei