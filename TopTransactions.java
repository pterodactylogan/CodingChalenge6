/*Coding sprint: fraud detection
 * main takes n, fname where n is a number of top transactions and fname is a filename
 * breaks if multiple of the top n transactions are for the same amount
 */

import java.util.*;

public class TopTransactions {
	
	public static void main(String[] args) {
		
		int m=Integer.parseInt(args[0]);
		TreeMap<Float,String> tmap = new TreeMap<Float,String>();
		Scanner input = new Scanner(System.in);
		while(input.hasNext()) {
			String line = input.nextLine();
			String[] segments = line.split("( )+");
			String transaction = segments[0]+" "+segments[1];
			float amount = Float.parseFloat(segments[2]);
			tmap.put(amount,transaction);
		}
		for(int i=0;i<m;i++) {
			float highest = tmap.lastKey();
			System.out.println(tmap.get(highest)+" "+highest);
			tmap.remove(highest);
		}
	}
}
