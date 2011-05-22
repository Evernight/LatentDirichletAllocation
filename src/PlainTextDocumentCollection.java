import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 22.05.11
 * Time: 1:45
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextDocumentCollection implements DocumentCollection {
	Logger log = Logger.getLogger(PlainTextDocumentCollection.class);

	String invertedIndexFilename;
	String dictionaryFilename;

	int documentsCount;
	int vocabSize;
	Map<Integer, Map<Integer, Integer>> documents;

	public PlainTextDocumentCollection(String invertedIndexFilename, String dictionaryFilename) throws FileNotFoundException {
		this.invertedIndexFilename = invertedIndexFilename;
		this.dictionaryFilename = dictionaryFilename;

		log.info("Opening inverted index");
		Scanner invertedIndex = new Scanner(new File(invertedIndexFilename));
		documentsCount = invertedIndex.nextInt();
		documents = new HashMap<Integer, Map<Integer, Integer>>();
		for (int i = 0; i < documentsCount; ++i) {
			Map<Integer, Integer> doc = new HashMap<Integer, Integer>();
			int docID = invertedIndex.nextInt();
			int count = invertedIndex.nextInt();
			for (int j = 0; j < count; ++j) {
				doc.put(invertedIndex.nextInt(), invertedIndex.nextInt());
			}
			documents.put(docID, doc);
		}
		invertedIndex.close();

		log.info("Opening dictionary");
		Scanner dictionary = new Scanner(new File(dictionaryFilename));
		vocabSize = dictionary.nextInt();
		dictionary.close();
	}

	public int getDocumentsCount() {
		return documentsCount;
	}

	public Map<Integer, Integer> getDocument(int id) {
		return documents.get(id);
	}

	public int getVocabSize() {
		return vocabSize;
	}
}
