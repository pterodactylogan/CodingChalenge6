/*
 * Coding Challenge 6
 * Authors: Logan Swanson, Sherri Lin
 */


import java.util.*;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class FraudDetection {

	public static void main(String[] args) {
		File in = new File("oklahoma-pcard-FY2014.csv");
		try{
			CSVParser parser = CSVParser.parse(in, StandardCharsets.ISO_8859_1, CSVFormat.EXCEL.withHeader());
			File file= new File("Test.csv");
			file.delete();
			BufferedWriter writer = new BufferedWriter(new FileWriter("Test.csv", true));
			CSVPrinter printer = new CSVPrinter(writer,CSVFormat.EXCEL.withHeader());
			for(CSVRecord record: parser) {
				//STORE RECORD IN AN INTELLIGENT DATASET
				String amount = record.get("Amount");
				if(Float.parseFloat(amount)>=50000) {
					//System.out.println(record);
					printer.printRecord(record);
				}
			}
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
}
