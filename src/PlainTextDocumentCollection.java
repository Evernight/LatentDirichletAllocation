import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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

	int categoriesCount;
	Map<Integer, List<Integer>> categories;

	public PlainTextDocumentCollection(
			String indexFilename, String dictionaryFilename,
			String categoriesFilename, String categoriesMappingFilename) throws FileNotFoundException {
		this.invertedIndexFilename = indexFilename;
		this.dictionaryFilename = dictionaryFilename;

		log.info("Opening index");
		Scanner index = new Scanner(new File(indexFilename));
		documentsCount = index.nextInt();
		documents = new HashMap<Integer, Map<Integer, Integer>>();
		for (int i = 0; i < documentsCount; ++i) {
			Map<Integer, Integer> doc = new HashMap<Integer, Integer>();
			int docID = index.nextInt();
			int count = index.nextInt();
			for (int j = 0; j < count; ++j) {
				doc.put(index.nextInt(), index.nextInt());
			}
			documents.put(docID, doc);
		}
		index.close();

		log.info("Opening dictionary");
		Scanner dictionary = new Scanner(new File(dictionaryFilename));
		vocabSize = dictionary.nextInt();
		dictionary.close();

		log.info("Opening categories file");
		Scanner categoriesFile = new Scanner(new File(categoriesFilename));

		categories = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < documentsCount; ++i) {
			List<Integer> cur = new ArrayList<Integer>();
			int count = categoriesFile.nextInt();
			for (int j = 0; j < count; ++j) {
				cur.add(categoriesFile.nextInt());
			}
			categories.put(i, cur);
		}
		categoriesFile.close();

		Scanner categoriesMappingFile = new Scanner(new File(categoriesMappingFilename));
		categoriesCount = categoriesMappingFile.nextInt();
		categoriesMappingFile.close();
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

	public int getCategoriesCount() {
		return categoriesCount;
	}

	public List<Integer> getCategories(int id) {
		return categories.get(id);
	}
}