import org.apache.log4j.Logger;

import javax.lang.model.type.PrimitiveType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main LDA class
 *
 * Simplifications:
 * 1) alpha and beta are single numbers, not parameter vectors
 * 2) only last sample counts
 *
 */
public class LatentDirichletAllocation {
	private static Logger log = Logger.getLogger(LatentDirichletAllocation.class);

	// Parameters
	public int topicsCount = 10;
	public double alpha = (double) 50/topicsCount;
	public double beta = 0.01;

	public int iterationsCount = 1;
	private int backupDelay = 40;

	//Private structures
	private DocumentCollection collection;

	private List<Integer> wordIDs[];
	private List<Integer> wordAssignment[];

	private int[][] documentTopicCount;
	private int[] documentTopicSum;
	private int[][] topicTermCount;
	private int[] topicTermSum;

	public int vocabSize;
	public int docsCount;

	// Resulting distributions
	private double topicTermDistribution[][];
	private double documentTopicDistribution[][];

	public double[][] getTopicTermDistribution() {
		return topicTermDistribution;
	}

	public double[][] getDocumentTopicDistribution() {
		return documentTopicDistribution;
	}

	public LatentDirichletAllocation(DocumentCollection collection) {
		this.collection = collection;
	}

	private void assignWord(int docID, int wordID, int topicID, int coeff) {
		documentTopicCount[docID][topicID] += coeff;
		documentTopicSum[docID] += coeff;
		topicTermCount[topicID][wordID] += coeff;
		topicTermSum[topicID] += coeff;
	}

	private double[] conditionalDistribution(int wordID, int docID) {
		double[] p = new double[topicsCount];
		double sum = 0;
		for (int k = 0; k < topicsCount; ++k) {
			double left =
					(topicTermCount[k][wordID] + beta) / (topicTermSum[k] + beta * vocabSize);
			double right =
					(documentTopicCount[docID][k] + alpha) / (documentTopicSum[docID] + alpha * topicsCount);
			p[k] = left * right;
			sum += p[k];
		}
		// Normalization to get probabilities
		for (int k = 0; k < topicsCount; ++k)
			p[k] /= sum;
		return p;
	}

	public void nextSample() {
		for (int i = 0; i < docsCount; ++i) {
			for (int j = 0; j < wordIDs[i].size(); ++j) {
				assignWord(i, wordIDs[i].get(j), wordAssignment[i].get(j), -1);

				double[] p = conditionalDistribution(wordIDs[i].get(j), i);
				int assignedTopic = (new SingleMultinomialDistribution(p)).sample();

				wordAssignment[i].set(j, assignedTopic);
				assignWord(i, wordIDs[i].get(j), wordAssignment[i].get(j), 1);
			}
		}
	}

	public void initialize() {
		log.info("LDA initialization");
		// Retrieve parameters from collection
		docsCount = collection.getDocumentsCount();
		vocabSize = collection.getVocabSize();

		// Create containers
		documentTopicCount = new int[docsCount][topicsCount];
		documentTopicSum = new int[docsCount];
		topicTermCount = new int[topicsCount][vocabSize];
		topicTermSum = new int[topicsCount];
		wordIDs = new ArrayList[docsCount];
		wordAssignment = new ArrayList[docsCount];

		// Initialization probability
		double[] univariateProbability = new double[topicsCount];
		for (int i = 0; i < topicsCount; ++i)
			univariateProbability[i] = 1.0 / topicsCount;
		SingleMultinomialDistribution univariate = new SingleMultinomialDistribution(univariateProbability);

		// Initialize containers
		for (int i = 0; i < docsCount; ++i) {
			Map<Integer, Integer> doc = collection.getDocument(i);

			wordIDs[i] = new ArrayList<Integer>();
			wordAssignment[i] = new ArrayList<Integer>();

			for (Map.Entry<Integer, Integer> word : doc.entrySet()) {
				for (int j = 0; j < word.getValue(); ++j) {
					wordIDs[i].add(word.getKey());

					// First assign to sample from univariate distribution
					int assignedTopic = univariate.sample();

					wordAssignment[i].add(assignedTopic);
					assignWord(i, wordIDs[i].get(wordIDs[i].size() - 1), assignedTopic, 1);
				}
			}
		}
	}

	public void run() throws FileNotFoundException {
		// Sampling
		for (int iteration = 0; iteration < iterationsCount; ++iteration) {
			log.info(String.format("Running sample #%d", iteration + 1));
			nextSample();
			if (iteration != 0 && iteration % backupDelay == 0)
				saveCurrentSampleToFile("reuters/samples/sample" + String.valueOf(iteration) + ".txt");
		}
	}

	public void generateParameters() {
		log.info("Generating Phi and Theta");
		// generate Phi
		topicTermDistribution = new double[topicsCount][vocabSize];
		for (int k = 0; k < topicsCount; ++k) {
			for (int j = 0; j < vocabSize; ++j) {
				topicTermDistribution[k][j] =
						(double) (topicTermCount[k][j] + beta) / (topicTermSum[k] + beta * vocabSize);
			}
		}

		// generate Theta
		documentTopicDistribution = new double[docsCount][topicsCount];
		for (int i = 0; i < docsCount; ++i) {
			for (int k = 0; k < topicsCount; ++k) {
				documentTopicDistribution[i][k] =
						(double) (documentTopicCount[i][k] + alpha) / (documentTopicSum[i] + alpha * topicsCount);
			}
		}
	}

	public void saveCurrentSampleToFile(String filename) throws FileNotFoundException {
		log.info("Saving current sample to " + filename);
		PrintWriter out = new PrintWriter(new File(filename));

		out.write(topicsCount + " " + vocabSize + " " + docsCount + "\n");
		for (int i = 0; i < docsCount; ++i) {
			out.write(wordIDs[i].size() + "\n");
			for (int j = 0; j < wordIDs[i].size(); ++j)
				out.write(wordIDs[i].get(j) + " " + wordAssignment[i].get(j) + " ");
			out.write("\n");
		}

		out.close();
	}
}
