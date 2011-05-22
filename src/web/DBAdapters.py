class LDAResults:
	def __init__(self):
		self.topic_word_dist = {}

	def loadFromFile(self, filename):
		params = open(filename, "r")
		self.topics_count, self.vocab_size, self.docs_count = map(int, params.readline().split())
		for topic in xrange(self.topics_count):
			word_dist = map(float, params.readline().split())
			for word in xrange(self.vocab_size):
				self.topic_word_dist[topic, word] = word_dist[word]

class TextCollection:
	def __init__(self):
		self.word_by_id = {}
	
	def loadFromFiles(self, dictionary_filename):
		dictionary = open(dictionary_filename, "r")
		self.vocab_size = int(dictionary.readline())
		words = map(lambda x : tuple(x.split()), dictionary.readlines())
		words = map(lambda x: (int(x[0]), x[1]), words)
		self.word_by_id = dict(words)
