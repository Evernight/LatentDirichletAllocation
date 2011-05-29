import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 29.05.11
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextResultsStorage implements ResultsStorage {
	private Logger log = Logger.getLogger(PlainTextResultsStorage.class);

	private LatentDirichletAllocation lda;
	private LDAExtras ldae;

	private String filename;

	public PlainTextResultsStorage(String filename) {
		this.filename = filename;
	}

	public void setSources(LatentDirichletAllocation lda, LDAExtras ldae) {
		this.lda = lda;
		this.ldae = ldae;
	}

	public void write() throws Exception {
		double[][] topicTermDistribution = lda.getTopicTermDistribution();
		double[][] documentTopicDistribution = lda.getDocumentTopicDistribution();
		double[][] topicCategoryDistribution = ldae.getTopicCategoryDistribution();
		double[][] topicDocumentDistribution = ldae.getTopicDocumentDistribution();

		PrintWriter out;
		try {
			out = new PrintWriter(new File(filename));
		} catch (Exception e) {
			throw new Exception("Your storage sucks, man");
		}

		out.write(lda.topicsCount + " " + lda.vocabSize + " " + lda.docsCount + " " + ldae.categoriesCount + "\n");
		log.info("Storing topic-term distribution");
		for (int k = 0; k < lda.topicsCount; ++k) {
			for (int j = 0; j < lda.vocabSize; ++j) {
				out.write(String.format("%f ", topicTermDistribution[k][j]));
			}
			out.write("\n");
		}
		out.write("\n");

		log.info("Storing document-topic distribution");
		for (int i = 0; i < lda.docsCount; ++i) {
			for (int k = 0; k < lda.topicsCount; ++k) {
				out.write(String.format("%f ", documentTopicDistribution[i][k]));
			}
			out.write("\n");
		}
		out.write("\n");

		log.info("Storing topic-document distribution");
		for (int k = 0; k < lda.topicsCount; ++k) {
			for (int i = 0; i < lda.docsCount; ++i) {
				out.write(String.format("%f ", topicDocumentDistribution[k][i]));
			}
			out.write("\n");
		}
		out.write("\n");

		log.info("Storing topic-category distribution");
		for (int k = 0; k < lda.topicsCount; ++k) {
			for (int c = 0; c < ldae.categoriesCount; ++c) {
				out.write(String.format("%f ", topicCategoryDistribution[k][c]));
			}
			out.write("\n");
		}

		out.close();
	}
}
