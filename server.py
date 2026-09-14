import http.server
import socketserver
import os
import sys
import webbrowser

PORT = 8000
DIRECTORY = os.path.dirname(os.path.abspath(__file__))
WEB_PREVIEW_DIR = os.path.join(DIRECTORY, "web_preview")

class WasteWiseHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        # Determine directory based on whether we serve from root or web_preview
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def do_GET(self):
        # If user visits root '/', redirect or serve web_preview/index.html
        if self.path in ('/', ''):
            self.send_response(302)
            self.send_header('Location', '/web_preview/index.html')
            self.end_headers()
            return
        elif self.path == '/index.html':
            self.send_response(302)
            self.send_header('Location', '/web_preview/index.html')
            self.end_headers()
            return
        return super().do_GET()

def start_server():
    global PORT
    for port_candidate in [8000, 8080, 5000, 3000, 8888, 8008]:
        try:
            with socketserver.TCPServer(("", port_candidate), WasteWiseHandler) as httpd:
                PORT = port_candidate
                url = f"http://localhost:{PORT}/web_preview/index.html"
                print(f"======================================================")
                print(f"  WasteWise AI Local Server is running!")
                print(f"  Local Host URL: {url}")
                print(f"  Root URL:       http://localhost:{PORT}/")
                print(f"======================================================")
                sys.stdout.flush()
                try:
                    httpd.serve_forever()
                except KeyboardInterrupt:
                    print("\nServer stopped.")
                return
        except OSError:
            print(f"Port {port_candidate} in use, trying next...")
            continue

if __name__ == "__main__":
    start_server()
