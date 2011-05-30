#!/usr/bin/python
import cherrypy 
from jinja2 import Environment, FileSystemLoader 
from DBAdapters import SQLLDAResults
import sys

import os.path 

current_dir = os.path.dirname(os.path.abspath(__file__))

templates_env = Environment(loader=FileSystemLoader('templates'))
lda_results = None

class WebServer:
	@cherrypy.expose
	def index(self):
		template = templates_env.get_template("index.html")
		params = {
			"index" : True,
			"documents" : range(lda_results.docs_count),
			"topics" : range(lda_results.topics_count),
			"categories" : lda_results.get_categories_list(),
			"title" : "index page"
		}
		return template.render(**params)

	@cherrypy.expose
	def lda_topic(self, id=None):
		if id == None:
			raise cherrypy.HTTPRedirect("/")
		id = int(id)
		template = templates_env.get_template("lda_topic.html")

		word_distribution = lda_results.get_topic_word_distribution(id)
		doc_distribution = lda_results.get_topic_doc_distribution(id)
		categ_distribution = lda_results.get_topic_category_distribution(id)

		params = {
			"distribution" : word_distribution,
			"doc_dist" : doc_distribution,
			"categ_dist" : categ_distribution,
			"title" : "topic " + str(id)
		}

		return template.render(params)

	@cherrypy.expose
	def document(self, id=None):
		if id == None:
			raise cherrypy.HTTPRedirect("/")
		id = int(id)
		template = templates_env.get_template("document.html")

		distribution = lda_results.get_document_topic_distribution(id)

		params = {
			#TODO "text" : reuters.raw(text_collection.doc_name_by_id[id]),
			"text" : "Not supported yet",
			"distribution" : distribution,
			"categories" : lda_results.get_document_categories(id), #TODO
			"title" : "document " + str(id)
		}
		return template.render(params)
	
	@cherrypy.expose
	def category(self, title=None):
		if title == None:
			raise cherrypy.HTTPRedirect("/")
		template = templates_env.get_template("category.html")
		params = {
			"category" : title,
			# TODO "documents" : map(lambda x: text_collection.id_by_doc_name[x], reuters.fileids(categories=[title])),
			"documents" : ["Not supported yet"],
			"title" : "category '%s'" % title
		}
		return template.render(**params)

if __name__=="__main__":
	lda_results = SQLLDAResults('results.db')

	cherrypy.quickstart(WebServer(), config="cherry.conf")

