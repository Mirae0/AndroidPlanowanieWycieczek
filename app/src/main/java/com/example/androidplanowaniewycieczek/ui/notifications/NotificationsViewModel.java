package com.example.androidplanowaniewycieczek.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

   private final MutableLiveData<String> mText;
//
 public NotificationsViewModel() {
 mText = new MutableLiveData<>();
mText.setValue("Powiadomienia");
}
//
public LiveData<String> getText() {
  return mText;
 }
}