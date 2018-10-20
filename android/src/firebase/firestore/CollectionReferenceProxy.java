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

import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.tasks.OnCompleteListener;

public class CollectionReferenceProxy extends KrollProxy {

	private final class onComplete implements
			OnCompleteListener<DocumentSnapshot> {
		@Override
		public void onComplete(@NonNull Task<DocumentSnapshot> task) {
			if (task.isSuccessful()) {
				DocumentSnapshot documentSnapshot = task.getResult();
				if (documentSnapshot.exists()) {
					Log.d(LCAT,
							"DocumentSnapshot data: "
									+ documentSnapshot.getData());
					KrollDict result =new KrollDict();
					try {
						result = new KrollDict(documentSnapshot.toObject(JSONObject.class));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (Callback != null)
						Callback.call(getKrollObject(), result);
				} else {
					Log.d(LCAT, "No such document");
				}
			} else {
				Log.d(LCAT, "get failed with ", task.getException());
			}
		}
	}

	private final class onQueryComplete implements
			OnCompleteListener<QuerySnapshot> {
		@Override
		public void onComplete(@NonNull Task<QuerySnapshot> task) {
			if (task.isSuccessful()) {
				ArrayList<JSONObject> list = new ArrayList<JSONObject>();
				for (QueryDocumentSnapshot queryDocumentSnapshot : task
						.getResult()) {
					list.add(queryDocumentSnapshot.toObject(JSONObject.class));
					Log.d(LCAT, "DocumentSnapshot data: "
							+ queryDocumentSnapshot.getData());
				}
				if (Callback != null)
					Callback.call(getKrollObject(), list.toArray());
			} else {

			}
		}
	}

	private final class onSetSuccess implements OnSuccessListener<Void> {
		@Override
		public void onSuccess(Void aVoid) {
			KrollDict e = new KrollDict();
			e.put("success", true);
			e.put("action", "set");
			if (Callback != null)
				Callback.call(getKrollObject(), e);
		}
	}

	private final class onAddSuccess implements
			OnSuccessListener<DocumentReference> {
		@Override
		public void onSuccess(DocumentReference documentReference) {
			KrollDict e = new KrollDict();
			e.put("success", true);
			e.put("action", "add");
			e.put("doc", new DocumentReferenceProxy(documentReference));
			if (Callback != null)
				Callback.call(getKrollObject(), e);

		}
	}

	private final class onFailure implements OnFailureListener {
		@Override
		public void onFailure(@NonNull Exception ex) {
			KrollDict e = new KrollDict();
			e.put("success", false);
			e.put("message", ex.getMessage());
			if (Callback != null)
				Callback.call(getKrollObject(), e);
		}
	}

	private String collectionName;
	private KrollFunction Callback;
	private final String LCAT = TifirestoreModule.LCAT;
	private FirebaseFirestore db;

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
		if (args.length < 1) {
			Log.e(LCAT, "get() needs minimal one parameter");
			return;
		}
		CollectionReference collectionRef = db.collection(collectionName);
		if ((args[0] instanceof String)) {
			String id = (String) args[0];
			collectionRef.document(id).get()
					.addOnCompleteListener(new onComplete());
			;
			return;
		} else if (!(args[0] instanceof KrollDict)) {
			Log.e(LCAT, "get() needs minimal one parameter as object");
			return;
		}

		KrollDict opts = (KrollDict) args[0];
		if (opts.containsKeyAndNotNull("where")) {
			Object o = opts.get("where");
			if (o instanceof KrollDict) {
				KrollDict where = (KrollDict) o;
				for (String key : opts.keySet()) {
					final String value = opts.getString(key);
					collectionRef = parseAndBuildQuery(key, value,
							collectionRef);
				}
			} else
				return;
		}
		if (opts.containsKeyAndNotNull("orderBy")) {
			collectionRef = (CollectionReference) collectionRef.orderBy(opts
					.getString("orderBy"));
		}
		if (opts.containsKeyAndNotNull("limit")) {
			collectionRef = (CollectionReference) collectionRef.limit(opts
					.getInt("limit"));
		}
		collectionRef.get().addOnCompleteListener(new onQueryComplete());

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
}
