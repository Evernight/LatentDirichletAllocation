import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 29.05.11
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextResultsStorage implements ResultsStorage {
	LatentDirichletAllocation lda;
	LDAExtras ldae;

	String filename;

	public PlainTextResultsStorage(String filename) {
		this.filename = filename;
	}

	public void setSources(LatentDirichletAllocation lda, LDAExtras ldae) {
		this.lda = lda;
		this.ldae = ldae;
	}

	public void write() throws FileNotFoundException {
		double[][] topicTermDistribution = lda.getTopicTermDistribution();
		double[][] documentTopicDistribution = lda.getDocumentTopicDistribution();

		PrintWriter out = new PrintWriter(new File(filename));

		out.write(lda.topicsCount + " " + lda.vocabSize + " " + lda.docsCount + " " + ldae.categoriesCount + "\n");
		for (int k = 0; k < lda.topicsCount; ++k) {
			for (int j = 0; j < lda.vocabSize; ++j) {
				out.write(String.format("%f ", topicTermDistribution[k][j]));
			}
			out.write("\n");
		}
		out.write("\n");

		for (int i = 0; i < lda.docsCount; ++i) {
			for (int k = 0; k < lda.topicsCount; ++k) {
				out.write(String.format("%f ", documentTopicDistribution[i][k]));
			}
			out.write("\n");
		}
	    out.write("\n");

		out.close();
	}
}
