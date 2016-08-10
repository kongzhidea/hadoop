#!/usr/bin/python
# -*- coding:utf-8 -*-

import sys

# input comes from STDIN (standard input)
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()
    # split the line into words
    words = line.split()
    print '%s\t%s' % (words[0], words[1])
