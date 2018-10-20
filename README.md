Ti.Firestore
===========================================
![](https://raw.githubusercontent.com/hansemannn/titanium-firebase/master/titanium-firebase-logo%402x.png)


This module is part of [Firebase in Appcelerator Titanium](https://github.com/hansemannn/titanium-firebase) maintained by [hansemann](https://github.com/hansemannn)

Lets beginn:

## Preparation
Go to Firebase console adn add a project and your app. Then you have to add the firestore part. In the end you have tp move the downloaded json file to Resources folder of your app.

## Costs

It is tollfree upto 20000/day and 1GB data and 10GB traffic.

## Usage 

### Initialization

```javascript
// import of configuration and start:
require('firebase.core').configure;

const Firestore = require('firebase.firestore');
Firestore.connect();
Firestore.setLoggingEnabled(true);
```

### Add data

Cloud Firestore stores data in Documents, which are stored in Collections. Cloud Firestore creates collections and documents implicitly the first time you add data to the document. You do not need to explicitly create collections or documents.

Create a new collection and a document using the following example code.

```
const users = Firestore.createCollectionReference('users');

function onComplete(e) {
	console.log(e);
}
// Create a new user with a first, middle, and last name
users.add({
	"first" : "Ada",
	"last" : "Miller",
	"born" :  1962
 },	onComplete);
```
Alternatively you can ommit the second paramter and can usea second paramter as callback listener.

```
const users = Firestore.createCollectionReference('users',onCompleted);
```

Now add another document to the users collection. Notice that this document includes a key-value pair (middle name) that does not appear in the first document. Documents in a collection can contain different sets of information.

```
users.add({
    data : {
    	"first" : "Alan",
    	"middle" :  "Mathison",
    	"last" : "Turing",
    	"born" :  1912
    }}, onComplete);
```
### Set data

When you use set() to create a document, you must specify an ID for the document to create. For example:

```
users.set({
    id : "alanmathison",
    data : {
	    "first" : "Alan",
  		 "middle" :  "Mathison",
  	    "last" : "Turing",
       "born" :  1912
  	}},	onComplete);
```
With `add data` a new document will created, With `set data` youcan update an existing document or you can add and you can choose an own ID.


### Read data

```
users.get(null,onComplete);

user.get({
	where : {
		born : "≥1820",
		first : "=Alan"
	}	
},onComplete);
```

### Listen data (realtime)

```
user.listen({
	where : {
		born : "≥1820",
		first : "=Alan"
	},
	limit : 1,
	orderBy : born
},onComplete);
```



### Secure your data

If you're using the Web, Android, or iOS SDK, use [Firebase Authentication](https://firebase.google.com/docs/auth/) and [Cloud Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started) to secure your data in Cloud Firestore.

Here are some basic rule sets you can use to get started. You can modify your security rules in the Rules tab of the console.

### Next steps
