import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Q2code {
    public static void main(String[] args) {
        String inputFile = "path_to_input_file.txt"; // Replace with the input file path
        String outputFile = "path_to_output_file.txt"; // Replace with the desired output file path

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            Pattern pattern = Pattern.compile("^(?:\\s*\\d+\\s+)(\\d+)\\sms\\s+(\\d+)\\sms\\s+(\\d+)\\sms\\s+.*\\[(.*?)\\]|.*\\((.*?)\\).*");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int delay1 = Integer.parseInt(matcher.group(1));
                    int delay2 = Integer.parseInt(matcher.group(2));
                    int delay3 = Integer.parseInt(matcher.group(3));
                    String ipAddress = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
                    double averageDelay = (delay1 + delay2 + delay3) / 3.0;

                    writer.write(ipAddress + " " + averageDelay + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
