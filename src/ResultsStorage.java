import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 29.05.11
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public interface ResultsStorage {
	public void setSources(LatentDirichletAllocation lda, LDAExtras ldae);
	public void write() throws Exception;
}
