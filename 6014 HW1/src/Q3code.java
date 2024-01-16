import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Q3code {
    public static void main(String[] args) {
        String filePath = "Q3_ping.txt"; //input file
        parsePingData(filePath);
    }

    public static void parsePingData(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        int totalDelay = 0;
        int count = 0;
        int minDelay = Integer.MAX_VALUE;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            Pattern pattern = Pattern.compile("时间=(\\d+)ms");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int delay = Integer.parseInt(matcher.group(1));
                    totalDelay += delay;
                    minDelay = Math.min(minDelay, delay);
                    count++;
                }
            }

            if (count > 0) {
                int avgDelay = totalDelay / count;
                int avgQueueDelay = avgDelay - minDelay;
                System.out.println("Average Round Trip Queue Delay: " + avgQueueDelay + " ms");
            } else {
                System.out.println("No ping data found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
