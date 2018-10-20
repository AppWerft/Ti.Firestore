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

public class CollectionReferenceProxy extends KrollProxy {

	private final class onFailure implements OnFailureListener {
		@Override
		public void onFailure(@NonNull Exception e) {
		}
	}

	private final class onSuccess implements
			OnSuccessListener<DocumentReference> {
		@Override
		public void onSuccess(DocumentReference documentReference) {

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
				.addOnSuccessListener(new onSuccess())
				.addOnFailureListener(new onFailure());

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
		db.collection(collectionName).add(new HashMap<>(dict))
				.addOnSuccessListener(new onSuccess())
				.addOnFailureListener(new onFailure());

	}

	/*
	 * first argument ist KrollDicz with condtions or null second optional arg
	 * is callback
	 */
	@Kroll.method
	public void listen(Object[] args) {
		CollectionReference ref = db.collection(collectionName);
		if (args[0] != null && args[0] instanceof KrollDict) {
			KrollDict opts = (KrollDict) args[0];
			if (opts.containsKeyAndNotNull("where")) {
				Object o = opts.get("where");
				if (o instanceof KrollDict) {
					KrollDict where = (KrollDict) o;
					for (String key : opts.keySet()) {
						final String value = opts.getString(key);
						ref = parseAndBuildQuery(key, value, ref);
					}
				} else
					return;
			}
			if (opts.containsKeyAndNotNull("orderBy")) {
				ref = (CollectionReference) ref.orderBy(opts
						.getString("orderBy"));
			}
			if (opts.containsKeyAndNotNull("limit")) {
				ref = (CollectionReference) ref.limit(opts.getInt("limit"));
			}

		}
	}

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

}
