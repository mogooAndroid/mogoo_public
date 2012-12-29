#!/usr/bin/python2.6
#
# Simple http server to emulate api.playfoursquare.com

import logging
import shutil
import sys
import urlparse

import SimpleHTTPServer
import BaseHTTPServer


class RequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
  """Handle playfoursquare.com requests, for testing."""

  def do_GET(self):
    logging.warn('do_GET: %s, %s', self.command, self.path)

    url = urlparse.urlparse(self.path)
    logging.warn('do_GET: %s', url)
    query = urlparse.parse_qs(url.query)
    query_keys = [pair[0] for pair in query]

    response = self.handle_url(url)
    if response != None:
      self.send_200()
      shutil.copyfileobj(response, self.wfile)
    self.wfile.close()

  do_POST = do_GET

  def handle_url(self, url):
    path = None

    if '/Store/recommend.do' in url.path:
      path = '../captures/api/Store/recommend.xml'

    if path is None:
      self.send_error(404)
    else:
      logging.warn('Using: %s' % path)
      return open(path)

  def send_200(self):
    self.send_response(200)
    self.send_header('Content-type', 'text/xml')
    self.end_headers()


def main():
  if len(sys.argv) > 1:
    port = int(sys.argv[1])
  else:
    port = 8080
  server_address = ('0.0.0.0', port)
  httpd = BaseHTTPServer.HTTPServer(server_address, RequestHandler)

  sa = httpd.socket.getsockname()
  print "Serving HTTP on", sa[0], "port", sa[1], "..."
  httpd.serve_forever()


if __name__ == '__main__':
  main()
