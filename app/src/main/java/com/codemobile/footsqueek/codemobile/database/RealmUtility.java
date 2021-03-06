package com.codemobile.footsqueek.codemobile.database;

import android.util.Log;

import com.codemobile.footsqueek.codemobile.AppDelegate;
import com.codemobile.footsqueek.codemobile.interfaces.FetcherInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by greg on 19/01/2017.
 */

public class RealmUtility {

    private static List<RealmObject> genericList = new ArrayList<>();

    public static void addNewRow(RealmObject obj){

        Realm realm = AppDelegate.getRealmInstance();
        try{
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(obj);
            realm.commitTransaction();
            Log.d("Realmstuff", "db edit");

        } catch (Exception e) {
            Log.e("RealmError", "error" + e);
            realm.cancelTransaction();

        }

    }

    public static void deleteTables(){
        final Realm realm = AppDelegate.getRealmInstance();
        try{
            realm.beginTransaction();
            realm.delete(Session.class);
            realm.delete(Speaker.class);
            realm.delete(Tag.class);
            realm.delete(Location.class);
            realm.commitTransaction();
            Log.d("Realmstuff", "db drop tables");

        } catch (Exception e) {
            Log.e("RealmError", "error" + e);
            realm.cancelTransaction();

        }
    }

    public static void addNewRows(List<RealmObject> obj, final FetcherInterface fetcherInterface){
        final Realm realm = AppDelegate.getRealmInstance();

        try{
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(obj);
            realm.commitTransaction();
            Log.d("Realmstuff", "db edit");

        } catch (Exception e) {
            Log.e("RealmError", "error" + e);
            realm.cancelTransaction();

        }

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                Log.d("Realmstuff", "realm saved");
                realm.removeAllChangeListeners();
                fetcherInterface.onComplete();

            }
        });

    }

   // public static void addNewRowDelayedCommit(RealmObject obj){

  //      genericList.add(obj);

 //   }

    public static void commitData(){
        Realm realm = AppDelegate.getRealmInstance();
        try{
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(genericList);
            realm.commitTransaction();
            Log.d("Realmstuff", "db edit");

        } catch (Exception e) {
            Log.e("RealmError", "error" + e);
            realm.cancelTransaction();

        }

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                genericList = null;
                Log.d("Realmstuff", "db edit changed!!!1");
            }
        });
    }


    public static void deleteRow(List<String> ids){
        Realm realm = AppDelegate.getRealmInstance();
        List<Speaker>speakers = realm.where(Speaker.class).findAll();
       //half implemeted delete feature that deletes anything in teh old DB not in the newer data. Pass in Id's straight from fetcher

        for (int j = 0; j <speakers.size() ; j++) {
            //if id's dion't match and its the last one#
            Log.d("miss3", "============" +j+"=================" +j+"======================");
            boolean match =false;
            for (int k = 0; k < ids.size(); k++) {
                if(ids.get(k).equals(speakers.get(j).getId())){
                    Log.d("miss3", "id speakers: " +speakers.get(j).getId() +"== K id= " +ids.get(k));
                    match = true;
                    // break;
                }else if(!ids.get(k).equals(speakers.get(j).getId())){

                    if( k == ids.size()-1){
                        if(match){
                            Log.d("miss3", "matched! not deleting");
                        }else{
                            Log.d("miss3", "delete: " +speakers.get(j).getId());
                        }
                    }
                }

            }
            Log.d("miss3", "===================================================");

        }
    }

    public static Session getUpcomingSession(){

        Date date = new Date();
        date.getTime();
        Realm realm = AppDelegate.getRealmInstance();
        List<Session> all = realm.where(Session.class).greaterThan("timeEnd",date).findAllSorted("timeStart");


        return all.get(0);
    }

    public static List<Session> getFutureSessions(){

        Date date = new Date();
        date.getTime();
        Realm realm = AppDelegate.getRealmInstance();

        return realm.where(Session.class).greaterThan("timeEnd",date).findAllSorted("timeStart");
    }


    public static String generatePrimaryKey(String feild1, String feild2){
        return feild1 + feild2;

    }

    public static List<Tag> getUniqueTags(){

        Realm realm = AppDelegate.getRealmInstance();
        List<Tag> tags = realm.where(Tag.class).findAllSorted("tag");
        List<Tag> uniqueTags = new ArrayList<>();
        String tagName = "";
        for (int i = 0; i < tags.size() ; i++) {
            if(!tags.get(i).getTag().equals(tagName)){
                uniqueTags.add(tags.get(i));
            }
            tagName = tags.get(i).getTag();
        }
        return uniqueTags;
    }

}
