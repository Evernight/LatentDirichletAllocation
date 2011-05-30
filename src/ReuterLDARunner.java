import org.apache.log4j.BasicConfigurator;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 22.05.11
 * Time: 1:54
 * To change this template use File | Settings | File Templates.
 */
public class ReuterLDARunner {
	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.US);
		BasicConfigurator.configure();
		PlainTextDocumentCollection reuters = new PlainTextDocumentCollection(
				"reuters/data_3/index.txt",
				"reuters/data_3/vocabuary.txt",
				"reuters/data_3/categories.txt",
				"reuters/data_3/categories_mapping.txt"
		);
		LatentDirichletAllocation lda = new LatentDirichletAllocation(reuters);

		lda.initialize();
		lda.run();
		lda.generateParameters();

		LDAExtras ldae = new LDAExtras(lda, reuters);
		ldae.calculateAll();

		ResultsStorage storage = new PlainTextResultsStorage("reuters/data_3/result/distributions.txt");
		storage.setSources(lda, ldae);
		storage.write();
	}
}
