package space.badboyin.smap.Interface;

import java.util.ArrayList;

import space.badboyin.smap.Model.Sales;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(ArrayList<Sales> salesList);
    void onFirebaseLoadFailed(String message);
}
