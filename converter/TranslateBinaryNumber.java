package converter;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

@Deprecated
public class TranslateBinaryNumber {

	public static void loadFileBinaryFormat(String file) throws IOException {
		// String thisLine;
		// BufferedReader myInput = null;
		DataInputStream myInput = null;
		try {
			FileInputStream fin = new FileInputStream(file);
			String[] filename = file.split("data");
			FileWriter fw = new FileWriter(filename[0] + "txt");
			BufferedWriter out = new BufferedWriter(fw);
			myInput = new DataInputStream(fin);
			
			String line = "";
			while (myInput.available() != 0) {
				int value = INT_little_endian_TO_big_endian(myInput.readInt());
				if (value == -2) { // indicate the end of a sequence
					line += value + "\n";
					out.write(line.trim() + "\n");
				} else {
					line += value + " ";
				}
			}
			out.flush();
			out.close();
			fw.close();
			myInput.close();
			fin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// This function was written by Anghel Leonard:
	static int INT_little_endian_TO_big_endian(int i) {
		return ((i & 0xff) << 24) + ((i & 0xff00) << 8) + ((i & 0xff0000) >> 8)
				+ ((i >> 24) & 0xff);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length == 1) {
			String inputFile = args[0];
			TranslateBinaryNumber.loadFileBinaryFormat(inputFile);
		}

	}

}
