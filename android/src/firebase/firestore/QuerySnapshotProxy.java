package firebase.firestore;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.*;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class QuerySnapshotProxy extends KrollProxy {

	
	private final String LCAT = TifirestoreModule.LCAT;
	public QuerySnapshot snapshots;

	public QuerySnapshotProxy() {
			
	}
	public QuerySnapshotProxy(QuerySnapshot snapshots) {
		this.snapshots = snapshots;
	}
	
	
    @Kroll.method
    public KrollDict getDocuments() {
    	KrollDict doc = new KrollDict();
		List<DocumentSnapshotProxy> results = new ArrayList<DocumentSnapshotProxy>();
		
    	for (QueryDocumentSnapshot snapshot : snapshots) {
			results.add(new DocumentSnapshotProxy(snapshot));
		}
		doc.put("hasPendingWrites", snapshots.getMetadata()
				.hasPendingWrites());
		doc.put("isFromCache", snapshots.getMetadata().isFromCache());
		doc.put("data", results.toArray(new KrollDict[results.size()]));
		
        
    	return doc;
    }
	@Kroll.method
	public KrollDict getChanges() {
		KrollDict doc = new KrollDict();
		for (DocumentChange dc : snapshots.getDocumentChanges()) {
			if (dc.getType() == Type.ADDED) {
			}
		}
		return doc;
	}
}
