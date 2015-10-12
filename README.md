# HTTP/HTTPS Upload library

Simple and minimalist Java library which allows to upload one or several files 
to a remote server which accepts file uploads using HTTP POST and encoded using
`multipart/form-data` (RFC2388).

The library is entirely based on JDK and has no external dependencies.


### Features


* Allows uploading multiple files in one operation (if supported by the endpoint).

* Supports HTTP or HTTPS.

* Has no external dependencies, e.g. no Apache HttpClient.

* Certificate validation can be turned off. This is important when working against a site that uses a self-signed certificate.

* Support for endpoints that require authentication (only authentication type "Basic" is currently supported).

* Java's `HttpURLConnection/HttpsURLConnection` will normally do internal buffering of the request being sent. 
If the file being uploaded is large this means that Java will try to buffer all of the file in memory. 
This class avoids this by streaming the file contents to the server and therefore 
infinitely large files to be uploaded without causing an out-of-memory error in the JVM.

* Support for explicitly setting network proxy. This is implemented without 
the use of global system properties, so can be used without affecting other classes 
in the same JVM.

* File upload progress can be tracked for every 1% progress. 
This allows an UI to let the user know how the upload is progressing.




### Javadoc

[javadoc](http://addicticks.github.io/httpsupload/)


### Usage


1. Invoke static method `HttpsFileUploader.upload()` method.
2. Check returned value from method

That's it.

See [javadoc](http://addicticks.github.io/httpsupload/) for more information.


### License

Apache License, version 2.0.