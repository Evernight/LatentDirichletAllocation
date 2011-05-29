from nltk.corpus import reuters

class LDAResults:
	def __init__(self):
		self.topic_word_dist = {}
		self.doc_topic_dist = {}
		self.topic_doc_dist = {}
		self.topic_category_dist = {}

	def loadFromFile(self, filename):
		params = open(filename, "r")
		self.topics_count, self.vocab_size, self.docs_count = map(int, params.readline().split())
		for topic in xrange(self.topics_count):
			word_dist = map(float, params.readline().split())
			for word in xrange(self.vocab_size):
				self.topic_word_dist[topic, word] = word_dist[word]
		params.readline()
		for doc in xrange(self.docs_count):
			topic_dist = map(float, params.readline().split())
			for topic in xrange(self.topics_count):
				self.doc_topic_dist[doc, topic] = topic_dist[topic]

	def process(self, collection):
		topic_sum = {}
		for topic in xrange(self.topics_count):
			topic_sum = sum([self.doc_topic_dist[doc, topic] for doc in xrange(self.docs_count)])
			for doc in xrange(self.docs_count):
				self.topic_doc_dist[topic, doc] = self.doc_topic_dist[doc, topic] / topic_sum

		"""for topic in xrange(self.topics_count):
			print topic
			for doc in xrange(self.docs_count):
				for category in reuters.categories(collection.doc_name_by_id):
					if (topic, category) not in self.topic_category_dist:
						self.topic_category_dist[topic, category] = 0
					self.topic_category_dist[topic, category] += self.topic_doc_dist[topic, doc]"""

class TextCollection:
	def __init__(self):
		self.word_by_id = {}
		self.doc_name_by_id = {}
		self.id_by_doc_name = {}
	
	def loadFromFiles(self, dictionary_filename, docs_filename):
		dictionary = open(dictionary_filename, "r")
		docs = open(docs_filename, "r")

		self.vocab_size = int(dictionary.readline())
		words = map(lambda x : tuple(x.split()), dictionary.readlines())
		words = map(lambda x: (int(x[0]), x[1]), words)
		self.word_by_id = dict(words)

		self.docs_count = int(docs.readline())
		lines = docs.readlines()
		self.doc_name_by_id = dict(map(lambda x: (int(x.split()[0]), x.split()[1]), lines))
		self.id_by_doc_name = dict(map(lambda x: (x.split()[1], int(x.split()[0])), lines))

