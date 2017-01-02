package org.bibliome.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;

public class Sampler extends CLIOParser {
	public static class Individual {
		private final String id;
		private final long[] properties;

		private Individual(String id, long[] properties) {
			super();
			this.id = id;
			this.properties = properties;
		}

		private int getPropertiesNumber() {
			return properties.length;
		}

		public String getId() {
			return id;
		}

		public long[] getProperties() {
			return properties;
		}
	}

	public static class Sample {
		private final List<Individual> population;
		private final List<Individual> sample;
		private final List<Individual> complement;
		private final int propertiesNumber;
		private final int populationSize;
		private final int sampleSize;
		private final float relativeSampleSize;
		private final long[] populationPropertiesSum;
		private final long[] samplePropertiesSum;
		private final double[] samplePropertiesExpect;
		private final double[] propertiesDeviations;
		private double deviation;
		
		private Sample(List<Individual> population, int sampleSize, double[] bias) {
			this.population = population;
			this.sampleSize = sampleSize;
			populationSize = population.size();
			propertiesNumber = population.get(0).getPropertiesNumber();
			relativeSampleSize = ((float) sampleSize) / populationSize;
			sample = population.subList(populationSize - sampleSize, populationSize);
			complement = population.subList(0, populationSize - sampleSize);
			populationPropertiesSum = new long[propertiesNumber];
			sumProperties(population, populationPropertiesSum);
			samplePropertiesSum = new long[propertiesNumber];
			samplePropertiesExpect = propertiesExpectation(populationPropertiesSum, relativeSampleSize, bias);
			propertiesDeviations = new double[propertiesNumber];
		}

		private static void sumProperties(List<Individual> individuals, long[] result) {
			Arrays.fill(result, 0);
			for (Individual ind : individuals) {
				for (int i = 0; i < result.length; ++i) {
					result[i] += ind.properties[i];
				}
			}
		}

		private static double[] propertiesExpectation(long[] populationPropertiesSum, float relativeSampleSize, double[] bias) {
			double[] result = new double[populationPropertiesSum.length];
			for (int i = 0; i < result.length; ++i) {
				if (bias[i] >= 0 && bias[i] <= populationPropertiesSum[i]) {
					result[i] = bias[i];
				}
				else {
					result[i] = relativeSampleSize * populationPropertiesSum[i];
				}
			}
			return result;
		}

		private void iteration(Random random, long seed) {
			random.setSeed(seed);
			shuffle(random);
			sumProperties(sample, samplePropertiesSum);
			deviations();
		}

		private void shuffle(Random random) {
			int n = populationSize - sampleSize;
			for (int i = population.size() - 1; i > n; --i) {
				int j = random.nextInt(i + 1);
				if (i != j) {
					Collections.swap(population, i, j);
				}
			}
		}
		
		private static double deviation(double samplePropertyExpect, long samplePropertySum) {
			if (samplePropertyExpect == 0) {
				return 0;
			}
			return Math.pow(samplePropertyExpect - samplePropertySum, 2) / samplePropertyExpect;
		}

		private void deviations() {
			deviation = 0;
			for (int i = 0; i < samplePropertiesExpect.length; ++i) {
				double d = deviation(samplePropertiesExpect[i], samplePropertiesSum[i]);
				propertiesDeviations[i] = d;
				deviation += d;
			}
		}
		
		public void sample(Random random, Random seeds, int iterations) {
			long bestSeed = 0;
			double bestDeviation = Double.MAX_VALUE;
			for (int i = 0; i < iterations; ++i) {
				long seed = seeds.nextLong();
				iteration(random, seed);
				if (deviation < bestDeviation) {
					bestSeed = seed;
					bestDeviation = deviation;
				}
			}

			iteration(random, bestSeed);
		}

		public List<Individual> getSample() {
			return sample;
		}
		
		public List<Individual> getComplement() {
			return complement;
		}

		public int getPropertiesNumber() {
			return propertiesNumber;
		}

		public int getPopulationSize() {
			return populationSize;
		}

		public int getSampleSize() {
			return sampleSize;
		}

		public float getRelativeSampleSize() {
			return relativeSampleSize;
		}

		public long[] getPopulationPropertiesSum() {
			return populationPropertiesSum;
		}

		public long[] getSamplePropertiesSum() {
			return samplePropertiesSum;
		}

		public double[] getSamplePropertiesExpect() {
			return samplePropertiesExpect;
		}

		public double[] getPropertiesDeviations() {
			return propertiesDeviations;
		}

		public double getDeviation() {
			return deviation;
		}
	}

	private int expectedPopulationSize = 1000;
	private File populationFile;
	private String charset = "UTF-8";
	private int expectedPropertiesNumber = -1;
	private boolean propertiesHeader = false;
	private boolean individualIdentifier = false;
	private char separator = '\t';
	private List<String> propertiesNames;
	private int sampleSize = 0;
	private float sampleRelativeSize = 0.5F;
	private int iterations = 10000;
	private File sampleFile;
	private File reportFile;
	private File complementFile;
	private final Map<String,Double> bias = new HashMap<String,Double>();
	
	private void writeSample(PrintStream out, Sample sample, boolean complement) {
		for (Individual ind : complement ? sample.getComplement() : sample.getSample()) {
			out.print(ind.getId());
			for (long p : ind.getProperties()) {
				out.print(separator);
				out.print(p);
			}
			out.println();
		}
	}
	
	private void writeSampleReport(PrintStream out, Sample sample) {
		out.println("PROPERTY\tPOPULATION\tEXPECTED\tSAMPLE\tCOMPLEMENT\tDEVIATION");
		for (int i = 0; i < sample.propertiesNumber; ++i) {
			out.print(propertiesNames.get(i));
			out.print('\t');
			out.print(sample.populationPropertiesSum[i]);
			out.print('\t');
			out.print(sample.samplePropertiesExpect[i]);
			out.print('\t');
			out.print(sample.samplePropertiesSum[i]);
			out.print('\t');
			out.print(sample.populationPropertiesSum[i] - sample.samplePropertiesSum[i]);
			out.print('\t');
			out.print(sample.propertiesDeviations[i]);
			out.println();
		}
		out.print("TOTAL");
		out.print('\t');
		out.print(sum(sample.populationPropertiesSum));
		out.print('\t');
		out.print(sum(sample.samplePropertiesExpect));
		out.print('\t');
		out.print(sum(sample.samplePropertiesSum));
		out.print('\t');
		out.print(sum(sample.populationPropertiesSum) - sum(sample.samplePropertiesSum));
		out.print('\t');
		out.print(sum(sample.propertiesDeviations));
		out.println();
	}
	
	private static long sum(long[] props) {
		long result = 0;
		for (long p : props) {
			result += p;
		}
		return result;
	}
	
	private static double sum(double[] props) {
		double result = 0;
		for (double p : props) {
			result += p;
		}
		return result;
	}
	
	private List<Individual> loadPopulation() throws IOException {
		List<Individual> result = new ArrayList<Individual>(expectedPopulationSize);
		try (BufferedReader r = openPopulationFile()) {
			int lineno = 0;
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				lineno++;
				List<String> cols = Strings.split(line, separator, -1);
				String id;
				if (individualIdentifier) {
					id = cols.get(0);
					cols = cols.subList(1, cols.size());
				}
				else {
					id = Integer.toString(lineno);
				}
				if (expectedPropertiesNumber == -1) {
					expectedPropertiesNumber = cols.size();
				}
				else {
					if (expectedPropertiesNumber != cols.size()) {
						throw new RuntimeException("line " + lineno + ": expected " + expectedPropertiesNumber + " properties, got " + cols.size());
					}
				}
				if (lineno == 1) {
					if (propertiesHeader) {
						propertiesNames = cols;
						continue;
					}
					propertiesNames = new ArrayList<String>(cols.size());
					for (int i = 0; i < cols.size(); ++i) {
						propertiesNames.add(Integer.toString(i));
					}
				}
				long[] properties = new long[expectedPropertiesNumber];
				for (int i = 0; i < properties.length; ++i) {
					try {
						properties[i] = Long.parseLong(cols.get(i));
					}
					catch (NumberFormatException e) {
						throw new RuntimeException("line " + lineno + ", property " + propertiesNames.get(i) + ": expected number, got " + cols.get(i));
					}
				}
				Individual ind = new Individual(id, properties);
				result.add(ind);
			}
		}
		return result;
	}

	private BufferedReader openPopulationFile() throws IOException {
		InputStream is = new FileInputStream(populationFile);
		Reader r = new InputStreamReader(is, charset);
		return new BufferedReader(r);
	}
	
	@CLIOption("-expectSize")
	public void setExpectedPopulationSize(int expectedPopulationSize) {
		this.expectedPopulationSize = expectedPopulationSize;
	}

	@CLIOption("-charset")
	public void setCharset(String charset) {
		this.charset = charset;
	}

	@CLIOption("-propertiesNumber")
	public void setExpectedPropertiesNumber(int expectedPropertiesNumber) {
		this.expectedPropertiesNumber = expectedPropertiesNumber;
	}

	@CLIOption("-propertiesHeader")
	public void setPropertiesHeader() {
		this.propertiesHeader = true;
	}

	@CLIOption("-identifierColumn")
	public void setIndividualIdentifier() {
		this.individualIdentifier = true;
	}

	@CLIOption("-separator")
	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
	@CLIOption("-size")
	public void setSampleSize(String sampleSize) throws CLIOException {
		try {
			float n = Float.parseFloat(sampleSize);
			if (n <= 0) {
				throw new CLIOException("-size expects a positive number");
			}
			if (n >= 1) {
				this.sampleSize = (int) n;
				this.sampleRelativeSize = 0;
			}
			else {
				this.sampleSize = 0;
				this.sampleRelativeSize = n;
			}
		}
		catch (NumberFormatException e) {
			throw new CLIOException("-size expects a number");
		}
	}
	
	@CLIOption("-iterations")
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	@CLIOption("-sampleFile")
	public void setSampleFile(File sampleFile) {
		this.sampleFile = sampleFile;
	}

	@CLIOption("-reportFile")
	public void setReportFile(File reportFile) {
		this.reportFile = reportFile;
	}
	
	@CLIOption("-complementFile")
	public void setComplementFile(File complementFile) {
		this.complementFile = complementFile;
	}
	
	@CLIOption("-setBias")
	public void setBias(String property, double expect) {
		this.bias.put(property, expect);
	}

	@CLIOption(value="-help", stop=true)
	public void help() {
		System.out.print(usage());
	}
	
	private double[] getBias() {
		double[] result = new double[expectedPropertiesNumber];
		for (int i = 0; i < result.length; ++i) {
			String name = propertiesNames.get(i);
			if (bias.containsKey(name)) {
				result[i] = bias.get(name);
			}
			else {
				result[i] = -1;
			}
		}
		return result;
	}

	private int getSampleSize(int populationSize) {
		if (sampleSize == 0) {
			return Math.round(populationSize * sampleRelativeSize);
		}
		return sampleSize;
	}
	
	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		if (populationFile != null) {
			throw new CLIOException("extra file name at the end of command line");
		}
		populationFile = new File(arg);
		return false;
	}

	@Override
	public String getResourceBundleName() {
		return Sampler.class.getCanonicalName() + "Help";
	}

	public static void main(String[] args) throws CLIOException, IOException {
		Sampler inst = new Sampler();
		if (inst.parse(args)) {
			return;
		}
		List<Individual> population = inst.loadPopulation();
		Sample sample = new Sample(population, inst.getSampleSize(population.size()), inst.getBias());
		sample.sample(new Random(), new Random(), inst.iterations);
		if (inst.sampleFile == null) {
			inst.writeSample(System.out, sample, false);
		}
		else {
			try (PrintStream out = new PrintStream(inst.sampleFile, inst.charset)) {
				inst.writeSample(out, sample, false);
			}
		}
		if (inst.complementFile != null) {
			try (PrintStream out = new PrintStream(inst.complementFile, inst.charset)) {
				inst.writeSample(out, sample, true);
			}
		}
		if (inst.reportFile != null) {
			try (PrintStream out = new PrintStream(inst.reportFile, inst.charset)) {
				inst.writeSampleReport(out, sample);
			}
		}
		inst.writeSampleReport(System.err, sample);
	}
}
