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
```

### Add data

Cloud Firestore stores data in Documents, which are stored in Collections. Cloud Firestore creates collections and documents implicitly the first time you add data to the document. You do not need to explicitly create collections or documents.

Create a new collection and a document using the following example code.

```
const user = Firestore.createCollection('users');

// Create a new user with a first, middle, and last name
users.add({
	"first" : "Ada",
	"last" : "Miller",
	"born" :  1962
	},function(e) {
	console.log(e);
});
```

Now add another document to the users collection. Notice that this document includes a key-value pair (middle name) that does not appear in the first document. Documents in a collection can contain different sets of information.

```
users.add({
    "first" : "Alan",
    "middle" :  "Mathison",
    "last" : "Turing",
    "born" :  1912
    },function(e) {
    console.log(e);
});
```

### Read data

```
Firestore.get('users',onComplete);
function onComplete(e) {
	console.log(e);
}

```