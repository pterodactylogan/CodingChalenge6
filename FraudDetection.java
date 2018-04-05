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
	
	//final constants for .csv field names and file names
	public static final String FRAUD_FILE="FlaggedRecords.csv";
	public static final String ERROR_FILE="Warnings.csv";
	
	public static final String FIRST_NAME="Cardholder First Initial";
	public static final String LAST_NAME="Cardholder Last Name";
	public static final String DESCRIPTION="Description";
	public static final String CAT_CODE="Merchant Category Code (MCC)";
	public static final String AMOUNT="Amount";
	public static final String VENDOR="Vendor";
	public static final String DATE="Transaction Date";
	public static final String YEAR_MON="Year-Month";
	public static final String AGENCY_ID="Agency Number";
	public static final String AGENCY_NAME="Agency Name";
	public static final String POST_DATE="Posted Date";

	public static void main(String[] args) {
		try{
			//create parser to read through file
			Reader in = new FileReader(args[0]);
			CSVParser parser = CSVParser.parse(in, CSVFormat.EXCEL.withHeader());
			//set up output files, and overwrite whatever was already there
			File file= new File(FRAUD_FILE);
			file.delete();
			File file2=new File(ERROR_FILE);
			file2.delete();
			//create writers for output files, and feed them to csvPrinters for formatting purposes
			BufferedWriter writer = new BufferedWriter(new FileWriter(FRAUD_FILE, true));
			CSVPrinter printer = new CSVPrinter(writer,CSVFormat.EXCEL.withHeader("Reason Flagged", YEAR_MON, AGENCY_ID, AGENCY_NAME,LAST_NAME,FIRST_NAME,DESCRIPTION,AMOUNT,VENDOR,DATE,POST_DATE,CAT_CODE));
			
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(ERROR_FILE, true));
			CSVPrinter printer2 = new CSVPrinter(writer2,CSVFormat.EXCEL.withHeader("Reason Flagged", YEAR_MON, AGENCY_ID, AGENCY_NAME,LAST_NAME,FIRST_NAME,DESCRIPTION,AMOUNT,VENDOR,DATE,POST_DATE,CAT_CODE));
			
			//set up map to store records by person (Initial+Last: List<CSVRecord>)
			HashMap users = new HashMap<String, ArrayList<CSVRecord>>();
			//set up map to store records by airline
			HashMap airlines = new HashMap<String, ArrayList<CSVRecord>>();
			
			//loop through each record
			for(CSVRecord record: parser) {
				//put this record in the user map 
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
					writer.write("Suspicious Transaction Amount,");
					printer.printRecord(record);
				}
				else if(vendor.contains("PAWN ") || vendor.contains("RESORT")|| vendor.contains("CASINO")) {
					writer.write("Suspicious Vendor,");
					printer.printRecord(record);
				}
				
				String description = record.get("Description");
				if(description.matches("[!-?]*") || description.equals("") || description.equals("PCE")){
					writer2.write("Unhelpful Description,");
					printer2.printRecord(record);
				}
				
				
			}
			
			//go through airlines and flag all records involving those with fewer than 10 transactions
			for(Object key : airlines.keySet()) {
				ArrayList<CSVRecord> records = (ArrayList<CSVRecord>) airlines.get(key);
				if(records.size()<10) {
					for(CSVRecord record: records) {
						writer.write("Unusual Airline,");
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
					if(amount>=20000 && amount<50000) { //>=50000 has already been checked
						if(largeTransactions.containsKey(date)) {
							if(largeTransactions.get(date)!=null) {
								//if there's already a high transaction for this day, flag both
								writer.write("Suspicious Same-Day Transactions,");
								printer.printRecord(record);
								writer.write("Suspicious Same-Day Transactions,");
								printer.printRecord((CSVRecord)largeTransactions.get(date));
								//then put a null value into map at this date
								//so we know suspicious transactions have already been found
								largeTransactions.put(date, null);
							}else {
								//if the value is null this date is already suspicous, just flag this record
								writer.write("Suspicious Same-Day Transactions,");
								printer.printRecord(record);
							}
						}else {
							largeTransactions.put(date,record);
						}
					}
				}
			}
			
			printer.close();
			printer2.close();
			writer.close();
			writer2.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
}
