# -*- coding:gbk -*-
import os
import os.path
from hashlib import md5
import fnmatch

junk = ['.svn', '*.pyc', '*.pyo', '.hg', "filelist.txt", "res.npk", "commres.npk"]

def log(x):
	print x

def ignorable(p):
	for pat in junk:
		if p.startswith(pat) or fnmatch.fnmatch(p, pat):
			return True
	return False

def md5_file(fname):
	m = md5()
	f = open(fname, 'rb')
	while 1:
		data = f.read(1024)
		if not data:
			break
		m.update(data)
	f.close()
	return m.hexdigest()

def visit(arg, dirname, names):
	for name in names:
		subname = os.path.join(dirname, name)
		subname = os.path.normpath(subname)
		if os.path.isfile(subname) and not ignorable(name):
			relpath = os.path.relpath(subname, arg[1]).replace('\\','/')
			arg[0].write("%s\t%s\n" % (relpath, md5_file(subname)))

def gen(root, fname):
	log("Generate "+ fname + " under directory "+root)
	fpath = os.path.join(root, fname)
	filelist = open(fpath, "w")
	os.path.walk(root, visit, [filelist, root])
	filelist.close()

if __name__ == "__main__":
	gen("assets", "filelist.txt")
