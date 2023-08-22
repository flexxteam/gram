// This is a Flexxgram source code file.
// Flexxgram is not a trademark of Telegram and Telegram X.
// Flexxgram is an open-source and freely distributed modification of Telegram X.
//
// Copyright (C) 2023 Flexxteam.

package com.flexxteam.messenger.preferences.drawer;

import android.content.Context;
import android.view.View;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.component.base.SettingView;
import org.thunderdog.challegram.config.Config;
import org.thunderdog.challegram.core.Lang;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.v.CustomRecyclerView;

import java.util.ArrayList;

import com.flexxteam.messenger.FlexxConfig;

public class DrawerManagerController extends RecyclerViewController<Void> implements View.OnClickListener {

  private SettingsAdapter adapter;

  public DrawerManagerController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () {
    return Lang.getString(R.string.DrawerManager);
  }

  @Override public int getId () {
    return R.id.controller_DrawerManager;
  }

  @Override public void onClick (View v) {
    int viewId = v.getId();
    if (viewId == R.id.btn_contacts) {
      FlexxConfig.instance().toggleDrawerElements(1);
      adapter.updateValuedSettingById(R.id.btn_contacts);
    } else if (viewId == R.id.btn_calls) {
      FlexxConfig.instance().toggleDrawerElements(2);
      adapter.updateValuedSettingById(R.id.btn_calls);
    } else if (viewId == R.id.btn_savedMessages) {
      FlexxConfig.instance().toggleDrawerElements(3);
      adapter.updateValuedSettingById(R.id.btn_savedMessages);
    } else if (viewId == R.id.btn_invite) {
      FlexxConfig.instance().toggleDrawerElements(4);
      adapter.updateValuedSettingById(R.id.btn_invite);
    } else if (viewId == R.id.btn_help) {
      FlexxConfig.instance().toggleDrawerElements(5);
      adapter.updateValuedSettingById(R.id.btn_help);
    } else if (viewId == R.id.btn_night) {
      FlexxConfig.instance().toggleDrawerElements(6);
      adapter.updateValuedSettingById(R.id.btn_night);
    }
  }

  @Override protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override protected void setValuedSetting (ListItem item, SettingView view, boolean isUpdate) {
        view.setDrawModifier(item.getDrawModifier());
        int itemId = item.getId();
        if (itemId == R.id.btn_contacts) {
          view.getToggler().setRadioEnabled(FlexxConfig.contacts, isUpdate);
        } else if (itemId == R.id.btn_calls) {
          view.getToggler().setRadioEnabled(FlexxConfig.calls, isUpdate);
        } else if (itemId == R.id.btn_savedMessages) {
          view.getToggler().setRadioEnabled(FlexxConfig.savedMessages, isUpdate);
        } else if (itemId == R.id.btn_invite) {
          view.getToggler().setRadioEnabled(FlexxConfig.invite, isUpdate);
        } else if (itemId == R.id.btn_help) {
          view.getToggler().setRadioEnabled(FlexxConfig.help, isUpdate);
        } else if (itemId == R.id.btn_night) {
          view.getToggler().setRadioEnabled(FlexxConfig.night, isUpdate);
        }
      }
    };

    ArrayList<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_contacts, 0, R.string.Contacts));
    if (Config.CHAT_FOLDERS_ENABLED) {
      items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
      items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_calls, 0, R.string.Calls));
    }
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_savedMessages, 0, R.string.SavedMessages));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_invite, 0, R.string.InviteFriends));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_help, 0, R.string.Help));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_night, 0, R.string.NightMode));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.WarningDrawerManager));

    adapter.setItems(items, true);
    recyclerView.setAdapter(adapter);
  }
}
