// This is a Flexxgram source code file.
// Flexxgram is not a trademark of Telegram and Telegram X.
// Flexxgram is an open-source and freely distributed modification of Telegram X.
//
// Copyright (C) 2023 Flexxteam.

package com.flexxteam.messenger;

import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.drinkmore.Tracer;
import org.thunderdog.challegram.Log;
import org.thunderdog.challegram.tool.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

import me.vkryl.core.reference.ReferenceList;
import me.vkryl.leveldb.LevelDB;

public class FlexxConfig {

  private static final int VERSION = 1;
  private static final AtomicBoolean hasInstance = new AtomicBoolean(false);
  private static volatile FlexxConfig instance;
  private final LevelDB config;
  private static final String KEY_VERSION = "version";

  private static final String PREF_HIDE_PHONE_NUMBER = "hide_phone_number";
  private static final String PREF_ENABLE_CHAT_FOLDERS = "enable_chat_folders";
  private static final String PREF_CHATFOLDERS_HIDE_BOTTOMBAR_ON_SCROLL = "chatfolders_hide_bottombar_on_scroll";
  private static boolean hidePhoneNumber = instance().getBoolean(PREF_HIDE_PHONE_NUMBER, false);
  private static boolean enableChatFolders = instance().getBoolean(PREF_ENABLE_CHAT_FOLDERS, true);
  private static boolean chatFoldersHideBottomBarOnScroll = instance().getBoolean(PREF_CHATFOLDERS_HIDE_BOTTOMBAR_ON_SCROLL, true);

  private static final String PREF_DISABLE_STICKER_TIMESTAMP = "disable_sticker_timestamp";
  private static final String PREF_DISABLE_CAMERA_BUTTON = "disable_camera_button";
  private static final String PREF_DISABLE_RECORD_BUTTON = "disable_record_button";
  private static final String PREF_DISABLE_SENDER_BUTTON = "disable_sender_button";
  private static boolean disableStickerTimestamp = instance().getBoolean(PREF_DISABLE_STICKER_TIMESTAMP, false);
  private static boolean disableCameraButton = instance().getBoolean(PREF_DISABLE_CAMERA_BUTTON, false);
  private static boolean disableRecordButton = instance().getBoolean(PREF_DISABLE_RECORD_BUTTON, false);
  private static boolean disableSenderButton = instance().getBoolean(PREF_DISABLE_SENDER_BUTTON, false);

  private static final String PREF_PHOTO_SIZE_LIMIT_2560 = "photo_size_limit_2560";
  private static boolean photoSizeLimit2560 = instance().getBoolean(PREF_PHOTO_SIZE_LIMIT_2560, false);

  private FlexxConfig () {
    File configDir = new File(UI.getAppContext().getFilesDir(), "flexx_config");
    if (!configDir.exists() && !configDir.mkdir()) {
      throw new IllegalStateException("Unable to create working directory");
    }
    long ms = SystemClock.uptimeMillis();
    config = new LevelDB(new File(configDir, "db").getPath(), true, new LevelDB.ErrorHandler() {
      @Override public boolean onFatalError (LevelDB levelDB, Throwable error) {
        Tracer.onDatabaseError(error);
        return true;
      }

      @Override public void onError (LevelDB levelDB, String message, @Nullable Throwable error) {
        // Cannot use custom Log, since settings are not yet loaded
        android.util.Log.e(Log.LOG_TAG, message, error);
      }
    });
    int configVersion = 0;
    try {
      configVersion = Math.max(0, config.tryGetInt(KEY_VERSION));
    } catch (FileNotFoundException ignored) {
    }
    if (configVersion > VERSION) {
      Log.e("Downgrading database version: %d -> %d", configVersion, VERSION);
      config.putInt(KEY_VERSION, VERSION);
    }
    for (int version = configVersion + 1; version <= VERSION; version++) {
      SharedPreferences.Editor editor = config.edit();
      editor.putInt(KEY_VERSION, version);
      editor.apply();
    }
    Log.i("Opened database in %dms", SystemClock.uptimeMillis() - ms);
  }

  public static FlexxConfig instance () {
    if (instance == null) {
      synchronized (FlexxConfig.class) {
        if (instance == null) {
          if (hasInstance.getAndSet(true)) throw new AssertionError();
          instance = new FlexxConfig();
        }
      }
    }
    return instance;
  }

  public LevelDB edit () {
    return config.edit();
  }

  public void remove (String key) {
    config.remove(key);
  }

  public void putLong (String key, long value) {
    config.putLong(key, value);
  }
  public long getLong (String key, long defValue) {
    return config.getLong(key, defValue);
  }

  public void putLongArray (String key, long[] value) {
    config.putLongArray(key, value);
  }
  public long[] getLongArray (String key) {
    return config.getLongArray(key);
  }

  public void putInt (String key, int value) {
    config.putInt(key, value);
  }
  public int getInt (String key, int defValue) {
    return config.getInt(key, defValue);
  }

  public void putFloat (String key, float value) {
    config.putFloat(key, value);
  }
  public void getFloat (String key, float defValue) {
    config.getFloat(key, defValue);
  }

  public void putBoolean (String key, boolean value) {
    config.putBoolean(key, value);
  }
  public boolean getBoolean (String key, boolean defValue) {
    return config.getBoolean(key, defValue);
  }

  public void putString (String key, @NonNull String value) {
    config.putString(key, value);
  }
  public String getString (String key, String defValue) {
    return config.getString(key, defValue);
  }

  public boolean containsKey (String key) {
    return config.contains(key);
  }

  public LevelDB config () {
    return config;
  }

  public interface SettingsChangeListener {
    void onSettingsChanged (String key, Object newSettings, Object oldSettings);
  }

  private ReferenceList<SettingsChangeListener> newSettingsListeners;

  public void addNewSettingsListener (SettingsChangeListener listener) {
    if (newSettingsListeners == null)
      newSettingsListeners = new ReferenceList<>();
    newSettingsListeners.add(listener);
  }

  private void notifyNewSettingsListeners (String key, Object newSettings, Object oldSettings) {
    if (newSettingsListeners != null) {
      for (SettingsChangeListener listener : newSettingsListeners) {
        listener.onSettingsChanged(key, newSettings, oldSettings);
      }
    }
  }

  public void toggleHidePhoneNumber() {
  	putBoolean(PREF_HIDE_PHONE_NUMBER, hidePhoneNumber ^= true);
  }

  public void toggleDisableStickerTimestamp() {
    putBoolean(PREF_DISABLE_STICKER_TIMESTAMP, disableStickerTimestamp ^= true);
  }

  public void toggleDisableCameraButton() {
    notifyNewSettingsListeners(PREF_DISABLE_CAMERA_BUTTON, !disableCameraButton, disableCameraButton);
    putBoolean(PREF_DISABLE_CAMERA_BUTTON, disableCameraButton ^= true);
  }

  public void toggleDisableRecordButton() {
    notifyNewSettingsListeners(PREF_DISABLE_RECORD_BUTTON, !disableRecordButton, disableRecordButton);
    putBoolean(PREF_DISABLE_RECORD_BUTTON, disableRecordButton ^= true);
  }

  public void toggleDisableSenderButton() {
    notifyNewSettingsListeners(PREF_DISABLE_SENDER_BUTTON, !disableSenderButton, disableSenderButton);
    putBoolean(PREF_DISABLE_SENDER_BUTTON, disableSenderButton ^= true);
  }

  public void toggleEnableChatFolders() {
    putBoolean(PREF_ENABLE_CHAT_FOLDERS, enableChatFolders ^= true);
  }

  public void toggleChatFoldersHideBottomBarOnScroll() {
    putBoolean(PREF_CHATFOLDERS_HIDE_BOTTOMBAR_ON_SCROLL, chatFoldersHideBottomBarOnScroll ^= true);
  }

  public void togglePhotoSizeLimit2560() {
    putBoolean(PREF_PHOTO_SIZE_LIMIT_2560, photoSizeLimit2560 ^= true);
  }
}
