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
//import java.nio.charset.StandardCharsets;
public class FraudDetection {
	
	//if your .csv uses different field names, change them here
	public static final String OUTPUT_FILE="FlaggedRecords.csv";
	public static final String FIRST_NAME="Cardholder First Initial";
	public static final String LAST_NAME="Cardholder Last Name";
	public static final String DESCRIPTION="Description";
	public static final String CAT_CODE="Merchant Category Code (MCC)";
	public static final String AMOUNT="Amount";
	public static final String VENDOR="Vendor";
	public static final String DATE="Transaction Date";

	public static void main(String[] args) {]
		try{
			//create parser to read through file (REPLACE W ARGS[0])
			Reader in = new FileReader("oklahoma-pcard-FY2014.csv");
			CSVParser parser = CSVParser.parse(in, CSVFormat.EXCEL.withHeader());
			//set up output file, and overwrite whatever was already there
			File file= new File(OUTPUT_FILE);
			file.delete();
			//create writer for output file, and feed it to a csvPrinter for formatting purposes
			BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, true));
			CSVPrinter printer = new CSVPrinter(writer,CSVFormat.EXCEL.withHeader());
			
			//set up map to store records by person (Initial+Last: List<CSVRecord>)
			HashMap users = new HashMap<String, ArrayList<CSVRecord>>();
			//set up map to store records by airline
			HashMap airlines = new HashMap<String, ArrayList<CSVRecord>>();
			
			//loop through each record
			for(CSVRecord record: parser) {
				//put this record in the map 
				String userID = record.get(FIRST_NAME)+" "+record.get(LAST_NAME);
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
				if(record.get(DESCRIPTION).equals("AIR TRAVEL")) {
					String airline = record.get(CAT_CODE);
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
				String amountstr = record.get(AMOUNT);
				String vendor=record.get(VENDOR);
				Float amount = Float.parseFloat(amountstr);
				if(amount>=50000 || amount%100==0) {
					printer.printRecord(record);
				}
				else if(vendor.contains("PAWN ") || vendor.contains("RESORT")|| vendor.contains("CASINO")) {
					printer.printRecord(record);
				}
				
				
			}
			
			//go through airlines and flag all records involving those with fewer than 10 transactions
			for(Object key : airlines.keySet()) {
				ArrayList<CSVRecord> records = (ArrayList<CSVRecord>) airlines.get(key);
				if(records.size()<10) {
					System.out.println(key);
					for(CSVRecord record: records) {
						printer.printRecord(record);
					}
				}
			}
			
			//go through users, and flag all sets of multiple large transactions in a single day
			for(Object key: users.keySet()) {
				ArrayList<CSVRecord> records = (ArrayList<CSVRecord>) users.get(key);
				HashMap largeTransactions = new HashMap<String, CSVRecord>();
				for(CSVRecord record: records) {
					String date = record.get(DATE);
					Float amount = Float.parseFloat(record.get(AMOUNT));
					if(amount>=20000 && amount<50000) {
						if(largeTransactions.containsKey(date)) {
							if(largeTransactions.get(date)!=null) {
								printer.printRecord(record);
								printer.printRecord((CSVRecord)largeTransactions.get(date));
								largeTransactions.put(date, null);
							}else {
								printer.printRecord(record);
							}
						}else {
							largeTransactions.put(date,record);
						}
					}
				}
			}
			
			printer.close();
			writer.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
}
