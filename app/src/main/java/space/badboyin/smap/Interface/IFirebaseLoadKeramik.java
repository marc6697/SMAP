package space.badboyin.smap.Interface;

import java.util.ArrayList;

import space.badboyin.smap.Model.Keramik;

public interface IFirebaseLoadKeramik {
    void onFirebaseLoadSuccess(ArrayList<Keramik> keramikArrayList);
    void onFirebaseLoadSpinnerData(ArrayList<Keramik> keramikArrayList);
    void onFirebaseLoadNamaKeramik(ArrayList<Keramik> keramikArrayList);
    void onFirebaseLoadFailed(String message);
}
