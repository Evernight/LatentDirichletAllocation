#!/usr/bin/python

from nltk.corpus import reuters
from sqlite3 import connect
import sys
import codecs

class LDAResults:
	def __init__(self):
		self.topic_word_dist = {}
		self.doc_topic_dist = {}
		self.topic_doc_dist = {}
		self.topic_category_dist = {}

	def loadFromFile(self, filename):
		params = open(filename, "r")
		self.topics_count, self.vocab_size, self.docs_count, self.categ_count = map(int, params.readline().split())
		for topic in xrange(self.topics_count):
			word_dist = map(float, params.readline().split())
			for word in xrange(self.vocab_size):
				self.topic_word_dist[topic, word] = word_dist[word]
		params.readline()

		for doc in xrange(self.docs_count):
			topic_dist = map(float, params.readline().split())
			for topic in xrange(self.topics_count):
				self.doc_topic_dist[doc, topic] = topic_dist[topic]
		params.readline()

		for topic in xrange(self.topics_count):
			doc_dist = map(float, params.readline().split())
			for doc in xrange(self.docs_count):
				self.topic_doc_dist[topic, doc] = doc_dist[doc]
		params.readline()

		for topic in xrange(self.topics_count):
			categ_dist = map(float, params.readline().split())
			for categ in xrange(self.categ_count):
				self.topic_category_dist[topic, categ] = categ_dist[categ]
		params.readline()

class TextCollection:
	def __init__(self):
		self.word_by_id = {}
		self.doc_name_by_id = {}
		self.id_by_doc_name = {}
		self.categ_name_by_id = {}
	
	def loadFromFiles(self, dictionary_filename, docs_filename, categ_map_filename):
		dictionary = open(dictionary_filename, "r")
		docs = open(docs_filename, "r")
		categ_map = open(categ_map_filename, "r")

		self.vocab_size = int(dictionary.readline())
		words = map(lambda x : tuple(x.split()), dictionary.readlines())
		words = map(lambda x: (int(x[0]), x[1]), words)
		self.word_by_id = dict(words)		

		self.docs_count = int(docs.readline())
		lines = docs.readlines()
		self.doc_name_by_id = dict(map(lambda x: (int(x.split()[0]), x.split()[1]), lines))
		self.id_by_doc_name = dict(map(lambda x: (x.split()[1], int(x.split()[0])), lines))

		self.categ_count = int(categ_map.readline())
		lines = categ_map.readlines()
		self.categ_name_by_id = dict(map(lambda x: (int(x.split()[0]), x.split()[1]), lines))
	
	def get_document_categories(self, id):
		return []

def main():
	textfile, input_dir, dbfile = sys.argv[1:]
	lda_results = LDAResults()
	lda_results.loadFromFile(textfile)

	text_collection = TextCollection()
	text_collection.loadFromFiles(
		dictionary_filename="reuters/" + input_dir + "/vocabuary.txt", 
		docs_filename="reuters/" + input_dir + "/docs.txt",
		categ_map_filename="reuters/" + input_dir + "/categories_mapping.txt"
	)

	conn = connect(dbfile)
	conn.execute('CREATE TABLE variables (name text, value int)')
	for v in \
		[("topics_count", lda_results.topics_count),
		 ("docs_count", lda_results.docs_count),
		 ("vocab_size", lda_results.vocab_size),
		 ("categ_count", lda_results.categ_count)]:
		conn.execute('INSERT INTO variables values(?, ?)', v)

	conn.execute('CREATE TABLE topic_word (topic_id int, word_id int, probability real)')
	for topic_id in xrange(lda_results.topics_count):
		for word_id in xrange(lda_results.vocab_size):
			conn.execute('INSERT INTO topic_word values(?, ?, ?)', (topic_id, word_id, lda_results.topic_word_dist[topic_id, word_id]))
	conn.commit()

	conn.execute('CREATE TABLE doc_topic (doc_id int, topic_id int, probability real)')
	for doc_id in xrange(lda_results.docs_count):
		for topic_id in xrange(lda_results.topics_count):
			conn.execute('INSERT INTO doc_topic values(?, ?, ?)', (doc_id, topic_id, lda_results.doc_topic_dist[doc_id, topic_id]))
	conn.commit()

	conn.execute('CREATE TABLE topic_doc (topic_id int, doc_id int, probability real)')
	for topic_id in xrange(lda_results.topics_count):
		for doc_id in xrange(lda_results.docs_count):
			conn.execute('INSERT INTO topic_doc values(?, ?, ?)', (topic_id, doc_id, lda_results.topic_doc_dist[topic_id, doc_id]))
	conn.commit()

	conn.execute('CREATE TABLE topic_category (topic_id int, category_id int, probability real)')
	for topic_id in xrange(lda_results.topics_count):
		for category_id in xrange(lda_results.categ_count):
			conn.execute('INSERT INTO topic_category values(?, ?, ?)', (topic_id, category_id, lda_results.topic_category_dist[topic_id, category_id]))
	conn.commit()

	conn.execute('CREATE TABLE words (word_id int, word text)')
	for id, word in text_collection.word_by_id.iteritems():
		conn.execute('INSERT INTO words values(?, ?)', (id, word))
	conn.commit()

	conn.execute('CREATE TABLE categories (category_id int, category text)')
	for id, categ in text_collection.categ_name_by_id.iteritems():
		conn.execute('INSERT INTO categories values(?, ?)', (id, categ))
	conn.commit()

	conn.execute('CREATE TABLE doc_category (doc_id int, category_id int)')
	conn.commit()

if __name__ == "__main__":
	main()
