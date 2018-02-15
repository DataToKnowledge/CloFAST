package converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Create a file IBMgenDatasets.sh for the generation of datasets
 * 
 * @author fabiana
 * 
 */
public class GenerateDBsh {

	// containsItemset the possible values for the parameter -ncust, that is
	// number_of_customers_in_000s (default: 100)
	double[] d;
	// containsItemset the possible values for the parameter -slen
	// avg_trans_per_customer (default: 10)
	double[] c;
	// containsItemset the possible values for the parameter -tlen
	// avg_items_per_transaction (default: 2.5)
	double[] t;
	// containsItemset the possible values for the parameter -nitems
	// number_of_different_items_in_000s (default: 10)
	double[] n;

	/**
	 * 
	 * @param d containsItemset the possible values for the parameter -ncust, that is number_of_customers_in_000s (default: 100)
	 * @param c containsItemset the possible values for the parameter -slen avg_trans_per_customer (default: 10
	 * @param t containsItemset the possible values for the parameter -tlen avg_items_per_transaction (default: 2.5)
	 * @param n containsItemset the possible values for the parameter -nitems number_of_different_items_in_000s (default: 10)
	 * @throws IOException
	 */
	public GenerateDBsh(double[] d, double[] c, double[] t, double[] n) throws IOException {
		this.d = d;
		this.t = t;
		this.c = c;
		this.n = n;
		BufferedWriter out = new BufferedWriter(new FileWriter("IBMgenDatasets.sh"));
		int s=6;
		int i=4;
		for(int di=0; di<d.length; di++){
			for(int ti=0; ti<t.length; ti++){
				for(int ci=0; ci<c.length; ci++){
					for(int ni=0; ni<n.length; ni++){
						String datasetName="D"+d[di]+"C"+c[ci]+"T"+t[ti]+"N"+n[ni]+"S"+s+"I"+i;
						out.write("~/data_generator/bin/seq_data_generator seq -ncust "+d[di]+" -slen "+c[ci]+" -tlen "+t[ti]+" -nitems "+n[ni]+" -seq.npats 2000 -lit.npats 5000 -seq.patlen "+s+" -lit.patlen "+i+" -fname "+datasetName+"\n");
					}
				}
			}
		}
		out.close();
		System.out.println("Generation sh end");
	}
	public static void main(String[] args) {
		double[] d={20};
		double[] t={20};
		double[] c={30};
		double[] n={0.1,0.5, 2.5};
		try {
			GenerateDBsh generator= new GenerateDBsh(d, c, t, n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
