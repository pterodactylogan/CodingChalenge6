/*
 * Coding Challenge 6
 * Authors: Logan Swanson, Sherri Lin
 * This program reads in the specified csv file of credit card transactions
 * and creates a new csv containing all transactions flagged for possible fraud
 * see accompanying README.txt for details
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
		//REPLACE W args[0]
		//set up input file
		File in = new File("oklahoma-pcard-FY2014.csv");
		try{
			//create parser to read through file
			CSVParser parser = CSVParser.parse(in, StandardCharsets.ISO_8859_1, CSVFormat.EXCEL.withHeader());
			//set up output file, and overwrite whatever was already there
			File file= new File("FlaggedRecords.csv");
			file.delete();
			//create writer for output file, and feed it to a csvPrinter for formatting purposes
			BufferedWriter writer = new BufferedWriter(new FileWriter("FlaggedRecords.csv", true));
			CSVPrinter printer = new CSVPrinter(writer,CSVFormat.EXCEL.withHeader());
			
			//set up map to store records by person (Initial+Last: List<CSVRecord>)
			HashMap users = new HashMap<String, ArrayList<CSVRecord>>();
			//set up map to store records by airline
			HashMap airlines = new HashMap<String, ArrayList<CSVRecord>>();
			
			//loop through each record
			for(CSVRecord record: parser) {
				//put this record in the map 
				String userID = record.get("Cardholder First Initial")+". "+record.get("Cardholder Last Name");
				if(users.containsKey(userID)) {
					ArrayList<CSVRecord> records = (ArrayList<CSVRecord>) users.get(userID);
					records.add(record);
					users.put(userID, records);
				}else {
					ArrayList<CSVRecord> records=new ArrayList();
					records.add(record);
					users.put(userID,records);
				}
				
				//if this transaction was for air travel, add to airlines map
				if(record.get("Description").equals("AIR TRAVEL")) {
					String airline = record.get("Merchant Category Code (MCC)");
					if(airlines.containsKey(airline)) {
						ArrayList<CSVRecord> records = (ArrayList<CSVRecord>) users.get(userID);
						records.add(record);
						airlines.put(airline,records);
					}else {
						//System.out.println("adding "+airline);
						ArrayList<CSVRecord> records = new ArrayList();
						records.add(record);
						airlines.put(airline,records);
					}
				}
				
				//check single-record flags
				String amountstr = record.get("Amount");
				Float amount = Float.parseFloat(amountstr);
				if(amount>=50000 || amount%100==0) {
					printer.printRecord(record);
				}
				String vendor=record.get("Vendor");
				if(vendor.contains("PAWN ") || vendor.contains("RESORT")|| vendor.contains("CASINO")) {
					printer.printRecord(record);
				}
				
				
			}
			printer.close();
			writer.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
}
