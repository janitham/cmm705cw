package mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class IntSumReducer extends
        Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(
            final Text key,
            final Iterable<IntWritable> values,
            final Context context
    ) throws IOException, InterruptedException {
        // Count the occurrences with the same key
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        // Set the count to the result object
        result.set(sum);
        // Set output with the key
        context.write(key, result);
    }
}