library ("XMLRPC")
server = "http://localhost:5678"
xml.rpc(server,"PathVisio.test")
xml.rpc(server, "PathVisio.importData", "/home/anwesha/Desktop/data1.txt", "","3", "/home/anwesha/PathVisio-Data/gene-databases","home/anwesha/workspacePathVisio/github/PathVisioRPC/testdata/results")

