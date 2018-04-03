import java.util.*;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class FraudDetection {

	public static void main(String[] args) {
		File in = new File("oklahoma-pcard-FY2014.csv");
		try{
			CSVParser parser = CSVParser.parse(in, StandardCharsets.ISO_8859_1, CSVFormat.EXCEL.withHeader());

			for(CSVRecord record: parser) {
				String amount = record.get("Amount");
				System.out.println(amount);
			}
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
}
