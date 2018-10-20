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

public class CollectionReferenceProxy extends KrollProxy {
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
	public void add(KrollDict doc) {
		db.collection(collectionName).add(new HashMap<>(doc));
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
			for (String key : opts.keySet()) {
				parseCondition(key, opts.getString(key), ref);
			}
		}
	}

	private static Query parseCondition(String field, String foo,
			CollectionReference ref) {
		Query query = null;
		String[] parts = foo.split(" ");
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
		return query;
	}

}
