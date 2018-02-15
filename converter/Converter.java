package converter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;


public class Converter {

	public static void main(String[] args){
		
		if (args.length != 1){
			System.err.println("specificy the name of the file to convert");
			System.err.println("Example: dataset.data");
			return;
		}
		String inFile = args[0];
		String[] filename = inFile.split("data");
		String outFile = filename[0] + "pxs";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			PrintWriter writer = new PrintWriter(new FileWriter(outFile));
			// process using spam
			System.out.println("Dataset processing");
			String line;
			
			while ((line = reader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				
				int tSize = Integer.parseInt(st.nextToken());
				while (st.hasMoreTokens()) {
					int iSize = Integer.parseInt(st.nextToken());
					for (int i = 0; i < iSize; i++) {
						writer.append(st.nextToken() + " ");
					}
					writer.append("-1 ");
				}
				writer.append("-2\n");
			}
			
			reader.close();
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
