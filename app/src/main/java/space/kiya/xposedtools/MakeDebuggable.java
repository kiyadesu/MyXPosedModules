package space.kiya.xposedtools;

import android.content.pm.ApplicationInfo;
import java.util.HashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by kiya on 16-8-9.
 */
public class MakeDebuggable implements IXposedHookLoadPackage{

    private boolean debuggable = true;
    private static final int ENABLE_DEBUGGER = 0x1;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.appInfo == null ||
                (loadPackageParam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }

        XposedBridge.log("MakeDebuggable:" + loadPackageParam.packageName);

        HashSet unhooks = (HashSet) XposedBridge.hookAllMethods(android.os.Process.class, "start", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int id = 5;
                int flags = (Integer) param.args[id];

                XposedBridge.log("flag before:" + flags);

                if (debuggable) {
                    if ((flags & ENABLE_DEBUGGER) == 0) {
                        flags |= ENABLE_DEBUGGER;
                    }
                    param.args[id] = flags;
                    XposedBridge.log("flag after:" + flags);
                }
            }
        });
        XposedBridge.log("hook result:" + unhooks.toString());
    }
}
