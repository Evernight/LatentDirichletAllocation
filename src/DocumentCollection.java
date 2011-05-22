import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 19.05.11
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentCollection {
	public int getDocumentsCount();
	public Map<Integer, Integer> getDocument(int id);

	int getVocabSize();
}
