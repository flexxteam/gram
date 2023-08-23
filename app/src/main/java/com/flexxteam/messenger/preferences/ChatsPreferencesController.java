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
    if (viewId == R.id.btn_IncreaseRecentStickersCount) {
      FlexxConfig.instnace().toggleIncreaseRecentStickersCount();
      adapter.updateValuedSettingById(R.id.btn_IncreaseRecentStickersCount)
    } else if (viewId == R.id.btn_DisableStickerTimestamp) {
      FlexxConfig.instance().toggleDisableStickerTimestamp();
      adapter.updateValuedSettingById(R.id.btn_DisableStickerTimestamp)
    } else if (viewId == R.id.btn_DisableCameraButton) {
      FlexxConfig.instance().toggleDisableChatButtons(1);
      adapter.updateValuedSettingById(R.id.btn_DisableCameraButton)
    } else if (viewId == R.id.btn_DisableRecordButton) {
      FlexxConfig.instance().toggleDisableChatButtons(2);
      adapter.updateValuedSettingById(R.id.btn_DisableRecordButton)
    } else if (viewId == R.id.btn_DisableSenderButton) {
      FlexxConfig.instance().toggleDisableChatButtons(3);
      adapter.updateValuedSettingById(R.id.btn_DisableSenderButton);
    }
  }

  @Override protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override protected void setValuedSetting (ListItem item, SettingView view, boolean isUpdate) {
        view.setDrawModifier(item.getDrawModifier());
        int itemId = item.getId();
        if (itemId == R.id.btn_IncreaseRecentStickersCount) {
          view.getToggler().setRadioEnabled(FlexxConfig.increaseRecentStickersCount, isUpdate);
          view.setData(R.string.IncreaseRecentStickersCountDesc);
        } else if (itemId == R.id.btn_DisableStickerTimestamp) {
          view.getToggler().setRadioEnabled(FlexxConfig.disableStickerTimestamp, isUpdate);
        } else if (itemId == R.id.btn_DisableCameraButton) {
          view.getToggler().setRadioEnabled(FlexxConfig.disableCameraButton, isUpdate);
        } else if (itemId == R.id.btn_DisableRecordButton) {
          view.getToggler().setRadioEnabled(FlexxConfig.disableRecordButton, isUpdate);
        } else if (itemId == R.id.btn_DisableSenderButton) {
          view.getToggler().setRadioEnabled(FlexxConfig.disableSenderButton, isUpdate);
        }
      }
    };

    ArrayList<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_EMPTY_OFFSET_SMALL));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.Stickers));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_DisableStickerTimestamp, 0, R.string.DisableStickerTimestamp));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_VALUED_SETTING_COMPACT_WITH_TOGGLER, R.id.btn_IncreaseRecentStickersCount, 0, R.string.IncreaseRecentStickersCount));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.MessagePanel));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_DisableCameraButton, 0, R.string.DisableCameraButton));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_DisableRecordButton, 0, R.string.DisableRecordButton));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_DisableSenderButton, 0, R.string.DisableSenderButton));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    adapter.setItems(items, true);
    recyclerView.setAdapter(adapter);
  }
}
