package firebase.firestore;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionReferenceProxy extends KrollProxy{
	private String key; 
	private KrollFunction Callback;
	private final String LCAT = TifirestoreModule.LCAT;
	
	public void handleCreationArgs(KrollModule createdInModule, Object[] args){
		if (args.length==0) {
			Log.e(LCAT,"key nasme for CollectionReference is missing");
			return;
		}
		key = (String)args[1];
		if (args.length==2 && args[1] instanceof KrollFunction) {
			Callback = (KrollFunction)args[1];
		}
	}
	
	@Kroll.method
	public void add(KrollDict doc) {
		FirebaseFirestore.getInstance().collection.add(doc).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(LCAT, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LCAT, "Error adding document", e);
            }
        });;
	}

}
