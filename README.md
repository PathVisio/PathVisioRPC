PathVisioRPC
============

PathVisioRPC is now published!

Please cite:

Bohler, Anwesha, et al. “Automatically visualise and analyse data on pathways using PathVisioRPC from any programming environment.” BMC Bioinformatics 16 (2015): 267. http://doi.org/10.1186/s12859-015-0708-8


PathVisioRPC is an XML-RPC implementation of PathVisio that allows users to create and edit biological pathways, visualize data on pathways, perform pathway statistics and exports results in various image formats. Since most major programming languages have a library/module providing XML-RPC functionality, this application enables users to access PathVisio functionality from their favourite analytical environment.

Visit the project website for more information, including a lot of documentation on how to interact with the server in various programming languages: http://projects.bigcat.unimaas.nl/pathvisiorpc/

Installation
------------

The build environment uses Ant and the compilation depends on libraries not available in this repository, but available from the PathVisio and HTMLExporter plugin repositories. The three default locations for additional libraries can be set:

    ant -Dcommon.bundles=../common-bundles -Dpathvisio.dir=../pathvisio \\
      -Dorg.pathvisio.htmlexport.dir=../../BigCat/pvplugins/htmlexport/trunk \\
      standalone

The standalone target creates a Java Archive with all the dependencies included in the jar.

Running
-------

The standalone jar fires up a PathVisioRPC server:

    java -jar PathVisioRPC-standalone.jar

Optionally, a port number can be given as parameter.
