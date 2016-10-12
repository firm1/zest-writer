# /usr/bin/python3
# -*- coding: utf-8 -*-

import argparse
import http.server
import socketserver
import os
from os import path as op
import sys
from subprocess import Popen, PIPE
import re
import signal
import time
import webbrowser

def stop(port):
    p = Popen(['netstat', '-tulpn'], stdout=PIPE, stderr=PIPE)
    stdout = p.stdout.read().decode('utf-8')
    try:
        pid = int([re.sub(' +', ' ', line).split(' ') for line in stdout.split('\n') if ':%d' % port in line][0][6].split('/')[0])
        print('Port %d currently used by PID %s, killing it...' % (port, pid))
        os.kill(pid, signal.SIGKILL)
        time.sleep(1)
    except:
        pass

def start(path, port):
    web_dir = os.path.join(os.path.dirname(__file__), path)
    os.chdir(web_dir)

    while True:
        try:
            Handler = http.server.SimpleHTTPRequestHandler
            httpd = socketserver.TCPServer(('', port), Handler)
            print('Serving directory %s at address 127.0.0.1:%d.' % (op.abspath(path), port))
            print('Ctrl-C to stop, or `serve.py -s -p %d` from an other process.' % port)
            webbrowser.open('http://127.0.0.1:%d' % port, new=2)
            httpd.serve_forever()
        except OSError as e:
            if e.args[0] != 98:
                raise
            print('Port %d already in use. Moving to port %d.' % (port, port+1))
            port += 1
        except KeyboardInterrupt:
            print('\nInterruped by the user. Bye!')
            break

    httpd.server_close()

parser = argparse.ArgumentParser(description='Serve a folder.')
parser.add_argument('-d', dest='path', default='.', help='The directory to serve (default = ./).')
parser.add_argument('-p', dest='port', default=8000, type=int, help='The port to use (default = 8000).')
parser.add_argument('-s', dest='stop', default=None, action='store_true', help='To stop the service currently using the port.')
args = vars(parser.parse_args())

stop(args['port'])
if not args['stop']:
    start(args['path'], args['port'])
