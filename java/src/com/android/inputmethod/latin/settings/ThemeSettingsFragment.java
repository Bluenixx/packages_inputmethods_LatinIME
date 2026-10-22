/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.inputmethod.latin.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.inputmethod.keyboard.KeyboardTheme;
import com.android.inputmethod.latin.R;
import com.android.inputmethod.latin.settings.RadioButtonPreference.OnRadioButtonClickedListener;

/**
 * "Keyboard theme" settings sub screen.
 */
public final class ThemeSettingsFragment extends SubScreenFragment
        implements OnRadioButtonClickedListener {
    private static final int REQUEST_KEYBOARD_BACKGROUND = 4107;
    private static final String KEY_BACKGROUND_URI = "bluenixx_keyboard_background_uri";
    private int mSelectedThemeId;

    static class KeyboardThemePreference extends RadioButtonPreference {
        final int mThemeId;

        KeyboardThemePreference(final Context context, final String name, final int id) {
            super(context);
            setTitle(name);
            mThemeId = id;
        }
    }

    static void updateKeyboardThemeSummary(final Preference pref) {
        final Context context = pref.getContext();
        final Resources res = context.getResources();
        final int keyboardThemeId = KeyboardTheme.getSelectedKeyboardThemeId(context);
        final String[] keyboardThemeNames = res.getStringArray(R.array.keyboard_theme_names);
        final int[] keyboardThemeIds = res.getIntArray(R.array.keyboard_theme_ids);
        for (int index = 0; index < keyboardThemeNames.length; index++) {
            if (keyboardThemeId == keyboardThemeIds[index]) {
                pref.setSummary(keyboardThemeNames[index]);
                return;
            }
        }
    }

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs_screen_theme);
        final PreferenceScreen screen = getPreferenceScreen();
        final Context context = getActivity();
        final Resources res = getResources();
        final String[] keyboardThemeNames = res.getStringArray(R.array.keyboard_theme_names);
        final int[] keyboardThemeIds = res.getIntArray(R.array.keyboard_theme_ids);
        for (int index = 0; index < keyboardThemeNames.length; index++) {
            final KeyboardThemePreference pref = new KeyboardThemePreference(
                    context, keyboardThemeNames[index], keyboardThemeIds[index]);
            screen.addPreference(pref);
            pref.setOnRadioButtonClickedListener(this);
        }
        mSelectedThemeId = KeyboardTheme.getSelectedKeyboardThemeId(context);

        // Bluenixx keyboard customization. These preferences are consumed by KeyboardView.
        final ListPreference outlineColor = new ListPreference(context);
        outlineColor.setKey("bluenixx_key_outline_color");
        outlineColor.setTitle("Key outline color");
        outlineColor.setDialogTitle("Choose outline color");
        outlineColor.setEntries(new CharSequence[] {"Monet", "White", "Black", "Blue", "Purple"});
        outlineColor.setEntryValues(new CharSequence[] {"monet", "#FFFFFFFF", "#FF000000",
                "#FF448AFF", "#FFBB86FC"});
        outlineColor.setDefaultValue("monet");
        screen.addPreference(outlineColor);

        final ListPreference outlineWidth = new ListPreference(context);
        outlineWidth.setKey("bluenixx_key_outline_width");
        outlineWidth.setTitle("Outline thickness");
        outlineWidth.setDialogTitle("Choose outline thickness");
        outlineWidth.setEntries(new CharSequence[] {"Thin", "Medium", "Thick"});
        outlineWidth.setEntryValues(new CharSequence[] {"1", "1.5", "2"});
        outlineWidth.setDefaultValue("1");
        screen.addPreference(outlineWidth);

        final ListPreference outlineOpacity = new ListPreference(context);
        outlineOpacity.setKey("bluenixx_key_outline_opacity");
        outlineOpacity.setTitle("Outline brightness");
        outlineOpacity.setDialogTitle("Choose outline brightness");
        outlineOpacity.setEntries(new CharSequence[] {"Subtle", "Balanced", "Bright"});
        outlineOpacity.setEntryValues(new CharSequence[] {"90", "150", "220"});
        outlineOpacity.setDefaultValue("150");
        screen.addPreference(outlineOpacity);

        final ListPreference outlineGlow = new ListPreference(context);
        outlineGlow.setKey("bluenixx_key_outline_glow");
        outlineGlow.setTitle("Outline glow");
        outlineGlow.setDialogTitle("Choose outline glow");
        outlineGlow.setEntries(new CharSequence[] {"Off", "Soft", "Strong"});
        outlineGlow.setEntryValues(new CharSequence[] {"0", "3", "7"});
        outlineGlow.setDefaultValue("0");
        screen.addPreference(outlineGlow);

        final Preference backgroundImage = new Preference(context);
        backgroundImage.setKey("bluenixx_keyboard_background_picker");
        backgroundImage.setTitle("Keyboard background photo");
        backgroundImage.setSummary("Choose a photo to use on keyboard keys");
        backgroundImage.setOnPreferenceClickListener(pref -> {
            final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_KEYBOARD_BACKGROUND);
            return true;
        });
        screen.addPreference(backgroundImage);

        final ListPreference backgroundOpacity = new ListPreference(context);
        backgroundOpacity.setKey("bluenixx_keyboard_background_opacity");
        backgroundOpacity.setTitle("Photo opacity");
        backgroundOpacity.setDialogTitle("Choose photo opacity");
        backgroundOpacity.setEntries(new CharSequence[] {"Subtle", "Balanced", "Strong"});
        backgroundOpacity.setEntryValues(new CharSequence[] {"70", "150", "230"});
        backgroundOpacity.setDefaultValue("150");
        screen.addPreference(backgroundOpacity);

        final Preference clearBackground = new Preference(context);
        clearBackground.setTitle("Remove background photo");
        clearBackground.setSummary("Return to the selected keyboard theme");
        clearBackground.setOnPreferenceClickListener(pref -> {
            getSharedPreferences().edit().remove(KEY_BACKGROUND_URI).apply();
            return true;
        });
        screen.addPreference(clearBackground);

        final SwitchPreferenceCompat outline = new SwitchPreferenceCompat(context);
        outline.setKey("bluenixx_key_outline_enabled");
        outline.setTitle("Key outlines");
        outline.setSummary("Draw a subtle outline around each key");
        outline.setDefaultValue(false);
        screen.addPreference(outline);

        final SwitchPreferenceCompat pressEffect = new SwitchPreferenceCompat(context);
        pressEffect.setKey("bluenixx_key_press_effect");
        pressEffect.setTitle("Touch feedback");
        pressEffect.setSummary("Enable enhanced visual feedback when pressing keys");
        pressEffect.setDefaultValue(true);
        screen.addPreference(pressEffect);

        final SwitchPreferenceCompat smoothAnimations = new SwitchPreferenceCompat(context);
        smoothAnimations.setKey("bluenixx_smooth_key_animations");
        smoothAnimations.setTitle("Smooth keyboard animations");
        smoothAnimations.setSummary("Use smoother transitions where supported");
        smoothAnimations.setDefaultValue(true);
        screen.addPreference(smoothAnimations);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_KEYBOARD_BACKGROUND || data == null
                || resultCode != android.app.Activity.RESULT_OK) {
            return;
        }
        final Uri uri = data.getData();
        if (uri == null) {
            return;
        }
        try {
            getActivity().getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
            // Some document providers grant only a temporary read permission.
        }
        getSharedPreferences().edit().putString(KEY_BACKGROUND_URI, uri.toString()).apply();
    }

    @Override
    public void onRadioButtonClicked(final RadioButtonPreference preference) {
        if (preference instanceof KeyboardThemePreference) {
            final KeyboardThemePreference pref = (KeyboardThemePreference)preference;
            mSelectedThemeId = pref.mThemeId;
            updateSelected();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSelected();
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardTheme.saveKeyboardThemeId(mSelectedThemeId, getSharedPreferences());
    }

    private void updateSelected() {
        final PreferenceScreen screen = getPreferenceScreen();
        final int count = screen.getPreferenceCount();
        for (int index = 0; index < count; index++) {
            final Preference preference = screen.getPreference(index);
            if (preference instanceof KeyboardThemePreference) {
                final KeyboardThemePreference pref = (KeyboardThemePreference)preference;
                final boolean selected = (mSelectedThemeId == pref.mThemeId);
                pref.setSelected(selected);
            }
        }
    }
}
