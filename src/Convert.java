
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author G0161
 * @version 1.0.1
 *
 */
public class Convert{
	/**
	 * Takes the name of the gpx file from the Driver then replaces the gpx tag with csv. 
	 * Both names are then passed into the csvWriter method
	 * @param filename
	 * 
	 */
	public static void convertFile(String filename) throws IOException {
		String csvName = filename.replace("gpx", "csv");
		csvWriter(filename,csvName);
	}
	/**
	 * Takes file name and uses the xml library to grab requested data from each Node
	 * @param gpx filename
	 * @param data name in gpx file
	 * @return ArrayList<Double> of data.
	 * 
	 */
	public static ArrayList<Double> collectData(String filename, String name) throws IOException{
		File gpx = new File(filename);
		String data;
		ArrayList<Double> Data = new ArrayList<>();
		
		//Get Document Builder
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder build;
		
		try {
			build = fact.newDocumentBuilder();
			
			//Get File
			Document file = build.parse(gpx);
			
			//Normalize xml structure
			file.getDocumentElement().normalize();
	
			NodeList tripList = file.getElementsByTagName("trkpt");
			
			// Grabs Data from the NodeList, cleans it then adds it to the ArrayList
			for(int x = 0; x < tripList.getLength(); x++) {
				Node trip = tripList.item(x);
				
				if(trip.getNodeType() ==Node.ELEMENT_NODE) {
					Element tripEle = (Element) trip;
					data = tripEle.getAttribute(name);
					Data.add(dataCleaner(data));
					}
				}		
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Data;		
	}
	
	/**
	 * takes a string and uses a regex pattern to remove all unwanted values
	 * @return correctly formatted data Parsed as a Double
	 */
	public static Double dataCleaner(String data) {
		//replaces all punctuation, letters, and whitespace excluding '.''-'
		data = data.replaceAll("[\\p{Punct}\\p{Alpha}\\p{Space}&&[^.-]]","");
		//Parses as a double and returns it
		return Double.parseDouble(data);
	}
	/**
	 * Calls the methods to collect the ArrayList<Double> of the Latitude and Longitude.
	 * It creates the CSV file with the requested name if it does not already exist.
	 * Then it writes everything in the desired format into a BufferedWriter. 
	 * The BufferedWriters toString() method is then passed in to the FileWriter which writes everything into the CVS file.
	 * @param The name of the gpx file
	 * @param The name of the csv file
	 * 
	 */
	public static void csvWriter(String gpx, String csv) throws IOException {
		ArrayList<Double> Lat = collectData(gpx,"lat");
		ArrayList<Double> Lon = collectData(gpx,"lon");
		File CSV = new File(csv);
		CSV.createNewFile();
		FileWriter fw = new FileWriter(CSV);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Time,Latitude,Longitude\n");
		for(int i = 0; i < Lat.size(); i++) {
			bw.write((i*5)+","+Lat.get(i)+","+Lon.get(i)+"\n");
		}
		String completeCSV = bw.toString();
		fw.write(completeCSV);
		bw.close();
		fw.close();
	}
}
