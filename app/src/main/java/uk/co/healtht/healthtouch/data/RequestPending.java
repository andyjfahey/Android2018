//package uk.co.healtht.healthtouch.data;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import org.json.JSONArray;
//
//import java.lang.reflect.Type;
//import java.util.List;
//
///**
// * Created by MacBook on 06/03/16.
// */
//public class RequestPending {
//    public String url;
//    public String value;
//    public String type; // get or post
//    public long timestamp;
//
//    public static List<RequestPending> getList(String jsonString){
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<RequestPending>>(){}.getType();
//        List<RequestPending> contactList = gson.fromJson(jsonString, type);
//        /*for (RequestPending contact : contactList){
//            Log.i("Contact Details", contact.id + "-" + contact.name + "-" + contact.email);
//        }*/
//        return contactList;
//    }
//
//    public static String getJSONArray(List<RequestPending> contactList){
//        Gson gson = new Gson();
//        String jsonA = gson.toJson(contactList);
//        return jsonA;
//    }
//}
