// This is a Flexxgram source code file.
// Flexxgram is not a trademark of Telegram and Telegram X.
// Flexxgram is an open-source and freely distributed modification of Telegram X.
//
// Copyright (C) 2023 Flexxteam.

package com.flexxteam.messenger.preferences;

import android.content.Context;
import android.view.View;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.component.base.SettingView;
import org.thunderdog.challegram.core.Lang;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.v.CustomRecyclerView;

import java.util.ArrayList;

import com.flexxteam.messenger.FlexxConfig;

public class OtherPreferencesController extends RecyclerViewController<Void> implements View.OnClickListener {

  private SettingsAdapter adapter;

  public OtherPreferencesController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () {
    return Lang.getString(R.string.OtherPreferences);
  }

  @Override public int getId () {
    return R.id.controller_OtherPreferences;
  }

  @Override public void onClick (View v) {
    int viewId = v.getId();
    if (viewId == R.id.btn_PhotoSizeLimit2560) {
      FlexxConfig.instance().togglePhotoSizeLimit2560();
      adapter.updateValuedSettingById(R.id.btn_PhotoSizeLimit2560);
    } 
  }

  @Override protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override protected void setValuedSetting (ListItem item, SettingView view, boolean isUpdate) {
        view.setDrawModifier(item.getDrawModifier());
        int itemId = item.getId();
        if (itemId == R.id.btn_PhotoSizeLimit2560) {
          view.getToggler().setRadioEnabled(FlexxConfig.photoSizeLimit2560, isUpdate);
          view.setData(R.string.PhotoSizeLimit2560Desc);
        } 
      }
    };

    ArrayList<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_EMPTY_OFFSET_SMALL));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.Experimental));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_VALUED_SETTING_COMPACT_WITH_TOGGLER, R.id.btn_PhotoSizeLimit2560, 0, R.string.PhotoSizeLimit2560));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.WarningPhotoSizeLimit));

    adapter.setItems(items, true);
    recyclerView.setAdapter(adapter);
  }
}
