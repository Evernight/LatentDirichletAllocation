import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 29.05.11
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class LDAExtras {
	private Logger log = Logger.getLogger(LDAExtras.class);

	private LatentDirichletAllocation lda;
	private DocumentCollection collection;

	public int documentsCount;
	public int topicsCount;
	public int categoriesCount;

	private double[][] topicDocumentDistribution;
	private double[][] topicCategoryDistribution;

	public LDAExtras(LatentDirichletAllocation lda, DocumentCollection collection) {
		this.lda = lda;
		this.collection = collection;

		this.documentsCount = collection.getDocumentsCount();
		this.topicsCount = lda.topicsCount;
		this.categoriesCount = collection.getCategoriesCount();
	}

	public double[][] getTopicCategoryDistribution() {
		return topicCategoryDistribution;
	}

	public double[][] getTopicDocumentDistribution() {
		return topicDocumentDistribution;
	}

	public void calculateTopicDocumentDistribution() {
		double[][] documentTopicDistribution = lda.getDocumentTopicDistribution();
		this.topicDocumentDistribution = new double[this.topicsCount][this.documentsCount];

		log.info("Calculating topic-document distribution");
		for (int k = 0; k < this.topicsCount; ++k) {
			double sum = 0;
			for (int i = 0; i < this.documentsCount; ++i) {
				sum += documentTopicDistribution[i][k];
			}
			for (int i = 0; i < this.documentsCount; ++i) {
				this.topicDocumentDistribution[k][i] = documentTopicDistribution[i][k] / sum;
			}
		}
	}

	public void calculateTopicCategoryDistribution() {
		this.topicCategoryDistribution = new double[this.topicsCount][this.categoriesCount];

		log.info("Calculating topic-category distribution");
		for (int k = 0; k < this.topicsCount; ++k) {
			for (int i = 0; i < this.documentsCount; ++i) {
				List<Integer> categories = collection.getCategories(i);
				for (int j = 0; j < categories.size(); ++j) {
					topicCategoryDistribution[k][categories.get(j)] += topicDocumentDistribution[k][i];
				}
			}
		}
	}

	public void calculateAll() {
		calculateTopicDocumentDistribution();
		calculateTopicCategoryDistribution();
	}
}
