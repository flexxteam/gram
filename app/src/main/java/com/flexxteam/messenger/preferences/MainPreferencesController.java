// This is a Flexxgram source code file.
// Flexxgram is not a trademark of Telegram and Telegram X.
// Flexxgram is an open-source and freely distributed modification of Telegram X.
//
// Copyright (C) 2023 Flexxteam.

package com.flexxteam.messenger.preferences;

import android.content.Context;
import android.view.View;

import org.thunderdog.challegram.BuildConfig;
import org.thunderdog.challegram.R;
import org.thunderdog.challegram.U;
import org.thunderdog.challegram.component.base.SettingView;
import org.thunderdog.challegram.core.Lang;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.telegram.TdlibUi;
import org.thunderdog.challegram.tool.UI;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.v.CustomRecyclerView;

import java.util.ArrayList;

public class MainPreferencesController extends RecyclerViewController<Void> implements View.OnClickListener, View.OnLongClickListener {

  private SettingsAdapter adapter;

  public MainPreferencesController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () {
    return Lang.getString(R.string.MainPreferences);
  }

  @Override public int getId () {
    return R.id.controller_MainPreferences;
  }

  @Override public void onClick (View v) {
    int viewId = v.getId();
    if (viewId == R.id.btn_Updates) {
      // navigateTo(new UpdatesController(context, tdlib)); // soon
    } else if (viewId == R.id.btn_GeneralPreferences) {
      navigateTo(new GeneralPreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_AppearancePreferences) {
      navigateTo(new AppearancePreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_ChatsPreferences) {
      navigateTo(new ChatsPreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_SecurityPreferences) {
      navigateTo(new SecurityPreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_notificationSettings) {
      navigateTo(new PushPreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_OtherPreferences) {
      navigateTo(new OtherPreferencesController(context, tdlib));
    } else if (viewId == R.id.btn_OfficialChannel) {
      tdlib.ui().openUrl(this, Lang.getStringSecure(R.string.OfficialChannelLink), new TdlibUi.UrlOpenParameters().forceInstantView());
    } else if (viewId == R.id.btn_OfficialWebsite) {
      tdlib.ui().openUrl(this, Lang.getStringSecure(R.string.OfficialWebsiteLink), new TdlibUi.UrlOpenParameters().forceInstantView());
    } else if (viewId == R.id.btn_SourceCode) {
      tdlib.ui().openUrl(this, Lang.getStringSecure(R.string.SourceCodeLink), new TdlibUi.UrlOpenParameters().forceInstantView());
    } else if (viewId == R.id.btn_Translation) {
      tdlib.ui().openUrl(this, Lang.getStringSecure(R.string.TranslationLink), new TdlibUi.UrlOpenParameters().forceInstantView());
    } else if (viewId == R.id.btn_Donate) {
      tdlib.ui().openUrl(this, Lang.getStringSecure(R.string.DonateLink), new TdlibUi.UrlOpenParameters().forceInstantView());
    }
  }

  @Override
  public boolean onLongClick (View v) {
    int viewId = v.getId();
    if (viewId == R.id.btn_Updates) {
      UI.copyText(Lang.getStringSecure(R.string.FlexxgramVersion) + " (" + Lang.getAppBuildAndVersion(tdlib) + ")\n", R.string.CopiedText);
    }
    return false;
  }

  @Override protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override protected void setValuedSetting (ListItem item, SettingView view, boolean isUpdate) {
        view.setDrawModifier(item.getDrawModifier());
        int itemId = item.getId();
        if (itemId == R.id.btn_Updates) {
          view.setData(Lang.getStringSecure(R.string.FlexxgramVersion) + " (" + Lang.getAppBuildAndVersion(tdlib) + ")\n");
        } else if (itemId == R.id.btn_OfficialChannel) {
          view.setData("@flexxgram");
        } else if (itemId == R.id.btn_OfficialWebsite) {
          view.setData("flexxgram.com");
        } else if (itemId == R.id.btn_SourceCode) {
          view.setData(BuildConfig.COMMIT);
        } else if (itemId == R.id.btn_Translation) {
          view.setData(R.string.TranslationDesc);
        } else if (itemId == R.id.btn_Donate) {
          view.setData(R.string.DonateDesc);
        }
      }
    };

    ArrayList<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_EMPTY_OFFSET_SMALL));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.Updates));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_VALUED_SETTING_COMPACT, R.id.btn_Updates, R.drawable.baseline_system_update_24, R.string.CheckUpdates));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.Preferences));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_GeneralPreferences, R.drawable.baseline_widgets_24, R.string.GeneralPreferences));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_AppearancePreferences, R.drawable.baseline_palette_24, R.string.AppearancePreferences));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_ChatsPreferences, R.drawable.baseline_chat_bubble_24, R.string.ChatsPreferences));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_SecurityPreferences, R.drawable.baseline_lock_24, R.string.SecurityPreferences));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_notificationSettings, R.drawable.baseline_notifications_24, R.string.Notifications));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_OtherPreferences, R.drawable.baseline_star_24, R.string.OtherPreferences));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.AboutProject));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_OfficialChannel, R.drawable.baseline_help_24, R.string.OfficialChannel));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_OfficialWebsite, R.drawable.baseline_public_24, R.string.OfficialWebsite));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_SourceCode, R.drawable.baseline_github_24, R.string.SourceCode));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_VALUED_SETTING_COMPACT, R.id.btn_Translation, R.drawable.baseline_translate_24, R.string.Translation));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR));
    items.add(new ListItem(ListItem.TYPE_VALUED_SETTING_COMPACT, R.id.btn_Donate, R.drawable.baseline_paid_24, R.string.Donate));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    adapter.setItems(items, true);
    recyclerView.setAdapter(adapter);
  }
}
