#!/usr/bin/python
# -*- coding:utf-8 -*-

import sys

# maps words to their counts
dict = {}

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    word, count = line.split('\t')
    dict[word] = dict.get(word,0)+1

for word, count in dict.items():
    print '%s\t%s'% (word, count)
 
