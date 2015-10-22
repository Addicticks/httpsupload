# HTTP/HTTPS Upload library

Simple and minimalist Java library which allows to upload one or several files 
to a remote server which accepts file uploads using HTTP POST and encoded using
`multipart/form-data` (RFC2388).

The library is entirely based on JDK and has no external dependencies.


### Features


* Allows uploading multiple files in one operation (if supported by the endpoint).

* Supports HTTP or HTTPS.

* Very small and has no external dependencies, e.g. no Apache HttpClient.

* Can upload from a physical file (the typical use case) or directly from an `InputStream`.

* Certificate validation can be turned off. This is important when working against a site that uses a self-signed certificate.

* Support for endpoints that require authentication (only authentication type "Basic" is currently supported).

* Java's `HttpURLConnection/HttpsURLConnection` will normally do internal buffering of the request being sent. 
If the file being uploaded is large this means that Java will try to buffer all of the file in memory. 
This class avoids this by streaming the file contents to the server and therefore allows
infinitely large files to be uploaded without causing an out-of-memory error in the JVM.
(this feature does not apply when uploading from a stream rather than physical file)

* Support for explicitly setting network proxy. This is implemented without 
the use of global system properties, so can be used without affecting other classes 
in the same JVM.

* Upload progress can be tracked for every 1% progress. 
This allows an UI to let the user know how the upload is progressing.


### Motivation

We needed something small and without external dependencies.

The alternative to this minimal library is probably the [Apache HttpClient](http://hc.apache.org/httpcomponents-client-ga/index.html).
This is a much bigger library which comes in at around 1 MB including dependencies
whereas our library is only 20 KB in size.

The fact is that a lot of what Apache HttpClient does is no longer required with a fairly recent
Java version. There was a time when Apache HttpClient was a necessity because the
standard JDK was simply too weak in the area of HTTP. This has changed with releases
Java 1.4, Java 5 and Java 6 all of which introduced new features in this area.

This is not to say that Apache HttpClient isn't still superior to the standard
JDK classes. It is!. It's just not enough to justify its use for a relatively simple
use case as file upload is.

If our library doesn't fit your requirements you should definitely look into
Apache HttpClient.


### Javadoc

[javadoc](http://addicticks.github.io/httpsupload/)


### Download

You can either download from [here](https://github.com/Addicticks/httpsupload/releases) or better yet get it from Maven Central:

```xml
<dependency>
    <groupId>com.addicticks.oss</groupId>
    <artifactId>httpsupload</artifactId>
    <version>xxxxx</version>
</dependency>
```
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.addicticks.oss/httpsupload/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.addicticks.oss/httpsupload)


### Usage

1. Invoke static method `HttpsFileUploader.upload()` method.
1. Check the method's return value.

That's it.

Here's a minimal example of the absolutely minimum scenario:

```java
HttpsFileUploaderConfig uploaderConfig = new HttpsFileUploaderConfig(endpointURL);

// Do the upload.
// A single file is uploaded with no progress notification
result = HttpsFileUploader.upload(uploaderConfig, new File("/tmp/testfile.dat"));
 
// Evaluate the result.
if (!result.isError()) {
    System.out.println("OK, upload successful");
} else {
    System.out.println("Error uploading, http code :" + result.getHttpStatusCode());
    System.out.println("Message from server : " + result.getResponseTextNoHtml());
}
```

See [javadoc](http://addicticks.github.io/httpsupload/) for more information.
The `HttpsFileUploader` class has a more elaborate code example in the javadoc.




### Determining what the server endpoint accepts

In order to use the library you'll need to know a little about what the endpoint
you plan to upload to actually accepts in terms of field names.

Most often the server will expose some kind of html form and the easiest way
to figure out what field names are used is to peak at such a file. Suppose
the endpoint's upload html file looks like this:

```html
<html lang="en">
    <head>
        <title>File Upload</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <form method="POST" action="upload" enctype="multipart/form-data" >
            File: <input type="file" name="file" id="fff" /> <br/>
            Destination: <input type="text" name="destination" value="/tmp"/>
            </br>
            <input type="submit" value="Upload" name="upload" id="upload" />
        </form>
    </body>
</html>
```

From this we can see the server accepts a single file to be uploaded into a field
called `file`. In addition it also supports a plain text field called 
`destination`.

If you don't have such information available to you then there are other options:

1. You can guess about those field names. Quite often a field of type `file`
   often has a field name of ...tada ... `file`. If the endpoint accepts multiple
   files then those names are very likely to be `file1`, `file2`, ... `fileN`.
1. Ask the administrator of the endpoint for this information.
1. Upload a file from your browser to the endpoint and use your browser's 
   network inspector to see what is actually being sent.



### Requirements

Java 7 or later.


### License

Apache License, version 2.0.