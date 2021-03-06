package com.nutomic.syncthingandroid.test.syncthing;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.nutomic.syncthingandroid.syncthing.DeviceStateHolder;
import com.nutomic.syncthingandroid.syncthing.NetworkReceiver;
import com.nutomic.syncthingandroid.syncthing.SyncthingService;
import com.nutomic.syncthingandroid.test.MockContext;

/**
 * Tests for correct extras on the Intent sent by
 * {@link com.nutomic.syncthingandroid.syncthing.NetworkReceiver}.
 *
 * Does not test for correct result value, as that would require mocking
 * {@link android.net.ConnectivityManager} (or replacing it with an interface).
 */
public class NetworkReceiverTest extends AndroidTestCase {

    private NetworkReceiver mReceiver;
    private MockContext mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mReceiver = new NetworkReceiver();
        mContext = new MockContext(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().clear().commit();
        super.tearDown();
    }

    @MediumTest
    public void testOnReceive() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(SyncthingService.PREF_ALWAYS_RUN_IN_BACKGROUND, true)
                .commit();
        mReceiver.onReceive(mContext, null);
        assertEquals(1, mContext.getReceivedIntents().size());

        Intent receivedIntent = mContext.getReceivedIntents().get(0);
        assertEquals(SyncthingService.class.getName(), receivedIntent.getComponent().getClassName());
        assertNull(receivedIntent.getAction());
        assertTrue(receivedIntent.hasExtra(DeviceStateHolder.EXTRA_HAS_WIFI));
        mContext.clearReceivedIntents();
    }

    @MediumTest
    public void testOnlyRunInForeground() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .putBoolean(SyncthingService.PREF_ALWAYS_RUN_IN_BACKGROUND, false)
                .commit();
        mReceiver.onReceive(mContext, null);
        assertEquals(0, mContext.getReceivedIntents().size());
    }

}
