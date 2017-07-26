XML Cloaker
===========

A Java tool for escaping DTD, entities, XIncludes and other XML content which
need to be preserved, in certain scenarios, during XSLT transformation.

When XML document is parsed, several irreversible changes are applied, namely
* all referenced entities are replaced with their values
* all referenced content (XML includes) is merged
* all default DTD attribute values are injected

If the purpose of particular XSLT transformation is to modify the source XML,
e.g. alter the structure, update attribute values or insert comments, it is
not desired to touch the rest of the content.

In these scenarios it is handy to cloak the XML first, perform the transformation
and finally uncloak the content back.

How to build
============
* Clone this repository to your local disc.
* Ensure that JDK 8 is available on your system.
* Open this Maven based project in your favorite IDE.
* Build the project.

The final jar file is located in the `target` subfolder.

How to use
==========
In your Java project
--------------------
Just add a new dependency and then use available `Cloaker` class methods
```xml
<dependency>
    <groupId>org.doctribute.xml</groupId>
    <artifactId>xml-cloaker</artifactId>
    <version>1.0.0</version>
</dependency>
```

In console
----------
For usage just run the tool in console without any parameters:
`java -jar xml-cloaker-{version}.jar`

Aknowledgment
=============
* Adapted from the [work](https://sourceforge.net/p/docbook/code/HEAD/tree/trunk/contrib/tools/cloak/cloak) of Michael Smith