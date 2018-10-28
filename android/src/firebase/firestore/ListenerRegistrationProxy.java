package firebase.firestore;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class ListenerRegistrationProxy extends KrollProxy {

	
	private final String LCAT = TifirestoreModule.LCAT;
	public ListenerRegistration registration;

	public ListenerRegistrationProxy() {
			
	}
	public ListenerRegistrationProxy(ListenerRegistration registration) {
		this.registration = registration;
	}
	
	
    @Kroll.method
    public void remove() {
    	registration.remove();
    }
	
}
