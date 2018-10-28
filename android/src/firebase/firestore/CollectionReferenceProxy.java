package firebase.firestore;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.*;
import com.google.firebase.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.tasks.OnCompleteListener;

@Kroll.module(parentModule = TifirestoreModule.class, propertyAccessors = {
		"onCompleted", "onError" })
public class CollectionReferenceProxy extends KrollProxy {

	private String collectionName;
	private KrollFunction Callback;
	private static final int GET = 0;
	private static final int LISTEN = 1;
	private static final String LCAT = TifirestoreModule.LCAT;
	private FirebaseFirestore db;

	// Realtime listening of query:
	private final class onSnapshotQueryListener implements
			EventListener<QuerySnapshot> {
		@Override
		public void onEvent(@Nullable QuerySnapshot value,
				@Nullable FirebaseFirestoreException ex) {
			KrollDict event = new KrollDict();
			if (ex != null) {
				event.put("code", ex.getCode());
				event.put("message", ex.getMessage());
				dispatchOnError(event);
				return;
			}
			List<DocumentSnapshotProxy> results = new ArrayList<DocumentSnapshotProxy>();
			for (QueryDocumentSnapshot snapshot : value) {
				results.add(new DocumentSnapshotProxy(snapshot));
			}
			event.put("hasPendingWrites", value.getMetadata()
					.hasPendingWrites());
			event.put("isFromCache", value.getMetadata().isFromCache());
			event.put("data", results.toArray(new KrollDict[results.size()]));
			dispatchOnCompleted(event);
		}
	}

	// Realtime listening of single Document:
	private final class onSnapshotListener implements
			EventListener<DocumentSnapshot> {
		@Override
		public void onEvent(@Nullable DocumentSnapshot snapshot,
				@Nullable FirebaseFirestoreException ex) {
			KrollDict event = new KrollDict();
			if (ex != null) {
				event.put("code", ex.getCode());
				event.put("message", ex.getMessage());
				dispatchOnError(event);
				return;
			}
			if (snapshot != null && snapshot.exists()) {
				event.put("data", new DocumentSnapshotProxy(snapshot));
				dispatchOnCompleted(event);
			} else {
				Log.d(LCAT, "Current data: null");
			}
		}
	}

	// getting of one single Document:
	private final class onComplete implements
			OnCompleteListener<DocumentSnapshot> {
		@Override
		public void onComplete(@NonNull Task<DocumentSnapshot> task) {
			KrollDict event = new KrollDict();
			if (task.isSuccessful()) {
				DocumentSnapshot snapshot = task.getResult();
				if (snapshot.exists()) {
					event.put("data", new DocumentSnapshotProxy(snapshot));
					dispatchOnCompleted(event);
				} else {
					Log.d(LCAT, "No such document");
				}
			} else {
				event.put("message", task.getException().getMessage());
				dispatchOnError(event);
				return;
			}
		}
	}

	// query a set of results
	private final class onQueryComplete implements
			OnCompleteListener<QuerySnapshot> {
		@Override
		public void onComplete(@NonNull Task<QuerySnapshot> task) {
			KrollDict result = new KrollDict();
			if (task.isSuccessful()) {
				result.put("hasPendingWrites", task.getResult().getMetadata()
						.hasPendingWrites());
				result.put("isFromCache", task.getResult().getMetadata()
						.isFromCache());
				ArrayList<DocumentSnapshotProxy> list = new ArrayList<DocumentSnapshotProxy>();
				for (QueryDocumentSnapshot snapshot : task.getResult()) {
					list.add(new DocumentSnapshotProxy(snapshot));
				}
				result.put("data", list.toArray((new KrollDict[list.size()])));
				dispatchOnCompleted(result);
			} else {
				result.put("message", task.getException().getMessage());
				dispatchOnError(result);
			}
		}
	}

	private final class onSetSuccess implements OnSuccessListener<Void> {
		@Override
		public void onSuccess(Void aVoid) {
			KrollDict result = new KrollDict();
			dispatchOnCompleted(result);
		}
	}

	private final class onAddSuccess implements
			OnSuccessListener<DocumentReference> {
		@Override
		public void onSuccess(DocumentReference documentReference) {
			KrollDict result = new KrollDict();
			result.put("id", documentReference.getId());
			result.put("path", documentReference.getPath());
			result.put("path", documentReference.getPath());
			dispatchOnCompleted(result);
		}
	}

	private final class onFailure implements OnFailureListener {
		@Override
		public void onFailure(@NonNull Exception ex) {
			KrollDict e = new KrollDict();
			e.put("success", false);
			e.put("message", ex.getMessage());
			dispatchOnError(e);
		}
	}

	// end of callbacks

	public CollectionReferenceProxy() {
		db = FirebaseFirestore.getInstance();
	}

	public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
		if (args.length == 0) {
			Log.e(LCAT,
					"collectionName nasme for CollectionReference is missing");
			return;
		}
		collectionName = (String) args[1];
		if (args.length == 2 && args[1] instanceof KrollFunction) {
			Callback = (KrollFunction) args[1];
		}
	}

	@Kroll.method
	public void add(Object[] args) {
		if (args.length < 1) {
			Log.e(LCAT, "add() needs minimal one parameter");
			return;
		}
		if (!(args[0] instanceof KrollDict)) {
			Log.e(LCAT, "add() needs minimal one parameter as object");
			return;
		}
		KrollDict opts = (KrollDict) args[0];
		if (!opts.containsKeyAndNotNull("data")) {
			Log.e(LCAT, "add() first parameter must contain a property 'data'");
			return;
		}
		Object o = opts.get("data");
		if (!(o instanceof KrollDict)) {
			Log.e(LCAT, "data must be an object");
			return;
		}
		KrollDict dict = (KrollDict) o;
		db.collection(collectionName).add(new HashMap<>(dict))
				.addOnSuccessListener(new onAddSuccess())
				.addOnFailureListener(new onFailure());
		if (args.length == 2)
			registerCallback(args[1]);

	}

	@Kroll.method
	public void set(Object[] args) {
		if (args.length < 1) {
			Log.e(LCAT, "add() needs minimal one parameter");
			return;
		}
		if (!(args[0] instanceof KrollDict)) {
			Log.e(LCAT, "add() needs minimal one parameter as object");
			return;
		}
		KrollDict opts = (KrollDict) args[0];
		if (!opts.containsKeyAndNotNull("data")) {
			Log.e(LCAT, "add() first parameter must contain a property 'data'");
			return;
		}
		Object o = opts.get("data");
		if (!(o instanceof KrollDict)) {
			Log.e(LCAT, "data must be an object");
			return;
		}
		KrollDict dict = (KrollDict) o;
		final String id = opts.getString("id");
		db.collection(collectionName).document(id).set(new HashMap<>(dict))
				.addOnSuccessListener(new onSetSuccess())
				.addOnFailureListener(new onFailure());
	}

	@Kroll.method
	public void get(Object[] args) {
		prepareAndStartQuery(args, GET);
	}

	@Kroll.method
	public ListenerRegistrationProxy listen(Object[] args) {
		return prepareAndStartQuery(args, LISTEN);
	}

	private ListenerRegistrationProxy prepareAndStartQuery(Object[] args,
			int QUERYTYPE) {
		if (args.length < 1) {
			Log.e(LCAT, "get() needs minimal one parameter");
			return null;
		}
		CollectionReference collRef = db.collection(collectionName);

		// getting/listening for one document:
		if ((args[0] instanceof String)) {
			String id = (String) args[0];
			DocumentReference docref = collRef.document(id);
			switch (QUERYTYPE) {
			case GET:
				docref.get().addOnCompleteListener(new onComplete());
				return null;
			case LISTEN:
				ListenerRegistration registration = docref
						.addSnapshotListener(new onSnapshotListener());
				return new ListenerRegistrationProxy(registration);
			}
			return null;
		} else if (!(args[0] instanceof KrollDict)) {
			Log.e(LCAT, "get() needs minimal one parameter as object");
			return null;
		}

		KrollDict opts = (KrollDict) args[0];
		if (opts.containsKeyAndNotNull("where")) {
			Object o = opts.get("where");
			if (o instanceof KrollDict) {
				KrollDict where = (KrollDict) o;
				for (String key : opts.keySet()) {
					final String value = opts.getString(key);
					collRef = parseAndBuildQuery(key, value, collRef);
				}
			}

		}
		if (opts.containsKeyAndNotNull("orderBy")) {
			collRef = (CollectionReference) collRef.orderBy(opts
					.getString("orderBy"));
		}
		if (opts.containsKeyAndNotNull("limit")) {
			collRef = (CollectionReference) collRef.limit(opts.getInt("limit"));
		}
		switch (QUERYTYPE) {
		case GET:
			collRef.get().addOnCompleteListener(new onQueryComplete());
			return null;
		case LISTEN:
			ListenerRegistration registration = collRef
					.addSnapshotListener(new onSnapshotQueryListener());
			return new ListenerRegistrationProxy(registration);
		}
	}

	/* helper for transform JS object to query */
	private static CollectionReference parseAndBuildQuery(String field,
			String valueString, CollectionReference ref) {
		Query query = null;
		String[] parts = valueString.split(" ");
		if (parts.length < 2)
			return null;
		String value = parts[1];
		switch (parts[0]) {
		case "<":
			query = ref.whereLessThan(field, value);
			break;
		case "<=":
		case "≤":
			query = ref.whereLessThanOrEqualTo(field, value);
			break;
		case ">":
			query = ref.whereGreaterThan(field, value);
			break;
		case ">=":
		case "≥":
			query = ref.whereGreaterThanOrEqualTo(field, value);
			break;
		case "=":
		case "==":
			query = ref.whereEqualTo(field, value);
			break;
		}
		return (CollectionReference) query;
	}

	private void registerCallback(Object o) {
		if (o == null)
			return;
		if (!(o instanceof KrollFunction))
			return;
		Callback = (KrollFunction) o;
	}

	private void dispatchOnCompleted(KrollDict event) {
		KrollFunction onverificationcompleted = (KrollFunction) getProperty("onCompleted");
		if (onverificationcompleted != null) {
			onverificationcompleted.call(getKrollObject(),
					new Object[] { event });
		}
		if (this.hasListeners("completed")) {
			this.fireEvent("completed", new Object[] { event });
		}
	}

	private void dispatchOnError(KrollDict event) {
		KrollFunction onError = (KrollFunction) getProperty("onError");
		if (onError != null) {
			onError.call(getKrollObject(), new Object[] { event });
		}
		if (this.hasListeners("error")) {
			this.fireEvent("error", new Object[] { event });
		}
	}
}
