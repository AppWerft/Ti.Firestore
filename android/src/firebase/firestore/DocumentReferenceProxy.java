package firebase.firestore;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class DocumentReferenceProxy extends KrollProxy {

	
	private final String LCAT = TifirestoreModule.LCAT;
	private DocumentReference doc;

	public DocumentReferenceProxy() {
			
	}
	public DocumentReferenceProxy(DocumentReference doc) {
		this.doc = doc;
	}
	public DocumentReference getDocument() {
		return doc;
	}
	@Kroll.method
	public String getId() {
		return doc.getId();
	}

	
}
