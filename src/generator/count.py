#!/usr/bin/python

from nltk.corpus import reuters

wordID = 0
docID = 0

def run():
	global wordID, docID
	words_count = {}

	docIDbyName = {}
	wordIDbyName = {}
	for i, doc in enumerate(reuters.fileids()):
		print "Processing %d..." % i
		docIDbyName[doc] = docID
		docID += 1
		cur = docIDbyName[doc]
		words_count[cur] = {}	
		for word in reuters.words(doc):
			norm = word.lower()
			if norm not in wordIDbyName:
				wordIDbyName[norm] = wordID
				wordID += 1
			if norm not in words_count[cur]:
				words_count[cur][norm] = 0
			words_count[cur][norm] += 1
	
	inv_index = open("inverted_index.txt", "w")
	for doc, bag in words_count.iteritems():
		inv_index.write(str(doc) + " ")
		inv_index.write(str(len(bag.keys())) + "\n")
		for word, count in bag.iteritems():
			inv_index.write(str(wordIDbyName[word]) + " " + str(count) + "\n")
		inv_index.write("\n")
	inv_index.close()

	vocabuary = open("vocabuary.txt", "w")
	vocabuary.write(str(len(wordIDbyName.keys())) + "\n")
	words = wordIDbyName.items()
	words.sort(lambda x, y : cmp(x[1], y[1]))
	for word, wid in words:
		vocabuary.write("%d %s\n" % (wid, word))
	vocabuary.close()

	documents = open("docs.txt", "w")	
	documents.write(str(len(docIDbyName.keys())) + "\n")
	docs = docIDbyName.items()
	docs.sort(lambda x, y : cmp(x[1], y[1]))
	for word, wid in docs:
		documents.write("%d %s\n" % (wid, word))
	documents.close()

run()
