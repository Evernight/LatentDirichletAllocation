#!/usr/bin/python

import cherrypy
from jinja2 import Environment, FileSystemLoader
from DBAdapters import LDAResults, TextCollection

templates_env = Environment(loader=FileSystemLoader('templates'))
lda_results = LDAResults()
text_collection = TextCollection()
		
class WebServer:
	@cherrypy.expose
	def index(self):
		return "test1111"

	@cherrypy.expose
	def document(self, id=None):
		if id == None:
			return self.index()
		
		return str(id)
	
	@cherrypy.expose
	def lda_topic(self, id=None):
		if id == None:
			return self.index()
		id = int(id)
		template = templates_env.get_template("lda_topic.html")

		distribution = [(word[1], lda_results.topic_word_dist[(id, word[0])]) for word in text_collection.word_by_id.iteritems()]
		distribution.sort(lambda x, y: cmp(y[1] , x[1]))
		distribution = distribution[:200]

		return template.render(text="<br />".join(map(str, distribution)))

if __name__=="__main__":
	text_collection.loadFromFiles(dictionary_filename="reuters/dictionary.txt")
	lda_results.loadFromFile("reuters/result/parameters.txt")
	cherrypy.quickstart(WebServer())
