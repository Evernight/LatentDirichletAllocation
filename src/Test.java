import org.apache.log4j.BasicConfigurator;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 21.05.11
 * Time: 1:02
 * To change this template use File | Settings | File Templates.
 */
public class Test {

	@org.junit.Test
	public static void Test1() {
		double[] p = {0.5, 0.25, 0.25};
		SingleMultinomialDistribution d = new SingleMultinomialDistribution(p);

		int[] r = new int[3];
		for (int i = 0; i < 100000; ++i) {
			++r[d.sample()];
		}

		double sum = r[0] + r[1] + r[2];
		System.out.println(r[0]/sum + " " + r[1]/sum + " " + r[2]/sum);
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		TestDocumentCollection collection = new TestDocumentCollection();
		LatentDirichletAllocation lda = new LatentDirichletAllocation(collection);
		lda.run();
	}
}
