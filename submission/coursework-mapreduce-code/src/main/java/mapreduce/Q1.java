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

public class Q1 {

    public static class DeliveryMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(
                final Object key,
                final Text lineText,
                final Context context
        ) {
            try {
                final String line = lineText.toString();
                final String extraRuns = line.split(",")[8];
                final String isWicket = line.split(",")[11];
                final String totalRuns = line.split(",")[9];

                // If any extra runs were occurred count them
                if (Integer.parseInt(extraRuns) > 0) {
                    word.set("extraRuns");
                    context.write(word, one);
                }

                // If any wicket was taken count them
                if ("1".equals(isWicket)) {
                    word.set("wicketCount");
                    context.write(word, one);
                }

                // If any no runs were observed count them
                if ("0".equals(totalRuns)) {
                    word.set("noRuns");
                    context.write(word, one);
                }
            } catch (Exception e) {
                // There are some issues in the dataset causes number format exception
                // Ignoring them
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "Delivery Summary");
        job.setJarByClass(Q1.class);
        job.setMapperClass(DeliveryMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}