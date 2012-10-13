Remote Storage Provider running on Google App Engine

### Usage

If your gmail address is for example **username@gmail.com**
then you can now use **username@remote-storage.appspot.com** as your remote storage address.

Have a look at https://remote-storage.appspot.com !


### Development

I recommend to use Eclipse 3.7 (Indigo) together with these plugins:
 * https://developers.google.com/appengine/docs/java/tools/eclipse
 * http://scala-ide.org/download/current.html

The appengine plugin will complain about missing jars in the libs directory.
This is intentional, as I did not wantend to commit this big heap of files.
You can use the appengine plugin (quickfix) to add this jars.
