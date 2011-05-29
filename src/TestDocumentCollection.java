import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 21.05.11
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class TestDocumentCollection implements DocumentCollection {
	Map<Integer, Map<Integer, Integer>> col1 = new HashMap<Integer, Map<Integer, Integer>>();

	public TestDocumentCollection() {
		Map<Integer, Integer> doc1 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> doc2 = new HashMap<Integer, Integer>();

		doc1.put(0, 13); doc1.put(2, 20);
		doc2.put(1, 15); doc2.put(3, 23);
		col1.put(0, doc1);
		col1.put(1, doc2);
	}

	public int getDocumentsCount() {
		return col1.size();
	}

	public Map<Integer, Integer> getDocument(int id) {
		return col1.get(id);
	}

	public int getVocabSize() {
		return 4;
	}

	public int getCategoriesCount() {
		return 0;
	}

	public List<Integer> getCategories(int id) {
		return null;
	}
}
