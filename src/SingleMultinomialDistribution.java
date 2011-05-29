import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: whatever
 * Date: 21.05.11
 * Time: 0:21
 * To change this template use File | Settings | File Templates.
 */
public class SingleMultinomialDistribution {
	double[] s;
	Random random = new Random();

	public SingleMultinomialDistribution(double[] p) {
		s = new double[p.length];
		s[0] = p[0];
		for (int i = 1; i < p.length; ++i)
			s[i] = s[i-1] + p[i];
	}

	public int sample() {
		double r = random.nextDouble();
		int i = 0;
		while (true) {
			if (s[i] >= r)
				return i;
			++i;
			if (i == s.length)
				return i - 1;
		}
	}
}
