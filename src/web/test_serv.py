#!/usr/bin/python

import cherrypy

class MyTest:
	def index(self):
		return "111"
	def wow(self):
		return "222"
	index.exposed = True
	wow.exposed = True

cherrypy.quickstart(MyTest())
#print "AAA"
