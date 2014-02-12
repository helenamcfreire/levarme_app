/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.levar.gcm;

import android.content.Context;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;

import java.util.Random;

import static me.levar.gcm.CommonUtilities.TAG;


/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     */
    public static void register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        GCMRegistrar.setRegisteredOnServer(context, true);
    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        GCMRegistrar.setRegisteredOnServer(context, false);
    }

}
