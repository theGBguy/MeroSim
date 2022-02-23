/*
 * Created by Chiranjeevi Pandey on 2/23/22, 9:41 AM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2021/10/28
 *
 * Licensed under GNU General Public License v3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsoft.merosim.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

/*
 * This class is used to pick contact from the contact list
 * using the Loader pattern.
 */

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String KEY_CONTENT_URI = "content_uri";
    public static final int LOADER_ID = 10;
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    };
    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone._ID + " = ?";
    private final String[] selectionArgs = {""};
    private final Context context;
    private final OnContactFoundListener onContactFoundListener;

    public ContactsLoader(Context context, OnContactFoundListener onContactFoundListener) {
        this.context = context;
        this.onContactFoundListener = onContactFoundListener;
    }

    public void restartLoader(LoaderManager loaderManager, Uri uri) {
        if (uri == null) return;
        Bundle args = new Bundle();
        args.putParcelable(KEY_CONTENT_URI, uri);
        loaderManager.restartLoader(LOADER_ID, args, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID && args != null) {
            selectionArgs[0] = getContactId(args);
            return new CursorLoader(
                    context,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    null
            );
        }
        return new CursorLoader(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID && data.getCount() > 0) {
            if (data.moveToNext()) {
                String name = data.getString(1);
                if (TextUtils.isEmpty(name))
                    name = "Unknown Contact";
                String number = data.getString(0);
                onContactFoundListener.onContactFound(name, number);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private String getContactId(Bundle args) {
        return ((Uri) args.getParcelable(KEY_CONTENT_URI)).getLastPathSegment();
    }
}
