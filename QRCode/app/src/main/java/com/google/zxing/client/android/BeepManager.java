/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
final class BeepManager implements
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, Closeable {

  private static final String TAG = BeepManager.class.getSimpleName();

  private static final float BEEP_VOLUME = 0.10f;
  private static final long VIBRATE_DURATION = 200L;

  private final Activity activity;
  private MediaPlayer mediaPlayer;
  private boolean playBeep;
  private boolean vibrate;

  BeepManager(Activity activity) {
    this.activity = activity;
    this.mediaPlayer = null;
  }

   synchronized void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    // When the beep has finished playing, rewind to queue up another one.      
    mp.seekTo(0);
  }

  @Override
  public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
      // we are finished, so put up an appropriate error toast if required and finish
      activity.finish();
    } else {
      // possibly media player error, so release and recreate
      mp.release();
      mediaPlayer = null;
    }
    return true;
  }

  @Override
  public synchronized void close() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

}
