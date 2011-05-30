from sqlite3 import connect

class SQLLDAResults:
	def __init__(self, filename):
		self.filename = filename
		conn = connect(filename).cursor()

		conn.execute("SELECT * FROM variables")
		var = dict(conn)
		self.topics_count = var['topics_count']
		self.docs_count = var['docs_count']
		self.vocab_size = var['vocab_size']
		self.categ_count = var['categ_count']

		conn.close()

	def get_categories_list(self):
		conn = connect(self.filename).cursor()
		conn.execute("SELECT category FROM categories")
		result = map(lambda x: x[0], list(conn))
		conn.close()
		return result

	def get_document_categories(self, id):
		conn = connect(self.filename).cursor()
		conn.execute("""
			SELECT category FROM doc_category INNER JOIN categories ON doc_category.category_id=categories.category_id
			WHERE doc_id=?""", [id])
		result = map(lambda x: x[0], list(conn))
		conn.close()
		return result

	def get_topic_word_distribution(self, id):
		conn = connect(self.filename).cursor()
		conn.execute("""
			SELECT word, probability FROM topic_word INNER JOIN words ON topic_word.word_id=words.word_id
			WHERE topic_id=? ORDER BY probability DESC LIMIT 200""", [id])
		result = list(conn)
		result = map(lambda x: (x[0], "%.6f" % x[1]), result)
		conn.close()
		return result
	
	def get_topic_doc_distribution(self, id):
		conn = connect(self.filename).cursor()
		conn.execute("SELECT doc_id, probability FROM topic_doc WHERE topic_id=? ORDER BY probability DESC LIMIT 200", [id])
		result = list(conn)
		result = map(lambda x: (x[0], "%.6f" % x[1]), result)
		conn.close()
		return result
	
	def get_topic_category_distribution(self, id):
		conn = connect(self.filename).cursor()
		conn.execute("""
			SELECT category, probability FROM topic_category INNER JOIN categories ON topic_category.category_id=categories.category_id
			WHERE topic_id=? ORDER BY probability DESC LIMIT 200""", [id])
		result = list(conn)
		result = map(lambda x: (x[0], "%.6f" % x[1]), result)
		conn.close()
		return result
	
	def get_document_topic_distribution(self, id):
		conn = connect(self.filename).cursor()
		conn.execute("SELECT topic_id, probability FROM doc_topic WHERE doc_id=? ORDER BY probability DESC LIMIT 200", [id])
		result = list(conn)
		result = map(lambda x: (x[0], "%.6f" % x[1]), result)
		conn.close()
		return result
