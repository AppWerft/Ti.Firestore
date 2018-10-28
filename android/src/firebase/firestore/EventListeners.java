package firebase.firestore;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.common.Log;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class EventListeners {
	private static final String LCAT = TifirestoreModule.LCAT;
	
	KrollFunction callback = null;

	public EventListeners() {
		
	}
	
public EventListeners(KrollFunction callback) {
	this.callback=callback;
		
	}
	
	publics final class onSnapshotQueryListener implements
			EventListener<QuerySnapshot> {
		@Override
		public void onEvent(@Nullable QuerySnapshot value,
				@Nullable FirebaseFirestoreException e) {
			if (e != null) {
				Log.w(LCAT, "Listen failed.", e);
				return;
			}

			List<String> cities = new ArrayList<>();
			for (QueryDocumentSnapshot doc : value) {
				if (doc.get("name") != null) {
					cities.add(doc.getString("name"));
				}
			}
			Log.d(LCAT, "Current cites in CA: " + cities);
		}
	}
}
