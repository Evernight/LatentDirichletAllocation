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
		BasicConfigurator.configure();
		PlainTextDocumentCollection reuters =
				new PlainTextDocumentCollection("reuters/inverted_index.txt", "reuters/dictionary.txt");
		LatentDirichletAllocation lda = new LatentDirichletAllocation(reuters);
		lda.initialize();
		lda.run();
		lda.generateParameters();
		Locale.setDefault(Locale.US);
		lda.storeParametersToFile("reuters/result/parameters.txt");
	}
}
