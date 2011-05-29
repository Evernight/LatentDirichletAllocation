import org.apache.log4j.BasicConfigurator;

import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 22.05.11
 * Time: 1:54
 * To change this template use File | Settings | File Templates.
 */
public class ReuterLDARunner {
	public static void main(String[] args) throws FileNotFoundException {
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
		//lda.storeParametersToFile("reuters/result/temp.txt");

		LDAExtras ldae = new LDAExtras(lda, reuters);
		ldae.calculateAll();
	}
}
