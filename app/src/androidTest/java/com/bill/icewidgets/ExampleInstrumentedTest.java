package com.bill.icewidgets;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
//        Realm realm = Realm.getDefaultInstance();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                Dog dog = realm.createObject(Dog.class);
//
//                for (int i = 0; i < 5; i++) {
//                    DogType type = realm.createObject(DogType.class);
//                    type.id.setVal("" + i);
//                    type.type.setVal("yyy" + i);
//                }
//                dog.name.setVal("xxx1");
//                dog.type = realm.where(DogType.class).equalTo("id", 2).findFirst();
//            }
//        });
//
//        RealmResults<Dog> all1 = realm.where(Dog.class).findAll();
//        for (Dog dog : all1) {
//            System.out.println(dog.name.getVal());
//            System.out.println(dog.type.type.getVal());
//            System.out.println(dog.type.id.getVal());
//        }
//
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.where(DogType.class).equalTo("id", 2).findFirst().deleteFromRealm();
//            }
//        });
//
//        RealmResults<Dog> all2 = realm.where(Dog.class).findAll();
//        for (Dog dog : all2) {
//            System.out.println(dog.name.getVal());
//            System.out.println(dog.type.type.getVal());
//            System.out.println(dog.type.id.getVal());
//        }

    }


}
