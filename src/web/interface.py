#!/usr/bin/python

import cherrypy
from jinja2 import Environment, FileSystemLoader
from DBAdapters import LDAResults, TextCollection
from nltk.corpus import reuters

import os.path
current_dir = os.path.dirname(os.path.abspath(__file__))

templates_env = Environment(loader=FileSystemLoader('templates'))
lda_results = LDAResults()
text_collection = TextCollection()

WORDS_LIMIT = 200
		
class WebServer:
	@cherrypy.expose
	def index(self):
		template = templates_env.get_template("index.html")
		params = {
			"index" : True,
			"documents" : range(lda_results.docs_count),
			"topics" : range(lda_results.topics_count),
			"categories" : reuters.categories()
		}
		return template.render(**params)

	@cherrypy.expose
	def lda_topic(self, id=None):
		if id == None:
			raise cherrypy.HTTPRedirect("/")
		id = int(id)
		template = templates_env.get_template("lda_topic.html")

		distribution = [(word[1], lda_results.topic_word_dist[(id, word[0])]) for word in text_collection.word_by_id.iteritems()]
		distribution.sort(lambda x, y: cmp(y[1] , x[1]))
		distribution = distribution[:WORDS_LIMIT]

		params = {
			"distribution" : distribution
		}

		return template.render(params)

	@cherrypy.expose
	def document(self, id=None):
		if id == None:
			raise cherrypy.HTTPRedirect("/")
		id = int(id)
		template = templates_env.get_template("document.html")

		distribution = [(topic, lda_results.doc_topic_dist[id, topic]) for topic in xrange(lda_results.topics_count)]
		distribution.sort(lambda x, y: cmp(y[1] , x[1]))
		distribution = distribution[:200]

		params = {
			"text" : reuters.raw(text_collection.doc_name_by_id[id]),
			"distribution" : distribution
		}
		return template.render(params)
	
	@cherrypy.expose
	def category(self, title=None):
		if title == None:
			raise cherrypy.HTTPRedirect("/")
		template = templates_env.get_template("category.html")
		params = {
			"category" : title,
			"documents" : map(lambda x: text_collection.id_by_doc_name[x], reuters.fileids(categories=[title]))
		}
		return template.render(**params)
		

if __name__=="__main__":
	text_collection.loadFromFiles(dictionary_filename="reuters/dictionary.txt", docs_filename="reuters/docs.txt")
	lda_results.loadFromFile("reuters/result/small.txt")
	cherrypy.quickstart(WebServer(), config="cherry.conf")

