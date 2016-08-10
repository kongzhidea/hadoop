#!/usr/bin/python
# -*- coding:utf-8 -*-

import sys

# maps words to their counts
total = {}
count = {}
# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    name, score = line.split('\t')
    total[name] = total.get(name,0)+ int(score)
    count[name] = count.get(name,0)+ 1

for name, score in total.items():
    print '%s\t%.2f'% (name, 1.0 * score/count[name])
 
