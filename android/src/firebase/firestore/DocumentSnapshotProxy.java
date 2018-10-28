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

public class DocumentSnapshotProxy extends KrollProxy {

	
	private final String LCAT = TifirestoreModule.LCAT;
	public DocumentSnapshot snapshot;

	public DocumentSnapshotProxy() {
			
	}
	public DocumentSnapshotProxy(DocumentSnapshot snapshot) {
		this.snapshot = snapshot;
	}
	
	
    @Kroll.method
    public KrollDict getSnapshot() {
    	KrollDict doc = new KrollDict();
    	for (Map.Entry<String, Object> entry : snapshot.getData()
				.entrySet()) {
    		doc.put(entry.getKey(), entry.getValue());
		}
    	return doc;
    }
	
}
