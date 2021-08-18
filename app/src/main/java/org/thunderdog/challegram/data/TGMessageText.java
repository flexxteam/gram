/**
 * File created on 03/05/15 at 10:53
 * Copyright Vyacheslav Krylov, 2014
 */
package org.thunderdog.challegram.data;

import android.graphics.Canvas;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.drinkless.td.libcore.telegram.TdApi;
import org.thunderdog.challegram.R;
import org.thunderdog.challegram.component.chat.MessageView;
import org.thunderdog.challegram.component.chat.MessagesManager;
import org.thunderdog.challegram.config.Config;
import org.thunderdog.challegram.core.Lang;
import org.thunderdog.challegram.loader.DoubleImageReceiver;
import org.thunderdog.challegram.loader.ImageReceiver;
import org.thunderdog.challegram.loader.Receiver;
import org.thunderdog.challegram.loader.gif.GifReceiver;
import org.thunderdog.challegram.mediaview.MediaViewThumbLocation;
import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.tool.Strings;
import org.thunderdog.challegram.util.text.Text;
import org.thunderdog.challegram.util.text.TextColorSet;
import org.thunderdog.challegram.util.text.TextColorSets;
import org.thunderdog.challegram.util.text.TextEntity;
import org.thunderdog.challegram.util.text.TextWrapper;

import me.vkryl.td.Td;

public class TGMessageText extends TGMessage {
  private TdApi.FormattedText text;
  private TextWrapper wrapper;

  private TGWebPage webPage;
  private TdApi.MessageText currentMessageText, pendingMessageText;

  public TGMessageText (MessagesManager context, TdApi.Message msg, TdApi.MessageText text) {
    super(context, msg);
    this.currentMessageText = text;
    this.pendingMessageText = tdlib.getPendingMessageText(msg.chatId, msg.id);
    if (this.pendingMessageText != null) {
      setText(this.pendingMessageText.text, false);
      setWebPage(this.pendingMessageText.webPage);
    } else {
      setText(text.text, false);
      setWebPage(text.webPage);
    }
  }

  public TGMessageText (MessagesManager context, TdApi.Message msg, TdApi.FormattedText text) {
    super(context, msg);
    setText(text, true);
  }

  public TdApi.File getTargetFile () {
    return webPage != null ? webPage.getTargetFile() : null;
  }

  @Override
  public MediaViewThumbLocation getMediaThumbLocation (long messageId, View view, int viewTop, int viewBottom, int top) {
    if (webPage == null || webPage.getMediaWrapper() == null) {
      return null;
    }
    MediaViewThumbLocation location = webPage.getMediaWrapper().getMediaThumbLocation(view, viewTop, viewBottom, top);
    if (location != null) {
      location.setColorId(useBubbles() && isOutgoing() ? R.id.theme_color_bubbleOut_background : R.id.theme_color_filling);
    }
    return location;
  }

  public TdApi.FormattedText getText () {
    return text;
  }

  @Nullable
  public String findUriFragment (TdApi.WebPage webPage) {
    if (text.entities == null || text.entities.length == 0)
      return null;
    Uri lookupUri = Strings.wrapHttps(webPage.url);
    if (lookupUri == null)
      return null;
    int count = 0;
    Uri uri = null;
    for (TdApi.TextEntity entity : text.entities) {
      String url;
      switch (entity.type.getConstructor()) {
        case TdApi.TextEntityTypeUrl.CONSTRUCTOR:
          url = Td.substring(text.text, entity);
          break;
        case TdApi.TextEntityTypeTextUrl.CONSTRUCTOR:
          url = ((TdApi.TextEntityTypeTextUrl) entity.type).url;
          break;
        default:
          continue;
      }
      count++;

      uri = Strings.wrapHttps(url);
      if (uri != null && uri.buildUpon().fragment(null).build().equals(lookupUri)) {
        return uri.getEncodedFragment();
      }
    }
    return count == 1 && uri != null ? uri.getEncodedFragment() : null;
  }

  @Override
  protected int onMessagePendingContentChanged (long chatId, long messageId, int oldHeight) {
    if (currentMessageText != null) {
      TdApi.MessageText messageText = tdlib.getPendingMessageText(chatId, messageId);
      if (this.pendingMessageText != messageText) {
        this.pendingMessageText = messageText;
        if (messageText != null) {
          setText(messageText.text, false);
          setWebPage(messageText.webPage);
        } else {
          setText(currentMessageText.text, false);
          setWebPage(currentMessageText.webPage);
        }
        rebuildContent();
        return (getHeight() == oldHeight ? MESSAGE_INVALIDATED : MESSAGE_CHANGED);
      }
    }
    return MESSAGE_NOT_CHANGED;
  }

  @Override
  protected boolean hasInstantView (String link) {
    if (webPage == null || !webPage.needInstantView())
      return false;
    if (link.equals(webPage.getWebPage().url))
      return true;
    boolean found = false;
    for (TdApi.TextEntity entity : text.entities) {
      String url;
      if (entity.type.getConstructor() == TdApi.TextEntityTypeUrl.CONSTRUCTOR) {
        url = text.text.substring(entity.offset, entity.offset + entity.length);
      } else if (entity.type.getConstructor() == TdApi.TextEntityTypeTextUrl.CONSTRUCTOR) {
        url = ((TdApi.TextEntityTypeTextUrl) entity.type).url;
      } else {
        continue;
      }
      if (link.equals(url)) {
        found = true;
      } else {
        found = false;
        break;
      }
    }
    return found;
  }

  @Override
  protected boolean isBeingEdited () {
    return pendingMessageText != null;
  }

  private boolean setText (TdApi.FormattedText text, boolean parseEntities) {
    if (this.text == null || !Td.equalsTo(this.text, text)) {
      this.text = text;
      TextColorSet colorSet = isErrorMessage() ? TextColorSets.Regular.NEGATIVE : getTextColorSet();
      if (text.entities != null || !parseEntities) {
        this.wrapper = new TextWrapper(text.text, getTextStyleProvider(), colorSet, TextEntity.valueOf(tdlib, text, openParameters())).setClickCallback(clickCallback());
      } else {
        this.wrapper = new TextWrapper(tdlib, text.text, getTextStyleProvider(), colorSet, Text.ENTITY_FLAGS_NONE, openParameters()).setClickCallback(clickCallback());
      }
      this.wrapper.addTextFlags(Text.FLAG_BIG_EMOJI);
      if (useBubbles()) {
        this.wrapper.addTextFlags(Text.FLAG_ADJUST_TO_CURRENT_WIDTH);
      }
      if (Config.USE_NONSTRICT_TEXT_ALWAYS || !useBubbles()) {
        this.wrapper.addTextFlags(Text.FLAG_BOUNDS_NOT_STRICT);
      }
      this.wrapper.setViewProvider(currentViews);
      return true;
    }
    return false;
  }

  @Override
  protected int getBubbleContentPadding () {
    return xBubblePadding + xBubblePaddingSmall;
  }

  @Override
  protected void buildContent (int maxWidth) {
    wrapper.prepare(maxWidth);

    int webPageMaxWidth = getSmallestMaxContentWidth();
    if (pendingMessageText != null) {
      if (setWebPage(pendingMessageText.webPage))
        webPage.buildLayout(webPageMaxWidth);
    } else if (msg.content.getConstructor() == TdApi.MessageText.CONSTRUCTOR && setWebPage(((TdApi.MessageText) msg.content).webPage)) {
      webPage.buildLayout(webPageMaxWidth);
    } else if (webPage != null && webPage.getMaxWidth() != webPageMaxWidth) {
      webPage.buildLayout(webPageMaxWidth);
    }
  }

  private boolean setWebPage (TdApi.WebPage page) {
    if (page != null) {
      String url = text != null ? Td.findUrl(text, page.url, false) : page.url;
      this.webPage = new TGWebPage(this, page, url);
      this.webPage.setViewProvider(currentViews);
      return true;
    } else {
      this.webPage = null;
    }
    return false;
  }

  @Override
  protected void onMessageIdChanged (long oldMessageId, long newMessageId, boolean success) {
    if (webPage != null) {
      webPage.updateMessageId(oldMessageId, newMessageId, success);
    }
  }

  @Override
  protected void onMessageAttachedToView (@NonNull MessageView view, boolean attached) {
    if (webPage != null) {
      webPage.notifyInvalidateTargetsChanged();
    }
  }

  private int getWebY () {
    if (Td.isEmpty(text)) {
      return getContentY();
    } else {
      return getContentY() + wrapper.getHeight() + getTextTopOffset() + Screen.dp(6f);
    }
  }

  @Override
  protected boolean onMessageContentChanged (TdApi.Message message, TdApi.MessageContent oldContent, TdApi.MessageContent newContent, boolean isBottomMessage) {
    if (!Td.equalsTo(((TdApi.MessageText) oldContent).text, ((TdApi.MessageText) newContent).text) || !Td.equalsTo(((TdApi.MessageText) oldContent).webPage, ((TdApi.MessageText) newContent).webPage)) {
      updateMessageContent(msg, newContent, isBottomMessage);
      return true;
    }
    return false;
  }

  @Override
  protected boolean updateMessageContent (TdApi.Message message, TdApi.MessageContent newContent, boolean isBottomMessage) {
    TdApi.WebPage oldWebPage = ((TdApi.MessageText) this.msg.content).webPage;
    this.msg.content = newContent;
    TdApi.MessageText newText = (TdApi.MessageText) newContent;
    this.currentMessageText = newText;
    if (!isBeingEdited()) {
      setText(newText.text, false);
      setWebPage(newText.webPage);
      rebuildContent();
      if (!Td.equalsTo(oldWebPage, newText.webPage)) {
        invalidateContent();
        invalidatePreviewReceiver();
      }
    }
    return true;
  }

  @Override
  public boolean needImageReceiver () {
    return webPage != null;
  }

  @Override
  public boolean needGifReceiver () {
    return webPage != null && webPage.needGif();
  }

  @Override
  public int getImageContentRadius (boolean isPreview) {
    return webPage != null ? webPage.getImageContentRadius(isPreview) : 0;
  }

  @Override
  public void requestImage (ImageReceiver receiver) {
    if (webPage != null) {
      webPage.requestContent(receiver, getContentX(), getWebY());
    } else {
      receiver.requestFile(null);
    }
  }

  @Override
  public void autoDownloadContent (TdApi.ChatType type) {
    if (webPage != null) {
      webPage.autodownloadContent(type);
    }
  }

  @Override
  public void requestPreview (DoubleImageReceiver receiver) {
    if (webPage != null) {
      webPage.requestPreview(receiver, getContentX(), getWebY());
    } else {
      receiver.clear();
    }
  }

  @Override
  public void requestGif (GifReceiver receiver) {
    if (webPage != null) {
      webPage.requestGif(receiver, getContentX(), getWebY());
    } else {
      receiver.requestFile(null);
    }
  }

  // Text without any trash

  private int getStartXRtl (int startX, int maxWidth) {
    return useBubbles() ? (Config.MOVE_BUBBLE_TIME_RTL_TO_LEFT || wrapper.getLineCount() > 1 ? getActualRightContentEdge() - getBubbleContentPadding() : startX) : startX + maxWidth;
  }

  @Override
  protected void drawContent (MessageView view, Canvas c, int startX, int startY, int maxWidth, Receiver preview, Receiver receiver) {
    wrapper.draw(c, startX, getStartXRtl(startX, maxWidth), Config.MOVE_BUBBLE_TIME_RTL_TO_LEFT ? 0 : getBubbleTimePartWidth(), startY + getTextTopOffset(), null, 1f);
    if (webPage != null && receiver != null) {
      webPage.draw(view, c, Lang.rtl() ? startX + maxWidth - webPage.getWidth() : startX, getWebY(), preview, receiver, 1f);
    }
  }

  @Override
  protected void drawContent (MessageView view, Canvas c, int startX, int startY, int maxWidth) {
    drawContent(view, c, startX, startY, maxWidth, null, null);
  }

  @Override
  protected int getContentHeight () {
    int height = 0;
    if (!Td.isEmpty(text)) {
      height += wrapper.getHeight() + getTextTopOffset();
    }
    if (webPage != null) {
      if (height > 0)
        height += Screen.dp(8f);
      height += webPage.getHeight();
    }
    return height;
  }

  @Override
  protected int getFooterPaddingTop () {
    return Td.isEmpty(text) ? -Screen.dp(3f) : Screen.dp(7f);
  }

  @Override
  protected int getBottomLineContentWidth () {
    if (webPage != null) {
      return webPage.getLastLineWidth();
    }
    if (Lang.rtl() == wrapper.getLastLineIsRtl()) {
      return wrapper.getLastLineWidth();
    } else {
      return BOTTOM_LINE_EXPAND_HEIGHT;
    }
  }

  @Override
  protected int getContentWidth () {
    return webPage != null ? Math.max(wrapper.getWidth(), webPage.getWidth()) : wrapper.getWidth();
  }

  public TdApi.WebPage getWebPage () {
    return webPage != null ? webPage.getWebPage() : null;
  }

  public TGWebPage getParsedWebPage () {
    return webPage;
  }

  @Override
  public boolean performLongPress (View view, float x, float y) {
    boolean res = super.performLongPress(view, x, y);
    return wrapper.performLongPress(view) || (webPage != null && webPage.performLongPress(view, this)) || res;
  }

  @Override
  protected void onMessageContainerDestroyed () {
    if (webPage != null) {
      webPage.destroy();
    }
  }

  // private int touchX, touchY;

  @Override
  public boolean onTouchEvent (MessageView view, MotionEvent e) {
    return super.onTouchEvent(view, e) || wrapper.onTouchEvent(view, e) || (webPage != null && webPage.onTouchEvent(view, e, getContentX(), getWebY(), clickCallback()));
  }
}