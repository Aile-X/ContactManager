package wandal.model.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import wandal.dao.BlackListBiz;

public class TelService extends Service {
    private class TelListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (blackBiz.exists(incomingNumber)) {
                    try {
                        Method method = Class.forName(
                                "android.os.ServiceManager").getDeclaredMethod(
                                "getService", String.class);
                        IBinder binder = (IBinder)method.invoke(null, TELEPHONY_SERVICE);
                        ITelephony tel = ITelephony.Stub.asInterface(binder);
                        tel.endCall();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private TelephonyManager tm;
    private BlackListBiz blackBiz;
    private TelService.TelListener listener;

    public void onCreate() {
        super.onCreate();
        setForeground(true);
        blackBiz = new BlackListBiz(this);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new TelService.TelListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
