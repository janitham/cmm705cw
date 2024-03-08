package mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q2 {

    public static class WicketsMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(
                final Object key,
                final Text lineText,
                final Context context
        ) {
            final String line = lineText.toString();
            try {
                final String bowlingTeam = line.split(",")[17].toUpperCase().trim();
                final String isWicket = line.split(",")[11].trim();
                // If any wicket was taken recorded them with the team
                if ("1".equals(isWicket.trim())) {
                    word.set(bowlingTeam);
                    context.write(word, one);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Ignore any exception was occured
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q2 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "Wickets Count");
        job.setJarByClass(Q2.class);
        job.setMapperClass(WicketsMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}