xml-cloaker
===========

A Java class for escaping DTD, entities and other content which need to be 
preserved, in certain scenarios, during XSLT transformation.

If DTD is present, some irreversible changes are made to the XML during XSLT 
trasformation, namely:

* all referenced entities are resolved
* all default parameters are injected

If your XSLT does just non-invasive task like removing certain attribute, you 
usually don't want to replace all entities with corresponding characters or mess
your XML with default attributes.

In this particular case it is handy to cloak the XML first, perform the 
transformation and finally uncloak the content back.