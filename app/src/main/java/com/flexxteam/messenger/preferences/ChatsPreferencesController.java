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

public class ChatsPreferencesController extends RecyclerViewController<Void> implements View.OnClickListener {

  private SettingsAdapter adapter;

  public ChatsPreferencesController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () {
    return Lang.getString(R.string.ChatsPreferences);
  }

  @Override public int getId () {
    return R.id.controller_ChatsPreferences;
  }

  @Override public void onClick (View v) {
    int viewId = v.getId();
    /* if (viewId == R.id.something) {
      Do action.
    } */
  }

  @Override protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override protected void setValuedSetting (ListItem item, SettingView view, boolean isUpdate) {
        view.setDrawModifier(item.getDrawModifier());
        int itemId = item.getId();
        /* if (itemId == R.id.something) {
          Do action.
        } */
      }
    };

    ArrayList<ListItem> items = new ArrayList<>();

    // List items.

    adapter.setItems(items, true);
    recyclerView.setAdapter(adapter);
  }
}
